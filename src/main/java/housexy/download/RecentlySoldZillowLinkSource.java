package housexy.download;

import java.util.ArrayList;
import java.util.List;

public class RecentlySoldZillowLinkSource implements ZillowLinkSource {

	private final String urlTemplate;
	
	public RecentlySoldZillowLinkSource(final String urlTemplate) {
		this.urlTemplate = urlTemplate;
	}
	
	@Override
	public Iterable<String> getLinks() {
		List<String> links = new ArrayList<String>();
		for (int i =1;i<=20;i++) {
			links.add(urlTemplate.replace("PAGE", String.valueOf(i)));
		}
		return links;
	}

}
