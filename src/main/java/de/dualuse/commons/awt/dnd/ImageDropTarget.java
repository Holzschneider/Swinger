package de.dualuse.commons.awt.dnd;

import java.awt.Component;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import javax.imageio.ImageIO;


public abstract class ImageDropTarget extends AbstractDropTarget<Image> {
	private static final long serialVersionUID = 1L;
	
	public ImageDropTarget() { }
	public ImageDropTarget(DropListener<Image> l) { setListener(l); }

	public ImageDropTarget(Component c) { super(c); }
	public ImageDropTarget(Component c, DropListener<Image> l) { super(c); setListener(l); }
	
	@Override
	public Image unpack(DropTargetDropEvent dtde) throws Exception {
		Collection<DataFlavor> flavors = Arrays.asList(dtde.getTransferable().getTransferDataFlavors());
		
		if (flavors.contains(DataFlavor.imageFlavor)) // try to unpack as Image first
			return (Image)dtde.getTransferable().getTransferData(DataFlavor.imageFlavor);
		
		return ImageIO.read( new File(((java.util.List<?>)dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor)).get(0).toString()) );
	}
	
}
