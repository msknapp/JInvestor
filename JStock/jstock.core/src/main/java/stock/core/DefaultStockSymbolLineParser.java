package stock.core;

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