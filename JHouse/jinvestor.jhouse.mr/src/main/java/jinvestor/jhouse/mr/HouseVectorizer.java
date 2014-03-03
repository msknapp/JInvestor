package jinvestor.jhouse.mr;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jinvestor.jhouse.core.House;
import jinvestor.jhouse.core.util.HouseAvroUtil;
import jinvestor.jhouse.download.HBaseHouseDAO;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.VectorWritable;

/**
 * Produces mahout vectors from House entries in HBase.
 * 
 * @author Michael Scott Knapp
 * 
 */
public class HouseVectorizer {

	private final Configuration configuration;
	private final House minimumHouse;
	private final House maximumHouse;

	public HouseVectorizer(final Configuration configuration,
			final House minimumHouse, final House maximumHouse) {
		this.configuration = configuration;
		this.minimumHouse = minimumHouse;
		this.maximumHouse = maximumHouse;
	}

	public void vectorize() throws IOException {
		JobConf jobConf = new JobConf();
		jobConf.setMapOutputKeyClass(LongWritable.class);
		jobConf.setMapOutputValueClass(VectorWritable.class);

		// we want the vectors written straight to HDFS,
		// the order does not matter.
		jobConf.setNumReduceTasks(0);

		jobConf.setOutputFormat(SequenceFileOutputFormat.class);

		Path outputDir = new Path("/home/cloudera/house_vectors");
		FileSystem fs = FileSystem.get(configuration);
		if (fs.exists(outputDir)) {
			fs.delete(outputDir, true);
		}

		FileOutputFormat.setOutputPath(jobConf, outputDir);

		// I want the mappers to know the max and min value
		// so they can normalize the data.
		// I will add them as properties in the configuration,
		// by serializing them with avro.
		String minmax = HouseAvroUtil.toString(Arrays.asList(minimumHouse,
				maximumHouse));
		jobConf.set("minmax", minmax);

		Job job = Job.getInstance(jobConf);
		Scan scan = new Scan();
		scan.addFamily(Bytes.toBytes("data"));
		TableMapReduceUtil.initTableMapperJob("homes", scan,
				HouseVectorizingMapper.class, LongWritable.class,
				VectorWritable.class, job);
		
		JobClient.runJob(jobConf);
	}

	public static class HouseVectorizingMapper extends
			TableMapper<LongWritable, VectorWritable> {

		private House minimumHouse;
		private House maximumHouse;

		@Override
		public void setup(Context context) {
			// this class needs to know what the range of values are.
			// it can get this using the job configuration property.
			String minmax = context.getConfiguration().get("minmax");
			List<House> homes = HouseAvroUtil.fromString(minmax);
			minimumHouse = homes.get(0);
			maximumHouse = homes.get(1);
		}

		@Override
		protected void map(ImmutableBytesWritable key, Result result,
				Context context) throws IOException, InterruptedException {
			House house = HBaseHouseDAO.makeHouse(result);
			VectorWritable vector = vectorizeHouse(house);
			context.write(new LongWritable(house.getZpid()), vector);
		}

		public VectorWritable vectorizeHouse(House house) {
			// homes have these important attributes to vectorize:
			// squareFeet, beds, baths, latitude, longitude, yearBuilt, acres

			// hopefully one day we will also use a house description, however,
			// that presents a challenge because that is usually only available
			// for homes that are for sale.

			// perhaps one day we can get more information on each house...
			// the school system, county it's in, number of stories, whether
			// it has a garage or not, has a finished basement, property type,
			// parking conditions, condition of home equipment, etc.

			// for now I am trying to learn how to cluster.

			// let's use numbers from 0 to 100.

			double[] values = new double[7];
			if (house.getSquareFeet() > 0 && maximumHouse.getSquareFeet()>minimumHouse.getSquareFeet()) {
				double val = 100
						* (house.getSquareFeet() - minimumHouse.getSquareFeet())
						/ (maximumHouse.getSquareFeet() - minimumHouse
								.getSquareFeet());
				values[0] = val;
			} else {
				values[0] = 0;
			}
			if (house.getBeds() > 0 && maximumHouse.getBeds()>minimumHouse.getBeds()) {
				double val = 100 * (house.getBeds() - minimumHouse.getBeds())
						/ (maximumHouse.getBeds() - minimumHouse.getBeds());
				values[1] = val;
			} else {
				values[1] = 0;
			}
			if (house.getBaths() > 0.001 && maximumHouse.getBaths()>minimumHouse.getBaths()) {
				double val = 100 * (house.getBaths() - minimumHouse.getBaths())
						/ (maximumHouse.getBaths() - minimumHouse.getBaths());
				values[2] = val;
			} else {
				values[2] = 0;
			}
			if (isSet(house.getLatitude()) && isSet(minimumHouse.getLatitude()) && maximumHouse.getLatitude()>minimumHouse.getLatitude()) {
				double val = 100
						* (house.getLatitude() - minimumHouse.getLatitude())
						/ (maximumHouse.getLatitude() - minimumHouse
								.getLatitude());
				values[3] = val;
			} else {
				values[3] = 0;
			}

			if (isSet(house.getLongitude()) && isSet(minimumHouse.getLongitude()) && maximumHouse.getLongitude()>minimumHouse.getLongitude()) {
				double val = 100
						* (house.getLongitude() - minimumHouse.getLongitude())
						/ (maximumHouse.getLongitude() - minimumHouse
								.getLongitude());
				values[4] = val;
			} else {
				values[4] = 0;
			}

			if (house.getYearBuilt() > 0 && maximumHouse.getYearBuilt()>minimumHouse.getYearBuilt()) {
				double val = 100
						* (house.getYearBuilt() - minimumHouse.getYearBuilt())
						/ (maximumHouse.getYearBuilt() - minimumHouse
								.getYearBuilt());
				values[5] = val;
			} else {
				values[5] = 0;
			}

			if (isSet(house.getAcres()) && isSet(maximumHouse.getAcres())) {
				double val = 100 * (house.getAcres() - minimumHouse.getAcres())
						/ (maximumHouse.getAcres() - minimumHouse.getAcres());
				values[6] = val;
			} else {
				values[6] = 0;
			}
			DenseVector dv = new DenseVector(values);
			NamedVector nv = new NamedVector(dv, house.getAddress());
			return new VectorWritable(nv);
		}
		
		private boolean isSet(double v) {
			return Math.abs(v-0)>0.001 && Math.abs(v-Double.MIN_VALUE)>0.001;
		}
		
		private boolean isSet(float v) {
			return Math.abs(v-0)>0.001 && Math.abs(v-Float.MIN_VALUE)>0.001;
		}
		
		private boolean isSet(int v) {
			return v!=0 && v!=Integer.MIN_VALUE;
		}
	}
}