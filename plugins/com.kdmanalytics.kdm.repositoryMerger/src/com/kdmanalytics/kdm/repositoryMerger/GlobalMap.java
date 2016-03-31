
package com.kdmanalytics.kdm.repositoryMerger;

import gnu.trove.map.hash.THashMap;

import java.util.Map;

import org.openrdf.model.URI;
import org.openrdf.model.Value;

import com.kdmanalytics.kdm.repositoryMerger.Utilities.MergerURI;

/**
 * The GlobalMap is used to store global information for the link/merge It is not complete KDM, but
 * just a subset of data needed for the merge.
 * 
 */
public class GlobalMap {
  
  /**
   * 
   */
  public static final int ROOTID = 0;
  
  /**
   * 
   */
  private URI root = null;
  
  private Map<URI, Map<Value, URI>> map = null;
  
  /**
   * 
   */
  public GlobalMap() {
    map = new THashMap<URI, Map<Value, URI>>();
    root = new MergerURI(MergerURI.KdmModelNS, "" + ROOTID);
    // root = SimpleValueFactory.getSimpleValueFactory().createURI(RDFInterface.modelNS, ""+ROOTID);
    Map<Value, URI> values = new THashMap<Value, URI>();
    map.put(root, values);
  }
  
  /**
   * 
   * @return
   */
  public URI getRoot() {
    return root;
  }
  
  /**
   * 
   * @param globalNode
   * @param id
   * @return
   */
  public boolean containsId(URI parentURI, Value id) {
    if (!map.containsKey(parentURI)) return false;
    Map<Value, URI> values = map.get(parentURI);
    if (values.containsKey(id)) return true;
    return false;
  }
  
  /**
   * Add the child with the given id and globalID to the tree
   * 
   * @param parentURI
   * @param childID
   * @param childURI
   */
  public void add(URI parentURI, Value childID, URI childURI) {
    Map<Value, URI> values = map.get(parentURI);
    if (values == null) {
      values = new THashMap<Value, URI>();
      map.put(parentURI, values);
    }
    values.put(childID, childURI);
  }
  
  /**
   * 
   * @param parentURI
   * @param id
   * @return
   */
  public URI get(URI parentURI, Value id) {
    if (!map.containsKey(parentURI)) return null;
    Map<Value, URI> values = map.get(parentURI);
    return values.get(id);
  }
}
