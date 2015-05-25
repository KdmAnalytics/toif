
package com.kdmanalytics.toif.adaptor;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.kdmanalytics.toif.adaptor.JlintAdaptor;

@RunWith(value = Parameterized.class)
public class JlintDataElementTests {
  
  private static final String EXPECTED_WEAKNESS_FILE = "/resource/expectedWeaknesses";

  private String expected;
  
  private String id;
  
  private String msg;
  
  private JlintAdaptor jla;
  
  // parameters pass via this constructor
  public JlintDataElementTests(String expected, String id, String msg) {
    this.expected = expected;
    this.id = id;
    this.msg = msg;
    
  }
  
  @Before
  public void setup() {
    jla = new JlintAdaptor();
  }
  
  // Declares parameters here
  @Parameters(name = "DataElementTest    ID:{1}  :  ELEMENT:{0}  :  MSG:{2}   ")
  public static Iterable<Object[]> generateParameters() throws IOException {
    
    ArrayList<Object[]> res = new ArrayList<Object[]>();
    
    InputStream is = JlintDataElementTests.class.getResourceAsStream(EXPECTED_WEAKNESS_FILE);
    
    Reader r = new InputStreamReader(is);
    BufferedReader in = new BufferedReader(r);
    
    String line;
    
    while ((line = in.readLine()) != null) {
      String[] split = line.split("\t");
      
      if (split.length == 3) {
        
        Object a[] = new Object[] {
            "null".equals(split[0]) ? null : split[0], split[1], split[2]
        };
        
        res.add(a);
        
      }
    }
    
    return res;
  }
  
  @Test
  public void test_getDataElement() {
    String dataElement = jla.getDataElement(id, msg);
	assertEquals("Not what was expected. Got:"+dataElement+" wanted:"+expected+"    -   MSG: "+msg,expected, dataElement);
  }

  
}
