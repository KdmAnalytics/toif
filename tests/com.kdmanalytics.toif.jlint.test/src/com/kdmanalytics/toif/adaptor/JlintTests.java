
package com.kdmanalytics.toif.adaptor;

import java.io.File;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.kdmanalytics.toif.adaptor.JlintAdaptor;
import com.kdmanalytics.toif.framework.toolAdaptor.AdaptorOptions;
import com.kdmanalytics.toif.framework.toolAdaptor.Language;

public class JlintTests extends TestCase {
  
  private static Logger LOG = Logger.getLogger(JlintTests.class);
  
  /**
   * The findbugs adaptor to test.
   */
  private JlintAdaptor jla;
  
  @Before
  public void setUp() {
    LOG.info("Before Class");
    jla = new JlintAdaptor();
  }
  
  @Test
  public void testGetLanguage() {
    Language l = jla.getLanguage();
    assertEquals(Language.JAVA, jla.getLanguage());
  }
  
  @Test
  public void testGetRuntoolName() {
    assertEquals("jlint", jla.getRuntoolName());
  }
  
  @Test
  public void testGetAdaptorDescription() {
    assertEquals("Jlint will check your Java code and find bugs, inconsistencies and synchronization problems by doing data flow analysis and building the lock graph.",
                 jla.getAdaptorDescription());
  }
  
  @Test
  public void testGetAdaptorName() {
    assertEquals("JLint", jla.getAdaptorName());
  }
  
  @Test
  public void testGetAdaptorVendorAddress() {
    assertEquals("1956 Robertson Road, Suite 204, Ottawa, ON, K2H 5B9", jla.getAdaptorVendorAddress());
    
  }
  
  @Test
  public void testGetAdaptorVendorDescription() {
    assertEquals("KDM Analytics is a security assurance company providing products and services for threat risk assessment and management, due diligence assessments, and information and data assurance. Leveraging our decades of experience in static analysis, reverse engineering and formal methods, we have created breakthrough products for the automated and systematic investigation of code, data and networks.",
                 jla.getAdaptorVendorDescription());
  }
  
  @Test
  public void testGetAdaptorVendorEmail() {
    assertEquals("info@kdmanalytics.com", jla.getAdaptorVendorEmail());
  }
  
  @Test
  public void testGetAdaptorVendorName() {
    assertEquals("KDM Analytics", jla.getAdaptorVendorName());
  }
  
  @Test
  public void testGetAdaptorVendorPhone() {
    assertEquals("1-613-627-1010", jla.getAdaptorVendorPhone());
  }
  
  @Test
  public void testGetGeneratorDescription() {
    assertEquals("Jlint will check your Java code and find bugs, inconsistencies and synchronization problems by doing data flow analysis and building the lock graph.",
                 jla.getGeneratorDescription());
  }
  
  @Test
  public void testGetGeneratorName() {
    assertEquals("jlint", jla.getGeneratorName());
  }
  
  @Test
  public void testGetGeneratorVendorAddress() {
    assertEquals("http://jlint.sourceforge.net/", jla.getGeneratorVendorAddress());
  }
  
  @Test
  public void testGetGeneratorVendorDescription() {
    assertEquals("We develop tools for web pages with dynamic content of medium size",
                 jla.getGeneratorVendorDescription());
  }
  
  @Test
  public void testGetGeneratorVendorEmail() {
    assertEquals("c.artho@aist.go.jp", jla.getGeneratorVendorEmail());
  }
  
  @Test
  public void testGetGeneratorVendorName() {
    assertEquals("Cyrille Artho", jla.getGeneratorVendorName());
  }
  
  @Test
  public void testGetGeneratorVendorPhone() {
    assertEquals("+81 (0)6 6494 7813", jla.getGeneratorVendorPhone());
  }
  
  @Test
  public void testGetGeneratorVersion() {
    assertEquals("Assumed 3.0.0", jla.getGeneratorVersion());
  }
  
  @Test
  public void testRunToolCommands() {
    AdaptorOptions mockoptions = Mockito.mock(AdaptorOptions.class);
    
    Mockito.when(mockoptions.getInputFile()).thenReturn(new File("Test.class"));
    
    //trying to break it. jlint doesnt actually use these options.
    String additional[] = new String[] {
        "-Dblah", "-Ifoo"
    };
    
    String[] runToolCommands = jla.runToolCommands(mockoptions, additional);
    
    String expected[] = new String[] {
        "jlint", "Test.class"
    };
    
    Assert.assertArrayEquals(expected, runToolCommands);
  }
  
  @Test
  public void testAcceptsDOptions() {
    assertEquals(false, jla.acceptsDOptions());
  }
  
  @Test
  public void testAcceptsIOptions() {
    assertEquals(false, jla.acceptsIOptions());
  }
  
  @Test
  public void testGetDataElement() {
    assertEquals("'???'", jla.getDataElement("null_var", "Value of referenced variable '???' may be NULL."));
  }
  
}
