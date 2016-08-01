package de.dualuse.commons.swing;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.peer.ComponentPeer;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

public class JOptionSheet extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPanel = new JPanel(new ElasticGridLayout(2, 2, 20,5));
	private JPanel customControlPanel = new JPanel(new BorderLayout());
	private JLabel iconLabel = new JLabel();
	
	private Box buttonBox = new Box(BoxLayout.X_AXIS);
	
	protected Option selectedOption = null;
	public JButton butt = null;

	final Window owner;
	Timer showUp;

	static public Icon EMPTY_ICON = new Icon() { public void paintIcon(Component c, Graphics g, int x, int y) {}; public int getIconWidth() { return 0; };  public int getIconHeight() { return 0; } };
	static public Icon DEFAULT_ICON = EMPTY_ICON;
	static public Icon QUESTION_ICON = EMPTY_ICON; //UIManager.getIcon("OptionPane.questionIcon"); //UIManager.getIcon//UIManager.getIcon("OptionPane.informationIcon");
	
	public void setControl(JComponent customControl) {
		while (customControlPanel.getComponentCount()>0)
			this.customControlPanel.remove(0);
		customControlPanel.add(customControl);
		customControlPanel.revalidate();
		pack();
	}
	
	private static class CompoundKey {
		final private Object keys[];
		
		public CompoundKey(Object... o) {
			keys = Arrays.copyOf(o, o.length);
		}
		
		public int hashCode() {
			int code = 0;
			for (int i=0;i<keys.length;i++)
				code ^= keys[i].hashCode();
			
			return code;
		}
		
		public boolean equals(Object obj) {
			if (obj instanceof CompoundKey) {
				CompoundKey ck = ((CompoundKey)obj);
				
				if (ck.keys.length==keys.length) {
					for (int i=0;i<keys.length;i++)
						if (keys[i] instanceof Object[])
							if (!(ck.keys[i] instanceof Object[]))
								return false;
							else
								if (!Arrays.equals((Object[])keys[i], (Object[])ck.keys[i]))
									return false;
						else
							if (!keys[i].equals(ck.keys[i]))
								return false;
					
					return true;
				} else
					return false;
			} else
				return false;
		}
	}
	
	private static Option[] optionize(String[] labels) {
		Option ao[] = new Option[labels.length];
		for (int i=0,I=labels.length;i<I;i++)
			ao[i] = new StringOption(labels[i]);
		
		return ao;
	}

	
	public static interface Option {
		public String getLabel();
		
		static final Option ABORT	= new StringOption ("Abort");
		static final Option CANCEL  = new StringOption ("Cancel");
		static final Option OK	 	= new StringOption ("OK");
		static final Option YES = new StringOption ("Yes");
		static final Option NO = new StringOption ("No");
		static final Option SAVE	= new StringOption ("Save");
		static final Option DISCARD	= new StringOption ("Discard");
		static final Option REPLACE	= new StringOption ("Replace");
		static final Option MERGE = new StringOption ("Merge");
		static final Option MORE = new StringOption ("More");
		static final Option CURRENT = new StringOption ("Current");
		static final Option RELATED = new StringOption ("Related");
		static final Option ALL = new StringOption ("All");
		static final Option SKIP = new StringOption ("Skip");
		static final Option SKIP_ALL = new StringOption ("Skip All");
		static final Option REPLACE_ALL = new StringOption ("Replace All");
		static final Option ASK = new StringOption ("Ask");
		static final Option SPLITTER = new StringOption(null);
	}
	
	public static class StringOption implements Option {
		final private String title;
		
		public String getLabel() { return title; }
		
		public StringOption(String title) { this.title = title; }		
	}
	
	/*
	public static enum Option implements AbstractOption {
		ABORT	("Abort"),
		CANCEL  ("Cancel"),
		OK		("OK"),
		YES ("Yes"),
		NO ("No"),
		SAVE	("Save"),
		DISCARD	("Discard"),
		REPLACE	("Replace"),
		MERGE("Merge"),
		MORE("More"),
		CURRENT("Current"),
		RELATED("Related"),
		ALL("All"),
		SKIP("Skip"),
		SKIP_ALL("Skip All"),
		REPLACE_ALL("Replace All"),
		ASK("Ask"),
		SPLITTER;
		
		final private String title;
		
		public String getTitle() {
			String t = title;
			while (t.length()>6) t = " "+t+" ";
			return t;
		}
		Option(String title) { this.title = title; }
		Option() { this.title = this.toString(); }
	}
	*/
//==[ Constructors ]===============================================================================
	
	public JOptionSheet(JComponent parent, Icon i, String... options) { this(parent,i,optionize(options)); }
	public JOptionSheet(JComponent parent, Icon i, Option... options) {
		this((Window)parent.getRootPane().getParent(), i, options);
	}
	
	public JOptionSheet(Window owner, Icon i, String... options) { this(owner,i,optionize(options)); }
	public JOptionSheet(Window owner, Icon i, Option... options) {
		this(owner,i,null,options);
	}
	
	public JOptionSheet(JComponent parent, Icon i, JComponent customControls, String... options) { this(parent,i,customControls, optionize(options)); }
	public JOptionSheet(JComponent parent, Icon i, JComponent customControls, Option... options) {
		this((Window)parent.getRootPane().getParent(), i, customControls, options);
	}
	
	public JOptionSheet(Window owner, Icon i, JComponent customControls, String... options) { this(owner,i,customControls, optionize(options)); }
	public JOptionSheet(Window owner, Icon i, JComponent customControls, Option... options) {
		super(owner);
		this.owner = owner;
		this.setModalityType(ModalityType.DOCUMENT_MODAL);
		
		String version = System.getProperty("java.version");
		if (owner != null)
			if (version.startsWith("1.5") || version.startsWith("1.6"))
				this.getRootPane().putClientProperty("apple.awt.documentModalSheet", Boolean.TRUE);
			else
				makeSheet(this);
			
		iconLabel.setHorizontalAlignment(JLabel.CENTER);
		iconLabel.setVerticalAlignment(JLabel.CENTER);
		iconLabel.setIcon(i);
		contentPanel.add(iconLabel);

		if (customControls!=null)
			contentPanel.add(customControls);
		else
			contentPanel.add(customControlPanel);
		
		buttonBox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		ArrayList<JButton> usedButts = new ArrayList<JButton>();
		
		int maxWidth=0,maxHeight=0;
		for (final Option o: options) {
			if (o==Option.SPLITTER || o==null)
				buttonBox.add(Box.createHorizontalGlue());
			else {
				butt = new JButton(new AbstractAction(widen(o.getLabel())) {
					private static final long serialVersionUID = 1L;
					public void actionPerformed(ActionEvent e) {
						selectedOption = o;
						setVisible(false);
					}
				});
				butt.addKeyListener(new KeyListener() {
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ENTER) {
							selectedOption = o;
							setVisible(false);
						}
					}
					public void keyReleased(KeyEvent arg0) {}
					public void keyTyped(KeyEvent arg0) {}
				});
				buttonBox.add(butt);
				usedButts.add(butt);
				Dimension pref = butt.getPreferredSize();
				maxWidth = maxWidth>pref.width?maxWidth:pref.width;
				maxHeight = maxHeight>pref.height?maxHeight:pref.height;
			}
		}
		
		for (JButton butts: usedButts)
			butts.setPreferredSize(new Dimension(maxWidth,maxHeight));

		contentPanel.add(new JPanel());
		
		contentPanel.add(buttonBox);
		contentPanel.setBorder(new EmptyBorder(20, 20, 16, 20));
		
		
		JPanel contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;
			{ setOpaque(false); }
			
			@Override public void doLayout() {
				int height = getHeight();
				for (int i=0,I=getComponentCount();i<I;i++) {
					Dimension dim = getComponent(i).getPreferredSize();
					getComponent(i).setBounds(0, height-dim.height, dim.width, dim.height);
				}
			}
		};
		
		contentPane.add(contentPanel);
		
		this.setContentPane(contentPane);
		this.setResizable(false);
		
		if (customControls!=null)
			this.pack();

	}

//==[ Public Interface ]===========================================================================
	
	public void setVisible(boolean b) {
		if (butt!=null) {
			butt.requestFocusInWindow();
		}

		String version = System.getProperty("java.version");
//		if (owner==null) {
//				final Rectangle bounds = (owner==null)?GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds():owner.getBounds();
//				setLocation(bounds.x+(bounds.width/2-this.getPreferredSize().width/2), bounds.y+23);
//				setBounds(bounds.x+(bounds.width/2-getPreferredSize().width/2), bounds.y+23, getPreferredSize().width, getPreferredSize().height);
//		} else
		if (!(version.startsWith("1.5") || version.startsWith("1.6"))) {
			if (b) {
				final Rectangle bounds = (owner==null)?GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds():owner.getBounds();
				setLocation(bounds.x+(bounds.width/2-this.getPreferredSize().width/2), bounds.y+23);
				final int height = getPreferredSize().height;
				setBounds(bounds.x+(bounds.width/2-getPreferredSize().width/2), bounds.y+23+(owner==null?bounds.height/2-getPreferredSize().height:0), getWidth(), owner==null?height:0);
				
				if (showUp!=null) showUp.stop();
				
				if (owner!=null) {
					showUp = new Timer(10, new ActionListener() {
						int h = 0, i= 0, steps = 7;
						public void actionPerformed(ActionEvent e) {
							setBounds(bounds.x+(bounds.width/2-getPreferredSize().width/2), bounds.y+23, getWidth(), Math.min(h,height));
							h+=(height/steps)+1;
							
							if (i++>steps) showUp.stop();
						}
					});
					showUp.start();
				}
				super.setVisible(true);
				
			} else {
				if (owner!=null) {
					final Rectangle bounds = getBounds();
					
					final int height = getPreferredSize().height;
					if (showUp!=null) showUp.stop();
					showUp = new Timer(1, new ActionListener() {
						int h = height, i= 0, steps = 10;
						public void actionPerformed(ActionEvent e) {
							setBounds(bounds.x+(bounds.width/2-getPreferredSize().width/2), bounds.y, getWidth(), Math.min(h,height));
							h-=(height/8)+1;
							if (i++>steps) {
								showUp.stop();
								JOptionSheet.super.setVisible(false);
							}
						}
					});
					showUp.start();
				} else {
					super.setVisible(false);
				}
			}
		} else {
			super.setVisible(b);
		}
	}
	
	public Option getSelectedOption() { return selectedOption; }
	public String getSelectedOptionLabel() { return selectedOption.getLabel(); }


//	static public class SheetDialog extends JDialog {
//		private static final long serialVersionUID = 1L;
//		
//		private JPanel contentPanel = new JPanel(new ElasticGridLayout(2, 2, 20,5));
//		private JPanel customControlPanel = new JPanel(new BorderLayout());
//		private JLabel iconLabel = new JLabel();
//		
//		private Box buttonBox = new Box(BoxLayout.X_AXIS);
//		
//		private Option selectedOption = null;
//		public JButton butt = null;
//
//
//		public void setControl(JComponent customControl) {
//			while (customControlPanel.getComponentCount()>0)
//				this.customControlPanel.remove(0);
//			customControlPanel.add(customControl);
//			customControlPanel.revalidate();
//			pack();
//		}
//		
//		private SheetDialog(Window owner, Icon i, Option... options) {
//			this(owner,i,null,options);
//		}
//		
//		private SheetDialog(Window owner, Icon i, JComponent customControls, Option... options) {
//			super(owner);
//			
//			this.setModalityType(ModalityType.DOCUMENT_MODAL);
//			this.getRootPane().putClientProperty("apple.awt.documentModalSheet", Boolean.TRUE);
//			iconLabel.setHorizontalAlignment(JLabel.CENTER);
//			iconLabel.setVerticalAlignment(JLabel.CENTER);
//			iconLabel.setIcon(i);
//			contentPanel.add(iconLabel);
//
//			if (customControls!=null)
//				contentPanel.add(customControls);
//			else
//				contentPanel.add(customControlPanel);
//			
//			buttonBox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//			ArrayList<JButton> usedButts = new ArrayList<JButton>();
//			
//			int maxWidth=0,maxHeight=0;
//			for (final Option o: options) {
//				if (o==Option.SPLITTER || o==null)
//					buttonBox.add(Box.createHorizontalGlue());
//				else {
//					butt = new JButton(new AbstractAction(o.getTitle()) {
//						private static final long serialVersionUID = 1L;
//
//						public void actionPerformed(ActionEvent e) {
//							selectedOption = o;
//							setVisible(false);
//						}
//					});
//					buttonBox.add(butt);
//					usedButts.add(butt);
//					Dimension pref = butt.getPreferredSize();
//					maxWidth = maxWidth>pref.width?maxWidth:pref.width;
//					maxHeight = maxHeight>pref.height?maxHeight:pref.height;
//				}
//			}
//			
//			for (JButton butts: usedButts)
//				butts.setPreferredSize(new Dimension(maxWidth,maxHeight));
//
//			contentPanel.add(new JPanel());
//			
//			contentPanel.add(buttonBox);
//			contentPanel.setBorder(new EmptyBorder(20, 20, 16, 20));
//			this.setContentPane(contentPanel);
//			this.setResizable(false);
//			
//			if (customControls!=null)
//				this.pack();
//
////			setLocationRelativeTo(owner);
//			
//			Rectangle bounds = (owner==null)?GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds():owner.getBounds();
//			setLocation(bounds.x+(bounds.width/2-this.getPreferredSize().width), bounds.y+(bounds.height/2-this.getPreferredSize().height));
//		}
//		
//		public void setVisible(boolean b) {
//			if (butt!=null) {
//				butt.requestFocusInWindow();
//			}
//
//			super.setVisible(b);
//		}
//		
//		public Option getSelectedOption() {
//			return selectedOption;
//		}
//	}
	
//==[ Save Abort Dialog ]==========================================================================
	
	static private WeakHashMap<CompoundKey, SaveAbortDiscardDialog> saveAbortDiscardDialogWarden = new WeakHashMap<CompoundKey, SaveAbortDiscardDialog>();
	static public Option showSaveAbortDiscardDialog(JComponent parent, String documentName) {
		CompoundKey k = new CompoundKey(parent,documentName);
		SaveAbortDiscardDialog sd = saveAbortDiscardDialogWarden.get(k);
		if (sd == null) 
			saveAbortDiscardDialogWarden.put(k, sd = new SaveAbortDiscardDialog((Window)parent.getRootPane().getParent(), DEFAULT_ICON, documentName));
		
		sd.setVisible(true);
		return sd.getSelectedOption();
	}
	
	static class SaveAbortDiscardDialog extends JOptionSheet {
		private static final long serialVersionUID = 1L;

		private Box textBox = new Box(BoxLayout.Y_AXIS);
		private JLabel messageText = new JLabel();
		private JText commentText = new JText();
		
		public SaveAbortDiscardDialog(Window w, Icon i, String documentName) {
			super(w,i,Option.DISCARD,null,Option.ABORT,Option.SAVE);
			
			messageText.setFont(Font.decode("Lucida grande-bold-13"));
			messageText.setText( "Do you want to save\""+documentName+"\" ? " );
//			messageText.setMinimumWrapWidth(400);
			commentText.setFont(Font.decode("Lucida grande-plain-11"));
			commentText.setText("All changes get lost, if you do not save.");
			commentText.setMinimumWrapWidth(400);
			
			textBox.add(messageText);
			textBox.add(commentText);

			textBox.setBorder(new EmptyBorder(0,10,5,10));

			setControl(textBox);
		}
	}
	
//==[ Cancel Replace Dialog ]======================================================================
	
	static private WeakHashMap<CompoundKey, CancelReplaceDialog> cancelReplaceDialogWarden = new WeakHashMap<CompoundKey, CancelReplaceDialog>();
	static public Option showCancelReplaceDialog(JComponent parent, String documentName) {
		CompoundKey k = new CompoundKey(parent,documentName);
		CancelReplaceDialog cr = cancelReplaceDialogWarden.get(k);
		if (cr == null) 
			cancelReplaceDialogWarden.put(k, cr = new CancelReplaceDialog((Window)parent.getRootPane().getParent(), DEFAULT_ICON, documentName));
		
		cr.setVisible(true);
		return cr.getSelectedOption();
	}

	
	static private class CancelReplaceDialog extends JOptionSheet {
		private static final long serialVersionUID = 1L;
		
		private Box textBox = new Box(BoxLayout.Y_AXIS);
		private JLabel messageText = new JLabel();
		private JText commentText = new JText();
		
		public CancelReplaceDialog(Window w, Icon i, String documentName) {
			super(w,i,(Option)null,Option.ABORT,Option.REPLACE);
			
			messageText.setFont(Font.decode("Lucida grande-bold-13"));
			messageText.setText( "\""+documentName+"\" already exists. Do you want to replace?" );
//			messageText.setMinimumWrapWidth(400);
			commentText.setFont(Font.decode("Lucida grande-plain-11"));
			commentText.setText("Theres already a file or folder with an identical name. If you choose to replace, the file or folder is overwritten.");
			commentText.setMinimumWrapWidth(400);
			
			textBox.add(messageText);
			textBox.add(commentText);
			
			textBox.setBorder(new EmptyBorder(0,10,5,10));
			
			setControl(textBox);
		}
	}
	
//==[ Message Dialog ]=============================================================================
	
	static private WeakHashMap<CompoundKey, MessageDialog> messageDialogWarden = new WeakHashMap<CompoundKey, MessageDialog>();
	static public void showMessageDialog(JComponent parent, String message, String comment) {
		CompoundKey k = new CompoundKey(parent,message,comment);
		MessageDialog sd = messageDialogWarden.get(k);
		if (sd == null)
			messageDialogWarden.put(k, sd = new MessageDialog((Window)parent.getRootPane().getParent(), DEFAULT_ICON, message, comment));
		sd.setVisible(true);
	}
	
	static private class MessageDialog extends JOptionSheet {
		private static final long serialVersionUID = 1L;
		
		Box textBox = new Box(BoxLayout.Y_AXIS);
		JLabel messageText = new JLabel();
		JText commentText = new JText();
		
		public MessageDialog(Window w, Icon i, String message, String comment) {
			super(w,i,(Option)null,Option.OK);
			
			messageText.setFont(Font.decode("Lucida grande-bold-13"));
			messageText.setText( message );
//			messageText.setMinimumWrapWidth(400);

			commentText.setFont(Font.decode("Lucida grande-plain-11"));
			commentText.setText( comment );
			commentText.setMinimumWrapWidth(400);
			
			textBox.add(messageText);
			textBox.add(commentText);

			textBox.setBorder(new EmptyBorder(0,10,5,10));
			
			setControl(textBox);
		}
	}
	
//==[ Question Dialog ]============================================================================
	
	static private WeakHashMap<CompoundKey, QuestionDialog> questionDialogWarden = new WeakHashMap<CompoundKey, QuestionDialog>();
	static public String showQuestionDialog(JComponent parent, String message, String comment, String... options) { return showQuestionDialog(parent, message, comment, optionize(options)).getLabel(); }
	static public Option showQuestionDialog(JComponent parent, String message, String comment, Option... options) {
		return showQuestionDialog(parent, QUESTION_ICON, message, comment, options);
	}
	
	static public String showQuestionDialog(JComponent parent, Icon icon, String message, String comment, String... options) { return showQuestionDialog(parent, message, comment, optionize(options)).getLabel(); }
	static public Option showQuestionDialog(JComponent parent, Icon icon, String message, String comment, Option... options) {
		options=Arrays.copyOf(options, options.length+1);
		for (int i=options.length-1;i>0;i--) options[i] = options[i-1];
		options[0]=null;
		
		CompoundKey k = new CompoundKey(parent,message,comment,options);
		
		QuestionDialog sd = questionDialogWarden.get(k);
		if (sd == null)
			questionDialogWarden.put(k, sd = new QuestionDialog((Window)parent.getRootPane().getParent(), icon, message, comment, options) );
		
		sd.setVisible(true);
		return sd.getSelectedOption();
	}
	
	static private class QuestionDialog extends JOptionSheet {
		private static final long serialVersionUID = 1L;
		
		private Box textBox = new Box(BoxLayout.Y_AXIS);
		private JLabel messageText = new JLabel();
		private JLabel commentText = new JLabel();
		
		public QuestionDialog(Window w, Icon i, String message, String comment, String...options) { this(w,i,message,comment, optionize(options)); }
		public QuestionDialog(Window w, Icon i, String message, String comment, Option...options) {
			super(w,i,options);
			
//			messageText.setFont(Font.decode("Lucida grande-bold-13"));
			messageText.setFont(new JLabel().getFont().deriveFont(Font.BOLD).deriveFont(13f));
			messageText.setText( message );
//			messageText.setMinimumWrapWidth(400);

//			commentText.setFont(Font.decode("Lucida grande-plain-11"));
			commentText.setFont(new JLabel().getFont().deriveFont(11f));
			commentText.setText( comment );
//			commentText.setMinimumWrapWidth(400);
			
			textBox.add(messageText);
			textBox.add(commentText);

			textBox.setBorder(new EmptyBorder(0,10,5,10));

			setControl(textBox);
		}
	}
	
//==[ Input Dialog (Multiple Choice) ]=============================================================	
	
	static public Object showInputDialog(JComponent parent, String message, Object[] items, Object selectedItem) {
		ItemSelectionDialog id = new ItemSelectionDialog((Window)parent.getRootPane().getParent(), QUESTION_ICON, message, items, selectedItem);
		
		id.setVisible(true);
		if (id.getSelectedOption()==Option.OK)
			return id.itemBox.getSelectedItem();
		else
			return null;
	}
	
	static private class ItemSelectionDialog extends JOptionSheet {
		private static final long serialVersionUID = 1L;

		Box contentBox = new Box(BoxLayout.Y_AXIS);

		JLabel messageText = new JLabel();
		JComboBox itemBox = new JComboBox();

		public ItemSelectionDialog(Window w, Icon i, String message, Object[] items, Object item) {
			super(w,i,(Option)null,Option.ABORT,Option.OK);

			messageText.setFont(Font.decode("Lucida grande-bold-13"));
			messageText.setText( message );
			itemBox.setModel(new DefaultComboBoxModel(items));
			itemBox.setSelectedItem(item);
			contentBox.add(messageText);
			contentBox.add(itemBox);
			
			setControl(contentBox);
		}
	}
	
//==[ Input Dialog (Text) ]========================================================================
	
	static private WeakHashMap<CompoundKey, InputDialog> inputDialogWarden = new WeakHashMap<CompoundKey, InputDialog>();
	static public Object showInputDialog(JComponent parent, String message, Object defaultValue) {
		
		CompoundKey ck = new CompoundKey(parent,message,defaultValue); 
		
		InputDialog id = inputDialogWarden.get(ck);
		if (id == null)
			inputDialogWarden.put(ck, id = new InputDialog((Window)parent.getRootPane().getParent(), QUESTION_ICON, message, defaultValue));
		
		id.inputField.setText(defaultValue.toString());
		
		final InputDialog id_ = id;
		id.inputField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				id_.selectedOption = Option.OK;
				id_.setVisible(false);
			}
		});
		
		id.setVisible(true);
	
		if (id.getSelectedOption()==Option.OK)
			return id.inputField.getText();
		else
			return null;
	}

	static private class InputDialog extends JOptionSheet {
		private static final long serialVersionUID = 1L;

		private JPanel contentPanel = new JPanel(new BorderLayout());

		private JLabel messageText = new JLabel();
		private JTextField inputField = new JTextField(); 

		public InputDialog(Window w, Icon i, String message, Object defaultValue) {
			super(w,i,(Option)null,Option.CANCEL,Option.OK);

			messageText.setFont(Font.decode("Lucida grande-bold-13"));
			messageText.setText( message );
			inputField.setText( defaultValue.toString() );
			contentPanel.add(messageText,BorderLayout.CENTER);
			contentPanel.add(inputField,BorderLayout.SOUTH);
			contentPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
			
			setControl(contentPanel);
			
			addWindowListener(new WindowListener() {
				public void windowOpened(WindowEvent arg0) {}
				public void windowIconified(WindowEvent arg0) {}
				public void windowDeiconified(WindowEvent arg0) {}
				public void windowDeactivated(WindowEvent arg0) {}
				public void windowClosing(WindowEvent arg0) {}
				public void windowClosed(WindowEvent arg0) {}
				public void windowActivated(WindowEvent arg0) {
					SwingUtilities.invokeLater(new Runnable() { public void run() { inputField.requestFocus(); } });
				}
			});
			
			inputField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					inputField.transferFocusBackward();
				}
			});
			
			getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CANCEL_CLOSE");
			getRootPane().getActionMap().put("CANCEL_CLOSE", new AbstractAction() {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent e) {
					InputDialog.this.selectedOption = Option.ABORT;
					setVisible(false);
				}
			});
		}
	}
	
//==[ Open File Dialog ]===========================================================================
	
	static public File showOpenFileDialog(JComponent parent, File root, final String... filters) {
		final JDialog d = new JDialog ((Window)parent.getRootPane().getParent());
		d.setModalityType(ModalityType.DOCUMENT_MODAL);
		d.getRootPane().putClientProperty("apple.awt.documentModalSheet", Boolean.TRUE);
		
		JFileChooser fc = new JFileChooser(root) {
			private static final long serialVersionUID = 1L;

			public void cancelSelection() {
				super.cancelSelection();
				setSelectedFile(null);
				d.setVisible(false);
			};
			
			public void approveSelection() {
				super.approveSelection();
				d.setVisible(false);
			};
		};
		d.add(fc);
		
		
		for (final String filter: filters)
			fc.addChoosableFileFilter(new FileFilter() {
				public String getDescription() { return filter; }
				public boolean accept(File f) {
					if (f.isDirectory()) return true;
					return Pattern.matches(filter.replace("?", ".").replace(".", "\\.").replace("*", ".+"), f.getName());
				}
			});
			
		
		d.pack();
		d.setVisible(true);
		
		return fc.getSelectedFile();
	}
	
//==[ Save Dialog ]================================================================================
	
	static public File showSaveFileDialog(JComponent parent, File root, String proposedName, String... filters) {
		return showSaveFileDialog(parent, root, proposedName, false, filters);
	}
	
	static public File showSaveFileDialog(JComponent parent, File root, String proposedName, boolean approveOverwrite, String... filters) {
		final JDialog d = new JDialog ((Window)parent.getRootPane().getParent());
		d.setModalityType(ModalityType.DOCUMENT_MODAL);
		d.getRootPane().putClientProperty("apple.awt.documentModalSheet", Boolean.TRUE);
		
		JFileChooser fc = new JFileChooser(root) {
			private static final long serialVersionUID = 1L;
			public void cancelSelection() { setSelectedFile(null); d.setVisible(false); };
			public void approveSelection() { d.setVisible(false); };
		};
		d.add(fc);
		
		fc.setSelectedFile(new File(root,proposedName));
		
		for (final String filter: filters) {
			fc.addChoosableFileFilter(new FileFilter() {
				public String getDescription() { return filter; }
				public boolean accept(File f) {
					if (f.isDirectory()) return true;
					return Pattern.matches(filter.replace("?", ".").replace(".", "\\.").replace("*", ".+"), f.getName());
				}
			});
		}
		
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		
		d.pack();
		d.setVisible(true);
		
		if(approveOverwrite) {
			while((fc.getSelectedFile() != null) &&
					(fc.getSelectedFile().exists() || new File(fc.getSelectedFile().getAbsolutePath() + fc.getFileFilter().getDescription().replace("*", "")).exists()) &&
					(showCancelReplaceDialog(parent, fc.getSelectedFile().getName()) != Option.REPLACE)) {
				d.setVisible(true);
			}
		}
		
		return fc.getSelectedFile();
	}
	

//==[ Progress Dialog ]============================================================================
	
	static public interface TaskProgress {
		public void setIndeterminate(boolean indet);
		public boolean isIndeterminate();
		public void setMessage (String action);
		public void setProgress(int done, int total);
		public int getValue();
		public int getMaximum();
		public String getMessage();
		public boolean taskWasCanceled();
		public void taskDone();
	}
	
	static public interface Task {
		public void execute(TaskProgress pc);
	};
	
	static private class ProgressDialog extends JOptionSheet implements TaskProgress {
		private static final long serialVersionUID = 1L;

		private JPanel contentPanel = new JPanel(new BorderLayout());
		private JLabel messageText = new JLabel();
		private JProgressBar progressBar = new JProgressBar();
		
		public ProgressDialog(Window w, String message) {
			super(w,null,(Option)null,Option.ABORT);

			messageText.setFont(Font.decode("Lucida grande-bold-13"));
			messageText.setText( message );
			messageText.setBorder(new EmptyBorder(0, 0, 8, 0));
			
			contentPanel.add(messageText,BorderLayout.CENTER);
			contentPanel.add(progressBar,BorderLayout.SOUTH);
			contentPanel.setBorder(new EmptyBorder(0, 0, 10, 20));
			
			progressBar.setPreferredSize(new Dimension(300,progressBar.getPreferredSize().height));
			setControl(contentPanel);
		}
		
		public void setIndeterminate(boolean indet) {
			progressBar.setIndeterminate(indet);
		}
		
		public boolean isIndeterminate() {
			return progressBar.isIndeterminate();
		}
		
		public void setProgress(int done, int total) {
			progressBar.setMaximum(total);
			progressBar.setValue(done);
		}
		
		public int getValue() {
			return progressBar.getValue();
		}
		
		public int getMaximum() {
			return progressBar.getMaximum();
		}
		
		public boolean taskWasCanceled() {
			return getSelectedOption()==Option.ABORT;
		}

		public void setMessage(String action) { // XXX Buggy for long strings (JOptionSheet not centered anymore, abort button not visible)
			messageText.setText(action);
		}
		
		public String getMessage() {
			return messageText.getText();
		}

		public void taskDone() {
			setVisible(false);
		}

	}
	
	static public void showProgressDialog(JComponent parent, String message, final Task t) {
		parent = (parent != null)?parent.getRootPane():null;
		final ProgressDialog pd = new ProgressDialog((parent!=null)?(Window)parent.getParent():null, message);
		
		new Thread("JOptionSheet ProgressDialog Thread") {
			public void run() {
				try {
					t.execute(pd);
				} catch(Exception ex) {
					ex.printStackTrace();
				} finally {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							pd.setVisible(false);							
						}
					});
				}
			};
		}.start();
		
		pd.setVisible(true);
		
	}
	
//==[ Dialog Wrapper ]=============================================================================
	
	public static void makeSheet(Dialog dialog) {
	    dialog.addNotify();
	    @SuppressWarnings("deprecation") ComponentPeer peer = dialog.getPeer();

	    // File dialogs are CFileDialog instead. Unfortunately this means this hack
	    // can't work for those. :(
	    if (peer.getClass().getName().equals("sun.lwawt.LWWindowPeer")) try {
//	        LWWindowPeer windowPeer = (LWWindowPeer) dialog.getPeer();
	        //XXX: Should check this before casting too.
//	        CPlatformWindow platformWindow = (CPlatformWindow) windowPeer.getPlatformWindow();
	    	Method method1 = peer.getClass().getMethod( "getPlatformWindow");
	    	
	    	Object platformWindow = method1.invoke(peer); 
	    	
            Method method = platformWindow.getClass().getDeclaredMethod( "setStyleBits", int.class, boolean.class);
            method.setAccessible(true);
            method.invoke(platformWindow, 64 /* CPlatformWindow.SHEET */, true);

//            Window parent = dialog.getOwner();
//            dialog.setLocation(dialog.getLocation().x, parent.getLocation().y + parent.getInsets().top);
	    } catch (Exception e) {
	    	System.out.println("Couldn't call setStyleBits: "+e);
	    }
	}

	
	private String widen(String w) {
		String t = w;
		while (t.length()<8) t = " "+t+" ";
		return t;
	}

}
