
package com.kdmanalytics.toif.ui.views;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.part.ViewPart;

import com.kdmanalytics.toif.ui.common.IFindingEntry;
import com.kdmanalytics.toif.ui.internal.DescriptionMap;

/**
 * Simple view that provides descriptive information about selected findings.
 * 
 * @author Ken Duck
 * 
 */
public class DefectDescriptionView extends ViewPart implements MouseMoveListener, SelectionListener {
  
  
  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "com.kdmanalytics.toif.ui.views.DefectDescriptionView";
  
  private TreeViewer viewer;
  
  /**
   * Margin when drawing text
   */
  final int TEXT_MARGIN = 3;
  
  /**
   * Cursor to change to when over a hyperlink
   */
  private Cursor cursor;
  
  /**
   * Current cursor
   */
  private Cursor currCurr;
  
  /**
   * Remember when we last clicked so we don't register multiple selections
   */
  private long lastSelect;
  
  private static long WAIT_MS = 1000;
  
  ISelectionListener selectionListener = new ISelectionListener() {
    
    
    public void selectionChanged(IWorkbenchPart part, ISelection sel) {
      handleSelection(sel);
    }
  };
  
  class NameSorter extends ViewerSorter {}
  
  /**
   * The constructor.
   */
  public DefectDescriptionView() {
    Display display = Display.getDefault();
    cursor = new Cursor(display, SWT.CURSOR_HAND);
  }
  
  
  /**
   * This is a callback that will allow us to create the viewer and initialize it.
   */
  public void createPartControl(Composite parent) {
    viewer = new TreeViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.WRAP);
    
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
    
    viewer.setContentProvider(new DefectDescViewContentProvider(DescriptionMap.INSTANCE.getSfpMap(), DescriptionMap.INSTANCE.getCweMap()));
    viewer.setLabelProvider(new DefectDescStyledLabelProvider(viewer));
    
    // Set the selection listener
    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    IWorkbenchPage page = window.getActivePage();
    page.addSelectionListener(selectionListener);
    
    // Get current selection
    ISelectionService service = window.getSelectionService();
    ISelection selection = service.getSelection();
    handleSelection(selection);
    
    tree.addMouseMoveListener(this);
    tree.addSelectionListener(this);
    
    // addWrapSupport(tree);
    
  }
  
  /**
   * Do something with the provided selection
   * 
   * @param sel
   */
  protected void handleSelection(ISelection sel) {
    if (sel instanceof IStructuredSelection) {
      for (final Object object : ((IStructuredSelection) sel).toArray()) {
        if (object instanceof IFindingEntry) {
          if (viewer != null) {
            viewer.setInput(object);
            viewer.refresh();
            viewer.expandAll();
            TreeColumn[] columns = viewer.getTree().getColumns();
            columns[1].pack();
          }
        }
      }
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.part.WorkbenchPart#dispose()
   */
  @Override
  public void dispose() {
    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    IWorkbenchPage page = window.getActivePage();
    page.removeSelectionListener(selectionListener);
  }
  
  /**
   * Passing the focus request to the viewer's control.
   */
  public void setFocus() {
    viewer.getControl().setFocus();
  }
  
  /*
   * 
   */
  @Override
  public void mouseMove(MouseEvent event) {
    Point point = new Point(event.x, event.y);
    Tree tree = viewer.getTree();
    
    TreeItem item = tree.getItem(point);
    if (item != null) {
      DefectNode node = (DefectNode) item.getData();
      if ("More info".equals(node.getName())) {
        if (currCurr == null) {
          tree.setCursor(cursor);
          currCurr = cursor;
        }
        return;
      }
    }
    
    tree.setCursor(null);
    currCurr = null;
  }
  
  /*
   * 
   */
  @Override
  public void widgetSelected(SelectionEvent event) {
    long now = System.currentTimeMillis();
    
    // Don't respond too quickly to a double event
    if (now - lastSelect > WAIT_MS) {
      lastSelect = now;
      IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
      DefectNode node = (DefectNode) selection.getFirstElement();
      if (node != null) {
        if ("More info".equals(node.getName())) {
          String desc = node.getDescription();
          desc = desc.trim();
          if (desc != null && !desc.isEmpty()) {
            
            int index = desc.lastIndexOf('/');
            // Get the CWE from the link (http://.../###.html)
            String cwe = "CWE" + desc.substring(index + 1, desc.length() - 5);
            
            IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
            IWebBrowser browser;
            
            try {
              browser = browserSupport.createBrowser(IWorkbenchBrowserSupport.LOCATION_BAR, null, cwe, cwe);
              URL url = new URL(desc);
              browser.openURL(url);
            } catch (PartInitException e) {
              e.printStackTrace();
            } catch (MalformedURLException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }
  
  /*
   * 
   */
  @Override
  public void widgetDefaultSelected(SelectionEvent e) {
    widgetSelected(e);
  }
}
