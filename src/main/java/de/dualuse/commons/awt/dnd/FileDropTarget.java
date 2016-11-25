package de.dualuse.commons.awt.dnd;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;


public class FileDropTarget extends AbstractDropTarget<File[]> implements DropListener<File[]>{
	private static FileFilter DEFAULT_FILTER = new FileFilter() { public boolean accept(File pathname) { return true; } };
	private static final long serialVersionUID = 1L;

	private final FileFilter filter;

	public FileDropTarget() { this(DEFAULT_FILTER); }
	public FileDropTarget(FileFilter f) { this.filter = f; }
	public FileDropTarget(final String nameFilterRegExp) { this(new FileFilter() { public boolean accept(File pathname) { return pathname.getName().matches(nameFilterRegExp); } }); }

	public FileDropTarget(DropListener<File[]> listener) { this(DEFAULT_FILTER); setListener(listener); }
	public FileDropTarget(FileFilter f, DropListener<File[]> listener) { this.filter = f; setListener(listener);}
	public FileDropTarget(final String nameFilterRegExp, DropListener<File[]> listener) { 
		this(new FileFilter() { public boolean accept(File pathname) { return pathname.getName().matches(nameFilterRegExp); } });
		setListener(this);
	}

	public FileDropTarget(Component c) { super(c); filter = DEFAULT_FILTER; }
	public FileDropTarget(Component c, FileFilter f) { super(c); this.filter = f; }
	public FileDropTarget(Component c, final String nameFilterRegExp) { 
		super(c);
		this.filter = new ExpressionFilter(nameFilterRegExp);
	}

	public FileDropTarget(Component c, DropListener<File[]> listener) { super(c); this.filter= DEFAULT_FILTER; setListener(listener); }
	public FileDropTarget(Component c, FileFilter f, DropListener<File[]> listener) { super(c); this.filter = f; setListener(listener);}
	public FileDropTarget(Component c, final String nameFilterRegExp, DropListener<File[]> listener) {
		super(c);
		this.filter = new ExpressionFilter(nameFilterRegExp);
		setListener(this);
	}

	
	@Override
	public File[] unpack(DropTargetDropEvent dtde) throws Exception {
		List<?> files = ((List<?>)dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
		ArrayList<File> acceptedFiles = new ArrayList<File>(files.size());
		
		for (Object o: files) {
			File file = new File(o.toString());
			if (filter.accept(file))
				acceptedFiles.add(file);
		}
		
		return acceptedFiles.toArray(new File[acceptedFiles.size()]);
	}
	
	private static class ExpressionFilter implements FileFilter {
		final String nameFilterRegExp;
		public ExpressionFilter(String expression) {
			this.nameFilterRegExp= expression;
		}
		
		public boolean accept(File pathname) { 
			return pathname.getName().matches(nameFilterRegExp); 
		} 
	}
}
