package de.dualuse.commons.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ListLayout implements LayoutManager2 {

	private final int fixedHeight;

	private int margin;

	private ArrayList<Component> layoutedComponents = new ArrayList<Component>();

	public ListLayout() {
		this(0, -1);
	}

	public ListLayout(int margin) {
		this(margin, -1);
	}

	public ListLayout(int margin, int fixedHeight) {
		this.fixedHeight = fixedHeight;
		this.setMargin(margin);
	}

	public void setMargin(int margin) {
		this.margin = margin;
	}

	public int getMargin() {
		return margin;
	}

	public void addLayoutComponent(String name, Component comp) {
		layoutedComponents.add(comp);
	}

	public void layoutContainer(Container parent) {
		Insets in = parent.getInsets();

		int sumY = in.top, width = parent.getWidth() - in.left - in.right;
		for (int i = 0, l = parent.getComponentCount(); i < l; i++) {
			final Component c = parent.getComponent(i);
			final int height = fixedHeight == -1 ? c.getPreferredSize().height : fixedHeight;
			c.setBounds(in.left, sumY, width, height);
			sumY += height + (i < l - 1 ? getMargin() : 0);
		}

		sumY += in.bottom;
	}

	public Dimension minimumLayoutSize(Container parent) {
		Insets in = parent.getInsets();

		int sumY = in.top, maxWidth = 0;
		for (int i = 0, l = parent.getComponentCount(); i < l; i++) {
			final Component c = parent.getComponent(i);
			Dimension minimum = c.getMinimumSize();
			final int height = fixedHeight == -1 ? minimum.height : fixedHeight;

			maxWidth = Math.max(minimum.width, maxWidth);
			sumY += height + (i < l - 1 ? getMargin() : 0);
		}

		sumY += in.bottom;
		return new Dimension(maxWidth + in.left + in.right, sumY);
	}

	public Dimension preferredLayoutSize(Container parent) {
		Insets in = parent.getInsets();

		int sumY = in.top, maxWidth = 0;
		for (int i = 0, l = parent.getComponentCount(); i < l; i++) {
			final Component c = parent.getComponent(i);
			Dimension preferred = c.getPreferredSize();
			final int height = fixedHeight == -1 ? preferred.height : fixedHeight;

			maxWidth = Math.max(preferred.width, maxWidth);
			sumY += height + (i < l - 1 ? getMargin() : 0);
		}

		sumY += in.bottom;
		// return new Dimension(maxWidth,sumY);
		return new Dimension(maxWidth + in.left + in.right, sumY);
	}

	public void removeLayoutComponent(Component comp) {
		layoutedComponents.remove(comp);
	}

	public static void main(String[] args) {

		JFrame f = new JFrame();

		JPanel p = new JPanel(new ListLayout(0, 40));
		final Color dimmed = new Color(236, 243, 254);
		p.add(new JLabel("hallo		JPanel p = new JPanel(new ListLayout()); ") {
			{
				setOpaque(true);
				setBackground(Color.WHITE);
			}
		});
		p.add(new JLabel("hallo		public void removeLayoutComponent(Component comp) { layoutedComponents.remove(comp); }") {
			{
				setOpaque(true);
				setBackground(dimmed);
			}
		});
		p.add(new JLabel("hallo		f.getContentPane().add(new JScrollPane(p));") {
			{
				setOpaque(true);
				setBackground(Color.WHITE);
			}
		});
		p.add(new JLabel("hallo		f.setBounds(100,100,500,300);") {
			{
				setOpaque(true);
				setBackground(dimmed);
			}
		});
		p.add(new JLabel("hallo		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);") {
			{
				setOpaque(true);
				setBackground(Color.WHITE);
			}
		});
		p.setBackground(Color.WHITE);
		f.getContentPane().add(new JScrollPane(p));

		f.setBounds(100, 100, 500, 300);

		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void addLayoutComponent(Component comp, Object constraints) {
		
	}

	public Dimension maximumLayoutSize(Container target) {
		return null;
	}

	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	public void invalidateLayout(Container target) {
		
	}
}