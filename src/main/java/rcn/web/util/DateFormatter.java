package rcn.web.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.format.Formatter;

public class DateFormatter implements Formatter<Date> {
	public DateFormatter() {
		super();
	}

	@Override
	public String print(Date object, Locale locale) {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		return dateFormat.format(object);
	}

	@Override
	public Date parse(String text, Locale locale) throws ParseException {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.parse(text);
	}

}