package moderare.correlations2.ui.tabs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

public class ClosableJTabbedPane extends JTabbedPane {

	private static final long serialVersionUID = 1040317242044917193L;

	@Override
	public void addTab(String title, Component component) {
		super.addTab(title, component);
		addCloseButton(component);
	}

	@Override
	public void addTab(String title, Icon icon, Component component) {
		super.addTab(title, icon, component);
		addCloseButton(component);
	}

	@Override
	public void addTab(String title, Icon icon, Component component, String tip) {
		super.addTab(title, icon, component, tip);
		addCloseButton(component);
	}

	private void addCloseButton(Component component) {
		super.setTabComponentAt(super.indexOfComponent(component),
				new ButtonTabComponent(this));
	}
}

// support class

class ButtonTabComponent extends JPanel {

	private static final long serialVersionUID = -2713045164101417004L;
	private final JTabbedPane pane;

	/**
	 * Constructs a {@code ButtonTabComponent}.
	 * 
	 * @param pane
	 *            the {@code JTabbedPane} containing this component.
	 */
	public ButtonTabComponent(final JTabbedPane pane) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		if (pane == null) {
			throw new NullPointerException("TabbedPane is null");
		}
		this.pane = pane;
		setOpaque(false);

		/*
		 * Make JLabel read titles from JTabbedPane.
		 */
		JLabel label = new JLabel() {

			private static final long serialVersionUID = 1L;
			public String getText() {
				int i = pane.indexOfTabComponent(ButtonTabComponent.this);
				if (i != -1) {
					return pane.getTitleAt(i);
				}
				return null;
			}
		};

		add(label);
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

		JButton button = new TabButton();
		add(button);
		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
	}

	private class TabButton extends JButton implements ActionListener {
		private static final long serialVersionUID = 1L;

		public TabButton() {
			int size = 17;
			setPreferredSize(new Dimension(size, size));
			setToolTipText("Close this tab");
			setUI(new BasicButtonUI());
			setContentAreaFilled(false);
			setFocusable(false);
			setRolloverEnabled(true);
			addActionListener(this);
			setComponentPopupMenu(new JPopupMenu());
			setBorder(BorderFactory.createEmptyBorder());
		}

		public void actionPerformed(ActionEvent e) {
			closeTab();
		}

		/*
		 * We don't want to update UI for this button
		 */
		public void updateUI() {
		}

		/*
		 * Paint the cross
		 */
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			if (getModel().isPressed()) {
				g2.translate(1, 1);
			}
			g2.setStroke(new BasicStroke(2));
			g2.setColor(Color.BLACK);
			
			if (getModel().isRollover()) {
				g2.setColor(Color.RED.darker());
				g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 3, 3);
				g2.setColor(Color.WHITE);
			}
			int delta = 5;
			g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
			g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
			g2.dispose();
		}

		public JPopupMenu getComponentPopupMenu() {
			JPopupMenu toret = new JPopupMenu();
			toret.add(new AbstractAction("Close all tabs") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					closeAllTabs();
				}
			});
			toret.add(new AbstractAction("Close this tab") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					closeTab();
				}
			});
			toret.add(new AbstractAction("Close other tabs") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					closeOtherTabs();
				}

				@Override
				public boolean isEnabled() {
					return pane.getTabCount() > 1;
				}
			});
			return toret;
		};
	}

	private void closeAllTabs() {
		pane.removeAll();
	}

	private void closeTab() {
		int index = pane.indexOfTabComponent(ButtonTabComponent.this);
		if (index != -1) {
			pane.remove(index);
		}
	}

	private void closeOtherTabs() {
		int index = pane.indexOfTabComponent(ButtonTabComponent.this);
		if (index != -1) {
			for (int toRemove = pane.getTabCount() - 1; toRemove >= 0; toRemove--) {
				if (toRemove != index) {
					pane.remove(toRemove);
				}
			}
		}
	}
}
