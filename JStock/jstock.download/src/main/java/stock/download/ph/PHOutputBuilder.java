package stock.download.ph;

import java.io.IOException;

import stock.core.TimeSpan;
import stock.download.OutputWithMeta;

public interface PHOutputBuilder<T> {
	String determineUrl(T stockSymbol,
			TimeSpan timeSpan);
	OutputWithMeta buildOutputStream(T stockSymbol,
			TimeSpan timeSpan,String url) throws IOException;
	void handleFailure(T stockSymbol, TimeSpan timeSpan,
			String url);
}
