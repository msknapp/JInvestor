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
