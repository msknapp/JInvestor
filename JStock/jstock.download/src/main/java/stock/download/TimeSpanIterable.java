package stock.download;

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

import java.util.Calendar;
import java.util.Iterator;

import org.apache.commons.lang3.time.DateUtils;

import stock.core.TimeSpan;

public class TimeSpanIterable implements Iterable<TimeSpan> {
	private final TimeSpan timeSpan;
	private final int interval;

	public TimeSpanIterable(TimeSpan timeSpan, int interval) {
		this.timeSpan = timeSpan;
		this.interval = interval;
	}

	@Override
	public Iterator<TimeSpan> iterator() {
		return new TimeSpanIterator(timeSpan, interval);
	}

	private static final class TimeSpanIterator implements Iterator<TimeSpan> {
		private final TimeSpan timeSpan;
		private final int interval;
		private int currentIndex;

		private TimeSpanIterator(TimeSpan timeSpan, int interval) {
			this.timeSpan = timeSpan;
			this.interval = interval;
		}

		@Override
		public boolean hasNext() {
			final Calendar start = DateUtils.toCalendar(DateUtils.addDays(timeSpan
					.getStart().getTime(), currentIndex * interval));
			final Calendar now = Calendar.getInstance();
			return now.after(start);
		}

		@Override
		public TimeSpan next() {
			final Calendar start = DateUtils.toCalendar(DateUtils.addDays(timeSpan
					.getStart().getTime(), currentIndex * interval));
			final Calendar end = DateUtils.toCalendar(DateUtils.addDays(timeSpan
					.getStart().getTime(), ((currentIndex + 1) * interval)));
			
			final TimeSpan ts = new TimeSpan(start, end);
			currentIndex++;
			return ts;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("cannot remove.");
		}
	}
}