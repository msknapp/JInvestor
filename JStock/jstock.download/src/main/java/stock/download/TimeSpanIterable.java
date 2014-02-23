package stock.download;

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