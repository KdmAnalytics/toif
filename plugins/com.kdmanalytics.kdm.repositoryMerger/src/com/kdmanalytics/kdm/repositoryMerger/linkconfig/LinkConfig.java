
package com.kdmanalytics.kdm.repositoryMerger.linkconfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Load the configuration file and provide access to the contents through the specified interfaces.
 * 
 */
public class LinkConfig {
  
  // private static final Logger LOG =
  // Logger.getLogger(LinkConfig.class);//getLogger(LinkConfig.class);
  
  /**
   * This class provides access to the merge specific components of the file
   * 
   */
  private FileMergeConfig mergeConfig = null;
  
  /**
   * 
   */
  private Map<String, Integer> mergeMap = null;
  
  /**
   * By default, what is done when an element has no id
   */
  private int noIdMergeType = MergeConfig.IGNORE;
  
  /**
   * 
   */
  private List<String> mergeRegex = null;
  
  // /**
  // *
  // */
  // private Map<String, String> types = null;
  
  /**
   * 
   */
  public LinkConfig() {
    init();
  }
  
  /**
   * Parse the configuration file and setup the required data structures. Instantiate the interface
   * classes that will be requested.
   * 
   * @param file
   */
  public LinkConfig(File file) {
    init();
    load(file);
  }
  
  /**
   * Parse the configuration stream and setup the required data structures. Instantiate the
   * interface classes that will be requested.
   * 
   * @param is
   */
  public LinkConfig(InputStream is) {
    init();
    load(is);
  }
  
  /**
   * Load the specified config file into the linker configuration.
   * 
   * @param file
   */
  public void load(File file) {
    try {
      InputStream is = new FileInputStream(file);
      load(is);
      is.close();
    } catch (FileNotFoundException e) {
      // LOG.error("Exception loading config file", e);
    } catch (IOException e) {
      // LOG.error("Exception loading config file", e);
    }
  }
  
  /**
   * Load configuration information from the specified stream into the linker configuration.
   * 
   * @param is
   */
  public void load(InputStream is) {
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(is));
      String line = in.readLine();
      int lineCount = 0;
      while (line != null) {
        lineCount++;
        line = line.trim();
        if (line.isEmpty()) {
          line = in.readLine();
          continue;
        }
        if (line.startsWith("#")) {
          line = in.readLine();
          continue;
        }
        if (line.startsWith("action,")) loadMergeDefinition(line.substring(7));
        else if (line.startsWith("noid,")) loadNoIdDefinition(line.substring(5));
        else System.err.println("Parse error on line " + lineCount);// LOG.error("Parse error on
                                                                    // line "
                                                                    // +
                                                                    // lineCount);
        line = in.readLine();
      }
    } catch (IOException e) {
      // LOG.error("Exception loading config file", e);
    }
  }
  
  /**
   * 
   * @param is
   */
  private void init() {
    mergeConfig = new FileMergeConfig(this);
    
    mergeMap = new HashMap<String, Integer>();
    mergeRegex = new ArrayList<String>();
    
    // types = new HashMap<String, String>();
  }
  
  /**
   * 
   * @param substring
   */
  private void loadNoIdDefinition(String def) {
    StringTokenizer st = new StringTokenizer(def, ",");
    if (!st.hasMoreElements()) {
      // LOG.error("Bad noid definition");
      return;
    }
    String type = st.nextToken();
    if ("COPY".equals(type)) {
      noIdMergeType = MergeConfig.COPY;
    } else if ("MERGE".equals(type)) {
      noIdMergeType = MergeConfig.MERGE;
    } else if ("IGNORE".equals(type)) {
      noIdMergeType = MergeConfig.IGNORE;
    } else if ("SINGLETON".equals(type)) {
      noIdMergeType = MergeConfig.SINGLETON;
    } else {
      // LOG.error("Invalid link type " + type);
      return;
    }
  }
  
  /**
   * Load the merge definition map
   * 
   * @param substring
   */
  private void loadMergeDefinition(String def) {
    StringTokenizer st = new StringTokenizer(def, ",");
    if (!st.hasMoreElements()) {
      // LOG.error("Bad action definition");
      return;
    }
    String kdmType = st.nextToken();
    if (!st.hasMoreElements()) {
      // LOG.error("Bad action definition");
      return;
    }
    String actionType = st.nextToken();
    try {
      Field field = MergeConfig.class.getField(actionType);
      int value = field.getInt(null);
      if (isRegex(kdmType)) mergeRegex.add(kdmType);
      
      // Even regexes are placed here, since they will never match
      // without coercion.
      mergeMap.put(kdmType, value);
    } catch (SecurityException e) {
      // LOG.error("Exception loading definitions", e);
    } catch (NoSuchFieldException e) {
      // LOG.error("Exception loading definitions", e);
    } catch (IllegalArgumentException e) {
      // LOG.error("Exception loading definitions", e);
    } catch (IllegalAccessException e) {
      // LOG.error("Exception loading definitions", e);
    }
  }
  
  /**
   * Ugly hack to identify regular expressions. This only works since we know the limitations of the
   * string names we are expecting.
   * 
   * @param kdmType
   * @return
   */
  private boolean isRegex(String s) {
    if (s.contains(".")) return true;
    if (s.contains("?")) return true;
    if (s.contains("*")) return true;
    if (s.contains("[")) return true;
    if (s.contains("(")) return true;
    if (s.contains("^")) return true;
    if (s.contains("$")) return true;
    return false;
  }
  
  /**
   * Get the interface used to retrieve merge specific data
   * 
   * @return
   */
  public MergeConfig getMergeConfig() {
    return mergeConfig;
  }
  
  /**
   * Return the merge type based on the KDM type.
   * 
   */
  public int getMergeType(String kdmType) {
    Integer result = mergeMap.get(kdmType);
    if (result == null) {
      for (Iterator<String> it = mergeRegex.iterator(); it.hasNext();) {
        String regex = it.next();
        if (kdmType.matches(regex)) {
          result = mergeMap.get(regex);
          break;
        }
      }
      
      // FALLBACK, anything else is ignored.
      if (result == null) result = MergeConfig.IGNORE;
    }
    return result.intValue();
  }
  
  /**
   * Where there are no link:ids, how should the elements be handled
   * 
   * @return
   */
  public int getNoIdMergeType() {
    return noIdMergeType;
  }
  
}
