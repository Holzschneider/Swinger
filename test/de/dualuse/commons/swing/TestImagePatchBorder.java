package de.dualuse.commons.swing;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.xml.bind.DatatypeConverter;

public class TestImagePatchBorder {

	public static void main(String[] args) throws IOException {
		
		JFrame f = new JFrame();
		
		Image bi = ImageIO.read(new ByteArrayInputStream(DatatypeConverter.parseBase64Binary("iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAAAXNSR0IArs4c6QAAAdVpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IlhNUCBDb3JlIDUuNC4wIj4KICAgPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4KICAgICAgPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIKICAgICAgICAgICAgeG1sbnM6dGlmZj0iaHR0cDovL25zLmFkb2JlLmNvbS90aWZmLzEuMC8iPgogICAgICAgICA8dGlmZjpDb21wcmVzc2lvbj4xPC90aWZmOkNvbXByZXNzaW9uPgogICAgICAgICA8dGlmZjpQaG90b21ldHJpY0ludGVycHJldGF0aW9uPjI8L3RpZmY6UGhvdG9tZXRyaWNJbnRlcnByZXRhdGlvbj4KICAgICAgICAgPHRpZmY6T3JpZW50YXRpb24+MTwvdGlmZjpPcmllbnRhdGlvbj4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgIDwvcmRmOlJERj4KPC94OnhtcG1ldGE+Cjl0tmoAAAFKSURBVHgB7ZrJCgJBDETdF/T//1TclyrQi+J0CIhOfA05DJN0J6/q1j0YsCAAAQhAAAIQgAAEIAABCEAAAhCAwJ8RGAbnXShvqZgrRsGab6VddPBesVXsWk1EAHj4lcIbnxRnxS+vsZqbKCzURtEJwcmttVaChz8orq3kH/jvHi2SxZ0qOgFE7OwcK9+35Z6b8zUTtIk36oPyzwK556ZwEQDPG5f6BkApORPD4IAEtFIlOKCUnIlhcEACWqkSHFBKzsQwOCABrVQJDiglZ2IYHJCAVqoEB5SSMzEMDkhAK1WCA0rJmRgGBySglSrBAaXkTAwTcYDv2CLX6InjP1rinpt3mhEAvhrvKwD33rkiAI73HZzbBxDu8THXo/e3EKIDzbSDX134QUW05u2hH/5h2/uBhK/G/aiDBQEIQAACEIAABCAAAQi8ErgBeZQfMN1AWq8AAAAASUVORK5CYII="))); 
		
		
//			Image bi = ImageIO.read(JStyledTabbedPane.class.getResource("TabbedPaneGroupBorder.png").openStream());
		JLabel testLabel = new JLabel("hallo") {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				repaint(30);
			};
		};
		
		testLabel.setLayout(new BorderLayout());
//			testLabel.add(new JButton("welt"));
		
		testLabel.setBorder(new ImagePatchBorder(bi, new Insets(24, 16, 20, 16),new Insets(20, 10, 12, 10), true));
		
		
		
		f.setContentPane(testLabel);
		f.setBounds(400,400, 300,300);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
