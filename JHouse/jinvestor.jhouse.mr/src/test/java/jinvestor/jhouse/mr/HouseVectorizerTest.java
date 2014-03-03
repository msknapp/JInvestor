package jinvestor.jhouse.mr;

import java.util.Arrays;

import jinvestor.jhouse.core.House;
import jinvestor.jhouse.core.util.HouseAvroUtil;
import jinvestor.jhouse.mr.HouseVectorizer.HouseVectorizingMapper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class HouseVectorizerTest {

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
		String serialized = HouseAvroUtil.toBase64String(Arrays.asList(min,max));
		
		HouseVectorizingMapper m = new HouseVectorizingMapper();
		Context context = Mockito.mock(Context.class);
		Configuration conf = Mockito.mock(Configuration.class);
		Mockito.when(context.getConfiguration()).thenReturn(conf);
		Mockito.when(conf.get(Mockito.matches("minmax"))).thenReturn(serialized);
		m.setup(context);
		House house = House.builder()
				.beds(3).baths(2.5f).address("jo23j34")
				.zpid(290853L).lastSoldDate("20080419")
				.longitude(138091).latitude(187254)
				.squareFeet(2001).yearBuilt(1993)
				.soldPrice(189500)
				.build();
		VectorWritable vw = m.vectorizeHouse(house);
		Vector v = vw.get();
		Assert.assertEquals(7,v.size());
		
		Assert.assertTrue(House.isEquals(v.get(0), 25));
		Assert.assertTrue(House.isEquals(v.get(1), 25));
		Assert.assertTrue(House.isEquals(v.get(2), 23.0769));
		Assert.assertTrue(House.isEquals(v.get(3), 87));
		Assert.assertTrue(House.isEquals(v.get(4), 38));
		Assert.assertTrue(House.isEquals(v.get(5), 80));
		Assert.assertTrue(House.isEquals(v.get(6), 0));
	}

	@Test
	public void missingValues() {
		House min = House.builder()
				.beds(1).baths(1f).address("3gf4af")
				.zpid(98345L).lastSoldDate("20000419")
				.squareFeet(600).yearBuilt(1910)
				.soldPrice(50000)
				.build();
		House max = House.builder()
				.beds(9).baths(7.5f)
				.zpid(29534L).lastSoldDate("20130419")
				.squareFeet(6000).yearBuilt(2013)
				.soldPrice(500000)
				.build();
		String serialized = HouseAvroUtil.toBase64String(Arrays.asList(min,max));
		
		HouseVectorizingMapper m = new HouseVectorizingMapper();
		Context context = Mockito.mock(Context.class);
		Configuration conf = Mockito.mock(Configuration.class);
		Mockito.when(context.getConfiguration()).thenReturn(conf);
		Mockito.when(conf.get(Mockito.matches("minmax"))).thenReturn(serialized);
		m.setup(context);
		House house = House.builder()
				.beds(3).baths(2.5f).address("jo23j34")
				.zpid(290853L).lastSoldDate("20080419")
				.longitude(138091).latitude(187254)
				.squareFeet(2001).yearBuilt(1993)
				.soldPrice(189500)
				.build();
		VectorWritable vw = m.vectorizeHouse(house);
		Vector v = vw.get();
		Assert.assertEquals(7,v.size());
		
		Assert.assertTrue(House.isEquals(v.get(0), 25));
		Assert.assertTrue(House.isEquals(v.get(1), 25));
		Assert.assertTrue(House.isEquals(v.get(2), 23.0769));
		Assert.assertTrue(House.isEquals(v.get(3), 0));
		Assert.assertTrue(House.isEquals(v.get(4), 0));
		Assert.assertTrue(House.isEquals(v.get(5), 80));
		Assert.assertTrue(House.isEquals(v.get(6), 0));
	}
}
