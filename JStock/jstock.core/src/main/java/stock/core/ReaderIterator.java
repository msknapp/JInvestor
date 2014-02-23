package stock.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.collections4.Transformer;
import org.apache.log4j.Logger;

public class ReaderIterator<T> implements CloseableIterator<T> {
	private static final Logger logger = Logger.getLogger(ReaderIterator.class);
	private final BufferedReader reader;
	private final Transformer<String, T> parser;
	
	private T next;

	public ReaderIterator(final Transformer<String,T> parser,final Reader reader,boolean hasHeader) {
		this.parser = parser;
		this.reader = new BufferedReader(reader);
		if (hasHeader) {
			try {
				this.reader.readLine();
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	private void readSome() {
		if (next !=null || reader == null) {
			return;
		}
		// read a line
		try {
			final String line = reader.readLine();
			if (line != null) {
				next = parser.transform(line);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean hasNext() {
		readSome();
		return next!=null;
	}

	@Override
	public T next() {
		readSome();
		final T tmp = next;
		next = null;
		return tmp;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove symbols."); 
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}
}