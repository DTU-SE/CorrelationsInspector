package moderare.correlations2.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import moderare.correlations2.expressions.ExpressionFilter;
import moderare.correlations2.expressions.FormulaExpression;
import moderare.correlations2.expressions.ExpressionFilter.OPERATOR;

public class Dataset extends LinkedList<Record> {

	private static final long serialVersionUID = 4678924868284946776L;
	private Set<String> columns = new HashSet<String>();
	
	public Dataset() {
		
	}
	
	/**
	 * 
	 * @see ExpressionFilter
	 * @param formula
	 * @return
	 * @throws Exception 
	 */
	public Dataset filter(String formula) throws Exception {
		ExpressionFilter ef = new ExpressionFilter();
		return filter(ef.parse(formula), this);
	}
	
	private Dataset filter(FormulaExpression fe, Dataset dataset) {
		if (fe.isConjuction()) {
			Dataset left = filter(fe.left(), dataset);
			Dataset right = filter(fe.right(), left);
			return right;
		} else {
			OPERATOR op = fe.getOperator();
			if (op == OPERATOR.EQUAL) {
				return dataset.keepEqual(fe.getComparison());
			} else if (op == OPERATOR.NOT_EQUAL) {
				return dataset.keepDistinct(fe.getComparison());
			}
		}
		return dataset;
	}
	
	/**
	 * Keeps only the items with a value, for the given variable name, equal to the value of the provided parameter.
	 * 
	 * @param comparison
	 * @return
	 */
	public Dataset keepEqual(Entry comparison) {
		Dataset d = new Dataset();
		for (Record r : this) {
			Entry e = r.get(comparison.getName());
			if (e != null) {
				Object value = e.getValue();
				boolean sameValue = value == null ? comparison.getValue() == null : value.equals(comparison.getValue());
				if (sameValue) {
					d.add(r);
				}
			}
		}
		return d;
	}

	/**
	 * Keeps only the items with a value, for the given variable, different to the value of the provided parameter.
	 * 
	 * @param comparison
	 * @return
	 */
	public Dataset keepDistinct(Entry comparison) {
		Dataset d = new Dataset();
		for (Record r : this) {
			Entry e = r.get(comparison.getName());
			boolean bothNull = e == null ? comparison == null : false;
			if (!bothNull) {
				if (e == null) {
					d.add(r);
				} else {
					Object value = e.getValue();
					boolean sameValue = value == null ? comparison.getValue() == null : value.equals(comparison.getValue());
					if (!sameValue) {
						d.add(r);
					}
				}
			}
		}
		return d;
	}
	
	/*public Dataset filterAny(Entry... toKeep) {
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
	}*/
	
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
