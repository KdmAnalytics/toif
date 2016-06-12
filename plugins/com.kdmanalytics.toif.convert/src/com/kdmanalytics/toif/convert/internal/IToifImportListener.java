package com.kdmanalytics.toif.convert.internal;

import com.kdmanalytics.toif.report.items.IFindingEntry;

public interface IToifImportListener {

  /** A new finding has been imported
   * 
   * @param finding
   */
  void add(IFindingEntry finding);

  /** Import events allow the tracking of import progress
   * 
   * @param toifImportEvent
   */
  void event(ToifImportEvent toifImportEvent);
  
}
