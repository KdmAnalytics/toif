/**
 * 
 */

package com.kdmanalytics.kdm.repositoryMerger.Utilities;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Kyle Girard
 *         
 */
public class IntUtils {
  
  /**
   * Parses the string argument as a signed decimal integer. Faster than Integer.parseInt since it
   * assumes radix 10
   * 
   * @param intString
   *          a String containing the int representation to be parsed
   *          
   * @return the integer value represented by the argument in decimal.
   */
  public static int parseIntChecked(final String intString) {
    // Ensure that we have
    checkNotNull(intString, "A null string cannot be parsed");
    
    // Check for a sign.
    int num = 0;
    int sign = -1;
    final int len = intString.length();
    final char ch = intString.charAt(0);
    if (ch == '-') {
      if (len == 1) {
        throw new NumberFormatException("Missing digits:  " + intString);
      }
      sign = 1;
    } else {
      final int d = ch - '0';
      if ((d < 0) || (d > 9)) {
        throw new NumberFormatException("Malformed:  " + intString);
      }
      num = -d;
    }
    
    // Build the number.
    final int max = (sign == -1) ? -Integer.MAX_VALUE : Integer.MIN_VALUE;
    final int multmax = max / 10;
    int i = 1;
    while (i < len) {
      final int d = intString.charAt(i++) - '0';
      if ((d < 0) || (d > 9)) {
        throw new NumberFormatException("Malformed:  " + intString);
      }
      if (num < multmax) {
        throw new NumberFormatException("Over/underflow:  " + intString);
      }
      num *= 10;
      if (num < (max + d)) {
        throw new NumberFormatException("Over/underflow:  " + intString);
      }
      num -= d;
    }
    
    return sign * num;
  }
  
  /**
   * Quick and dirt integer parsing. Faster than Integer.parseInt since it assumes radix 10. Use
   * this function only when you know that your String really is an integer.
   * 
   * @param intString
   *          a String containing the int representation to be parsed
   *          
   * @return the integer value represented by the argument in decimal.
   */
  public static int parseInt(final String intString) {
    // Check for a sign.
    int num = 0;
    int sign = -1;
    final int len = intString.length();
    final char ch = intString.charAt(0);
    if (ch == '-') {
      sign = 1;
    } else {
      num = '0' - ch;
    }
    
    // Build the number.
    int i = 1;
    while (i < len) {
      num = num * 10 + '0' - intString.charAt(i++);
    }
    
    return sign * num;
  }
  
}
