package jinvestor.jhouse.mr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jinvestor.jhouse.core.House;
import jinvestor.jhouse.core.House.HouseBuilder;
import jinvestor.jhouse.core.util.DateUtil;
import jinvestor.jhouse.mr.HouseRangeFinder.HouseRangeFindingReducer;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.junit.Assert;
import org.junit.Test;


public class HouseRangeFinderTest {
	
	@Test
	public void findRange() throws IOException {
		InputStream input = HouseRangeFinderTest.class.getResourceAsStream("/jinvestor/jhouse/mr/sampleForFindRange.txt");
		String in = IOUtils.toString(input);
		IOUtils.closeQuietly(input);
		House[] mm = HouseRangeFinder.findRange(in);
		House min = mm[0];
		House max = mm[1];
		Assert.assertEquals(1,min.getBeds());
		Assert.assertEquals(8,max.getBeds());
		Assert.assertTrue(House.isEquals(1, min.getBaths()));
		Assert.assertTrue(House.isEquals(5, max.getBaths()));
		Assert.assertEquals(1910,min.getYearBuilt());
		Assert.assertEquals(2013,max.getYearBuilt());
		Assert.assertEquals(18723,min.getLongitude());
		Assert.assertEquals(20958,max.getLongitude());
	}
	
	@Test 
	public void isInteger() {
		Assert.assertTrue(HouseRangeFinder.isInteger(new Text("beds")));
		Assert.assertFalse(HouseRangeFinder.isInteger(new Text("baths")));
	}

	@Test
	public void makeOutput() {
		Date d = DateUtil.parseToDayOfMonth("20100318");
		long tm = d.getTime();
		House house = new HouseBuilder().acres(4).beds(3).baths(2.5f)
				.longitude(13243).latitude(29583).address("lkaqj3")
				.lastSoldDate(d).build();
		Map<Text,Text> map = HouseRangeFinder.makeOutput(house);
		Assert.assertEquals(new Text("2.5"),map.get(new Text("baths")));
		Assert.assertEquals(new Text(String.valueOf(tm)),map.get(new Text("lastsoldtimestamp")));
		Assert.assertEquals(new Text("3"),map.get(new Text("beds")));
	}

	@Test
	public void reduce() throws IOException {
		HouseRangeFindingReducer reducer = new HouseRangeFindingReducer();
		Text key = new Text("acres");
		List<Text> values = Arrays.asList(new Text("8"), new Text("20"),
				new Text("4"), new Text("10"));
		MyCollector output = new MyCollector();
		reducer.reduce(key, values.iterator(), output, null);
		Map<String, String> results = output.getResults();
		Assert.assertEquals(2, results.size());
		Assert.assertEquals("4.0", results.get("acres-min"));
		Assert.assertEquals("20.0", results.get("acres-max"));
	}

	@Test
	public void reduceBeds() throws IOException {
		HouseRangeFindingReducer reducer = new HouseRangeFindingReducer();
		Text key = new Text("beds");
		List<Text> values = Arrays.asList(new Text("8"), new Text("20"),
				new Text("4"), new Text("10"));
		MyCollector output = new MyCollector();
		reducer.reduce(key, values.iterator(), output, null);
		Map<String, String> results = output.getResults();
		Assert.assertEquals(2, results.size());
		Assert.assertEquals("4", results.get("beds-min"));
		Assert.assertEquals("20", results.get("beds-max"));
	}

	private static final class MyCollector implements
			OutputCollector<Text, Text> {
		private Map<String, String> output = new HashMap<String, String>();

		@Override
		public void collect(Text key, Text value) throws IOException {
			output.put(key.toString(), value.toString());
		}

		public Map<String, String> getResults() {
			return output;
		}
	}
}
