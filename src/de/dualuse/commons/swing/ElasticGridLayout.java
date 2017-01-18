package de.dualuse.commons.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ElasticGridLayout implements LayoutManager {
	
	private final int rows, cols, xs[], ys[], widths[], heights[], vgap, hgap;
	public ElasticGridLayout(int rows, int cols, int hgap, int vgap) {
		this.rows = rows;
		this.cols = cols;
		
		this.xs = new int[cols];
		this.ys = new int[rows];
		this.widths = new int[cols];
		this.heights = new int[rows];
		
		this.vgap = vgap;
		this.hgap = hgap;
	}
	
	public ElasticGridLayout(int rows, int cols) {
		this(rows,cols,0,0);
	}
	
	public void addLayoutComponent(String name, Component comp) { }
	public void removeLayoutComponent(Component comp) { }
	public Dimension preferredLayoutSize(Container parent) {
		for (int i=0;i<rows;i++) heights[i]=0;
		for (int i=0;i<cols;i++) widths[i]=0;
		
		final int numCompontents = parent.getComponentCount();
		for (int i=0,l=numCompontents;i<l;i++) {
			int col = i%cols;
			int row = i/cols;
			
			Component c = parent.getComponent(i);
			Dimension dim = c.getPreferredSize();
			
			widths[col] = Math.max(widths[col],dim.width);
			heights[row] = Math.max(heights[row],dim.height);
		}
		
		int prefHeight = 0, prefWidth = 0;
		for (int i=0;i<rows;i++) 
			ys[i] = ((prefHeight += heights[i]))-heights[i];
		
		for (int i=0;i<cols;i++) 
			xs[i] = ((prefWidth += widths[i]))-widths[i];
		
		Insets in = parent.getInsets();
		return new Dimension(prefWidth+hgap*(cols-1)+in.left+in.right,prefHeight+vgap*(numCompontents/cols-1)+in.top+in.bottom);
	}

	public Dimension minimumLayoutSize(Container parent) { 
		return new Dimension(0,0);
	}

	public void layoutContainer(Container parent) {
		
		Dimension prefDim = preferredLayoutSize(parent);
		float widthScaleFactor = parent.getWidth()*1f/prefDim.width;
		float heightScaleFactor = parent.getHeight()*1f/prefDim.height; 

		Insets in = parent.getInsets();
		for (int i=0,l=parent.getComponentCount();i<l;i++) {
			int col = i%cols;
			int row = i/cols;
		
			parent.getComponent(i).setBounds((int)(in.left+hgap*col+xs[col]*widthScaleFactor),(int)(in.top+vgap*row+ys[row]*heightScaleFactor),(int)(widths[col]*widthScaleFactor), (int)(heights[row]*heightScaleFactor));
		}
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		
		
//		p.add(new JButton("möp"));
//		p.add(new JButton("halllo dies ist ein Test") {{setPreferredSize(new Dimension(100,100));}});
//		p.add(new JButton());
//		p.add(new JButton("bla blabalb blablabal "));
		
//		f.getContentPane().setLayout(new FlowLayout());
//		f.getContentPane().add(p);
		f.setBounds(300,100,500,400);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
