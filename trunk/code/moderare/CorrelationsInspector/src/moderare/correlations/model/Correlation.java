package moderare.correlations.model;

import moderare.correlations.utils.Triple;

public class Correlation extends Triple<Double, Double, Double> {

	public Correlation(Double correlation, Double significance, Double frequency) {
		super(correlation, significance, frequency);
	}
	
	public Double getCorrelation() {
		return getFirst();
	}
	
	public Double getSignificance() {
		return getSecond();
	}
	
	public Double getFrequency() {
		return getThird();
	}
}
