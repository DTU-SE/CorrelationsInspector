package moderare.correlations.ui.widgets.tabs;

import java.awt.Component;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import moderare.correlations.calculators.CorrelationsComputer;
import moderare.correlations.model.Correlation;
import moderare.correlations.model.CorrelationsTable;
import moderare.correlations.model.Dataset;
import moderare.correlations.ui.widgets.ExcelAdapter;
import moderare.correlations.ui.widgets.ValueColorRenderer;
import moderare.correlations.ui.widgets.VerticalTableHeaderCellRenderer;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class CorrelationTab extends JSplitPane {

	private static final long serialVersionUID = 3864291727486660639L;
	
	// gui objects
	private static final DecimalFormat myFormatter = new DecimalFormat("0.000");
	private JTable table = null;
	private JTextPane description = null;
	
	// logic objects
	private Dataset dataset;
	private List<String> rows;
	private List<String> columns;
	private CorrelationsTable correlationsTable;
	private String formula;
	
	/**
	 * 
	 * @param dataset
	 * @throws Exception 
	 */
	public CorrelationTab(String formula, Dataset dataset, List<String> rows, List<String> columns) throws Exception {
		super(JSplitPane.HORIZONTAL_SPLIT,
				new JScrollPane(new JTextPane()),
				new JScrollPane(new JTable()));
		
		this.description = (JTextPane)  ((JScrollPane) getLeftComponent()).getViewport().getView();
		this.table = (JTable) ((JScrollPane) getRightComponent()).getViewport().getView();
		
		this.dataset = dataset;
		this.formula = formula;
		this.rows = rows;
		this.columns = columns;
		
		CorrelationsComputer cc = new CorrelationsComputer(dataset);
		this.correlationsTable = cc.getCorrelationTable(rows, columns);
		
		init();
		
		setDividerLocation(0.0d);
		getLeftComponent().setMinimumSize(new Dimension());
		getRightComponent().setMinimumSize(new Dimension());
		setOneTouchExpandable(true);
	}

	/**
	 * 
	 */
	private void init() {
		// table with correlations
		table.setModel(new CorrelationsTableModel());
		TableCellRenderer headerRenderer = new VerticalTableHeaderCellRenderer();
		Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
		while (columns.hasMoreElements()) {
			columns.nextElement().setHeaderRenderer(headerRenderer);
		}
		table.setDefaultRenderer(Object.class, new ValueColorRenderer());
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		table.getColumnModel().setColumnSelectionAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		for (int column = 0; column < 2; column++) {
			TableColumn tableColumn = table.getColumnModel().getColumn(column);
			int preferredWidth = tableColumn.getMinWidth();
			int maxWidth = tableColumn.getMaxWidth();
			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
				Component c = table.prepareRenderer(cellRenderer, row, column);
				int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
				preferredWidth = Math.max(preferredWidth, width);
				// We've exceeded the maximum width, no need to check other rows
				if (preferredWidth >= maxWidth) {
					preferredWidth = maxWidth;
					break;
				}
			}
			tableColumn.setPreferredWidth(preferredWidth);
		}
		new ExcelAdapter(table);
		
		// description of attributes
		List<String> attributes = new ArrayList<String>(dataset.getAttributes());
		Collections.sort(attributes);
		
		StringBuffer sb = new StringBuffer();
		sb.append("<html><body style=\"font-family: sans-serif;\">");
		sb.append("<h1>Stats</h1>");
		sb.append("<table border=\"1\" border-style=\"solid\">");
		sb.append("<tr><th>Attribute Name</th>"
				+ "<th>N</th>"
				+ "<th>Min</th>"
				+ "<th>Max</th>"
				+ "<th>Mean</th>"
				+ "<th>Median</th>"
				+ "<th>Std.Dev.</th>"
				+ "</tr>");
		for (String attribute : attributes) {
			DescriptiveStatistics ds = dataset.getStatistics(attribute);
			if (ds.getN() > 0) {
				sb.append("<tr>");
				sb.append("<td style=\"font-family: monospace;\">" + attribute + "</td>");
				sb.append("<td align=\"right\">" + ds.getN() + "</td>");
				sb.append("<td align=\"right\">" + myFormatter.format(ds.getMin()) + "</td>");
				sb.append("<td align=\"right\">" + myFormatter.format(ds.getMax()) + "</td>");
				sb.append("<td align=\"right\">" + myFormatter.format(ds.getMean()) + "</td>");
				sb.append("<td align=\"right\">" + myFormatter.format(ds.getPercentile(50)) + "</td>");
				sb.append("<td align=\"right\">" + myFormatter.format(ds.getStandardDeviation()) + "</td>");
				sb.append("</tr>");
			}
		}
		sb.append("</table>");
		sb.append("</body></html>");
		
		description.setContentType("text/html");
		description.setText(sb.toString());
		description.setEditable(false);
	}
	
	public String getFormula() {
		return formula;
	}

	// support classes ---------------------------------------------------------
	class CorrelationsTableModel extends DefaultTableModel {
	
		private static final long serialVersionUID = -7118654969223960972L;
		
		public String getColumnName(int column) {
			if (column < 2) {
				return "";
			}
			return columns.get(column - 2);
		}
		
		@Override
		public Object getValueAt(int row, int col) {
			if (col == 0) {
				if ((row % 3) == 0) {
					return rows.get(row / 3);
				} else {
					return "";
				}
			} else if (col == 1) {
				if ((row % 3) == 0) {
					return "corr";
				} else if ((row % 3) == 1) {
					return "sig";
				} else if ((row % 3) == 2) {
					return "freq";
				}
			} else {
				Correlation c = correlationsTable.get(rows.get((int) Math.floor((row) / 3d)), columns.get(col - 2));
				if (c != null) {
					if ((row % 3) == 0) {
						return c.getCorrelation();
					} else if ((row % 3) == 1) {
						return c.getSignificance();
					} else if ((row % 3) == 2) {
						return c.getFrequency();
					}
				}
			}
			return null;
		}
		
		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}
		
		@Override
		public int getRowCount() {
			return rows.size() * 3;
		}
		@Override
		public int getColumnCount() {
			return columns.size() + 2;
		}
	}
}
