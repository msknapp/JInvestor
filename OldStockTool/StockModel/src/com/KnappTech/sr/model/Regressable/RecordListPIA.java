package com.KnappTech.sr.model.Regressable;

import java.util.Calendar;

import com.KnappTech.model.LiteDate;
import com.KnappTech.util.IndexDeterminer;
import com.KnappTech.util.PredictableIndexArray;

public class RecordListPIA<E> extends PredictableIndexArray<E> {
	private static final long serialVersionUID = 201103051447L;
	private static final int sz = determineSize();
	private transient static final IndexDeterminer indt = makeIndexDeterminer();
	
	public RecordListPIA() {
		super(sz,indt);
	}

	private static IndexDeterminer makeIndexDeterminer() {
		return new IDTRecordYear(1988);
	}

	private static int determineSize() {
		int base = LiteDate.determineHashCode(1, Calendar.JANUARY, 1988);
		return LiteDate.getOrCreate().hashCode()+5-base;
	}

	private static final class IDTRecordYear implements IndexDeterminer {
		private final int baseHC;
		
		public IDTRecordYear(int year) {
			if (year<LiteDate.BASEYEAR)
				throw new IllegalArgumentException("Year is too far in past.");
			this.baseHC = LiteDate.determineHashCode(1, 0, year);
		}

		@Override
		public int determineIndex(Object o) {
			if (o instanceof Record)
				return ((Record)o).getDate().hashCode()-baseHC;
			else if (o instanceof LiteDate) 
				return ((LiteDate)o).hashCode()-baseHC;
			else 
				return o.hashCode()-baseHC;
		}
	}
}
