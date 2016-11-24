package de.dualuse.commons.awt.dnd;

import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.swing.JComponent;


public class ImageDropTarget extends FileDropTarget {
	static final FileFilter IMAGE_FILE_FILTER = new FileFilter() {
		@Override public boolean accept(File pathname) {
			return true;
		}
	};
	
	
	private static final long serialVersionUID = 1L;
	
	public ImageDropTarget() { super(IMAGE_FILE_FILTER); }
	public ImageDropTarget(JComponent dropzone) { super(IMAGE_FILE_FILTER, dropzone); }
	

	@Override protected boolean receive(DropTargetDropEvent dtde) throws Exception {
		Collection<DataFlavor> flavors = Arrays.asList(dtde.getTransferable().getTransferDataFlavors());
		if (flavors.contains(DataFlavor.javaFileListFlavor))
			return super.receive(dtde);
		else
		if (flavors.contains(DataFlavor.imageFlavor))
			return receive(dtde.getLocation(), (Image)dtde.getTransferable().getTransferData(DataFlavor.imageFlavor));
		
		return false;	
	}
	
	@Override public boolean receive(Point dropLocation, File file) throws Exception {
		return receive(dropLocation,ImageIO.read(file));
	}
	
	final @Override public boolean receive(Point dropLocation, File... files) throws Exception {
		return super.receive(dropLocation, files);
	}
	
	public boolean receive(Point dropLocation, Image dropped) throws Exception { return true; }
	
	
}
