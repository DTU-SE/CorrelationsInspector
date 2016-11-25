package moderare.correlations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import moderare.correlations.loader.CSVLoader;
import moderare.correlations.loader.Loader;
import moderare.correlations.model.Dataset;
import moderare.correlations.ui.AttributesDialog;
import moderare.correlations.ui.CorrelationsInspectorFrame;

public class CorrelationsInspector {
	
	public static void main(String[] args) throws Exception {
		
		String datasetFile;
		if (args.length == 1) {
			datasetFile = args[0];
		} else {
			JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
			fc.setDialogTitle("Select the CSV file with the observations");
			fc.setFileFilter(new FileNameExtensionFilter("CSV file", "csv"));
			if (fc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
				return;
			}
			datasetFile = fc.getSelectedFile().getAbsolutePath();
		}

		Loader l = new CSVLoader();
		l.loadFile(datasetFile);
		Dataset d = l.exportDataset();
		
		List<String> attributes = new ArrayList<String>(d.getAttributes());
		Collections.sort(attributes);
		
		AttributesDialog rowsDialog = new AttributesDialog("Select rows attributes", attributes);
		rowsDialog.setVisible(true);
		AttributesDialog columnsDialog = new AttributesDialog("Select column attributes", attributes);
		columnsDialog.setVisible(true);
		
		CorrelationsInspectorFrame frame = new CorrelationsInspectorFrame(d, rowsDialog.getSelectedAttributes(), columnsDialog.getSelectedAttributes());
		frame.setDefaultCloseOperation(CorrelationsInspectorFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.setSize(800, 800);
		frame.setTitle("CorrelationsInspector - " + datasetFile);
		frame.setVisible(true);
	}
}
