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

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import jinvestor.jhouse.core.House;
import jinvestor.jhouse.core.House.HouseBuilder;
import jinvestor.jhouse.core.HouseDAO;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SearchProcessor {
	private static final Logger logger = Logger
			.getLogger(SearchProcessor.class);

	private final HouseDAO dao;

	public SearchProcessor(final HouseDAO dao) {
		this.dao = dao;
	}

	public void process(InputStream in) {
		// first use Jackson to parse the json
		try {
			ObjectMapper m = new ObjectMapper();
			JsonNode rootNode = m.readTree(in);

			JsonNode n2 = rootNode.path("list").path("listHTML");
			String xml = n2.getTextValue();
			xml = StringEscapeUtils.unescapeXml(xml);
			String html = new StringBuilder().append("<html><head></head><body>")
					.append(xml).append("</body>").toString();
			
			// then parse the xml.
			Document doc2 = Jsoup.parse(html);
			Elements elements = doc2.getElementsByClass("property-listing");
			Iterator<Element> iter = elements.iterator();
			while (iter.hasNext()) {
				Element element = iter.next();
				processElement(element);
			}
		} catch (Exception e) {
			logger.error("Failed to process a search", e);
		}
	}

	public void processElement(Element element) {
		House.HouseBuilder b = House.builder();
		b.zpid(getZid(element));
		b.acres(getAcres(element));
		b.address(getAddress(element));
		processPropertyDetails(b, element);
		b.lastSoldDate(getLastSoldDate(element));
		b.latitude(getLatitude(element));
		b.longitude(getLongitude(element));
		b.soldPrice(getSoldPrice(element));
		dao.save(b.build());
	}

	private long getZid(Element element) {
		String s = element.attr("id");
		String[] ps = s.split("_");
		return Long.parseLong(ps[1]);
	}

	private String getAddress(Element element) {
		return element.select("img").first().attr("alt");
	}

	private void processPropertyDetails(HouseBuilder b, Element element) {
		Element elm = element.select(".property-data").first();
		if (elm == null) {
			return;
		}
		String s = elm.text();
		String[] p = s.split(",\\s+");
		for (String part : p) {
			if (part.endsWith("beds")) {
				b.beds(Byte.parseByte(part.split(" ")[0]));
			}
			if (part.endsWith("baths")) {
				b.baths(Float.parseFloat(part.split(" ")[0]));
			}
			if (part.endsWith("sqft")) {
				b.squareFeet(Integer.parseInt(part.split(" ")[0].replace(",",
						"")));
			}
		}
	}

	private Date getLastSoldDate(Element element) {
		String s = element.select(".sold-date").first().text();
		String[] p = s.split("\\s+");
		String lst = p[p.length - 1];
		SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yy");
		Date d = null;
		try {
			d = sdf.parse(lst);
		} catch (ParseException e) {
			logger.error("Failed to parse the date " + lst);
		}
		return d;
	}

	private int getLatitude(Element element) {
		return Integer.parseInt(element.attr("latitude"));
	}

	private int getLongitude(Element element) {
		return Integer.parseInt(element.attr("longitude"));
	}

	private int getSoldPrice(Element element) {
		Elements elms = element.select("strong");
		Iterator<Element> iter = elms.iterator();
		while (iter.hasNext()) {
			Element elm = iter.next();
			if (elm.text().startsWith("Sold: ")) {
				String p = elm.text().substring(7).replace(",", "");
				return Integer.parseInt(p);
			}
		}
		return -1;
	}

	public float getAcres(Element element) {
		Elements elms = element.select(".property-lot");
		Element f = elms.first();
		String s = f.text();
		String[] parts = s.split("\\s+");
		float v = Float.parseFloat(parts[0].replace(",", ""));
		String units = parts[1];
		if ("ac".equals(units)) {
			return v;
		} else if ("sqft".equals(units)) {
			return v / 43560f;
		} else {
			logger.warn("Unknown units: " + units);
		}
		return 0;
	}

}
