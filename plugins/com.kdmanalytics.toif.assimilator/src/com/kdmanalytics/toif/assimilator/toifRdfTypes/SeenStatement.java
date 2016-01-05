/**
 * KDM Analytics Inc (2012)
 * 
 * @Author Adam Nunn
 * @Date Jul 10, 2012
 */

package com.kdmanalytics.toif.assimilator.toifRdfTypes;

/**
 * Class to store values about the statements. these values are used in the equality to determine if
 * the statement has been seen.
 * 
 * @author adam
 *         
 */
public class SeenStatement {
  
  private String subjectURI;
  
  private String predicateURI;
  
  private String objectURI;
  
  /**
   * create a new statement.
   * 
   * @param subjectURI
   *          the subject of the statement
   * @param predicateURI
   *          the predicate of the statement
   * @param objectURI
   *          the object of the statement.
   */
  public SeenStatement(String subjectURI, String predicateURI, String objectURI) {
    this.subjectURI = subjectURI;
    this.predicateURI = predicateURI;
    this.objectURI = objectURI;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((objectURI == null) ? 0 : objectURI.hashCode());
    result = prime * result + ((predicateURI == null) ? 0 : predicateURI.hashCode());
    result = prime * result + ((subjectURI == null) ? 0 : subjectURI.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SeenStatement other = (SeenStatement) obj;
    if (objectURI == null) {
      if (other.objectURI != null) return false;
    } else if (!objectURI.equals(other.objectURI)) return false;
    if (predicateURI == null) {
      if (other.predicateURI != null) return false;
    } else if (!predicateURI.equals(other.predicateURI)) return false;
    if (subjectURI == null) {
      if (other.subjectURI != null) return false;
    } else if (!subjectURI.equals(other.subjectURI)) return false;
    return true;
  }
  
}
