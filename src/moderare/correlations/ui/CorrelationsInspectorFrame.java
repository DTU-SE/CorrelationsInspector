package moderare.correlations.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;

import moderare.correlations.model.Correlation;
import moderare.correlations.model.CorrelationsCollection;
import moderare.correlations.model.Dataset;
import moderare.correlations.ui.widgets.VerticalTableHeaderCellRenderer;
import moderare.correlations.utils.Pair;

public class CorrelationsInspectorFrame extends JFrame {

	private static final long serialVersionUID = 2703376924626039135L;
	private CorrelationsCollection correlations = new CorrelationsCollection();
	private Dataset current;
	
	DefaultMutableTreeNode datasetsRootNode = new DefaultMutableTreeNode("JTree");
	JTree datasetsList = new JTree() {
		private static final long serialVersionUID = -8883229333373308499L;
		@Override
		public void updateUI() {
			setCellRenderer(null);
			setCellEditor(null);
			super.updateUI();
			setEditable(true);
			setRootVisible(false);
			setShowsRootHandles(false);
			setCellRenderer(new CheckBoxNodeRenderer());
			setCellEditor(new CheckBoxNodeEditor());
		}
	};
	
	
	TableModel tm = new DefaultTableModel() {
		private static final long serialVersionUID = -7118654969223960972L;
		
		@Override
		public Object getValueAt(int row, int col) {
			if (col == 0) {
				if ((row % 3) == 0) {
					return Dataset.rows.get((row - 1) / 3);
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
				Dataset d = getCurrent();
				if (d != null) {
					Pair<String, String> key = new Pair<String, String>(
							Dataset.rows.get((int) Math.floor((row) / 3d)),
							Dataset.columns.get(col - 2));
					Correlation c = d.get(key);
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
			return Dataset.rows.size() * 3;
		}
		@Override
		public int getColumnCount() {
			return Dataset.columns.size() + 2;
		}
		
		public String getColumnName(int column) {
			if (column < 2) {
				return "";
			}
			return Dataset.columns.get(column - 2);
		};
	};
	JTable table = new JTable(tm);
	
	public CorrelationsInspectorFrame() {
		setLayout(new BorderLayout());
		
		// list of datasets
		JPanel listPanel = new JPanel(new BorderLayout());
		datasetsList.setModel(new DefaultTreeModel(datasetsRootNode));
		for (String dataset : correlations.keySet()) {
			datasetsRootNode.add(new DefaultMutableTreeNode(new CheckBoxNode(dataset, false)));
		}
		((DefaultTreeModel) datasetsList.getModel()).reload(datasetsRootNode);
		
		JButton updateButton = new JButton("Update");
		updateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String> toCompute = new HashSet<String>();
				for (int i = 0 ; i < datasetsRootNode.getChildCount(); i++) {
					DefaultMutableTreeNode n = (DefaultMutableTreeNode) datasetsRootNode.getChildAt(i);
					CheckBoxNode cbn = (CheckBoxNode) n.getUserObject();
					if (cbn.selected) {
						toCompute.add(cbn.text);
					}
				}
				current = correlations.getShared(toCompute);
				((AbstractTableModel) table.getModel()).fireTableDataChanged();
				
				
				System.out.println("NEW MODEL " + toCompute.toString() + " - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
				System.out.println(current);
				System.out.println("========================================\n\n");
			}
		});
		
		listPanel.add(new JScrollPane(datasetsList), BorderLayout.NORTH);
		listPanel.add(updateButton, BorderLayout.SOUTH);
		add(listPanel, BorderLayout.WEST);
		
		// add table
		TableCellRenderer headerRenderer = new VerticalTableHeaderCellRenderer();
		Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
		while (columns.hasMoreElements()) {
			columns.nextElement().setHeaderRenderer(headerRenderer);
		}
		table.setDefaultRenderer(Object.class, new ValueColorRenderer());
		
		add(new JScrollPane(table), BorderLayout.CENTER);
	}
	
	
	private Dataset getCurrent() {
		return current;
	}
	
	public static void main(String[] args) throws IOException {
		CorrelationsInspectorFrame frame = new CorrelationsInspectorFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.setSize(800, 800);
		frame.setTitle("MODERARE - Correlations computation");
		frame.setVisible(true);
	}
}

//support classes -------------------------------------------------------------

class CheckBoxNode {
	public final String text;
	public final boolean selected;

	protected CheckBoxNode(String text, boolean selected) {
		this.text = text;
		this.selected = selected;
	}

	@Override
	public String toString() {
		return text;
	}
}

class CheckBoxNodeRenderer implements TreeCellRenderer {
	private final JCheckBox checkBox = new JCheckBox();
	private final TreeCellRenderer renderer = new DefaultTreeCellRenderer();

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if (leaf && value instanceof DefaultMutableTreeNode) {
			checkBox.setOpaque(false);
			Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
			if (userObject instanceof CheckBoxNode) {
				CheckBoxNode node = (CheckBoxNode) userObject;
				checkBox.setText(node.text);
				checkBox.setSelected(node.selected);
			}
			return checkBox;
		}
		return renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	}
}

class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {
	private static final long serialVersionUID = -4869945478686992753L;
	private final JCheckBox checkBox = new JCheckBox() {
		private static final long serialVersionUID = 3620299210768257346L;
		private transient ActionListener handler;

		@Override
		public void updateUI() {
			removeActionListener(handler);
			super.updateUI();
			setOpaque(false);
			setFocusable(false);
			handler = e -> stopCellEditing();
			addActionListener(handler);
		}
	};

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row) {
		if (leaf && value instanceof DefaultMutableTreeNode) {
			Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
			if (userObject instanceof CheckBoxNode) {
				checkBox.setSelected(((CheckBoxNode) userObject).selected);
			} else {
				checkBox.setSelected(false);
			}
			checkBox.setText(value.toString());
		}
		return checkBox;
	}

	@Override
	public Object getCellEditorValue() {
		return new CheckBoxNode(checkBox.getText(), checkBox.isSelected());
	}

	@Override
	public boolean isCellEditable(EventObject e) {
		return e instanceof MouseEvent;
	}
}
