
package com.kdmanalytics.toif.adaptor;

import static org.junit.Assert.*;

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
import com.kdmanalytics.toif.common.exception.ToifException;

@RunWith(value = Parameterized.class)
public class DeriveIdTests {
  
  private static final String EXPECTED_WEAKNESS_FILE = "/resource/expectedWeaknesses";
  
  private String expected;
  
  private String msg;
  
  private JlintAdaptor jla;
  
  // parameters pass via this constructor
  public DeriveIdTests(String expected, String msg) {
    this.expected = expected;
    this.msg = msg;
    
  }
  
  @Before
  public void setup() {
    jla = new JlintAdaptor();
  }
  
  // Declares parameters here
  @Parameters(name = "deriveIdTest    ID:{0}  :   MSG:{1}   ")
  public static Iterable<Object[]> generateParameters() throws IOException {
    
    ArrayList<Object[]> res = new ArrayList<Object[]>();
    
    InputStream is = DeriveIdTests.class.getResourceAsStream(EXPECTED_WEAKNESS_FILE);
    
    Reader r = new InputStreamReader(is);
    BufferedReader in = new BufferedReader(r);
    
    String line;
    
    while ((line = in.readLine()) != null) {
      String[] split = line.split("\t");
      
      if (split.length == 3) {
        
        Object a[] = new Object[] {
                                    split[1], split[2]
        };
        
        res.add(a);
        
      }
    }
    
    return res;
  }
  
  @Test
  public void test_deriveId() {
    try {
      String id = jla.deriveId(msg);
      
      assertEquals("Expected:" + expected + " got:" + id, expected, id);
      
    } catch (ToifException e) {
      fail("EXCEPTION when expected:" + expected + " EXCEPTION::" + e);
    }
  }
  
}
