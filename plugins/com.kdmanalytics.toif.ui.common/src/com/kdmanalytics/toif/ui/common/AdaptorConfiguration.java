
package com.kdmanalytics.toif.ui.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.core.runtime.IPath;

/**
 * Load the configuration adaptor file and make the data available to whomever needs it. Currently
 * this includes various UI components.
 * 
 * CSV code samples can be found here:
 *    https://examples.javacodegeeks.com/core-java/apache/commons/csv-commons/writeread-csv-files-with-apache-commons-csv-example/
 * 
 * @author Ken Duck
 *
 */
public class AdaptorConfiguration {
  
  
  private static final String FILENAME = "AllAdaptorConfiguration.csv";
  //Delimiter used in CSV file
  private static final String NEW_LINE_SEPARATOR = "\n";
  
  /**
   * The strings expected in the header.
   */
  private static String COLUMN_SFP_STRING = "SFP";
  private static String COLUMN_CWE_STRING = "CWE";
  private static String COLUMN_SHOW_STRING = "Show?";
  private static String COLUMN_CPPCHECK_STRING = "Cppcheck";
  private static String COLUMN_RATS_STRING = "RATS";
  private static String COLUMN_SPLINT_STRING = "Splint";
  private static String COLUMN_JLINT_STRING = "Jlint";
  private static String COLUMN_FINDBUGS_STRING = "Findbugs";
  private static String COLUMN_COUNT_C_STRING = "Count C/C++";
  private static String COLUMN_COUNT_JAVA_STRING = "Count Java";
  
  /**
   * The column numbers might very well change. They are determined by the
   * header location.
   */
  private static int COLUMN_SFP = 0;
  private static int COLUMN_CWE = 1;
  private static int COLUMN_SHOW = 2;
  private static int COLUMN_CPPCHECK = 3;
  private static int COLUMN_RATS = 4;
  private static int COLUMN_SPLINT = 5;
  private static int COLUMN_JLINT = 6;
  private static int COLUMN_FINDBUGS = 7;
  private static int COLUMN_COUNT_C = 8;
  private static int COLUMN_COUNT_JAVA = 9;
  
  private static AdaptorConfiguration instance;
  
  /**
   * Configuration header
   */
  private List<String> headers;
  
  /**
   * Table data
   */
  private List<List<String>> data = new LinkedList<List<String>>();
  
  /**
   * Map of cwe to data position
   */
  private Map<String,Integer> rowMap = new HashMap<String,Integer>();
  
  /**
   * Map of cwe to visibility
   */
  private Map<String,Boolean> visibilityMap = new HashMap<String,Boolean>();

  /**
   * Location of the "local" config file copy. This is the working copy.
   */
  private File configFile;
  
  private AdaptorConfiguration() {
  }
  
  /**
   * Get the adaptor configuration singleton
   * 
   * @return
   */
  public static synchronized AdaptorConfiguration getAdaptorConfiguration() {
    if (instance == null) {
      instance = new AdaptorConfiguration();
    }
    return instance;
  }
  
  /**
   * Load the configuration file. This should be called on the very first creation of the instance.
   * This is done in the Activator.
   */
  public void init(IPath stateLocation) {
    boolean updated = false;
    // Load from the state location first.
    try {
      InputStream is = null;
      try {
        File location = stateLocation.toFile();
        System.err.println("STATE LOCATION: " + location);
        configFile = new File(location, FILENAME);
        if (configFile.exists()) {
          is = new FileInputStream(configFile);
          load(is);
        }
      } finally {
        if (is != null) is.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    // Now update with the distribution data. This will not replace anything.
    try {
      InputStream is = null;
      try {
        is = getClass().getResourceAsStream("/resources/" + FILENAME);
        updated = load(is);
      } finally {
        if (is != null) is.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    if (updated) {
      save();
    }
  }
  
  /**
   * Load configuration data from the specified stream.
   * 
   * @param is
   * @throws IOException
   */
  private synchronized boolean load(InputStream is) throws IOException {
    boolean changed = false;
    InputStreamReader in = null;
    CSVParser parser = null;
    try {
      in = new InputStreamReader(is);
      CSVFormat format = CSVFormat.EXCEL.withDelimiter(',').withIgnoreEmptyLines();
      
      parser = new CSVParser(in, format);
      
      // Set to false once the header is read
      boolean header = true;
      // Number of rows we have loaded so far
      int rcount = data.size();
      // Import all new rows
      for (CSVRecord record : parser) {
        int size = record.size();
        
        List<String> row = null;
        if (header) {
          if(headers == null) {
            headers = new LinkedList<String>();
            row = headers;
          }
          else {
            // This will be thrown out. Duplicate header.
            // FIXME: This is actually a NEW header which should be used
            // to figure out the proper column ordering in case it changed
            // between releases.
            row = new LinkedList<String>();
          }
        } else {
          row = new LinkedList<String>();
        }
        
        // Import the cells
        for (int i = 0; i < size; i++) {
          String text = record.get(i);
          row.add(text);
          
          if(header) {
            if(COLUMN_SFP_STRING.equals(text)) COLUMN_SFP = i;
            if(COLUMN_CWE_STRING.equals(text)) COLUMN_CWE = i;
            if(COLUMN_SHOW_STRING.equals(text)) COLUMN_SHOW = i;
            if(COLUMN_CPPCHECK_STRING.equals(text)) COLUMN_CPPCHECK = i;
            if(COLUMN_RATS_STRING.equals(text)) COLUMN_RATS = i;
            if(COLUMN_SPLINT_STRING.equals(text)) COLUMN_SPLINT = i;
            if(COLUMN_JLINT_STRING.equals(text)) COLUMN_JLINT = i;
            if(COLUMN_FINDBUGS_STRING.equals(text)) COLUMN_FINDBUGS = i;
            if(COLUMN_COUNT_C_STRING.equals(text)) COLUMN_COUNT_C = i;
            if(COLUMN_COUNT_JAVA_STRING.equals(text)) COLUMN_COUNT_JAVA = i;
          }
        }
        
        if (header) {
          header = false;
        } else {
          if (!row.isEmpty()) {
            String cwe = row.get(COLUMN_CWE);
            // Only add a new row if this is a non-empty row and the CWE
            // does not exist in the map yet.
            if (!cwe.isEmpty() && !rowMap.containsKey(cwe)) {
              data.add(row);
              rowMap.put(row.get(COLUMN_CWE), rcount);
              visibilityMap.put(row.get(COLUMN_CWE), !"No".equals(row.get(COLUMN_SHOW)));
              // We just added a new row
              rcount++;
              changed = true;
            }
          }
        }
      }
    } finally {
      if (in != null) {
        in.close();
      }
      if (parser != null) {
        parser.close();
      }
    }
    return changed;
  }
  
  /**
   * Save the data to the specified output stream
   * 
   * @param os
   * @throws IOException 
   */
  public synchronized void save() {
    try {
      OutputStream os = null;
      CSVPrinter printer = null;
      try {
        os = new FileOutputStream(configFile);
        //Create the CSVFormat object with "\n" as a record delimiter
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
        OutputStreamWriter out = new OutputStreamWriter(os);
        printer = new CSVPrinter(out, csvFileFormat);
        printer.printRecord(headers);
        for (List<String> row : data) {
          printer.printRecord(row);
        }
      } finally {
        if(printer != null) printer.close();
        if (os != null) os.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /** Get the weight used for sorting. 0 is at the top.
   * 
   * @param cwe
   * @return
   */
  public int getWeight(String cwe)
  {
    if(rowMap.containsKey(cwe)) return rowMap.get(cwe);
    return rowMap.size();
  }
  
  /** Return true if the CWE is visible.
   * 
   * @param cwe
   * @return
   */
  public boolean getVisibility(String cwe)
  {
    if(visibilityMap.containsKey(cwe)) {
      return visibilityMap.get(cwe);
    }
    return true;
  }
}
