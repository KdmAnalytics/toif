
package com.kdmanalytics.kdm.repositoryMerger.linkconfig;

/**
 * Merge configuration interface. Provides an API through which the merge tool can determine how to
 * handle various situations. Also tells the Linker proper what id generators to use.
 * 
 * Subclass this to provide the data required to work.
 * 
 */
public abstract class MergeConfig {
  
  public static final int IGNORE = 0;
  
  public static final int MERGE = 1;
  
  public static final int COPY = 2;
  
  public static final int SINGLETON = 3;
  
  public static final int DEFAULT = IGNORE;
  
  public MergeConfig() {
  }
  
  // /**
  // * Get the merge type for the provided element
  // *
  // * @param element
  // * @return
  // */
  // public int getMergeType(Element element)
  // {
  // return getMergeType(element.getClass().getSimpleName());
  // }
  
  /**
   * Given the string type name, return the type of merge to engage in.
   * 
   * @param simpleName
   * @return
   */
  public abstract int getMergeType(String kdmType);
  
  /**
   * If there is no link ID, how should the elements be handled?
   * 
   * @return
   */
  public abstract int getNoIdMergeType();
}
