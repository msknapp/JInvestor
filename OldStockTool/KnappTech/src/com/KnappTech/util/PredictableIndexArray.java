package com.KnappTech.util;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

/**
 * This is an array collection that is designed to be very fast.  It is meant
 * to be used when you have an array full of unique items, there is an 
 * easy way to map them to an index, and you need them to be iterated over in
 * a predictable order.  The main difference between this and a hashtable is
 * that this gives you more control over the order that your objects will
 * be iterated over.  At the same time, you enjoy the performance benefits
 * associated with a hash table, and the predictable order associated with
 * a list.
 * This array collection will not allow null entries, nor duplicate entries.
 * It does not support multi-threading.  There is no check for concurrent 
 * modification, the coder should be sure that this is not used by multiple
 * threads, or use Collections.synchronizedSet.  
 * Using this class depends on the developer writing a good IndexDeterminer.
 * The assumption is that if a.equals(b) then the index determiner will
 * produce the same index for a and b.  There is also the assumption that
 * if !a.equals(b) then the index determiner will produce different indexes
 * for a and b.  You must also be careful that the capacity provided to 
 * this collection is equal to or greater than the highest possible index 
 * produced by your index creater.
 * This class can come in very handy for objects that are associated with
 * a date or time.  Have the index creater produce an index based on the date.  
 * 
 * @author Michael Knapp
 * @param <TYPE>
 */
public class PredictableIndexArray<TYPE> extends AbstractSet<TYPE> implements Serializable {
	private static final long serialVersionUID = 201103051446L;
	private Object[] items;
	private int size=0;
	private final IndexDeterminer indexDeterminer;
	private transient Integer[] indexes=null;
	
	public PredictableIndexArray(int capacity,IndexDeterminer indexDeterminer) {
		if (capacity<1)
			throw new IllegalArgumentException("Must allow at least one item in this array.");
		if (indexDeterminer==null) 
			throw new IllegalArgumentException("Must define an index determiner.");
		this.indexDeterminer = indexDeterminer;
		items = new Object[capacity];
	}
	
	public PredictableIndexArray(PredictableIndexArray<TYPE> other) {
		this.indexDeterminer = other.getIndexDeterminer();
		items = new Object[other.capacity()];
		for (Object o : other.items) {
			if (o!=null)
				add((TYPE)o);
		}
	}
	
	public PredictableIndexArray<TYPE> clone() {
		return new PredictableIndexArray<TYPE>(this);
	}
	
	@Override
	public final boolean addAll(Collection<? extends TYPE> moreItems) {
		if (wasIndexed())
			throw new IllegalStateException("Cannot add items after this was indexed.");
		int index;
		boolean modified=false;
		for (TYPE item : moreItems) {
			if (item!=null) {
				index = indexDeterminer.determineIndex(item);
				if (0<=index && index<items.length) {
					if (items[index]==null){
						items[index]=item;
						size++;
						modified=true;
					}
				}
			}
		}
		return modified;
	}
	
	public final boolean addAll(TYPE[] moreItems) {
		if (wasIndexed())
			throw new IllegalStateException("Cannot add items after this was indexed.");
		int index;
		boolean modified=false;
		for (TYPE item : moreItems) {
			if (item!=null) {
				index = indexDeterminer.determineIndex(item);
				if (0<=index && index<items.length) {
					if (items[index]==null) {
						items[index]=item;
						size++;
						modified=true;
					}
				}
			}
		}
		return modified;
	}

	@Override
	public final boolean add(final TYPE item) {
		if (item==null) 
			throw new NullPointerException();
		if (wasIndexed())
			throw new IllegalStateException("Cannot add items after this was indexed.");
		int index = indexDeterminer.determineIndex(item);
		if (0<=index && index<items.length && items[index]==null) {
			items[index]=item;
			size++;
			return true;
		}
		return false;
	}
	
	public final boolean replace(final TYPE item) {
		if (item==null) 
			throw new NullPointerException();
		if (wasIndexed())
			throw new IllegalStateException("Cannot add or replace items after this was indexed.");
		int index = indexDeterminer.determineIndex(item);
		if (0<=index && index<items.length) {
			if (items[index]==null)
				size++;
			items[index]=item;
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public final TYPE getFromInternalIndex(int index) {
		if (index<0 || items.length<=index)
			throw new IllegalArgumentException("The index requested is out of bounds: "+index);
		return (TYPE) items[index];
	}
	
	public TYPE get(Object o) {
		return getFromInternalIndex(indexDeterminer.determineIndex(o));
	}
	
	public IndexDeterminer getIndexDeterminer() {
		return indexDeterminer;
	}
	
	public boolean removeFromInternalIndex(int index) {
		if (index<0 || items.length<=index)
			throw new IllegalArgumentException("The index requested is out of bounds: "+index);
		if (items[index]==null) {
			return false;
		} else {
			items[index]=null;
			size--;
			return true;
		}
	}
	
	public boolean remove(int index) {
		if (index<0 || size<=index)
			throw new IllegalArgumentException("The index requested is out of bounds: "+index);
		if (wasIndexed())
			throw new IllegalStateException("Cannot remove items after this was indexed.");
		TYPE t;
		int i = 0;
		Iterator<TYPE> iter = iterator();
		while(iter.hasNext()) {
			t=iter.next();
			if (i==index) {
				iter.remove();
				return true;
			}
			i++;
		}
		return false;
	}
	
	@Override
    public boolean remove(Object o) {
    	return removeFromInternalIndex(indexDeterminer.determineIndex(o));
    }
	
    public boolean removeAll(Collection<?> c) {
    	boolean modified = false;
    	int index;
    	Object k;
    	for (Object o : c) {
    		index=indexDeterminer.determineIndex(o);
    		if (0<=index && index<items.length) {
    			k=items[index];
    			if (k!=null) {
    				items[index]=null;
    				modified=true;
    			}	
    		}
    	}
    	return modified;
    }

	@Override
	public Iterator<TYPE> iterator() {
		return new PIIterator(true);
	}
	public Iterator<TYPE> iterator(boolean forward) {
		return new PIIterator(forward);
	}
	public Iterator<TYPE> iterator(int startIndex,boolean forward) {
		return new PIIterator(startIndex,true);
	}

	@Override
	public int size() {
		return size;
	}
	
	public int capacity() {
		return items.length;
	}
	
    public boolean contains(Object o) {
    	int index = indexDeterminer.determineIndex(o);
    	if (index<0 || index>items.length)
    		return false;
    	return (items[index]!=null);
    }
    
    public TYPE getFirst() {
    	if (wasIndexed())
    		return getItem(0,false);
    	return getItemOnOrAfterInternalIndex(0);
    }
    
    public TYPE getLast() {
    	if (wasIndexed())
    		return getItem(size-1,false);
    	return getItemBeforeInternalIndex(capacity());
    }
    
    public TYPE getItemAfterInternalIndex(int internalIndex) {
    	return getItemOnOrAfterInternalIndex(internalIndex+1);
    }
    
    public TYPE getItemBeforeInternalIndex(int internalIndex) {
    	return getItemOnOrBeforeInternalIndex(internalIndex-1);
    }
    
    public TYPE getItemOnOrAfterInternalIndex(int internalIndex) {
    	validateInternalIndex(internalIndex);
    	int i = internalIndex;
    	TYPE t=null;
    	while (t==null && i<items.length) {
    		t=(TYPE) items[i];
    		if (t!=null) {
    			return t;
    		}
    		i++;
    	}
    	return null;
    }
    
    public TYPE getItemOnOrBeforeInternalIndex(int internalIndex) {
    	validateInternalIndex(internalIndex);
    	int i = internalIndex;
    	TYPE t=null;
    	while (t==null && i>=0) {
    		t=(TYPE) items[i];
    		if (t!=null) {
    			return t;
    		}
    		i--;
    	}
    	return null;
    }
    
    public TYPE getItemAfter(int index) {
//    	if (index+1<0 || size<=index+1)
//    		throw new IndexOutOfBoundsException();
		return getItem(index+1, false);
    }
    
    public TYPE getItemBefore(int index) {
//    	if (index-1<0 || size<=index-1)
//    		throw new IndexOutOfBoundsException();
		return getItem(index-1, false);
    }
    
    public TYPE getItemAfter(Object o) {
    	return getItemAfterInternalIndex(this.indexDeterminer.determineIndex(o));
    }
    
    public TYPE getItemBefore(Object o) {
    	return getItemBeforeInternalIndex(this.indexDeterminer.determineIndex(o));
    }
    
    public TYPE getItemOnOrAfter(Object o) {
    	return getItemOnOrAfterInternalIndex(this.indexDeterminer.determineIndex(o));
    }
    
    public TYPE getItemOnOrBefore(Object o) {
    	return getItemOnOrBeforeInternalIndex(this.indexDeterminer.determineIndex(o));
    }
    
    private void validateInternalIndex(int index) {
    	if (!checkInternalIndex(index)) 
    		throw new IndexOutOfBoundsException();
    }
    
    private boolean checkInternalIndex(int index) {
    	return (0<=index && index<items.length);
    }
    
    public void indexArray() {
//    	System.out.println("Indexing the array.");
//    	Thread.dumpStack();
    	this.indexes=new Integer[size];
    	Iterator<TYPE> iter = iterator();
    	int i = 0;
    	TYPE t;
    	while (iter.hasNext()) {
    		t = iter.next();
    		indexes[i]=indexDeterminer.determineIndex(t);
    		i++;
    	}
    }
    
    public void indexIfNecessary() {
    	if (!wasIndexed()) {
    		indexArray();
    	}
    }
    
    public TYPE getItem(int trueIndex) {
    	return getItem(trueIndex,false);
    }
    
    public TYPE getItem(int trueIndex,boolean allowIndexing) {
    	if (0<=trueIndex && trueIndex<size) {
    		if (!wasIndexed() && allowIndexing) {
    			indexArray();
    			return (TYPE) items[indexes[trueIndex]];
    		} else {
    			int i = 0;
    			Iterator iter = iterator();
    			TYPE t;
    			while (iter.hasNext()) {
    				t=(TYPE) iter.next();
    				if (i==trueIndex)
    					return t;
    				i++;
    			}
    			return null;
    		}
    	} else {
    		throw new IndexOutOfBoundsException("Requested index "+trueIndex+", size is "+size);
    	}
    }
    
    public boolean wasIndexed() {
    	return indexes!=null;
    }
    
    @Override
    public void clear() {
    	if (wasIndexed())
    		throw new IllegalStateException("Cannot get derivative after indexing.");
    	for (int i = 0;i<items.length;i++) {
    		items[i]=null;
    	}
    	size=0;
    }
	
	private class PIIterator implements Iterator<TYPE> {
		private int index = -1;
		private Object next;
		private boolean forward;

		public PIIterator(int startIndex,boolean forward) {
			index=startIndex;
			this.forward=forward;
			if (validIndex()) 
				next=items[index];
			else {
				if (index<0) {
					if (forward) 
						index=-1;
					else 
						index=0;
				} else if (index>items.length-1) {
					if (forward)
						index=items.length-1;
					else 
						index=items.length;
				}
			}
			if (next==null) {
				if (forward) {
					advance();
				} else {
					regress();
				}
			}
		}
		
		public PIIterator(boolean forward) {
			this((forward ? -1 : items.length),forward);
		}

		@Override
		public boolean hasNext() {
			return next!=null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public TYPE next() {
			TYPE current=(TYPE)next;
			next=null;
			if (forward) {
				advance();
			} else {
				regress();
			}
			return current;
		}
		
		private void advance() {
			while (next==null && index<items.length) {
				index++;
				if (validIndex()) 
					next= items[index];
			}
		}
		
		private void regress() {
			while (next==null && index>0) {
				index--;
				if (validIndex()) 
					next= items[index];
			}
		}
		
		private boolean validIndex() {
			return 0<=index && index<items.length;
		}

		@Override
		public void remove() {
			if (validIndex())
				items[index]=null;
		}
		
		public int getIndex() {
			return index;
		}
	}
}