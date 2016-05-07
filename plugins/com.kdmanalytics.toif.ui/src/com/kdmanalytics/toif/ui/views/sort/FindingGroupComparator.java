package com.kdmanalytics.toif.ui.views.sort;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import com.kdmanalytics.toif.ui.common.AdaptorConfiguration;
import com.kdmanalytics.toif.ui.common.FindingGroup;
import com.kdmanalytics.toif.ui.common.IFindingEntry;

/** Comparator that uses the AdaptorConfiguration file order:
 * 
 *     1. Number of tools defining defects on same file/line 
 *     2. Calculated weighting (see REQ 9.x for details) 
 *     3. Confidence 
 *     4. File 
 *     5. Line
 * 
 * @author Ken Duck
 *
 */
public class FindingGroupComparator extends ViewerComparator {
  /**
   * Use the adaptor configuration for ordering
   */
  private AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(Viewer viewer, Object e1, Object e2) {
    IFindingEntry entry1 = (IFindingEntry) e1;
    IFindingEntry entry2 = (IFindingEntry) e2;
    
    String cwe1 = entry1.getCwe();
    String cwe2 = entry2.getCwe();
    
    // Primary sort: Number of tools defining defects on same file/line
    if (entry1 instanceof FindingGroup) {
      if (entry2 instanceof FindingGroup) {
        // As long as there are duplicates we consider them the same. The *number* of
        // duplicates is inconsequential.
      } else {
        return -1;
      }
    } else if (entry2 instanceof FindingGroup) {
      return 1;
    }
    
    // Secondary sort: Calculated weighting
    int i1 = config.getIndex(cwe1);
    int i2 = config.getIndex(cwe2);
    int diff = i1 - i2;
    if (diff != 0) return diff;
    
    // Tertiary sort: Confidence
    i1 = entry1.getTrust();
    i2 = entry2.getTrust();
    diff = i1 - i2;
    if (diff != 0) return diff;
    
    // Quaternary sort: File
    IFile if1 = entry1.getFile();
    IFile if2 = entry2.getFile();
    IPath p1 = if1.getFullPath();
    IPath p2 = if2.getFullPath();
    File f1 = p1.toFile();
    File f2 = p2.toFile();
    diff = f1.compareTo(f2);
    if (diff != 0) return diff;
    
    // Quinary sort: Line
    i1 = entry1.getLineNumber();
    i2 = entry2.getLineNumber();
    diff = i1 - i2;
    if (diff != 0) return diff;
    
    // Otherwise whatever
    return 0;
  }
}
