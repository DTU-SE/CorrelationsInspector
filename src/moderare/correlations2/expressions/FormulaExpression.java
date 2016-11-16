package moderare.correlations2.expressions;

import moderare.correlations.utils.TypeDetector;
import moderare.correlations.utils.TypeDetector.FIELD_TYPE;
import moderare.correlations2.expressions.ExpressionFilter.CONJUCTION;
import moderare.correlations2.expressions.ExpressionFilter.OPERATOR;
import moderare.correlations2.model.Entry;

import org.parboiled.trees.ImmutableBinaryTreeNode;

public class FormulaExpression extends ImmutableBinaryTreeNode<FormulaExpression> {

	CONJUCTION conjunction = null;
	String expressionVariableName = null;
	OPERATOR expressionOperator = null;
	String expressionValue = null;
	
	public FormulaExpression(FormulaExpression left, CONJUCTION conjunction, FormulaExpression right) {
		super(left, right);
		this.conjunction = conjunction;
	}
	
	public FormulaExpression(String variableName, OPERATOR operator, String value) {
		super(null, null);
		this.expressionVariableName = variableName.trim();
		this.expressionOperator = operator;
		this.expressionValue = value.trim();
		if ((expressionValue.startsWith("\"") && expressionValue.endsWith("\"")) ||
				((expressionValue.startsWith("'") && expressionValue.endsWith("'")))) {
			expressionValue = expressionValue.substring(1, expressionValue.length() - 1);
		}
	}
	
	public boolean isConjuction() {
		return (conjunction != null);
	}
	
	public CONJUCTION getConjuction() {
		if (isConjuction()) {
			return conjunction;
		}
		return null;
	}
	
	public boolean isComparison() {
		return !isConjuction();
	}
	
	public OPERATOR getOperator() {
		if (isComparison()) {
			return expressionOperator;
		}
		return null;
	}
	
	public Entry getComparison() {
		if (isComparison()) {
			Entry e = null;
			FIELD_TYPE type = TypeDetector.detectType(expressionValue);
			if (type == FIELD_TYPE.STRING) {
				e = new Entry(expressionVariableName, expressionValue);
			} else if (type == FIELD_TYPE.DOUBLE) {
				e = new Entry(expressionVariableName, Double.parseDouble(expressionValue));
			}
			return e;
		}
		return null;
	}
	
	@Override
	public String toString() {
		if (isComparison()) {
			return expressionVariableName + " " + expressionOperator + " " + expressionValue;
		} else {
			return "(" + left().toString() + ") " + conjunction + " (" + right().toString() + ")";
		}
	}
}