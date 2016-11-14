package moderare.correlations2.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Dataset extends LinkedList<Record> {

	private static final long serialVersionUID = 4678924868284946776L;
	private Set<String> columns = new HashSet<String>();
	
	public Dataset() {
		
	}
	
	public Dataset filterAny(Entry... toKeep) {
		Dataset d = new Dataset();
		for (Record r : this) {
			for (Entry e : toKeep) {
				if (r.contains(e)) {
					d.add(r);
					break;
				}
			}
		}
		return d;
	}
	
	public Dataset filterAll(Entry... toKeep) {
		Dataset d = new Dataset();
		for (Record r : this) {
			boolean allContained = true;
			for (Entry e : toKeep) {
				if (!r.contains(e)) {
					allContained = false;
				}
			}
			if (allContained) {
				d.add(r);
			}
		}
		return d;
	}
	
	public Dataset slice(String... attributeNames) {
		Dataset d = new Dataset();
		for (Record r : this) {
			Record newRecord = new Record();
			for (String attributeName : attributeNames) {
				newRecord.add(r.get(attributeName));
			}
			d.add(newRecord);
		}
		return d;
	}
	
	public Set<Entry> getDistinctValues(String attributeName) {
		HashSet<Object> setValues = new HashSet<Object>();
		HashSet<Entry> set = new HashSet<Entry>();
		for (Record r : this) {
			Entry e = r.get(attributeName);
			if (e != null && !setValues.contains(e.getValue())) {
				set.add(e);
				setValues.add(e.getValue());
			}
		}
		return set;
	}
	
	public List<Entry> getAllValues(String attributeName) {
		List<Entry> list = new LinkedList<Entry>();
		for (Record r : this) {
			Entry e = r.get(attributeName);
			if (e != null) {
				list.add(e);
			}
		}
		return list;
	}
	
	@Override
	public boolean add(Record r) {
		// add the record
		boolean toRet = super.add(r);
		
		// add the columns
		for (String newName : r.getNames()) {
			if (!columns.contains(newName)) {
				columns.add(newName);
			}
		}
		
		return toRet;
	}
}
