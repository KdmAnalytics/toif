/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.common;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

/**
 * House the information for a finding that is being displayed in the finding view. Build the data
 * from a marker.
 * 
 * @author Ken Duck
 *        
 */
public class FindingEntry extends FindingData implements IFindingEntry {
  
  private FindingGroup group;

  /**
   * Pull the interesting information out of the marker.
   * 
   * @param marker
   */
  public FindingEntry(IMarker marker) {
    try {
      IFile resource = (IFile) marker.getResource();
      String tool = marker.getType();
      int index = tool.lastIndexOf('.');
      tool = tool.substring(index + 1);
      tool = tool.replace("Marker", "");
      String description = marker.getAttribute(IMarker.MESSAGE, "");
      int line = marker.getAttribute(IMarker.LINE_NUMBER, 0);
      int offset = marker.getAttribute(IMarker.CHAR_START, 0);
      String cwe = marker.getAttribute(IToifMarker.CWE, "");
      String sfp = marker.getAttribute(IToifMarker.SFP, "");
      
      cwe = fixSfpCweIdentifier(cwe);
      sfp = fixSfpCweIdentifier(sfp);

      
      setFindingData(resource, tool, description, line, offset, cwe, sfp);
      
      // Map<String, Object> attrs = marker.getAttributes();
      // System.err.println("ATTRS");
      // for(Map.Entry<String, Object> attr: attrs.entrySet())
      // {
      // System.err.println(" o " + attr.getKey() + "=" + attr.getValue());
      // }
    } catch (CoreException e) {
      e.printStackTrace();
    }
  }

  /** CWE and SFP identifiers should not have single hyphens in them.
   * 
   * @param name
   * @return
   */
  private String fixSfpCweIdentifier(String name) {
    return name.replaceAll("([^-])-([^-])", "$1$2");
  }

  /** This constructor is intended for testing purposes only
   * 
   * @param file
   * @param tool
   * @param descr
   * @param line
   * @param offset
   * @param cwe
   * @param sfp
   */
  public FindingEntry(File file, String tool, String descr, int line, int offset, String cwe, String sfp) {
    setFindingData(file, tool, descr, line, offset, cwe, sfp);
  }

  /** The entry may be part of a group. This sets the group.
   * 
   * @param findingGroup
   */
  public void setParent(FindingGroup findingGroup) {
    this.group = findingGroup;
  }
  
  /**The entry may be part of a group. This gets the group.
   * 
   * @return
   */
  public FindingGroup getParent() {
    return group;
  }
}
