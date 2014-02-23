package com.KnappTech.model;

import java.util.Calendar;

public class TimeFrame {
	
	private final LiteDate startDate;
	private final LiteDate endDate;
	
	/**
	 * Default constructor spans the current month
	 */
	public TimeFrame() {
		Calendar sc = Calendar.getInstance();
		sc.set(Calendar.DAY_OF_MONTH, 1);
		startDate = LiteDate.getOrCreate(sc);
		sc.add(Calendar.MONTH, 1);
		endDate = LiteDate.getOrCreate(sc);
	}
	
	/**
	 * Builds a time frame with the start and end date specified.
	 * @param startDate
	 * @param endDate
	 */
	public TimeFrame(LiteDate startDate,LiteDate endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public LiteDate getEndDate() {
		return endDate;
	}

	public LiteDate getStartDate() {
		return startDate;
	}
	
	public boolean includes(LiteDate date) {
		if (date!=null) {
			return date.onOrAfter(startDate) && date.before(endDate);
		}
		return false;
	}
}