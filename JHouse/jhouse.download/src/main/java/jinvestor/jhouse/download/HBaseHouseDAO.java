package jinvestor.jhouse.download;

/*
 * #%L
 * jinvestor.parent
 * %%
 * Copyright (C) 2014 Michael Scott Knapp
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jinvestor.jhouse.House;
import jinvestor.jhouse.query.QueryNode;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

public class HBaseHouseDAO implements HouseDAO {
	private static final Logger logger = Logger.getLogger(HBaseHouseDAO.class);
	private static final byte[] DATA = "data".getBytes();
	private static final byte[] BEDS = "beds".getBytes();
	private static final byte[] BATHS = "baths".getBytes();
	private static final byte[] ADDRESS = "address".getBytes();
	private static final byte[] LAST_SOLD_DATE = "last_sold_date".getBytes();
	private static final byte[] SOLD_PRICE = "sold_price".getBytes();
	private static final byte[] LATITUDE = "latitude".getBytes();
	private static final byte[] LONGITUDE = "longitude".getBytes();
	private static final byte[] YEAR_BUILT = "year_built".getBytes();
	private static final byte[] SQUARE_FEET = "square_feet".getBytes();
	private static final byte[] ACRES = "acres".getBytes();

	private final HTableInterface homesTable;
	private final HTableInterface addressIndexTable;

	public HBaseHouseDAO(HTableInterface homesTable, HTableInterface addressIndexTable) {
		this.homesTable = homesTable;
		this.addressIndexTable = addressIndexTable;
	}

	@Override
	public void save(House house) {
		try {
			Put put = buildHousePut(house);
			homesTable.put(put);

			// the address index
			Put adrPut = buildAddressPut(house);
			addressIndexTable.put(adrPut);
		} catch (IOException e) {
			logger.error("Failed to save house with zpid "+house.getZpid()+" at "+house.getAddress(),e);
		}
	}

	private Put buildAddressPut(House house) {
		Put adrPut = new Put(house.getAddress().getBytes());
		adrPut.add(DATA, "zpid".getBytes(), String.valueOf(house.getZpid())
				.getBytes());
		return adrPut;
	}

	private Put buildHousePut(House house) {
		Put put = new Put(String.valueOf(house.getZpid()).getBytes());
		put.add(DATA, BEDS, new byte[] { house.getBeds() });
		put.add(DATA, BATHS, Bytes.toBytes(house.getBaths()));
		put.add(DATA, ACRES, Bytes.toBytes(house.getAcres()));
		put.add(DATA, LATITUDE,Bytes.toBytes(house.getLatitude()));
		put.add(DATA, LONGITUDE,Bytes.toBytes(house.getLongitude()));
		put.add(DATA, SOLD_PRICE,Bytes.toBytes(house.getSoldPrice()));
		put.add(DATA, SQUARE_FEET,Bytes.toBytes(house.getSquareFeet()));
		put.add(DATA, YEAR_BUILT,Bytes.toBytes(house.getYearBuilt()));
		put.add(DATA, ADDRESS, Bytes.toBytes(house.getAddress()));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String lsd = sdf.format(house.getLastSoldDate());
		put.add(DATA, LAST_SOLD_DATE, Bytes.toBytes(lsd));
		return put;
	}

	@Override
	public void save(List<House> homes) {
		try {
			List<Put> homePuts = new ArrayList<Put>();
			for (House home : homes) {
				homePuts.add(buildHousePut(home));
			}
			homesTable.put(homePuts);
			List<Put> addressPuts = new ArrayList<Put>();
			for (House home : homes) {
				addressPuts.add(buildAddressPut(home));
			}
			addressIndexTable.put(addressPuts);
		} catch (IOException e) {
			logger.error("Failed to save homes",e);
		}
	}

	@Override
	public House load(long id) {
		Get get = new Get(String.valueOf(id).getBytes());
		House house = null;
		try {
			Result result = homesTable.get(get);
			house = makeHouse(result);
		} catch (IOException e) {
			logger.error("Failed to get "+id+" from the homes table.",e);
		}
		return house;
	}
	
	private House makeHouse(Result result) {
		House.HouseBuilder b = House.builder().zpid(Bytes.toLong(result.getRow()))
				.address(Bytes.toString(result.getValue(DATA,ADDRESS)))
				.beds(result.getValue(DATA,BEDS)[0])
				.baths(Bytes.toFloat(result.getValue(DATA,BATHS)));
		b.yearBuilt(Integer.valueOf(String.valueOf(result.getValue(DATA,YEAR_BUILT))));
		String lsd = Bytes.toString(result.getValue(DATA,LAST_SOLD_DATE));
		Date d=null;
		try {
			d = new SimpleDateFormat("yyyy-MM-dd").parse(lsd);
		} catch (ParseException e) {
			logger.error("Failed to parse "+lsd+" to a date",e);
		}
		b.lastSoldDate(d)
				.squareFeet(Bytes.toInt(result.getValue(DATA,SQUARE_FEET)))
				.longitude(Bytes.toInt(result.getValue(DATA,LONGITUDE)))
				.latitude(Bytes.toInt(result.getValue(DATA,LATITUDE)))
				.acres(Bytes.toFloat(result.getValue(DATA,ACRES)))
				.soldPrice(Bytes.toInt(result.getValue(DATA,SOLD_PRICE)));
		return b.build();
	}

	@Override
	public List<House> query(String query) {
		Scan scan = new Scan();
		Filter filter = QueryNode.parseNode(query).toQueryPart().toFilter(DATA);
		scan.setFilter(filter);
		scan.addFamily(DATA);
		ResultScanner rs = null;
		List<House> homes = new ArrayList<House>();
		try {
			rs = homesTable.getScanner(scan);
			Result result;
			while ((result=rs.next())!=null) {
				House house = makeHouse(result);
				homes.add(house);
			}
		} catch (IOException e) {
			logger.error("Failed to scan by query "+query,e);
		} finally {
			rs.close();
		}
		return homes;
	}

	@Override
	public void delete(House house) {
		delete(house.getZpid());
	}

	@Override
	public void delete(List<House> homes) {
		List<Delete> deletes = new ArrayList<Delete>();
		for (House house : homes) {
			deletes.add(new Delete(String.valueOf(house.getZpid()).getBytes()));
		}
		try {
			homesTable.delete(deletes);
		} catch (IOException e) {
			logger.error("Failed to delete from the homes table.",e);
		}
	}

	@Override
	public int count(String query) {
		return query(query).size();
	}

	@Override
	public void delete(long id) {
		Delete delete = new Delete(String.valueOf(id).getBytes());
		try {
			homesTable.delete(delete);
		} catch (IOException e) {
			logger.error("Failed to delete "+id+" from the homes table.",e);
		}
	}

	@Override
	public House queryFirst(String query) {
		List<House> homes = query(query);
		if (homes!=null && !homes.isEmpty()) {
			return homes.get(0);
		}
		return null;
	}
}