package stock.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class FileSymbolSource implements CloseableSource<StockSymbol> {
	private static final Logger logger = Logger
			.getLogger(FileSymbolSource.class);
	private final File source;

	private CloseableIterator<StockSymbol> knownIter;

	@Autowired
	public FileSymbolSource(final File source) {
		this.source = source;
	}

	@Override
	public CloseableIterator<StockSymbol> iterator() {
		StockSymbolReaderIterator iter = null;
		try {
			iter = new StockSymbolReaderIterator(new FileReader(source),true);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		knownIter = iter;
		return iter;
	}

	@Override
	public void close() throws IOException {
		if (knownIter != null) {
			knownIter.close();
		}
	}
}