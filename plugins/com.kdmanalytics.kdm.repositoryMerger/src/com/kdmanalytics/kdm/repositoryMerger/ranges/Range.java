/**
 * 
 * @author Ken Duck
 *
 */

package com.kdmanalytics.kdm.repositoryMerger.ranges;

import java.util.ArrayList;
import java.util.List;

public class Range implements Cloneable {
  
  protected int from;
  
  protected int to;
  
  public Range(int from, int to) {
    this.from = from;
    this.to = to;
  }
  
  public Range(long uidBefore, long uidAfter) {
    from = (int) uidBefore;
    to = (int) uidAfter;
  }
  
  public int getFrom() {
    return from;
  }
  
  public int getTo() {
    return to;
  }
  
  public void setFrom(int from) {
    this.from = from;
  }
  
  public void setTo(int to) {
    this.to = to;
  }
  
  /**
   * 
   */
  @Override
  public final int hashCode() {
    return (from + "," + to).hashCode();
  }
  
  /**
   * Returns true if the ranges are equal
   * 
   */
  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof Range)) return false;
    
    Range r = (Range) o;
    return (r.from == from && r.to == to);
  }
  
  /**
   * Returns true if the specified range is contained within this range.
   * 
   * @param r
   * @return
   */
  public final boolean contains(Range r) {
    if (r.from >= from && r.to <= to) return true;
    return false;
  }
  
  /**
   * Returns true if the specified range is contained within this range.
   * 
   * @param r
   * @return
   */
  public final boolean contains(long id) {
    if (id < from) return false;
    if (id > to) return false;
    return true;
  }
  
  /**
   * 
   * @param r
   * @return
   */
  public boolean intersects(Range r) {
    if (r.from >= from && r.from <= to) return true;
    if (r.to >= from && r.to <= to) return true;
    
    if (from >= r.from && from <= r.to) return true;
    if (to >= r.from && to <= r.to) return true;
    return false;
  }
  
  /**
   * Return the intersection between two ranges
   * 
   * @param testRange
   * @return
   */
  public Range getIntersection(Range r) {
    if (!intersects(r)) return null;
    
    int f = r.from;
    int t = r.to;
    if (from > f) f = from;
    if (to < t) t = to;
    
    return new Range(f, t);
  }
  
  /**
   * Return a list of ranges that indicates the difference between this range and the specified
   * range
   * 
   * Consider the situation where we have a range from 1 to 6 (inclusive) 1 2 3 4 5 6
   * 
   * There are 4 situations of overlapping ranges. One where the range is completely contained, one
   * where it overlaps the beginning of the range, one where it overlaps the end, and one where the
   * range is completely covered.
   * 
   * a. 1 2 - - 5 6 b. - - 3 4 5 6 c. 1 2 3 4 - - d. - - - - - -
   * 
   * @param r
   * @return
   */
  public final List<Range> minus(Range r) {
    List<Range> results = new ArrayList<Range>();
    if (r.from <= from) // case b or case d
    {
      if (r.to >= to) // case d -- all deleted
      {
        return results;
      } else // case b -- beginning removed
      {
        results.add(new Range(r.to + 1, to));
      }
    } else if (r.to >= to) // case c
    {
      results.add(new Range(from, r.from - 1));
    } else // case a
    {
      results.add(new Range(from, r.from - 1));
      results.add(new Range(r.to + 1, to));
    }
    
    return results;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public final String toString() {
    return "Range: " + from + "-" + to;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public final Range clone() {
    return new Range(from, to);
  }
  
  /**
   * Get the size of the range.
   * 
   * @return
   */
  public int getSize() {
    return to - from + 1;
  }
}
