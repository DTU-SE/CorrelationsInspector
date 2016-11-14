package moderare.correlations2.model;

import java.util.HashSet;
import java.util.Set;

public class Record extends HashSet<Entry> {

	private static final long serialVersionUID = -5078987577221880240L;

	public void addValue(String name, String value) {
		add(new Entry(name, value));
	}
	
	public void addValue(String name, Double value) {
		add(new Entry(name, value));
	}
	
	public Set<String> getNames() {
		Set<String> names = new HashSet<String>();
		for (Entry v : this) {
			names.add(v.getName());
		}
		return names;
	}
	
	public Entry get(String entryName) {
		for (Entry e : this) {
			if (e.getName().equals(entryName)) {
				return e;
			}
		}
		return null;
	}
	
	@Override
	public boolean contains(Object entry) {
		if (entry instanceof Entry) {
			String name = ((Entry) entry).getName();
			Object value = ((Entry) entry).getValue();
			
			for (Entry e : this) {
				String entryName = e.getName();
				Object entryValue = e.getValue();
				
				boolean sameName = name == null ? entryName == null : name.equals(entryName);
				boolean sameValue = value == null ? entryValue == null : value.equals(entryValue);
				if (sameName && sameValue) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		String toReturn = "[";
		for(Entry v: this) {
			toReturn += v.toString() + ", ";
		}
		toReturn += "]";
		
		return toReturn;
	}
}
