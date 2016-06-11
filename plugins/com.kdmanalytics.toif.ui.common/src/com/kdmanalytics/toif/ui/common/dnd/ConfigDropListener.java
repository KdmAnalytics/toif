
package com.kdmanalytics.toif.ui.common.dnd;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.kdmanalytics.toif.ui.common.AdaptorConfiguration;

/**
 * Based on example here: o http://www.vogella.com/tutorials/EclipseDragAndDrop/article.html
 * 
 * @author Ken Duck
 *
 */
public class ConfigDropListener extends ViewerDropAdapter {
  /**
   * Required for doing edits as requested
   */
  private AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
  
  private final Viewer viewer;
  private int location;

  private int insertionIndex;
  
  public ConfigDropListener(Viewer viewer) {
    super(viewer);
    this.viewer = viewer;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.ViewerDropAdapter#drop(org.eclipse.swt.dnd.DropTargetEvent)
   */
  @Override
  public void drop(DropTargetEvent event) {
    location = this.determineLocation(event);
    Object o = determineTarget(event);
    DropTarget target = (DropTarget)event.getSource();
    Control control = target.getControl();
    
    List<?> row = (List<?>)o;
    insertionIndex = config.getSize();
    if(row != null) {
      String cwe = (String) row.get(config.getCweColumnIndex());
      insertionIndex = config.getIndex(cwe);
    }
    
    
    
    switch (location) {
      case LOCATION_BEFORE:
        // translatedLocation = "Dropped before the target ";
        break;
      case LOCATION_AFTER:
        // translatedLocation = "Dropped after the target ";
        insertionIndex++;
        break;
      case LOCATION_ON:
        // translatedLocation = "Dropped on the target ";
        break;
      case LOCATION_NONE:
        // Was this dropped on 'none' above the table? If so then
        // adjust the location and insertion index
        Point cursorLocation = Display.getCurrent().getCursorLocation();
        Point relativeCursorLocation = control.toControl(cursorLocation);
        if (relativeCursorLocation.y < 50) {
          location = LOCATION_BEFORE;
          insertionIndex = 0;
        }
        break;
    }

    super.drop(event);
  }
  
  // This method performs the actual drop
  // We simply add the String we receive to the model and trigger a refresh of the
  // viewer by calling its setInput method.
  @Override
  public boolean performDrop(Object data) {
    IStructuredSelection sel = (IStructuredSelection)data;
    Object[] objs = sel.toArray();
    for(int i = objs.length - 1; i >= 0; i--) {
      List<?> row = (List<?>)objs[i];
      int idx = config.remove(row);
      // Adjust the insertion index
      if (idx <= insertionIndex && insertionIndex != 0) {
        insertionIndex--;
      }
      config.add(insertionIndex, row);
    }
    viewer.refresh();
    return false;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.ViewerDropAdapter#validateDrop(java.lang.Object, int,
   * org.eclipse.swt.dnd.TransferData)
   */
  @Override
  public boolean validateDrop(Object target, int operation, TransferData transferType) {
    return true;
    
  }
  
}
