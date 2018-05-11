/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.views;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;

import com.kdmanalytics.toif.ui.Activator;
import com.kdmanalytics.toif.ui.common.AdaptorConfiguration;
import com.kdmanalytics.toif.ui.common.IFindingEntry;
import com.kdmanalytics.toif.ui.internal.DescriptionMap;

/**
 * 
 * @author Ken Duck
 * 
 */
class FindingStyledLabelProvider extends StyledCellLabelProvider {

  /** The green. */
  private static final Color GREEN = new Color(PlatformUI.getWorkbench().getDisplay(), new RGB(220, 255, 220));

  private static final Color WHITE = new Color(PlatformUI.getWorkbench().getDisplay(), new RGB(255, 255, 255));

  /** The red. */
  private static final Color RED = new Color(PlatformUI.getWorkbench().getDisplay(), new RGB(255, 220, 220));

  /** The foreground green. */
  private static final Color FOREGROUND_GREEN = new Color(PlatformUI.getWorkbench().getDisplay(), new RGB(0, 80, 0));

  private static final Color FOREGROUND_BLACK = new Color(PlatformUI.getWorkbench().getDisplay(), new RGB(80, 80, 80));

  /** The foreground red. */
  private static final Color FOREGROUND_RED = new Color(PlatformUI.getWorkbench().getDisplay(), new RGB(100, 0, 0));

  /** The Constant TICK_KEY. */
  private static final String TICK_KEY = "tick";

  /** The Constant CROSS_KEY. */
  private static final String CROSS_KEY = "cross";

  /** The Constant PAGE_WHITE_STACK_KEY. */
  private static final String PAGE_WHITE_STACK_KEY = "page_stack";

  /** The Constant PAGE_KEY. */
  private static final String PAGE_KEY = "page";

  /** The Constant WRENCH_KEY. */
  private static final String WRENCH_KEY = "wrench";

  /**
   * Use the configuration to get extra column values
   */
  private static AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();

  /**
   * Cache the index numbers for the extra columns
   */
  private List<Integer> extraColumnIndices = new LinkedList<Integer>();

  private int columnIndex;

  /**
   * 
   */
  public FindingStyledLabelProvider(int colIndex) {
    this.columnIndex = colIndex;
    loadImagesIntoRegistry();

    String[] names = config.getExtraColumnNames();
    for (String name : names) {
      int index = config.getColumnIndex(name);
      extraColumnIndices.add(index);
    }
  }

  /**
   * Load images.
   */
  private void loadImagesIntoRegistry() {
    final ImageRegistry imgReg = Activator.getDefault().getImageRegistry();
    if (imgReg.get(TICK_KEY) == null) {
      final URL url = this.getClass().getResource("/icons/tick.png");
      imgReg.put(TICK_KEY, ImageDescriptor.createFromURL(url));
    }
    if (imgReg.get(CROSS_KEY) == null) {
      final URL url = this.getClass().getResource("/icons/cross.png");
      imgReg.put(CROSS_KEY, ImageDescriptor.createFromURL(url));
    }
    if (imgReg.get(PAGE_WHITE_STACK_KEY) == null) {
      final URL url = this.getClass().getResource("/icons/page_white_stack.png");
      imgReg.put(PAGE_WHITE_STACK_KEY, ImageDescriptor.createFromURL(url));
    }
    if (imgReg.get(PAGE_KEY) == null) {
      final URL url = this.getClass().getResource("/icons/page.png");
      imgReg.put(PAGE_KEY, ImageDescriptor.createFromURL(url));
    }
    if (imgReg.get(WRENCH_KEY) == null) {
      final URL url = this.getClass().getResource("/icons/wrench.png");
      imgReg.put(WRENCH_KEY, ImageDescriptor.createFromURL(url));
    }
  }

  /**
   * Get the entry text for the specified column
   * 
   * @param entry
   * @param colIndex
   * @return
   */
  private String getColumnText(IFindingEntry entry, int colIndex) {
    switch (colIndex) {
      case 0: {
        return entry.getFileName();
      }
      case 1: {
        return entry.getLine();
      }
      case 2: {
        return entry.getTool();
      }
      case 3: {
        String sfp = entry.getSfp();
        if (sfp != null) {
          return fixSfpCweIdentifier(sfp);
        }
        return null;
      }
      case 4: {
        String cwe = entry.getCwe();
        if (cwe != null) {
          return fixSfpCweIdentifier(cwe);
        }
        return null;
      }
      case 5: {
        return Integer.toString(entry.getTrust());
      }
      case 6: {
        String desc = entry.getDescription();
        String[] split = desc.split(":", 2);
        if (split.length > 1) {
          return split[1].trim();
        } else {
          return entry.getDescription();
        }
      }
      default: {
        int index = colIndex - 7;
        if (index < extraColumnIndices.size()) {
          // Get the config index matching this column
          index = extraColumnIndices.get(index);
          String cwe = entry.getCwe();
          String value = (String) config.getCell(cwe, index);
          return value;
        }
      }
    }
    return null;
  }

  /**
   * CWE and SFP identifiers should not have single hyphens in them.
   * 
   * @param name
   * @return
   */
  private String fixSfpCweIdentifier(String name) {
    return name.replaceAll("([^-])-([^-])", "$1$2");
  }

  /**
   * Gets the image.
   * 
   * @param element
   *          the element
   * @return the image
   */
  public Image getImage(IFindingEntry entry, int colIndex) {
    final ImageRegistry imgReg = Activator.getDefault().getImageRegistry();

    switch (colIndex) {
      case 0: {
        return imgReg.get(PAGE_WHITE_STACK_KEY);
      }
      case 1: {
        return imgReg.get(PAGE_KEY);
      }
      case 2: {
        return imgReg.get(WRENCH_KEY);
      }
      case 3: {
        // fall through
      }
      case 4: {
        // fall through
      }
      case 5: {
        Boolean citing = entry.getCiting();
        if (citing != null) {
          return citing ? imgReg.get(CROSS_KEY) : imgReg.get(TICK_KEY);
        }
        return null;
      }
      case 6: {
        break;
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
      switch (columnIndex) {
        case 3: {
          String sfp = entry.getSfp();
          if (sfp != null) {
            String[] sfpData = DescriptionMap.INSTANCE.getSfpMap().get(sfp);
            if (sfpData != null && sfpData.length > 1) {
              return sfpData[1];
            }
          }
          return "";
        }
        case 4: {
          String cwe = entry.getCwe();
          if (cwe != null) {
            String[] cweData = DescriptionMap.INSTANCE.getCweMap().get(cwe);
            if (cweData != null && cweData.length > 1) {
              return cweData[1];
            }
          }
          return "";
        }
        case 6: {
          String desc = entry.getDescription();
          String[] split = desc.split(":", 2);
          if (split.length > 1) {
            return split[0].trim();
          }
          return "";
        }
        
//        case 0: {
//          return entry.getFileName();
//        }
//        case 1: {
//          return entry.getLine();
//        }
//        case 2: {
//          return entry.getTool();
//        }
//        case 3: {
//          String sfp = entry.getSfp();
//          if (sfp != null) {
//            return fixSfpCweIdentifier(sfp);
//          }
//          return null;
//        }
//        case 4: {
//          String cwe = entry.getCwe();
//          if (cwe != null) {
//            return fixSfpCweIdentifier(cwe);
//          }
//          return null;
//        }
//        case 5: {
//          return Integer.toString(entry.getTrust());
//        }
//        case 6: {
//          return entry.getDescription();
//        }
        default: {
          return entry.getPath();
        }
      }
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
    final IFindingEntry entry = (IFindingEntry) cell.getElement();
    int index = cell.getColumnIndex();
    String text = getColumnText(entry, index);
    if (text == null) {
      text = "";
    }
    final StyledString styledString = new StyledString(text);

    Boolean citing = entry.getCiting();
    if (citing != null) {
      if (citing) {
        cell.setForeground(FOREGROUND_RED);
        cell.setBackground(RED);
      } else {
        cell.setForeground(FOREGROUND_GREEN);
        cell.setBackground(GREEN);
      }
    } else {
      cell.setForeground(FOREGROUND_BLACK);
      cell.setBackground(WHITE);
    }

    cell.setText(styledString.toString());
    cell.setStyleRanges(styledString.getStyleRanges());
    cell.setImage(getImage(entry, cell.getColumnIndex()));
  }

}
