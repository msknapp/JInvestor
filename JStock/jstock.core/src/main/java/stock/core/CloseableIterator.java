package stock.core;

import java.io.Closeable;
import java.util.Iterator;

public interface CloseableIterator<T> extends Closeable, Iterator<T> {

}
