
package com.kdmanalytics.kdm.repositoryMerger.ranges;

import info.aduna.iteration.CloseableIteration;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import com.kdmanalytics.kdm.repositoryMerger.Utilities.IntUtils;
import com.kdmanalytics.kdm.repositoryMerger.Utilities.KdmConstants.WorkbenchPredicate;
import com.kdmanalytics.kdm.repositoryMerger.Utilities.MergerURI;
import com.kdmanalytics.kdm.repositoryMerger.Utilities.StringTokenIterator;

/**
 * Class that tracks a list of ranges, providing operations to add and remove ranges from the
 * contained list.
 * 
 * The operations of this list are a little different from a standard list. Since it is working with
 * ranges, a range can be "contained" in this list by being "contained" in any of the stored ranges.
 * For example if the list consists of a single element (1-5) it can be said to contain the element
 * (2-3)
 * 
 * This list *may* be backed by a repository specified by a particular URI (subject). In this case
 * the initial list should be initialised from the repository and any edits should be written to the
 * repository.
 * 
 */
public class RangeSet implements List<Range> {
  
  private static final Logger LOG = Logger.getLogger(RangeSet.class);
  
  /**
   * 
   */
  private static URI UID;
  
  private static URI lastUID;
  
  private static final int INFINITY = Integer.MAX_VALUE;
  
  /**
   * 
   */
  private final List<Range> ranges = new ArrayList<Range>();
  
  /**
   * If the list is backed by a repository, store that information here.
   * 
   */
  private Repository repository;
  
  private URI subject;
  
  /**
   * Indicates whether edits should be committed or not
   */
  private boolean commit = true;
  
  /**
   * Cache the result of the test for a particular ID. I do this since tests are in partial order it
   * is quite possible the same test will occur multiple times in a row.
   */
  private long cacheContainsId = -1;
  
  private boolean cacheContainsResult;
  
  /**
   * More advanced caching. IDs near each other (an in order search) are more likely to be in the
   * same range.
   * 
   * FIXME: Currently I do not use this value for extra sophistimicated performance improvements,
   * since it appears the simpler caching and binary search are currently sufficient. There is no
   * reason currently to mess with the code to remove this yet, though, and it is always possible I
   * will want to add additional caching.
   */
  // private Range cacheContainsRange;
  
  /**
   * Instantiate a new set of ranges, initially containing only a single range containing a single
   * value.
   * 
   * @param uid
   */
  public RangeSet(int uid) {
    addImpl(new Range(uid, uid));
  }
  
  /**
   * I want to remove all references to Point, which is why this constructor is deprecated.
   * 
   * 
   * @param containedRanges
   * @deprecated No longer for public use, The Point class is not to be used
   */
  @Deprecated
  public RangeSet(List<Point> containedRanges) {
    for (final Point p : containedRanges) {
      addImpl(new Range(p.x, p.y));
    }
  }
  
  /**
   * 
   * @param range
   */
  public RangeSet(Range range) {
    add(range);
  }
  
  /**
   * Instantiate a new ranges set based on the information found in the specified repository.
   * 
   * @param element
   * @throws RepositoryException
   */
  public RangeSet(Repository repository, URI subject) throws RepositoryException {
    this.repository = repository;
    this.subject = subject;
    
    commit = false;
    
    final RepositoryConnection con = repository.getConnection();
    
    try {
      // Add any other ranges found listed in the repository
      initRanges(con, subject);
      
      // Get the natural range
      final int uid = getUID(con, subject);
      // In some cases an element does not have ranges.
      if (uid < 0) {
        return;
      }
      
      final int lastUID = getLastUID(con, subject);
      
      // Always ensure the natural range is included
      addImpl(new Range(uid, lastUID));
    } finally {
      commit = true;
      if (con != null) {
        con.close();
      }
    }
  }
  
  public RangeSet() {
  }
  
  /**
   * Generate a range given the text range description.
   * 
   * @param txt
   * @return
   */
  public static RangeSet parse(String txt) {
    final RangeSet ranges = new RangeSet();
    ranges.parseString(txt);
    return ranges;
  }
  
  /**
   * Initialize more ranges from the __KNT_UIDs attribute.
   * 
   * @param con
   * @param subject
   * @throws RepositoryException
   */
  private final void initRanges(RepositoryConnection con, URI subject) throws RepositoryException {
    final CloseableIteration<Statement, RepositoryException> results = con.getStatements(subject,
                                                                                         WorkbenchPredicate.WORKBENCH_RANGES.toURI(),
                                                                                         null, false);
    while (results.hasNext()) {
      final Statement stmt = results.next();
      final Value object = stmt.getObject();
      final String rangesString = object.stringValue();
      if ((rangesString != null) && !rangesString.isEmpty()) {
        parseString(rangesString);
      }
    }
  }
  
  /**
   * This method makes the assumption that the rangeString is produced by a clean RangeSet which is
   * minimal and in order. If it detects that this is not the case, it calls a much less efficient
   * version.
   * 
   * @param rangesString
   */
  private final void parseString(String rangesString) {
    int max = 0;
    if ((rangesString == null) || rangesString.isEmpty()) {
      return;
    }
    for (final String token : new StringTokenIterator(rangesString, ',', false)) {
      final int index = token.indexOf("-");
      if (index <= 0) {
        LOG.error("Bad ranges found in " + rangesString + " (" + token + ")");
        continue;
      }
      final String x = token.substring(0, index);
      final String y = token.substring(index + 1);
      try {
        // final int from = Integer.parseInt(x);
        final int from = IntUtils.parseIntChecked(x);
        // final int to = Integer.parseInt(y);
        final int to = IntUtils.parseIntChecked(y);
        
        // If an error is detected, use the slow method.
        if (from < max) {
          LOG.error("Range string not in order or overlapping");
          ranges.clear();
          correctingParseString(rangesString);
          return;
        }
        final Range range = new Range(from, to);
        // addImpl(range);
        ranges.add(range);
        max = to;
      } catch (final NumberFormatException ex) {
        LOG.error("Invalid ranges found in " + rangesString + " (" + token + ")");
        continue;
      }
    }
  }
  
  /**
   * This parses the string and corrects any errors along the way (overlapping ranges, out of order,
   * etc.)
   * 
   * @param rangesString
   */
  private final void correctingParseString(String rangesString) {
    if ((rangesString == null) || rangesString.isEmpty()) {
      return;
    }
    for (final String token : new StringTokenIterator(rangesString, ',', false)) {
      final int index = token.indexOf("-");
      if (index <= 0) {
        LOG.error("Bad ranges found in " + rangesString + " (" + token + ")");
        continue;
      }
      final String x = token.substring(0, index);
      final String y = token.substring(index + 1);
      try {
        // final Range range = new Range(Integer.parseInt(x), Integer.parseInt(y));
        final Range range = new Range(IntUtils.parseIntChecked(x), IntUtils.parseIntChecked(y));
        addImpl(range);
      } catch (final NumberFormatException ex) {
        LOG.error("Invalid ranges found in " + rangesString + " (" + token + ")");
        continue;
      }
    }
  }
  
  /**
   * Get the UID for the specified node.
   * 
   * @param con
   * @param subject
   * @return
   * @throws RepositoryException
   */
  private final int getUID(RepositoryConnection con, URI subject) throws RepositoryException {
    if (UID == null) {
      UID = con.getValueFactory().createURI(MergerURI.KdmNS, "UID");
    }
    final CloseableIteration<Statement, RepositoryException> results = con.getStatements(subject, UID, null, false);
    if (!results.hasNext()) {
      // There are some cases where we are happy to not have a UID.
      // In these cases we will just have empty ranges.
      
      // I used to error out in cases where I didn't expect this,
      // but this is tough to keep up to date (no one will
      // remember). Therefore assume it is correct that there
      // is no UID in this situation.
      return -1;
    }
    
    final Statement stmt = results.next();
    final Value object = stmt.getObject();
    // final int uid = Integer.parseInt(object.stringValue());
    final int uid = IntUtils.parseInt(object.stringValue());
    return uid;
  }
  
  /**
   * 
   * @param con
   * @param subject
   * @return
   * @throws RepositoryException
   */
  private final int getLastUID(RepositoryConnection con, URI subject) throws RepositoryException {
    if (lastUID == null) {
      lastUID = con.getValueFactory().createURI(MergerURI.KdmNS, "lastUID");
    }
    final CloseableIteration<Statement, RepositoryException> results = con.getStatements(subject, lastUID, null, false);
    if (!results.hasNext()) {
      return getUID(con, subject);
    }
    
    final Statement stmt = results.next();
    final Value object = stmt.getObject();
    // final int uid = Integer.parseInt(object.stringValue());
    final int uid = IntUtils.parseInt(object.stringValue());
    return uid;
  }
  
  /*
   * The public add method ensures that the Range does not belong to another RangeSet by cloning the
   * passed in range. This MAY add some extra time to the process since it takes time to construct
   * and de-construct the objects, but is the only way we can be sure the data is consistent.
   * 
   * (non-Javadoc)
   * 
   * @see java.util.List#add(java.lang.Object)
   */
  @Override
  public final boolean add(Range e) {
    if (e == null) return true;
    // To ensure we are not sharing ranges with other RangeSets only
    // add clones.
    return addImpl(e.clone());
  }
  
  /**
   * 
   * @param newRange
   * @return
   */
  private final boolean addImpl(Range newRange) {
    return addImpl(0, newRange) >= 0;
  }
  
  /**
   * FIXME: This is not efficient
   * 
   * This version of the "add" method DOES NOT clone the range, so we must be sure that this is the
   * only RangeSet that contains the specified range.
   * 
   */
  private final int addImpl(int start, Range newRange) {
    boolean result = false;
    int i;
    // Add the new range, merging if applicable
    for (i = start; i < ranges.size(); ++i) {
      final Range range = ranges.get(i);
      // If the range is contained, then bail out
      if (range.contains(newRange)) {
        result = true;
        break;
      }
      
      // pre
      if (newRange.from < range.from) {
        // join
        if (newRange.to >= range.from - 1) {
          range.from = newRange.from;
          // Does the new range completely overlap the current range?
          if (newRange.to > range.to) {
            range.to = newRange.to;
            // It is possible we overlap further ranges. Merge any
            // overlapped ranges.
            final int j = i + 1;
            while (j < ranges.size()) // The size gets smaller with
            // each iteration due to the
            // merge
            {
              final Range nextRange = ranges.get(j);
              // Overlap!
              if (newRange.intersects(nextRange)) {
                if (nextRange.to > newRange.to) {
                  newRange.to = nextRange.to; // Merge
                }
                ranges.remove(j); // Remove the merged next
              }
              // No more overlaps
              else {
                break;
              }
            }
          }
          result = true;
          break;
        }
        
        // insert
        ranges.add(i, newRange.clone());
        result = true;
        break;
      }
      
      // post
      // merge
      if (range.to >= newRange.from - 1) {
        range.to = newRange.to;
        result = true;
        // Can we join with the next?
        if (i < ranges.size() - 1) {
          mergeRanges(range, i);
        }
        break;
      }
    }
    
    // If we couldn't add in by now, just append to the list
    if (!result) {
      result = ranges.add(newRange.clone());
    }
    
    if (commit && (repository != null)) {
      try {
        updateRanges();
      } catch (final RepositoryException e1) {
        e1.printStackTrace();
        return -1;
      }
    }
    if (result) {
      return i;
    } else {
      return -1;
    }
    
  }
  
  /**
   * The specified range may span multiple subsequent ranges.
   * 
   * @param range
   *          The range to keep
   * @param index
   *          The index of the range to keep, possibly merge subsequent ranges
   */
  private void mergeRanges(Range range, int index) {
    final int i = index + 1;
    
    // Keep going until we run out of ranges. We will break
    // the loop if merges are no longer possible (before we run out
    // of ranges in the set).
    while (i < ranges.size()) {
      final Range next = ranges.get(i);
      
      // If the next "from" is equal to or less than the previous "to"
      // then the
      // ranges should be merged.
      if (next.from <= range.to + 1) {
        range.to = next.to;
        
        // Now that the merge is done, remove the next.
        ranges.remove(i);
      }
      // If there is no intersection between the nodes, then there are no
      // merges
      // remaining.
      else {
        break;
      }
      
      // Don't increment i, since the size is getting smaller due to
      // removing elements from the list.
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.List#add(int, java.lang.Object)
   */
  @Override
  public final void add(int index, Range element) {
    throw new UnsupportedOperationException();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.List#addAll(java.util.Collection)
   */
  @Override
  public final boolean addAll(Collection<? extends Range> c) {
    if (c == null) {
      return true; // We can successfully add nothing
    }
    int start = 0;
    boolean result = true;
    commit = false; // Hold off commit until all adds are complete
    
    try {
      if (c instanceof RangeSet) {
        for (final Range range : c) {
          start = addImpl(start, range);
          if (start < 0) {
            result = false;
            break;
          }
        }
      } else {
        for (final Range range : c) {
          result &= add(range);
        }
      }
      if (repository != null) {
        try {
          updateRanges();
        } catch (final RepositoryException e1) {
          e1.printStackTrace();
          return false;
        }
      }
      return result;
    } finally {
      commit = true;
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.List#addAll(int, java.util.Collection)
   */
  @Override
  public final boolean addAll(int index, Collection<? extends Range> c) {
    throw new UnsupportedOperationException();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.List#clear()
   */
  @Override
  public final void clear() {
    ranges.clear();
  }
  
  /**
   * Do any of the component ranges contain the passed in range?
   * 
   * WARNING: There are some conditions on the use of this method to compare two RangeSets. This
   * test is a quick and dirty check to see what the chances are that one range contains the other.
   * Only the first element of the "child" is checked. If it is contained we will assume (for now)
   * that the rest are too. This may have to be revisited if we want to use this method in a more
   * generally applicable way.
   */
  @Override
  public final boolean contains(Object o) {
    if (o instanceof Range) {
      for (final Range range : ranges) {
        if (range.contains((Range) o)) {
          return true;
        }
      }
    }
    
    // This test is a quick and dirty check to see what the chances
    // are that one range contains the other. Only the first
    // element of the "child" is checked. If it is contained we
    // will assume (for now) that the rest are too. This may have
    // to be revisited if we want to use this method in a more
    // generally applicable way.
    if (o instanceof RangeSet) {
      final RangeSet child = (RangeSet) o;
      if (child.size() > 0) {
        return contains(child.get(0)) && !equals(child);
      }
    }
    return false;
  }
  
  /**
   * 
   * @param id
   * @return
   */
  public final boolean contains(long id) {
    // If the id is cached, then return the cached result
    if (id == cacheContainsId) {
      return cacheContainsResult;
    }
    
    // Make the new cached id the specified id
    cacheContainsId = id;
    
    final int val = Collections.binarySearch(ranges, new Range(id, id), new Comparator<Range>() {
      
      @Override
      public final int compare(Range arg0, Range arg1) {
        int result = 0;
        if (arg1.from < arg0.from) {
          result = 1;
        }
        if (arg1.from > arg0.to) {
          result = -1;
        }
        return result;
      }
      
    });
    
    cacheContainsResult = val >= 0;
    
    return cacheContainsResult;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.List#containsAll(java.util.Collection)
   */
  @Override
  public final boolean containsAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.List#get(int)
   */
  @Override
  public final Range get(int index) {
    return ranges.get(index);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.List#indexOf(java.lang.Object)
   */
  @Override
  public final int indexOf(Object o) {
    throw new UnsupportedOperationException();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.List#isEmpty()
   */
  @Override
  public final boolean isEmpty() {
    return ranges.isEmpty();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.List#iterator()
   */
  @Override
  public final Iterator<Range> iterator() {
    return ranges.iterator();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.List#lastIndexOf(java.lang.Object)
   */
  @Override
  public final int lastIndexOf(Object o) {
    throw new UnsupportedOperationException();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.List#listIterator()
   */
  @Override
  public final ListIterator<Range> listIterator() {
    throw new UnsupportedOperationException();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.List#listIterator(int)
   */
  @Override
  public final ListIterator<Range> listIterator(int index) {
    throw new UnsupportedOperationException();
  }
  
  /**
   * Remove the specified range from the contained set of ranges.
   * 
   */
  @Override
  public final boolean remove(Object o) {
    return remove(0, o) >= 0;
  }
  
  /**
   * Optimized range for use when removing many ranges (from a range set). This allows us to perform
   * the remove operation for multiple ranges in one pass through this RangeSet
   * 
   * @param index
   * @param o
   * @return
   */
  private final int remove(int index, Object o) {
    if (!(o instanceof Range)) {
      throw new UnsupportedOperationException("Cannot remove class " + o.getClass() + " from ranges");
    }
    
    final Range r = (Range) o;
    
    final List<Range> buf = new ArrayList<Range>();
    
    final int size = ranges.size();
    for (int i = index; i < size; i++) {
      final Range range = ranges.get(i);
      if (range.intersects(r)) {
        final List<Range> tmp = range.minus(r);
        buf.addAll(tmp);
        if (tmp.size() == 0) // completely remove a range
        {
          ranges.remove(i);
          return i;
        }
        if (tmp.size() == 1) // Removed one side or the other
        {
          final Range newRange = tmp.get(0);
          range.from = newRange.from;
          range.to = newRange.to;
          return i;
        }
        if (tmp.size() == 2) // Split range
        {
          Range newRange = tmp.get(0);
          range.from = newRange.from;
          range.to = newRange.to;
          newRange = tmp.get(1);
          ++i;
          ranges.add(i, newRange);
          return i;
        }
        LOG.error("Range 'minus' results in 3 ranges");
      }
    }
    
    // Write changes to the repository
    if (commit && (repository != null)) {
      try {
        updateRanges();
      } catch (final RepositoryException e) {
        LOG.error(e.getLocalizedMessage(), e);
        return -1;
      }
    }
    
    return index;
  }
  
  @Override
  public final Range remove(int index) {
    throw new UnsupportedOperationException();
  }
  
  /**
   * FIXME: Only apply changes after ALL removes
   * 
   */
  @Override
  public final boolean removeAll(Collection<?> c) {
    boolean result = true;
    commit = false; // Don't commit removes till all complete
    try {
      if (c instanceof RangeSet) {
        // Special optimized remove
        int index = 0;
        for (final Range range : (RangeSet) c) {
          index = remove(index, range);
        }
      } else {
        for (final Object o : c) {
          result &= remove(o);
        }
      }
      if (repository != null) {
        try {
          updateRanges();
        } catch (final RepositoryException e1) {
          e1.printStackTrace();
          return false;
        }
      }
      return result;
    } finally {
      commit = true;
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.List#retainAll(java.util.Collection)
   */
  @Override
  public final boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.List#set(int, java.lang.Object)
   */
  @Override
  public final Range set(int index, Range element) {
    throw new UnsupportedOperationException();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.List#size()
   */
  @Override
  public final int size() {
    return ranges.size();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.List#subList(int, int)
   */
  @Override
  public final List<Range> subList(int fromIndex, int toIndex) {
    return ranges.subList(fromIndex, toIndex);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.List#toArray()
   */
  @Override
  public final Object[] toArray() {
    return ranges.toArray();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.List#toArray(T[])
   */
  @Override
  public final <T> T[] toArray(T[] a) {
    return ranges.toArray(a);
  }
  
  /**
   * Write any changes to the list to the repository.
   * 
   * @throws RepositoryException
   *           
   */
  private final void updateRanges() throws RepositoryException {
    if (repository == null) {
      return;
    }
    
    final RepositoryConnection con = repository.getConnection();
    
    // Keep in mind the RangeSet includes the range between UID and lastUID.
    // Keep the stored data minimal by not reproducing that data if not
    // required.
    // Get the natural range
    if (ranges.size() == 1) {
      final int uid = getUID(con, subject);
      // In some cases an element does not have ranges.
      if (uid >= 0) {
        final int lastUID = getLastUID(con, subject);
        final Range range = new Range(uid, lastUID);
        // If these ranges are equal, then remove the range from the
        // repository,
        // or just don't write it.
        if (range.equals(ranges.get(0))) {
          if (con.hasStatement(subject, WorkbenchPredicate.WORKBENCH_RANGES.toURI(), null, false)) {
            con.remove(subject, WorkbenchPredicate.WORKBENCH_RANGES.toURI(), null);
          }
          return;
        }
      }
    }
    
    try {
      con.add(subject, WorkbenchPredicate.WORKBENCH_RANGES.toURI(), con.getValueFactory().createLiteral(toString()));
    } finally {
      if (con != null) {
        con.close();
      }
    }
  }
  
  /**
   * Return the string in a format suitable for writing to the repository.
   * 
   */
  @Override
  public final String toString() {
    // Update the ranges in the repository
    final Iterator<Range> it = ranges.iterator();
    // Record the rest
    final StringBuilder sb = new StringBuilder();
    while (it.hasNext()) {
      final Range range = it.next();
      sb.append(range.from + "-" + range.to);
      if (it.hasNext()) {
        sb.append(",");
      }
    }
    return sb.toString();
  }
  
  /**
   * Return the inverted range. Do not edit this range set.
   * 
   * @return
   */
  public final RangeSet getInvertedRange() {
    final Range range = new Range(0, INFINITY);
    final RangeSet results = new RangeSet(range);
    results.removeAll(this);
    return results;
  }
  
  /**
   * 
   * @param offset
   */
  public final void offsetAll(long offset) {
    for (final Range r : ranges) {
      r.to += offset;
      r.from += offset;
    }
  }
  
  /**
   * Determine if the two ranges intersect
   * 
   * @param test
   * @return
   */
  public boolean intersects(RangeSet test) {
    for (final Range range : ranges) {
      for (final Range testRange : test) {
        if (range.intersects(testRange)) {
          return true;
        }
      }
    }
    return false;
  }
  
  /**
   * Return the full intersection between the two ranges
   * 
   * FIXME: Not efficient.
   * 
   * @param elementRanges
   * @return
   */
  public RangeSet getIntersection(RangeSet test) {
    RangeSet results = new RangeSet();
    
    for (final Range range : ranges) {
      for (final Range testRange : test) {
        if (range.intersects(testRange)) {
          results.add(range.getIntersection(testRange));
        }
      }
    }
    return results;
  }
  
  /**
   * Validate the contained ranges to ensure this RangeSet is correct. This is only used for testing
   * purposes.
   * 
   * Ensure none of the internal ranges overlap
   * 
   */
  public boolean isValid() {
    for (final Range range : ranges) {
      for (final Range testRange : ranges) {
        // Of course ranges overlap themselves
        if (range == testRange) {
          continue;
        }
        
        // But they should not overlap others
        if (range.intersects(testRange)) {
          return false;
        }
      }
    }
    return true;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof RangeSet)) {
      return false;
    }
    
    final RangeSet oSet = (RangeSet) o;
    if (oSet.size() != size()) {
      return false;
    }
    
    for (int i = size() - 1; i >= 0; --i) {
      if (!get(i).equals(oSet.get(i))) {
        return false;
      }
    }
    
    return true;
  }
  
  /**
   * Range X completely contains range Y if and only if: * X intersects with Y AND * The inverse of
   * X does not intersect with Y
   * 
   * @param linkRange
   * @return
   */
  public boolean containsRanges(RangeSet ranges) {
    if (!intersects(ranges)) {
      return false;
    }
    final RangeSet inv = getInvertedRange();
    if (inv.intersects(ranges)) {
      return false;
    }
    return true;
  }
  
  /**
   * Get the sum of the contained ranges sizes
   * 
   * @return
   */
  public int getCoverageSize() {
    int size = 0;
    for (final Range range : ranges) {
      size += range.getSize();
    }
    return size;
  }
}
