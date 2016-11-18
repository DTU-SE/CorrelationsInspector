package moderare.correlations2.test;

import moderare.correlations2.calculators.CorrelationsComputer;
import moderare.correlations2.loader.CSVLoader;
import moderare.correlations2.model.Dataset;
import moderare.correlations2.model.Entry;

public class Tester {

	public static void main(String[] args) throws Exception {
		CSVLoader l = new CSVLoader();
		l.loadFile("C:\\Users\\Andrea\\Desktop\\behavior-pragmatic.csv");
		Dataset d = l.exportDataset();

		System.out.println(d.size());
		System.out.println(d.filter("expertise = \"experts\" and No_homogeneous_fragments != 1").size());
		System.out.println(d.keepEqual(new Entry("expertise", "experts")).size());
		System.out.println(d.keepEqual(new Entry("expertise", "familiar")).size());
		System.out.println(d.keepEqual(new Entry("expertise", "a")).size());
		
		System.out.println("---");
		System.out.println(d.getDistinctValues("expertise"));
		System.out.println(d.getAllValues("expertise"));
		
		CorrelationsComputer cc = new CorrelationsComputer(d.keepEqual(new Entry("expertise", "unfamiliar")));
		System.out.println(cc.getCorrelation("percent_acts_in_mixed_fragments_aligned", "sum_handles_free_choice"));
	}

}
