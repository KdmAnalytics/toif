/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
package com.kdmanalytics.toif.ui.common;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;

/** A group of findings
 * 
 * @author Ken Duck
 *
 */
public class FindingGroup implements IFindingEntry {

  List<FindingEntry> entries = new LinkedList<FindingEntry>();
  private IFile file;
  private int line;
  private String cwe;
  private String sfp;
  
  public FindingGroup(IFile file, int line, String sfp, String cwe) {
    this.file = file;
    this.line = line;
    this.sfp = sfp;
    this.cwe = cwe;
  }
  
  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getFile()
   */
  @Override
  public IFile getFile() {
    return file;
  }

  /** Get all entries within this group
   * 
   * @return
   */
  public Collection<? extends FindingEntry> getFindingEntries() {
    return entries;
  }

  /** Add a new entry to the group
   * 
   * @param entry
   */
  public void add(FindingEntry entry) {
    entries.add(entry);
  }

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getLineNumber()
   */
  @Override
  public int getLineNumber() {
    return line;
  }

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getCwe()
   */
  @Override
  public String getCwe() {
    return cwe;
  }

  /** Get all contained findings as an object array.
   * 
   * @return
   */
  public Object[] getFindingEntryArray() {
    return entries.toArray();
  }

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getLine()
   */
  @Override
  public String getLine() {
    return Integer.toString(line);
  }

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getTool()
   */
  @Override
  public String getTool() {
    return "MULTIPLE";
  }

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getSfp()
   */
  @Override
  public String getSfp() {
    return sfp;
  }

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getTrust()
   */
  @Override
  public int getTrust() {
    int trust = 0;
    for(FindingEntry entry: entries) {
      trust = Math.max(trust, entry.getTrust());
    }
    return trust;
  }

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getDescription()
   */
  @Override
  public String getDescription() {
    return "";
  }

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getCiting()
   */
  @Override
  public Boolean getCiting() {
    boolean first = true;
    Boolean citing = null;
    for(FindingEntry entry: entries) {
      Boolean eCiting = entry.getCiting();
      
      // On the first pass we just accept whatever value we get
      if (first) {
        // If one is null, then the group is null
        if (eCiting == null) return null;
        citing = entry.getCiting();
        first = false;
      } else {
        // If the cites are different, then group is null
        if (!citing.equals(eCiting)) {
          return null;
        }
      }
    }
    return citing;
  }

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getFileName()
   */
  @Override
  public String getFileName() {
    return file.getName();
  }

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getPath()
   */
  @Override
  public String getPath() {
    return file.getProjectRelativePath().toString();
  }

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getSearchableText()
   */
  @Override
  public String getSearchableText() {
    StringBuilder sb = new StringBuilder();
    sb.append(file.getProjectRelativePath().toString());
    sb.append(" | ");
    sb.append(line);
    sb.append(" | ");
    if(sfp != null && !sfp.isEmpty())
    {
        sb.append(sfp);
        sb.append(" | ");
    }
    if(cwe != null && !cwe.isEmpty())
    {
        sb.append(cwe);
        sb.append(" | ");
    }
    sb.append("trust = ").append(getTrust());
    sb.append(getTools());
    sb.append(getEntryDescription());
    return sb.toString();
  }

  /** Get a list of all of the descriptions of findings represented in this group
   * 
   * @return
   */
  private String getEntryDescription() {
    StringBuilder sb = new StringBuilder();
    for(FindingEntry entry: entries) {
      sb.append(" | ");
      sb.append(entry.getDescription());
    }
    return sb.toString();
  }

  /** Get a list of all of the tools represented in this group
   * 
   * @return
   */
  private String getTools() {
    StringBuilder sb = new StringBuilder();
    for(FindingEntry entry: entries) {
      sb.append(" | ");
      sb.append(entry.getTool());
    }
    return sb.toString();
  }
  
}
