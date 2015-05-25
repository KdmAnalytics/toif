package com.kdmanalytics.toif.assimilator;

import static org.junit.Assert.*;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Before;
import org.junit.Test;


public class KdmXmiIdHandlerTest {
  
  private KdmXmiIdHandler handler;
  
  private SAXParser saxParser;
 
  @Before
  public void setUp() throws Exception {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    saxParser = factory.newSAXParser();
    handler = new KdmXmiIdHandler();
  }
  
  @Test
  public void simpleMaxIdTest()  throws Exception {
    File simpleXml = new File("resources/simpleXml.xml");
    saxParser.parse(simpleXml, handler);
    assertEquals(0L,handler.getMaxId());
  }
  
  @Test
  public void actionElementsMaxIdTest()  throws Exception {
    File simpleXml = new File("resources/actionElements.xml");
    saxParser.parse(simpleXml, handler);
    assertEquals(3420961L,handler.getMaxId());
  }
  
  @Test
  public void overlapMaxIdTest()  throws Exception {
    File simpleXml = new File("resources/overlapId.xml");
    saxParser.parse(simpleXml, handler);
    assertEquals(80L,handler.getMaxId());
  }
  
}
