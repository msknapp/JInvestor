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

import java.util.Arrays;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

public class DefaultStockSymbolLineParser implements Transformer<String, StockSymbol> {

	@Override
	public StockSymbol transform(String line) {
		String[] parts = line.split("\\s+");
		String symbol = parts[0];
		String name = StringUtils.join(Arrays.asList(parts).subList(1, parts.length-1), " ");
		return new StockSymbol(symbol, name);
	}
}