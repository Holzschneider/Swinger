package de.dualuse.commons.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

//XXX make version that extends TexturedBorder
public class FocusBorder implements Border {
	final Color c0, c1, c2, c3;
	final Color c[];
	final boolean simple;
	
	public FocusBorder(Color base) {
		this(base,false);
	}
	
	public FocusBorder(Color base, boolean simple) {
//		c0 = new Color(base.getRed(),base.getGreen(),base.getBlue(),base.getAlpha()*78/100);
//		c1 = new Color(base.getRed(),base.getGreen(),base.getBlue(),base.getAlpha()*62/100);
//		c2 = new Color(base.getRed(),base.getGreen(),base.getBlue(),base.getAlpha()*40/100);
//		c3 = new Color(base.getRed(),base.getGreen(),base.getBlue(),base.getAlpha()*25/100);
		c0 = new Color(base.getRed(),base.getGreen(),base.getBlue(),base.getAlpha()*95/100);
		c1 = new Color(base.getRed(),base.getGreen(),base.getBlue(),base.getAlpha()*65/100);
		c2 = new Color(base.getRed(),base.getGreen(),base.getBlue(),base.getAlpha()*40/100);
		c3 = new Color(base.getRed(),base.getGreen(),base.getBlue(),base.getAlpha()*25/100);
		
		c = new Color[] {c3,c2,c1,c0};
		
		this.simple = simple;
	}
	

	public void paintBorder(Component cmp, Graphics g, int x, int y, int width, int height) {
		if (simple) {
			g.setColor(c3);
			g.drawRect(x, y, width-1, height-1);
			g.setColor(c2);
			g.drawRect(x+1, y+1, width-3, height-3);
			g.setColor(c1);
			g.drawRect(x+2, y+2, width-5, height-5);
			g.setColor(c0);
			g.drawRect(x+3, y+3, width-7, height-7);
		} else {
			
			for (int i=0;i<3;i++) {
				g.setColor(c[i]);
				g.drawLine(x+4, y+i, x+width-1-4, y+i);
				g.drawLine(x+4, y+height-1-i,x+ width-1-4, y+height-1-i);
				g.drawLine(x+i, y+4, x+i, y+height-1-4);
				g.drawLine(x+width-1-i, y+4, x+width-1-i, y+height-1-4);
			}
			
			g.setColor(c3);
			
			g.fillRect(x+1, y+1, 3, 3);
			g.fillRect(x+0, y+2, 2, 2);
			g.fillRect(x+2, y+0, 2, 2);
			g.fillRect(x+2, y+2, 2, 2);
	//		g.fillRect(2, 2, 2, 2);
			
			g.fillRect(x+width-1-3, y+1, 3, 3);
			g.fillRect(x+width-1-3, y+0, 2, 2);
			g.fillRect(x+width-1-1, y+2, 2, 2);
			g.fillRect(x+width-1-3, y+2, 2, 2);
	//		g.fillRect(width-1-3, 2, 2, 2);
			
			g.fillRect(x+width-1-3, y+height-1-3, 3, 3);
			g.fillRect(x+width-1-3, y+height-1-1, 2, 2);
			g.fillRect(x+width-1-1, y+height-1-3, 2, 2);
			g.fillRect(x+width-1-3, y+height-1-3, 2, 2);
	//		g.fillRect(width-1-3, height-1-3, 2, 2);
		
			
			g.fillRect(x+1, y+height-1-3, 3, 3);
			g.fillRect(x+0, y+height-1-3, 2, 2);
			g.fillRect(x+2, y+height-1-1, 2, 2);
			g.fillRect(x+2, y+height-1-3, 2, 2);
		//		g.fillRect(2, height-1-3, 2, 2);
		}
	}
	
	final static private Insets BORDER_INSETS = new Insets(4,4,4,4);

	public Insets getBorderInsets(Component c) {
		return BORDER_INSETS;
	}

	public boolean isBorderOpaque() {
		return false;
	}
	
}
