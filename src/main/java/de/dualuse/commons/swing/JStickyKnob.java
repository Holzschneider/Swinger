package de.dualuse.commons.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.prefs.Preferences;

@Deprecated
public class JStickyKnob extends JKnob {

	private static final long serialVersionUID = 1L;

	private static final String CENTER_X = ".centerX";
	private static final String CENTER_Y = ".centerY";
	
	
	final Preferences node;
	final String name, keyCenterX, keyCenterY;
	final double defaultCenterX, defaultCenterY;
	
	static int counter = 0;
	
	public JStickyKnob(double centerX, double centerY) {
		this("MemoryKnob"+(counter++),centerX,centerY);
	}
	public static Preferences nodeForMainClass () {
		Class<?> clazz = new LinkedList<Class<?>>(Arrays.asList(new SecurityManager() { protected Class<?>[] getClassContext() { return super.getClassContext(); } }.getClassContext())).getLast();
		Preferences prefs = Preferences.userNodeForPackage(clazz);
		return prefs.node(clazz.getName());
	}	

	public JStickyKnob(String name, double centerX, double centerY) {
		this(
				nodeForMainClass(), 
				name, 
				centerX, 
				centerY
			);
	}
	
	public JStickyKnob(Preferences node, String name, double centerX, double centerY) {
		this.node = node;
		this.name = name;
		this.keyCenterX = name+CENTER_X;
		this.keyCenterY = name+CENTER_Y;
		
		this.defaultCenterX = centerX;
		this.defaultCenterY = centerY;
		
		
		super.setCenter(node.getDouble(keyCenterX,centerX), node.getDouble(keyCenterY,centerY));		
	}
	
	public double getDefaultCenterX() { return defaultCenterX; }
	
	public double getDefaultCenterY() { return defaultCenterY; }
	
	public void reset() { setCenter(defaultCenterX, defaultCenterY); }
	
	MouseListener resetListener = new MouseAdapter() {
		{ addMouseListener(this); }
		
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getClickCount()==2)
				setCenter(defaultCenterX, defaultCenterY);
		};
	}; 
	
	Listener movementListener = new Listener() {
		{ addKnobListener(this); }
		
		public void knobMoved(JKnob k, double dx, double dy) {
			node.putDouble(keyCenterX, getCenterX());
			node.putDouble(keyCenterY, getCenterY());
		}
	};
	
}