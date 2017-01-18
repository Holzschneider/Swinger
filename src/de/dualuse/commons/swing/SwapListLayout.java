package de.dualuse.commons.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class SwapListLayout implements LayoutManager2 {
	
	public static interface SwapListListener {
		void listFloating(Component[] floatingOrder);
		void listCameToRest(Component[] order);
	}
	
	protected final CopyOnWriteArrayList<SwapListListener> slls = new CopyOnWriteArrayList<SwapListLayout.SwapListListener>();
	public void addSwapListListener(SwapListListener sll) { slls.add(sll); }
	public void removeSwapListListener(SwapListListener sll) { slls.remove(sll); }
	
	
	
	protected boolean enabled = true;
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	static public Timer scheduler = new Timer(true);
	
	static public Component interactiveComponent = null;
	
	protected final int fixedHeight, gap;
	
	protected double flipRidge = 0.0;
	
	public double getFlipRidge() { return flipRidge; }
	public void setFlipRidge(double flipRidge) { this.flipRidge = flipRidge; }
	
	public SwapListLayout(int gap, int fixedHeight) { this.fixedHeight=fixedHeight; this.gap=gap; }
	public SwapListLayout(int gap) { this(gap,-1); }
	public SwapListLayout() {
		this(0, -1);
	}
	
	protected Comparator<Component> ridgeComparator = new Comparator<Component>() {
		@Override
		public int compare(Component o1, Component o2) {
			return (int)(o1.getY()+o1.getHeight()*flipRidge)-(int)(o2.getY()+o2.getHeight()*flipRidge);
		}
	};
	
	int steps = 20;
	public void layoutContainer(final Container parent) {
		if (!enabled)
			return;
		
		if (parent.getComponentCount()==0)
			return;
		
		Insets in = parent.getInsets();

		LinkedList<Component> appendComponents = new LinkedList<Component>();
			
		int totalHeight = in.top;
		int width = parent.getWidth()-in.left-in.right;
		for (int i=0,l=parent.getComponentCount();i<l;i++) {

			final Component c = parent.getComponent(i);

			if (!c.isVisible())
 				continue;

			boolean found = false;
			for (MouseListener ml: c.getMouseListeners())
				if (ml==ma)
					found = true;
			
			if (!found) {
				c.addMouseListener(ma);
				c.addMouseMotionListener(ma);
			}
				
 			final int height = fixedHeight==-1?c.getPreferredSize().height:fixedHeight;
			
			
			if (insertionConstraints.containsKey(c)) {

				Integer constraint = insertionConstraints.remove(c);
				
				if (constraint==null) 
					appendComponents.add(c);
				else
					c.setLocation(in.left, constraint+in.top-1);
				
			} else
				totalHeight += height+gap;
			
			c.setSize( width, height);

		}
		
		for (Component c: appendComponents) {
			if (!c.isVisible())
 				continue;

			c.setLocation(in.left, totalHeight);
			totalHeight += c.getHeight()+gap;
		}
			
		Component[] components = parent.getComponents();
		Arrays.sort(components, ridgeComparator);
		
		boolean needsMoreFix = false;
		Point dp = new Point(in.left,in.top);
		for (int i=0;i<components.length;i++) {
			if (!components[i].isVisible())
 				continue;

			Point p = components[i].getLocation();

			if (Math.abs(p.y-dp.y)>2) {
				needsMoreFix = true;
				if (components[i]!=interactiveComponent)
					components[i].setLocation(in.left, p.y+(dp.y-p.y)/steps+(int)Math.signum(dp.y-p.y));
			} else
				if (components[i]!=interactiveComponent)
					components[i].setLocation(in.left, dp.y);
				
			dp.y += components[i].getHeight()+gap;
		}
		

		parent.repaint();
		
		if (needsMoreFix || interactiveComponent!=null) {
			for (SwapListListener sll: slls)
				sll.listFloating(components);
			
			scheduler.schedule(new TimerTask() {
				@Override
				public void run() {
					parent.doLayout();		
				}
			}, 10);
		} else {
			for (SwapListListener sll: slls)
				sll.listCameToRest(components);
		}
	}
	
	

	public Dimension minimumLayoutSize(Container parent) {
		Insets in = parent.getInsets();
		
		int sumY = in.top, maxWidth=0;
		for (int i=0,l=parent.getComponentCount();i<l;i++) {
			final Component c = parent.getComponent(i);

			if (!c.isVisible())
 				continue;
 				
 			Dimension minimum = c.getMinimumSize();
 			final int height = fixedHeight==-1?minimum.height:fixedHeight;
 			
 			maxWidth = Math.max(minimum.width,maxWidth);
 			sumY += height+(i<l-1?gap:0);
		}
		
		sumY += in.bottom; 
		return new Dimension(maxWidth,sumY);
	}

	public Dimension preferredLayoutSize(Container parent) {
		Insets in = parent.getInsets();
		
		int sumY = in.top, maxWidth=0;
		for (int i=0,l=parent.getComponentCount();i<l;i++) {
 			final Component c = parent.getComponent(i);
 			
 			if (!c.isVisible())
 				continue;
 				
 			Dimension preferred = c.getPreferredSize();
 			final int height = fixedHeight==-1?preferred.height:fixedHeight;
 			
 			maxWidth = Math.max(preferred.width,maxWidth);
 			sumY += height+(i<l-1?gap:0);
		}
		
		sumY += in.bottom; 
		return new Dimension(maxWidth,sumY);
	}
	
	protected HashMap<Component, Integer> insertionConstraints = new HashMap<Component, Integer>();
	
	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		comp.addMouseListener(ma);
		comp.addMouseMotionListener(ma);
		
		if (constraints == null)
			insertionConstraints.put(comp, null);
		else
			insertionConstraints.put(comp, (int) Double.parseDouble(constraints.toString()) );
		
	}
	
	
	@Override
	public void addLayoutComponent(String name, Component comp) {
		comp.addMouseListener(ma);
		comp.addMouseMotionListener(ma);
	}
	
	
	@Override
	public void removeLayoutComponent(Component comp) {

		comp.removeMouseListener(ma);
		comp.removeMouseMotionListener(ma);
		
	}
	
	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0;
	}
	
	@Override
	public void invalidateLayout(Container target) {
		
	}
	
	@Override
	public Dimension maximumLayoutSize(Container target) {
		return null;
	}

	
	private boolean draggable = true;
	public void setDraggable(boolean draggable) {
		this.draggable = draggable;
	}
	
	
	public boolean isDraggable() {
		return draggable;
	}
	
	
	protected MouseAdapter ma = new MouseAdapter() {
		Point grab;

		@Override
		public void mousePressed(MouseEvent e) {
			if (!isDraggable())
				return;
			
			if (e.getButton()!=1)
				return;
			

			interactiveComponent = (Component) e.getSource();
			grab = e.getLocationOnScreen();
			
			interactiveComponent.getParent().setComponentZOrder(interactiveComponent, 0);

			interactiveComponent.getParent().doLayout();
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if (!isDraggable())
				return;
			if (e.getButton()!=1)
				return;
			
			if (interactiveComponent==null)
				return;
			
			Point to = e.getLocationOnScreen();
			
			interactiveComponent.setLocation(interactiveComponent.getX(), interactiveComponent.getY()+(to.y-grab.y));
			
			Component parent =  interactiveComponent.getParent();
			parent.repaint();
			grab = to;

		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if (!isDraggable())
				return;
			if (e.getButton()!=1)
				return;

			if (interactiveComponent==null)
				return;
			
			interactiveComponent.getParent().doLayout();
			interactiveComponent = null;
		}

	};
	
	static public void releaseDraggedComponent() {
		if (interactiveComponent==null)
			return;
			
		interactiveComponent.getParent().doLayout();
		interactiveComponent = null;
	}
	
	
	final static Integer APPEND = null;
	final static Integer INSERT = -0;
	
	
}
