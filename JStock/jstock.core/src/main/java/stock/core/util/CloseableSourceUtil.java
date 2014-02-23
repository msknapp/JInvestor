package stock.core.util;

import java.io.IOException;

import org.apache.commons.collections4.Closure;

import stock.core.CloseableIterator;
import stock.core.CloseableSource;

public final class CloseableSourceUtil {

	private CloseableSourceUtil() {
	}

	public static <T> void process(final CloseableSource<T> source,
			final Closure<T> handler) {
		final CloseableIterator<T> iter = source.iterator();
		try {
			while (iter.hasNext()) {
				final T stockSymbol = iter.next();
				handler.execute(stockSymbol);
			}
		} finally {
			if (iter != null) {
				try {
					iter.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
