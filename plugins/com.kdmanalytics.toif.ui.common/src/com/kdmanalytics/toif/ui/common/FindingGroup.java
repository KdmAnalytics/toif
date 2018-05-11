/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
package com.kdmanalytics.toif.ui.common;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.resources.IFile;

/** A group of findings
 * 
 * @author Ken Duck
 *
 */
public class FindingGroup implements IFindingEntry {
  /**
   * Use the configuration to get SFP values
   */
  private static AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();

  /**
   * Findings contained within the group
   */
  List<IFindingEntry> entries = new LinkedList<IFindingEntry>();
  
  private final IFile ifile;
  private final int line;
  private final String cwe;			// The actual cwe
  private final String sfp;			// The actual sfp
 
  
  /**
   * Used for testing only
   */
  private File file;
  
  public FindingGroup(IFile file, int line, String sfp, String cwe) {
    this.ifile = file;
    this.line = line;
    
    this.cwe = fixSfpCweIdentifier(cwe);
    // Ignore the provided SFP, instead use the value found in the adaptor configuration
    //this.sfp = sfp;
    this.sfp = config.getSfp(cwe);
  }
  
  /** Constructor for testing purposes only
   * 
   * @param file2
   * @param line2
   * @param sfp2
   * @param cwe2
   */
  public FindingGroup(File file, int line, String sfp, String cwe) {
    this.ifile = null; // NOT USED IS TESTING
    this.file = file;
    this.line = line;
    this.cwe = fixSfpCweIdentifier(cwe);
    // Ignore the provided SFP, instead use the value found in the adaptor configuration
    //this.sfp = sfp;
    this.sfp = config.getSfp(cwe);
  }
  
  private String fixSfpCweIdentifier(String name) {
    return name.replaceAll("([^-])-([^-])", "$1$2");
  }


  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getFile()
   */
  @Override
  public IFile getFile() {
    return ifile;
  }

  /** Get all entries within this group
   * 
   * @return
   */
  public Collection<IFindingEntry> getFindingEntries() {
    return entries;
  }

  /** Add a new entry to the group
   * 
   * @param entry
   */
  public void add(FindingEntry entry) {
    if (!ifile.equals(entry.getFile()) ||
        line != entry.getLineNumber() //||
 // RJF FIX       !cwe.equals(entry.getCwe()) ||
 //       !sfp.equals(entry.getSfp())
        ) {
      throw new IllegalArgumentException("Cannot add this entry to the group, it does not belong\n  * GROUP: " + this + "\n  * ENTRY: " + entry);
    }
    
    entries.add(entry);
    entry.setParent(this);
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
  
  
  
  public String getCweDisplay()
  {
  if (entries.isEmpty())
	  return this.cwe;
  else
	  {
	  Set<String> set = new HashSet<String>();
	  for (IFindingEntry entry : entries)
		  set.add( entry.getCwe());
		  
	  if (set.size() == 1)
		  return this.cwe;
	  else
		  return "CWE *";
	  }
  
  

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
    String tool = null;
    for(IFindingEntry entry: entries) {
      if (tool == null) {
        tool = entry.getTool();
      } else {
        if (!tool.equals(entry.getTool())) {
          return "MULTIPLE";
        }
      }
    }
    return tool;
  }

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getSfp()
   */
  @Override
  public String getSfp() {
    return sfp;
  }
  
 
  
  // Provide SFP Display text, fallback to sfp value
  public String getSfpDisplay()
  {
  if (entries.isEmpty())
	  return this.sfp;
  else
	  {
	  Set<String> set = new HashSet<String>();
	  for (IFindingEntry entry : entries)
		  set.add( entry.getSfp());
		  
	  if (set.size() == 1)
		  return this.sfp;
	  else
		  return "SFP *";
	  }
  }
  

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getTrust()
   */
  @Override
  public int getTrust() {
    int trust = 0;
    for(IFindingEntry entry: entries) {
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
    for(IFindingEntry entry: entries) {
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
    if (ifile != null) {
      return ifile.getName();
    } else {
      return file.getName();
    }
  }

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getPath()
   */
  @Override
  public String getPath() {
    if (ifile != null) {
      return ifile.getProjectRelativePath().toString();
    } else {
      return file.getAbsolutePath();
    }
  }

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getSearchableText()
   */
  @Override
  public String getSearchableText() {
    StringBuilder sb = new StringBuilder();
    sb.append(ifile.getProjectRelativePath().toString());
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
    for(IFindingEntry entry: entries) {
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
    for(IFindingEntry entry: entries) {
      sb.append(" | ");
      sb.append(entry.getTool());
    }
    return sb.toString();
  }

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#cite(java.lang.Boolean)
   */
  @Override
  public void cite(Boolean b) {
    for(IFindingEntry entry: entries) {
      entry.cite(b);
    }
  }

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#setTrust(int)
   */
  @Override
  public void setTrust(int val) {
    for(IFindingEntry entry: entries) {
      entry.setTrust(val);
    }
  }

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getTypeId()
   */
  @Override
  public Collection<String> getTypeIds() {
    List<String> results = new ArrayList<String>(entries.size());
    for(IFindingEntry entry: entries) {
      results.add(((FindingEntry)entry).getTypeId());
    }
    return results;
  }

  /*
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#getKdmLine()
   */
  @Override
  public int getKdmLine() {
    return line;
  }

  /** Get the number of contained entries
   * 
   * @return
   */
  public int size() {
    return entries.size();
  }

  /* Groups never hold another group
   * (non-Javadoc)
   * @see com.kdmanalytics.toif.ui.common.IFindingEntry#group()
   */
@Override
public Optional<FindingGroup> group()
	{
	return Optional.empty();
	}

@Override
public boolean isGroup()
	{
	return true;
	}
  
}
