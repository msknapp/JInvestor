package jinvestor.jhouse.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import jinvestor.jhouse.core.House;
import jinvestor.jhouse.core.House.HouseBuilder;

import org.apache.avro.generic.GenericRecord;
import org.junit.Assert;
import org.junit.Test;

public class HouseAvroUtilTest {

	@Test
	public void serialize() {
		House h = new HouseBuilder().acres(3f).address("foo").zpid(1890532L)
				.build();
		House h2 = new HouseBuilder().beds(3).baths(5).zpid(16392L)
				.address("8t23j").build();
		GenericRecord r = HouseAvroUtil.createGenericRecord(h);
		GenericRecord r2 = HouseAvroUtil.createGenericRecord(h2);
		House rec = HouseAvroUtil.createHouse(r);
		House rec2 = HouseAvroUtil.createHouse(r2);
		Assert.assertEquals(h, rec);
		Assert.assertEquals(h2, rec2);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		HouseAvroUtil.write(Arrays.asList(h, h2), baos, true);
		byte[] bs = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(bs);
		List<House> recs = HouseAvroUtil.read(bais, true);
		Assert.assertTrue(new String(bs), recs.contains(h));
		Assert.assertTrue(recs.contains(h2));

//		byte[] s = HouseAvroUtil.toBytes(Arrays.asList(h, h2));
//		List<House> recs2 = HouseAvroUtil.fromBytes(s);
//		Assert.assertTrue(new String(s), recs2.contains(h));
//		Assert.assertTrue(recs2.contains(h2));
		
		String s = HouseAvroUtil.toString(Arrays.asList(h, h2));
		List<House> recs2 = HouseAvroUtil.fromString(s);
		Assert.assertTrue(new String(s), recs2.contains(h));
		Assert.assertTrue(recs2.contains(h2));
	}

	@Test
	public void vectorize() {
		House min = House.builder()
				.beds(1).baths(1f).address("3gf4af")
				.zpid(98345L).lastSoldDate("20000419")
				.longitude(100000).latitude(100000)
				.squareFeet(600).yearBuilt(1910)
				.soldPrice(50000)
				.build();
		House max = House.builder()
				.beds(9).baths(7.5f)
				.zpid(29534L).lastSoldDate("20130419")
				.longitude(200000).latitude(200000)
				.squareFeet(6000).yearBuilt(2013)
				.soldPrice(500000)
				.build();
		String serialized = HouseAvroUtil.toString(Arrays.asList(min,max));
		List<House> recs2 = HouseAvroUtil.fromString(serialized);
		Assert.assertTrue(recs2.contains(min));
		Assert.assertTrue(recs2.contains(max));
	}

	@Test
	public void vectorize2() {
		House min = House.builder()
				.beds(1).baths(1f).address("3gf4af")
				.zpid(98345L).lastSoldDate("20000419")
				.longitude(100000).latitude(100000)
				.squareFeet(600).yearBuilt(1910)
				.soldPrice(50000)
				.build();
		String serialized = HouseAvroUtil.toString(Arrays.asList(min));
		List<House> recs2 = HouseAvroUtil.fromString(serialized);
		Assert.assertEquals(min,recs2.iterator().next());
	}
}