package de.dualuse.commons.awt.dnd;

import java.awt.Point;

public interface DropListener<T> {
	public boolean drop(Point dropLocation, T droptype) throws Exception;
}
