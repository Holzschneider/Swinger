package de.dualuse.commons.swing;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class TestAnimatedImageIcon {
	
	public static void main(String[] args) throws InterruptedException {
		
		BufferedImage a = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		a.createGraphics().fillOval(1, 1, 63, 63);
		
		BufferedImage b = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		b.createGraphics().fillRoundRect(1, 1, 63, 63,10,10);
		
		BufferedImage c = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		c.createGraphics().fillPolygon( new int[] { 31, 63, 1}, new int[] {1,63,63}, 3);

		BufferedImage d = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		d.createGraphics().drawLine(1, 1, 63, 63);
		d.createGraphics().drawLine(1, 63, 63, 1);

		
		
		JFrame f = new JFrame();

		AnimatedImageIcon aii = new AnimatedImageIcon(a, 0);
		
		f.setContentPane( new JLabel(aii) );
		f.setBounds(600, 400, 100, 100);
		f.setVisible(true);
		
		
		Thread.sleep(3000);
		aii.setImage(b,1);

		Thread.sleep(3000);
		aii.setImage(c,1);

		Thread.sleep(3000);
		aii.setImage(d,1);
		
	}
}
