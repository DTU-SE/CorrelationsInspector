package moderare.correlations2.app;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import moderare.correlations2.loader.CSVLoader;
import moderare.correlations2.ui.CorrelationsInspector2Frame;

public class CorrelationsInspector2 {
	
	public static void main(String[] args) throws Exception {
		
		String datasetFile = "C:\\Users\\Andrea\\Desktop\\behavior-pragmatic.csv";
		
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
		
		CorrelationsInspector2Frame frame = new CorrelationsInspector2Frame(new CSVLoader(datasetFile).exportDataset(), rows, columns);
		frame.setDefaultCloseOperation(CorrelationsInspector2Frame.EXIT_ON_CLOSE);
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.setSize(800, 800);
		frame.setTitle("MODERARE - " + datasetFile);
		frame.setVisible(true);
	}
}
