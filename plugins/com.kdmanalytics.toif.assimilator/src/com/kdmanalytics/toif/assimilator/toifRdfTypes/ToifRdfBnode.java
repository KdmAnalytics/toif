/**
 * 
 */

package com.kdmanalytics.toif.assimilator.toifRdfTypes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.BNode;

/**
 * class representing an anonymous bnode.
 * 
 * @author adam
 *         
 */
public class ToifRdfBnode extends ToifRdfResource {
  
  private BNode object;
  
  /**
   * create a toif bnode object.
   * 
   * @param createBNode
   *          the rdf bnode
   * @param currentFile
   *          the current file.
   */
  public ToifRdfBnode(BNode bnode, File currentFile) {
    
    this.file = currentFile;
    this.object = bnode;
  }
  
  /**
   * get the bnode.
   * 
   * @return the object
   */
  public BNode getObject() {
    return object;
  }
  
  /**
   * get the details for this object.
   */
  @Override
  public List<String> getDetails() {
    List<String> list = new ArrayList<String>();
    list.add(object.toString());
    return list;
  }
}
