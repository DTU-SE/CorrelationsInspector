package moderare.correlations2.model;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Entry {

	enum TYPE {
		STRING,
		NUMERIC,
	}

	private TYPE type;
	private String name;
	private Object value;
	
	public Entry(String name, String value) {
		this(TYPE.STRING, name, value);
	}
	
	public Entry(String name, Double value) {
		this(TYPE.NUMERIC, name, value);
	}
	
	private Entry(TYPE type, String name, Object value) {
		this.type = type;
		this.name = name;
		this.value = value;
	}
	
	public TYPE getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public Object getValue() {
		return value;
	}
	
	public String getValueString() {
		if (type == TYPE.STRING) {
			return (String) value;
		}
		return null;
	}
	
	public Double getValueNumeric() {
		if (type == TYPE.NUMERIC) {
			return (Double) value;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "(" + type + ", " + name + " = '" + value + "')";
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name).append(value).toHashCode();
	}
}
