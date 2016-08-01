package de.dualuse.commons.swing;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JKnob extends JComponent  {
	private static final long serialVersionUID = 1L;


	public static interface Listener {
		public void knobMoved( JKnob k, double dx, double dy );
	}

	
	static final String PREFIX = "JKnob";
	
	@Override
	public String toString() {
		return PREFIX+"("+getCenterX()+":"+getCenterY()+")";
	}
	
	public JKnob fromString(String s) {
		s = s.substring(s.indexOf(PREFIX)+PREFIX.length());
		String elems[]= s.split("[\\(:\\)]");
		this.setCenter(Double.parseDouble(elems[1]), Double.parseDouble(elems[2]));

		return this;
	}
	
	
	private ChangeListener microscopeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			setCanvasTransform( ((JMicroscope)e.getSource()).canvasTransform );
		}
	};
	
	public void addNotify() {
		super.addNotify();

		if (getParent() instanceof JMicroscope) { 
			JMicroscope mic = (JMicroscope)getParent(); 
			mic.addChangeListener(microscopeListener);
			setCanvasTransform(mic.canvasTransform);
		}
	};
	
	public void removeNotify() {
		super.removeNotify();
		
		if (getParent() instanceof JMicroscope)
			((JMicroscope)getParent()).removeChangeListener(microscopeListener);
	};
	
	
	Collection<JComponent> consumers = new CopyOnWriteArrayList<JComponent>();
	public void addConsumer(JComponent n) { consumers.add(n); };
	public void removeConsumer(JComponent n) { consumers.remove(n); };
	
	
	Collection<Listener> kls = new LinkedList<Listener>(); 
	public void addKnobListener( Listener l ) {
		Collection<Listener> newKls = new LinkedList<Listener>(kls);
		newKls.add(l); 
		kls = newKls;
	}
	
	public void removeKnobListener( Listener l ) {
		Collection<Listener> newKls = new LinkedList<Listener>(kls);
		newKls.remove(l);
		kls = newKls;
	}
	
	final Point2D center;
	public void setCenter(final double cx, final double cy) {
		center.setLocation(cx,cy);

		int ix = (int)(at.getScaleX()*cx+at.getShearX()*cx+at.getTranslateX());
		int iy = (int)(at.getScaleY()*cy+at.getShearY()*cy+at.getTranslateY());
		
		for (Listener l: kls)
			l.knobMoved(JKnob.this, cx-getCenterX(), cy-getCenterY());
		
		for (JComponent l: consumers)
			l.repaint();

		if (getParent()!=null)
			getParent().repaint();

		super.setLocation( (int)(ix-this.getWidth()/2.0), (int)(iy-this.getHeight()/2.0));
	}
	
	public double getCenterX() { 
		return center.getX();
	}
	
	public double getCenterY() {
		return center.getY();
	}
	
	public void setLocation(int x, int y) {
		super.setLocation(x, y);
		
		center.setLocation(x+getWidth()/2, y+getHeight()/2);
		try { at.inverseTransform(center, center); } catch (NoninvertibleTransformException nte) {};
	}
	
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);

		center.setLocation(x+width/2, y+height/2);
		try { at.inverseTransform(center, center); } catch (NoninvertibleTransformException nte) {};
	}
	
	public void setBounds(Rectangle r) {
		this.setBounds(r.x, r.y, r.width, r.height);
	}
	
	public void setSize(int width, int height) {
		this.setBounds(getX(),getY(),width,height);
	}
	
	public void setSize(Dimension d) {
		this.setSize(d.width,d.height);
	}
	

	
	private AffineTransform at = new AffineTransform();
	public void preTransformCanvas(AffineTransform t) { 
		at.preConcatenate(t); 
		double cx = center.getX(), cy = center.getY();
		
		double dx = cx*at.getScaleX()+cx*at.getShearX()+at.getTranslateX();
		double dy = cy*at.getScaleY()+cy*at.getShearY()+at.getTranslateY();

		super.setBounds((int)(dx-this.getWidth()/2.0), (int)(dy-this.getHeight()/2.0), getWidth(), getHeight());
//		super.setLocation( (int)(dx-this.getWidth()/2.0), (int)(dy-this.getHeight()/2.0));
	}

	public void transformCanvas(AffineTransform t) { 
		at.concatenate(t); 
		double cx = center.getX(), cy = center.getY();
		
		double dx = cx*at.getScaleX()+cx*at.getShearX()+at.getTranslateX();
		double dy = cy*at.getScaleY()+cy*at.getShearY()+at.getTranslateY();

		super.setBounds((int)(dx-this.getWidth()/2.0), (int)(dy-this.getHeight()/2.0), getWidth(), getHeight());
//		super.setLocation( (int)(dx-this.getWidth()/2.0), (int)(dy-this.getHeight()/2.0));
	}
	
	public void setCanvasTransform(AffineTransform t) { 
		at.setTransform(t); 

		double cx = center.getX(), cy = center.getY();
		
		double dx = cx*at.getScaleX()+cx*at.getShearX()+at.getTranslateX();
		double dy = cy*at.getScaleY()+cy*at.getShearY()+at.getTranslateY();
		
		super.setBounds((int)(dx-this.getWidth()/2.0), (int)(dy-this.getHeight()/2.0), getWidth(), getHeight());

	}
	
	

	final static private float W = 5.5f, w = 3;
	static public final Stroke THIN = new BasicStroke(w);
	static public final Stroke THICK = new BasicStroke(W);
	static private final Ellipse2D e = new Ellipse2D.Double();
	
	private Color color = Color.WHITE;
	public Color getColor() { return color; }
	public void setColor(Color color) { this.color = color; }
	
	private Color borderColor = Color.BLACK;
	public Color getBorderColor() { return borderColor; }
	public void setBorderColor(Color borderColor) { this.borderColor = borderColor; }
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		
		Insets i = this.getInsets();
		int width = getWidth()-Math.max(i.right,i.left)*2, height = getHeight()-Math.max(i.bottom,i.top)*2;
		
		
		e.setFrameFromCenter( getWidth()/2, getHeight()/2, getWidth()/2+width/2-W/2-1, getHeight()/2+height/2-W/2-1);
//		e.setFrameFromCenter( center.getX()-getX(), center.getY()-getY(), center.getX()-getX()+width/2-W/2-1, center.getY()-getY()+height/2-W/2-1);
		
		g.setColor(borderColor);
		((Graphics2D)g).setStroke(THICK);
		((Graphics2D)g).draw(e);
		
		g.setColor(color);
		((Graphics2D)g).setStroke(THIN);
		((Graphics2D)g).draw(e);
	}
	
	final static Dimension PREFFERED = new Dimension(20,20);
	

	
	public JKnob(Point2D positionContainer) {
		this.center = positionContainer;
		double x = center.getX(), y = center.getY();
		
		setPreferredSize(new Dimension(PREFFERED));
		setSize(PREFFERED);
		
		this.setCenter(x, y);
		
		MouseAdapter ma = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				lastXOnScreen = e.getXOnScreen();
				lastYOnScreen = e.getYOnScreen();
			}	
		
			int lastXOnScreen, lastYOnScreen;
			public void mouseDragged(MouseEvent e) {
				int dx = e.getXOnScreen()-lastXOnScreen, dy = e.getYOnScreen()-lastYOnScreen;
				
				setLocation(getX()+dx, getY()+dy);
				lastXOnScreen = e.getXOnScreen();
				lastYOnScreen = e.getYOnScreen();
				
				for (Listener l: kls)
					l.knobMoved(JKnob.this, dx, dy);
//					l.knobMoved(getCenterX(), getCenterY());
				
				for (JComponent l: consumers)
					l.repaint();

//				if (kls.isEmpty() && consumers.isEmpty())
					if (getParent()!=null)
						getParent().repaint();
				
			}
		};
		this.addMouseListener(ma);
		this.addMouseMotionListener(ma);
	}
	
	public JKnob() {
		this(new Point2D.Double());
	}
	
	
	public JKnob(double centerX, double centerY) {
		this();
		setCenter(centerX, centerY);
	}
	
	protected JKnob(double centerX, double centerY, AffineTransform at ) {
		this();
		setCanvasTransform(at);
		setCenter(centerX, centerY);
	}


	public static void main(String args[]) throws Exception {
		JFrame f = new JFrame();
		f.setBounds(100,100,500,400);

		
		f.getContentPane().setLayout(null);
		
		JKnob k =new JKnob(100,100) {
			private static final long serialVersionUID = 1;

			{
				setBorder(BorderFactory.createLoweredBevelBorder());
				setPreferredSize(new Dimension(20,20));
				setSize(getPreferredSize());
			}
		};
		
		f.getContentPane().add( k );
		
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		String s = k.toString();
		System.out.println(k);
		
		System.in.read();
		
		k.fromString(s);
		
	}

	
}


