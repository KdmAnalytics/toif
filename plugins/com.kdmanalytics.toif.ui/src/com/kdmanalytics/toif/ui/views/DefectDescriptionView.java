package com.kdmanalytics.toif.ui.views;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.kdmanalytics.toif.ui.common.FindingData;
import com.kdmanalytics.toif.ui.common.FindingEntry;


/** Simple view that provides descriptive information about selected findings.
 * 
 * @author Ken Duck
 *
 */
public class DefectDescriptionView extends ViewPart
{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.kdmanalytics.toif.ui.views.DefectDescriptionView";

	private TreeViewer viewer;
	
	/**
	 * Lookup table for SFP information
	 */
	private Map<String,String[]> sfpLookup = new HashMap<String,String[]>();
	/**
	 * Lookup table for CWE information
	 */
	private Map<String,String[]> cweLookup = new HashMap<String,String[]>();

	ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection sel)
		{
			handleSelection(sel);
		}
	};

	/**
	 * 
	 * @author Ken Duck
	 *
	 */
	class DefectNode
	{
		static final int CWE_NODE = 1;
		static final int SFP_NODE = 2;
		static final int CLUSTER_NODE = 3;
		static final int ROOT_NODE = 4;
		static final int DESCRIPTION_NODE = 5;

		private int type;
		private String name;
		private String description;
		private DefectNode parent;
		private List<DefectNode> children = new ArrayList<DefectNode>();

		public DefectNode(int type, String name)
		{
			this.type = type;
			this.name = name;
		}
		public DefectNode(int type, String name, String description)
		{
			this.type = type;
			this.name = name;
			this.description = description;
		}
		public int getType()
		{
			return type;
		}
		public String getName()
		{
			return name;
		}
		public String getDescription()
		{
			return description;
		}
		public void setParent(DefectNode parent)
		{
			this.parent = parent;
		}
		public DefectNode getParent()
		{
			return parent;
		}
		public void addChild(DefectNode child)
		{
			children.add(child);
			child.setParent(this);
		}
		public void removeChild(DefectNode child)
		{
			children.remove(child);
			child.setParent(null);
		}
		public DefectNode [] getChildren()
		{
			return (DefectNode [])children.toArray(new DefectNode[children.size()]);
		}
		public boolean hasChildren()
		{
			return children.size()>0;
		}
		public String toString()
		{
			return getName();
		}
	}

	/** Very simple content provider
	 * 
	 * @author Ken Duck
	 *
	 */
	class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider
	{
		private DefectNode root;

		public void inputChanged(Viewer v, Object oldInput, Object newInput)
		{
			if(newInput instanceof FindingData)
			{
				FindingData data = (FindingData)newInput;
				root = new DefectNode(DefectNode.ROOT_NODE, "ROOT");

				String sfp = data.getSfp();
				DefectNode clusterNode = null;
				if(sfpLookup.containsKey(sfp))
				{
					// If we have a cluster then add the cluster node
					String[] sfpData = sfpLookup.get(sfp);
					if(sfpData.length > 2)
					{
						clusterNode = new DefectNode(DefectNode.CLUSTER_NODE, "Cluster", sfpData[2]);
						root.addChild(clusterNode);
						clusterNode.setParent(root);
					}
				}
				
				// If we do not have a cluster node then skip it.
				if(clusterNode == null) clusterNode = root;

				DefectNode sfpNode = null;
				if(sfpLookup.containsKey(sfp))
				{
					String[] sfpData = sfpLookup.get(sfp);
					sfpNode = new DefectNode(DefectNode.SFP_NODE, sfp, sfpData[1]);
				}
				else
				{
					sfpNode = new DefectNode(DefectNode.SFP_NODE, sfp, "Unmapped");
				}
				clusterNode.addChild(sfpNode);
				sfpNode.setParent(clusterNode);

				String cwe = data.getCwe();
				DefectNode cweNode = null;
				if(cweLookup.containsKey(cwe))
				{
					String[] cweData = cweLookup.get(cwe);
					cweNode = new DefectNode(DefectNode.CWE_NODE, cwe, cweData[1]);
					sfpNode.addChild(cweNode);
					cweNode.setParent(sfpNode);

					// If there is a CWE description, then add it in next
					if(cweData.length > 2)
					{
						DefectNode descNode = new DefectNode(DefectNode.DESCRIPTION_NODE, "Description", cweData[2]);
						sfpNode.addChild(descNode);
						descNode.setParent(sfpNode);
					}
				}
				else
				{
					cweNode = new DefectNode(DefectNode.CWE_NODE, cwe, "Unmapped");
					sfpNode.addChild(cweNode);
					cweNode.setParent(sfpNode);
				}
			}
		}
		public void dispose()
		{
		}
		public Object[] getElements(Object parent)
		{
			if (parent instanceof FindingEntry)
			{
				return getChildren(root);
			}
			return getChildren(parent);
		}
		public Object getParent(Object child)
		{
			if (child instanceof DefectNode)
			{
				return ((DefectNode)child).getParent();
			}
			return null;
		}
		public Object [] getChildren(Object parent)
		{
			if (parent instanceof DefectNode)
			{
				return ((DefectNode)parent).getChildren();
			}
			return new Object[0];
		}
		public boolean hasChildren(Object parent)
		{
			if (parent instanceof DefectNode)
			{
				return ((DefectNode)parent).hasChildren();
			}
			return false;
		}
	}

	/** Very simple label provider
	 * 
	 * @author Ken Duck
	 *
	 */
	class ViewLabelProvider  implements ITableLabelProvider
	{

		public String getText(Object obj)
		{
			return obj.toString();
		}
		@Override
		public void addListener(ILabelProviderListener listener) {
		}
		@Override
		public void dispose() {
		}
		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}
		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
//			switch(columnIndex)
//			{
//			case 0:
//				String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
//				if (element instanceof DefectNode)
//				{
//					switch(((DefectNode)element).getType())
//					{
//					case DefectNode.CLUSTER_NODE:
//						imageKey = ISharedImages.IMG_OBJ_FOLDER;
//						break;
//					case DefectNode.SFP_NODE:
//						imageKey = ISharedImages.IMG_OBJ_FOLDER;
//						break;
//					case DefectNode.CWE_NODE:
//						break;
//					}
//				}
//				return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
//			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof DefectNode)
			{
				DefectNode node = (DefectNode)element;
				switch(columnIndex)
				{
				case 0:
					return node.getName();
				case 1:
					return node.getDescription();
				}
			}
			return null;
		}
	}
	class NameSorter extends ViewerSorter
	{
	}

	/**
	 * The constructor.
	 */
	public DefectDescriptionView()
	{
		try
		{
			loadSfpLookups();
			loadCweLookups();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/** SFP table lookup
	 * 
	 * @throws IOException
	 */
	private void loadSfpLookups() throws IOException
	{
		InputStreamReader in = null;
		CSVParser parser = null;
		try
		{
			InputStream is = getClass().getResourceAsStream("/resources/sfp.csv");
			in = new InputStreamReader(is);
			CSVFormat format = CSVFormat.EXCEL
					.withDelimiter(',')
					.withIgnoreEmptyLines();

			parser = new CSVParser(in, format);
			
			boolean header = true;
			for (CSVRecord record : parser)
			{
				if(header)
				{
					// Ignore header
					header = false;
					continue;
				}
				String sfpid = record.get(0);
				String cluster = record.get(1);
				String name = record.get(2);
				
				sfpLookup.put("SFP-" + sfpid, new String[] {sfpid, name, cluster});
			}
		}
		finally
		{
			if(in != null)
			{
				in.close();
			}
			if(parser != null)
			{
				parser.close();
			}
		}
	}

	/** SFP table lookup
	 * 
	 * @throws IOException
	 */
	private void loadCweLookups() throws IOException
	{
		InputStreamReader in = null;
		CSVParser parser = null;
		try
		{
			InputStream is = getClass().getResourceAsStream("/resources/cwe.csv");
			in = new InputStreamReader(is);
			CSVFormat format = CSVFormat.EXCEL
					.withDelimiter(',')
					.withIgnoreEmptyLines();

			parser = new CSVParser(in, format);
			
			boolean header = true;
			for (CSVRecord record : parser)
			{
				if(header)
				{
					// Ignore header
					header = false;
					continue;
				}
				String cweid = record.get(0);
				String name = record.get(1);
//				if(size > 2)
//				{
//					String description = record.get(2);
//					cweLookup.put(cweid, new String[] {cweid, name, description});
//				}
//				else
				{
					cweLookup.put("CWE-" + cweid, new String[] {cweid, name});
				}
			}
		}
		finally
		{
			if(in != null)
			{
				in.close();
			}
			if(parser != null)
			{
				parser.close();
			}
		}
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent)
	{
		viewer = new TreeViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

		Tree tree = viewer.getTree();
		tree.setHeaderVisible(true);

		TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
		tree.setLinesVisible(true);
		column1.setAlignment(SWT.LEFT);
		column1.setText("ID");
		column1.setWidth(150);
		TreeColumn column2 = new TreeColumn(tree, SWT.RIGHT);
		column2.setAlignment(SWT.LEFT);
		column2.setText("Description");
		column2.setWidth(500);

		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());

		// Set the selection listener
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		page.addSelectionListener(selectionListener);

		// Get current selection
		ISelectionService service = window.getSelectionService();
		ISelection selection = service.getSelection();
		handleSelection(selection);
	}

	/** Do something with the provided selection
	 * 
	 * @param sel
	 */
	protected void handleSelection(ISelection sel)
	{
		if (sel instanceof IStructuredSelection)
		{
			for (final Object object : ((IStructuredSelection) sel).toArray())
			{
				if (object instanceof FindingData)
				{
					if(viewer != null)
					{
						viewer.setInput(object);
						viewer.refresh();
						viewer.expandAll();
					}
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose()
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		page.removeSelectionListener(selectionListener);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}
}