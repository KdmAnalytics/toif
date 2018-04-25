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
import com.kdmanalytics.toif.ui.common.FindingGroup;
import com.kdmanalytics.toif.ui.common.IFindingEntry;

/** Test the weight sorting algorithm.
 * 
 * Test that sorting is done by:
 *     1. Number of tools defining defects on same file/line 
 *     2. Calculated weighting (see REQ 9.x for details) 
 *     3. Confidence 
 *     4. File 
 *     5. Line
 * 
 * @author Ken Duck
 *
 */
public class FindingGroupSortTests {
  private static final String sortedBySfpConfig = "/resources/SortedBySfp.csv";
  
  @After
  public void after() {
    AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
    config.clear();
  }
  
  /** Test that sorting is done by:
   *     1. Number of tools defining defects on same file/line 
   *     2. Calculated weighting (see REQ 9.x for details) 
   *     3. Confidence 
   *     4. File 
   *     5. Line
   * 
   * @throws IOException 
   */
  @Test
  public void testConfigSortByOrder() throws IOException {
    File file = new File(new File("."), sortedBySfpConfig);
    assertTrue("Test file: " + file.getAbsolutePath(), file.exists());
    AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
    config.load(file);
    
    List<IFindingEntry> findings = new LinkedList<IFindingEntry>();
    findings.add(new FindingEntry(new File("A"), "Findbugs", "VA_FORMAT_STRING", 172, 0, "CWE-785", "SFP-9"));
    findings.add(new FindingEntry(new File("B"), "Jlint", "shadow_local", 175, 0, "CWE-125", "SFP-8"));
    findings.add(new FindingEntry(new File("C"), "Findbugs", "VA_FORMAT_STRING", 188, 0, "CWE-114", "SFP--1"));
    findings.add(new FindingEntry(new File("D"), "Jlint", "hashcode_not_overridden", 1, 0, "CWE-131", "SFP--1"));
    
    FindingGroupComparator comparator = new FindingGroupComparator();
    Collections.sort(findings, comparator);
    
    //    System.err.println("SORTED________");
    //    for (IFindingEntry finding : findings) {
    //      System.err.println("  * " + finding.getFileName());
    //    }
    
    Iterator<IFindingEntry> it = findings.iterator();
    assertEquals("B", it.next().getFileName());
    assertEquals("A", it.next().getFileName());
    assertEquals("C", it.next().getFileName());
    assertEquals("D", it.next().getFileName());
  }
  
  /** Test that sorting is done by:
   *     1. Number of tools defining defects on same file/line 
   *     2. Calculated weighting (see REQ 9.x for details) 
   *     3. Confidence 
   *     4. File 
   *     5. Line
   * 
   * @throws IOException 
   */
  @Test
  public void testConfigSortByGrouping() throws IOException {
    File file = new File(new File("."), sortedBySfpConfig);
    assertTrue("Test file: " + file.getAbsolutePath(), file.exists());
    AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
    config.load(file);
    
    List<IFindingEntry> findings = new LinkedList<IFindingEntry>();
    findings.add(new FindingEntry(new File("A"), "Findbugs", "VA_FORMAT_STRING", 172, 0, "CWE-785", "SFP--1"));
    findings.add(new FindingEntry(new File("B"), "Jlint", "shadow_local", 175, 0, "CWE-125", "SFP--1"));
    findings.add(new FindingGroup(new File("C"), 188, "SFP--1", "CWE-133"));
    
    FindingGroupComparator comparator = new FindingGroupComparator();
    Collections.sort(findings, comparator);
    
    Iterator<IFindingEntry> it = findings.iterator();
    assertEquals("C", it.next().getFileName());
    assertEquals("B", it.next().getFileName());
    assertEquals("A", it.next().getFileName());
  }
  
  /** Test that sorting is done by:
   *     1. Number of tools defining defects on same file/line 
   *     2. Calculated weighting (see REQ 9.x for details) 
   *     3. Confidence 
   *     4. File 
   *     5. Line
   * 
   * @throws IOException 
   */
  @Test
  public void testConfigSortByTrust() throws IOException {
    File file = new File(new File("."), sortedBySfpConfig);
    assertTrue("Test file: " + file.getAbsolutePath(), file.exists());
    AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
    config.load(file);
    
    List<IFindingEntry> findings = new LinkedList<IFindingEntry>();
    findings.add(new FindingEntry(new File("A"), "Findbugs", "VA_FORMAT_STRING", 172, 0, "CWE-398", "SFP-9"));
    findings.add(new FindingEntry(new File("B"), "Jlint", "shadow_local", 175, 0, "CWE-398", "SFP-8"));
    findings.add(new FindingEntry(new File("C"), "Findbugs", "VA_FORMAT_STRING", 188, 0, "CWE-398", "SFP--1"));
    findings.add(new FindingEntry(new File("D"), "Jlint", "hashcode_not_overridden", 1, 0, "CWE-398", "SFP--1"));
    
    FindingGroupComparator comparator = new FindingGroupComparator();
    Collections.sort(findings, comparator);
    
    Iterator<IFindingEntry> it = findings.iterator();
    assertEquals("B", it.next().getFileName());
    assertEquals("D", it.next().getFileName());
    assertEquals("A", it.next().getFileName());
    assertEquals("C", it.next().getFileName());
  }
  
  
  /** Test that sorting is done by:
   *   1. Config file order
   *   2. Grouping
   *   3. Trust
   *   4. Path
   *   5. Line
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
    findings.add(new FindingEntry(new File("A"), "Findbugs", "VA_FORMAT_STRING", 172, 0, "CWE-131", "SFP-9"));
    findings.add(new FindingEntry(new File("B"), "Jlint", "shadow_local", 175, 0, "CWE-131", "SFP-8"));
    findings.add(new FindingEntry(new File("C"), "Findbugs", "VA_FORMAT_STRING", 188, 0, "CWE-131", "SFP--1"));
    findings.add(new FindingEntry(new File("D"), "Jlint", "hashcode_not_overridden", 1, 0, "CWE-131", "SFP--1"));
    
    FindingGroupComparator comparator = new FindingGroupComparator();
    Collections.sort(findings, comparator);
    
    Iterator<IFindingEntry> it = findings.iterator();
    assertEquals("A", it.next().getFileName());
    assertEquals("B", it.next().getFileName());
    assertEquals("C", it.next().getFileName());
    assertEquals("D", it.next().getFileName());
  }
  
  
  /** Test that sorting is done by:
   *     1. Number of tools defining defects on same file/line 
   *     2. Calculated weighting (see REQ 9.x for details) 
   *     3. Confidence 
   *     4. File 
   *     5. Line
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
    findings.add(new FindingEntry(new File("A"), "Findbugs", "VA_FORMAT_STRING", 3, 0, "CWE-131", "SFP-9"));
    findings.add(new FindingEntry(new File("A"), "Jlint", "shadow_local", 2, 0, "CWE-131", "SFP-8"));
    findings.add(new FindingEntry(new File("A"), "Findbugs", "VA_FORMAT_STRING", 1, 0, "CWE-131", "SFP--1"));
    findings.add(new FindingEntry(new File("A"), "Jlint", "hashcode_not_overridden", 4, 0, "CWE-131", "SFP--1"));
    
    FindingGroupComparator comparator = new FindingGroupComparator();
    Collections.sort(findings, comparator);
    
    Iterator<IFindingEntry> it = findings.iterator();
    assertEquals(1, it.next().getLineNumber());
    assertEquals(2, it.next().getLineNumber());
    assertEquals(3, it.next().getLineNumber());
    assertEquals(4, it.next().getLineNumber());
  }
}
