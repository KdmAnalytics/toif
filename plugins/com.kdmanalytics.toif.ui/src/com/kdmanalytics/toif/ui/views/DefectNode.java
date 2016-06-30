
package com.kdmanalytics.toif.ui.views;

import java.util.ArrayList;
import java.util.List;

public class DefectNode {
  
  
  static final int CWE_NODE = 1;
  
  static final int SFP_NODE = 2;
  
  static final int CLUSTER_NODE = 3;
  
  static final int ROOT_NODE = 4;
  
  static final int DESCRIPTION_NODE = 5;
  
  private int type;
  
  private String name;
  
  private String description;
  
  private DefectNode parent;
  
  private List<DefectNode> children = new ArrayList<DefectNode>();
  
  public DefectNode(int type, String name) {
    this.type = type;
    this.name = name;
  }
  
  public DefectNode(int type, String name, String description) {
    this.type = type;
    this.name = name;
    this.description = description;
  }
  
  public int getType() {
    return type;
  }
  
  public String getName() {
    return name;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setParent(DefectNode parent) {
    this.parent = parent;
  }
  
  public DefectNode getParent() {
    return parent;
  }
  
  public void addChild(DefectNode child) {
    children.add(child);
    child.setParent(this);
  }
  
  public void removeChild(DefectNode child) {
    children.remove(child);
    child.setParent(null);
  }
  
  public DefectNode[] getChildren() {
    return (DefectNode[]) children.toArray(new DefectNode[children.size()]);
  }
  
  public boolean hasChildren() {
    return children.size() > 0;
  }
  
  public String toString() {
    return getName();
  }
}
