package moderare.correlations2.expressions;

import org.parboiled.trees.ImmutableBinaryTreeNode;

public class FormulaExpression extends ImmutableBinaryTreeNode<FormulaExpression> {

	String conjunction = null;
	String expressionVariableName = null;
	String expressionOperator = null;
	String expressionValue = null;
	
	public FormulaExpression(FormulaExpression left, String conjunction, FormulaExpression right) {
		super(left, right);
		this.conjunction = conjunction.trim();
	}
	
	public FormulaExpression(String variableName, String operator, String value) {
		super(null, null);
		this.expressionVariableName = variableName.trim();
		this.expressionOperator = operator.trim();
		this.expressionValue = value.trim();
	}
	
	@Override
	public String toString() {
		if (conjunction == null) {
			return expressionVariableName + " " + expressionOperator + " " + expressionValue;
		} else {
			return "(" + left().toString() + ") " + conjunction + " (" + right().toString() + ")";
		}
	}
}