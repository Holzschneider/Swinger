package de.dualuse.commons.swing;

import java.awt.Color;

import javax.swing.JFrame;

import de.dualuse.commons.swing.JScatterPlot3D.Dot;

public class TestScatterPlot3D {

	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		
		
		JFrame f = new JFrame();
		
		
		JScatterPlot3D h = new JScatterPlot3D();
		
		h.new Dot(.5f,.3f,.1f, Color.MAGENTA);
		
		
		f.setContentPane(h);
		
		f.setContentPane(new JScatterPlot3D() {
			private static final long serialVersionUID = 1L;
			
			
			{
				double S = 1./Math.sqrt(3), T = 1./Math.sqrt(2);
//				q.addLine(-1.1,-1.1,-1.1,  1.1,1.1,1.1);
//				System.out.println(q.evaluate(S, S, S));
				
				
				int s = 16;
				for (int r=0;r<255;r+=s) 
					for (int g=0;g<255;g+=s)
						for (int b=0;b<255;b+=s) {
							
							float R = r/255f, G = g/255f, B = b/255f;
							
//							double d = 1-Math.abs(R*S+G*S+B*S-Math.sqrt(3)/2)*2;
//							double d = 1-Math.sqrt(q.evaluate(R, G, B))*2;
//							double d = Math.pow(q.evaluate(R, G, B),3)*20;
//							double d = q.evaluate(R, G, B);
							
//							final double x_ = x*a00+y*a01+z*a02+a03, y_ = x*a01+y*a11+z*a12+a13, z_ = x*a02+y*a12+z*a22+a23, w_ = x*a03+y*a13+z*a23+a33;
//							return x*x_+y*y_+z*z_+w_;
							double P = 2/3., Q = -1/3.;
							
//							double d = (R*P+G*Q+B*Q)*R+(R*Q+G*P+B*Q)*G+(R*Q+G*Q+B*P)*B;
							
							double D = (R*2-G-B)*R+(-R+G*2-B)*G+(-R-G+B*2)*B;
//							double d = (R*.666-G*.333-B*.333)*R+(-R*.333+G*.666-B*.333)*G+(-R*.333-G*.333+B*.666)*B;
							
							float d = ((r*2-g-b)*r+(-r+g*2-b)*g+(-r-g+b*2)*b)/65536f;
							
//							D
//							d = d*d;
//							d>>>=16;
							
//							D=D*D;
							
							D=d*d;	
							
//							double distSq = R*R+G*G+B*B;
//							double dist = Math.sqrt(distSq)/Math.sqrt(3);
//							
//							double d = 1-Math.abs(dist);
							
							D=D<0?0:D>1?1:D;
							
//							D = 1;
//							float alpha = 1-(float)Math.abs(q.evaluate(r/255f, g/255f, b/255f));
//							alpha= alpha<0?0:alpha>1?1:alpha;
//							System.out.println(alpha);
							
							new Dot(R+.5f/s,G+.5f/s,B+.5f/s, new Color(R,G,B, (float)D));
							
//							new Dot(new Color(r, g, b, 255).getRGB());
						}
				
			}
			
		});
		
		f.setBounds(200, 100, 300, 300);
		f.setVisible(true);
		
		
		
	}
}
