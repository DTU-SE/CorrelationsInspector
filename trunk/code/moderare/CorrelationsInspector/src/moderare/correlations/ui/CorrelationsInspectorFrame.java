package moderare.correlations.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import moderare.correlations.calculators.CorrelationsComputer.CORRELATION_TYPE;
import moderare.correlations.model.Dataset;
import moderare.correlations.ui.widgets.tabs.ClosableJTabbedPane;
import moderare.correlations.ui.widgets.tabs.CorrelationTab;
import moderare.correlations.ui.widgets.tabs.SummaryTab;

public class CorrelationsInspectorFrame extends JFrame {

	private static final long serialVersionUID = 6840815319338936556L;
	
	// gui objects
	private JTextField filter = new JTextField();
	private ClosableJTabbedPane tabs = new ClosableJTabbedPane();
	private JComboBox<String> correlations = new JComboBox<>(new String[] {
			CORRELATION_TYPE.PEARSONS.name(),
			CORRELATION_TYPE.SPERMANS.name()});
	
	// logic objects
	private Dataset original;
	private List<String> rows;
	private List<String> columns;
	
	/**
	 * 
	 * @param datasetFileName
	 * @throws Exception
	 */
	public CorrelationsInspectorFrame(Dataset original, List<String> rows, List<String> columns) {
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
					newFormula(getFormula(), getCorrelationType());
				}
			}
		});
		
		correlations.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newFormula(getFormula(), getCorrelationType());
			}
		});
		
		JPanel filterPanel = new JPanel(new BorderLayout(5, 5));
		filterPanel.add(new JLabel("Filter: "), BorderLayout.WEST);
		filterPanel.add(filter, BorderLayout.CENTER);
		filterPanel.add(correlations, BorderLayout.EAST);
		add(filterPanel, BorderLayout.NORTH);
		
		tabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Component c = tabs.getSelectedComponent();
				if (c instanceof CorrelationTab) {
					String formula = ((CorrelationTab) c).getFormula();
					if (!formula.isEmpty()) {
						filter.setText(formula);
					}
				}
			}
		});
		
		// tab panel
		add(tabs, BorderLayout.CENTER);
		
		// show inital tabs
		tabs.add("Dataset summary", new SummaryTab(original));
		correlations.setSelectedItem(CORRELATION_TYPE.SPERMANS.name());
	}
	
	private String getFormula() {
		return filter.getText().trim();
	}
	
	private CORRELATION_TYPE getCorrelationType() {
		return CORRELATION_TYPE.valueOf((String) correlations.getSelectedItem());
	}
	
	/**
	 * 
	 * @param formula
	 * @param type 
	 */
	private void newFormula(String formula, CORRELATION_TYPE type) {
		try {
			String title = "Original datset";
			Dataset newDataset = original;
			if (!formula.isEmpty()) {
				newDataset = original.filter(formula);
				title = formula;
			}
			title += " (" + newDataset.size() + " entries, " + type.name().toLowerCase() + " correlation)";
			tabs.add(title, new CorrelationTab(formula, newDataset, rows, columns, type));
			tabs.setSelectedIndex(tabs.getTabCount() - 1);
		} catch (Exception e) {
			filter.selectAll();
			JOptionPane.showMessageDialog(this, e.getMessage(), "Formula error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
}
