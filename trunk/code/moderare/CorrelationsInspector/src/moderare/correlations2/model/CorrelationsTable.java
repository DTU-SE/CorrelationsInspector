package moderare.correlations2.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import moderare.correlations.model.Correlation;
import moderare.correlations.utils.Pair;

public class CorrelationsTable {

	private HashMap<Pair<String, String>, Correlation> table;
	private List<String> set1;
	private List<String> set2;
	
	public CorrelationsTable() {
		this.table = new HashMap<Pair<String, String>, Correlation>();
		this.set1 = new LinkedList<String>();
		this.set2 = new LinkedList<String>();
	}
	
	public void add(String attribute1, String attribute2, Correlation correlation) {
		table.put(new Pair<String, String>(attribute1, attribute2), correlation);
		table.put(new Pair<String, String>(attribute2, attribute1), correlation);
		
		set1.add(attribute1);
		set2.add(attribute2);
	}
	
	public Correlation get(String attribute1, String attribute2) {
		return table.get(new Pair<String, String>(attribute1, attribute2));
	}
}
