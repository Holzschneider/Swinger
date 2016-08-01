package de.dualuse.commons.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JComponent;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class JScatterPlot3D extends JComponent implements ScatterPlot3D, ScatterPlot3D.DataPoints {
	
	final static public Color defaultDotColor = new Color(0,0,0,0.333f);
	
	@Override public int n() { return dots.size(); }
	@Override public float x(int i) { return (dots.get(i).objPos.x+1)/2; }
	@Override public float y(int i) { return (dots.get(i).objPos.y+1)/2;	}
	@Override public float z(int i) { return (dots.get(i).objPos.z+1)/2; }
	@Override public Color c(int i) { return dots.get(i).color; }
	@Override public float r(int i) { return 30f; }
	@Override public boolean v(int i) { return dots.get(i).visible; }
	
	DataPoints data = this;
	
	
	public class Dot implements Comparable<Dot> {
		private Vector4f objPos = new Vector4f(), scrPos = new Vector4f();
		private float r = 30f;
		
		public Color color;
		
		public boolean visible = true;
		
//		public void set(int argb) {
//			set(((argb>>16)&0xFF)/255f,((argb>>8)&0xFF)/255f,((argb>>0)&0xFF)/255f, new Color (argb, true));
//		}
		
		public Dot set(float x, float y, float z, Color c, float r) {
			set(x,y,z,c);
			this.r=r;
			return this;
		}

		public Dot set(float x, float y, float z, Color c) {
			objPos.set(x*2-1, y*2-1, z*2-1, 1);
			this.color = c;
			visible = true;
			return this;
		}

		public Dot hide() {
			visible = false;
			return this;
		}

		{
			dots.add(this);
		}
		
		public Dot() {
			visible = false;
		}
		
//		public Dot(int argb) {
//			set(argb);
//		}
		
		public Dot(float x, float y, float z, Color c) {
			set(x,y,z,c);
		}

		public Dot(float x, float y, float z, Color c, float r) {
			set(x,y,z,c, r);
		}
		
		public int compareTo(Dot o) {
			return Double.compare(o.scrPos.z,this.scrPos.z);
		}
		
		public Dot setVisible(boolean visible) {
			this.visible = visible;
			return this;
		}
	}


	
	private static final long serialVersionUID = 1L;

	static Vector3f cross( float a1, float a2, float a3, float b1, float b2, float b3, Vector3f r) {
		r.set(a2*b3-a3*b2, a3*b1-a1*b3, a1*b2-a2*b1);
		return r;
	}
	
	public static void frustum( float left, float right, float bottom, float top, float nearVal, float farVal, Matrix4f m ) {

		float A = (right + left)/(right - left);
		float B = (top + bottom)/(top - bottom);
		float C = -(farVal + nearVal)/(farVal - nearVal);
		float D = -(2 * farVal * nearVal) /( farVal -nearVal);
		
		m.m00 = (2*nearVal)/(right-left);	m.m01 = 0; 							m.m02 = A; m.m03 = 0;
		m.m10 = 0; 							m.m11 = (2*nearVal)/(top-bottom); 	m.m12 = B; m.m13 = 0;
		m.m20 = 0; 							m.m21 = 0; 							m.m22 = C; m.m23 = D;
		m.m30 = 0; 							m.m31 = 0; 							m.m32 = -1; m.m33 = 0;
	}
	
	public static void frustum( float left, float right, float bottom, float top, float nearVal, float farVal, Matrix4d m ) {

		float A = (right + left)/(right - left);
		float B = (top + bottom)/(top - bottom);
		float C = -(farVal + nearVal)/(farVal - nearVal);
		float D = -(2 * farVal * nearVal) /( farVal -nearVal);
		
//		m.m00 = 2; m.m10 = nearVal; m.m20 = right-left; m.m30=0;
//		m.m01 = A; m.m11 = 0; m.m21 = 0; m.m31 = 2;
//		m.m02 = nearVal; m.m12 = top-bottom; m.m22 = B; m.m32 = 0;
//		m.m03 = 0; m.m13 = 0; m.m23 = C; m.m33 = D;
		
//		m.m00 = 2; m.m01 = nearVal; m.m02 = right-left; m.m03=0;
//		m.m10 = A; m.m11 = 0; m.m12 = 0; m.m13 = 2;
//		m.m20 = nearVal; m.m21 = top-bottom; m.m22 = B; m.m23 = 0;
//		m.m30 = 0; m.m31 = 0; m.m32 = C; m.m33 = D;
		
//		m.m00 = (2*nearVal)/(right-left); 	m.m10 = 0; 							m.m20 = A; 	m.m30 = 0;
//		m.m01 = 0; 							m.m11 = (2*nearVal)/(top-bottom); 	m.m21 = B; 	m.m31 = 0;
//		m.m02 = 0; 							m.m12 = 0; 							m.m22 = C; 	m.m32 = D;
//		m.m03 = 0; 							m.m13 = 0; 							m.m23 = -1; m.m33 = 0;
		
		m.m00 = (2*nearVal)/(right-left);	m.m01 = 0; 							m.m02 = A; m.m03 = 0;
		m.m10 = 0; 							m.m11 = (2*nearVal)/(top-bottom); 	m.m12 = B; m.m13 = 0;
		m.m20 = 0; 							m.m21 = 0; 							m.m22 = C; m.m23 = D;
		m.m30 = 0; 							m.m31 = 0; 							m.m32 = -1; m.m33 = 0;
	}
	
	
	public final ArrayList<Dot> dots = new ArrayList<Dot>();
	public final ArrayList<Dot> toBeRendered = new ArrayList<Dot>();
	
	
	public void setColorTransform(Matrix4f colortransform) {
		this.colortransform.set(colortransform);
	}
	
	public void setColorTransform(Matrix4d colortransform) {
		this.colortransform.set(colortransform);
	}
	
	public Matrix4f getModelViewMatrix() {
		return new Matrix4f(modelview);
	}
	
	public void setModelViewMatrix(Matrix4f modelview) {
		this.modelview = modelview;
	}
	
	private Matrix4f projection = new Matrix4f(), modelview = new Matrix4f(), colortransform = new Matrix4f(); 
	{	
		float near = 3;
		Matrix4f proj = new Matrix4f(), trans = new Matrix4f(), scale = new Matrix4f();
		frustum(-1,1,-1,1,near,near*4, proj);
		
		scale.setIdentity();
		scale.setScale(1);
		
		trans.setIdentity();
		trans.setTranslation(new Vector3f(0, 0, -(near*2)));
		projection.setIdentity();
		projection.mul(proj);
		projection.mul(trans);
		projection.mul(scale);
		
		modelview.setIdentity();
		
		colortransform.setIdentity();

//		Matrix4f rot = new Matrix4f();
//		
////		rot.rotY((float)(-Math.PI/1));
////		modelview.mul(rot);
//		
//		rot.rotY((float)(-Math.PI/4));
//		modelview.mul(rot);
//
//		rot.rotX((float)(-Math.PI/4));
//		modelview.mul(rot);
//		
//		rot.rotY((float)(-Math.PI/4));
//		modelview.mul(rot);
//
//		Matrix4f inv = new Matrix4f();
//		inv.invert(modelview);
//
//		
//		float S = 3f;
//		modelview.mul( new Matrix4f(
//				S, 0, 0, 0, 
//				0, 1, 0, 0,
//				0, 0, S, 0, 
//				0, 0, 0, 1),modelview);
//
//		modelview.mul( inv, modelview );
//		System.out.println(modelview)
//		
//		;
		
//		inv.mul(modelview, modelview);
		
		
//		rot.rotY(45);
//		modelview.mul(rot);
//		modelview.rotX(45);
//		modelview.rotY(45);
//		modelview.rotX(45);
		
	}
	Vector4f a = new Vector4f(-1,-1,-1,1), a_ = new Vector4f();
	Vector4f b = new Vector4f(+1,-1,-1,1), b_ = new Vector4f();
	Vector4f c = new Vector4f(+1,+1,-1,1), c_ = new Vector4f();
	Vector4f d = new Vector4f(-1,+1,-1,1), d_ = new Vector4f();
	Vector4f e = new Vector4f(-1,-1,+1,1), e_ = new Vector4f();
	Vector4f f = new Vector4f(+1,-1,+1,1), f_ = new Vector4f();
	Vector4f g = new Vector4f(+1,+1,+1,1), g_ = new Vector4f();
	Vector4f h = new Vector4f(-1,+1,+1,1), h_ = new Vector4f();
	
	Vector4f v_ = new Vector4f(), w = new Vector4f(), w_ = new Vector4f();
	Vector3f r = new Vector3f();

	Path2D.Double p = new Path2D.Double();
	Line2D.Double l = new Line2D.Double();
	
//	Color inside = new Color(1,1,1,.7f);
	
	protected void paintComponent(Graphics g1) {
		super.paintComponent(g1);
		
//		if (isBackgroundSet()) {
//			Color c = g1.getColor();
//			g1.setColor(getBackground());
//			Insets in = getInsets();
//			g1.fillRect(in.left, in.top, getWidth()-in.left-in.right, getHeight()-in.top-in.bottom);
//			g1.setColor(c);
//		}
			
		
		modelview.transform(a, a_);
		modelview.transform(b, b_);
		modelview.transform(c, c_);
		modelview.transform(d, d_);
		modelview.transform(e, e_);
		modelview.transform(f, f_);
		modelview.transform(g, g_);
		modelview.transform(h, h_);
		
		projection.transform(a_, a_);
		projection.transform(b_, b_);
		projection.transform(c_, c_);
		projection.transform(d_, d_);
		projection.transform(e_, e_);
		projection.transform(f_, f_);
		projection.transform(g_, g_);
		projection.transform(h_, h_);
		
		a_.scale(1f/a_.w);
		b_.scale(1f/b_.w);
		c_.scale(1f/c_.w);
		d_.scale(1f/d_.w);
		e_.scale(1f/e_.w);
		f_.scale(1f/f_.w);
		g_.scale(1f/g_.w);
		h_.scale(1f/h_.w);

		Graphics2D g2 = (Graphics2D)g1.create();
		g2.translate(0, getHeight());
		g2.scale(1, -1);
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_PURE);
		
		float size = Math.min(getWidth(),getHeight())*.85f;
		
		float wDiv2 = size/2, hDiv2 = size/2, sx = (getWidth()-size)/2, sy = (getHeight()-size)/2, z=1;
		Color foreground = getForeground(), background = getBackground();
		boolean backgroundSet = isBackgroundSet();
		
		
		if (cross(c_.x-a_.x,c_.y-a_.y,c_.z-a_.z, c_.x-b_.x,c_.y-b_.y,c_.z-b_.z, r).z>0) {

			p.reset();
			p.moveTo(a_.x*z*wDiv2+wDiv2+sx,  a_.y*z*hDiv2+hDiv2+sy);
			p.lineTo(b_.x*z*wDiv2+wDiv2+sx,  b_.y*z*hDiv2+hDiv2+sy);
			p.lineTo(c_.x*z*wDiv2+wDiv2+sx,  c_.y*z*hDiv2+hDiv2+sy);
			p.lineTo(d_.x*z*wDiv2+wDiv2+sx,  d_.y*z*hDiv2+hDiv2+sy);
			p.closePath();
	
			g2.setColor(background);
			if (backgroundSet) g2.fill(p);
			g2.setColor(foreground);
			g2.draw(p);
	
		}
		
		if (cross(g_.x-e_.x,g_.y-e_.y,g_.z-e_.z, g_.x-f_.x,g_.y-f_.y,g_.z-f_.z, r).z<=0) {
			p.reset();
			p.moveTo(e_.x*z*wDiv2+wDiv2+sx, e_.y*z*hDiv2+hDiv2+sy);
			p.lineTo(f_.x*z*wDiv2+wDiv2+sx, f_.y*z*hDiv2+hDiv2+sy);
			p.lineTo(g_.x*z*wDiv2+wDiv2+sx, g_.y*z*hDiv2+hDiv2+sy);
			p.lineTo(h_.x*z*wDiv2+wDiv2+sx, h_.y*z*hDiv2+hDiv2+sy);
			p.closePath();
			
			g2.setColor(background);
			if (backgroundSet) g2.fill(p);
			g2.setColor(foreground);
			g2.draw(p);
		}

		if (cross(a_.x-d_.x,a_.y-d_.y,a_.z-d_.z, a_.x-h_.x,a_.y-h_.y,a_.z-h_.z, r).z>0) {
			p.reset();
			p.moveTo(a_.x*z*wDiv2+wDiv2+sx, a_.y*z*hDiv2+hDiv2+sy);
			p.lineTo(d_.x*z*wDiv2+wDiv2+sx, d_.y*z*hDiv2+hDiv2+sy);
			p.lineTo(h_.x*z*wDiv2+wDiv2+sx, h_.y*z*hDiv2+hDiv2+sy);
			p.lineTo(e_.x*z*wDiv2+wDiv2+sx, e_.y*z*hDiv2+hDiv2+sy);
			p.closePath();
			
			g2.setColor(background);
			if (backgroundSet) g2.fill(p);
			g2.setColor(foreground);
			g2.draw(p);
		}
		
		if (cross(b_.x-c_.x,b_.y-c_.y,b_.z-c_.z, b_.x-f_.x,b_.y-f_.y,b_.z-f_.z, r).z<=0) {
			p.reset();
			p.moveTo(b_.x*z*wDiv2+wDiv2+sx, b_.y*z*hDiv2+hDiv2+sy);
			p.lineTo(c_.x*z*wDiv2+wDiv2+sx, c_.y*z*hDiv2+hDiv2+sy);
			p.lineTo(g_.x*z*wDiv2+wDiv2+sx, g_.y*z*hDiv2+hDiv2+sy);
			p.lineTo(f_.x*z*wDiv2+wDiv2+sx, f_.y*z*hDiv2+hDiv2+sy);
			p.closePath();
			
			g2.setColor(background);
			if (backgroundSet) g2.fill(p);
			g2.setColor(foreground);
			g2.draw(p);
		}

		if (cross(f_.x-a_.x,f_.y-a_.y,f_.z-a_.z, f_.x-b_.x,f_.y-b_.y,f_.z-b_.z, r).z<=0) {
			p.reset();
			
			p.moveTo(a_.x*z*wDiv2+wDiv2+sx, a_.y*z*hDiv2+hDiv2+sy);
			p.lineTo(b_.x*z*wDiv2+wDiv2+sx, b_.y*z*hDiv2+hDiv2+sy); 
			p.lineTo(f_.x*z*wDiv2+wDiv2+sx, f_.y*z*hDiv2+hDiv2+sy);
			p.lineTo(e_.x*z*wDiv2+wDiv2+sx, e_.y*z*hDiv2+hDiv2+sy);
			p.lineTo(a_.x*z*wDiv2+wDiv2+sx, a_.y*z*hDiv2+hDiv2+sy); 
			p.closePath();
			
			g2.setColor(background);
			if (backgroundSet) g2.fill(p);
			g2.setColor(foreground);
			g2.draw(p);
		}

		if (cross(g_.x-d_.x,g_.y-d_.y,g_.z-d_.z, g_.x-c_.x,g_.y-c_.y,g_.z-c_.z, r).z>0) {
			p.reset();
			
			p.moveTo(d_.x*z*wDiv2+wDiv2+sx, d_.y*z*hDiv2+hDiv2+sy);
			p.lineTo(c_.x*z*wDiv2+wDiv2+sx, c_.y*z*hDiv2+hDiv2+sy);
			p.lineTo(g_.x*z*wDiv2+wDiv2+sx, g_.y*z*hDiv2+hDiv2+sy);
			p.lineTo(h_.x*z*wDiv2+wDiv2+sx, h_.y*z*hDiv2+hDiv2+sy); 
			p.lineTo(d_.x*z*wDiv2+wDiv2+sx, d_.y*z*hDiv2+hDiv2+sy);
			p.closePath();
			
			g2.setColor(background);
			if (backgroundSet) g2.fill(p);
			g2.setColor(foreground);
			g2.draw(p);
		}
		
		
		
		
		float S = 30f;
		synchronized (dots) {
			for (int i=0,I=n();i<I;i++)
				if (i<dots.size())
					dots.get(i).set(x(i), y(i), z(i), c(i));
//					dots.get(i).set(x(i), y(i), z(i), c(i), r(i)).setVisible(v(i));
//				else
//					new Dot(x(i),y(i),z(i),c(i),r(i));
//			
//			for (int i=n();i<dots.size();i++)
//				dots.get(i).setVisible(false);
			
			toBeRendered.clear();
			
//			for (Dot d: dots)
			for (int i=0,l=dots.size();i<l;i++) {
				Dot d = dots.get(i);
				
				Vector4f v = d.objPos;
				
				colortransform.transform(v,v_);
				modelview.transform(v_, v_);
				projection.transform(v_, v_);
				
				float ooW = 1f/v_.w;
				v_.x *= ooW;
				v_.y *= ooW;
				v_.z *= ooW;
//				v_.w *= ooW;
				
				d.scrPos.set(v_);
				
				if (d.visible && d.color.getAlpha()>0)
					toBeRendered.add(d);
			}
		}

		
		
		Collections.sort(toBeRendered);
		
		for (int i=0,l=toBeRendered.size();i<l;i++) {
			Dot d = toBeRendered.get(i);
			Vector4f v_ = d.scrPos;

			float x = v_.x*z*wDiv2+wDiv2+sx, y = v_.y*z*hDiv2+hDiv2+sy;
			float r = d.r/v_.w;
			elli.setFrameFromCenter(x,y,x+r,y+r);
			g2.setColor(d.color);
			g2.fill(elli);
		}
		

		g2.setColor(getForeground());
		if (cross(c_.x-a_.x,c_.y-a_.y,c_.z-a_.z, c_.x-b_.x,c_.y-b_.y,c_.z-b_.z, r).z<=0) {
			l.setLine(a_.x*z*wDiv2+wDiv2+sx, a_.y*z*hDiv2+hDiv2+sy, b_.x*z*wDiv2+wDiv2+sx, b_.y*z*hDiv2+hDiv2+sy); g2.draw(l);
			l.setLine(b_.x*z*wDiv2+wDiv2+sx, b_.y*z*hDiv2+hDiv2+sy, c_.x*z*wDiv2+wDiv2+sx, c_.y*z*hDiv2+hDiv2+sy); g2.draw(l);
			l.setLine(c_.x*z*wDiv2+wDiv2+sx, c_.y*z*hDiv2+hDiv2+sy, d_.x*z*wDiv2+wDiv2+sx, d_.y*z*hDiv2+hDiv2+sy); g2.draw(l);
			l.setLine(d_.x*z*wDiv2+wDiv2+sx, d_.y*z*hDiv2+hDiv2+sy, a_.x*z*wDiv2+wDiv2+sx, a_.y*z*hDiv2+hDiv2+sy); g2.draw(l);
		}
		if (cross(g_.x-e_.x,g_.y-e_.y,g_.z-e_.z, g_.x-f_.x,g_.y-f_.y,g_.z-f_.z, r).z>0) {
			l.setLine(e_.x*z*wDiv2+wDiv2+sx, e_.y*z*hDiv2+hDiv2+sy, f_.x*z*wDiv2+wDiv2+sx, f_.y*z*hDiv2+hDiv2+sy); g2.draw(l);
			l.setLine(f_.x*z*wDiv2+wDiv2+sx, f_.y*z*hDiv2+hDiv2+sy, g_.x*z*wDiv2+wDiv2+sx, g_.y*z*hDiv2+hDiv2+sy); g2.draw(l);
			l.setLine(g_.x*z*wDiv2+wDiv2+sx, g_.y*z*hDiv2+hDiv2+sy, h_.x*z*wDiv2+wDiv2+sx, h_.y*z*hDiv2+hDiv2+sy); g2.draw(l);
			l.setLine(h_.x*z*wDiv2+wDiv2+sx, h_.y*z*hDiv2+hDiv2+sy, e_.x*z*wDiv2+wDiv2+sx, e_.y*z*hDiv2+hDiv2+sy); g2.draw(l);
		}
		if (cross(f_.x-a_.x,f_.y-a_.y,f_.z-a_.z, f_.x-b_.x,f_.y-b_.y,f_.z-b_.z, r).z>0) {
//			l.setLine(a_.x*z*wDiv2+wDiv2+sx, a_.y*z*hDiv2+hDiv2+sy, b_.x*z*wDiv2+wDiv2+sx, b_.y*z*hDiv2+hDiv2+sy); g2.draw(l);
			l.setLine(b_.x*z*wDiv2+wDiv2+sx, b_.y*z*hDiv2+hDiv2+sy, f_.x*z*wDiv2+wDiv2+sx, f_.y*z*hDiv2+hDiv2+sy); g2.draw(l);
//			l.setLine(f_.x*z*wDiv2+wDiv2+sx, f_.y*z*hDiv2+hDiv2+sy, e_.x*z*wDiv2+wDiv2+sx, e_.y*z*hDiv2+hDiv2+sy); g2.draw(l);
			l.setLine(e_.x*z*wDiv2+wDiv2+sx, e_.y*z*hDiv2+hDiv2+sy, a_.x*z*wDiv2+wDiv2+sx, a_.y*z*hDiv2+hDiv2+sy); g2.draw(l);
		}
		

		if (cross(g_.x-d_.x,g_.y-d_.y,g_.z-d_.z, g_.x-c_.x,g_.y-c_.y,g_.z-c_.z, r).z<=0) {
//			l.setLine(d_.x*z*wDiv2+wDiv2+sx, d_.y*z*hDiv2+hDiv2+sy, c_.x*z*wDiv2+wDiv2+sx, c_.y*z*hDiv2+hDiv2+sy); g2.draw(l);
			l.setLine(c_.x*z*wDiv2+wDiv2+sx, c_.y*z*hDiv2+hDiv2+sy, g_.x*z*wDiv2+wDiv2+sx, g_.y*z*hDiv2+hDiv2+sy); g2.draw(l);
//			l.setLine(g_.x*z*wDiv2+wDiv2+sx, g_.y*z*hDiv2+hDiv2+sy, h_.x*z*wDiv2+wDiv2+sx, h_.y*z*hDiv2+hDiv2+sy); g2.draw(l);
			l.setLine(h_.x*z*wDiv2+wDiv2+sx, h_.y*z*hDiv2+hDiv2+sy, d_.x*z*wDiv2+wDiv2+sx, d_.y*z*hDiv2+hDiv2+sy); g2.draw(l);
			
		}

	}
	
	
	Ellipse2D.Double elli = new Ellipse2D.Double();
	
	
	MouseEvent last;
	
	{
		
		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				last = null;
			}
			
			public void mouseDragged(MouseEvent e) {
				if (last!=null) {
					
					Matrix4f rotM = new Matrix4f();
					rotM.rotY((e.getX()-last.getX())*0.01f);
					
					modelview.mul(rotM,modelview);

					rotM.rotX((e.getY()-last.getY())*0.01f);
					modelview.mul(rotM,modelview);
					
					repaint();
				}
				
				last = e;
			}
			
		});
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount()==2) {
					modelview.setIdentity();
					repaint();
				}
			}
			
			public void mouseReleased(MouseEvent e) {
				last = null;
			}
		});
	}

//	static float F(float t) {
//		return t>(6*6*6/(29f*29f*29f))?(float)Math.pow(t, 1/3f):(1/3f*(29*29)/(6*6)*t+4/29f);
//	}
	
}

