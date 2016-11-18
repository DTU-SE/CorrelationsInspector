package moderare.correlations.calculators;

import java.util.ArrayList;
import java.util.Collection;

import moderare.correlations.model.Correlation;
import moderare.correlations.model.CorrelationsTable;
import moderare.correlations.model.Dataset;
import moderare.correlations.model.Entry;
import moderare.correlations.model.Record;
import moderare.correlations.model.Entry.TYPE;
import moderare.correlations.utils.Pair;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

public class CorrelationsComputer {

	private Dataset dataset;
	
	public CorrelationsComputer(Dataset dataset) {
		this.dataset = dataset;
	}
	
	public CorrelationsTable getCorrelationTable(Collection<String> set1, Collection<String> set2) throws Exception {
		CorrelationsTable toReturn = new CorrelationsTable();
		for (String attribute1 : set1) {
			for (String attribute2 : set2) {
				toReturn.add(attribute1, attribute2, getCorrelation(attribute1, attribute2));
			}
		}
		return toReturn;
	}
	
	public Correlation getCorrelation(String attribute1, String attribute2) throws Exception {
		ArrayList<Pair<Double, Double>> values = new ArrayList<Pair<Double, Double>>();
		for (Record r : dataset) {
			Entry e1 = r.get(attribute1);
			Entry e2 = r.get(attribute2);
			
			if (e1 != null && e2 != null) {
				if (e1.getType() != TYPE.NUMERIC || e2.getType() != TYPE.NUMERIC) {
					throw new Exception("Correlation between non-numeric variables!");
				}
				
				if (e1.getValue() != null && e2.getValue() != null) {
					values.add(new Pair<Double, Double>(e1.getValueNumeric(), e2.getValueNumeric()));
				}
			}
		}
		
		if (values.size() == 0) {
			return null;
		}
		
		double[][] dataset = new double[values.size()][2];
		for (int i = 0; i < values.size(); i++) {
			dataset[i][0] = values.get(i).getFirst();
			dataset[i][1] = values.get(i).getSecond();
		}
		
		Array2DRowRealMatrix m = new Array2DRowRealMatrix(dataset);
		PearsonsCorrelation corr = new SpearmansCorrelation(m).getRankCorrelation();
		
		Double correlation = corr.getCorrelationMatrix().getEntry(0, 1);
		Double significance = corr.getCorrelationPValues().getEntry(0, 1);
		return new Correlation(correlation, significance, (double) dataset.length);
	}
}
