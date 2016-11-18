package moderare.correlations2.loader;

import moderare.correlations2.model.Dataset;

public interface Loader {

	public void loadFile(String fileName);

	public Dataset exportDataset() throws Exception;

}
