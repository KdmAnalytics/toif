/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.internal.filters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.kdmanalytics.toif.ui.common.IFindingEntry;

/**
 * Creates a filter which filters out all elements that don't have/aren't findingEntrys with a trust
 * level above the set amount.
 * 
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * @author Ken Duck
 */
public class TrustFilter extends ViewerFilter {
  
  /**
   * The trust amount that the element must be over.
   */
  private int amount;
  
  /**
   * create a new filter with the trust amount set.
   * 
   * @param amount
   */
  public TrustFilter(int amount) {
    this.amount = amount;
  }
  
  /**
   * @return the amount
   */
  public int getAmount() {
    return amount;
  }
  
  @Override
  public boolean select(Viewer viewer, Object parentElement, Object element) {
    if (element instanceof IFindingEntry) {
      IFindingEntry entry = (IFindingEntry) element;
      List<IFindingEntry> list = new ArrayList<IFindingEntry>();
      list.add(entry);
      return trustIsHighEnough(list);
    }
    return false;
  }
  
  /**
   * set the trust amount
   */
  public void setAmount(int amount) {
    this.amount = amount;
  }
  
  /**
   * returns true if there is a finding with a trust higher than amount.
   * 
   * @param element
   */
  private boolean trustIsHighEnough(List<IFindingEntry> list) {
    for (IFindingEntry findingEntry : list) {
      int trust = findingEntry.getTrust();
      if (trust >= amount) {
        return true;
      }
    }
    
    return false;
  }
  
}
