/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.views;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;

import com.kdmanalytics.toif.ui.common.FindingEntry;
import com.kdmanalytics.toif.ui.common.FindingGroup;
import com.kdmanalytics.toif.ui.common.IFindingEntry;

/**
 * Tracks current selection of the Finding View.
 * 
 * @author Ken Duck
 *        
 */
public class FindingSelectionChangedListener extends FindingSelection implements ISelectionChangedListener {
  
  /**
   * Table that owns the selection
   */
  private TreeViewer viewer = null;
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.
   * SelectionChangedEvent)
   */
  @Override
  public void selectionChanged(SelectionChangedEvent event) {
    viewer = (TreeViewer) event.getSource();
    
    IStructuredSelection selection = (IStructuredSelection) event.getSelection();
    clear();
    for (Iterator<?> it = selection.iterator(); it.hasNext();) {
      Object o = it.next();
      if (o instanceof IFindingEntry) {
        add((IFindingEntry) o);
      }
    }
  }
  
  /**
   * Cite the selected findings. 3 states:
   * 
   * o true: Finding is a weakness o false: Finding is not a weakness o null: Remove citing
   * 
   * @param b
   */
  public void cite(Boolean b) {
    super.cite(b);
    List<Object> updateThese = new LinkedList<Object>();
    Object[] selected = getSelectionArray();
    for (Object object : selected) {
      if (object instanceof FindingEntry) {
        // If this finding is part of a group, then change the entire group
        FindingGroup parent = ((FindingEntry)object).getParent();
        if(parent != null) {
          object = parent;
        }
      }
      
      // Update all children
      if (object instanceof FindingGroup) {
        updateThese.addAll(((FindingGroup)object).getFindingEntries());
      }
      
      // Update myself
      updateThese.add(object);
    }
    viewer.update(updateThese.toArray(), null);
  }
  
  /**
   * Set the trust level for this finding type. From the documentation:
   * 
   * This option ... sets the level of trust for the selected finding. This level is propagated
   * throughout the data set, marking any finding with the same CWE from the same tool with the
   * specified value. Trust is an indication of how much faith the analyst has in the tools ability
   * to accurately detect the defect.
   * 
   * @param val
   * @return Set of finding type IDs that were affected by the trust change
   */
  public Set<String> setTrust(int val) {
//    Set<String> types = super.setTrust(val);
//    
//    // Find everybody who needs updating
//    FindingContentProvider contents = (FindingContentProvider) viewer.getContentProvider();
//    FindingEntry[] findings = contents.getEntries();
//    List<FindingEntry> updatedFindings = new LinkedList<FindingEntry>();
//    if (findings != null) {
//      for (FindingEntry finding : findings) {
//        String tid = finding.getTypeId();
//        if (types.contains(tid)) {
//          updatedFindings.add(finding);
//        }
//      }
//    }
//    viewer.update(updatedFindings.toArray(), null);
//    return types;
    throw new UnsupportedOperationException();
  }
  
}
