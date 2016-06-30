/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.views;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.kdmanalytics.toif.ui.common.Activator;

/** Save the order of the columns in the table for restarts.
 * 
 * @author Ken Duck
 *
 */
public class SaveColumnOrderAction extends Action {

  /**
   * Name for preference
   */
  private static final String COLUMN_ORDER = Activator.PLUGIN_ID + ".ColumnOrder";
  private TreeViewer viewer;
  
  private Preferences store = Platform.getPreferencesService().getRootNode().node(InstanceScope.SCOPE).node(COLUMN_ORDER);
  
  public SaveColumnOrderAction(FindingView view, TreeViewer viewer) {
    this.viewer = viewer;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.action.Action#run()
   */
  @Override
  public void run() {
    StringBuilder sb = new StringBuilder();
    int[] order = viewer.getTree().getColumnOrder();
    boolean first = true;
    for (int i : order) {
      if (!first) {
        sb.append(',');
      } else {
        first = false;
      }
      sb.append(i);
    }
    
    
    store.put(COLUMN_ORDER, sb.toString());
    String test = store.get(COLUMN_ORDER, "");
    if (!sb.toString().equals(test)) {
      System.err.println("ZOUNDS");
    }
    try {
      store.flush();
    } catch (BackingStoreException e) {
      e.printStackTrace();
    }
  }

  /**
   * Restore the column order in the tree to that which was last saved.
   */
  public void restore() {
    String savedOrder = store.get(COLUMN_ORDER, "");
    if (savedOrder != null) {
      String[] tokens = savedOrder.split(",");
      // Remember which column we have not put in the tree yet
      Set<Integer> unused = new TreeSet<Integer>();
      Tree tree = viewer.getTree();
      int[] order = tree.getColumnOrder();
      for (int i : order) {
        unused.add(i);
      }
      int[] neworder = new int[order.length];
      int j = 0;
      // Add saved columns
      for (String token : tokens) {
        try {
          int i = Integer.parseInt(token);
          if (i < neworder.length) {
            if (j < neworder.length) {
              neworder[j++] = i;
              unused.remove(i);
            }
          }
        } catch (NumberFormatException e) {}
      }
      
      // Add remaining columns
      for(Integer i: unused) {
        neworder[j++] = i;
      }
      tree.setColumnOrder(neworder);
    }
  }
}
