/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
package com.kdmanalytics.toif.ui.views.sort;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import com.kdmanalytics.toif.ui.common.AdaptorConfiguration;
import com.kdmanalytics.toif.ui.common.FindingEntry;
import com.kdmanalytics.toif.ui.common.IFindingEntry;

/** Test sorting by table column.
 * 
 * @author Ken Duck
 *
 */
public class ColumnSortTests {
  private static final String sortedBySfpConfig = "/resources/SortedBySfp.csv";
  
  @After
  public void after() {
    AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
    config.clear();
  }
  
  /** Test that sorting is done by File column
   * 
   * @throws IOException 
   */
  @Test
  public void testConfigSortByFile() throws IOException {
    File file = new File(new File("."), sortedBySfpConfig);
    assertTrue("Test file: " + file.getAbsolutePath(), file.exists());
    AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
    config.load(file);
    
    List<IFindingEntry> findings = new LinkedList<IFindingEntry>();
    findings.add(new FindingEntry(new File("A"), "Findbugs", "VA_FORMAT_STRING", 172, 0, "CWE-785", "SFP-9"));
    findings.add(new FindingEntry(new File("B"), "Jlint", "shadow_local", 175, 0, "CWE-125", "SFP-8"));
    findings.add(new FindingEntry(new File("C"), "Findbugs", "VA_FORMAT_STRING", 188, 0, "CWE-114", "SFP--1"));
    findings.add(new FindingEntry(new File("D"), "Jlint", "hashcode_not_overridden", 1, 0, "CWE-131", "SFP--1"));
    
    FindingViewColumnComparator comparator = new FindingViewColumnComparator();
    comparator.setColumn(0);
    Collections.sort(findings, comparator);
    
    Iterator<IFindingEntry> it = findings.iterator();
    assertEquals("D", it.next().getFileName());
    assertEquals("C", it.next().getFileName());
    assertEquals("B", it.next().getFileName());
    assertEquals("A", it.next().getFileName());
    
    // Reverse direction
    comparator.setColumn(0);
    Collections.sort(findings, comparator);
    
    it = findings.iterator();
    assertEquals("A", it.next().getFileName());
    assertEquals("B", it.next().getFileName());
    assertEquals("C", it.next().getFileName());
    assertEquals("D", it.next().getFileName());
  }
  
  /** Test that sorting is done by Line number
   * 
   * @throws IOException 
   */
  @Test
  public void testConfigSortByLine() throws IOException {
    File file = new File(new File("."), sortedBySfpConfig);
    assertTrue("Test file: " + file.getAbsolutePath(), file.exists());
    AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
    config.load(file);
    
    List<IFindingEntry> findings = new LinkedList<IFindingEntry>();
    findings.add(new FindingEntry(new File("A"), "Findbugs", "VA_FORMAT_STRING", 172, 0, "CWE-785", "SFP-9"));
    findings.add(new FindingEntry(new File("B"), "Jlint", "shadow_local", 175, 0, "CWE-125", "SFP-8"));
    findings.add(new FindingEntry(new File("C"), "Findbugs", "VA_FORMAT_STRING", 188, 0, "CWE-114", "SFP--1"));
    findings.add(new FindingEntry(new File("D"), "Jlint", "hashcode_not_overridden", 1, 0, "CWE-131", "SFP--1"));
    
    FindingViewColumnComparator comparator = new FindingViewColumnComparator();
    comparator.setColumn(1);
    Collections.sort(findings, comparator);
    
    Iterator<IFindingEntry> it = findings.iterator();
    assertEquals("C", it.next().getFileName());
    assertEquals("B", it.next().getFileName());
    assertEquals("A", it.next().getFileName());
    assertEquals("D", it.next().getFileName());
    
    // Reverse direction
    comparator.setColumn(1);
    Collections.sort(findings, comparator);
    
    it = findings.iterator();
    assertEquals("D", it.next().getFileName());
    assertEquals("A", it.next().getFileName());
    assertEquals("B", it.next().getFileName());
    assertEquals("C", it.next().getFileName());
  }
  
  /** Test that sorting is done by Line number
   * 
   * @throws IOException 
   */
  @Test
  public void testConfigSortByTool() throws IOException {
    File file = new File(new File("."), sortedBySfpConfig);
    assertTrue("Test file: " + file.getAbsolutePath(), file.exists());
    AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
    config.load(file);
    
    List<IFindingEntry> findings = new LinkedList<IFindingEntry>();
    findings.add(new FindingEntry(new File("A"), "Findbugs", "VA_FORMAT_STRING", 172, 0, "CWE-785", "SFP-9"));
    findings.add(new FindingEntry(new File("B"), "Jlint", "shadow_local", 175, 0, "CWE-125", "SFP-8"));
    findings.add(new FindingEntry(new File("C"), "Findbugs", "VA_FORMAT_STRING", 188, 0, "CWE-114", "SFP--1"));
    findings.add(new FindingEntry(new File("D"), "Jlint", "hashcode_not_overridden", 1, 0, "CWE-131", "SFP--1"));
    
    FindingViewColumnComparator comparator = new FindingViewColumnComparator();
    comparator.setColumn(2);
    Collections.sort(findings, comparator);
    
    Iterator<IFindingEntry> it = findings.iterator();
    assertEquals("B", it.next().getFileName());
    assertEquals("D", it.next().getFileName());
    assertEquals("A", it.next().getFileName());
    assertEquals("C", it.next().getFileName());
    
    // Reverse direction
    comparator.setColumn(2);
    Collections.sort(findings, comparator);
    
    it = findings.iterator();
    assertEquals("A", it.next().getFileName());
    assertEquals("C", it.next().getFileName());
    assertEquals("B", it.next().getFileName());
    assertEquals("D", it.next().getFileName());
  }
  
  /** Test that sorting is done by Line number
   * 
   * @throws IOException 
   */
  @Test
  public void testConfigSortBySfp() throws IOException {
    File file = new File(new File("."), sortedBySfpConfig);
    assertTrue("Test file: " + file.getAbsolutePath(), file.exists());
    AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
    config.load(file);
    
    List<IFindingEntry> findings = new LinkedList<IFindingEntry>();
    findings.add(new FindingEntry(new File("A"), "Findbugs", "VA_FORMAT_STRING", 172, 0, "CWE-785", "SFP-9"));
    findings.add(new FindingEntry(new File("B"), "Jlint", "shadow_local", 175, 0, "CWE-125", "SFP-8"));
    findings.add(new FindingEntry(new File("C"), "Findbugs", "VA_FORMAT_STRING", 188, 0, "CWE-114", "SFP--1"));
    findings.add(new FindingEntry(new File("D"), "Jlint", "hashcode_not_overridden", 1, 0, "CWE-131", "SFP--1"));
    
    FindingViewColumnComparator comparator = new FindingViewColumnComparator();
    comparator.setColumn(3);
    Collections.sort(findings, comparator);
    
    Iterator<IFindingEntry> it = findings.iterator();
    assertEquals("A", it.next().getFileName());
    assertEquals("B", it.next().getFileName());
    assertEquals("C", it.next().getFileName());
    assertEquals("D", it.next().getFileName());
    
    // Reverse direction
    comparator.setColumn(3);
    Collections.sort(findings, comparator);
    
    it = findings.iterator();
    assertEquals("C", it.next().getFileName());
    assertEquals("D", it.next().getFileName());
    assertEquals("B", it.next().getFileName());
    assertEquals("A", it.next().getFileName());
  }
  
  /** Test that sorting is done by Line number
   * 
   * @throws IOException 
   */
  @Test
  public void testConfigSortByCwe() throws IOException {
    File file = new File(new File("."), sortedBySfpConfig);
    assertTrue("Test file: " + file.getAbsolutePath(), file.exists());
    AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
    config.load(file);
    
    List<IFindingEntry> findings = new LinkedList<IFindingEntry>();
    findings.add(new FindingEntry(new File("A"), "Findbugs", "VA_FORMAT_STRING", 172, 0, "CWE-785", "SFP-9"));
    findings.add(new FindingEntry(new File("B"), "Jlint", "shadow_local", 175, 0, "CWE-125", "SFP-8"));
    findings.add(new FindingEntry(new File("C"), "Findbugs", "VA_FORMAT_STRING", 188, 0, "CWE-114", "SFP--1"));
    findings.add(new FindingEntry(new File("D"), "Jlint", "hashcode_not_overridden", 1, 0, "CWE-131", "SFP--1"));
    
    FindingViewColumnComparator comparator = new FindingViewColumnComparator();
    comparator.setColumn(4);
    Collections.sort(findings, comparator);
    
    System.err.println("SORTED________");
    for (IFindingEntry finding : findings) {
      System.err.println("  * " + finding.getFileName());
    }
    
    Iterator<IFindingEntry> it = findings.iterator();
    assertEquals("A", it.next().getFileName());
    assertEquals("D", it.next().getFileName());
    assertEquals("B", it.next().getFileName());
    assertEquals("C", it.next().getFileName());
    
    // Reverse direction
    comparator.setColumn(4);
    Collections.sort(findings, comparator);
    
    it = findings.iterator();
    assertEquals("C", it.next().getFileName());
    assertEquals("B", it.next().getFileName());
    assertEquals("D", it.next().getFileName());
    assertEquals("A", it.next().getFileName());
  }
}
