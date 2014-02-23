package jinvestor.jhouse.download;

import jinvestor.jhouse.download.HouseDAO;
import jinvestor.jhouse.download.MemHouseDAO;

public class MemHouseDAOTest extends HouseDAOTest {

	@Override
	public HouseDAO getDao() {
		return new MemHouseDAO();
	}

}
