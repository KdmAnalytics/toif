/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
package com.kdmanalytics.toif.ui.common;

import java.util.Collection;

import org.eclipse.core.resources.IFile;

/** Common finding interface
 * 
 * @author Ken Duck
 *
 */
public interface IFindingEntry {

  IFile getFile();

  int getLineNumber();

  String getCwe();

  String getLine();

  String getTool();

  String getSfp();

  int getTrust();

  String getDescription();

  Boolean getCiting();

  String getFileName();

  String getPath();

  String getSearchableText();

  void cite(Boolean b);

  void setTrust(int val);

  Collection<String> getTypeIds();

  int getKdmLine();
  
}
