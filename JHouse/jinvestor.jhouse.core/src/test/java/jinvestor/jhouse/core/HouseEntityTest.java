package jinvestor.jhouse.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class HouseEntityTest {

	@Test
	public void serialize() throws IOException {
		HouseEntity house = new HouseEntity();
		house.setAcres(3.5f);
		house.setAddress("123 cherry lane, Lakewood Missouri 39067");
		house.setSoldPrice(209853);
		house.setZpid(750823034L);
		house.setLastSoldDate(new Date(8257392));
		
		HouseEntity house2 = new HouseEntity();
		house2.setLatitude(9208354);
		house2.setLongitude(7652389);
		house2.setSquareFeet(3592);
		house2.setAddress("8523 Apple Street, Rogerstown Alabama 29841");
		house2.setBaths(2.5f);
		house2.setBeds((byte)4);
		
		checkMarshalling(house);
		checkMarshalling(house2);
		
		HouseEntity houseClone = new HouseEntity(house);
		checkEquals(house,houseClone);
		checkNotEquals(house,house2);
		
		// check that the dates are separate
		Assert.assertFalse(house.getLastSoldDate()==houseClone.getLastSoldDate());
		house.getLastSoldDate().setTime(System.currentTimeMillis());
		Assert.assertNotSame(house.getLastSoldDate(), houseClone.getLastSoldDate());
	}
	
	private void checkMarshalling(HouseEntity house) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutput dos = new DataOutputStream(baos);
		house.write(dos);
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		DataInput dis = new DataInputStream(bais);
		HouseEntity rec = new HouseEntity();
		rec.readFields(dis);
		Assert.assertEquals(house, rec);
	}
	
	private void checkEquals(HouseEntity h1,HouseEntity h2) {
		Assert.assertEquals(h1,h2);
		Assert.assertEquals(h1.hashCode(),h2.hashCode());
	}
	
	private void checkNotEquals(HouseEntity h1,HouseEntity h2) {
		Assert.assertNotSame(h1,h2);
		Assert.assertNotSame(h1.hashCode(),h2.hashCode());
	}
}