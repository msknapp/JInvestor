package jinvestor.jhouse.download;

import java.util.List;

import jinvestor.jhouse.core.ZillowSearchUrl;

import org.apache.commons.collections4.IteratorUtils;
import org.junit.Assert;
import org.junit.Test;

public class SimpleZillowLinkSourceTest {
	
	@Test
	public void links() {
		ZillowLinkSource source = new SimpleZillowLinkSource(ZillowSearchUrl.forSaleOnly());
		List<String> links = IteratorUtils.toList(source.getLinks().iterator());
		String exp = "http://www.zillow.com/search/GetResults.htm?status=100000&lt=11110&"
				+ "ht=11111&pr=%2C&mp=%2C&bd=0%2C&ba=0%2C&sf=%2C&lot=%2C&yr=%2C&pho=0&"
				+ "pets=0&parking=0&laundry=0&pnd=0&red=0&zso=0&days=any&ds=all&pmf=0&"
				+ "pf=0&zoom=11&rect=-76974506%2C39213768%2C-76726284%2C39320089&p=10&"
				+ "sort=days&search=maplist&disp=1&listright=true&"
				+ "responsivemode=defaultList&isMapSearch=true";
		// indexes are off by one.
		Assert.assertEquals(exp,links.get(9));
	}
}
