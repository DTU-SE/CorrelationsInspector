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

		toLoad.add("final-all");
		toLoad.add("final-all-novices");
		toLoad.add("final-all-experts");
		toLoad.add("final-eindhoven");
		toLoad.add("final-eyetracking-complex-cs");
		toLoad.add("final-eyetracking-complex-ps");
		toLoad.add("final-eyetracking-medium");
		toLoad.add("final-eyetracking-simple");
		toLoad.add("final-manuel");
		toLoad.add("final-modelingstyles-task1");
		toLoad.add("final-modelingstyles-task2");
		toLoad.add("final-novicesexperts-experts");
		toLoad.add("final-novicesexperts-novices");
		
		toLoad.add("avg-all");
		toLoad.add("avg-all-novices");
		toLoad.add("avg-all-experts");
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
				System.out.println(file);
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
