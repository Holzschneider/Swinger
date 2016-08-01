package de.dualuse.commons.swing;

import java.awt.Color;

public interface ScatterPlot3D {
	static public interface DataPoints {
		int n();
		
		float x(int i);
		float y(int i);
		float z(int i);
		float r(int i);
		Color c(int i);
		boolean v(int i);
		
	}
}