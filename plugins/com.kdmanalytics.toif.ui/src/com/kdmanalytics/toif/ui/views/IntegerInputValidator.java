/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.views;

import org.eclipse.jface.dialogs.IInputValidator;

/**
 * Only accept numeric input
 * 
 * @author Ken Duck
 *        
 */
public class IntegerInputValidator implements IInputValidator {
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
   */
  @Override
  public String isValid(String newText) {
    try {
      int i = Integer.parseInt(newText);
      if (i >= 0) {
        if (i <= 100) {
          return null;
        }
      }
    } catch (NumberFormatException e) {}
    
    // If we get here the value was invalid for some reason or another
    return "Value must be an integer number between 0 and 100";
  }
  
}
