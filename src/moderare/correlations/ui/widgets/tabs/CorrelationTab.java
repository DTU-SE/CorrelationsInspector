package moderare.correlations.ui.widgets.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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

public class CorrelationTab extends JPanel {

	private static final long serialVersionUID = 3864291727486660639L;
	
	// gui objects
	private JTable table = null;
	
	// logic objects
//	private Dataset dataset;
	private List<String> rows;
	private List<String> columns;
	private CorrelationsTable correlationsTable;
	
	/**
	 * 
	 * @param dataset
	 * @throws Exception 
	 */
	public CorrelationTab(Dataset dataset, List<String> rows, List<String> columns) throws Exception {
//		this.dataset = dataset;
		this.rows = rows;
		this.columns = columns;
		
		CorrelationsComputer cc = new CorrelationsComputer(dataset);
		this.correlationsTable = cc.getCorrelationTable(rows, columns);
		
		init();
	}

	/**
	 * 
	 */
	private void init() {
		table = new JTable(new CorrelationsTableModel());
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
		
		setLayout(new BorderLayout());
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	
	// support classes ---------------------------------------------------------
	class CorrelationsTableModel extends DefaultTableModel {
	
		private static final long serialVersionUID = -7118654969223960972L;
		
		@Override
		public Object getValueAt(int row, int col) {
			if (col == 0) {
				if ((row % 3) == 0) {
					return rows.get((row - 1) / 3);
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
		
		public String getColumnName(int column) {
			if (column < 2) {
				return "";
			}
			return columns.get(column - 2);
		};
	};
}