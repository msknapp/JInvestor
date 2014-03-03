package jinvestor.jhouse.download;

/*
 * #%L
 * jinvestor.parent
 * %%
 * Copyright (C) 2014 Michael Scott Knapp
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import jinvestor.jhouse.core.MemHouseDAO;
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
