package jinvestor.jhouse.core;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * A domain object that stores all of the zillow
 * url fields.  This is mutable, and uses reflection 
 * to set its values and build its string value.
 * The field names actually matter, they match what
 * zillow expects in the url parameters.
 * @author cloudera
 *
 */
public class ZillowSearchUrl {
	private static final Logger logger = Logger
			.getLogger(ZillowSearchUrl.class);
	private static String ENC = "UTF-8";
	private String base = "http://www.zillow.com/search/GetResults.htm";
	
	// this controls what sort of properties can be returned,
	// the first digit includes "for sale", second "pre-market"
	// third is "recently sold", fifth is "for rent", and I don't
	// know what the others control.
	public String status = "001000";
	
	// this seems to control what types of for sale properties can 
	// be returned
	public String lt = "00000";
	
	// I don't know what this controls
	public String ht = "11111";
	public String pr = ",";
	public String mp = ",";
	public String bd = "0,";
	public String ba = "0,";
	public String sf = ",";
	public String lot = ",";
	public String yr = ",";
	public String pho = "0";
	public String pets = "0";
	public String parking = "0";
	public String laundry = "0";
	public String pnd = "0";
	public String red = "0";
	public String zso = "0";
	public String days = "any";
	public String ds = "all";
	public String pmf = "0";
	public String pf = "0";
	public String zoom = "11";
	
	// the lat/long rectangle
	public String rect = "-76974506,39213768,-76726284,39320089";
	
	// the page number.
	public String p = "1";
	public String sort = "days";
	public String search = "maplist";
	public String disp = "1";
	public String listright = "true";
	public String responsivemode = "defaultList";
	public String isMapSearch = "true";

	public static ZillowSearchUrl forSaleOnly() {
		ZillowSearchUrl url = new ZillowSearchUrl();
		url.status="100000";
		url.lt="11110";
		url.ht="11111";
		return url;
	}
	
	public static ZillowSearchUrl recentlySoldOnly() {
		ZillowSearchUrl url = new ZillowSearchUrl();
		url.status="001000";
		url.lt="00000";
		url.ht="11111";
		return url;
	}
	
	public static ZillowSearchUrl forSaleAndRecentlySold() {
		ZillowSearchUrl url = new ZillowSearchUrl();
		url.status="101000";
		url.lt="11110";
		url.ht="11111";
		return url;
	}
	
	public ZillowSearchUrl() {

	}

	public ZillowSearchUrl(String fullUrl) {
		int i = fullUrl.indexOf('?');
		if (i > 0) {
			this.base = fullUrl.substring(0, i);
			String parms = fullUrl.substring(i + 1);
			String[] ps = parms.split("&");
			for (String p : ps) {
				String[] kv = p.split("=");
				try {
					set(URLDecoder.decode(kv[0], ENC),
							URLDecoder.decode(kv[1], ENC));
				} catch (UnsupportedEncodingException e) {
					logger.error(e);
				}
			}
		} else {
			this.base = fullUrl;
		}
	}

	private void set(String fieldName, String value) {
		try {
			Field f = this.getClass().getDeclaredField(fieldName);
			if (f != null) {
				boolean p = Modifier.isPublic(f.getModifiers());
				if (!p) {
					f.setAccessible(true);
				}
				f.set(this, value);
				if (!p) {
					f.setAccessible(false);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(base).append('?');
		Field[] fs = this.getClass().getDeclaredFields();
		List<String> sss = new ArrayList<>();
		for (Field f : fs) {
			if ("base".equals(f.getName()) || Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			try {
				sss.add(String.format("%s=%s",
						URLEncoder.encode(f.getName(), ENC),
						URLEncoder.encode(f.get(this).toString(), ENC)));
			} catch (Exception e) {
				logger.error(e);
			}
		}
		sb.append(StringUtils.join(sss,"&"));
		return sb.toString();
	}
}