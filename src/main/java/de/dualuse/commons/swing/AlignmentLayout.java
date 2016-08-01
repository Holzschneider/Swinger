package de.dualuse.commons.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

public class AlignmentLayout implements LayoutManager2 {

	final int offX, offY;
	final int overX, overY;
	
	int padX = 3;
	
	public AlignmentLayout(int offsetX, int offsetY, int overlapX, int overlapY) {
		offX = offsetX;
		offY = offsetY;
		
		overX = overlapX;
		overY = overlapY;
	}
	
	@Override public void addLayoutComponent(String name, Component comp) {}

	@Override public void removeLayoutComponent(Component comp) {}
	
	
	@Override public void layoutContainer(Container parent) {
		Insets in = parent.getInsets(); 
		
		Dimension pref = preferredLayoutSize(parent);
		int commonHeight = Math.min(parent.getHeight(),pref.height);
		
		int sumX = offX;
		for (int i=0,count=parent.getComponentCount(),last=count-1;i<count;i++) {
			Component c = parent.getComponent(i);
			Dimension compPref = c.getPreferredSize();
			
			c.setBounds(
				in.left+sumX,
				in.top+offY,
				i==last?(parent.getWidth()-sumX-overX-in.left-in.right):(compPref.width+padX),
				commonHeight-overY-in.bottom-in.top
			);
			
			sumX += compPref.width+padX+overX;
		}
	}

	@Override public Dimension minimumLayoutSize(Container parent) {
		Insets in = parent.getInsets(); 
		Dimension min = new Dimension();
		
		for (int i=0;i<parent.getComponentCount();i++) {
			Component c = parent.getComponent(i);
			Dimension compMin = c.getMinimumSize();
			min.setSize(
				min.width+Math.max(0,compMin.width),
				Math.max(min.height,compMin.height+overY)
			);
		}
		
		min.setSize(min.width+in.left+in.right, min.height+in.top+in.bottom);
		
		return min;
	}

	@Override public Dimension preferredLayoutSize(Container parent) {
		Insets in = parent.getInsets(); 
		Dimension pref = new Dimension();
		
		for (int i=0,last=parent.getComponentCount()-1;i<parent.getComponentCount();i++) {
			Component c = parent.getComponent(i);
			Dimension compPref = c.getPreferredSize();
			
			pref.setSize(
				(i==last)?pref.width:pref.width+Math.max(0,compPref.width+overX+padX),
				Math.max(pref.height,compPref.height+overY)
			);
		}
		
		pref.setSize(pref.width+in.left+in.right, pref.height+in.top+in.bottom);
		
		return pref;
	}

	@Override public void addLayoutComponent(Component comp, Object constraints) {}

	@Override public Dimension maximumLayoutSize(Container target) {
		return new Dimension(2000,2000);
	}

	@Override public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	@Override public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	@Override public void invalidateLayout(Container target) {}	
}
