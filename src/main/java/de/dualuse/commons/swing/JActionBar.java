package de.dualuse.commons.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JActionBar extends JToolBar {

	private static final long serialVersionUID = 1L;
	
	public JActionBar() {
		this.setOpaque(false);
		this.setFloatable(false);
		this.setBorder(new EmptyBorder(0, 0, 4, 0));
		this.setPreferredSize(new Dimension(64, 55));
		this.addMouseListener(DraggableWindowBackgroundListener.singleton);
		this.addMouseMotionListener(DraggableWindowBackgroundListener.singleton);
	}
	
	public class Separator extends JLabel implements Icon {
		private static final long serialVersionUID = 1L;

		public Separator() {
			setIcon(this);
			JActionBar.this.add(this);
		}
		
		@Override public int getIconHeight() { return 32; }
		@Override public int getIconWidth() { return 24; }

		@Override public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(new Color(0,0,0,.3f));
			g.drawLine(x+getIconWidth()/2, y, x+getIconWidth()/2, y+getIconHeight());
		}
		
	}
	
	public class Button extends JButton implements Icon {
		private static final long serialVersionUID = 1L;

		final Icon original;
		
		public Button(Action a) { 
			super (a);
			setBorder(null);
			
			setBorderPainted(false);
			setHorizontalTextPosition(SwingUtilities.CENTER);
			setVerticalTextPosition(SwingUtilities.BOTTOM);
			JActionBar.this.add(this);
	
			setFocusable(false);
			int W = 80,H=140;
			
			setMinimumSize(new Dimension(W,H));
			setMaximumSize(new Dimension(W,H));
			setPreferredSize(new Dimension(W,H));

	        setContentAreaFilled(false); 
	        setFocusPainted(false); 
	        setOpaque(false);
			
			original = (Icon) a.getValue(Action.SMALL_ICON);
			
			setIcon(this);
			setPressedIcon(this);
			
			this.addChangeListener(new ChangeListener() {
				boolean pressedState = getModel().isPressed();
				@Override public void stateChanged(ChangeEvent e) {
					if (getModel().isPressed() != pressedState) {
						pressedState = getModel().isPressed();
						if (pressedState)
							setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
						else
							setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
					}
				}
			});
		}

		@Override public void paintIcon(Component c, Graphics g, int x, int y) {
			original.paintIcon(c, g, x, y-5);
			
			if (getModel().isPressed()) {
				g.setColor(new Color(0,0,0,.1f));
				g.fillRect(x, y, getIconWidth(), getIconHeight()+5);
			}
		}

		@Override public int getIconWidth() { return original.getIconWidth(); }
		@Override public int getIconHeight() { return original.getIconHeight()-14; }
	}	
	
	
}
