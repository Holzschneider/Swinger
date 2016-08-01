package de.dualuse.commons.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;


public class JText extends JComponent {
	private static final long serialVersionUID = 1L;
	
	private String text = "";
	private String lines[] = new String[0]; 
	private int maximumWrapWidth = Integer.MAX_VALUE, minimumWrapWidth = 64;

	public JText() {
		fetchFontLazy().getStringBounds(text,0,text.length(),frc);
	}
	
	public JText(String init) {
		this();
		setText(init);
	}
	
	private final static boolean ANTIALIAS = true, FRACTIONAL_METRICS = true;
	private final FontRenderContext frc = new FontRenderContext(new AffineTransform(), ANTIALIAS, FRACTIONAL_METRICS);
	private final Font DEFAULT_FONT = UIManager.getFont("TextField.font");

	
	public void setMinimumWrapWidth(int minimumWrapWidth) {
		this.minimumWrapWidth = minimumWrapWidth;
		recalcIfNecessary();
	}
	
	public int getMinimumWrapWidth() {
		return minimumWrapWidth;
	}
	
	public int getMaximumWrapWidth() {
		return maximumWrapWidth;
	}
	
	public void setMaximumWrapWidth(int wrapWidth) {
		this.maximumWrapWidth = wrapWidth;
		recalcIfNecessary();
	}

	public void setText(String text) {
		this.text = text;
		recalcIfNecessary();
	}
	
	private Font fetchFontLazy() {
		Font f = getFont();
		
		if (f == null)
			f = DEFAULT_FONT;
		return f;
	}
	
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		recalcIfNecessary();
	}
	
	@Override
	public void setBorder(Border border) {
		super.setBorder(border);
		recalcIfNecessary();
	}

	
	private void recalc(int wrapWidth) {
		Font f = fetchFontLazy();
		
		ArrayList<String> lines = new ArrayList<String>();
		
		
		String text = this.text.trim()+" ";
		int b = 0, j = 0, max = 0;
		for (int i= text.indexOf(' ');i!=-1;j=i,i=text.indexOf(' ',i+1)) {
			Rectangle2D bounds = f.getStringBounds(text,b,i,frc);
			if (bounds.getWidth()>wrapWidth) {
				lines.add(text.substring(b,j).trim());
				b = j;
			} else {
				max = (int)(bounds.getWidth()>max?bounds.getWidth():max);
			}
		}
		
		Rectangle2D bounds = f.getStringBounds(text,b,text.length(),frc);
		max = (int)(bounds.getWidth()>max?bounds.getWidth():max);
		lines.add(text.substring(b,text.length()).trim());
		
		this.lines = lines.toArray(new String[lines.size()]); 

		FontMetrics fm = this.getFontMetrics(f);
		int lineHeight = fm.getHeight();
		Insets in = getInsets();
		Dimension dim = new Dimension(wrapWidth, (int)((lineHeight*(lines.size()))+1+in.top+in.bottom+fm.getDescent()));
		this.setPreferredSize(dim);
	}
	
	
	
	
	private int calcedWidth = -1;
	private Font lastCalcedFont = null;

	private synchronized void recalcIfNecessary() {
		Insets in = getInsets();
		int layoutWidth = Math.max(Math.min(getWidth(),maximumWrapWidth),minimumWrapWidth)-in.left-in.right;
		if (calcedWidth!=layoutWidth || lastCalcedFont!=fetchFontLazy())
			recalc(calcedWidth = layoutWidth);
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		recalcIfNecessary();

		Font f = fetchFontLazy();
		g.setFont(f);
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		
		Insets in = getInsets();
		int lineHeight = this.getFontMetrics(f).getHeight();
		for (int i=0,l=lines.length;i<l;i++) {
			int y = (int)(lineHeight*(i+1))+in.top;
			g.drawString(lines[i], 0+in.left, y);
		}
		
	}

	public static void main(String[] args) {
		
		final JFrame f = new JFrame();

		JPanel test = new JPanel();
		JText text = new JText();
		text.setText("Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenitg");
		text.setSize(800, 100);
		text.setBorder(new LineBorder(Color.BLUE,10));
//		text.setMaximumWrapWidth(300);
		test.add(text);
		f.getContentPane().add(text,BorderLayout.NORTH);
		f.getContentPane().add(new JButton("center"));
		
		f.setBounds(300,100,500,400);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}
