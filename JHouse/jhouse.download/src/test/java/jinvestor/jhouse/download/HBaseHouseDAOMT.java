package jinvestor.jhouse.download;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.junit.Test;

public class HBaseHouseDAOMT {

	@Test
	public void crud() throws ZooKeeperConnectionException {
		Configuration config = new Configuration();
		HConnection connection = HConnectionManager.createConnection(config);
		
	}
}
