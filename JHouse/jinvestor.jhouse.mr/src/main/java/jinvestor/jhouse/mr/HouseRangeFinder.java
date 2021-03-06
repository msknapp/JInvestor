package jinvestor.jhouse.mr;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jinvestor.jhouse.core.House;
import jinvestor.jhouse.core.House.HouseBuilder;
import jinvestor.jhouse.download.HBaseHouseDAO;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.RowCounter;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.log4j.Logger;

/**
 * Uses map/reduce to find the range of values 
 * for several house numbers.  This is useful because
 * it lets us normalize the data.
 * @author Michael Scott Knapp
 *
 */
public class HouseRangeFinder {
	private static final Logger logger = Logger.getLogger(HouseRangeFinder.class);
	private static final Text ACRES=new Text("acres");
	private static final Text BATHS=new Text("baths");
	private static final Text LATITUDE=new Text("latitude");
	private static final Text LONGITUDE=new Text("longitude");
	private static final Text SOLDPRICE=new Text("soldprice");
	private static final Text SQUAREFEET=new Text("squarefeet");
	private static final Text YEARBUILT=new Text("yearbuilt");
	private static final Text BEDS=new Text("beds");
	private static final Text LASTSOLDTIME=new Text("lastsoldtimestamp");
	
	private final Configuration configuration;
	

//	RowCounter rc;
	
	public HouseRangeFinder(final Configuration configuration) {
		this.configuration = configuration;
	}
	
	public static boolean isInteger(Text text) {
		return text.toString().equals(YEARBUILT.toString()) || 
				text.toString().equals(BEDS.toString()) || 
				text.toString().equals(SQUAREFEET.toString()) || 
				text.toString().equals(LASTSOLDTIME.toString()) || 
				text.toString().equals(LATITUDE.toString()) || 
				text.toString().equals(LONGITUDE.toString()) || 
				text.toString().equals(SOLDPRICE.toString()) || 
				text.toString().equals(LASTSOLDTIME.toString());
	}
	
	public House[] findRange() throws IOException, ClassNotFoundException, InterruptedException {
		return findRange(this.configuration);
	}
	
	public static House[] findRange(final Configuration configuration) throws IOException, ClassNotFoundException, InterruptedException {
		JobConf jobConf = new JobConf();
		// no need to set the mapper, TableMapReduceUtil will do that.
		jobConf.setCombinerClass(HouseRangeFindingCombiner.class);
		jobConf.setReducerClass(HouseRangeFindingReducer.class);
		jobConf.setMapOutputKeyClass(Text.class);
		jobConf.setMapOutputValueClass(Text.class);
		// no need to set the input format, TableMapReduceUtil will do that.
//		jobConf.setInputFormat(TextInputFormat.class);
		jobConf.setOutputFormat(TextOutputFormat.class);
		
		Path outputDir = new Path("/home/cloudera/house_ranges");
		FileSystem fs = FileSystem.get(configuration);
		if (fs.exists(outputDir)) {
			fs.delete(outputDir, true);
		}
		
		FileOutputFormat.setOutputPath(jobConf, outputDir);
		
		Job job = Job.getInstance(jobConf);
		Scan scan = new Scan();
		scan.addFamily(Bytes.toBytes("data"));
		TableMapReduceUtil.initTableMapperJob("homes", scan, HouseRangeFindingTableMapper.class, Text.class, Text.class, job);
		
//		JobClient.runJob(jobConf);
		job.waitForCompletion(true);
		
		// read back the results.
		RemoteIterator<LocatedFileStatus> iter = fs.listFiles(outputDir, false);
		StringBuilder sb = new StringBuilder();
		while (iter.hasNext()) {
			LocatedFileStatus lfs = iter.next();
			// The amount of data should not be large
			// one file per reducer.  each should be really small.
			if (lfs.getPath().getName().startsWith("part")) {
				FSDataInputStream in = fs.open(lfs.getPath());
				String s = IOUtils.toString(in);
				sb.append(s);
				IOUtils.closeQuietly(in);
			}
		}
		House[] range = findRange(sb.toString());
		return range;
	}
	
//	private static House[] findRange(final FSDataInputStream in) {
//		String input = null;
//		try {
//			input = IOUtils.toString(in);
//		} catch (IOException e) {
//			logger.error(e);
//		} finally {
//			IOUtils.closeQuietly(in);
//		}
//		return findRange(input);
//	}
	
	public static House[] findRange(String input) {
		if (input==null) {
			return null;
		}
		HouseBuilder minB = new HouseBuilder();
		HouseBuilder maxB = new HouseBuilder();
		for (String line : input.split("\n")) {
			String[] kv = line.split("\\s+");
			String[] km = kv[0].split("-");
			String fieldName = km[0];
			boolean max = "max".equals(km[1]);
			if (max) {
				maxB.setField(fieldName,kv[1]);
			} else {
				minB.setField(fieldName,kv[1]);
			}
		}
		House min = minB.build();
		House max = maxB.build();
		return new House[] {min,max};
	}

	// the output key is the house field name,
	// the output value is the house field value,
	public static class HouseRangeFindingTableMapper extends TableMapper<Text,Text> {
		@Override
		protected void map(ImmutableBytesWritable rowKey,Result result, Context context) throws IOException, InterruptedException {
			if (result == null || context == null) {
				return;
			}
			House house = HBaseHouseDAO.makeHouse(result);
			if (house!=null) {
				Map<Text,Text> output = makeOutput(house);
				for (Text key : output.keySet()) {
					context.write(key, output.get(key));
				}
			}
		}
	}
	
	public static Map<Text,Text> makeOutput(House house) {
		Map<Text,Text> map = new HashMap<Text,Text>();
		// don't need address since that has no range.
		map.put(ACRES, new Text(String.valueOf(house.getAcres())));
		map.put(BATHS, new Text(String.valueOf(house.getBaths())));
		map.put(LATITUDE, new Text(String.valueOf(house.getLatitude())));
		map.put(LONGITUDE, new Text(String.valueOf(house.getLongitude())));
		map.put(SOLDPRICE, new Text(String.valueOf(house.getSoldPrice())));
		map.put(SQUAREFEET, new Text(String.valueOf(house.getSquareFeet())));
		map.put(YEARBUILT, new Text(String.valueOf(house.getYearBuilt())));
		map.put(BEDS, new Text(String.valueOf(house.getBeds())));
		if (house.getLastSoldDate()!=null) {
			map.put(LASTSOLDTIME, new Text(String.valueOf(house.getLastSoldDate().getTime())));
		}
		return map;
	}

	public static class HouseRangeFindingCombiner extends HouseRangeFindingReducer {
		public HouseRangeFindingCombiner() {
			super(true);
		}
	}
	
	
	public static class HouseRangeFindingReducer extends MapReduceBase implements Reducer<Text,Text,Text,Text> {
		
		private boolean combiner = false;
		
		public HouseRangeFindingReducer() {
			this.combiner=false;
		}
		
		public HouseRangeFindingReducer(boolean combiner) {
			this.combiner=combiner;
		}

		@Override
		public void reduce(Text key, Iterator<Text> values,
				OutputCollector<Text, Text> output,
				Reporter reporter) throws IOException {
			if (key == null || values == null || output == null) {
				return;
			}
			// need to find the max and min values.
			double maxValue = Double.NEGATIVE_INFINITY;
			double minValue = Double.POSITIVE_INFINITY;
			while (values.hasNext()) {
				Text value = values.next();
				try {
					double v = Double.parseDouble(value.toString());
					if (v<minValue) {
						minValue = v;
					} else if (v>maxValue) {
						maxValue=v;
					}
				} catch (Exception e) {
					if (reporter!=null) {
						reporter.incrCounter("stats", "badvalue", 1L);
					}
				}
			}
			Text minOut = null;
			Text maxOut = null;
			if (isInteger(key)) {
				// round
				int min = (int)Math.round(minValue);
				int max = (int)Math.round(maxValue);
				minOut = new Text(String.format("%d", min));
				maxOut = new Text(String.format("%d", max));
			} else {
				minOut = new Text(String.format("%.1f", minValue));
				maxOut = new Text(String.format("%.1f", maxValue));
			}
			if (combiner) {
				output.collect(new Text(key.toString()), minOut);
				output.collect(new Text(key.toString()), maxOut);
			} else {
				output.collect(new Text(key.toString()+"-min"), minOut);
				output.collect(new Text(key.toString()+"-max"), maxOut);
			}
		}
		
	}
}
