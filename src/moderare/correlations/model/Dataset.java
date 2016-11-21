package moderare.correlations.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import moderare.correlations.expressions.ExpressionFilter;
import moderare.correlations.expressions.ExpressionFilter.OPERATOR;
import moderare.correlations.expressions.FormulaExpression;
import moderare.correlations.model.Entry.TYPE;

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
			return dataset.filter(fe.getOperator(), fe.getComparison());
		}
	}
	
	/**
	 * Keeps only the items fulfilling the opeartor + comparison expression
	 * 
	 * @param operator
	 * @param comparison
	 * @return
	 */
	public Dataset filter(OPERATOR operator, Entry comparison) {
		Dataset d = new Dataset();
		for (Record r : this) {
			if (toKeep(operator, r.get(comparison.getName()), comparison)) {
				d.add(r);
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
	
	public Set<String> getAttributes() {
		return columns;
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
	
	private boolean toKeep(OPERATOR operator, Entry iterator, Entry reference) {
		if (operator == OPERATOR.EQUAL) {
			if (iterator == null && reference == null) {
				return true;
			} else {
				Object value = iterator.getValue();
				return value == null ? reference.getValue() == null : value.equals(reference.getValue());
			}
		} else if (operator == OPERATOR.NOT_EQUAL) {
			if ((iterator == null && reference != null) || (iterator != null && reference == null)) {
				return true;
			}
			Object value = iterator.getValue();
			return value == null ? reference.getValue() != null : !value.equals(reference.getValue());
		} else if (operator == OPERATOR.GREATER) {
			if (iterator == null || reference == null) {
				return false;
			} else {
				if (iterator.getType() == TYPE.NUMERIC && reference.getType() == TYPE.NUMERIC &&
						iterator.getValue() != null && reference.getValue() != null) {
					return iterator.getValueNumeric() > reference.getValueNumeric();
				} else {
					return false;
				}
			}
		} else if (operator == OPERATOR.GREATER_EQUAL) {
			if (iterator == null || reference == null) {
				return false;
			} else {
				if (iterator.getType() == TYPE.NUMERIC && reference.getType() == TYPE.NUMERIC &&
						iterator.getValue() != null && reference.getValue() != null) {
					return iterator.getValueNumeric() >= reference.getValueNumeric();
				} else {
					return false;
				}
			}
		} else if (operator == OPERATOR.LESS) {
			if (iterator == null || reference == null) {
				return false;
			} else {
				if (iterator.getType() == TYPE.NUMERIC && reference.getType() == TYPE.NUMERIC &&
						iterator.getValue() != null && reference.getValue() != null) {
					return iterator.getValueNumeric() < reference.getValueNumeric();
				} else {
					return false;
				}
			}
		} else if (operator == OPERATOR.LESS_EQUAL) {
			if (iterator == null || reference == null) {
				return false;
			} else {
				if (iterator.getType() == TYPE.NUMERIC && reference.getType() == TYPE.NUMERIC &&
						iterator.getValue() != null && reference.getValue() != null) {
					return iterator.getValueNumeric() <= reference.getValueNumeric();
				} else {
					return false;
				}
			}
		}
		
		return false;
	}
}
