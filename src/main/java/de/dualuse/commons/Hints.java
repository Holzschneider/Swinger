package de.dualuse.commons;

import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

public class Hints {

	static public final Map<RenderingHints.Key,Object> SMOOTH = new HashMap<RenderingHints.Key,Object>();
	static {
		SMOOTH.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		SMOOTH.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
	}

	static public final Map<RenderingHints.Key,Object> PLAIN = new HashMap<RenderingHints.Key,Object>();
	static {
		PLAIN.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		PLAIN.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
	}
	
}
