package de.dualuse.commons.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class JWidgetTree extends JTree implements TreeModel, TreeCellRenderer, PropertyChangeListener, ContainerListener {
	private static final long serialVersionUID = 1L;
	
	
	final JComponent root;
	public JWidgetTree(JComponent root) { this.root = root;super.setModel(this); super.setCellRenderer(this); super.setRowHeight(0); root.addContainerListener(this);}
	public JWidgetTree(JComponent root, int cellspacing) { this(root); setCellSpacing(cellspacing); }
	
	@Override public Object getRoot() { return root; }

	@Override public int getIndexOfChild(Object parent, Object child) { return Arrays.asList(((Container)parent).getComponents()).indexOf(child); }
	
	
	HashSet<Component> listensToProperty = new HashSet<Component>(); 
	@Override public Object getChild(Object parent, int index) { 
		Container comp = (Container)((Container)parent).getComponent(index);
		
		if (listensToProperty.add(comp)) { 
			comp.addPropertyChangeListener(this);
			comp.addContainerListener(this);
		}
		
		return comp;
	}
	@Override public int getChildCount(Object parent) { return ((Container)parent).getComponentCount(); }
	
	@Override public boolean isLeaf(Object node) { return getChildCount(node)==0;  }

	@Override public void valueForPathChanged(TreePath path, Object newValue) {}

	@Override public void componentAdded(ContainerEvent ce) {
		listensToProperty.add(ce.getChild());
		((Container)ce.getChild()).addPropertyChangeListener(this);
		((Container)ce.getChild()).addContainerListener(this);
		
		Object[] path = pathForSource(root, ce.getContainer());
		for (TreeModelListener tml: tmls) 
			tml.treeStructureChanged(new TreeModelEvent(ce.getContainer(), path));
	}
	
	@Override public void componentRemoved(ContainerEvent ce) {
		listensToProperty.remove(ce.getChild());
		ce.getChild().removePropertyChangeListener(this);
		((Container)ce.getChild()).removeContainerListener(this);
		
		Object[] path = pathForSource(root, ce.getContainer());
		for (TreeModelListener tml: tmls) 
			tml.treeStructureChanged(new TreeModelEvent(ce.getContainer(), path));
	}

	
	@Override public void propertyChange(PropertyChangeEvent pce) {
		Object[] path = pathForSource(root, (JComponent)pce.getSource());
		for (TreeModelListener tml: tmls) 
			tml.treeStructureChanged(new TreeModelEvent(pce.getSource(), path));
	}
	
	private Object[] pathForSource(JComponent root, Container container) {
		if (container==root) 
			return new Object[] { container };
		else 
			return append(pathForSource(root, (JComponent)container.getParent()), container);
	}
	
	private Object[] append(Object[] a, Object o) {
		(a = Arrays.copyOf(a, a.length+1))[a.length-1]=o;
		return a;
	}



//	@Override public void addTreeModelListener(TreeModelListener l) { }
//	@Override public void removeTreeModelListener(TreeModelListener l) {  }

	@Override public void addTreeModelListener(TreeModelListener l) { tmls.add(l); }
	@Override public void removeTreeModelListener(TreeModelListener l) { tmls.remove(l); }

	public CopyOnWriteArrayList<TreeModelListener> tmls = new CopyOnWriteArrayList<TreeModelListener>(); 


	private int cellSpacing = 0;
	public int getCellSpacing() { return cellSpacing; }
	public void setCellSpacing(int r) { cellSpacing=r; renderer.setBorder(new EmptyBorder(r,r,r,r)); }

	
	JComponent renderTarget = null;
	DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {
		private static final long serialVersionUID = 1L;
		public Dimension getPreferredSize() { 
			Dimension dim = renderTarget.getPreferredSize();
			getInsets(in);
			return new Dimension(Math.max(128,dim.width+in.left+in.right), dim.height+in.top+in.bottom);
		};
		
		Method paintComponent, paintBorder;
		
		@Override public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			Component c = super.getTreeCellRendererComponent(tree, "", selected, expanded, leaf, row, hasFocus);
			setIcon(null);
			return c;
		}

		Insets in = new Insets(0, 0, 0, 0);
		protected void paintComponent(java.awt.Graphics g) {
			super.paintComponent(g);
			try {
				if (paintComponent==null) (paintComponent = JComponent.class.getDeclaredMethod("paintComponent", Graphics.class)).setAccessible(true);
				if (paintBorder==null) (paintBorder = JComponent.class.getDeclaredMethod("paintBorder", Graphics.class)).setAccessible(true);
				
				getInsets(in);
				renderTarget.setBounds(0, 0, getWidth()-in.left-in.right, getHeight()-in.top-in.bottom);
				g.translate(in.left, in.top);
				renderTarget.setForeground(g.getColor());
				paintComponent.invoke(renderTarget, g);
				paintBorder.invoke(renderTarget, g);
				g.translate(-in.left, -in.top);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		};
	};

	
	@Override public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		renderTarget = (JComponent) value;
		return renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	}
	

	
}
