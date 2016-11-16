package moderare.correlations2.expressions;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.Var;

@BuildParseTree
public class ExpressionFilter extends BaseParser<FormulaExpression> {

	public enum CONJUCTION {
		AND("AND");
		
		private final String text;
		
		private CONJUCTION(String text) {
			this.text = text;
		}
		
		@Override
		public String toString() {
			return text;
		}
		
		public static CONJUCTION fromString(String text) {
			if (text != null) {
				for (CONJUCTION c : CONJUCTION.values()) {
					if (c.text.toLowerCase().equals(text.toLowerCase())) {
						return c;
					}
				}
			}
			return null;
		}
	}
	
	public enum OPERATOR {
		EQUAL("="),
		NOT_EQUAL("!=");
		
		private final String text;
		
		private OPERATOR(String text) {
			this.text = text;
		}
		
		@Override
		public String toString() {
			return text;
		}
		
		public static OPERATOR fromString(String text) {
			if (text != null) {
				for (OPERATOR o : OPERATOR.values()) {
					if (o.text.equals(text)) {
						return o;
					}
				}
			}
			return null;
		}
	}
	
	public FormulaExpression parse(String formula) throws Exception {
		ExpressionFilter parser = Parboiled.createParser(ExpressionFilter.class);
		ParsingResult<FormulaExpression> result = new RecoveringParseRunner<FormulaExpression>(parser.Expression()).run(formula);
		if (!result.hasErrors()) {
			System.out.println(result.parseTreeRoot.getValue());
			return result.parseTreeRoot.getValue();
		}
		throw new Exception(result.parseErrors.get(0).getErrorMessage());
	}
	
	public Rule Expression() {
		 Var<String> op = new Var<String>();
		return Sequence(
				SingularExpression(),
				ZeroOrMore(Sequence(
						Conjunction(), op.set(match()),
						Expression(),
						swap() && push(new FormulaExpression(pop(), CONJUCTION.fromString(op.get().trim()), pop())))),
				EOI);
	}

	public Rule Conjunction() {
		return AND();
	}

	public Rule SingularExpression() {
		Var<String> op1 = new Var<String>();
		Var<String> op2 = new Var<String>();
		return Sequence(
				FieldName(), op1.set(match()),
				Operator(), op2.set(match()),
				Value(),
				push(new FormulaExpression(op1.get(), OPERATOR.fromString(op2.get().trim()), match())));
	}

	public Rule FieldName() {
		return Sequence(
				OneOrMore(FirstOf(Sequence(TestNot(AnyOf(" \\\n\r")), ANY),
						ECHAR())), WS());
	}

	public Rule Operator() {
		return FirstOf(EQUAL(), NOT_EQUAL());
	}

	public Rule Value() {
		return FirstOf(ValueString(), ValueNumber());
	}

	public Rule ValueString() {
		return FirstOf(STRING_LITERAL1(), STRING_LITERAL2());
	}

	public Rule ValueNumber() {
		return FirstOf(NumericLiteralUnsigned(), NumericLiteralPositive(),
				NumericLiteralNegative());
	}

	public Rule NumericLiteral() {
		return FirstOf(NumericLiteralUnsigned(), NumericLiteralPositive(),
				NumericLiteralNegative());
	}

	public Rule NumericLiteralUnsigned() {
		return FirstOf(DOUBLE(), DECIMAL(), INTEGER());
	}

	public Rule NumericLiteralPositive() {
		return FirstOf(DOUBLE_POSITIVE(), DECIMAL_POSITIVE(),
				INTEGER_POSITIVE());
	}

	public Rule NumericLiteralNegative() {
		return FirstOf(DOUBLE_NEGATIVE(), DECIMAL_NEGATIVE(),
				INTEGER_NEGATIVE());
	}

	public Rule INTEGER() {
		return Sequence(OneOrMore(DIGIT()), WS());
	}

	public Rule DECIMAL() {
		return Sequence(FirstOf( //
				Sequence(OneOrMore(DIGIT()), DOT(), ZeroOrMore(DIGIT())), //
				Sequence(DOT(), OneOrMore(DIGIT())) //
				), WS());
	}

	public Rule DOUBLE() {
		return Sequence(
				FirstOf(//
				Sequence(OneOrMore(DIGIT()), DOT(), ZeroOrMore(DIGIT())), //
						Sequence(DOT(), OneOrMore(DIGIT())), //
						OneOrMore(DIGIT())), WS());
	}

	public Rule INTEGER_POSITIVE() {
		return Sequence(PLUS(), INTEGER());
	}

	public Rule DECIMAL_POSITIVE() {
		return Sequence(PLUS(), DECIMAL());
	}

	public Rule DOUBLE_POSITIVE() {
		return Sequence(PLUS(), DOUBLE());
	}

	public Rule INTEGER_NEGATIVE() {
		return Sequence(MINUS(), INTEGER());
	}

	public Rule DECIMAL_NEGATIVE() {
		return Sequence(MINUS(), DECIMAL());
	}

	public Rule DOUBLE_NEGATIVE() {
		return Sequence(MINUS(), DOUBLE());
	}

	public Rule DOT() {
		return Ch('.');
	}

	public Rule DIGIT() {
		return CharRange('0', '9');
	}

	public Rule STRING_LITERAL1() {
		return Sequence(
				"'",
				ZeroOrMore(FirstOf(
						Sequence(TestNot(FirstOf("'", '\\', '\n', '\r')), ANY),
						ECHAR())), "'", WS());
	}

	public Rule STRING_LITERAL2() {
		return Sequence(
				'"',
				ZeroOrMore(FirstOf(Sequence(TestNot(AnyOf("\"\\\n\r")), ANY),
						ECHAR())), '"', WS());
	}

	public Rule ECHAR() {
		return Sequence('\\', AnyOf("tbnrf\\\"\'"));
	}

	public Rule EQUAL() {
		return StringIgnoreCaseWS(OPERATOR.EQUAL.toString());
	}

	public Rule NOT_EQUAL() {
		return StringIgnoreCaseWS(OPERATOR.NOT_EQUAL.toString());
	}

	public Rule AND() {
		return StringIgnoreCaseWS(CONJUCTION.AND.toString());
	}

	public Rule PLUS() {
		return ChWS('+');
	}

	public Rule MINUS() {
		return ChWS('-');
	}
	
	public Rule ChWS(char c) {
		return Sequence(Ch(c), WS());
	}

	public Rule StringWS(String s) {
		return Sequence(String(s), WS());
	}

	public Rule StringIgnoreCaseWS(String string) {
		return Sequence(IgnoreCase(string), WS());
	}
	
	public Rule WS() {
		return ZeroOrMore(AnyOf(" \t\f"));
	}
	
	public Rule EOL() {
		return AnyOf("\n\r");
	}
}
