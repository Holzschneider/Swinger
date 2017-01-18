package de.dualuse.commons.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class RasterLayout implements java.awt.LayoutManager2, Serializable {
	private static final long serialVersionUID = 1L;
	
	private HashMap<Component, RasterLayout.Range> componentConstraints = new HashMap<Component, Range>();
	
	
	static public class Range implements Serializable {
		private static final long serialVersionUID = 1L;
		public int fromY, fromX, toY, toX;
		
		public Range(int fx, int fy, int tx, int ty) {
			fromY=fy;
			fromX=fx;
			toY=ty;
			toX=tx;
		}
		
		public Range(String s) {
			this.fromString(s);
		}
		
		@Override
		public String toString() {
			return String.format("RasterLayout.Range[fromX=%d,fromY=%d,toX=%d,toY=%d]", fromX, fromY, toX, toY);
		}
		
		public Range fromString(String s) {
			if (!(s.charAt(0)=='[') && !s.startsWith("RasterLayout.Range[")) 
				throw new NumberFormatException();
			
			String v[] = s.substring(s.indexOf('['),s.indexOf(']')).split("\\,");
			fromX = Integer.parseInt(v[0].substring(v[0].indexOf('=')+1).trim());
			fromY = Integer.parseInt(v[1].substring(v[1].indexOf('=')+1).trim());
			toX = Integer.parseInt(v[2].substring(v[2].indexOf('=')+1).trim());
			toY = Integer.parseInt(v[3].substring(v[3].indexOf('=')+1).trim());
			
			return this;
		}
	}
	
	static public class Cell extends Range {
		public Cell(int x, int y) {
			super(x,y,x,y);
		}
	}

	public RasterLayout() {
		this(1,1,0,0);
	}
	
	int cw, ch, gw, gh;
	public RasterLayout(int cellWidth, int cellHeight, int hGap, int vGap) { 
		this.cw=cellWidth;
		this.ch=cellHeight; 
		this.gw=hGap;
		this.gh=vGap;
	}
	
	public void addLayoutComponent(Component comp, Object constraints) { 
		componentConstraints.put(comp, (Range)constraints);
	}

	public void removeLayoutComponent(Component comp) { componentConstraints.remove(comp); }

	public float getLayoutAlignmentX(Container target) { return 0; }

	public float getLayoutAlignmentY(Container target) { return 0; }

	public void invalidateLayout(Container target) { }

	public Dimension maximumLayoutSize(Container target) { return new Dimension(0,0); }

	public void addLayoutComponent(String name, Component comp) { }

	
	public void layoutContainer(Container parent) {
		int W = parent.getWidth(), H = parent.getHeight();
		
		for (Component c: parent.getComponents() )  {
			Range a = componentConstraints.get(c);
			
			if (a!=null) {
				
				int fx = a.fromX*(cw+gw)+(a.fromX<0?W:0);
				int fy = a.fromY*(ch+gh)+(a.fromY<0?H:0);
				
				int tx = cw+a.toX*(cw+gw)+(a.toX<0?W:0);
				int ty = ch+a.toY*(ch+gh)+(a.toY<0?H:0);
	
				if (!overrideLayout.contains(c)) {
					if (!usePreferredSizes)
						c.setBounds(fx, fy, tx-fx, ty-fy);
					else {
						Dimension d = c.getPreferredSize();
						c.setBounds(fx, fy, d.width, d.height);
					}
				}
				
			} else {
				
				c.setSize(c.getPreferredSize());
				c.setLocation(0, 0);
				
			}
		}
	}
	
	
	final public Set<Component> overrideLayout = new HashSet<Component>();
	
	boolean usePreferredSizes = false; 

	public Dimension minimumLayoutSize(Container parent) { return new Dimension(0,0); }

	public Dimension preferredLayoutSize(Container parent) { 

		int maxW=0,maxH=0;

		/*
		for (int i=0;i<parent.getComponentCount();i++) {
			Component c =parent.getComponent(i);
			Range a = get(c);
			
			int colspan = 1+(a.toX<0?cols-a.toX:a.toX)-(a.fromX<0?cols-a.fromX:a.fromX);
			int rowspan = 1+(a.toY<0?rows-a.toY:a.toY)-(a.fromY<0?rows-a.fromY:a.fromY);

			Dimension p = c.getPreferredSize();
			
			maxW = Math.max(maxW, (int)(p.getWidth()*cols/colspan));
			maxH = Math.max(maxH, (int)(p.getHeight()*rows/rowspan));
			
		}
			*/
		
		return new Dimension(maxW, maxH);
	}


	
}



