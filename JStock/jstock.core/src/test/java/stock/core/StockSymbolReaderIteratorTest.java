package stock.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.Assert;
import org.junit.Test;

public class StockSymbolReaderIteratorTest {

	
	@Test
	public void read() throws FileNotFoundException {
		File file = new File("src/test/resources/stock/core/sample_symbols.txt");
		FileReader reader = new FileReader(file);
		StockSymbolReaderIterator s = new StockSymbolReaderIterator(reader,true);
		StockSymbol smbl = s.next();
		Assert.assertEquals("ADDA",smbl.getSymbol());
		smbl = s.next();
		Assert.assertEquals("ADDD",smbl.getSymbol());
	}
}
