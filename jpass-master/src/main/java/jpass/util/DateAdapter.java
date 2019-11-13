package jpass.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date> {
	public Date unmarshal(String value) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public String marshal(Date value) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
	}
}
