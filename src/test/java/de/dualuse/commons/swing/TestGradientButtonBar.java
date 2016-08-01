package de.dualuse.commons.swing;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import de.dualuse.commons.swing.JGradientButtonBar.ActionOption;
import de.dualuse.commons.swing.JGradientButtonBar.Add;
import de.dualuse.commons.swing.JGradientButtonBar.Eject;
import de.dualuse.commons.swing.JGradientButtonBar.Pause;
import de.dualuse.commons.swing.JGradientButtonBar.Play;
import de.dualuse.commons.swing.JGradientButtonBar.Remove;
import de.dualuse.commons.swing.JGradientButtonBar.Stop;

public class TestGradientButtonBar {

	public static void main(String[] args) {
		
		JFrame f = new JFrame();
		
		final JGradientButtonBar gbb = new JGradientButtonBar();
		
		gbb.addAction(new Add() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				System.out.println("hallo");
			}
		});
		
		gbb.addAction(new Remove() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				System.out.println("welt");
			}
		});
		
//		new ImageIcon(Toolkit.getDefaultToolkit().getImage("NSImage://NSActionTemplate"));
		
		
		final AbstractAction aa = new ActionOption() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				System.out.println("test");
			}
		};
		
		gbb.addAction(aa);
		
		gbb.addAction(new Play() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				gbb.removeAction(aa);
			}
		});

		gbb.addAction(new Pause() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				gbb.removeAction(aa);
			}
		});

		gbb.addAction(new Stop() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				gbb.removeAction(aa);
			}
		});

		gbb.addAction(new Eject() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				gbb.removeAction(aa);
			}
		});
		
//		gbb.setBorder(new LineBorder(Color.BLUE,10));
//		gbb.addAction(new Abstract);

		f.getContentPane().setBackground(Color.WHITE);
//		f.getContentPane().setLayout(new FlowLayout());
		f.getContentPane().add(gbb);
		
		f.setBounds(100,100,400,100);
		
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
	}
}
