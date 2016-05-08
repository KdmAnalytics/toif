/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.views;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;

import com.kdmanalytics.toif.ui.common.IFindingEntry;

/**
 * 
 * @author Ken Duck
 * 
 */
class DefectDescStyledLabelProvider extends StyledCellLabelProvider {
  
  
  /**
   * The hyperlink blue, from https://en.wikipedia.org/wiki/Help:Link_color
   */
  private static final Color FOREGROUND_BLUE = new Color(PlatformUI.getWorkbench().getDisplay(), new RGB(6, 69, 173));
  
  /**
   * 
   */
  private TreeViewer viewer;
  
  /**
   * 
   */
  public DefectDescStyledLabelProvider(TreeViewer viewer) {
    this.viewer = viewer;
  }
  
  /**
   * Get the entry text for the specified column
   * 
   * @param entry
   * @param colIndex
   * @return
   */
  private String getColumnText(DefectNode entry, int colIndex) {
    switch (colIndex) {
      case 0: {
        String name = entry.getName();
        name = name.replaceAll("([^-])-([^-])", "$1$2");
        return name;
      }
      case 1: {
        return entry.getDescription();
      }
    }
    return null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang. Object)
   */
  @Override
  public String getToolTipText(Object element) {
    if (element instanceof IFindingEntry) {
      IFindingEntry entry = ((IFindingEntry) element);
      return entry.getPath();
    }
    return null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
   */
  public void update(final ViewerCell cell) {
    final DefectNode entry = (DefectNode) cell.getElement();
    int index = cell.getColumnIndex();
    final StyledString styledString = new StyledString(getColumnText(entry, index));
    
    String desc = entry.getDescription();
    if (desc.startsWith("http://") || desc.startsWith("https://")) {
      cell.setForeground(FOREGROUND_BLUE);
    }
    
    String text = styledString.toString();
    
    // This removed code is used to split the string into a number of separate
    // lines suitable for wrapping.
    //
    //
    // // How many characters will we allow per line?
    // Tree tree = (Tree)cell.getControl();
    //
    // // Use the client area minus the first column to see how much room we have
    // // for text.
    // Rectangle area = viewer.getTree().getClientArea();
    // TreeColumn column = tree.getColumn(0);
    // int width = area.width - column.getWidth();
    // if (width < 50) width = 50;
    // width -= 10;
    // GC gc = new GC(tree);
    // FontMetrics fm = gc.getFontMetrics();
    // int charWidth = fm.getAverageCharWidth();
    // int charCount = width / charWidth;
    // if (charCount < 10) charCount = 10;
    //
    // List<String> lines = new LinkedList<String>();
    //
    // while (text.length() > 0) {
    // // Break at space
    // int lastSpace = text.lastIndexOf(' ', charCount);
    // if (lastSpace > 10) {
    // lines.add(text.substring(0, lastSpace));
    // text = text.substring(lastSpace + 1);
    // continue;
    // }
    //
    // // Break at next space
    // int nextSpace = text.indexOf(' ', charCount);
    // if (nextSpace > 10) {
    // lines.add(text.substring(0, nextSpace));
    // text = text.substring(nextSpace + 1);
    // continue;
    // }
    //
    // // Just use whole string
    // lines.add(text);
    // text = "";
    // }
    //
    // // Add in \n characters
    // StringBuilder sb = new StringBuilder();
    // for (int i = 0; i < lines.size(); i++) {
    // if (i > 0) sb.append("\n");
    // sb.append(lines.get(i));
    // }
    //
    // cell.setText(sb.toString());
    cell.setText(text);
    cell.setStyleRanges(styledString.getStyleRanges());
  }
  
}
