/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.views.sort;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import com.kdmanalytics.toif.ui.common.AdaptorConfiguration;
import com.kdmanalytics.toif.ui.common.FindingGroup;
import com.kdmanalytics.toif.ui.common.IFindingEntry;
import com.kdmanalytics.toif.ui.views.FindingView;

/**
 * Provides column sorting.
 * 
 * Derived from ReportViewerComparator in design
 * 
 * @author Ken Duck
 *        
 */
public class FindingViewColumnComparator extends ViewerComparator implements Comparator<IFindingEntry> {
  
  /**
   * Required for getting column positions
   */
  private AdaptorConfiguration config = AdaptorConfiguration.getAdaptorConfiguration();
  
  /** The column index. */
  private int columnIndex;
  
  /** The Constant DESCENDING. */
  private static final int DESCENDING = 1;
  
  private static final int ASCENDING = 0;
  
  /** The direction. */
  private int direction = DESCENDING;
  
  /**
   * Cache the index numbers for the extra columns
   */
  private List<Integer> extraColumnIndices = new LinkedList<Integer>();
  
  /**
   * Instantiates a new report viewer comparator.
   */
  public FindingViewColumnComparator() {
    this.columnIndex = 0;
    direction = ASCENDING;
    
    
    String[] names = config.getExtraColumnNames();
    for (String name : names) {
      int index = config.getColumnIndex(name);
      extraColumnIndices.add(index);
    }
  }
  
  /**
   * Gets the direction.
   * 
   * @return the direction
   */
  public int getDirection() {
    return direction == 1 ? SWT.DOWN : SWT.UP;
  }
  
  /**
   * Sets the column.
   * 
   * @param column
   *          the new column
   */
  public void setColumn(int column) {
    if (column == this.columnIndex) {
      // Same column as last sort; toggle the direction
      direction = 1 - direction;
    } else {
      // New column; do an ascending sort
      this.columnIndex = column;
      direction = DESCENDING;
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface. viewers.Viewer,
   * java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(Viewer viewer, Object e1, Object e2) {
    IFindingEntry entry1 = (IFindingEntry) e1;
    IFindingEntry entry2 = (IFindingEntry) e2;
    
    return compare(entry1, entry2);
  }
  
  /*
   * (non-Javadoc)
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(IFindingEntry entry1, IFindingEntry entry2) {
    Integer result = null;
    
    switch (columnIndex) {
      case FindingView.FILE_COLUMN: {
        String file1 = entry1.getFileName();
        String file2 = entry2.getFileName();
        result = file1.compareTo(file2);
        break;
      }
      case FindingView.LOCATION_COLUMN: {
        int line1 = 0;
        try {
          line1 = Integer.parseInt(entry1.getLine());
        } catch (NumberFormatException e) {}
        int line2 = 0;
        try {
          line2 = Integer.parseInt(entry2.getLine());
        } catch (NumberFormatException e) {}
        
        if (line1 == line2) {
          // If the lines are the same, order by name as well
          IFile file1 = entry1.getFile();
          IFile file2 = entry2.getFile();
          result = file1.getName().split(" ")[0].compareTo(file2.getName().split(" ")[0]);
        } else {
          result = line1 - line2;
        }
        break;
      }
      case FindingView.TOOL_COLUMN: {
        String tool1 = entry1.getTool();
        String tool2 = entry2.getTool();
        result = tool1.compareTo(tool2);
        break;
      }
      case FindingView.SFP_COLUMN: {
        // Remove old-style prefix
        String sfp1 = entry1.getSfp().replace("SFP-", "");
        String sfp2 = entry2.getSfp().replace("SFP-", "");
        // Remove new-style prefix
        sfp1 = sfp1.replace("SFP", "");
        sfp2 = sfp2.replace("SFP", "");
        int sfp1Int = 0;
        int sfp2Int = 0;
        
        try {
          sfp1Int = Integer.parseInt(sfp1);
        } catch (NumberFormatException nfe) {
          sfp1Int = -1;
        }
        
        try {
          sfp2Int = Integer.parseInt(sfp2);
        } catch (NumberFormatException nfe) {
          sfp2Int = -1;
        }
        
        result = sfp1Int - sfp2Int;
        break;
      }
      
      // Sort CWE column
      case FindingView.CWE_COLUMN: {
        String cwe1;
        String cwe2;
        
        
        // Sorting for this column will defer to group if defined
        Optional<FindingGroup> group1 = Optional.empty();
        Optional<FindingGroup> group2 = Optional.empty();
        if (entry1 instanceof FindingGroup)
        	cwe1 = ((FindingGroup)entry1).getCweDisplay();
        else
			{
			group1 = entry1.group();
			if (group1.isPresent())
			  	{
			  	cwe1 = group1.get().getCweDisplay();
			  	System.out.println( "CWE1 display="+cwe1);
			  	}
			  else
			  	{
			  	cwe1 = entry1.getCwe();
			  	System.out.println( "CWE1 NOT display="+cwe1);
			    }
        	}
      
      
        if (entry2 instanceof FindingGroup)
        	cwe2 = ((FindingGroup)entry2).getCweDisplay();
        else
        	{
	        group2 = entry2.group();
	        if (group2.isPresent())
	        	cwe2 = group2.get().getCweDisplay();
	        else
	        	cwe2 = entry2.getCwe();
        	}
      
        // Remove old-style prefix
        cwe1 = cwe1.replace("CWE-", "").trim();
        cwe2 = cwe2.replace("CWE-", "").trim();
        
        // Remove new-style prefix
        cwe1 = cwe1.replace("CWE", "");
        cwe2 = cwe2.replace("CWE", "");
      
        if (cwe1.contains("*"))
        	cwe1 = "0";
        
        if (cwe2.contains("*"))
        	cwe2 = "0";


        result = cwe1.compareToIgnoreCase(cwe2);
        
        // Check if we equal due to being in the same group
        if (result == 0)
        	{
        	// Yes, sort on entry CWE
        	if (group1.isPresent() && group2.isPresent())
        		if (group1.get().equals(group2.get()))
        			result = entry1.getCwe().compareToIgnoreCase(entry2.getCwe());		
        	}
        
        break;
      }
      case FindingView.CONFIDENCE_COLUMN: {
        int cwe1Int = entry1.getTrust();
        int cwe2Int = entry2.getTrust();
        result = cwe1Int - cwe2Int;
        break;
      }
      case FindingView.DESCRIPTION_COLUMN: {
        String desc1 = entry1.getDescription();
        String desc2 = entry2.getDescription();
        result = desc1.compareTo(desc2);
        break;
      }
      
      // Remaining columns are defined by the config
      default: {
        int index = columnIndex - 7;
        if (index < extraColumnIndices.size()) {
          // Get the config index matching this column
          index = extraColumnIndices.get(index);
          String cwe1 = entry1.getCwe();
          String value1 = (String)config.getCell(cwe1, index);
          String cwe2 = entry2.getCwe();
          String value2 = (String)config.getCell(cwe2, index);
          
          // Should we use numeric compare
          try {
            Long int1 = (Long.valueOf(value1));
            Long int2 = (Long.valueOf(value2));
            result = int1.compareTo(int2);
          } catch (NumberFormatException e) {}
          if (result == null) {
            try {
              Double d1 = (Double.valueOf(value1));
              Double d2 = (Double.valueOf(value2));
              result = d1.compareTo(d2);
            } catch (NumberFormatException e) {}
          }
          if (result == null) {
            result = value1.compareTo(value2);
            break;
          }
        }
      }
    }
    
    // If descending order, flip the direction
    if (direction == DESCENDING) {
      result = -result;
    }
    
    return result;
  }
}
