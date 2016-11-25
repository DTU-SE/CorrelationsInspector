package moderare.correlations.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class AttributesDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 945992212175979851L;
	private JList<String> list;
	private List<String> selectedAttributes;
	
	public AttributesDialog(String title, List<String> attributes) {
		super();
		
		setTitle(title);
		setModal(true);
		
		// create and initialize the buttons
		JButton setButton = new JButton("Select");
		setButton.addActionListener(this);
		getRootPane().setDefaultButton(setButton);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(setButton);
		
		// create the list
		list = new JList<String>(attributes.toArray(new String[attributes.size()]));
		
		// add everything
		setLayout(new BorderLayout());
		add(new JScrollPane(list), BorderLayout.CENTER);
		add(buttonPane, BorderLayout.PAGE_END);
		
		pack();
		setSize(400, 400);
		setLocationRelativeTo(null);
	}
	
	public List<String> getSelectedAttributes() {
		return selectedAttributes;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (list.getSelectedValuesList().size() == 0) {
			JOptionPane.showMessageDialog(this, "Select at least one attribute!", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			selectedAttributes = list.getSelectedValuesList();
			setVisible(false);
		}
	}
}
