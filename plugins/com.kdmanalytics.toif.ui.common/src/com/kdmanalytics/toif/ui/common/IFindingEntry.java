/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
package com.kdmanalytics.toif.ui.common;

import java.util.Collection;
import java.util.Optional;

import org.eclipse.core.resources.IFile;

/** Common finding interface
 * 
 * @author Ken Duck
 *
 */
public interface IFindingEntry {

  public IFile getFile();

  public int getLineNumber();

  public String getCwe();

  public String getLine();

  public String getTool();

  public String getSfp();

  public int getTrust();

  public String getDescription();

  public Boolean getCiting();

  public String getFileName();

  public String getPath();

  public String getSearchableText();

  public void cite(Boolean b);

  public void setTrust(int val);

  public Collection<String> getTypeIds();

  public int getKdmLine();
  
  // Expose group membership
  public Optional<FindingGroup> group();
  
  // State if entry is a group
  public boolean isGroup();
  
}
