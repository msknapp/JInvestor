package jinvestor.jhouse.core;

import jinvestor.jhouse.core.House.HouseBuilder;
import jinvestor.jhouse.core.util.DateUtil;

import org.junit.Assert;
import org.junit.Test;

public class HouseTest {

	@Test
	public void setField() {
		String dom = "20100318";
		HouseBuilder hb = new HouseBuilder();
		hb.setField("acres", "3.5");
		hb.setField("beds", "4");
		hb.setField("baths", "2.5");
		hb.setField("squarefeet", "5392");
		hb.setField("lastsoldtimestamp",String.valueOf(DateUtil.millisFromDayOfMonth(dom)));
		House h = hb.build();
		Assert.assertTrue(House.isEquals(3.5f, h.getAcres()));
		Assert.assertTrue(House.isEquals(2.5f, h.getBaths()));
		Assert.assertEquals(4,h.getBeds());
		Assert.assertEquals(5392,h.getSquareFeet());
		Assert.assertEquals(DateUtil.parseToDayOfMonth(dom),h.getLastSoldDate());
	}
}