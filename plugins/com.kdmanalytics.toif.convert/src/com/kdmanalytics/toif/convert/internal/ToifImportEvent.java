/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
package com.kdmanalytics.toif.convert.internal;

/** Tracking the import process
 * 
 * @author Ken Duck
 *
 */
public class ToifImportEvent {

  public static final int IMPORT_REPO_START = 1;
  public static final int IMPORT_REPO_DONE = 2;
  public static final int IMPORT_FINDINGS_START = 3;
  public static final int IMPORT_FINDINGS_DONE = 4;
  
  private int type;

  public ToifImportEvent(int type) {
    this.type = type;
  }
  
  public int getType() {
    return type;
  }
}
