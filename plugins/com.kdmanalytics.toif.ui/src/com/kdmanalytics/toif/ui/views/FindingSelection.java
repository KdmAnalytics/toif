/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.views;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.kdmanalytics.toif.ui.common.FindingEntry;
import com.kdmanalytics.toif.ui.common.FindingGroup;
import com.kdmanalytics.toif.ui.common.IFindingEntry;

/**
 * Provide generic operations that might be run against a selection of findings.
 * 
 * @author Ken Duck
 *        
 */
public class FindingSelection {
  
  private static final Object NO_CWE = "CWE--1";
  
  /**
   * Current selected element
   */
  private List<IFindingEntry> selection = new LinkedList<IFindingEntry>();
  
  /**
   * Create an empty selection
   */
  public FindingSelection() {
  }
  
  /**
   * Create a selection with the specified entries
   * 
   * @param findings
   */
  public FindingSelection(IFindingEntry[] findings) {
    for (IFindingEntry entry : findings) {
      add(entry);
    }
  }
  
  /**
   * Create a selection, either on cited entries of on uncited entries
   * 
   * @param findings
   * @param cited
   */
  public FindingSelection(IFindingEntry[] findings, boolean cited) {
    for (IFindingEntry entry : findings) {
      if (cited) {
        Boolean citing = entry.getCiting();
        int trust = entry.getTrust();
        if (citing != null || trust > 0) {
          add(entry);
        }
      } else {
        Boolean citing = entry.getCiting();
        int trust = entry.getTrust();
        if (citing == null && trust == 0) {
          add(entry);
        }
        
      }
    }
  }
  
  /**
   * Clear the selection
   */
  protected void clear() {
    selection.clear();
  }
  
  /**
   * Add a finding
   * 
   * @param entry
   */
  protected void add(IFindingEntry entry) {
    selection.add(entry);
  }
  
  /**
   * Cite the selected findings. 3 states:
   * 
   * o true: Finding is a weakness o false: Finding is not a weakness o null: Remove citing
   * 
   * @param b
   */
  public void cite(Boolean b) {
    for (IFindingEntry finding : selection) {
      if (finding instanceof FindingEntry) {
        // If this finding is part of a group, then cite the entire group
        FindingGroup parent = ((FindingEntry)finding).getParent();
        if(parent != null) {
          finding = parent;
        }
      }
      finding.cite(b);
    }
  }
  
  /**
   * Set the trust level for this finding type. From the documentation:
   * 
   * This option ... sets the level of trust for the selected finding. This level is propagated
   * throughout the data set, marking any finding with the same CWE from the same tool with the
   * specified value. Trust is an indication of how much faith the analyst has in the tools ability
   * to accurately detect the defect.
   * 
   * @param val
   * @return Set of finding type IDs that were affected by the trust change
   */
  public Set<String> setTrust(int val) {
    Set<String> types = new HashSet<String>();
    for (IFindingEntry finding : selection) {
      finding.setTrust(val);
      types.addAll(finding.getTypeIds());
    }
    return types;
  }
  
  /**
   * Get the trust for the first element in the selection.
   * 
   * @return
   */
  public int getTrust() {
    if (!selection.isEmpty()) {
      IFindingEntry entry = selection.get(0);
      return entry.getTrust();
    }
    return 0;
  }
  
  /**
   * Open a browser pane for each CWE selected
   */
  public void moreInfo() {
    Set<String> cwes = new HashSet<String>();
    
    for (IFindingEntry finding : selection) {
      String cwe = finding.getCwe();
      if (cwe != null && !cwe.isEmpty() && !NO_CWE.equals(cwe)) {
        cwes.add(cwe);
      }
    }
    
    IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
    IWebBrowser browser;
    
    for (String cwe : cwes) {
      try {
        browser = browserSupport.createBrowser(IWorkbenchBrowserSupport.LOCATION_BAR, null, cwe, cwe);
        URL url = new URL("http://cwe.mitre.org/data/definitions/" + cwe.replace("CWE-", "") + ".html");
        browser.openURL(url);
      } catch (PartInitException e) {
        e.printStackTrace();
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Export selected findings in a TSV (Tab Separated Value) file
   * 
   * @param file
   */
  public void exportTsv(File file) throws IOException {
    PrintWriter out = new PrintWriter(new FileWriter(file));
    try {
      out.println("SFP\tCWE\tCiting Status\tTrust\tResource\tLine Number\tKDM Line Number\tSCA tool\tWeakness Description");
      
      for (IFindingEntry finding : selection) {
        out.print(finding.getSfp());
        out.print('\t');
        out.print(finding.getCwe());
        out.print('\t');
        if (finding.getCiting() != null) out.print(finding.getCiting());
        out.print('\t');
        out.print(finding.getTrust());
        out.print('\t');
        out.print(finding.getPath());
        out.print('\t');
        out.print(finding.getLine());
        out.print('\t');
        out.print(finding.getKdmLine());
        out.print('\t');
        out.print(finding.getTool());
        out.print('\t');
        out.print(finding.getDescription());
        out.println();
      }
    } finally {
      if (out != null) out.close();
    }
  }
  
  /**
   * Return true if the current finding selection is empty.
   * 
   * @return
   */
  public boolean isEmpty() {
    return selection == null || selection.isEmpty();
  }
  
  /**
   * Get the selection
   * 
   * @return
   */
  protected Object[] getSelectionArray() {
    return selection.toArray();
  }
  
}
