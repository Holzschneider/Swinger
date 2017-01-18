package de.dualuse.commons.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class OnTopOfEachOtherLayout implements LayoutManager {
	public void removeLayoutComponent(Component comp) { }
	
	
	public Dimension preferredLayoutSize(Container parent) {
		
		Dimension maxPreferred = new Dimension(1,1);
		for (int i=0,l=parent.getComponentCount();i<l;i++) {
			Dimension preferred = parent.getComponent(i).getPreferredSize();
			
			maxPreferred.width = Math.max(maxPreferred.width,preferred.width);
			maxPreferred.height = Math.max(maxPreferred.height,preferred.height);
		}

		return maxPreferred;
	}
	
	public Dimension minimumLayoutSize(Container parent) { return parent.getMinimumSize(); }
	public void addLayoutComponent(String name, Component comp) { }
	public void layoutContainer(Container parent) { 
		for (int i=0,l=parent.getComponentCount();i<l;i++)
			parent.getComponent(i).setBounds(0, 0, parent.getWidth(), parent.getHeight());
	}
}