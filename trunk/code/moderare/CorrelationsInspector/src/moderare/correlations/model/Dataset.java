package moderare.correlations.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import moderare.correlations.utils.Pair;

public class Dataset extends HashMap<Pair<String, String>, Correlation> {

	private static final long serialVersionUID = -6863550420221329450L;
	
	public static final List<String> rows = new ArrayList<String>();
	public static final List<String> columns = new ArrayList<String>();
	static {
		rows.add("no_Dead_transitions");
		rows.add("no_Non_live_transitions");
		rows.add("no_Unbounded_places");
		rows.add("no_PThandles");
		rows.add("no_TPhandles");
		rows.add("sum_handles");
		rows.add("no_Freechoice_violations");
		rows.add("sum_handles_free_choice");
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
		columns.add("M-BP");
	};
	
	private String datasetName;
	
	public Dataset(String datasetName, Double[][] values) {
		this.datasetName = datasetName;
		if (values != null) {
			load(values);
		}
	}
	
	public Dataset(File file) throws FileNotFoundException {
		this.datasetName = file.getName();
		Double[][] values = new Double[rows.size()*3][columns.size()];
		Scanner input = new Scanner(file);
		int row = 0;
		while(input.hasNext()) {
			String nextLine = input.nextLine();
			String[] tabs = nextLine.split("\t");
			for (int col = 0; col < tabs.length; col++) {
				values[row][col] = Double.parseDouble(tabs[col]);
			}
			row++;
		}
		input.close();
		load(values);
	}
	
	public Dataset(String datasetName) {
		this(datasetName, null);
	}
	
	private void load(Double[][] values) {
		for (int i = 0; i < rows.size(); i++) {
			for (int j = 0; j < columns.size(); j++) {
				String row = rows.get(i);
				String column = columns.get(j);
				Double corr = values[i*3][j];
				Double sign = values[i*3+1][j];
				Double freq = values[i*3+2][j];
				put(new Pair<String, String>(row, column), new Correlation(corr, sign, freq));
				put(new Pair<String, String>(column, row), new Correlation(corr, sign, freq));
			}
		}
	}
	
	public String getName() {
		return datasetName;
	}
	
	@Override
	public int hashCode() {
		return datasetName.hashCode();
	}
	
	@Override
	public String toString() {
		String out = "DATASET: " + datasetName + "\n";
		String[][] matrix = new String[3*rows.size()+1][2+columns.size()];
		
		for (int i = 0; i < rows.size(); i++) {
			String row = rows.get(i);
			matrix[i*3+1][0] = row;
			matrix[i*3+1][1] = "corr";
			matrix[i*3+2][1] = "sign";
			matrix[i*3+3][1] = "freq";
			for (int j = 0; j < columns.size(); j++) {
				String column = columns.get(j);
				if (i == 0) {
					matrix[0][2+j] = column;
				}
				Correlation triple = get(new Pair<String, String>(row, column));
				matrix[i*3+1][2+j] = (triple.getCorrelation() == null? null : triple.getCorrelation().toString());
				matrix[i*3+2][2+j] = (triple.getSignificance() == null? null : triple.getSignificance().toString());
				matrix[i*3+3][2+j] = (triple.getFrequency() == null? null : triple.getFrequency().toString());
			}
		}
		
		for (int i = 0; i < matrix.length; i++) {
			String[] line = matrix[i];
			for (int j = 0; j < line.length; j++) {
				out += (line[j] == null? "/":line[j]) + "\t";
			}
			out += "\n";
		}
		return out;
	}
	
	public static Dataset sharedSignificance(Dataset d1, Dataset d2, double minSignificance) {
		Dataset shared = new Dataset(d1.datasetName + " AND " + d2.datasetName);
		
		for(String row : rows) {
			for (String column : columns) {
				Pair<String, String> pair = new Pair<String, String>(row, column);
				Correlation c1 = d1.get(pair);
				Correlation c2 = d2.get(pair);
				
				Correlation cs = new Correlation(null, null, null);
				if (c1.getSignificance() != null && c1.getSignificance() <= minSignificance && c2.getSignificance() != null && c2.getSignificance() <= minSignificance) {
					cs = new Correlation(
							(c1.getCorrelation() + c2.getCorrelation()) / 2d,
							(c1.getSignificance() + c2.getSignificance()) / 2d,
							(c1.getFrequency() + c2.getFrequency()) / 2d);
				}
				shared.put(pair, cs);
			}
		}
		return shared;
	}
}
