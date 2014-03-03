package jinvestor.jhouse.mr;

import java.io.IOException;
import java.util.List;

import jinvestor.jhouse.core.House;
import jinvestor.jhouse.core.util.HouseAvroUtil;

import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;

public class HouseVectorizerMT {

	
	String minMax64 = "T2JqAQIWYXZyby5zY2hlbWGuCHsidHlwZSI6InJlY29yZCIsIm5hbWUiOiJIb3VzZSIsIm5hbWVzcGFjZSI6ImppbnZlc3Rvci5qaG91c2UuY29yZSIsImZpZWxkcyI6W3sibmFtZSI6InpwaWQiLCJ0eXBlIjoibG9uZyJ9LHsibmFtZSI6ImFjcmVzIiwidHlwZSI6WyJmbG9hdCIsIm51bGwiXX0seyJuYW1lIjoiYWRkcmVzcyIsInR5cGUiOlsic3RyaW5nIiwibnVsbCJdfSx7Im5hbWUiOiJiYXRocyIsInR5cGUiOlsiZmxvYXQiLCJudWxsIl19LHsibmFtZSI6ImJlZHMiLCJ0eXBlIjpbImludCIsIm51bGwiXX0seyJuYW1lIjoibGF0aXR1ZGUiLCJ0eXBlIjpbImludCIsIm51bGwiXX0seyJuYW1lIjoibG9uZ2l0dWRlIiwidHlwZSI6WyJpbnQiLCJudWxsIl19LHsibmFtZSI6InNvbGRwcmljZSIsInR5cGUiOlsiaW50IiwibnVsbCJdfSx7Im5hbWUiOiJsYXN0c29sZGRhdGUiLCJ0eXBlIjpbImxvbmciLCJudWxsIl19LHsibmFtZSI6InNxdWFyZWZlZXQiLCJ0eXBlIjpbImludCIsIm51bGwiXX0seyJuYW1lIjoieWVhcmJ1aWx0IiwidHlwZSI6WyJpbnQiLCJudWxsIl19XX0AxcQJPPJmdqtRCkJEU0FSNASAAQAAAAAAAAIAAAAAAAAAALCasyUAzZa0SQAAAgAAAAAAAJqZKUECAAAAEEEADgCy4L8lAPuqlkkAAAIAnLQBAADFxAk88mZ2q1EKQkRTQVI0";
	
	@Test
	public void vectorize() throws ClassNotFoundException, IOException, InterruptedException {
		System.setProperty("java.library.path","/usr/lib/hadoop/lib:/usr/lib/hadoop-mapreduce/lib:/usr/lib/hadoop-0.20-mapreduce/lib:"+System.getProperty("java.library.path"));
		System.out.println(System.getProperty("java.library.path"));
		List<House> minmax = HouseAvroUtil.fromBase64String(minMax64); 
		House minimumHouse = minmax.get(0); 
		House maximumHouse = minmax.get(1);
		HouseVectorizer hv = new HouseVectorizer(new Configuration(), minimumHouse, maximumHouse);
		hv.vectorize();
		HouseVectorizer.CountingVectorCallback cb = new HouseVectorizer.CountingVectorCallback();
		hv.readResults(cb);
		Assert.assertTrue(cb.getCount()>3);
	}
}
