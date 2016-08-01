package de.dualuse.commons.swing;

import static java.lang.Math.min;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.border.Border;

public class DropShadowBorder implements Border {

//	private static int S = 30, R = (int)(S*1.85);//R*2/3;
//	private static double sigma = R/3.5, s = .80, shift = -0.8;
//	private static double ooSigmaSigma = 1/(sigma*sigma);
//	private static int W = R*2+1, H = R*2+1, pixels[] = new int[W*H];
//	private static BufferedImage shadow = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB_PRE);

//	private int S = 30, R = (int)(S*1.85);//R*2/3;
//	private double sigma = R/3.5, s = .80, shift = -0.8;
//	private double ooSigmaSigma = 1/(sigma*sigma);
//	private int W = R*2+1, H = R*2+1, pixels[] = new int[W*H];
//	private BufferedImage shadow = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB_PRE);
	
	private final BufferedImage shadow;
	
	public DropShadowBorder(int shiftX, int shiftY, int S, int R, double sigma, double alpha, double base, int r ) {
		this.S = S;
		this.R = R;
		this.r = r;
		this.shiftX = shiftX;
		this.shiftY = shiftY;
		borderInsets = new Insets(S, S, S, S);
		
		double ooSigmaSigma = 1/(sigma*sigma);
		int W = R*2+1;
		int H = R*2+1;
		int[] pixels = new int[W*H];
		this.shadow = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB_PRE);
		
		for (int y=-R,o=0;y<=R;y++)
			for (int x=-R;x<=R;x++,o++)
				pixels[o] = (((int)(Math.max(0,base+Math.exp(-.5*(x*x+y*y)*ooSigmaSigma)*alpha*255))) << 24);
		
		shadow.setRGB(0,0,W,H,pixels,0,W);
	}
	
	private int r = 0;
	private final int S, R;
	private final Insets borderInsets;
	private final int shiftX, shiftY;
	
	public void setCutout(int r ) {
		this.r=r;
	}
	
	public DropShadowBorder(int shiftX, int shiftY, int S) {
		this(shiftX, shiftY, S, (int)(S*1.85));
	}

	public DropShadowBorder(int shiftX, int shiftY, int S, int R) {
		this(shiftX, shiftY, S, R, R/3.5, .8, -.8, 0);
	}

	public DropShadowBorder(int shiftX, int shiftY) {
		this(shiftX, shiftY, 30);
	}
	
	
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		paintBorder(g,x,y,width,height);
	}
	
	public void paintBorder(Graphics g, int x, int y, int width, int height) {
//		x+=shiftX;
//		y+=shiftY;

		g=g.create();
		if (r>0) {
			Area a = new Area(new Rectangle2D.Double(x,y,width,height));
			a.subtract(new Area(new RoundRectangle2D.Double(x+S-r,y+S-r,width-(S-r)*2,height-(S-r)*2,r*5,r*5)));
			((Graphics2D)g).clip(a);
		}
		
		final int sx = -shiftX, sy = -shiftY;
//		final int sx = 0, sy = 0;
		int w2 = width/2, h2=height/2, rw = width%2, rh = height%2;
		
		g.drawImage(shadow, x, y, x+S, y+S, 				sx+0, sy+0, sx+S, sy+S, null);
		
		g.drawImage(shadow, x+S, y, x+min(R,w2), y+S, 				sx+S, sy+0, sx+min(R,w2), sy+S, null);
		if (w2+rw>R) g.drawImage(shadow, x+R, y, x+width-R, y+S, 				sx+R, sy+0, sx+R+1, sy+S, null);
		g.drawImage(shadow, x+width-min(R,w2+rw), y, x+width-S, y+S, 				sx+R*2-min(R,w2+rw)+1, sy+0, sx+R*2+1-S, sy+S, null);
		
		g.drawImage(shadow, x+width-S, y, x+width, y+S, 				sx+R*2+1-S, sy+0, sx+R*2+1, sy+S, null);
		
		g.drawImage(shadow, x+width-S, y+S, x+width, y+min(R,h2), 				sx+R*2+1-S, sy+S, sx+R*2+1, sy+min(R,h2), null);
		if (h2+rh>R) g.drawImage(shadow, x+width-S, y+R, x+width, y+height-R, 				sx+R*2+1-S, sy+R, sx+R*2+1, sy+R+1, null);
		g.drawImage(shadow, x+width-S, y+height-min(R,h2+rh), x+width, y+height-S, 				sx+R*2+1-S, sy+R*2-min(R,h2+rh)+1, sx+R*2+1, sy+R*2+1-S, null);
			
		g.drawImage(shadow, x+width-S, y+height-S, x+width, y+height, 				sx+R*2+1-S, sy+R*2+1-S, sx+R*2+1, sy+R*2+1, null);

		g.drawImage(shadow, x, y+S, x+S, y+min(R,h2), 				sx+0, sy+S, sx+S, sy+min(R,h2), null);
		if (h2+rh>R) g.drawImage(shadow, x, y+R, x+S, y+height-R, 				sx+0, sy+R, sx+S, sy+R+1, null);
		g.drawImage(shadow, x, y+height-min(R,h2+rh), x+S, y+height-S, 				sx+0, sy+R*2-min(R,h2+rh)+1, sx+S, sy+R*2+1-S, null);

		g.drawImage(shadow, x, y+height-S, x+S, y+height, 				sx+0, sy+R*2+1-S, sx+S, sy+R*2+1, null);
		
		g.drawImage(shadow, x+S, y+height-S, x+min(R,w2), y+height, 				sx+S, sy+R*2+1-S, sx+min(R,w2), sy+R*2+1, null);
		if (w2+rw>R) g.drawImage(shadow, x+R, y+height-S, x+width-R, y+height, 				sx+R, sy+R*2+1-S, sx+R+1, sy+R*2+1, null);
		g.drawImage(shadow, x+width-min(R,w2+rw), y+height-S, x+width-S, y+height, 				sx+2*R-min(R,w2+rw)+1, sy+R*2+1-S, sx+R*2+1-S, sy+R*2+1, null);
		
	}

	public Insets getBorderInsets() {
		return borderInsets;
	}
	
	public Insets getBorderInsets(Component c) {
		return borderInsets;
	}

	public boolean isBorderOpaque() {
		return false;
	}
	
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		
		f.setContentPane(new JComponent() {
			private static final long serialVersionUID = 1L;
			{
				setBorder(new DropShadowBorder(1,1,9,15));
//				setBorder(new DropShadowBorder(0,0, 6,10));
				
//				setLayout(new BorderLayout());
//				add(new JButton());
			}
			protected void paintComponent(Graphics g) {
				
				Insets in = getInsets();
				g.drawRect(in.left, in.top, -in.left-in.right+getWidth()-1,-in.top-in.bottom+getHeight()-1);
				
			}
		});

		
		
		f.setBounds(300, 300, 300, 300);
		f.setVisible(true);
	}




}
