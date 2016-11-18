package moderare.correlations;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import moderare.correlations.loader.CSVLoader;
import moderare.correlations.loader.Loader;
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
		
		List<String> rows = new LinkedList<String>();
		rows.add("no_Dead_transitions");
		rows.add("no_Non_live_transitions");
		rows.add("no_Unbounded_places");
		rows.add("no_PThandles");
		rows.add("no_TPhandles");
		rows.add("sum_handles");
		rows.add("no_Freechoice_violations");
		rows.add("sum_handles_free_choice");
		
		List<String> columns = new LinkedList<String>();
		columns.add("Alignment_of_elements");
		columns.add("Alignment_of_gateways");
		columns.add("No_homogeneous_fragments");
		columns.add("No_mixed_fragments");
		columns.add("Alignment_of_fragments_with_explicit_gateways");
		columns.add("Alignment_of_fragments_with_implicit_gateways");
		columns.add("percent_acts_in_mixed_fragments");
		columns.add("percent_acts_in_mixed_fragments_aligned");
		columns.add("percent_acts_in_mixed_fragments_not_aligned");
		columns.add("percent_acts_in_homogeneous_fragments");
		columns.add("percent_acts_in_homogeneous_fragments_aligned");
		columns.add("percent_acts_in_homogeneous_fragments_not_aligned");
		columns.add("percent_crossing_edges");
		columns.add("percent_orthogonal_segments");
		columns.add("M_BP");
		
		Loader l = new CSVLoader();
		l.loadFile(datasetFile);
		CorrelationsInspectorFrame frame = new CorrelationsInspectorFrame(l.exportDataset(), rows, columns);
		frame.setDefaultCloseOperation(CorrelationsInspectorFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.setSize(800, 800);
		frame.setTitle("MODERARE - " + datasetFile);
		frame.setVisible(true);
	}
}
