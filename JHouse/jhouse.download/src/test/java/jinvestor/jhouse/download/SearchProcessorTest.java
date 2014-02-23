package jinvestor.jhouse.download;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import jinvestor.jhouse.download.MemHouseDAO;
import jinvestor.jhouse.download.SearchProcessor;

import org.codehaus.jackson.JsonProcessingException;
import org.junit.Test;
import org.xml.sax.SAXException;

public class SearchProcessorTest {

	@Test
	public void process() throws JsonProcessingException, IOException, SAXException, ParserConfigurationException {
		MemHouseDAO dao = new MemHouseDAO();
		SearchProcessor p = new SearchProcessor(dao);
		p.process(SearchProcessorTest.class.getResourceAsStream("/sample_search.json"));
		System.out.println(dao.count(null));
	}
}
