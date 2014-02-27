package jinvestor.jhouse.download;

import jinvestor.jhouse.core.HouseDAO;

import org.apache.hadoop.conf.Configuration;

public class HBaseHouseDAOMT extends HouseDAOTest {

	@Override
	public HouseDAO getDao() {
		Configuration config = new Configuration();
		HBaseHouseDAO dao = new HBaseHouseDAO(config);
		return dao;
	}
}