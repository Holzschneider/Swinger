package de.dualuse.commons.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.net.URL;

import javax.swing.ImageIcon;

public class ScaledImageIcon extends ImageIcon {
	private static final long serialVersionUID = 1L;

	final double scaleFactor;
	public ScaledImageIcon(double scaleFactor, byte[] data) {
		super(data);
		this.scaleFactor = scaleFactor;
	}

	public ScaledImageIcon(double scaleFactor, Image data) {
		super(data);
		this.scaleFactor = scaleFactor;
	}

	public ScaledImageIcon(double scaleFactor, String data) {
		super(data);
		this.scaleFactor = scaleFactor;
	}

	public ScaledImageIcon(double scaleFactor, URL data) {
		super(data);
		this.scaleFactor = scaleFactor;
	}

	public ScaledImageIcon(double scaleFactor, byte[] data, String description) {
		super(data, description);
		this.scaleFactor = scaleFactor;
	}

	public ScaledImageIcon(double scaleFactor, Image data, String description) {
		super(data, description);
		this.scaleFactor = scaleFactor;
	}

	public ScaledImageIcon(double scaleFactor, String data, String description) {
		super(data, description);
		this.scaleFactor = scaleFactor;
	}

	public ScaledImageIcon(double scaleFactor, URL data, String description) {
		super(data, description);
		this.scaleFactor = scaleFactor;
	}


	
	@Override
	public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
		
		Graphics2D g2 = (Graphics2D)g.create();
		g2.scale(scaleFactor, scaleFactor);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		super.paintIcon(c, g2, (int)(x/scaleFactor), (int)(y/scaleFactor));
		g2.dispose();
	}

	
	
	@Override
	public int getIconHeight() {
		return (int)(super.getIconHeight()*scaleFactor);
	}
	
	@Override
	public int getIconWidth() {
		return (int)(super.getIconWidth()*scaleFactor);
	}
}