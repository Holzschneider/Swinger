package de.dualuse.commons.awt.dnd;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

public abstract class FileDropTarget extends DropTarget {
	private static final long serialVersionUID = 1L;
	
	static FileFilter DEFAULT_FILTER = new FileFilter() { public boolean accept(File pathname) { return true; } };
	
	final FileFilter filter;
	
	public FileDropTarget() { this(DEFAULT_FILTER); }
	public FileDropTarget(FileFilter f) { this.filter = f; }
	public FileDropTarget(final String nameFilterRegExp) { this(new FileFilter() { public boolean accept(File pathname) { return pathname.getName().matches(nameFilterRegExp); } }); }
	

	public FileDropTarget(JComponent target) { this(DEFAULT_FILTER); target.setDropTarget(this); }
	public FileDropTarget(FileFilter f, JComponent target) { this.filter = f; target.setDropTarget(this); }
	public FileDropTarget(final String nameFilterRegExp, JComponent target) { this(new FileFilter() { public boolean accept(File pathname) { return pathname.getName().matches(nameFilterRegExp); } }, target); }

	
	public abstract boolean receive(Point dropLocation, File... files) throws Exception;
	
	protected boolean receive(DropTargetDropEvent dtde) throws Exception {
		List<?> files = ((List<?>)dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
		ArrayList<File> acceptedFiles = new ArrayList<File>(files.size());
		
		for (Object o: files) {
			File file = new File(o.toString());
			if (filter.accept(file))
				acceptedFiles.add(file);
		}
		
		if (acceptedFiles.isEmpty()) return false;
		return receive(dtde.getLocation(), acceptedFiles.toArray(new File[acceptedFiles.size()]));		
	}
	
	@Override final public synchronized void drop(final DropTargetDropEvent dtde) {
		dtde.acceptDrop(DnDConstants.ACTION_LINK);
		try {
			dtde.dropComplete(receive(dtde));
		} catch (Exception e) { e.printStackTrace(); dtde.dropComplete(false); }
	}

}
