package stock.core;

import java.io.Closeable;

public interface CloseableSource<T> extends Iterable<T>, Closeable {
	CloseableIterator<T> iterator();
}
