package de.dualuse.commons.swing;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

import de.dualuse.commons.swing.SwapListLayout.SwapListListener;


public class JSwapList extends JPanel {

	private static final long serialVersionUID = 1L;
	SwapListLayout lm = new SwapListLayout();
	
	public JSwapList() {
		setLayout(lm);
	}
	

	public static class ColorBox extends JComponent {
		private static final long serialVersionUID = 1L;
		int number = counter++;
		float hue = (float) Math.random();

		{
			Dimension dim = new Dimension(500, (int) (20 + hue * 100));
			setPreferredSize(dim);
		}
		
		public ColorBox() {
		}
		
		public ColorBox(int size) {
			Dimension dim = new Dimension(500, (int) (20 + size));
			setPreferredSize(dim);
		}
		
		@Override
		public String toString() {
			return "ColorBox "+number;
		}

		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(new Color(Color.HSBtoRGB(hue, 0.6f, 0.8f)));
			g.fillRect(0, 0, getWidth(), getHeight());
			
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}

	static int counter =0 ;
	public static void main(String[] args) throws IOException {
		final SwapListLayout sll = new SwapListLayout();
		
		final JLabel statusLabel = new JLabel(" ");
		sll.addSwapListListener(new SwapListListener() {
			@Override
			public void listFloating(Component[] floatingOrder) {
//				System.out.println("floating: "+Arrays.asList(floatingOrder));
				statusLabel.setText("floating: "+Arrays.asList(floatingOrder));
			}
			
			@Override
			public void listCameToRest(Component[] order) {
//				System.out.println("rest: "+Arrays.asList(ordering));
				statusLabel.setText("resting: "+Arrays.asList(order));
			}
		});
		
		final JPanel sl = new JPanel(sll);

		// sl.setBorder(new BevelBorder(BevelBorder.LOWERED));
		sl.setBorder(new MatteBorder(10, 10, 10, 10, Color.orange));

		
		

		// sl.add(new JButton("hallo"));
		// sl.add()


		sl.add(new ColorBox());
		sl.add(new ColorBox());
		
		sl.add(new ColorBox());
		sl.add(new ColorBox());
		sl.add(new ColorBox());
		sl.add(new ColorBox());

		final ColorBox cb = new ColorBox();
		sl.add(cb);
		

		JFrame f = new JFrame();

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		f.setBounds(500, 200, 800, 700);

		f.getContentPane().add(statusLabel,BorderLayout.NORTH);
		f.getContentPane().add(new JScrollPane(sl));

		f.getContentPane().add(new JButton(new AbstractAction() {
			int frame = 0;
			public void actionPerformed(ActionEvent e) {
//				if (frame++ % 2 == 0)
//					sl.add(new ColorBox(), SwapListLayout.APPEND);
//				else
//					sl.add(new ColorBox(), SwapListLayout.INSERT);
				sl.setComponentZOrder(cb, sl.getComponentCount()-1);
				
				cb.setPreferredSize(new Dimension(500,frame++%2==0?50:200));
				sl.revalidate();
				sl.repaint();
				
//				sll.setDraggable(false);
//				new Thread() {
//					public void run() {
//						
//						sll.setEnabled(false);
//						int steps = 20;
//						int dpy = 100;
//						
//						sl.setComponentZOrder(cb, 0);
//						for (int i=0,py=cb.getY();Math.abs(py-dpy)>2;i++) {
//							py = cb.getY();
//							int dy = (dpy-py)/steps+(int)Math.signum(dpy-py);
//							cb.setLocation(cb.getParent().getInsets().left, py+dy);
//							try {
//								Thread.sleep(10);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//						}
//
//						sll.setEnabled(true);
//						sl.revalidate();
//						
//					}
//				}.start();
//				
//
//				sl.revalidate();
				
			}
		}),BorderLayout.SOUTH);
		
		f.setVisible(true);
//		int frame = 0;
//		while (System.in.read() != 'x') {
//
//			if (frame++ % 2 == 0)
//				sl.add(new ColorBox(), SwapListLayout.APPEND);
//			else
//				sl.add(new ColorBox(), SwapListLayout.INSERT);
//
//			sl.revalidate();
//		}
	}

}
