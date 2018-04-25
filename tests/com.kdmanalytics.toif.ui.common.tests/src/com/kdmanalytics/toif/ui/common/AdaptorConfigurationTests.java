package com.kdmanalytics.toif.ui.common;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** Load tests for adaptor configuration files.
 * 
 * @author Ken Duck
 *
 */
public class AdaptorConfigurationTests {
  
  private static final String simpleConfig = "/resources/SimpleConfig.csv";
  private static final String appendConfig = "/resources/AppendedColumn.csv";
  private static final String newRowConfig = "/resources/NewRow.csv";
  private static final String replaceSfpConfig = "/resources/ReplaceSfpConfig.csv";

  @Before
  public void before() {
    //AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
  }
  
  @After
  public void after() {
    AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
    config.clear();
  }
  
  /**
   * Load the basic configuration
   * @throws IOException 
   */
  @Test
  public void loadDefaults() throws IOException {
    File file = new File(new File("."), simpleConfig);
    assertTrue("Test file: " + file.getAbsolutePath(), file.exists());
    AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
    assertTrue(config.isEmpty());
    config.load(file);
    assertFalse(config.isEmpty());
    
    // Check default values
    assertDefaults(config);
  }
  
  /** Load a model that has an additional collumn
   * 
   * @throws IOException
   */
  @Test
  public void loadAppendedColumn() throws IOException {
    File file = new File(new File("."), appendConfig);
    assertTrue("Test file: " + file.getAbsolutePath(), file.exists());
    AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
    assertTrue(config.isEmpty());
    config.load(file);
    assertFalse(config.isEmpty());
    
    // Check default values
    assertDefaults(config);
    
    // Check for extra column data
    assertExtraColumns(config);
  }

  /** Load the default file then merge the appended column file
   * 
   * @throws IOException
   */
  @Test
  public void mergeAppendedColumn() throws IOException {
    // First load the default configuration
    File file = new File(new File("."), simpleConfig);
    assertTrue("Test file: " + file.getAbsolutePath(), file.exists());
    AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
    assertTrue(config.isEmpty());
    config.load(file);
    assertFalse(config.isEmpty());

    // Edit a few values
    
    // Merge the appended data
    file = new File(new File("."), appendConfig);
    assertTrue("Test file: " + file.getAbsolutePath(), file.exists());
    config.load(file);
    
    // Check for extra column data
    assertExtraColumns(config);
    
    // Check for edited data
  }
  
  /** Load the default file then merge the a file with a new row
   * 
   * @throws IOException
   */
  @Test
  public void mergeNewRow() throws IOException {
    // First load the default configuration
    File file = new File(new File("."), simpleConfig);
    assertTrue("Test file: " + file.getAbsolutePath(), file.exists());
    AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
    assertTrue(config.isEmpty());
    config.load(file);
    assertFalse(config.isEmpty());

    // Edit a few values
    
    // Merge the new row data
    file = new File(new File("."), newRowConfig);
    assertTrue("Test file: " + file.getAbsolutePath(), file.exists());
    config.load(file);
    
    // Check for extra column data
    assertNewRows(config);
    
    // Check for edited data
  }
  
  /** Load the default file then merge the a file with a new row
   * 
   * @throws IOException
   */
  @Test
  public void replaceSfp() throws IOException {
    // First load the default configuration
    File file = new File(new File("."), simpleConfig);
    assertTrue("Test file: " + file.getAbsolutePath(), file.exists());
    AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
    assertTrue(config.isEmpty());
    config.load(file);
    assertFalse(config.isEmpty());

    // Edit a few values
    
    // Merge the new row data
    file = new File(new File("."), replaceSfpConfig);
    assertTrue("Test file: " + file.getAbsolutePath(), file.exists());
    config.load(file);
    
    // Check for extra column data
    assertReplacedSfps(config);
    
    // Check for edited data
  }
  
  /** Tests that look at default values
   * 
   * @param config
   */
  private void assertDefaults(AdaptorConfiguration config) {
    assertEquals(0, config.getSfpColumnIndex());
    assertEquals(1, config.getCweColumnIndex());
    assertEquals(2, config.getShowColumnIndex());
    
    assertEquals(0, config.getIndex("CWE-190"));
    assertEquals(155, config.getIndex("CWE-785"));
    
    assertEquals(156, config.getIndex("CWE-DOESN'T EXIST"));
    assertEquals(156, config.getWeight("CWE-DOESN'T EXIST"));
    assertEquals(156, config.getSize());
    
    assertEquals(0, config.getTrust("CWE-190", "cppcheck"));
    assertEquals(0, config.getTrust("CWE-190", "findbugs"));
  }

  /** Tests that look at default values
   * 
   * @param config
   */
  private void assertReplacedSfps(AdaptorConfiguration config) {
    assertEquals(0, config.getSfpColumnIndex());
    assertEquals(1, config.getCweColumnIndex());
    assertEquals(2, config.getShowColumnIndex());
    
    assertEquals(0, config.getIndex("CWE-190"));
    assertEquals(1, config.getIndex("CWE-194"));
    assertEquals(2, config.getIndex("CWE-195"));
    
    assertEquals("SFP-99", config.getSfp("CWE-190"));
    assertEquals("SFP-99", config.getSfp("CWE-194"));
    assertEquals("SFP-99", config.getSfp("CWE-195"));
    assertEquals("SFP-1", config.getSfp("CWE-197"));
  }

  /** Check for extra column data
   * 
   * @param config
   */
  private void assertExtraColumns(AdaptorConfiguration config) {
    String[] names = config.getExtraColumnNames();
    assertNotNull(names);
    assertEquals(1, names.length);
    assertEquals("RowNum", names[0]);
    int index = config.getColumnIndex("RowNum");
    assertEquals(10, index);
    assertEquals("1", config.getCell("CWE-190", index));
    assertEquals("156", config.getCell("CWE-785", index));
  }
  
  /** Check for new row data
   * 
   * @param config
   */
  private void assertNewRows(AdaptorConfiguration config) {
    String cwe = "CWE-999999";
    assertTrue(config.hasCwe(cwe));
    int trust = config.getTrust(cwe, "RATS");
    assertEquals(1, trust);
  }
}
