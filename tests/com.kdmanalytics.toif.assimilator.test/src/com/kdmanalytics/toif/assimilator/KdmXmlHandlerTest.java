
package com.kdmanalytics.toif.assimilator;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.xml.sax.SAXException;

public class KdmXmlHandlerTest {
  
  private Repository repository;
  
  private RepositoryConnection connection;
  
  private SAXParser saxParser;
  
  private KdmXmlHandler xmlHandler;
  private KdmXmiIdHandler idHandler;
  
  private PrintWriter printWriter;
  
  private File outputFile;
  
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();
  
  @Before
  public void setUp() throws Exception {
    repository = new SailRepository(new MemoryStore());
    repository.initialize();
    connection = repository.getConnection();
    if (connection == null) {
      fail("Unable to establish connection to repository");
    }
    connection.setAutoCommit(false); // Control commit for speed
    
    SAXParserFactory factory = SAXParserFactory.newInstance();
    saxParser = factory.newSAXParser();
    
    outputFile = folder.newFile("testOutput.kdm");
    printWriter = new PrintWriter( new FileOutputStream(outputFile));
    idHandler = new KdmXmiIdHandler();
  }
  
  @After
  public void tearDown() throws Exception {
    if (connection != null) {
      connection.close();
    }
    if (printWriter != null) {
      printWriter.flush();
      printWriter.close();
    }
  }
  
  /**
   * Basic sanity test, reads a basically empty KDM XML file
   * 
   * @throws Exception
   */
  @Test 
  public void simpleParse() throws Exception {
    File simpleXml = new File("resources/simpleXml.xml");
    saxParser.parse(simpleXml, idHandler);
    xmlHandler = new KdmXmlHandler(printWriter, repository, idHandler.getMaxId());
    saxParser.parse(simpleXml, xmlHandler);

    String fileContents = readOutputFile();
    assertTrue(fileContents.contains("<http://kdmanalytics.com/0> <http://org.omg.kdm/kdmType> \"kdm/Segment\""));
    assertTrue(fileContents.contains("<http://kdmanalytics.com/0> <http://org.omg.kdm/name> \"Java$Application\""));
    assertTrue(fileContents.contains("<http://kdmanalytics.com/0> <http://org.omg.kdm/UID> \"0\""));
//    dumpFileToConsole();
  }

  @Test 
  public void actionElementParse() throws Exception {
    File simpleXml = new File("resources/actionElements.xml");
    saxParser.parse(simpleXml, idHandler);
    xmlHandler = new KdmXmlHandler(printWriter, repository, idHandler.getMaxId());
    saxParser.parse(simpleXml, xmlHandler);
    //dumpFileToConsole();

    String fileContents = readOutputFile();
    
    Pattern pattern = Pattern.compile(Pattern.quote("<http://kdmanalytics.com/3420961> <http://org.omg.kdm/kdmType>"));
    Matcher matcher = pattern.matcher(fileContents);
    assertTrue(matcher.find());
    assertFalse("There should only one match of id 3420961 and kdmType", matcher.find());
  }
  
  @Test 
  public void overlapId() throws Exception {
    File simpleXml = new File("resources/overlapId.xml");
    saxParser.parse(simpleXml, idHandler);
    xmlHandler = new KdmXmlHandler(printWriter, repository, idHandler.getMaxId());
    saxParser.parse(simpleXml, xmlHandler);
    //dumpFileToConsole();

    String fileContents = readOutputFile();
    
    Pattern pattern = Pattern.compile(Pattern.quote("<http://kdmanalytics.com/80> <http://org.omg.kdm/kdmType>"));
    Matcher matcher = pattern.matcher(fileContents);
    assertTrue(matcher.find());
    assertFalse("There should only one match of id 80 and kdmType", matcher.find());
  }
  
  @Test (expected=SAXException.class)
  public void overlapIdInvalidMaxId() throws Exception {
    File simpleXml = new File("resources/overlapId.xml");
    xmlHandler = new KdmXmlHandler(printWriter, repository, 60);
    saxParser.parse(simpleXml, xmlHandler);
   }
  
  /**
   * Flushes the printwriter and dumps the contents of the outputFile to 
   * StandardErr
   * 
   * @throws Exception if anything bad happens
   */
  private String readOutputFile() throws Exception {
    printWriter.flush();
    StringBuilder builder = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(outputFile));) {
      String line = "";
      while ((line = reader.readLine()) != null) {
        builder.append(line).append("\n");
      }
    }
    return builder.toString();
  }
  
  /**
   * Flushes the printwriter and dumps the contents of the outputFile to 
   * StandardErr
   * 
   * @throws Exception if anything bad happens
   */
  void dumpFileToConsole() throws Exception {
    printWriter.flush();
    try (BufferedReader reader = new BufferedReader(new FileReader(outputFile));) {
      String line = "";
      while ((line = reader.readLine()) != null) {
        System.err.println(line);
      }
    }
  }
  
//  
//  @Test
//  public void testStartDocument() {
//    fail("Not yet implemented");
//  }
//  
//  @Test
//  public void testAddOrWrite() {
//    fail("Not yet implemented");
//  }
//  
//  @Test
//  public void testKdmXmlHandlerRepository() {
//    fail("Not yet implemented");
//  }
//  
//  @Test
//  public void testKdmXmlHandlerPrintWriterRepository() {
//    fail("Not yet implemented");
//  }
//  
//  @Test
//  public void testAddChild() {
//    fail("Not yet implemented");
//  }
//  
//  @Test
//  public void testCommitNode() {
//    fail("Not yet implemented");
//  }
//  
//  @Test
//  public void testEndElementStringStringString() {
//    fail("Not yet implemented");
//  }
//  
//  @Test
//  public void testGetNextId() {
//    fail("Not yet implemented");
//  }
//  
//  @Test
//  public void testGetSmallestBigNumber() {
//    fail("Not yet implemented");
//  }
//  
//  @Test
//  public void testPostLoad() {
//    fail("Not yet implemented");
//  }
//  
//  @Test
//  public void testSetRDFAttribute() {
//    fail("Not yet implemented");
//  }
//  
//  @Test
//  public void testStartElementStringStringStringAttributes() {
//    fail("Not yet implemented");
//  }
//  
//  @Test
//  public void testStopDocument() {
//    fail("Not yet implemented");
//  }
  
}
