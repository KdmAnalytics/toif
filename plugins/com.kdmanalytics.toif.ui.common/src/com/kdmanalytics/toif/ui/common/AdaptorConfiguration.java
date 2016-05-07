/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * https://examples.javacodegeeks.com/core-java/apache/commons/csv-commons/writeread-csv-files-with-
 * apache-commons-csv-example/
 * 
 * @author Ken Duck
 *
 */
public class AdaptorConfiguration {
  
  
  private static final String FILENAME = "AllAdaptorConfiguration.csv";
  
  // Delimiter used in CSV file
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
   * The column numbers might very well change. They are determined by the header location.
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
  private List<List<?>> data = new LinkedList<List<?>>();
  
  /**
   * Map of cwe to data position
   */
  private Map<String, Integer> rowMap = new HashMap<String, Integer>();
  
  /**
   * Map of cwe to visibility
   */
  private Map<String, Boolean> visibilityMap = new HashMap<String, Boolean>();
  
  /**
   * Location of the "local" config file copy. This is the working copy.
   */
  private File configFile;
  
  private boolean dirty;
  
  /**
   * Allow for code to listen for configuration changes
   */
  private Set<IAdaptorConfigurationListener> listeners = new HashSet<IAdaptorConfigurationListener>();
  
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
  
  /** Add a new listener
   * 
   * @param listener
   */
  public void addConfigurationListsner(IAdaptorConfigurationListener listener) {
    listeners.add(listener);
  }
  
  /** Remove a listener
   * 
   * @param listener
   */
  public void removeConfigurationListsner(IAdaptorConfigurationListener listener) {
    listeners.remove(listener);
  }
  
  /**
   * Load the configuration file. This should be called on the very first creation of the instance.
   * This is done in the Activator.
   */
  public void init(IPath stateLocation) {
    File location = stateLocation.toFile();
    System.err.println("STATE LOCATION: " + location);
    configFile = new File(location, FILENAME);
    
    // Load from the state location first.
    loadLocalConfig();
    
    // Now update with the distribution data. This will not replace anything.
    loadDefaults();
    
    if (dirty) {
      save();
    }
  }
  
  /**
   * Load configuration information from the local file
   */
  private void loadLocalConfig() {
    try {
      InputStream is = null;
      try {
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
  }
  
  /**
   * Load the default definitions into the current set.
   * 
   * @return True if the default definitions changed the config set.
   */
  private void loadDefaults() {
    try {
      InputStream is = null;
      try {
        is = getClass().getResourceAsStream("/resources/" + FILENAME);
        load(is);
      } finally {
        if (is != null) is.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Load configuration data from the specified stream.
   * 
   * @param is
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  private synchronized void load(InputStream is) throws IOException {
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
        
        @SuppressWarnings("rawtypes")
        List row = null;
        if (header) {
          if (headers == null) {
            headers = new LinkedList<String>();
            row = headers;
          } else {
            // This will be thrown out. Duplicate header.
            // FIXME: This is actually a NEW header which should be used
            // to figure out the proper column ordering in case it changed
            // between releases.
            row = new LinkedList<String>();
          }
        } else {
          row = new LinkedList<Object>();
        }
        
        // Import the cells
        for (int i = 0; i < size; i++) {
          String text = record.get(i);
          row.add(getCell(header, i, text));
          
          if (header) {
            if (COLUMN_SFP_STRING.equals(text)) COLUMN_SFP = i;
            if (COLUMN_CWE_STRING.equals(text)) COLUMN_CWE = i;
            if (COLUMN_SHOW_STRING.equals(text)) COLUMN_SHOW = i;
            if (COLUMN_CPPCHECK_STRING.equals(text)) COLUMN_CPPCHECK = i;
            if (COLUMN_RATS_STRING.equals(text)) COLUMN_RATS = i;
            if (COLUMN_SPLINT_STRING.equals(text)) COLUMN_SPLINT = i;
            if (COLUMN_JLINT_STRING.equals(text)) COLUMN_JLINT = i;
            if (COLUMN_FINDBUGS_STRING.equals(text)) COLUMN_FINDBUGS = i;
            if (COLUMN_COUNT_C_STRING.equals(text)) COLUMN_COUNT_C = i;
            if (COLUMN_COUNT_JAVA_STRING.equals(text)) COLUMN_COUNT_JAVA = i;
          }
        }
        
        if (header) {
          header = false;
        } else {
          if (row.size() > COLUMN_CWE) {
            String cwe = (String) row.get(COLUMN_CWE);
            // Only add a new row if this is a non-empty row and the CWE
            // does not exist in the map yet.
            if (!cwe.isEmpty() && !rowMap.containsKey(cwe)) {
              data.add(row);
              rowMap.put((String) row.get(COLUMN_CWE), rcount);
              ShowField showState = (ShowField) row.get(COLUMN_SHOW);
              visibilityMap.put((String) row.get(COLUMN_CWE), showState.toBoolean());
              // We just added a new row
              rcount++;
              dirty = true;
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
  }
  
  /**
   * Convert the input text into an appropriate representative object for the cell.
   * 
   * @param header
   * @param index
   * @param text
   * @return
   */
  private Object getCell(boolean header, int index, String text) {
    if (!header) {
      if (index == COLUMN_SHOW) {
        return ShowField.fromString(text);
      }
      if (index == COLUMN_CPPCHECK) {
        return TrustField.fromString(text);
      }
      if (index == COLUMN_RATS) {
        return TrustField.fromString(text);
      }
      if (index == COLUMN_SPLINT) {
        return TrustField.fromString(text);
      }
      if (index == COLUMN_JLINT) {
        return TrustField.fromString(text);
      }
      if (index == COLUMN_FINDBUGS) {
        return TrustField.fromString(text);
      }
   }
    return text;
  }
  
  /**
   * Save the data to the specified output stream
   * 
   * @param os
   * @throws IOException
   */
  public synchronized void save() {
    if (dirty) {
      try {
        OutputStream os = null;
        CSVPrinter printer = null;
        try {
          os = new FileOutputStream(configFile);
          // Create the CSVFormat object with "\n" as a record delimiter
          CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
          OutputStreamWriter out = new OutputStreamWriter(os);
          printer = new CSVPrinter(out, csvFileFormat);
          printer.printRecord(headers);
          for (List<?> row : data) {
            printer.printRecord(row);
          }
        } finally {
          if (printer != null) printer.close();
          if (os != null) os.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      
      // Tell all the listeners about changes
      for(IAdaptorConfigurationListener listener: listeners) {
        listener.configChanged();
      }
      dirty = false;
    }
  }
  
  /**
   * Get the weight used for sorting. 0 is at the top.
   * 
   * @param cwe
   * @return
   */
  public int getWeight(String cwe) {
    if (rowMap.containsKey(cwe)) return rowMap.get(cwe);
    return rowMap.size();
  }
  
  /**
   * Return true if the CWE is visible.
   * 
   * @param cwe
   * @return
   */
  public boolean getVisibility(String cwe) {
    if (visibilityMap.containsKey(cwe)) {
      return visibilityMap.get(cwe);
    }
    return true;
  }
  
  /**
   * Reload the local configuration.
   */
  public void reset() {
    data.clear();
    rowMap.clear();
    visibilityMap.clear();
    headers = null;
    // Load from the state location first.
    loadLocalConfig();
    
    // Now update with the distribution data. This will not replace anything.
    loadDefaults();
  }
  
  /**
   * Completely reload the configuration data from the system default set. This will not overwrite
   * the local copy unless save is called.
   */
  public void resetToDefault() {
    data.clear();
    rowMap.clear();
    visibilityMap.clear();
    headers = null;
    loadDefaults();
  }
  
  /**
   * Get the headers.
   * 
   * @return
   */
  public List<String> getHeaders() {
    return headers;
  }
  
  /**
   * Get the data as an array.
   * 
   * @return
   */
  public Object[] getDataArray() {
    return data.toArray();
  }
  
  /**
   * Get the index for the SFP column
   * 
   * @return
   */
  public int getSfpColumnIndex() {
    return COLUMN_SFP;
  }
  
  /**
   * Get the index for the CWE column
   * 
   * @return
   */
  public int getCweColumnIndex() {
    return COLUMN_CWE;
  }
  
  public int getShowColumnIndex() {
    return COLUMN_SHOW;
  }
  
  /**
   * Replace a row's data
   * 
   * @param row
   */
  public void update(List<Object> row) {
    String cwe = (String) row.get(COLUMN_CWE);
    int rowNum = rowMap.get(cwe);
    data.remove(rowNum);
    data.add(rowNum, row);
    
    ShowField state = (ShowField) row.get(COLUMN_SHOW);
    visibilityMap.put(cwe, state.toBoolean());
    
    dirty = true;
  }
  
  /** Return true if the given index is one of the adaptors
   * 
   * @param index
   * @return
   */
  public boolean isAdaptorIndex(int index) {
    if(COLUMN_CPPCHECK == index) return true;
    if(COLUMN_RATS == index) return true;
    if(COLUMN_SPLINT == index) return true;
    if(COLUMN_JLINT == index) return true;
    if(COLUMN_FINDBUGS == index) return true;
    return false;
  }
  
  /** Get the size of the configuration
   * 
   * @return
   */
  public int getSize() {
    return data.size();
  }
  
  /** Get the location of the specified CWE
   * 
   * @param cwe
   * @return
   */
  public int getIndex(String cwe) {
    if(rowMap.containsKey(cwe)) {
      return rowMap.get(cwe);
    }
    return data.size();
  }

  /** Remove the row
   * 
   * @param row
   * @return The index of the removed row
   */
  public int remove(List<?> row) {
    String cwe = (String) row.get(COLUMN_CWE);
    int rowNum = rowMap.get(cwe);
    data.remove(rowNum);
    rowMap.remove(cwe);
    visibilityMap.remove(cwe);
    dirty = true;
    return rowNum;
  }

  /** Add a row into the data set at the specified location
   * 
   * @param index
   * @param row
   */
  public void add(int index, List<?> newRow) {
    data.add(index, newRow);
    
    rowMap.clear();
    int count = 0;
    for(List<?> row: data) {
      String cwe = (String) row.get(COLUMN_CWE);
      rowMap.put(cwe, count++);
    }
  }
}
