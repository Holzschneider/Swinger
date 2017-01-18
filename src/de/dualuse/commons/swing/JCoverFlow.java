package de.dualuse.commons.swing;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

import de.dualuse.commons.awt.Graphics3D;


public class JCoverFlow extends JComponent {

	public static interface CoverAlbumProvider {
		int getSize();
		String getCoverTitle(int i);
		
		Shape getCoverShape(int i, int preferredWidth, int preferredHeight);
		BufferedImage getCoverArt(int i, int preferredWidth, int preferredHeight);
//		void paintCoverArt(int i, Graphics g, int x, int y, int width, int height);
	}


	private static final long serialVersionUID = 1L;
	
	final static Color DUMMY_OUTLINE = new Color(.8f,.8f,.8f);
//	final static Color DUMMY_AREA = new Color( DUMMY_OUTLINE.getRGB() & 0x20FFFFFF, true);
	final static Color DUMMY_AREA = new Color( .95f, .95f, .95f);
	final static int DUMMY_STROKE_THICKNESS = 2;
	final static Stroke DUMMY_STROKE = new BasicStroke(DUMMY_STROKE_THICKNESS*.6f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,1,new float[] { 6, 4}, 0);
	
	private BufferedImage dummy = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
	
	
	Insets margin = new Insets(16, 16, 16, 16);
	
	CoverAlbumProvider model = new CoverAlbumProvider() {
		
		public int getSize() { return 30; }
		public String getCoverTitle(int i) { return "Title "+i; }
		
		public BufferedImage getCoverArt(int i, int preferredWidth, int preferredHeight) {
			return null;
		}

		public Shape getCoverShape(int i, int preferredWidth, int preferredHeight) {
			return null;
		}
	};
	
	int posm = 0, pos=0;
	double posd = 0; 
	
	MouseWheelListener mouseWheelControl = new MouseWheelListener() {
		public void mouseWheelMoved(MouseWheelEvent e) {
			if ((e.getModifiers()&MouseWheelEvent.SHIFT_MASK)==0)
				return;
			
			posm += Math.min(3,Math.max(-3,-e.getWheelRotation()));
			pos = posm/3;
			
			if (pos>0)
				pos = posm = 0;
			else
				if (pos<-(model.getSize()-count+6))
					posm = 3*(pos = -(model.getSize()-count+6));
			
			repaint();
		}
	};
	
	
	public JCoverFlow() {
		this.addMouseWheelListener(mouseWheelControl);
	}
	
	
	private long timestamp = 0;
	final static double Z = 15,P = .95, M = 32;
	
	int count = 0;
	
	double coverSpacingFactor = 1.6;
	
	protected void paintComponent(Graphics g) {
		if (pos<-(model.getSize()-count+6))
			posm = 3*(pos = -(model.getSize()-count+6));

		long now = System.nanoTime();
		double dt = Math.max((now-timestamp)/1e7,0);
		double dp = pos-posd;
		double sp = Math.signum(dp)*Math.min(M,Math.pow(Math.abs(dp)/Z,P));
		
		posd += sp*dt;

		timestamp = now;
		if (Math.abs(pos-posd)>0.01)
			repaint();
		else
			timestamp = Long.MAX_VALUE;
		
		
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);

		int W = getWidth(), H = getHeight();
		int S = Math.max(24,H-margin.top-margin.bottom);
		
		if (dummy.getWidth()!=S && dummy.getHeight()!=S) {
			
			BufferedImage bi = new BufferedImage(S, S, dummy.getType());
			
			Graphics2D gi = bi.createGraphics();
			gi.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			
			int D = DUMMY_STROKE_THICKNESS*4;
			gi.setColor(DUMMY_AREA);
			gi.fillRoundRect(D, D, S-1-2*D, S-1-2*D, S/4, S/4);

			gi.setStroke(DUMMY_STROKE);
			gi.setColor(DUMMY_OUTLINE);
			gi.drawRoundRect(D, D, S-1-2*D, S-1-2*D, S/4, S/4);
			
			dummy = bi;
		}
	

		double coverSpacing = S*coverSpacingFactor; 
		
		Graphics2D g2 = ((Graphics2D)g);
		Graphics3D g3 = new Graphics3D(g2);
		g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g3.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		int max = model.getSize()-1;
		int minI = (int)Math.max(-posd-2,0);
		int maxI = (int) Math.min(max,minI+W/coverSpacing+4);
		
		int i = minI-3;
		int I = maxI+3;
		
		@SuppressWarnings("unused")
		int cnt = 0;
		for (;i<I;i++,cnt++)
			if (i>=0 && i<max-1)
				if (!paintCover(g3,i,coverSpacing,W,S))
					break;
		
		for (;i<I;I--,cnt++)
			if (I<max-1	)
				paintCover(g3,I,coverSpacing,W,S);

		count=(int)(W/coverSpacing+4);
	}
	
	float near = 5;
	
	private boolean paintCover(Graphics3D g3, int i, double coverSpacing, double W, int S) {
		double x = (i+posd)*coverSpacing;
		
		double cx = S/2+margin.left, cy = S/2+margin.top;
		double tx = translation(x, coverSpacing, W);
		double r = rotation(x, coverSpacing, W);
		double alpha = Math.max(0,Math.min(1,1.2f*alpha(x, coverSpacing, W)));
		
		
		g3.pushTransform();			
		
		g3.translate(tx, 0);
		g3.translate(cx, cy);
		g3.scale(S/2, S/2);
		
		g3.frustum(-1, 1, -1, 1, near, 100);
		g3.translate(0,0,near);
		
		g3.rotate(r, 0, 1, 0);

		g3.scale(-1, -1);
		
		g3.pushTransform();

		BufferedImage cover = (cover = model.getCoverArt(i, S, S))==null?dummy:cover;
		Shape frame = cover!=dummy?model.getCoverShape(i, S, S):null;
		String title = model.getCoverTitle(i);
		
		double iw = cover.getWidth(), ih = cover.getHeight();
		double is = Math.max(iw,ih); 
		
		g3.scale(2./is, 2./is);

		g3.translate(-iw/2,-ih/2);

		Point2D p = g3.project((float)iw/2, (float)ih);
		
//		g3.setStroke(new BasicStroke(1f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL));
		g3.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)alpha));

		g3.drawImage(cover, 0, 0, null);
		if (frame!=null) {
			g3.setColor(new Color(0,0,0,.2f));
			g3.draw(frame);
		}
		
		
//		g3.drawRect(0,0,cover.getWidth(),cover.getHeight());
//
//		g3.setColor(new Color(0,0,0,.5f));
//		g3.drawRect(-1,-1,2+cover.getWidth(),2+cover.getHeight());
//		
//		g3.setColor(new Color(0,0,0,.2f));
//		g3.drawRect(-2,-2,4+cover.getWidth(),4+cover.getHeight());
		
		g3.popTransform();
		
//		g3.drawLine(-1, -1, 1, -1);
//		g3.drawLine(-1, -1, -1, 1);
//		g3.drawLine( -1, -1, 1, 1);
//		g3.drawLine( 1, -1, -1, 1);

		g3.popTransform();

		g3.setColor(new Color(0,0,0,(float)Math.max(0,Math.min(1,1-1.3*Math.abs(r)))));
		Rectangle2D rect = g3.getFontMetrics().getStringBounds(title, g3);
		g3.drawString(title, (float)p.getX()-(float)rect.getWidth()/2, (float)p.getY());
		
		return x+coverSpacing*2<W/2;
	}
	
	double alpha(double x, double m, double w) {
		return 1;
//		return Geometry.smoothStep(0, m*1, x+m*3)*Geometry.smoothStep(0, m*1, w-x+m);
//		return Geometry.smoothStep(0, m*1, w-x+m);
	}
	
	double rotation(double x, double m, double w) {
		double r = 1-(translation(x+.1,m,w)-translation(x-.1,m,w))*5;
		if (x+m>w/2)
			return -r;
		else
			return r;
	}
	
	double translation(double x, double m, double w) {
		double c = m*1.1, s = .9, p = m/2;
		
//		w*=0.5;
		w-=p*3.8;
//		double y = 0;
		if (x<0&&x>-c*s) return p+(x+c)*(x+c)/c/2-c/2;
		if (x<-c*s) return p+x*(1-s)-c*s*s/2;
//		w+=p*.25;
		if (x>w&x<w+c*s) return p+w-((w-x+c)*(w-x+c)/c/2-c/2);
		if (x>w+c*s) return p+w-(Math.pow(w-w-c*s+c,2)/c/2-c/2)+((x-w-c*s)*(1-s));

		return p+x;
		
//		return y+p;
		
	}

	public static void main(String... args) throws IOException {

		BufferedImage bi = ImageIO.read(new File("/Library/Desktop Pictures/Isles.jpg"));
		BufferedImage bj = new BufferedImage(bi.getWidth(),bi.getHeight(),BufferedImage.TYPE_INT_RGB);
		bj.createGraphics().drawImage(bi, 0, 0, null);
		
		int W = 200, H = 200, S = bi.getWidth()/W;
		final BufferedImage[] tiles = new BufferedImage[(bi.getWidth()/W)*(bi.getHeight()/H)];
		
		for (int j=0,y=0,Y=bi.getHeight()-H;y<Y;y+=H,j++)
			for (int i=0,x=0,X=bi.getWidth()-W;x<X;x+=W,i++) {
				int M=5,m=4;
				int s = 0;
				
				BufferedImage si = bj.getSubimage(x, y, W, H); 
				BufferedImage su = new BufferedImage(s+W,s+H*M/m,BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = su.createGraphics();
				g2.drawImage(si,new AffineTransform(1,0,0,-1,s,s+H+H),null);
				g2.setPaint(new GradientPaint(0, H, new Color(1,1,1,.6f), 0, H*M/m, new Color(1,1,1,1f)));
				g2.fillRect(0, H, W, H);
				
				g2.drawImage(si,s,s,null);
				
				tiles[i+j*S] = su;
			}
		
		
		JCoverFlow cf = new JCoverFlow();
		cf.model = new CoverAlbumProvider() {
			public int getSize() {
				return tiles.length;
			}
			
			public String getCoverTitle(int i) {
				return "Title " +i;
			}
			
			public BufferedImage getCoverArt(int i, int preferredWidth, int preferredHeight) {
				return tiles[i];
			}

			public Shape getCoverShape(int i, int preferredWidth, int preferredHeight) {
				return new Rectangle2D.Double(0,0,tiles[i].getWidth(),tiles[i].getWidth());
			}
		};
		
		JFrame f = new JFrame();
		
		f.setBounds(600, 250, 600, 130);
		f.setContentPane(cf);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	
}






