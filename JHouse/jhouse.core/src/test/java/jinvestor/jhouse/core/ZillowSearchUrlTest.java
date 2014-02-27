package jinvestor.jhouse.core;

import org.junit.Assert;
import org.junit.Test;

public class ZillowSearchUrlTest {

	@Test
	public void url() {
		ZillowSearchUrl url = ZillowSearchUrl.forSaleOnly();
		Assert.assertEquals("100000", url.status);
		String exp = "http://www.zillow.com/search/GetResults.htm?"
				+ "status=100000&lt=11110&ht=11111&pr=%2C&mp=%2C&bd=0%2C&ba=0%2C&sf=%2C&lot=%2C&yr=%2C&"
				+ "pho=0&pets=0&parking=0&laundry=0&pnd=0&red=0&zso=0&days=any&ds=all&"
				+ "pmf=0&pf=0&zoom=11&rect=-76974506%2C39213768%2C-76726284%2C39320089&"
				+ "p=1&sort=days&search=maplist&disp=1&listright=true&"
				+ "responsivemode=defaultList&isMapSearch=true";
		Assert.assertEquals(exp,url.toString());
	}
	
	@Test
	public void parseAndSet() {
		String source = "http://www.zillow.com/search/GetResults.htm?"
				+ "status=100000&lt=11110&ht=11111&pr=%2C&mp=%2C&bd=0%2C&ba=0%2C&sf=%2C&lot=%2C&yr=%2C&"
				+ "pho=0&pets=0&parking=0&laundry=0&pnd=0&red=0&zso=0&days=any&ds=all&"
				+ "pmf=0&pf=0&zoom=11&rect=-76974506%2C39213768%2C-76726284%2C39320089&"
				+ "p=1&sort=days&search=maplist&disp=1&listright=true&"
				+ "responsivemode=defaultList&isMapSearch=true";
		ZillowSearchUrl parsed = new ZillowSearchUrl(source);
		Assert.assertEquals("100000", parsed.status);
		Assert.assertEquals(source,parsed.toString());
		Assert.assertEquals("days", parsed.sort);
		parsed.p="8";
		String exp = "http://www.zillow.com/search/GetResults.htm?"
				+ "status=100000&lt=11110&ht=11111&pr=%2C&mp=%2C&bd=0%2C&ba=0%2C&sf=%2C&lot=%2C&yr=%2C&"
				+ "pho=0&pets=0&parking=0&laundry=0&pnd=0&red=0&zso=0&days=any&ds=all&"
				+ "pmf=0&pf=0&zoom=11&rect=-76974506%2C39213768%2C-76726284%2C39320089&"
				+ "p=8&sort=days&search=maplist&disp=1&listright=true&"
				+ "responsivemode=defaultList&isMapSearch=true";
		Assert.assertEquals(exp,parsed.toString());
	}
}
