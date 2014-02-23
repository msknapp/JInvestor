package stock.core;

import java.io.Reader;

public class StockSymbolReaderIterator extends ReaderIterator<StockSymbol> {

	public StockSymbolReaderIterator(final Reader reader,boolean hasHeader) {
		super(new DefaultStockSymbolLineParser(),reader,hasHeader);
	}
}