package de.dualuse.commons.swing;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.xml.bind.DatatypeConverter;

public class JGradientButtonBar extends JPanel {
	private static final long serialVersionUID = 1L;

	public static class NSAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public NSAction(String name,String iconResource, int scale) {
			super(name, new ImageIcon(Toolkit.getDefaultToolkit().getImage(iconResource).getScaledInstance(scale, scale, Image.SCALE_DEFAULT)));
		}
		public NSAction(String name,String iconResource) {
			super(name, new ImageIcon(Toolkit.getDefaultToolkit().getImage(iconResource)));
		}
		
		public NSAction(String name, ImageIcon icon) {
			super(name, icon);
		}
		
		public void actionPerformed(ActionEvent e) {
			
		}
	}
	
	
	public static class Add extends NSAction {
		private static final long serialVersionUID = 1L;

		public Add() {
//			super("", "NSImage://NSAddTemplate");
			super(""+(char)0x253C,"");
		}
	}

	public static class Remove extends NSAction {
		private static final long serialVersionUID = 1L;
		public Remove() {
//			super("", "NSImage://NSRemoveTemplate");
			super(""+(char)0x2015, "");
		}
	}
	
	public static class Action extends NSAction {
		private static final long serialVersionUID = 1L;
		public Action() {
			super("", new ScaledImageIcon(0.3f,optionIcon));
		}
	}
	
	
	static final private byte[] optionIcon = DatatypeConverter.parseBase64Binary("iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAQAAAD9CzEMAAAB9ElEQVR4Ae3Wv2sTYRjA8W+0NYX+0KaUJLviUGkHf0BBLZYOUh0sXQT/BhHqkFoHwUV00MHJRR2CIf0jxEJVKLiJKFYXDbkiwWo7WKV97JAD7/I89x72XQL5fKe8eY73COTeo210vEFireBREVEqkMI+0jjsWu1scCT9qts0DapMEspxl01EaZM7DBKapMp3pnHI8rl5+XvmyHGFBmLHNy4zxHU+ND9/IkuiG5HLfyNG9tQ82IpsIHtsgyKmp4iHnmA4yQ7ioR1OoMjwGvHUKxSzSGJfKTFCdrdjzFNzTM/Q4hxbiFmZXv7VRwUx22ICxbh5X2UyxGXMLb5wCkOeJfXH6UXTT12Zfs4w2Lq4j8QqYVlomb3HfpziD4cRLKOxyYBUtpFIWSw9sck/xuPaG0m3wXrq5/5RohruDbp5SI6oi6iUbwo8oAtsRZaRlmr0oRkgUKaXyGM4Qx1Rq6h/tEVjusY4iqnE46VCf+zuFxOPoAlaXEISq7PAKD27jXGTwDF9AcUy4qkXqI57OnC2GcPwGPHQI0x5fiJ7bJ1hsJUiw78QO2NqjkQHWG0OvuUqh5hlDcEuYIZBrvGO8HWtG4fzrFHmNKGD3Dbeln5wiwFCZ3lGwBT/pWocpN7erlft1TbZ4KO96kcBURrCoxUk1ks62sRfY/sENEN3jjcAAAAASUVORK5CYII=");
	public static class ActionOption extends NSAction {
		private static final long serialVersionUID = 1L;
		public ActionOption() {
			super(""+((char)0x25BE), new ScaledImageIcon(0.3f,optionIcon));
//			super(((char)0x25BE)+" ?", "",10);
		}
		
	}

	public static class Play extends NSAction {
		private static final long serialVersionUID = 1L;
		public Play() {
			super(""+(char)0x25B6 , "");
		}
	}

	public static class Stop extends NSAction {
		private static final long serialVersionUID = 1L;
		public Stop() {
			super(""+(char)0x25FC , "");
		}
	}

	public static class Pause extends NSAction {
		private static final long serialVersionUID = 1L;
		public Pause() {
			super(""+(char)0x2759+" "+(char)0x2759 , "");
		}
	}

	
	
	public static class Eject extends NSAction {
		private static final long serialVersionUID = 1L;
		public Eject() {
			super(""+(char)0x23CF , "");
		}
	}
	
	
	
	private AbstractAction emptyAction = new AbstractAction(" ") {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			
		}
	};
	
	public JGradientButtonBar() {
		this.setLayout(new AlignmentLayout(-5,-5,-10, -11));
		
		this.addAction(emptyAction);
	}

	private boolean stealFocus = true;
	public boolean isStealFocus() { return stealFocus; };
	public void setStealFocus(boolean stealFocus) { this.stealFocus = stealFocus; }
	private MouseAdapter focusStealer = new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			if (stealFocus)
				grabFocus();
		}
	};
	
	
	public AbstractButton addAction(javax.swing.Action a) {
		JButton button = new JButton(a);
		return addAction(button);
	}

	public AbstractButton addAction(AbstractButton button) {
		button.setFocusable(false);
		button.addMouseListener(focusStealer);
		button.putClientProperty("JButton.buttonType","gradient");
		
		this.add(button,this.getComponentCount()-1);
		
		revalidate();
		repaint();
		
		return button;
	}
	
	public void removeAction(javax.swing.Action a) {
		for (int i=0;i<getComponentCount();i++)
			if (((JButton)getComponent(i)).getAction().equals(a)) {
				getComponent(i).removeMouseListener(focusStealer);
				this.remove(i);
				break;
			}

		revalidate();
		repaint();
	}
	
	public void removeAction(AbstractButton button) {
		for (int i=0;i<getComponentCount();i++)
			if (getComponent(i)==button) {
				getComponent(i).removeMouseListener(focusStealer);
				this.remove(i);
				break;
			}

		revalidate();
		repaint();
	}
	
	public void removeAllActions() {
		removeAll();
		addAction(emptyAction);
		revalidate();
		repaint();
	}
	
	@Override public void paint(Graphics g) {
		super.paint(g);
		paintBorder(g);
	}

	
	
	
}
