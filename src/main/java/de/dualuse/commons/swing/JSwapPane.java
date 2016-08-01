package de.dualuse.commons.swing;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class JSwapPane extends JSwitchPane {
	
	public JSwapPane() {
		setTransitionAnimationDuration(1.0);
	}
	
	private static final long serialVersionUID = 1L;
	protected void paintTransition(Graphics gOld, BufferedImage fadeOut, BufferedImage fadeIn, float fade, int deltaIndex) {
		gOld.setColor(Color.BLACK);
		gOld.fillRect(0, 0, getWidth(), getHeight());
		
		Graphics2D f = ((Graphics2D)gOld.create());
		Graphics2D b = ((Graphics2D)gOld.create());
		
		double sp = fade;
		
		double dir = deltaIndex>0?1:-1;
		double tf = dir*0.5*(Math.sin(sp*Math.PI*2-Math.PI/2)+1)*-getWidth()*4/7, tb = -tf;
		double maxS  = 0.2;
		double sf = 1-maxS*(Math.sin(sp*Math.PI-Math.PI/2)+1), sb = 1-maxS*(Math.sin((1-sp)*Math.PI-Math.PI/2)+1);

		b.translate(getWidth()/2, getHeight()/2);
		b.scale(sb, sb);
		b.translate(-getWidth()/2, -getHeight()/2);
		b.translate(tb, 0);

		f.translate(getWidth()/2, getHeight()/2);
		f.scale(sf, sf);
		f.translate(-getWidth()/2, -getHeight()/2);
		f.translate(tf, 0);

		b.setClip(b.getClipBounds().intersection(new Rectangle(0, 0, getWidth(), getHeight()+getHeight()/3)));
		f.setClip(f.getClipBounds().intersection(new Rectangle(0, 0, getWidth(), getHeight()+getHeight()/3)));
		
		int d = getHeight()/3;
		if (sp>0.5) {
			f.drawImage(fadeOut, 0,0, this);
			f.drawImage(fadeOut, 0, getHeight(), fadeOut.getWidth(), getHeight()+d,   0, getHeight(), fadeOut.getWidth(), getHeight()-d, this);
			f.setPaint(new GradientPaint(0, getHeight(), new Color(0,0,0,0.5f), 0, getHeight()+d, new Color(0,0,0,1f)));
			f.fillRect(0, getHeight(), getWidth()+2, d);
			
			b.drawImage(fadeIn, 0,0, this);
			b.drawImage(fadeIn, 0, getHeight(), fadeIn.getWidth(), getHeight()+d,   0, getHeight(), fadeIn.getWidth(), getHeight()-d, this);
			b.setPaint(new GradientPaint(0, getHeight(), new Color(0,0,0,0.5f), 0, getHeight()+d, new Color(0,0,0,1f)));
			b.fillRect(0, getHeight(), getWidth()+2, d);
		} else {
			b.drawImage(fadeIn, 0,0, this);
			b.drawImage(fadeIn, 0, getHeight(), fadeIn.getWidth(), getHeight()+d,   0, getHeight(), fadeIn.getWidth(), getHeight()-d, this);
			b.setPaint(new GradientPaint(0, getHeight(), new Color(0,0,0,0.5f), 0, getHeight()+d, new Color(0,0,0,1f)));
			b.fillRect(0, getHeight(), getWidth()+2, d);

			f.drawImage(fadeOut, 0,0, this);
			f.drawImage(fadeOut, 0, getHeight(), fadeOut.getWidth(), getHeight()+d,   0, getHeight(), fadeOut.getWidth(), getHeight()-d, this);
			f.setPaint(new GradientPaint(0, getHeight(), new Color(0,0,0,0.5f), 0, getHeight()+d, new Color(0,0,0,1f)));
			f.fillRect(0, getHeight(), getWidth()+2, d);
		}
		
	};

}

