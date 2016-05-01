package com.kdmanalytics.toif.ui.common.dnd;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

/** Based on example here:
 *     o http://www.vogella.com/tutorials/EclipseDragAndDrop/article.html
 * 
 * @author Ken Duck
 *
 */
public class ConfigDragListener implements DragSourceListener {


  private TableViewer viewer;

  public ConfigDragListener(TableViewer viewer) {
    this.viewer = viewer;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.swt.dnd.DragSourceListener#dragFinished(org.eclipse.swt.dnd.DragSourceEvent)
   */
  @Override
  public void dragFinished(DragSourceEvent event) {
    System.out.println("Finshed Drag");
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
   */
  @Override
  public void dragSetData(DragSourceEvent event) {
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.swt.dnd.DragSourceListener#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
   */
  @Override
  public void dragStart(DragSourceEvent event) {
    IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
    LocalSelectionTransfer.getTransfer().setSelection(selection);
  }
}
