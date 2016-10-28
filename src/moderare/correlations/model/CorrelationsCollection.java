package moderare.correlations.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import moderare.correlations.config.MinSignificance;

public class CorrelationsCollection extends HashMap<String, Dataset> {

	private static final long serialVersionUID = -4538125166743883419L;
	private List<String> toLoad = new LinkedList<String>();
	
	public CorrelationsCollection() {

		toLoad.add("eindhoven");
		toLoad.add("eyetracking-complex-cs");
		toLoad.add("eyetracking-complex-ps");
		toLoad.add("eyetracking-medium");
		toLoad.add("eyetracking-simple");
		toLoad.add("manuel");
		toLoad.add("modelingstyles-task1");
		toLoad.add("modelingstyles-task2");
		toLoad.add("novicesexperts-experts");
		toLoad.add("novicesexperts-novices");
		
		toLoad.add("avg-eindhoven");
		toLoad.add("avg-eyetracking-complex-cs");
		toLoad.add("avg-eyetracking-complex-ps");
		toLoad.add("avg-eyetracking-medium");
		toLoad.add("avg-eyetracking-simple");
		toLoad.add("avg-manuel");
		toLoad.add("avg-modelingstyles-task1");
		toLoad.add("avg-modelingstyles-task2");
		toLoad.add("avg-novicesexperts-experts");
		toLoad.add("avg-novicesexperts-novices");
		
		for(String file : toLoad) {
			try {
				put(file, new Dataset("correlations/" + file));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Dataset getShared(Set<String> datasetNames) {
		if (datasetNames == null || datasetNames.size() == 0) {
			return null;
		}
		if (datasetNames.size() == 1) {
			return get(datasetNames.iterator().next());
		}
		
		Set<Dataset> datasets = new HashSet<Dataset>();
		for (String dataset : datasetNames) {
			datasets.add(get(dataset));
		}
		return Dataset.sharedSignificance(datasets, MinSignificance.MIN_SIGNIFICANCE);
	}
}
