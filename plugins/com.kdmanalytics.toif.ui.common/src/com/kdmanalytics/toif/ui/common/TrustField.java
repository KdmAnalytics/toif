/**
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved.
 */

package com.kdmanalytics.toif.ui.common;

/**
 * The trust field is an integer between 0 and 100 (inclusive) or unset.
 * 
 * @author Ken Duck
 *
 */
public class TrustField {
  
  
  private Integer value;
  
  /**
   * Initialize the value, do some simple range checking
   * 
   * @param value
   */
  public TrustField(Integer value) {
    if (value != null) {
      if (value < 0) {
        value = 0;
      }
      if (value > 100) {
        value = 100;
      }
    }
    this.value = value;
  }
  
  /**
   * Get the TrustField represented by the given text
   * 
   * @param text
   * @return
   */
  public static TrustField fromString(String text) {
    if ("-".equals(text)) {
      return new TrustField(null);
    }
    try {
      Integer value = Integer.parseInt(text);
      return new TrustField(value);
    } catch (NumberFormatException e) {
      return new TrustField(null);
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (value == null) {
      return "-";
    }
    return value.toString();
  }

  /** Return true if this field represents a valid (editable) value.
   * 
   * @return
   */
  public boolean isValid() {
    return value != null;
  }
}
