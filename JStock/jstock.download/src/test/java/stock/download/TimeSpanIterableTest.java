package stock.download;

import java.text.ParseException;
import java.util.List;

import org.apache.commons.collections4.IteratorUtils;
import org.junit.Assert;
import org.junit.Test;

import stock.core.TimeSpan;

public class TimeSpanIterableTest {

	
	@Test
	public void iterate() throws ParseException {
		TimeSpan ts = new TimeSpan("2000-01-01");
		TimeSpanIterable tsi = new TimeSpanIterable(ts, 60);
		List<TimeSpan> tss = IteratorUtils.toList(tsi.iterator());
		Assert.assertTrue(tss.size()>20);
//		for (TimeSpan t : tss) {
//			System.out.println(t);
//		}
	}
}
