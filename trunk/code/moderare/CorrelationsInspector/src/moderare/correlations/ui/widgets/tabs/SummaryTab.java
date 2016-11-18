package moderare.correlations.ui.widgets.tabs;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import moderare.correlations.model.Dataset;
import moderare.correlations.model.Entry;
import moderare.correlations.model.Entry.TYPE;

public class SummaryTab extends JPanel {

	private static final long serialVersionUID = -3348416545513698931L;

	private Dataset dataset;
	private JTextPane description;
	
	public SummaryTab(Dataset dataset) {
		this.dataset = dataset;
		
		init();
	}
	
	private void init() {
		List<String> attributes = new ArrayList<String>(dataset.getAttributes());
		Collections.sort(attributes);
		
		StringBuffer sb = new StringBuffer();
		sb.append("<html><body style=\"font-family: sans-serif;\">");
		sb.append("<h1>Summary</h1>");
		sb.append("<p>Summary of all attributes (" + dataset.getAttributes().size() + " in total):");
		sb.append("<ol>");
		for(String attribute : attributes) {
			String attributeName = attribute;

			Set<Entry> values = dataset.getDistinctValues(attribute);
			if (values.iterator().next().getType() != TYPE.STRING) {
				attributeName += " <i>(The attribute is not a string)</i>";
			} else if (values.size() > 20) {
				attributeName += " <i>(More than 20 distinct values for this attribute)</i>";
			} else if (values.size() == 0) {
				attributeName += " <i>(No value to show for this attribute)</i>";
			}
			
			sb.append("<li>" + attributeName + "</li>");
		}
		sb.append("</ol>");
		sb.append("<h1>Attributes</h1>");
		for(String attribute : attributes) {
			Set<Entry> values = dataset.getDistinctValues(attribute);
			if (values.size() <= 20 && values.size() != 0 && values.iterator().next().getType() == TYPE.STRING) {
				List<String> valuesString = new ArrayList<String>();
				for (Entry e : values) {
					if (e.getValue() != null) {
						valuesString.add(e.getValue().toString());
					}
				}
				Collections.sort(valuesString);
				
				sb.append("<h2>" + attribute + "</h2>");
				sb.append("<table border=\"1\" border-style=\"solid\">");
				sb.append("<tr><th>#</th><th>Value</th><th>Frequency</th></tr>");
				int i = 1;
				for (String v : valuesString) {
					sb.append("<tr>");
					sb.append("<td align=\"right\">" + (i++) + "</td>");
					sb.append("<td style=\"font-family: monospace;\">" + v + "</td>");
					sb.append("<td align=\"right\">" + dataset.keepEqual(new Entry(attribute, v)).size() + "</td>");
					sb.append("</tr>");
				}
				sb.append("</table>");
			}
		}
		sb.append("</body></html>");
		
		setLayout(new BorderLayout());
		description = new JTextPane();
		description.setContentType("text/html");
		description.setText(sb.toString());
		description.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(new JScrollPane(description), BorderLayout.CENTER);
	}
}
