package de.dualuse.commons.swing;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.dualuse.commons.swing.RasterLayout.Range;

public class TestRasterLayout {

	public static void main(String args[]) throws Exception {
		JFrame f = new JFrame();
		

		System.out.println(new Range(new Range(0,0,0,0).fromString("RasterLayout.Range[fromX =10, fromY= 2, toX=0, toY=4]").toString()));
		
		JComponent p = new JPanel(new RasterLayout(30,30,0,0));

		f.getContentPane().add(p);
		
		int R = 3, C = 3;
		
		/*
		for (int x=0;x<C;x++)
			for (int y=0;y<R;y++)
				f.getContentPane().add(new JButton(x+","+y),new RasterLayout.Cell(x,y));
		
		
		for (int x=1;x<=C;x++)
			for (int y=1;y<=R;y++)
				f.getContentPane().add(new JButton(x+","+y),new RasterLayout.Cell(-x,-y));
		*/
		
		RasterLayout.Range r = new RasterLayout.Range(4,4,-5,4);
		
		
		
		
		p.add(new JButton("hallo"),new RasterLayout.Cell(0,0));
		p.add(new JButton("du"),new RasterLayout.Cell(1,1));
		p.add(new JButton("welt"),new RasterLayout.Cell(2,2));
		p.add(new JButton("des"),new RasterLayout.Cell(3,3));
		p.add(new JButton("Layouts"),r);
		p.add(new JButton("Layouts"),new RasterLayout.Range(0,8,-1,-1));
		
		p.add(new JButton("Layouts"),new RasterLayout.Range(6,5,-3,5));
		
//		p.add(new JButton("Layouts"),new RasterLayout.Range(4,6,-3,4));

		
//		p.add(new JButton("Layouts"),new RasterLayout.Range(4,0,4,-1));
//		p.add(new JButton("Layouts (blablaaaaaaaaaaaa)"),new RasterLayout.Cell(-4,-2));
		
		f.setBounds(100,100,600,400);
//		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		
		System.in.read();
		
		r.toX = -6;
		
		p.revalidate();
	}
}
