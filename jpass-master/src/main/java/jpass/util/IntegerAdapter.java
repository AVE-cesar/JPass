package jpass.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class IntegerAdapter extends XmlAdapter<String, Integer> {

	@Override
	public Integer unmarshal(String value) throws Exception {
		return Integer.valueOf(value);
	}

	@Override
	public String marshal(Integer value) throws Exception {
		return value.toString();		
	}

}
