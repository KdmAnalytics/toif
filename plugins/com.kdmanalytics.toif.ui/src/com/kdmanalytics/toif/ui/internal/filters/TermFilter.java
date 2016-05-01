/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.internal.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.kdmanalytics.toif.ui.common.IFindingEntry;

/**
 * Originally from TOIF code. Updated for EToif.
 * 
 * Filter ToifReportEntry based on string matches from the FindingEntry
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * @author Kyle Girard <kyle@kdmanalytics.com>
 * @author Ken Duck
 */
public class TermFilter extends ViewerFilter {
  
  /** The terms. */
  private final String[] terms;
  
  private boolean result;
  
  /**
   * Instantiates a new term filter.
   * 
   * @param terms
   *          the terms
   */
  public TermFilter(String[] terms) {
    this.terms = terms;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers .Viewer,
   * java.lang.Object, java.lang.Object)
   */
  @Override
  public boolean select(Viewer viewer, Object parentElement, Object element) {
    if (element instanceof IFindingEntry) {
      IFindingEntry findingEntry = (IFindingEntry) element;
      String text = findingEntry.getSearchableText().toLowerCase();
      
      String[] textarray = text.split("\\|");
      
      String[] terms2 = terms.clone();
      
      if (terms2.length > 0) {
        String t = terms2[0].trim();
        if (t.startsWith("NOT")) {
          result = false;
          terms2[0] = t.replace("NOT", "");
        } else {
          result = true;
        }
      }
      
      for (String term : terms2) {
        for (String string : textarray) {
          if ((string.toLowerCase().trim()).contains(term.toLowerCase().trim())) {
            return result;
          }
        }
        
      }
    }
    return !result;
  }
  
}
