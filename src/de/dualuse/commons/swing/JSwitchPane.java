package de.dualuse.commons.swing;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//import sun.swing.SwingUtilities2;



public class JSwitchPane extends JPanel {
	static final public ImageIcon DEFAULT_ICON = new ImageIcon(Toolkit.getDefaultToolkit().createImage("NSImage://NSCaution"));
	static final public ImageIcon LEFT_ICON = new ImageIcon(Toolkit.getDefaultToolkit().createImage("NSImage://NSLeftFacingTriangleTemplate"));
	static final public ImageIcon RIGHT_ICON = new ImageIcon(Toolkit.getDefaultToolkit().createImage("NSImage://NSRightFacingTriangleTemplate"));
	
	static class DraggableWindowBackgroundListener extends MouseAdapter {
		public final static DraggableWindowBackgroundListener singleton = new DraggableWindowBackgroundListener();
		
		private DraggableWindowBackgroundListener() {
		}
		
		
		int startX, startY;
		int windowX, windowY;
		Window captured; 
		
		public void mousePressed(java.awt.event.MouseEvent e) {
			startX = e.getXOnScreen();
			startY = e.getYOnScreen();
			
			Object src = e.getSource();
			if (!(src instanceof JComponent))
				return;
			
			JComponent srcComponent = (JComponent) src;
			Container srcContainer = srcComponent.getRootPane().getParent();
			
			if (!(srcContainer instanceof Window))
				return;
			
			captured = (Window) srcContainer;
				
			Point p = captured.getLocation();
			windowX = p.x;
			windowY = p.y;
			
		};
		

		public void mouseReleased(java.awt.event.MouseEvent e) {
			captured = null;
		};
		
		public void mouseDragged(java.awt.event.MouseEvent e) {
			Window w = captured;
			if (w==null)
				return;

			int newX = windowX+(e.getXOnScreen()-startX), newY = windowY+(e.getYOnScreen()-startY);
			
			w.setLocation(newX, newY);
		};
		
	}
	
	
	static public class MetalBar extends JToolBar {

		private static final long serialVersionUID = 1L;
		
		public MetalBar() {
			setOpaque(false);
			setFloatable(false);
			setBorder(new EmptyBorder(0, 4, 4, 4));
			addMouseListener(DraggableWindowBackgroundListener.singleton);
			addMouseMotionListener(DraggableWindowBackgroundListener.singleton);
		}
	}
	
	public class ToolBar extends MetalBar {
		private static final long serialVersionUID = 1L;
		private ButtonGroup selectionButtonGroup = new ButtonGroup();
		
		PopupMenu pm = new PopupMenu();
		private ArrayList<MenuItem> currentlyInvisible = new ArrayList<MenuItem>();
		private ArrayList<CheckboxMenuItem> menuitems = new ArrayList<CheckboxMenuItem>();
		ArrayList<JToggleButton> buttons = new ArrayList<JToggleButton>();
		JToggleButton rest = null, next = null, prev = null;
		
		class ToolBarLayoutManager implements LayoutManager {
			public void addLayoutComponent(String name, Component comp) { }
			public void removeLayoutComponent(Component comp) { }
			public Dimension minimumLayoutSize(Container parent) { return null; }
			public Dimension preferredLayoutSize(Container parent) { 
				Insets in = parent.getInsets();
				int sumX = in.left, maxY = 0;
				for (int i=0,l=parent.getComponentCount();i<l;i++) {
					Component comp = parent.getComponent(i);
					if (comp==prev || comp == next || comp == rest)
						continue;
					
					Dimension ps = comp.getPreferredSize();
					sumX += ps.width;
					maxY = maxY>ps.height?maxY:ps.height;
				}
				
				return new Dimension(sumX,maxY+in.top+in.bottom);
			}
			
			public void layoutContainer(Container parent) {
				Insets in = parent.getInsets();
				
				Dimension prefRest = rest.getPreferredSize();
				int sx = in.left,lx = sx, cy = in.top+(getHeight()-in.top-in.bottom)/2, maxX = parent.getWidth()-in.right-prefRest.width;
				
				int i=0,l =parent.getComponentCount();
				for (;i<l;i++) {
					Component comp = parent.getComponent(i);
					
					if (comp==prev || comp == next || comp == rest)
						continue;
					
					Dimension pref = comp.getPreferredSize();
					comp.setBounds(sx,cy-pref.height/2,pref.width,pref.height);
					
					sx+=pref.width;
					lx+=sx<maxX?pref.width:0;
					parent.getComponent(i).setVisible(sx<maxX);
				}
				
				rest.setBounds(lx,cy-prefRest.height/2,prefRest.width,prefRest.height);
				rest.setVisible(sx>maxX);
			}
		}
		
		private ActionListener nextListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				next.setSelected(false);
				for (int i=1;i<buttons.size();i++)
					if (buttons.get(i-1).isSelected()) {
						buttons.get(i).setSelected(true);
						doLayout();
						return;
					}
			}
		};
		
		private ActionListener prevListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prev.setSelected(false);
				for (int i=0;i<buttons.size()-1;i++)
					if (buttons.get(i+1).isSelected()) {
						buttons.get(i).setSelected(true);
						doLayout();
						return;
					}
			}
		};
		
		private ActionListener popupListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rest.setSelected(false);
				
				for (int i=0;i<menuitems.size();i++)
					menuitems.get(i).setState(buttons.get(i).isSelected());

				pm.removeAll();
				for (int i=0;i<currentlyInvisible.size();i++)
					pm.add(currentlyInvisible.get(i));
				
				pm.show(rest, 0, rest.getHeight());
			}
		};
		
		
		protected JToggleButton createTabSelectionButton(int index, String tit, Icon ic) {
			JToggleButton sel = new JToggleButton(tit,ic);
			sel.setHorizontalTextPosition(SwingUtilities.CENTER);
			sel.setVerticalTextPosition(SwingUtilities.BOTTOM);
			return sel;
		}
		
		protected JToggleButton createNextTabButton() {
			return new JToggleButton("",RIGHT_ICON);
		}
		
		protected JToggleButton createPreviousTabButton() {
//			return createTabSelectionButton(buttons.size(),""+(char)0x25C0, DEFAULT_ICON);
			return new JToggleButton("",LEFT_ICON);
		}
		
		protected JToggleButton createPopupButton() {
//			return createTabSelectionButton(buttons.size(),""+(char)0x25BC, DEFAULT_ICON);
			return new JToggleButton("",RIGHT_ICON);
		}
		
		public void doLayout() {
			super.doLayout();
			
			Rectangle r = getBounds();
			synchronized(this) {
				currentlyInvisible.clear();
				
				for (int i=0;i<buttons.size();i++)
					if (buttons.get(i).getBounds().intersection(r).isEmpty() || !buttons.get(i).isVisible())
						currentlyInvisible.add(menuitems.get(i));
				
			}
		}
		
		public ToolBar() {
			setLayout(new ToolBarLayoutManager());
			
			JSwitchPane.this.addContainerListener(new ContainerAdapter() {
				public void componentAdded(ContainerEvent e) {
					rebuild();
				}
				
				public void componentRemoved(ContainerEvent e) {
					rebuild();
				}
			});
			
			JSwitchPane.this.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					buttons.get(getSelectedIndex()).setSelected(true);
				}
			});
			rebuild();
		}
		
		protected void rebuild() {
			this.removeAll();
			buttons.clear();

			menuitems.clear();
			pm.removeAll();
			
			selectionButtonGroup = new ButtonGroup();
			
			int i=0;
			for (Page p: pages) {
				final int j = i++;
				final JToggleButton sel = createTabSelectionButton(j, p.title==null?"":p.title, p.icon==null?DEFAULT_ICON:p.icon);
				sel.setToolTipText(p.tip);

				selectionButtonGroup.add(sel);
				
				buttons.add(sel);
				
				final CheckboxMenuItem cbm = new CheckboxMenuItem(p.title);
				cbm.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if(cbm.getState())
							sel.setSelected(true);
					}
				});
				menuitems.add(cbm);
				pm.add(cbm);
				
				if (i-1==selectedIndex)
					sel.setSelected(true);
				
				sel.addChangeListener(new ChangeListener() {
					int tabIndex = j;
					
					public void stateChanged(ChangeEvent e) {
						if (!((JToggleButton)e.getSource()).isSelected())
							return;
						
						if (getSelectedIndex()!=tabIndex)
							setSelectedIndex(tabIndex);
					}
				});
				
				add(sel);
			}
			
			next = createNextTabButton();
			add(next);
			next.addActionListener(nextListener);
			
			prev = createPreviousTabButton();
			add(prev,0);
			prev.addActionListener(prevListener);
			
			rest = createPopupButton();
//			rest.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					System.out.println("bl��k");
//				}
//			});
			rest.add(pm);
			rest.addActionListener(popupListener);
			
			add(rest);
			
			repaint();
		}
	}
	
	
	public class TabBar extends ToolBar {
		private static final long serialVersionUID = 1L;
		
		{
			setBorder(null);
		}

		class TabLayoutManager extends ToolBarLayoutManager {
			int pivotX = 0;
			
			public void layoutContainer(Container parent) {
				Insets in = parent.getInsets();
				Dimension prefPrev = prev.getPreferredSize(), prefNext = next.getPreferredSize(); 
				
				int sx = in.left,lx = sx, cy = in.top+(getHeight()-in.top-in.bottom)/2-1, maxX = parent.getWidth()-in.right-in.left;
				
				
				if (buttons.size()==0)
					return;
				
				if (buttons.size()==1) {
					Component comp = buttons.get(0);
					Dimension pref = comp.getPreferredSize();
					comp.setBounds(sx+pivotX,cy-pref.height/2,maxX,pref.height);
					return;
				}
				
				
				
				int leftX = 0, rightX = 0;
				
				int i=0,l =parent.getComponentCount();
				for (;i<l;i++) {
					Component comp = parent.getComponent(i);
					
					if (comp==prev || comp == next || comp == rest)
						continue;
					
					Dimension pref = comp.getPreferredSize();
					comp.setBounds(sx+pivotX,cy-pref.height/2,pref.width,pref.height);
					
					if ( ((JToggleButton)comp).isSelected() ) {
						leftX = comp.getX();
						rightX = leftX+comp.getWidth();
					}
							
					sx+=pref.width;
					lx+=sx<maxX?pref.width:0;
				}
				
				int skipLeft = 0, skipX = 0;
				while (rightX-skipX+prefPrev.width+prefNext.width>maxX) {
					Component comp = parent.getComponent(skipLeft++);
					if (comp==prev || comp == next || comp == rest)
						continue;
					skipX+=comp.getWidth();
				}

				if (skipLeft>0)
					skipX-=prefPrev.width;
				
				int cutX = 0;
				if (sx-skipX>maxX)
					cutX = prefNext.width;
						
				int restX = 0, numVisTabs=0;
				for (int j=0;j<l;j++) {
					Component comp = parent.getComponent(j);
					comp.setLocation(comp.getX()-(j>=skipLeft?skipX:0), comp.getY());
					comp.setVisible(j>=skipLeft && comp.getX()+comp.getWidth()+cutX<=maxX);
					
					if (comp==prev || comp == next || comp == rest || !comp.isVisible())
						continue;
					
					restX += comp.getWidth();
					numVisTabs++;
				}
				
				prev.setBounds(in.left,cy-prefPrev.height/2,prefPrev.width,prefPrev.height);
				prev.setVisible(skipLeft>0);
				
				next.setBounds(-1+getWidth()-in.right-prefNext.width,cy-prefNext.height/2,prefNext.width+1,prefNext.height);
				next.setVisible(cutX>0);
				
				int toBeDistributedOnTabs = getWidth()-in.left-in.right-(skipLeft>0?prefPrev.width:0)-(cutX>0?prefNext.width:0)-restX;
				if (numVisTabs==0)
					return;
				
				double perTab = toBeDistributedOnTabs*1./numVisTabs, r =0;
				for (int j=0,k=0,s=0;j<l;j++) {
					Component comp = parent.getComponent(j);
					if (comp==prev || comp == next || comp == rest || !comp.isVisible())
						continue;
					
					comp.setLocation(comp.getX()+s,comp.getY());
					comp.setSize(comp.getWidth()+(int)r+(int)perTab,comp.getHeight());
					
					s += (int)r+(int)perTab;
					r += perTab-(int)perTab-(int)r;
					
					k++;
				}
				
				
			}
		}
		
		
		protected JToggleButton createTabSelectionButton(int index, String tit, Icon ic) {
			JToggleButton button = new JToggleButton(tit);
			button.putClientProperty("JButton.buttonType", "segmented");
//			button.putClientProperty("JButton.segmentPosition", "middle"); //first, middle, last, only
			button.putClientProperty("JButton.segmentPosition", JSwitchPane.this.getComponentCount()==1?"only":(index==0?"first":(index==JSwitchPane.this.getComponentCount()-1?"last":"middle"))); //first, middle, last, only
			
			Dimension dim = button.getPreferredSize();
			button.setPreferredSize(new Dimension(dim.width,23));
			return button;
		}
		
		protected JToggleButton createPreviousTabButton() {
			JToggleButton tb = super.createPreviousTabButton();
			tb.putClientProperty("JButton.buttonType", "segmented");
			tb.putClientProperty("JButton.segmentPosition", "first");
			Dimension dim = tb.getPreferredSize();
			tb.setPreferredSize(new Dimension(dim.width,23));
			return tb;
		}
		
		protected JToggleButton createNextTabButton() {
			JToggleButton tb = super.createNextTabButton();
			tb.putClientProperty("JButton.buttonType", "segmented");
			tb.putClientProperty("JButton.segmentPosition", "last");
			Dimension dim = tb.getPreferredSize();
			tb.setPreferredSize(new Dimension(dim.width,23));
			return tb;
		}
		
		protected JToggleButton createPopupButton() {
			JToggleButton tb = super.createPopupButton();
			tb.putClientProperty("JButton.buttonType", "segmented");
			tb.putClientProperty("JButton.segmentPosition", "last");
			Dimension dim = tb.getPreferredSize();
			tb.setPreferredSize(new Dimension(dim.width,23));
			return tb;
		}
		
		public TabBar() {
			setLayout(new TabLayoutManager());
		}
	}
	
	
	private static final long serialVersionUID = 1L;
	
	public JSwitchPane() {
		super.setLayout(new BorderLayout());
//		setOpaque(false);
	}
	
	public static class Page {
		String title;
		Icon icon;
		Component component;
		String tip;

		public Page(String title, Icon icon, Component component, String tip) {
			this.title = title;
			this.icon = icon;
			this.component = component;
			this.tip = tip;
		}
	}
	
	public Component add(Component comp) {
		add(comp.getName(), null, comp, comp.getName(), pages.size());
		return comp;
	}
	
	public Component add(Component comp, int index) {
		add(comp.getName(), null, comp, comp.getName(), index);
		return comp;
	}
	
	public void add(Component comp, Object constraints) {
		this.add(comp);
	}
	public void add(Component comp, Object constraints, int index) {
		this.add(comp,index);
	}
	
	public Component addTab(String name, Component comp) {
		add(name, null, comp, name, pages.size());
		return comp;
	}
	
	public Component add(String name, Component comp) {
		add(name, null, comp, name, pages.size());
		return comp;
	}
	
	public void remove(Component comp) {
		for (int i=0;i<pages.size();i++)
			if (pages.get(i).component==comp) {
				pages.remove(i);
				super.remove(i);
				
				
				if (selectedIndex>pages.size()-1)
					setSelectedIndex(pages.size()-1);
				else
					selectionChanged(false);
				
				revalidate();
				repaint();
				return;
			}
		

	}
	
	public void remove(int i) {
		pages.remove(i);
		super.remove(i);
		if (selectedIndex>pages.size()-1)
			setSelectedIndex(pages.size()-1);
		
		revalidate();
		repaint();
		return;
	}
	
	public void removeAll() {
		pages.clear();
		super.removeAll();
		setSelectedIndex(-1);

		revalidate();
		repaint();
	}
	
	public Component getComponent(int n) {
		return pages.get(n).component;
	}
	
	public int getComponentCount() { return pages.size(); };
	
	public Component[] getComponents() {
		Component[] components = new Component[getComponentCount()];
		for (int i=0;i<pages.size();i++)
			components[i] = pages.get(i).component;
		return components;
	}
	
	private ArrayList<Page> pages = new ArrayList<Page>();
	
	public void add(String title, Icon icon, Component component, String tip) {
		add(title,icon,component,tip,pages.size());
	}
	
	public void add(String title, Icon icon, Component component, String tip, int index) {
		pages.add(index, new Page(title, icon, component, tip));
		
		if (component != null) {
			addImpl(component, null, index);
			component.setVisible(false);
		}
		
		selectedIndex = index;
		selectionChanged(false);
		
		revalidate();
		repaint();
	}

	private int selectedIndex = -1;

	public void setSelectedComponent(Component c) {
		for (int i=0,l=pages.size();i<l;i++)
			if (pages.get(i).component==c || pages.get(i).component.equals(c))
				setSelectedIndex(i);
	}
	
	public void setSelectedIndex(int index) {
		deltaIndex = (index-selectedIndex);
		selectedIndex = index;
		selectionChanged(true);
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	private Component visComp = null;

	protected void selectionChanged(boolean fade) {
		
		fade = isVisible()&fade;
		
		int selIndex = getSelectedIndex();
		
		if (pages.size()==0)
			return;
		
		Page newTab = pages.get(selIndex);

		Component hidingComponent = null, showingComponent = null;
		Component newComp = newTab.component;
		if (selIndex < 0) {
			if (visComp != null && visComp.isVisible())
				hidingComponent = visComp;

			visComp = null;
		} else if (newComp != null && newComp != visComp) {
			boolean shouldChangeFocus = false;

			if (visComp != null) {
				shouldChangeFocus = (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == null ? false : (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner().getParent() == visComp));

				if (visComp.isVisible())
					hidingComponent = visComp;
			}

			if (!newComp.isVisible())
				showingComponent = newComp;

			if (shouldChangeFocus)
				tabbedPaneChangeFocusTo(newComp);

			visComp = newComp;
		}

		if (fade && transitionAnimationEnabled)
			switchComponents(hidingComponent,showingComponent);
		else 
		{
			if (hidingComponent!=null)
				hidingComponent.setVisible(false);
				
			if (showingComponent!=null)
				showingComponent.setVisible(true);
			
			revalidate();
		}
			
		for (ChangeListener cl: cls)
			cl.stateChanged(new ChangeEvent(this));
		
	}	
	
	public static boolean tabbedPaneChangeFocusTo(Component comp) {
		if (comp != null) {
			if (comp.isFocusTraversable()) {
				compositeRequestFocus(comp);
				return true;
			} else if (comp instanceof JComponent && ((JComponent) comp).requestDefaultFocus()) {

				return true;
			}
		}

		return false;
	}
	public static Component compositeRequestFocus(Component component) {
		if (component instanceof Container) {
			Container container = (Container) component;
			if (container.isFocusCycleRoot()) {
				FocusTraversalPolicy policy = container.getFocusTraversalPolicy();
				Component comp = policy.getDefaultComponent(container);
				if (comp != null) {
					comp.requestFocus();
					return comp;
				}
			}
			Container rootAncestor = container.getFocusCycleRootAncestor();
			if (rootAncestor != null) {
				FocusTraversalPolicy policy = rootAncestor.getFocusTraversalPolicy();
				Component comp = policy.getComponentAfter(rootAncestor, container);

				if (comp != null && SwingUtilities.isDescendingFrom(comp, container)) {
					comp.requestFocus();
					return comp;
				}
			}
		}
		if (component.isFocusable()) {
			component.requestFocus();
			return component;
		}
		return null;
	}
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (fade<1)
			paintTransition(g, fadeOut, fadeIn, fade, deltaIndex);
		
	}
	
	
	protected void paintTransition(Graphics g, BufferedImage from, BufferedImage to, float progress, int deltaIndex) {
		progress = 1-(float)Math.exp(-progress*6);
		
		g = g.create();
		g.drawImage(from, 0, 0, this);
		((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, progress));
		g.drawImage(to, 0, 0, this);
	}
	
	private Timer fader = new Timer(FRAME_DURATION_MS, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			fade = (float)((System.nanoTime()-start)/1e9/seconds);
			
			if (fade>1) {
				fade = 1;
				fader.stop();
				visComp.setVisible(true);
				Container c = getParent();
				if (c!=null)
					c.repaint();
				
//				setPreferredSize(null); //delete manually set preferred size
				revalidate();
			}
			
			repaint();
		}
	});
	
	private CopyOnWriteArrayList<ChangeListener> cls = new CopyOnWriteArrayList<ChangeListener>();
	public void addChangeListener(ChangeListener cl) { cls.add(cl); }
	public void removeChangeListener(ChangeListener cl) { cls.remove(cl); }
	
	public float getTransitionAnimationDuration() { return seconds; }
	public void setTransitionAnimationDuration(double seconds) { this.seconds = (float)seconds; }
	
	boolean transitionAnimationEnabled = true;
	public void setTransitionAnimationEnabled(boolean state) { this.transitionAnimationEnabled = state; }
	public boolean isTransitionAnimationEnabled() { return transitionAnimationEnabled; }
	
	final static int FRAME_DURATION_MS = 16;

	private long start = System.nanoTime();
	private float seconds = .4f;
	private float fade = 1;
	
	private BufferedImage fadeIn = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	private BufferedImage fadeOut = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	
	private int deltaIndex = 0;
	
	public Dimension getPreferredSize() {
		return visComp==null?new Dimension(0,0):visComp.getPreferredSize();
	}

	
	final protected void switchComponents(Component hidingComponent, Component showingComponent) {
		
		if (deltaIndex==0)
			return;

		if (hidingComponent!=null)
			hidingComponent.setVisible(false);

		if (showingComponent!=null)
			showingComponent.setVisible(true);

//		setPreferredSize(getPreferredSize()); //set explicit preferred size 
		
		if (showingComponent!=null)
			showingComponent.setVisible(false);
		
		if (fade<1 || seconds*1000<FRAME_DURATION_MS*2) {
			fade = 1;
			fader.stop();
			if (hidingComponent!=null)
				hidingComponent.setVisible(false);

			if (showingComponent!=null)
				showingComponent.setVisible(true);
			
//			setPreferredSize(null); //delete manually set preferred size
			revalidate();
			
			repaint();
		} else {
			Dimension hidingDim = new Dimension(0,0), showingDim = new Dimension(0,0);
			if (hidingComponent!=null)
				hidingDim = hidingComponent.getPreferredSize();
			
			if (showingComponent!=null)
				showingDim = showingComponent.getPreferredSize();
			
			
//			Dimension hidingDim = hidingComponent!=null?hidingComponent.getPreferredSize():new Dimension(0,0), showingDim = showingComponent!=null?showingComponent.getPreferredSize():new Dimension(0,0);
			
			hidingDim.width = getWidth();hidingDim.height = Math.max(hidingDim.height, getHeight());
			showingDim.width = getWidth();showingDim.height = Math.max(showingDim.height, getHeight());
//			int mw = Math.min(hidingDim.width, showingDim.width), mh = Math.max(hidingDim.height, hidingDim.width); 
			
			if (fadeIn.getWidth()<showingDim.width || fadeIn.getHeight()<showingDim.height)
				fadeIn = new BufferedImage(1+showingDim.width*3/2,1+ showingDim.height*3/2, BufferedImage.TYPE_INT_RGB);
			
			if (fadeOut.getWidth()<hidingDim.width || fadeOut.getHeight()<hidingDim.height)
				fadeOut = new BufferedImage(1+hidingDim.width*3/2,1+hidingDim.height*3/2, BufferedImage.TYPE_INT_RGB);
			
			Insets in = getInsets();
			Graphics g = fadeIn.getGraphics(); 
			g.setColor(this.getBackground());
//			g.setColor(Color.ORANGE);
			g.fillRect(0, 0, fadeIn.getWidth(), fadeIn.getHeight());
			if (showingComponent!=null) {
				Graphics gr = fadeIn.getGraphics();
				gr.translate(in.left, 0*in.top);
				
				showingComponent.setSize(showingDim);
				showingComponent.paint(gr);
			}
			
			Graphics e = fadeOut.getGraphics(); 
			e.setColor(this.getBackground());
//			e.setColor(Color.RED);
			e.fillRect(0, 0, fadeOut.getWidth(), fadeOut.getHeight());
			if (hidingComponent!=null) {
				Graphics gr = fadeOut.getGraphics();
				gr.translate(in.left, 0*in.top);
				
				hidingComponent.setSize(hidingDim);
				hidingComponent.paint(gr);
			}
			
			fade = 0;
			start = System.nanoTime();
			fader.start();
			fader.setRepeats(true);
			
			if (hidingComponent!=null)
				hidingComponent.setVisible(false);
		}
	}
	
	public void doLayout() {
		Insets in = getInsets();
		for (int i = 0, l = getComponentCount(); i < l; i++)
			getComponent(i).setBounds(in.left, in.top, getWidth()-in.left-in.right, getHeight()-in.top-in.bottom);
	}

	public static void main(String[] args) {
		
		JFrame f = new JFrame();

		f.getRootPane().putClientProperty("apple.awt.brushMetalLook", true);
		final JSwitchPane sp = new JSwapPane();
		
		final JSwitchPane.ToolBar tb = sp.new TabBar();
		
		/*
		final JSwitchPane.ToolBar tb = sp.new TabBar() {
			private static final long serialVersionUID = 1L;
			
			{
				setBorder(new LineBorder(Color.ORANGE));
//				setLayout(new FlowLayout());
//				setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
//				setLayout(new GridLayout(1,4));
			}
			
	
			protected JToggleButton createPreviousTabButton() {
				JToggleButton tb = super.createPreviousTabButton();
				tb.putClientProperty("JButton.buttonType", "segmented");
				tb.putClientProperty("JButton.segmentPosition", "first");
				Dimension dim = tb.getPreferredSize();
				tb.setPreferredSize(new Dimension(dim.width,23));
				return tb;
			}
			
			protected JToggleButton createNextTabButton() {
				JToggleButton tb = super.createNextTabButton();
				tb.putClientProperty("JButton.buttonType", "segmented");
				tb.putClientProperty("JButton.segmentPosition", "last");
				Dimension dim = tb.getPreferredSize();
				tb.setPreferredSize(new Dimension(dim.width,23));
				return tb;
			}
			
			protected JToggleButton createPopupButton() {
				JToggleButton tb = super.createPopupButton();
				tb.putClientProperty("JButton.buttonType", "segmented");
				tb.putClientProperty("JButton.segmentPosition", "last");
				Dimension dim = tb.getPreferredSize();
				tb.setPreferredSize(new Dimension(dim.width,23));
				return tb;
			}
			
			protected JToggleButton createTabSelectionButton(int index, String tit, Icon ic) {
				JToggleButton button = new JToggleButton(tit);
				button.putClientProperty("JButton.buttonType", "segmented");
				
//				button.putClientProperty("JButton.segmentPosition", "middle"); //first, middle, last, only
				button.putClientProperty("JButton.segmentPosition", sp.getComponentCount()==1?"only":(index==0?"first":(index==sp.getComponentCount()-1?"last":"middle"))); //first, middle, last, only
				
				Dimension dim = button.getPreferredSize();
				button.setPreferredSize(new Dimension(dim.width,23));
				return button;
			}
		};*/
		
		sp.setBorder(new LineBorder(Color.BLUE));
		
		f.getContentPane().add(sp);
		f.getContentPane().add(tb,BorderLayout.NORTH);
		
		f.getContentPane().add(new JButton(new AbstractAction() {
			private static final long serialVersionUID = 1L;
			
			int counter = 1;
			public void actionPerformed(ActionEvent e) {
				JLabel lb = new JLabel(""+new Date());
				
				lb.setOpaque(true);
				lb.setBackground(new Color(Color.HSBtoRGB((float)Math.random(), 0.5f, 1f)));
				sp.add("hallo "+counter++,lb);
			}
		}),BorderLayout.SOUTH);
		
		f.getContentPane().add(new JButton(new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				sp.remove(sp.getComponent(sp.getSelectedIndex()));
			}
		}),BorderLayout.EAST);
		
		f.setBounds(100, 100, 600, 500);
		f.setVisible(true);
		
		/*
		JFrame f = new JFrame();

		final JSwitchPane sp = new JSwapPane();

		sp.add(new JTree());
		sp.add(new JLabel("blablabla"));
		sp.add(new JLabel(new ImageIcon("/Library/Desktop Pictures/Nature/Cirques.jpg")));
		
//		sp.insertTab("first", null, new JTree(), "hallo", 0);
//		sp.insertTab("second", null, new JLabel("mist"), "welt", 0);
//		sp.insertTab("third", null, new JLabel(new ImageIcon("/Library/Desktop Pictures/Nature/Cirques.jpg")), "bild", 0);

		f.getContentPane().add(sp);
		f.getContentPane().add(new JButton(new AbstractAction("next") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (sp.getSelectedIndex() < sp.getComponentCount() - 1)
					sp.setSelectedIndex(sp.getSelectedIndex() + 1);
			}
		}), BorderLayout.EAST);

		f.getContentPane().add(new JButton(new AbstractAction("prev") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (sp.getSelectedIndex() > 0)
					sp.setSelectedIndex(sp.getSelectedIndex() - 1);
			}
		}), BorderLayout.WEST);

		f.getContentPane().add(new JButton(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				sp.repaint();
//				BufferedImage bi = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
//				sp.visComp.paint(bi.getGraphics());
//
//				JFrame g = new JFrame();
//				g.setContentPane(new JLabel(new ImageIcon(bi)));
//				g.pack();
//				g.setLocation(2000, 100);
//				g.setVisible(true);
			}
		}), BorderLayout.SOUTH);
		
		f.getContentPane().add(new JButton(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				sp.removeAll();
				sp.repaint();
			}
		}),BorderLayout.NORTH);
		
		f.setBounds(300, 100, 800, 500);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		*/
	}
}
