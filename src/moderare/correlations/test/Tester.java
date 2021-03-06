package moderare.correlations.test;

import moderare.correlations.calculators.CorrelationsComputer;
import moderare.correlations.calculators.CorrelationsComputer.CORRELATION_TYPE;
import moderare.correlations.expressions.ExpressionFilter.OPERATOR;
import moderare.correlations.loader.CSVLoader;
import moderare.correlations.model.Dataset;
import moderare.correlations.model.Entry;

public class Tester {

	public static void main(String[] args) throws Exception {
		CSVLoader l = new CSVLoader();
		l.loadFile("C:\\Users\\Andrea\\Desktop\\behavioral-pragmatic.csv");
		Dataset d = l.exportDataset();

		System.out.println("---");
		System.out.println(d.size());
		System.out.println(d.filter("expertise = \"experts\" & No_homogeneous_fragments != 1").size());
		System.out.println(d.filter(OPERATOR.EQUAL, new Entry("expertise", "experts")).size());
		System.out.println(d.filter(OPERATOR.EQUAL, new Entry("expertise", "familiar")).size());
		System.out.println(d.filter(OPERATOR.EQUAL, new Entry("expertise", "a")).size());
		
		System.out.println("---");
		System.out.println(d.getDistinctValues("expertise"));
		System.out.println(d.getAllValues("expertise"));
		
		System.out.println("---");
		CorrelationsComputer cc = new CorrelationsComputer(d.filter(OPERATOR.EQUAL, new Entry("expertise", "unfamiliar")));
		System.out.println(cc.getCorrelation("percent_acts_in_mixed_fragments_aligned", "sum_handles_free_choice", CORRELATION_TYPE.PEARSONS));
		
		System.out.println("---");
		System.out.println(d.filter(OPERATOR.LESS_EQUAL, new Entry("no_Dead_transitions", 10d)).size());
		System.out.println(d.filter("no_Dead_transitions <= 10").size());
	}

}
