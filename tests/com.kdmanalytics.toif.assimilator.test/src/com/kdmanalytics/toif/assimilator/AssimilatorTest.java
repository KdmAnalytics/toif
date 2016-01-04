/**
 * 
 */

package com.kdmanalytics.toif.assimilator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import com.kdmanalytics.kdm.repositoryMerger.RepositoryMerger;
import com.kdmanalytics.toif.assimilator.exceptions.AssimilatorArgumentException;
import com.kdmanalytics.toif.common.exception.ToifException;
import com.kdmanalytics.toif.mergers.ToifMerger;

/**
 * @author adam
 *         
 */
public class AssimilatorTest {
  
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();
  
  private Assimilator assimilator;
  
  private File outputDir;
  
  /**
   * make the assimilator before every test.
   */
  @Before
  public void before() throws Exception {
    assimilator = Mockito.spy(new Assimilator());
    outputDir = folder.newFolder("output");
  }
  
  /**
   * make the assimilator before every test.
   */
  @After
  public void after() throws Exception {
    // Thread.sleep(1000);
  }
  
  /**
   * Test method for
   * {@link com.kdmanalytics.toif.assimilator.Assimilator#assimilate(java.lang.String[])} .
   * 
   * @throws AssimilatorArgumentException
   */
  @Test
  public void testAssimilate() throws AssimilatorArgumentException {
    String[] args = {
                      "-r", outputDir.toString(), "resources/function_pointer.tkdm", "resources/segment.c.toif.xml"
    };
    try {
      assimilator.assimilate(args);
    } catch (ToifException | IOException e) {
      fail(e.getMessage());
    }
    
  }
  
  /**
   * Tests that an exception is thrown when there is no repository option provided.
   * 
   * Test method for
   * {@link com.kdmanalytics.toif.assimilator.Assimilator#getOutputLocation(java.lang.String[])} .
   * 
   * @throws AssimilatorArgumentException
   */
  @Test(expected = AssimilatorArgumentException.class)
  public void testGetRepositoryLocationNoRepositoryOption() throws AssimilatorArgumentException {
    String[] args = {
                      outputDir.toString(), "resources/function_pointer.tkdm", "resources/segment.c.toif.xml"
    };
    assimilator.getOutputLocation(args);
  }
  
  /**
   * Tests that an exception is thrown when there is no repository location provided.
   * 
   * Test method for
   * {@link com.kdmanalytics.toif.assimilator.Assimilator#getOutputLocation(java.lang.String[])} .
   * 
   * @throws AssimilatorArgumentException
   */
  @Test(expected = AssimilatorArgumentException.class)
  public void testGetRepositoryLocationNoRepositoryLocation() throws AssimilatorArgumentException {
    String[] args = {
                      "-r", "resources/function_pointer.tkdm", "resources/segment.c.toif.xml"
    };
    assimilator.getOutputLocation(args);
  }
  
  /**
   * Test method for
   * {@link com.kdmanalytics.toif.assimilator.Assimilator#createRepository(java.io.File)} .
   */
  @Test
  public void testCreateRepository() throws Exception {
    File out = folder.newFolder("testRepo");
    Repository repository = assimilator.createRepository(out);
    assertNotNull("The repository returned should not be null", repository);
    assertFalse("The output directory's contents should not be empty.", out.list().length == 0);
  }
  
  /**
   * Tests the ability to extract the correct tkdm files from the argument list.
   * 
   * Test method for
   * {@link com.kdmanalytics.toif.assimilator.Assimilator#getFiles(java.lang.String[], java.lang.String)}
   * .
   */
  @Test
  public void testGetFilesTkdm() {
    String[] args = {
                      "-r", outputDir.toString(), "resources/function_pointer.tkdm", "resources/segment.c.toif.xml",
                      "resources/defs.tkdm", "resources/GENERAL_INFORMATION.toif.xml"
    };
    
    List<File> files;
    try {
      files = assimilator.getFiles(args, ".tkdm");
      assertTrue(files.contains(new File("resources/function_pointer.tkdm")));
      assertTrue(files.contains(new File("resources/defs.tkdm")));
    } catch (ToifException e) {
      fail("Unexpected TOIF Exception: " + e.getMessage());
    }
    
  }
  
  /**
   * Tests the ability to extract the correct toif files from the arguments.
   * 
   * Test method for
   * {@link com.kdmanalytics.toif.assimilator.Assimilator#getFiles(java.lang.String[], java.lang.String)}
   * .
   */
  @Test
  public void testGetFilesToif() {
    String[] args = {
                      "-r", outputDir.toString(), "resources/function_pointer.tkdm", "resources/segment.c.toif.xml",
                      "resources/defs.tkdm", "resources/GENERAL_INFORMATION.toif.xml"
    };
    
    List<File> files;
    try {
      files = assimilator.getFiles(args, ".toif.xml");
      assertTrue(files.contains(new File("resources/segment.c.toif.xml")));
      assertTrue(files.contains(new File("resources/GENERAL_INFORMATION.toif.xml")));
    } catch (ToifException e) {
      fail("Unexpected TOIF Exception: " + e.getMessage());
    }
    
  }
  
  /**
   * Test method for {@link com.kdmanalytics.toif.assimilator.Assimilator#getTkdmMerger()}.
   */
  @Test
  public void testGetTkdmMerger() {
    RepositoryMerger merger = assimilator.getTkdmMerger(new PrintWriter(System.out), null);
    assertNotNull("The returned merger should not be null", merger);
  }
  
  /**
   * Test method for {@link com.kdmanalytics.toif.assimilator.Assimilator#getToifMerger()}.
   */
  @Test
  public void testGetToifMerger() {
    ToifMerger merger = assimilator.getToifMerger(new PrintWriter(System.out), 0L, Long.MAX_VALUE, null);
    assertNotNull("The returned merger should not be null", merger);
  }
  
  /**
   * Test method for
   * {@link com.kdmanalytics.toif.assimilator.Assimilator#getValidRepositoryLocation(java.lang.String)}
   * .
   * 
   * @throws AssimilatorArgumentException
   */
  @Test
  public void testGetValidRepositoryLocation() throws AssimilatorArgumentException {
    File directory = assimilator.getValidRepositoryLocation(outputDir.toString());
    
    assertNotNull("The repository's directory should not be null", directory);
    assertEquals(outputDir, directory);
  }
  
  /**
   * Test method for
   * {@link com.kdmanalytics.toif.assimilator.Assimilator#getValidRepositoryLocation(java.lang.String)}
   * .
   * 
   * @throws AssimilatorArgumentException
   */
  @Test
  public void testGetValidRepositoryLocationCreatedRepository() throws AssimilatorArgumentException {
    assimilator.createRepository(outputDir);
    File directory = assimilator.getValidRepositoryLocation(outputDir.toString());
    
    assertNotNull("The repository's directory should not be null", directory);
    assertEquals(outputDir, directory);
  }
  
  /**
   * Test that when the input for this method is null, that is throws the correct exception.
   * 
   * Test method for
   * {@link com.kdmanalytics.toif.assimilator.Assimilator#getValidRepositoryLocation(java.lang.String)}
   * .
   * 
   * @throws AssimilatorArgumentException
   */
  @Test(expected = AssimilatorArgumentException.class)
  public void testGetValidRepositoryLocationNullLocation() throws AssimilatorArgumentException {
    assimilator.getValidRepositoryLocation(null);
  }
  
  /**
   * Test that an exception is throws when a bad location is given. Test method for
   * {@link com.kdmanalytics.toif.assimilator.Assimilator#getValidRepositoryLocation(java.lang.String)}
   * .
   * 
   * @throws AssimilatorArgumentException
   */
  @Test(expected = AssimilatorArgumentException.class)
  public void testGetValidRepositoryLocationBadLocation() throws AssimilatorArgumentException {
    assimilator.getValidRepositoryLocation("resources/defs.tkdm");
  }
  
  /**
   * Test method for
   * {@link com.kdmanalytics.toif.assimilator.Assimilator#load(org.openrdf.repository.Repository, java.io.InputStream)}
   * .
   * 
   * @throws RepositoryException
   * @throws IOException
   */
  @Test
  public void testLoad() throws RepositoryException, IOException {
    Repository repository = new SailRepository(new MemoryStore());
    repository.initialize();
    
    File file = new File("resources/defs.tkdm");
    FileInputStream is = new FileInputStream(file);
    
    assimilator.load(repository, is);
    
    RepositoryConnection con = repository.getConnection();
    
    RepositoryResult<Statement> statements = con.getStatements(null, null, null, true);
    
    while (statements.hasNext()) {
      Statement st = statements.next();
      
      assertTrue("The Subject should always have the kdmanalytics namespace", st.getSubject().toString().startsWith(
                                                                                                                    "http://kdmanalytics.com"));
      assertTrue("The Predicate should always have the org.omg.kdm namespace", st.getPredicate().toString().startsWith(
                                                                                                                       "http://org.omg.kdm"));
      assertTrue("The Object should either be a literal or have the kdmanalytics namespace.", st.getObject().toString()
                                                                                                .startsWith("\"") || st
                                                                                                                       .getObject()
                                                                                                                       .toString()
                                                                                                                       .startsWith("http://kdmanalytics.com"));
    }
    
  }
  
  /**
   * Test method for
   * {@link com.kdmanalytics.toif.assimilator.Assimilator#mergeTkdm(com.kdmanalytics.kdm.repositorymerger.RepositoryMerger, java.util.List)}
   * .
   * 
   * @throws IOException
   * @throws RepositoryException
   */
  @Test
  public void testMergeTkdm() throws RepositoryException, IOException {
    RepositoryMerger kdmMerger = Mockito.mock(RepositoryMerger.class);
    List<File> tkdmFiles = new ArrayList<File>();
    tkdmFiles.add(new File("resources/defs.tkdm"));
    tkdmFiles.add(new File("resources/function_pointer.tkdm"));
    assimilator.mergeTkdm(kdmMerger, tkdmFiles);
    
    Mockito.verify(assimilator, Mockito.times(2)).load(Mockito.any(Repository.class), Mockito.any(
                                                                                                  FileInputStream.class));
    Mockito.verify(kdmMerger, Mockito.times(2)).merge(Mockito.anyString(), Mockito.any(Repository.class));
  }
}
