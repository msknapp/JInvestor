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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

public class TimeSpan {
	private final Calendar start;
	private final Calendar end;

	public TimeSpan(final String start, final String end) throws ParseException {
		this(DateUtils.toCalendar(DateUtils.parseDate(start,"yyyy-MM-dd")),
				DateUtils.toCalendar(DateUtils.parseDate(end,"yyyy-MM-dd")));
	}

	public TimeSpan(final String start) throws ParseException {
		this(DateUtils.toCalendar(DateUtils.parseDate(start,"yyyy-MM-dd")));
	}

	public TimeSpan(final Calendar start, final Calendar end) {
		this.start = DateUtils.truncate(start, Calendar.DAY_OF_MONTH);
		this.end = DateUtils.truncate(end, Calendar.DAY_OF_MONTH);
		if (this.end.before(this.start)) {
			throw new IllegalArgumentException(
					"start time must be before end time.");
		}
	}

	public TimeSpan(final Calendar start) {
		this.start = DateUtils.truncate(start, Calendar.DAY_OF_MONTH);
		this.end = DateUtils.toCalendar(DateUtils.truncate(
				DateUtils.addDays(new Date(), 1), Calendar.DAY_OF_MONTH));
		if (this.end.before(this.start)) {
			throw new IllegalArgumentException(
					"start time must be before end time.");
		}
	}

	public Calendar getStart() {
		return start;
	}

	public Calendar getEnd() {
		return end;
	}
	
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String s = sdf.format(start.getTime())+" to "+sdf.format(end.getTime());
		return s;
	}

	public static Calendar getStart(TimeSpan ts) {
		return ts == null ? null : ts.getStart();
	}
	
	public static Calendar getEnd(TimeSpan ts) {
		return ts == null ? null : ts.getEnd();
	}
}