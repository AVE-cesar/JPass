package jpass.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import jpass.Consts;

public class DateAdapter extends XmlAdapter<String, Date> {
	public Date unmarshal(String value) {
		try {
			return new SimpleDateFormat(Consts.DATE_FORMAT).parse(value);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public String marshal(Date value) {
		return new SimpleDateFormat(Consts.DATE_FORMAT).format(value);
	}
}
