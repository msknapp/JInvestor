package housexy.util;

import housexy.House;

import org.junit.Assert;
import org.junit.Test;

public class HouseQueryUtilTest {

	
	@Test
	public void matchesComplex() {
		House h = House.builder().beds((byte)3).baths(2f).acres(1).build();
		Assert.assertTrue(HouseQueryUtil.matches(h, "beds > 2 and (baths = 1 or baths=2) and acres > 0.5"));
		Assert.assertFalse(HouseQueryUtil.matches(h, "beds > 3 and (baths = 1 or baths=2) and acres > 0.5"));
		Assert.assertFalse(HouseQueryUtil.matches(h, "beds > 2 and (baths = 1 or baths=3) and acres > 0.5"));
	}
	
	@Test
	public void matchesSimple() {
		String query = "zpid = '12345'";
		House h = House.builder().zpid(12345L).build();
		Assert.assertTrue(HouseQueryUtil.matches(h, query));
		Assert.assertFalse(HouseQueryUtil.matches(h, "zpid='523'"));
	}
}
