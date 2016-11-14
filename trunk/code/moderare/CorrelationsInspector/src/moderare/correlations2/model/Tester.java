package moderare.correlations2.model;

public class Tester {

	public static void main(String[] args) throws Exception {
		CSVLoader l = new CSVLoader("C:\\Users\\Andrea\\Desktop\\behavior-pragmatic.csv");
		Dataset d = l.exportDataset();

//		System.out.println(d.filterAny(new Entry("expertise", "experts")).size());
//		System.out.println(d.filterAny(new Entry("expertise", "familiar")).size());
//		System.out.println(d.filterAny(new Entry("expertise", "experts"), new Entry("expertise", "familiar")).size());
//		System.out.println(d.filterAll(new Entry("expertise", "a")).size());
//		
//		System.out.println("---");
//		System.out.println(d.getDistinctValues("expertise"));
//		System.out.println(d.getAllValues("expertise"));
//		
//		System.out.println("---");
//		System.out.println(d.slice("expertise", "percent_orthogonal_segments").size());
		
		CorrelationsComputer cc = new CorrelationsComputer(d.filterAll(new Entry("expertise", "unfamiliar")));
		System.out.println(cc.getCorrelation("percent_acts_in_mixed_fragments_aligned", "sum_handles_free_choice"));
	}

}
