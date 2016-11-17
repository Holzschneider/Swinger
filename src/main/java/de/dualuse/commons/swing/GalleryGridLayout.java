package de.dualuse.commons.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

public class GalleryGridLayout implements LayoutManager {

	public int cellSpacing = 4;
	public int targetCellSize = 128;
	public int preferredColumnCount = 2;
	public int minimumColumnCount = 1;
	public int fixedHeight = 0, fixedWidth = 0;
	
	public GalleryGridLayout() { }
	public GalleryGridLayout(int targetCellSize) {
		this.targetCellSize = targetCellSize;
	}

	@Override public void addLayoutComponent(String name, Component comp) {	}
	@Override public void removeLayoutComponent(Component comp) { }

	@Override public Dimension preferredLayoutSize(Container parent) {
		if (parent.getWidth()==0)
			return minimumLayoutSize(parent);
		Insets in = parent.getInsets();
		

		int S = targetCellSize, GAP = cellSpacing;
		int width = parent.getWidth()-in.left-in.right-GAP-1;
		
		int cellsPerLine = Math.max(1, width / (S+GAP));
		
		float cellSize = (width * 1f / cellsPerLine);
		float cellHeight = fixedHeight>0?fixedHeight:cellSize;
		
		int maxX = 0, maxY = 0;
		for (int i=0,I=parent.getComponentCount();i<I;i++) {
			int x = i % cellsPerLine, y = i / cellsPerLine;
			
			int x1 = GAP+(int)((x+1)*cellSize)+in.left+in.right, y1 = GAP+(int)((y+1)*cellHeight)+in.top+in.bottom;
			maxX = maxX<x1?x1:maxX;
			maxY = maxY<y1?y1:maxY;
		}
		
		return new Dimension(parent.getWidth()-targetCellSize/2,maxY);
	}
	
	@Override
	public Dimension minimumLayoutSize(Container parent) {
		int width = targetCellSize*minimumColumnCount;
		int height = targetCellSize * (1+(int)( parent.getComponentCount() *1f/ minimumColumnCount));
		return new Dimension(width,height); 
	}

	@Override
	public void layoutContainer(Container parent) {
		Insets in = parent.getInsets();
		
		int S = targetCellSize, GAP = cellSpacing;
		int width = parent.getWidth()-in.left-in.right-GAP-1;
		
		int cellsPerLine = Math.max(1, width / (S+GAP));
		
		float cellSize = (width * 1f / cellsPerLine);
		float cellWidth = fixedWidth>0?fixedWidth:cellSize;
		float cellHeight = fixedHeight>0?fixedHeight:cellSize;
		
		for (int i=0,I=parent.getComponentCount();i<I;i++) {
			int x = i % cellsPerLine, y = i / cellsPerLine;
			
			int x0 = GAP+(int)(x*cellSize), y0 = GAP+(int)(y*cellHeight);
			int x1 = GAP+(int)((x+1)*cellSize), y1 = GAP+(int)((y+1)*cellHeight);
			
			parent.getComponent(i).setBounds(in.left+x0+((x1-x0))/2-(int)cellWidth/2, in.top+y0, (int)(fixedWidth>0?cellWidth:((x1-x0)-GAP)), (y1-y0)-GAP);
		}
	}
	
	
	public static void main(String[] args) {
		
		JFrame f = new JFrame();
		
		f.setContentPane(new JScrollPane(new JPanel( new GalleryGridLayout() ) {
			private static final long serialVersionUID = 1L;

			{
				setBorder(new MatteBorder(100, 20, 80, 60, Color.GRAY));
				final Random r = new Random(1337);
				for (int i=0;i<20;i++)
					add(new JComponent() {
						private static final long serialVersionUID = 1L;
						Color c = new Color(Color.HSBtoRGB(r.nextFloat(), 0.5f, .85f));
						protected void paintComponent(Graphics g) {
							g.setColor(c);
							g.fillRect(0, 0, getWidth(), getHeight());
						}
					});
			}
			
		}));
		f.setBounds(100, 100, 800, 800);
		f.setVisible(true);
	}

}