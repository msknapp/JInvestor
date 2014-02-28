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

import jinvestor.jhouse.core.House;
import jinvestor.jhouse.core.HouseDAO;
import jinvestor.jhouse.core.query.QueryNode;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
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
	private static final String HOMES_TABLE_NAME = "homes";
	private static final String ADDRESS_TABLE_NAME = "homes_index_address";
	private static final byte[] DATA = Bytes.toBytes("data");
	private static final byte[] BEDS = Bytes.toBytes("beds");
	private static final byte[] BATHS = Bytes.toBytes("baths");
	private static final byte[] ADDRESS = Bytes.toBytes("address");
	private static final byte[] LAST_SOLD_DATE = Bytes
			.toBytes("last_sold_date");
	private static final byte[] SOLD_PRICE = Bytes.toBytes("sold_price");
	private static final byte[] LATITUDE = Bytes.toBytes("latitude");
	private static final byte[] LONGITUDE = Bytes.toBytes("longitude");
	private static final byte[] YEAR_BUILT = Bytes.toBytes("year_built");
	private static final byte[] SQUARE_FEET = Bytes.toBytes("square_feet");
	private static final byte[] ACRES = Bytes.toBytes("acres");

	private final Configuration configuration;

	public HBaseHouseDAO(final Configuration configuration) {
		this.configuration = configuration;
	}

	private static interface TablesCallback {
		void process(HTableInterface homesTable,
				HTableInterface addressIndexTable) throws IOException;
	}

	private void doWithCallback(TablesCallback callback) {
		HTableInterface homesTable = null;
		HTableInterface addressIndexTable = null;
		try {
			homesTable = new HTable(configuration, HOMES_TABLE_NAME);
			addressIndexTable = new HTable(configuration, ADDRESS_TABLE_NAME);
			callback.process(homesTable, addressIndexTable);
		} catch (IOException e) {
			logger.error("There was an error processing with a callback", e);
		} finally {
			try {
				homesTable.close();
			} catch (IOException e) {
				logger.error(e);
			}
			try {
				addressIndexTable.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}

	}

	@Override
	public void save(final House house) {
		doWithCallback(new TablesCallback() {
			@Override
			public void process(HTableInterface homesTable,
					HTableInterface addressIndexTable) throws IOException {
				Put put = buildHousePut(house);
				homesTable.put(put);
				Put adrPut = buildAddressPut(house);
				addressIndexTable.put(adrPut);
				logger.info("Successfully saved a house with zpid: "+house.getZpid());
			}
		});
	}

	private Put buildAddressPut(House house) {
		Put adrPut = new Put(Bytes.toBytes(house.getAddress()));
		adrPut.add(DATA, Bytes.toBytes("zpid"), Bytes.toBytes(house.getZpid()));
		return adrPut;
	}

	private Put buildHousePut(House house) {
		Put put = new Put(Bytes.toBytes(house.getZpid()));
		put.add(DATA, BEDS, new byte[] { house.getBeds() });
		put.add(DATA, BATHS, Bytes.toBytes(house.getBaths()));
		put.add(DATA, ACRES, Bytes.toBytes(house.getAcres()));
		put.add(DATA, LATITUDE, Bytes.toBytes(house.getLatitude()));
		put.add(DATA, LONGITUDE, Bytes.toBytes(house.getLongitude()));
		put.add(DATA, SOLD_PRICE, Bytes.toBytes(house.getSoldPrice()));
		put.add(DATA, SQUARE_FEET, Bytes.toBytes(house.getSquareFeet()));
		put.add(DATA, YEAR_BUILT, Bytes.toBytes(house.getYearBuilt()));
		put.add(DATA, ADDRESS, Bytes.toBytes(house.getAddress()));
		if (house.getLastSoldDate()!= null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String lsd = sdf.format(house.getLastSoldDate());
			put.add(DATA, LAST_SOLD_DATE, Bytes.toBytes(lsd));
		}
		return put;
	}

	@Override
	public void save(final List<House> homes) {
		doWithCallback(new TablesCallback() {

			@Override
			public void process(HTableInterface homesTable,
					HTableInterface addressIndexTable) throws IOException {
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
			}
		});
	}

	@Override
	public House load(long id) {
		Get get = new Get(Bytes.toBytes(id));
		House house = null;
		HTableInterface homesTable = null;
		try {
			homesTable = new HTable(configuration, HOMES_TABLE_NAME);
			Result result = homesTable.get(get);
			house = makeHouse(result);
		} catch (IOException e) {
			logger.error("Failed to get " + id + " from the homes table.", e);
		} finally {
			try {
				homesTable.close();
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return house;
	}

	private House makeHouse(Result result) {
		if (result.isEmpty()) {
			return null;
		}
		House house = null;
		try {
			House.HouseBuilder b = House.builder().zpid(
					Bytes.toLong(result.getRow()));
			if (result.containsColumn(DATA, ADDRESS)) {
				b.address(Bytes.toString(result.getValue(DATA, ADDRESS)));
			}
			if (result.containsColumn(DATA, BEDS)) {
				b.beds(result.getValue(DATA, BEDS)[0]);
			}
			if (result.containsColumn(DATA, BATHS)) {
				b.baths(Bytes.toFloat(result.getValue(DATA, BATHS)));
			}
			if (result.containsColumn(DATA, YEAR_BUILT)) {
				b.yearBuilt(Bytes.toInt(result.getValue(DATA, YEAR_BUILT)));
			}
			if (result.containsColumn(DATA, LAST_SOLD_DATE)) {
				String lsd = Bytes.toString(result.getValue(DATA,
						LAST_SOLD_DATE));
				Date d = null;
				try {
					d = new SimpleDateFormat("yyyy-MM-dd").parse(lsd);
				} catch (ParseException e) {
					logger.error("Failed to parse " + lsd + " to a date", e);
				}
				b.lastSoldDate(d);
			}
			if (result.containsColumn(DATA, SQUARE_FEET)) {
				b.squareFeet(Bytes.toInt(result.getValue(DATA, SQUARE_FEET)));
			}
			if (result.containsColumn(DATA, LONGITUDE)) {
				b.longitude(Bytes.toInt(result.getValue(DATA, LONGITUDE)));
			}
			if (result.containsColumn(DATA, LATITUDE)) {
				b.latitude(Bytes.toInt(result.getValue(DATA, LATITUDE)));
			}
			if (result.containsColumn(DATA, ACRES)) {
				b.acres(Bytes.toFloat(result.getValue(DATA, ACRES)));
			}
			if (result.containsColumn(DATA, SOLD_PRICE)) {
				b.soldPrice(Bytes.toInt(result.getValue(DATA, SOLD_PRICE)));
			}
			house = b.build();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return house;
	}

	@Override
	public List<House> query(final String query) {
		final List<House> homes = new ArrayList<House>();
		doWithCallback(new TablesCallback() {

			@Override
			public void process(HTableInterface homesTable,
					HTableInterface addressIndexTable) throws IOException {
				Scan scan = new Scan();
				if (StringUtils.isEmpty(query)) {
					Filter filter = QueryNode.parseNode(query).toQueryPart()
							.toFilter(DATA);
					scan.setFilter(filter);
				}
				scan.addFamily(DATA);
				ResultScanner rs = null;
				try {
					rs = homesTable.getScanner(scan);
					Result result;
					while ((result = rs.next()) != null) {
						House house = makeHouse(result);
						if (house!=null) {
							homes.add(house);
						}
					}
				} catch (IOException e) {
					logger.error("Failed to scan by query " + query, e);
				} finally {
					rs.close();
				}
			}
		});
		return homes;
	}

	@Override
	public void delete(House house) {
		delete(house.getZpid());
	}

	@Override
	public void delete(final List<House> homes) {
		doWithCallback(new TablesCallback() {
			@Override
			public void process(HTableInterface homesTable,
					HTableInterface addressIndexTable) throws IOException {
				List<Delete> deletes = new ArrayList<Delete>();
				for (House house : homes) {
					deletes.add(new Delete(Bytes.toBytes(house.getZpid())));
				}
				try {
					homesTable.delete(deletes);
				} catch (IOException e) {
					logger.error("Failed to delete from the homes table.", e);
				}
			}
		});
	}

	@Override
	public int count(String query) {
		return query(query).size();
	}

	@Override
	public void delete(final long id) {
		doWithCallback(new TablesCallback() {

			@Override
			public void process(HTableInterface homesTable,
					HTableInterface addressIndexTable) throws IOException {
				Delete delete = new Delete(Bytes.toBytes(id));
				homesTable.delete(delete);
			}
		});
	}

	@Override
	public House queryFirst(String query) {
		List<House> homes = query(query);
		if (homes != null && !homes.isEmpty()) {
			return homes.get(0);
		}
		return null;
	}
}