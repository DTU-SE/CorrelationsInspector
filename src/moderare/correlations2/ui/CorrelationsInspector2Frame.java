package moderare.correlations2.ui;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import moderare.correlations2.model.Dataset;
import moderare.correlations2.ui.tabs.ClosableJTabbedPane;
import moderare.correlations2.ui.tabs.CorrelationTab;
import moderare.correlations2.ui.tabs.SummaryTab;

public class CorrelationsInspector2Frame extends JFrame {

	private static final long serialVersionUID = 6840815319338936556L;
	
	// gui objects
	private JTextField filter = new JTextField();
	private ClosableJTabbedPane tabs = new ClosableJTabbedPane();
	
	// logic objects
	private Dataset original;
	private List<String> rows;
	private List<String> columns;
	
	/**
	 * 
	 * @param datasetFileName
	 * @throws Exception
	 */
	public CorrelationsInspector2Frame(Dataset original, List<String> rows, List<String> columns) {
		this.original = original;
		this.rows = rows;
		this.columns = columns;
		
		init();
	}
	
	/**
	 * 
	 */
	private void init() {
		getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(10, 10));
		
		// filter panel
		filter.setFont(filter.getFont().deriveFont(15f));
		filter.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					newFormula(filter.getText().trim());
				}
			}
		});
		
		JPanel filterPanel = new JPanel(new BorderLayout(5, 5));
		filterPanel.add(new JLabel("Filter: "), BorderLayout.WEST);
		filterPanel.add(filter, BorderLayout.CENTER);
		add(filterPanel, BorderLayout.NORTH);
		
		// tab panel
		add(tabs, BorderLayout.CENTER);
		
		// show inital tabs
		tabs.add("Dataset summary", new SummaryTab(original));
		newFormula("");
	}
	
	/**
	 * 
	 * @param formula
	 */
	private void newFormula(String formula) {
		try {
			String title = "Original datset";
			Dataset newDataset = original;
			if (!formula.isEmpty()) {
				newDataset = original.filter(formula);
				title = formula;
			}
			tabs.add(title, new CorrelationTab(newDataset, rows, columns));
			tabs.setSelectedIndex(tabs.getTabCount() - 1);
		} catch (Exception e) {
			filter.selectAll();
			JOptionPane.showMessageDialog(this, e.getMessage(), "Formula error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
}
