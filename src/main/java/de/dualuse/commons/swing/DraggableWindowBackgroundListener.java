package de.dualuse.commons.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseAdapter;

import javax.swing.JComponent;

public class DraggableWindowBackgroundListener extends MouseAdapter {
	public final static DraggableWindowBackgroundListener singleton = new DraggableWindowBackgroundListener();
	
	private DraggableWindowBackgroundListener() {
	}
	
	
	int startX, startY;
	int windowX, windowY;
	Window captured; 
	
	public void mousePressed(java.awt.event.MouseEvent e) {
		startX = e.getXOnScreen();
		startY = e.getYOnScreen();
		
		Object src = e.getSource();
		if (!(src instanceof JComponent))
			return;
		
		JComponent srcComponent = (JComponent) src;
		Container srcContainer = srcComponent.getRootPane().getParent();
		
		if (!(srcContainer instanceof Window))
			return;
		
		captured = (Window) srcContainer;
			
		Point p = captured.getLocation();
		windowX = p.x;
		windowY = p.y;
		
	};
	

	public void mouseReleased(java.awt.event.MouseEvent e) {
		captured = null;
	};
	
	public void mouseDragged(java.awt.event.MouseEvent e) {
		Window w = captured;
		if (w==null)
			return;

		int newX = windowX+(e.getXOnScreen()-startX), newY = windowY+(e.getYOnScreen()-startY);
		
		w.setLocation(newX, newY);
	};
	
}
