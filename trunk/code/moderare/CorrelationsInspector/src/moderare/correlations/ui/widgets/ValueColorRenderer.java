package moderare.correlations.ui.widgets;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import moderare.correlations.config.MinSignificance;

public class ValueColorRenderer implements TableCellRenderer {

	public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
	public static final DecimalFormat numberFormat = new DecimalFormat("#.####");
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		Component c = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if (column > 1) {
			Double significance = (Double) table.getModel().getValueAt((int) Math.floor(row / 3d) * 3 + 1, column);
			if (significance != null) {
				boolean isSignificant = significance <= MinSignificance.MIN_SIGNIFICANCE;
				if (isSignificant) {
					c.setForeground(Color.black);
				} else {
					c.setForeground(Color.lightGray);
				}
				if ((row % 3) == 0 && isSignificant) {
					Double correlation = (Double) table.getModel().getValueAt((int) Math.floor(row / 3d) * 3, column);
					Color bgColor = Color.white;
					if (correlation > 0) {
						bgColor = new Color(0, 255, 0, (int) (255 * (correlation)));
					} else if (correlation < 0) {
						bgColor = new Color(255, 0, 0, (int) (255 * (-correlation)));
					}
					c.setBackground(bgColor);
				} else {
					c.setBackground(Color.white);
				}
				((JLabel) c).setHorizontalAlignment(JLabel.RIGHT);
				((JLabel) c).setText(numberFormat.format(value));
			} else {
				reset(c);
			}
		} else {
			reset(c);
		}
		if (isSelected) {
			if (c.getBackground().equals(Color.white)) {
				c.setBackground(UIManager.getColor("Table.dropCellBackground"));
			} else {
				c.setBackground(c.getBackground().darker().darker().darker());
			}
		}
		return c;
	}
	
	private void reset(Component c) {
		c.setForeground(Color.black);
		c.setBackground(Color.white);
		((JLabel) c).setHorizontalAlignment(JLabel.LEFT);
	}
}
