
package com.kdmanalytics.toif.assimilator;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.kdmanalytics.toif.common.exception.ToifException;

public class CompareFileTest {
  
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();
  
  private Assimilator assimilator;
  
  @Before
  public void before() {
    assimilator = Mockito.spy(new Assimilator());
  }
  
  @Test
  public void compareFiles() throws IOException {
    File outputFile = folder.newFile("output.kdm");
    String[] args = {
                      "-k", outputFile.toString(), "resources/javaOutput.kdm",
                      "resources/TOIF/FindBugs Adaptor/NamesConversion.class.toif.xml",
                      "resources/TOIF/JLint Adaptor/NamesConversion.class.toif.xml"
    };
    try {
      assimilator.assimilate(args);
    } catch (ToifException e) {
      fail(e.getMessage());
    }
    
    File expected = new File("resources/expected-output.kdm");
    File actual = new File(outputFile.toString());
    
    // actual = replace(actual);
    
    boolean same = isSame(expected, actual);
    
    if (!same) {
      BufferedReader br = new BufferedReader(new FileReader(actual));
      String line;
      while ((line = br.readLine()) != null) {
        System.err.println(line);
      }
      br.close();
    }
    assertTrue(same);
    
  }
  
  private boolean isSame(File expected, File actual) throws IOException {
    try (BufferedReader exReader = new BufferedReader(new FileReader(expected));
         BufferedReader actReader = new BufferedReader(new FileReader(actual));) {
         
      String exLine;
      String actLine;
      int lineNumber = 0;
      // Read File Line By Line
      while ((exLine = exReader.readLine()) != null) {
        actLine = actReader.readLine();
        lineNumber++;
        
        if (!actLine.equals(exLine)) {
          System.err.println("Line: " + lineNumber + " is different.");
          System.err.println(exLine);
          System.err.println(actLine);
          return false;
        }
      }
      return true;
    }
  }
  
  // private File replace(File actual) throws IOException {
  // File newFile = folder.newFile("modified.kdm");
  //
  // try (FileInputStream fstream = new FileInputStream(actual);
  // BufferedWriter bw = new BufferedWriter(new FileWriter(newFile, true));
  // BufferedReader br = new BufferedReader(new InputStreamReader(new
  // DataInputStream(fstream)));) {
  // String strLine;
  // // Read File Line By Line
  // while ((strLine = br.readLine()) != null) {
  // strLine = strLine.replaceAll("_:[A-Za-z-0-9]*", "ANONYMOUS");
  // strLine = strLine.replaceAll("/[0-9]*>", "/ID>");
  // bw.write(strLine);
  // bw.newLine();
  //
  // }
  // } catch (Exception e) {// Catch exception if any
  // System.err.println("Error: " + e.getMessage());
  // }
  // return newFile;
  //
  // }
  
  public int count(String filename) throws IOException {
    try (InputStream is = new BufferedInputStream(new FileInputStream(filename));) {
      byte[] c = new byte[1024];
      int count = 0;
      int readChars = 0;
      while ((readChars = is.read(c)) != -1) {
        for (int i = 0; i < readChars; ++i) {
          if (c[i] == '\n') ++count;
        }
      }
      return count;
    }
  }
  
}
