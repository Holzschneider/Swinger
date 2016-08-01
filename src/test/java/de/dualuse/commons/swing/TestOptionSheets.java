package de.dualuse.commons.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;

import de.dualuse.commons.swing.JOptionSheet.Task;
import de.dualuse.commons.swing.JOptionSheet.TaskProgress;


public class TestOptionSheets {
	
	public static void main(String[] args) {
		
		final JFrame f = new JFrame();
//		Box testBox = new Box(BoxLayout.Y_AXIS);
////		testBox.add(Box.createHorizontalGlue());
//		testBox.add(new JButton("hallo"));
//		testBox.add(new JButton("welt"));
//		testBox.add(new JTextArea("Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas humanitatis per seacula quarta decima et quinta decima. Eodem modo typi, qui nunc nobis videntur parum clari, fiant sollemnes in futurum.") 
//		{{ setEditable(false); setLineWrap(true); setWrapStyleWord(true); setOpaque(false);}});
////		testBox.add(new JPanel());
////		testBox.add(Box.createHorizontalGlue());
//		testBox.add(new JButton("hallo"));
////		testBox.add(new JPanel());
//		testBox.add(Box.createHorizontalGlue());
//		testBox.add(new JButton("hallo"));
////		testBox.add(Box.createHorizontalGlue());
//		testBox.setBorder(new LineBorder(Color.	));
//		f.setContentPane(testBox);
		
		f.setContentPane(new JButton(new AbstractAction() {
			JOptionSheet.SaveAbortDiscardDialog ssdd = new JOptionSheet.SaveAbortDiscardDialog(f, UIManager.getIcon("OptionPane.questionIcon"), "Test");
			public void actionPerformed(ActionEvent e) {
				ssdd.setVisible(true);
				System.out.println(ssdd.getSelectedOption());
			}
			
			private static final long serialVersionUID = 1L;

			public void actionPerformeds(ActionEvent e) {
				JOptionSheet.showProgressDialog(f.getRootPane(), "converting file into pieces of shit", new Task() {
					public void execute(TaskProgress pc) {
						
						for (int i=0,l=1000;i<l;i+=(int)(Math.random()*50)) {
							pc.setProgress( i, l);
							try {
								Thread.sleep((int)(Math.random()*100));
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							if (pc.taskWasCanceled()) {
								System.out.println("wtf!");
								break;
							}
						}
						
						pc.setProgress(100, 100);
						pc.taskDone();						
					}
				});
				
				
			}
		}));
		
		f.setBounds(300,100,500,400);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}
