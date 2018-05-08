
package com.kdmanalytics.toif.ui.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public enum DescriptionMap {
  INSTANCE;

  /**
   * Lookup table for SFP information
   */
  private Map<String, String[]> sfpLookup = new HashMap<String, String[]>();

  /**
   * Lookup table for CWE information
   */
  private Map<String, String[]> cweLookup = new HashMap<String, String[]>();

  private DescriptionMap() {
    try {
      loadSfpLookups();
      loadCweLookups();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Map<String, String[]> getSfpMap() {
    return Collections.unmodifiableMap(sfpLookup);
  }

  public Map<String, String[]> getCweMap() {
    return Collections.unmodifiableMap(cweLookup);
  }

  /**
   * SFP table lookup
   * 
   * @throws IOException
   */
  private void loadSfpLookups() throws IOException {
    InputStreamReader in = null;
    CSVParser parser = null;
    try {
      InputStream is = getClass().getResourceAsStream("/resources/sfp.csv");
      in = new InputStreamReader(is);
      CSVFormat format = CSVFormat.EXCEL.withDelimiter(',').withIgnoreEmptyLines();

      parser = new CSVParser(in, format);

      boolean header = true;
      for (CSVRecord record : parser) {
        if (header) {
          // Ignore header
          header = false;
          continue;
        }
        String sfpid = record.get(0);
        String cluster = record.get(1);
        String name = record.get(2);

        if ("-1".equals(sfpid)) {
          sfpLookup.put("SFP-" + sfpid, new String[] {
                                                       sfpid, name, cluster
          });
        } else {
          sfpLookup.put("SFP" + sfpid, new String[] {
                                                      sfpid, name, cluster
          });
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
   * SFP table lookup
   * 
   * @throws IOException
   */
  private void loadCweLookups() throws IOException {
    InputStreamReader in = null;
    CSVParser parser = null;
    try {
      InputStream is = getClass().getResourceAsStream("/resources/cwe.csv");
      in = new InputStreamReader(is);
      CSVFormat format = CSVFormat.EXCEL.withDelimiter(',').withIgnoreEmptyLines();

      parser = new CSVParser(in, format);

      boolean header = true;
      for (CSVRecord record : parser) {
        if (header) {
          // Ignore header
          header = false;
          continue;
        }
        String cweid = record.get(0);
        String name = record.get(1);
        // if(size > 2)
        // {
        // String description = record.get(2);
        // cweLookup.put(cweid, new String[] {cweid, name, description});
        // }
        // else
        if ("-1".equals(cweid)) {
          cweLookup.put("CWE-" + cweid, new String[] {
                                                       cweid, name
          });
        } else {
          cweLookup.put("CWE" + cweid, new String[] {
                                                      cweid, name
          });
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
