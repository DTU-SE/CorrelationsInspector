package moderare.correlations.utils;

import com.google.common.base.CharMatcher;

public class TypeDetector {
	
	public enum FIELD_TYPE {
		DOUBLE,
		STRING
	}
	
	public static FIELD_TYPE detectType(String value) {
		value = CharMatcher.whitespace().trimFrom(value);
		try {
			Integer.parseInt(value);
			return FIELD_TYPE.DOUBLE;
		} catch(NumberFormatException e) {}
		try {
			Double.parseDouble(value);
			return FIELD_TYPE.DOUBLE;
		} catch(NumberFormatException e) {}
		return FIELD_TYPE.STRING;
	}
}
