package jinvestor.jhouse.download;

import jinvestor.jhouse.core.HouseDAO;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

// MT stands for manual test
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/applicationContext.xml"})
@ActiveProfiles(profiles={"test","mysql"})
public class HibernateHouseDAOMT extends HouseDAOTest {

	@Autowired
	private HouseDAO dao;
	
	@Override
	public HouseDAO getDao() {
		return dao;
	}
}