/**
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved.
 */
package com.kdmanalytics.toif.ui.common;

/** A three state 'boolean' that allows not only for three states, but differentiation
 * for label styling and editing.
 * 
 * @author Ken Duck
 *
 */
public enum YesNoUnsetState {
  YES,
  NO,
  UNSET;
  
  @Override
  public String toString() {
    switch(this) {
      case YES: return "Yes";
      case NO: return "No";
      default: return "-";
    }
  }

  /** Get the enum that matches the provided string value.
   * 
   * @param text
   * @return
   */
  public static Object fromString(String text) {
    if (text != null) {
      for (YesNoUnsetState state : YesNoUnsetState.values()) {
        if (text.equalsIgnoreCase(state.toString())) {
          return state;
        }
      }
    }
    return UNSET;
  }
}
