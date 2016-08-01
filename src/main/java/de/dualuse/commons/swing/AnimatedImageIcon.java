package de.dualuse.commons.swing;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class AnimatedImageIcon extends ImageIcon {

	private static final long serialVersionUID = 1L;
	
	public ArrayList<WeakReference<Component>> consumers = new ArrayList<WeakReference<Component>>();
	
	private long updateTimestamp = 0, setTimestamp = 0;
	public synchronized void setImage(Image image) {
		super.setImage(image);
		updateTimestamp = setTimestamp = 0;
		for (int i=consumers.size()-1;i>=0;i--) {
			Component c = consumers.remove(i).get();
			if (c!=null)
				c.repaint();
		}
	}
	
	private Image newImage = null, oldImage = null;
	public synchronized void setImage(Image image, double fadeSeconds) {
		oldImage = getImage();
		super.setImage(image);
		newImage = image;
		updateTimestamp = (setTimestamp = System.nanoTime())+(long)(fadeSeconds*1e9);
	
		for (int i=consumers.size()-1;i>=0;i--) {
			Component c = consumers.remove(i).get();
			if (c!=null)
				c.repaint();
		}
	}
	
	
	public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
		long now = System.nanoTime();

		long delta = updateTimestamp-now;
		
		if (delta>0) {
			float fade = (float)((double)delta/(updateTimestamp-setTimestamp));
			
			g = g.create();
			((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fade));
			g.drawImage(oldImage, x, y, null);
			
			((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1-fade));
			g.drawImage(newImage, x, y, null);
			
			c.repaint();
		} else {
			newImage = oldImage = null;
			super.paintIcon(c, g, x, y);
		}
		
		for (int i=0,l=consumers.size();i<l;i++)
			if (consumers.get(i).get()==c)
				break;
		
		consumers.add(new WeakReference<Component>(c));
	}
	
	
	public AnimatedImageIcon(byte[] imageData) { this(imageData,0); }
	public AnimatedImageIcon(byte[] imageData, int fadeSeconds) { 
		super(imageData);
		oldImage = null;
		newImage = getImage();
		updateTimestamp = (setTimestamp = System.nanoTime())+(long)(fadeSeconds*1e9);
	}
	
	public AnimatedImageIcon(Image image) { this(image,0); }
	public AnimatedImageIcon(Image image, double fadeSeconds) { 
		super(image);
		oldImage = null;
		newImage = image;
		updateTimestamp = (setTimestamp = System.nanoTime())+(long)(fadeSeconds*1e9);
	}
	
	public AnimatedImageIcon(String filename) { this(filename, 0); }
	public AnimatedImageIcon(String filename, double fadeSeconds) { 
		super(filename);
		oldImage = null;
		newImage = getImage();
		updateTimestamp = (setTimestamp = System.nanoTime())+(long)(fadeSeconds*1e9);
	}
	
	public AnimatedImageIcon(URL location) { this(location, 0); }
	public AnimatedImageIcon(URL location, double fadeSeconds) { 
		super(location);
		oldImage = null;
		newImage = getImage();
		updateTimestamp = (setTimestamp = System.nanoTime())+(long)(fadeSeconds*1e9);
	}
	
}
