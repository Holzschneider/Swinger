package de.dualuse.commons.swing;

import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.xml.bind.DatatypeConverter;

public class Base64Image {
	public static void main(String[] args) {
		
		final JFrame frame = new JFrame();
		
		frame.setContentPane(new JComponent() {
			
			BufferedImage bi = null;
			
			{
				setDropTarget(new DropTarget() {
					public synchronized void drop(java.awt.dnd.DropTargetDropEvent dtde) {
						if (!dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
							dtde.rejectDrop();
							return;
						}
						
						dtde.acceptDrop(DnDConstants.ACTION_COPY);
						
						try {
							File droppedFile = (File)((java.util.List<?>)dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor)).get(0);
							dtde.dropComplete(true);
							
							bi = ImageIO.read(droppedFile);
							
							if (bi==null)
								dtde.dropComplete(false);
							else {

								byte[] bytes = new byte[(int)droppedFile.length()];
								RandomAccessFile raf = new RandomAccessFile(droppedFile,"r");
								raf.readFully(bytes);
								raf.close();
								
								String base64Image = DatatypeConverter.printBase64Binary(bytes);
								StringSelection selectedText = new StringSelection(base64Image);
								getToolkit().getSystemClipboard().setContents(selectedText, selectedText);
								
								frame.setTitle(base64Image.length()+" characters");
								
								dtde.dropComplete(true);
							}
								
							repaint();
						} catch (Exception ex) {
							dtde.dropComplete(false);
						}
					};
				});
			}
			
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (bi!=null) {
					g.drawImage(bi, getWidth()/2-bi.getWidth()/2, getHeight()/2-bi.getHeight()/2, this);
				}
			}
			
		});

		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 800, 500);
		frame.setVisible(true);
	}
	
	
}

