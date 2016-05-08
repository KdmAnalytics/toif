
package com.kdmanalytics.toif.ui.views;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
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

import com.kdmanalytics.toif.ui.common.FindingData;

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
   * Lookup table for SFP information
   */
  private Map<String, String[]> sfpLookup = new HashMap<String, String[]>();
  
  /**
   * Lookup table for CWE information
   */
  private Map<String, String[]> cweLookup = new HashMap<String, String[]>();
  
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
    
    try {
      loadSfpLookups();
      loadCweLookups();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * SFP table lookup
   * 
   * @throws IOException
   */
  private void loadSfpLookups() throws IOException {
    InputStreamReader in = null;
    CSVParser parser = null;
    try {
      InputStream is = getClass().getResourceAsStream("/resources/sfp.csv");
      in = new InputStreamReader(is);
      CSVFormat format = CSVFormat.EXCEL.withDelimiter(',').withIgnoreEmptyLines();
      
      parser = new CSVParser(in, format);
      
      boolean header = true;
      for (CSVRecord record : parser) {
        if (header) {
          // Ignore header
          header = false;
          continue;
        }
        String sfpid = record.get(0);
        String cluster = record.get(1);
        String name = record.get(2);
        
        sfpLookup.put("SFP-" + sfpid, new String[] {
                                                     sfpid, name, cluster
        });
      }
    } finally {
      if (in != null) {
        in.close();
      }
      if (parser != null) {
        parser.close();
      }
    }
  }
  
  /**
   * SFP table lookup
   * 
   * @throws IOException
   */
  private void loadCweLookups() throws IOException {
    InputStreamReader in = null;
    CSVParser parser = null;
    try {
      InputStream is = getClass().getResourceAsStream("/resources/cwe.csv");
      in = new InputStreamReader(is);
      CSVFormat format = CSVFormat.EXCEL.withDelimiter(',').withIgnoreEmptyLines();
      
      parser = new CSVParser(in, format);
      
      boolean header = true;
      for (CSVRecord record : parser) {
        if (header) {
          // Ignore header
          header = false;
          continue;
        }
        String cweid = record.get(0);
        String name = record.get(1);
        // if(size > 2)
        // {
        // String description = record.get(2);
        // cweLookup.put(cweid, new String[] {cweid, name, description});
        // }
        // else
        {
          cweLookup.put("CWE-" + cweid, new String[] {
                                                       cweid, name
          });
        }
      }
    } finally {
      if (in != null) {
        in.close();
      }
      if (parser != null) {
        parser.close();
      }
    }
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
    
    viewer.setContentProvider(new DefectDescViewContentProvider(sfpLookup, cweLookup));
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
  
  // The following code was an attempt to wrap the text in the table cells. The problem
  // is that it wants every row to be the same height, which is not good.
  //
  //
  // /**
  // http://git.eclipse.org/c/platform/eclipse.platform.swt.git/tree/examples/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet231.java
  // *
  // * @param tree
  // */
  // private void addWrapSupport(Tree tree) {
  // /*
  // * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly. Therefore, it is critical
  // * for performance that these methods be as efficient as possible.
  // */
  // tree.addListener(SWT.MeasureItem, new Listener() {
  //
  // @Override
  // public void handleEvent(Event event) {
  // TreeItem item = (TreeItem) event.item;
  // String text = item.getText(event.index);
  // Point size = event.gc.textExtent(text);
  // event.width = size.x + 2 * TEXT_MARGIN;
  // event.height = Math.max(event.height, size.y + TEXT_MARGIN);
  // }
  // });
  // tree.addListener(SWT.EraseItem, new Listener() {
  //
  // @Override
  // public void handleEvent(Event event) {
  // event.detail &= ~SWT.FOREGROUND;
  // }
  // });
  // tree.addListener(SWT.PaintItem, new Listener() {
  //
  // @Override
  // public void handleEvent(Event event) {
  // TreeItem item = (TreeItem) event.item;
  // String text = item.getText(event.index);
  // /* center column 1 vertically */
  // int yOffset = 0;
  // if (event.index == 1) {
  // Point size = event.gc.textExtent(text);
  // yOffset = Math.max(0, (event.height - size.y) / 2);
  // }
  // event.gc.drawText(text, event.x + TEXT_MARGIN, event.y + yOffset, true);
  // }
  // });
  // }
  
  /**
   * Do something with the provided selection
   * 
   * @param sel
   */
  protected void handleSelection(ISelection sel) {
    if (sel instanceof IStructuredSelection) {
      for (final Object object : ((IStructuredSelection) sel).toArray()) {
        if (object instanceof FindingData) {
          if (viewer != null) {
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
