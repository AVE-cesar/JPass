package jpass.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jpass.Consts;

public class DateUtils {

	public static String dateToString(Date value) {
		if (value == null) {
			return "";
		}
		return new SimpleDateFormat(Consts.DATE_FORMAT).format(value);
	}

	public static Date stringToDate(String value) {
		try {
			return new SimpleDateFormat(Consts.DATE_FORMAT).parse(value);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
