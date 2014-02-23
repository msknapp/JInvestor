package com.KnappTech.sr.model;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.Regressable.Record;
import com.KnappTech.util.IndexDeterminer;

public class IndexDeterminerHelper {
	private static final IndexDeterminer idt1988 = createIDT1988();
	private static final int idt1988MaxCapacity = estimateMaxCapacityIDT1988();

	private static IndexDeterminer createIDT1988() {
		return new IDTRecordYear(1988);
	}
	
	private static int estimateMaxCapacityIDT1988() {
		int base = LiteDate.determineHashCode(1, 0, 1988);
		return LiteDate.getOrCreate().hashCode()+5-base;
	}
	
	public static final IndexDeterminer getIDT1988() {
		return idt1988;
	}
	
	public static final int getIDT1988Capacity() {
		return idt1988MaxCapacity;
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
