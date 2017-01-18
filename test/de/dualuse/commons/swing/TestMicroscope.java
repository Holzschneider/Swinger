package de.dualuse.commons.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

public class TestMicroscope {

	public static void main(String[] args) {
		
		JFrame f = new JFrame();
		f.add(new JMicroscope( Sticky.value(new AffineTransform()) ) {

			private static final long serialVersionUID = 1L;
			
			JKnob a = new JKnob(Sticky.value(new Point2D.Double(500,100)));
			JKnob b = new JKnob(Sticky.value(new Point2D.Double(200,200)));
			
			{
				add(a);
				add(b);
			}
			
			@Override
			public void paintCanvas(Graphics g) {
				Graphics2D gg = (Graphics2D) g.create();
				
				gg.draw(new Line2D.Double(a.getCenterX(),a.getCenterY(),b.getCenterX(),b.getCenterY()));
				
				gg.dispose();
				
				System.out.println(b.center);
			}
		});
		
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBounds(100, 100, 800, 800);
		f.setVisible(true);;
	}
	
}
