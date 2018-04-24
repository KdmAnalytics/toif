/**
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved.
 */
package com.kdmanalytics.toif.ui.common.preferences;

import java.util.List;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;

import com.kdmanalytics.toif.ui.common.AdaptorConfiguration;

/** Labels for the adaptor configuration preference table
 * 
 * @author Ken Duck
 *
 */
public class AConfigStyledLabelProvider extends StyledCellLabelProvider {
  
  /**
   * Config file, used for figuring out which column is which
   */
//  private AdaptorConfiguration config;
  
  private int cweIndex;
  private int sfpIndex;
  
  public AConfigStyledLabelProvider(AdaptorConfiguration config) {
//    this.config = config;
    
    sfpIndex = config.getSfpColumnIndex();
    cweIndex = config.getCweColumnIndex();
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
   */
  @Override
  public void update(final ViewerCell cell) {
    @SuppressWarnings("unchecked")
    final List<Object> entry = (List<Object>) cell.getElement();
    final StyledString styledString = new StyledString(getColumnText(entry, cell.getColumnIndex()));
    
    cell.setText(styledString.toString());
    cell.setStyleRanges(styledString.getStyleRanges());
    //    cell.setImage(getImage(entry, cell.getColumnIndex()));
  }
  
  /** Get appropriately styled text for the given column
   * 
   * FIXME: Fix the SFP and CWE identifiers (remove the '-')
   * 
   * @param entry
   * @param index
   * @return
   */
  private String getColumnText(List<Object> entry, int index) {
    String text = null;
    if (index < entry.size()) {
      Object o = entry.get(index);
      if (o != null) {
        text = o.toString();
      } else {
        text = "";
      }
      if(index == sfpIndex || index == cweIndex) {
        text = fixSfpCweIdentifier(text);
      }
    } else {
      text = "";
    }
    return text;
  }
  
  /** CWE and SFP identifiers should not have single hyphens in them.
   * 
   * @param name
   * @return
   */
  private String fixSfpCweIdentifier(String name) {
    return name.replaceAll("([^-])-([^-])", "$1$2");
  }
  
}
