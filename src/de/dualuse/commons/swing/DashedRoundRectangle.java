package de.dualuse.commons.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JFrame;

public class DashedRoundRectangle implements Icon {
	public int width, height, round;
	public String text;
	
	public DashedRoundRectangle(int width, int height, int round, String text) {
		this.width  = width;
		this.height = height;
		this.round = round;
		this.text = text;
	}
	
	public DashedRoundRectangle(int size) {
		this(size, size, size/10, null);
	}
	
	@Override public void paintIcon(Component c, Graphics g, int x, int y) {
		
		int D = 4, M = 2*D;
		Graphics2D g2 = (Graphics2D)g.create();
		g2.setStroke(new BasicStroke(D, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1, new float[] { 15, 5f }, 0));
		g2.setColor(new Color(0,0,0,.3f));
		g2.drawRoundRect(x+M, y+M, width-2*M, height-2*M, round, round);
		
		if (text!=null) g2.drawString(text, x+width/2-g2.getFontMetrics().stringWidth(text)/2, y+height/2+g2.getFontMetrics().getHeight()/3);
		g2.dispose();
		
	}

	@Override public int getIconWidth() { return width; }
	@Override public int getIconHeight() { return height; }
	
	
	public static void main(String[] args) {
		
		JFrame f = new JFrame();
		f.setContentPane(new JLabel(new DashedRoundRectangle(128)));
		f.setBounds(200, 100, 800, 800);
		f.setVisible(true);
	}
}
