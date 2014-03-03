package jinvestor.jhouse.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jinvestor.jhouse.core.House;
import jinvestor.jhouse.core.House.HouseBuilder;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

public final class HouseAvroUtil {
	private static final Logger logger = Logger.getLogger(HouseAvroUtil.class);

	private static Schema schema;

	private HouseAvroUtil() {
	}

	public static Schema getSchema() {
		if (schema == null) {
			InputStream in = null;
			try {
				in = HouseAvroUtil.class
						.getResourceAsStream("/jinvestor/jhouse/core/HouseEntity.avsc");
				schema = new Schema.Parser().parse(in);
			} catch (Exception e) {
				logger.error(e);
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
		return schema;
	}
	
	public static String toString(List<House> homes) {
		byte[] bs = toBytes(homes);
		// unfortunately these bytes don't transfer nicely into a string.
		// so I base 64 encode them.
		byte[] encoded = Base64.encodeBase64(bs);
		return new String(encoded);
	}
	
	public static byte[] toBytes(List<House> homes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		write(homes,baos,true);
		return baos.toByteArray();
	}
	
	public static List<House> fromString(String avro) {
		byte[] decoded = Base64.decodeBase64(avro.getBytes());
		return fromBytes(decoded);
	}
	
	public static List<House> fromBytes(byte[] byteArray) {
		ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
		List<House> homes = read(bais,true);
		return homes;
	}

	public static void write(List<House> homes, OutputStream out, boolean close) {
		if (out == null) {
			return;
		}
		DataFileWriter<GenericRecord> dataFileWriter = null;
		try {
			DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(
					getSchema());
			dataFileWriter = new DataFileWriter<>(datumWriter);
			dataFileWriter.create(getSchema(), out);
			for (House house : homes) {
				dataFileWriter.append(createGenericRecord(house));
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				dataFileWriter.close();
				if (close) {
					IOUtils.closeQuietly(out);
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	public static List<House> read(InputStream in, boolean close) {
		List<House> homes = new ArrayList<House>();
		if (in == null) {
			return homes;
		}
		DataFileStream<GenericRecord> dataFileReader = null;
		try {
			// unfortunately with how avro is designed, you must load in the
			// entire stream
			// just to de-serialize it.
			DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>(
					getSchema());
			dataFileReader = new DataFileStream<GenericRecord>(in, datumReader);
			GenericRecord record = null;
			while (dataFileReader.hasNext()) {
				// Reuse user object by passing it to next(). This saves us
				// from
				// allocating and garbage collecting many objects for files
				// with
				// many items.
				record = dataFileReader.next(record);
				homes.add(createHouse(record));
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				if (close) {
					IOUtils.closeQuietly(in);
				}
				if (dataFileReader != null) {
					dataFileReader.close();
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}
		return homes;
	}

	public static House createHouse(GenericRecord record) {
		HouseBuilder hb = new HouseBuilder();
		if (record.get("zpid") != null) {
			hb.zpid((long) record.get("zpid"));
		}
		if (record.get("acres") != null) {
			hb.acres((float) record.get("acres"));
		}
		if (record.get("address") != null) {
			hb.address(record.get("address").toString());
		}
		if (record.get("baths") != null) {
			hb.baths((float) record.get("baths"));
		}
		if (record.get("beds") != null) {
			hb.beds(new Integer(record.get("beds").toString()).byteValue());
		}
		if (record.get("latitude") != null) {
			hb.latitude((int) record.get("latitude"));
		}
		if (record.get("longitude") != null) {
			hb.longitude((int) record.get("longitude"));
		}
		if (record.get("soldprice") != null) {
			hb.soldPrice((int) record.get("soldprice"));
		}
		if (record.get("squarefeet") != null) {
			hb.squareFeet((int) record.get("squarefeet"));
		}
		if (record.get("yearbuilt") != null) {
			hb.yearBuilt((int) record.get("yearbuilt"));
		}
		return hb.build();
	}

	public static GenericRecord createGenericRecord(House house) {
		GenericRecord record = new GenericData.Record(getSchema());
		record.put("acres", house.getAcres());
		record.put("address", house.getAddress());
		record.put("beds", (int)house.getBeds());
		record.put("baths", house.getBaths());
		record.put("latitude", house.getLatitude());
		record.put("longitude", house.getLongitude());
		record.put("soldprice", house.getSoldPrice());
		record.put("squarefeet", house.getSquareFeet());
		record.put("yearbuilt", house.getYearBuilt());
		record.put("zpid", house.getZpid());
		return record;
	}
}