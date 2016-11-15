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

	public Rule Expression() {
		 Var<String> op = new Var<String>();
		return Sequence(
				SingularExpression(),
				ZeroOrMore(Sequence(
						Conjunction(), op.set(match()),
						Expression(),
						swap() && push(new FormulaExpression(pop(), op.get(), pop())))),
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
				push(new FormulaExpression(op1.get(), op2.get(), match())));
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
		return ChWS('=');
	}

	public Rule NOT_EQUAL() {
		return StringIgnoreCaseWS("!=");
	}

	public Rule AND() {
		return StringIgnoreCaseWS("AND");
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

	public static void main(String[] args) {
		
		String input = "test =1 and prova != \"wqrefwerf\" ";
		ExpressionFilter parser = Parboiled.createParser(ExpressionFilter.class);
		ParsingResult<FormulaExpression> result = new RecoveringParseRunner<FormulaExpression>(parser.Expression()).run(input);

		if (!result.hasErrors()) {
			System.out.println(result.parseTreeRoot.getValue());
		}
	}
}
