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
import java.util.Arrays;
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
  
  
  private static final String FILENAME = "TOIF_Findings_Configuration.csv";
  
  /**
   * Placeholders for other sample config names used for testing
   */
  @SuppressWarnings("unused")
  private static final String EXTRA_COLUMNS_FILENAME = "AppendedColumn.csv";
  @SuppressWarnings("unused")
  private static final String NEW_ROW_FILENAME = "NewRow.csv";
  @SuppressWarnings("unused")
  private static final String REPALCE_SFP_FILENAME = "ReplaceSfpConfig.csv";
  
  private static String DEFAULT_RESOURCE_NAME = FILENAME;
  
  // Some debug code that is used to load different "default" files and
  // therefore test the configuration upgrade capability in the UI.
  static {
    String configName = System.getenv("TOIF_ADAPTOR_CONFIG");
    if (configName != null) {
      configName = configName.trim();
      if (!configName.isEmpty()) {
        DEFAULT_RESOURCE_NAME = configName;
      }
    }
  }
  
  // Delimiter used in CSV file
  private static final String NEW_LINE_SEPARATOR = "\n";
  
  /**
   * The strings expected in the header.
   */
  private static final String COLUMN_SFP_STRING = "sfp";
  
  private static final String COLUMN_CWE_STRING = "cwe";
  
  private static final String COLUMN_SHOW_STRING = "show";
  private static final String COLUMN_SHOW_STRING_OLD = "show?";
  
  private static final String COLUMN_CPPCHECK_STRING = "cppcheck";
  
  private static final String COLUMN_RATS_STRING = "rats";
  
  private static final String COLUMN_SPLINT_STRING = "splint";
  
  private static final String COLUMN_JLINT_STRING = "jlint";
  
  private static final String COLUMN_FINDBUGS_STRING = "findbugs";
  
  private static final String COLUMN_COUNT_C_STRING1 = "count c/c++";
  private static final String COLUMN_COUNT_JAVA_STRING1 = "count java";
  
  private static final String COLUMN_COUNT_C_STRING2 = "Count of C/C++ tools";  
  private static final String COLUMN_COUNT_JAVA_STRING2 = "Count of Java tools";
  
  /**
   * The column numbers might very well change. They are determined by the header location.
   */
  private int COLUMN_SFP = 0;
  
  private int COLUMN_CWE = 1;
  
  private int COLUMN_SHOW = 2;
  
  private int COLUMN_CPPCHECK = 3;
  
  private int COLUMN_RATS = 4;
  
  private int COLUMN_SPLINT = 5;
  
  private int COLUMN_JLINT = 6;
  
  private int COLUMN_FINDBUGS = 7;
  
//  private int COLUMN_COUNT_C = 8;
//  
//  private int COLUMN_COUNT_JAVA = 9;
  
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
   * Map of the column name to its integer value
   */
  private Map<String, Integer> columnMap = new HashMap<String, Integer>();
  
  /**
   * List of "extra" columns. These will show up in the finding view.
   */
  private List<String> extraColumns = new LinkedList<String>();
  
  /**
   * Map of CWE to SFP
   */
  private Map<String,String> sfpMap = new HashMap<String,String>();
  
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
  
  /**
   * Add a new listener
   * 
   * @param listener
   */
  public void addConfigurationListsner(IAdaptorConfigurationListener listener) {
    listeners.add(listener);
  }
  
  /**
   * Remove a listener
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
      load(configFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Load the default definitions into the current set.
   * 
   * @return True if the default definitions changed the config set.
   */
  public void loadDefaults() {
    try {
      boolean success = loadResource("/resources/" + DEFAULT_RESOURCE_NAME);
      // If this load fails, check if the default resource is pointing at a file
      if (!success) {
        File file = new File(DEFAULT_RESOURCE_NAME);
        if (file.exists()) {
          load(file);
        }
      }
      //      loadResource("/resources/" + EXTRA_COLUMNS_FILENAME);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Load configuration from the given file.
   * 
   * @param file
   * @throws IOException
   */
  public void load(File file) throws IOException {
    InputStream is = null;
    try {
      if (file.exists()) {
        is = new FileInputStream(file);
        load(is);
      }
    } finally {
      if (is != null) is.close();
    }
  }
  
  /**
   * Load a resource (file embedded in the jar)
   * 
   * @param path
   * @throws IOException
   */
  public boolean loadResource(String path) throws IOException {
    InputStream is = null;
    try {
      is = getClass().getResourceAsStream(path);
      if (is != null) {
        load(is);
        return true;
      }
    } finally {
      if (is != null) {
        is.close();
      }
    }
    return false;
  }
  
  /**
   * Load configuration data from the specified stream.
   * 
   * @param is
   * @throws IOException
   */
  private synchronized void load(InputStream is) throws IOException {
    if (!isEmpty()) {
      // If there is already data loaded, we want to merge the new data
      merge(is);
    } else {
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
          if (header) {
            parseHeader(record);
            header = false;
          } else {
            rcount = parseData(record, rcount);
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
  }
  
  /**
   * Merge data from a different configuration stream with the currently loaded data.
   * 
   * @param is
   * @throws IOException
   */
  private void merge(InputStream is) throws IOException {
    // Load the stream into an empty configuration file
    AdaptorConfiguration config = new AdaptorConfiguration();
    config.load(is);
    
    // Are there new columns?
    String[] myNames = getExtraColumnNames();
    String[] yourNames = config.getExtraColumnNames();
    
    Set<String> myNameSet = new HashSet<String>();
    for (String name : myNames) {
      myNameSet.add(name.toLowerCase());
    }
    
    List<String> newNames = new LinkedList<String>();
    for (String name : yourNames) {
      if (!myNameSet.contains(name.toLowerCase())) {
        newNames.add(name);
      }
    }
    
    // Add columns
    for (String name : newNames) {
      addColumn(name);
    }
    
    // Look through all new rows of data. If there are new rows then append them to
    // our data. If there are new columns, add it to the appropriate row.
    List<String> yourHeaders = config.getHeaders();
    int yourCweIndex = config.getCweColumnIndex();
    Object[] yourData = config.getDataArray();
    for (Object object : yourData) {
      List<?> yourRow = (List<?>) object;
      String yourCwe = (String) yourRow.get(yourCweIndex);
      
      // Do we have the row yet? If no, then add it
      if (!hasCwe(yourCwe)) {
        addRow(yourHeaders, yourRow);
      } else {
        // Copy all new cells, but also replace all "extra" cells
        // from the original as well. "Extra" columns/cells can be
        // upgraded by the system data because they are not user
        // editable.
        // 
        // This code is not particularly efficient
        for (String name : getExtraColumnNames()) {
          Integer yourIndex = config.getColumnIndex(name);
          if (yourIndex != null) {
            Object yourCell = config.getCell(yourCwe, yourIndex);
            int myIndex = getColumnIndex(name);
            setCell(yourCwe, myIndex, yourCell);
          }
        }
        {
          // Replace SFP
          int yourIndex = config.getColumnIndex(COLUMN_SFP_STRING);
          Object yourCell = config.getCell(yourCwe, yourIndex);
          int myIndex = getColumnIndex(COLUMN_SFP_STRING);
          setCell(yourCwe, myIndex, yourCell);
          sfpMap.put(yourCwe, (String) yourCell);
        }
        {
          // Replace "Count C/C++"
          int yourIndex = config.getColumnIndex(COLUMN_COUNT_C_STRING2);
          Object yourCell = config.getCell(yourCwe, yourIndex);
          int myIndex = getColumnIndex(COLUMN_COUNT_C_STRING2);
          setCell(yourCwe, myIndex, yourCell);
        }
        {
          // Replace "Count Java"
          int yourIndex = config.getColumnIndex(COLUMN_COUNT_JAVA_STRING2);
          Object yourCell = config.getCell(yourCwe, yourIndex);
          int myIndex = getColumnIndex(COLUMN_COUNT_JAVA_STRING2);
          setCell(yourCwe, myIndex, yourCell);
        }
      }
    }
  }
  
  /**
   * 
   * @param cwe
   * @return
   */
  public boolean hasCwe(String cwe) {
    return rowMap.containsKey(cwe);
  }
  
  /**
   * Add a new column to the configuration
   * 
   * @param name
   */
  private void addColumn(String name) {
    // Convert from old column name to new column name
    if (COLUMN_COUNT_C_STRING1.equalsIgnoreCase(name)) {
      name = COLUMN_COUNT_C_STRING2;
    }
    if (COLUMN_COUNT_JAVA_STRING1.equalsIgnoreCase(name)) {
      name = COLUMN_COUNT_JAVA_STRING2;
    }
    
    int index = columnMap.size();
    columnMap.put(name.toLowerCase(), index);
    extraColumns.add(name);
    headers.add(name);
    
    // Stub values in data
    for (List<?> row : data) {
      row.add(null);
    }
  }
  
  /**
   * Return true if the configuration is empty
   * 
   * @return
   */
  public boolean isEmpty() {
    return data.isEmpty();
  }
  
  /**
   * Parse the header row
   * 
   * @param record
   */
  private void parseHeader(CSVRecord record) {
    int size = record.size();
    
    headers = new LinkedList<String>();
    
    // Import the cells
    for (int i = 0; i < size; i++) {
      String text = record.get(i);
      headers.add(text);
      
      if (COLUMN_SFP_STRING.equalsIgnoreCase(text)) COLUMN_SFP = i;
      else if (COLUMN_CWE_STRING.equalsIgnoreCase(text)) COLUMN_CWE = i;
      else if (COLUMN_SHOW_STRING.equalsIgnoreCase(text)) COLUMN_SHOW = i;
      else if (COLUMN_SHOW_STRING_OLD.equalsIgnoreCase(text)) COLUMN_SHOW = i;
      else if (COLUMN_CPPCHECK_STRING.equalsIgnoreCase(text)) COLUMN_CPPCHECK = i;
      else if (COLUMN_RATS_STRING.equalsIgnoreCase(text)) COLUMN_RATS = i;
      else if (COLUMN_SPLINT_STRING.equalsIgnoreCase(text)) COLUMN_SPLINT = i;
      else if (COLUMN_JLINT_STRING.equalsIgnoreCase(text)) COLUMN_JLINT = i;
      else if (COLUMN_FINDBUGS_STRING.equalsIgnoreCase(text)) COLUMN_FINDBUGS = i;
      
      else if (COLUMN_COUNT_C_STRING1.equalsIgnoreCase(text)) {
//        COLUMN_COUNT_C = i;
        // Convert to new name
        text = COLUMN_COUNT_C_STRING2;
      }
      else if (COLUMN_COUNT_JAVA_STRING1.equalsIgnoreCase(text)) {
//        COLUMN_COUNT_JAVA = i;
        // Convert to new name
        text = COLUMN_COUNT_JAVA_STRING2;
      }
//      else if (COLUMN_COUNT_C_STRING2.equalsIgnoreCase(text)) COLUMN_COUNT_C = i;
//      else if (COLUMN_COUNT_JAVA_STRING2.equalsIgnoreCase(text)) COLUMN_COUNT_JAVA = i;
      else {
        extraColumns.add(text);
      }
      columnMap.put(text.toLowerCase(), i);
    }
  }
  
  /**
   * Parse the record as a row of data
   * 
   * @param record
   * @param rcount
   * @return
   */
  private int parseData(CSVRecord record, int rcount) {
    int size = record.size();
    
    List<Object> row = new LinkedList<Object>();
    
    // Import the cells
    for (int i = 0; i < size; i++) {
      String text = record.get(i);
      row.add(getCell(i, text));
    }
    
    if (row.size() > COLUMN_CWE) {
      String cwe = (String) row.get(COLUMN_CWE);
      
      // Fix the CWE ID and replace the value
      cwe = fixSfpCweIdentifier(cwe);
      row.remove(COLUMN_CWE);
      row.add(COLUMN_CWE, cwe);
      
      String sfp = (String)row.get(COLUMN_SFP);
      // Fix the CWE ID and replace the value
      sfp = fixSfpCweIdentifier(sfp);
      row.remove(COLUMN_SFP);
      row.add(COLUMN_SFP, sfp);
      
      // Only add a new row if this is a non-empty row and the CWE
      // does not exist in the map yet.
      if (!cwe.isEmpty() && !rowMap.containsKey(cwe)) {
        data.add(row);
        rowMap.put(cwe, rcount);
        sfpMap.put(cwe, (String) row.get(COLUMN_SFP));
        ShowField showState = (ShowField) row.get(COLUMN_SHOW);
        visibilityMap.put((String) row.get(COLUMN_CWE), showState.toBoolean());
        // We just added a new row
        rcount++;
        dirty = true;
      }
    }
    return rcount;
  }
  
  private String fixSfpCweIdentifier(String name) {
    return name.replaceAll("([^-])-([^-])", "$1$2");
  }

  /**
   * Convert the input text into an appropriate representative object for the cell.
   * 
   * @param header
   * @param index
   * @param text
   * @return
   */
  private Object getCell(int index, String text) {
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
        export(configFile);
      } catch (IOException e) {
        e.printStackTrace();
      }
      
      // Tell all the listeners about changes
      for (IAdaptorConfigurationListener listener : listeners) {
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
    return getIndex(cwe);
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
   * Clear all of the internal data
   */
  public void clear() {
    data.clear();
    rowMap.clear();
    visibilityMap.clear();
    headers = null;
    columnMap.clear();
    extraColumns.clear();
    sfpMap.clear();
    this.dirty = true;
  }
  
  /**
   * Reload the local configuration.
   */
  public void reset() {
    clear();
    
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
    clear();
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
  
  /**
   * Return true if the given index is one of the adaptors
   * 
   * @param index
   * @return
   */
  public boolean isAdaptorIndex(int index) {
    if (COLUMN_CPPCHECK == index) return true;
    if (COLUMN_RATS == index) return true;
    if (COLUMN_SPLINT == index) return true;
    if (COLUMN_JLINT == index) return true;
    if (COLUMN_FINDBUGS == index) return true;
    return false;
  }
  
  /**
   * Get the size of the configuration
   * 
   * @return
   */
  public int getSize() {
    return data.size();
  }
  
  /**
   * Get the location of the specified CWE
   * 
   * @param cwe
   * @return
   */
  public int getIndex(String cwe) {
    if (rowMap.containsKey(cwe)) {
      return rowMap.get(cwe);
    }
    return data.size();
  }
  
  /**
   * Remove the row
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
  
  /**
   * Add a row into the data set at the specified location
   * 
   * @param index
   * @param row
   */
  public void add(int index, List<?> newRow) {
    data.add(index, newRow);
    
    rowMap.clear();
    int count = 0;
    for (List<?> row : data) {
      String cwe = (String) row.get(COLUMN_CWE);
      rowMap.put(cwe, count++);
    }
  }
  
  /**
   * Get the trust for the specified cwe/tool
   * 
   * @param cwe
   * @return
   */
  public int getTrust(String cwe, String tool) {
    Integer index = rowMap.get(cwe);
    if (index != null) {
      tool = tool.toLowerCase();
      List<?> row = data.get(index);
      TrustField trust = null;
      switch (tool) {
        case COLUMN_CPPCHECK_STRING:
          trust = (TrustField) row.get(COLUMN_CPPCHECK);
          break;
        case COLUMN_RATS_STRING:
          trust = (TrustField) row.get(COLUMN_RATS);
          break;
        case COLUMN_SPLINT_STRING:
          trust = (TrustField) row.get(COLUMN_SPLINT);
          break;
        case COLUMN_JLINT_STRING:
          trust = (TrustField) row.get(COLUMN_JLINT);
          break;
        case COLUMN_FINDBUGS_STRING:
          trust = (TrustField) row.get(COLUMN_FINDBUGS);
          break;
      }
      if (trust != null) {
        return trust.intValue();
      }
    }
    return 0;
  }
  
  /**
   * Get the index of the specified column (by name)
   * 
   * @param name
   * @return
   */
  public Integer getColumnIndex(String name) {
    return columnMap.get(name.toLowerCase());
  }
  
  /**
   * Get the cell value for the specified CWE and index.
   * 
   * @param cwe
   * @param index
   * @return
   */
  public Object getCell(String cwe, int index) {
    List<?> row = getRow(cwe);
    if (row != null) {
      if (row.size() > index) {
        return row.get(index);
      }
    }
    return null;
  }
  
  /** Get the cell value for the specified data row and column index
   * 
   * @param row
   * @param col
   * @return
   */
  public Object getCell(int row, int col) {
    List<?> rowData = data.get(row);
    if (rowData != null) {
      if (rowData.size() > col) {
        return rowData.get(col);
      }
    }
    return null;
  }
  
  /**
   * Set the value of the cell at the specified cwe/index
   * 
   * @param cwe
   * @param index
   * @param yourCell
   */
  private void setCell(String cwe, int index, Object yourCell) {
    List<Object> row = getRow(cwe);
    Object value = row.get(index);
    boolean changed = false;
    if (value == null) {
      if (yourCell != null) {
        changed = true;
      }
    } else {
      changed = !value.equals(yourCell);
    }
    if (changed) {
      row.remove(index);
      row.add(index, yourCell);
      this.dirty = true;
    }
  }
  
  /**
   * Get the row for the specified CWE
   * 
   * @param cwe
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<Object> getRow(String cwe) {
    int rowIndex = rowMap.get(cwe);
    List<?> row = data.get(rowIndex);
    return (List<Object>) row;
  }
  
  /**
   * Append a new row to our data. The columns may need reordering.
   * 
   * @param rowHeaders
   * @param row
   */
  private void addRow(List<String> rowHeaders, List<?> row) {
    // Create the new row in an array. This is done to simplify the steps
    Object[] newRow = new Object[columnMap.size()];
    String yourCwe = null;
    boolean yourShow = true;
    
    for (int i = 0; i < rowHeaders.size(); i++) {
      String name = rowHeaders.get(i);
      
      // Convert from old column name to new column name
      if (COLUMN_COUNT_C_STRING1.equalsIgnoreCase(name)) {
        name = COLUMN_COUNT_C_STRING2;
      }
      if (COLUMN_COUNT_JAVA_STRING1.equalsIgnoreCase(name)) {
        name = COLUMN_COUNT_JAVA_STRING2;
      }
      
      Object cell = row.get(i);
      if (COLUMN_CWE_STRING.equalsIgnoreCase(name)) {
        yourCwe = (String) cell;
      }
      if (COLUMN_SHOW_STRING.equalsIgnoreCase(name)) {
        Boolean b = ((ShowField) cell).toBoolean();
        if (b == false) {
          yourShow = false;
        }
      }
      if (COLUMN_SHOW_STRING_OLD.equalsIgnoreCase(name)) {
        Boolean b = ((ShowField) cell).toBoolean();
        if (b == false) {
          yourShow = false;
        }
      }
      int myIndex = getColumnIndex(name.toLowerCase());
      newRow[myIndex] = cell;
    }
    
    if (yourCwe != null) {
      List<Object> newRowList = Arrays.asList(newRow);
      int count = data.size();
      data.add(newRowList);
      rowMap.put(yourCwe, count);
      sfpMap.put(yourCwe, (String) newRowList.get(COLUMN_SFP));
      visibilityMap.put(yourCwe, yourShow);
    } else {
      System.err.println("Cannot add a row; missing CWE column");
    }
  }
  
  /**
   * Get the list of "extra" column names, which are any of the non-standard columns. These columns
   * will show up in the finding view.
   * 
   * @return
   */
  public String[] getExtraColumnNames() {
    return extraColumns.toArray(new String[extraColumns.size()]);
  }
  
  /** Get the SFP mapped to the specified CWE
   * 
   * @param cwe
   * @return
   */
  public String getSfp(String cwe) {
    String result = sfpMap.get(cwe);
    if (result == null) {
      return "SFP--1";
    }
    return result;
  }
  
  /** Export to the specified file
   * 
   * @param file
   * @throws IOException
   */
  public void export(File file) throws IOException {
    OutputStream os = null;
    CSVPrinter printer = null;
    try {
      os = new FileOutputStream(file);
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
  }
}
