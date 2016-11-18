package moderare.correlations.loader;

import moderare.correlations.model.Dataset;

public interface Loader {

	public void loadFile(String fileName);

	public Dataset exportDataset() throws Exception;

}
