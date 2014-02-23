package stock.core;

/*
 * #%L
 * jinvestor.parent
 * %%
 * Copyright (C) 2014 Michael Scott Knapp
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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