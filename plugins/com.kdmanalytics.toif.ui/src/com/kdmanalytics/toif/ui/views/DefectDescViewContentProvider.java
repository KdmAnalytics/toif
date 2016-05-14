package com.kdmanalytics.toif.ui.views;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.kdmanalytics.toif.ui.common.IFindingEntry;

/** Provide the contents for the defect description view
 * 
 * @author Ken Duck
 *
 */
public class DefectDescViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {
  private DefectNode root;
  private Map<String, String[]> sfpLookup;
  private Map<String, String[]> cweLookup;
  
  public DefectDescViewContentProvider(Map<String, String[]> sfpLookup, Map<String, String[]> cweLookup) {
    this.sfpLookup = sfpLookup;
    this.cweLookup = cweLookup;
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
   */
  @Override
  public void inputChanged(Viewer v, Object oldInput, Object newInput) {
    if (newInput instanceof IFindingEntry) {
      IFindingEntry data = (IFindingEntry) newInput;
      root = new DefectNode(DefectNode.ROOT_NODE, "ROOT");
      
      String sfp = data.getSfp();
      DefectNode clusterNode = null;
      if (sfpLookup.containsKey(sfp)) {
        // If we have a cluster then add the cluster node
        String[] sfpData = sfpLookup.get(sfp);
        if (sfpData.length > 2) {
          clusterNode = new DefectNode(DefectNode.CLUSTER_NODE, "Cluster", sfpData[2]);
          root.addChild(clusterNode);
          clusterNode.setParent(root);
        }
      }
      
      // If we do not have a cluster node then skip it.
      if (clusterNode == null) clusterNode = root;
      
      DefectNode sfpNode = null;
      if (sfpLookup.containsKey(sfp)) {
        String[] sfpData = sfpLookup.get(sfp);
        sfpNode = new DefectNode(DefectNode.SFP_NODE, sfp, sfpData[1]);
      } else {
        sfpNode = new DefectNode(DefectNode.SFP_NODE, sfp, "Unmapped");
      }
      clusterNode.addChild(sfpNode);
      sfpNode.setParent(clusterNode);
      
      addCweNodes(sfpNode, data);
    }
  }
  
  /** Add all of the nodes that appears under the SFP. These will be
   * several related nodes.
   * 
   * @param sfpNode
   * @param data
   */
  private void addCweNodes(DefectNode sfpNode, IFindingEntry data) {
    
    // CWE node itself
    String cwe = data.getCwe();
    DefectNode cweNode = null;
    if (cweLookup.containsKey(cwe)) {
      String[] cweData = cweLookup.get(cwe);
      cweNode = new DefectNode(DefectNode.CWE_NODE, cwe, cweData[1]);
      sfpNode.addChild(cweNode);
      cweNode.setParent(sfpNode);
      
      // If there is a CWE description, then add it in next
      if (cweData.length > 2) {
        DefectNode descNode = new DefectNode(DefectNode.DESCRIPTION_NODE, "Description", cweData[2]);
        sfpNode.addChild(descNode);
        descNode.setParent(sfpNode);
      }
    } else {
      cweNode = new DefectNode(DefectNode.CWE_NODE, cwe, "Unmapped");
      sfpNode.addChild(cweNode);
      cweNode.setParent(sfpNode);
    }
    
    // Description node
    String description = data.getDescription();
    if(description != null && !description.trim().isEmpty()) {
      DefectNode descriptionNode = new DefectNode(DefectNode.DESCRIPTION_NODE, "Description", data.getDescription());
      sfpNode.addChild(descriptionNode);
      descriptionNode.setParent(sfpNode);
    }
    
    // More info
    try {
      URL url = new URL("http://cwe.mitre.org/data/definitions/" + cwe.replace("CWE-", "") + ".html");
      DefectNode moreInfoNode = new DefectNode(DefectNode.DESCRIPTION_NODE, "More info", url.toString());
      sfpNode.addChild(moreInfoNode);
      moreInfoNode.setParent(sfpNode);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    
  }
  
  public void dispose() {
  }
  
  public Object[] getElements(Object parent) {
    if (parent instanceof IFindingEntry) {
      return getChildren(root);
    }
    return getChildren(parent);
  }
  
  public Object getParent(Object child) {
    if (child instanceof DefectNode) {
      return ((DefectNode) child).getParent();
    }
    return null;
  }
  
  public Object[] getChildren(Object parent) {
    if (parent instanceof DefectNode) {
      return ((DefectNode) parent).getChildren();
    }
    return new Object[0];
  }
  
  public boolean hasChildren(Object parent) {
    if (parent instanceof DefectNode) {
      return ((DefectNode) parent).hasChildren();
    }
    return false;
  }
  
}
