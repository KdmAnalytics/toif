
package com.kdmanalytics.kdm.repositoryMerger.Utilities;

/**
 * Simple subclass of IllegalArguemntException
 * 
 * Gives a little more info than the standards IllegalArgumentException
 * 
 * @author Kyle Girard
 *        
 */
public class NullArgumentException extends IllegalArgumentException {
  
  /**
   * Required for serialization support.
   *
   * @see java.io.Serializable
   */
  private static final long serialVersionUID = 1174360235354917591L;
  
  public NullArgumentException(String argName) {
    super((argName == null ? "Argument" : argName) + " must not be null.");
  }
  
}
