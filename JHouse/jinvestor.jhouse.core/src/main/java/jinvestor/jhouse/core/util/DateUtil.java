package jinvestor.jhouse.core.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

public final class DateUtil {
	private static final Logger logger = Logger.getLogger(DateUtil.class);
	
	private DateUtil(){}
	
	public static Date parseToDayOfMonth(String value) {
		Date d = quietlyParse(value, "yyyyMMdd","yyyy-MM-dd","yyyy/MM/dd","MM/dd/yy","MM-dd-yy");
		d = DateUtils.truncate(d, Calendar.DAY_OF_MONTH);
		return d;
	}
	
	public static Date quietlyParse(String value,String ...format ) {
		Date d = null;
		try {
			d = DateUtils.parseDate(value, format);
		} catch (ParseException e) {
			logger.error(e);
		}
		return d;
	}
	
	public static long millisFromDayOfMonth(String value) {
		return parseToDayOfMonth(value).getTime();
	}
}
