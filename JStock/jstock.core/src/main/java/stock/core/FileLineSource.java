package stock.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.collections4.TransformerUtils;
import org.apache.log4j.Logger;

public class FileLineSource implements CloseableSource<String> {
	private static final Logger logger = Logger
			.getLogger(FileLineSource.class);
	private final File source;

	private CloseableIterator<String> knownIter;

	private final boolean hasHeader;
	
	public FileLineSource(final File source,boolean hasHeader) {
		this.source = source;
		this.hasHeader = hasHeader;
	}

	@Override
	public void close() throws IOException {
		if (knownIter != null) {
			knownIter.close();
		}
	}

	@Override
	public CloseableIterator<String> iterator() {
		try {
			knownIter = new ReaderIterator<String>(TransformerUtils.<String>nopTransformer(), new FileReader(source), hasHeader);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		}
		return knownIter;
	}
	

}
