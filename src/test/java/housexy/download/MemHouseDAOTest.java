package housexy.download;

public class MemHouseDAOTest extends HouseDAOTest {

	@Override
	public HouseDAO getDao() {
		return new MemHouseDAO();
	}

}
