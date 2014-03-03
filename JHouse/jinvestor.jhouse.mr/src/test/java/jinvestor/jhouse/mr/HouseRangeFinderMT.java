package jinvestor.jhouse.mr;

import java.io.IOException;
import java.util.Arrays;

import jinvestor.jhouse.core.House;
import jinvestor.jhouse.core.util.HouseAvroUtil;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

public class HouseRangeFinderMT {
	
	// manual test for house range finder
	
	@Test
	public void test() throws IOException, ClassNotFoundException, InterruptedException {
		HouseRangeFinder hrf = new HouseRangeFinder(new Configuration());
		House[] homes = hrf.findRange();
		String s = ToStringBuilder.reflectionToString(homes[0]);
		String s2 = ToStringBuilder.reflectionToString(homes[1]);
		System.out.println(s);
		System.out.println(s2);
		System.out.println(HouseAvroUtil.toString(Arrays.asList(homes[0],homes[1])));
	}
}

// T2JqAQIWYXZyby5zY2hlbWGuCHsidHlwZSI6InJlY29yZCIsIm5hbWUiOiJIb3VzZSIsIm5hbWVzcGFjZSI6ImppbnZlc3Rvci5qaG91c2UuY29yZSIsImZpZWxkcyI6W3sibmFtZSI6InpwaWQiLCJ0eXBlIjoibG9uZyJ9LHsibmFtZSI6ImFjcmVzIiwidHlwZSI6WyJmbG9hdCIsIm51bGwiXX0seyJuYW1lIjoiYWRkcmVzcyIsInR5cGUiOlsic3RyaW5nIiwibnVsbCJdfSx7Im5hbWUiOiJiYXRocyIsInR5cGUiOlsiZmxvYXQiLCJudWxsIl19LHsibmFtZSI6ImJlZHMiLCJ0eXBlIjpbImludCIsIm51bGwiXX0seyJuYW1lIjoibGF0aXR1ZGUiLCJ0eXBlIjpbImludCIsIm51bGwiXX0seyJuYW1lIjoibG9uZ2l0dWRlIiwidHlwZSI6WyJpbnQiLCJudWxsIl19LHsibmFtZSI6InNvbGRwcmljZSIsInR5cGUiOlsiaW50IiwibnVsbCJdfSx7Im5hbWUiOiJsYXN0c29sZGRhdGUiLCJ0eXBlIjpbImxvbmciLCJudWxsIl19LHsibmFtZSI6InNxdWFyZWZlZXQiLCJ0eXBlIjpbImludCIsIm51bGwiXX0seyJuYW1lIjoieWVhcmJ1aWx0IiwidHlwZSI6WyJpbnQiLCJudWxsIl19XX0AxcQJPPJmdqtRCkJEU0FSNASAAQAAAAAAAAIAAAAAAAAAALCasyUAzZa0SQAAAgAAAAAAAJqZKUECAAAAEEEADgCy4L8lAPuqlkkAAAIAnLQBAADFxAk88mZ2q1EKQkRTQVI0
