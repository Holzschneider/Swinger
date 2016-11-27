package de.dualuse.commons.awt.dnd;

import java.awt.Component;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;

abstract class AbstractDropTarget<T> extends DropTarget implements DropListener<T>{
	private static final long serialVersionUID = 1L;
	
	private DropListener<T> listener;

	public AbstractDropTarget() { listener = this; }
	public AbstractDropTarget(DropListener<T> listener) { setListener(this); }

	public AbstractDropTarget(Component c) { listener = this; c.setDropTarget(this); }
	public AbstractDropTarget(Component c, DropListener<T> listener) { c.setDropTarget(this); setListener(this); }

	
	public DropListener<T> getListener() { return listener; }
	public void setListener(DropListener<T> listener) {
		if (listener==null) this.listener = this;
		else this.listener = listener; 
	}
	
	
	private T received= null;
	public boolean drop(Point dropLocation, T files) throws Exception {
		this.received = files;
		return true;
	}
	
	public T getDrop() { return received; }
	
	abstract public T unpack(DropTargetDropEvent dtde) throws Exception;
	
	public boolean receive(Point dropLocation, T files) throws Exception { 
		return listener.drop(dropLocation, files); 
	}
	
	protected boolean receive(DropTargetDropEvent dtde) throws Exception {
		return receive(dtde.getLocation(), unpack(dtde));		
	}
	
	@Override final public synchronized void drop(final DropTargetDropEvent dtde) {
		dtde.acceptDrop(DnDConstants.ACTION_LINK);
		try {
			dtde.dropComplete(receive(dtde));
		} catch (Exception e) { 
			dtde.dropComplete(false); 
		}
		getComponent().repaint();
	}
	
}
