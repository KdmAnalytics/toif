/**
 * 
 */

package com.kdmanalytics.toif.assimilator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.kdmanalytics.toif.assimilator.FilePathTrie.Node;

/**
 * @author adam
 *         
 */
public class FilePathTreeTest {
  
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();
  
  private File treeFile;
  
  private FilePathTrie tree;
  
  @Before
  public void before() throws Exception {
    treeFile = folder.newFile("file");
    tree = new FilePathTrie(treeFile.getName());
  }
  
  /**
   * Test method for
   * {@link com.kdmanalytics.toif.assimilator.FilePathTrie#FilePathTree(java.lang.String)} .
   */
  @Test
  public void testFilePathTree() {
    assertTrue(tree.getFile().getName().equals(treeFile.getName()));
  }
  
  /**
   * Test method for
   * {@link com.kdmanalytics.toif.assimilator.FilePathTrie#FilePathTree(java.lang.String)} .
   */
  @Test(expected = NullPointerException.class)
  public void testFilePathTreeNull() {
    new FilePathTrie(null);
  }
  
  /**
   * Test method for {@link com.kdmanalytics.toif.assimilator.FilePathTrie#getFile()}.
   */
  @Test
  public void testGetFile() {
    assertTrue(tree.getFile().getName().equals(treeFile.getName()));
  }
  
  /**
   * Test method for
   * {@link com.kdmanalytics.toif.assimilator.FilePathTrie#addParentDirectory(com.kdmanalytics.toif.assimilator.FilePathTrie.Node, java.lang.String)}
   * .
   */
  @Test
  public void testAddParentDirectory() {
    String directoryName = "dir";
    Node dir = tree.addParentDirectory(tree.getFile(), directoryName);
    assertTrue(dir.getName().equals(directoryName));
  }
  
  @Test
  public void testNodeEqualsPass() {
    Node node1 = tree.new Node("node");
    Node node2 = tree.new Node("node");
    
    assertTrue(node1.equals(node2));
    
  }
  
  @Test
  public void testNodeEqualsFail() {
    Node node1 = tree.new Node("blah");
    Node node2 = tree.new Node("foo");
    
    assertFalse(node1.equals(node2));
    
  }
  
  @Test
  public void testNodeEqualsNullPass() {
    FilePathTrie tree = new FilePathTrie(treeFile.getName());
    Node node1 = tree.new Node(null);
    Node node2 = tree.new Node(null);
    
    assertTrue(node1.equals(node2));
    
  }
  
  @Test
  public void testNodeEqualsNullFail() {
    FilePathTrie tree = new FilePathTrie(treeFile.getName());
    Node node1 = tree.new Node("foo");
    Node node2 = tree.new Node(null);
    
    assertFalse(node1.equals(node2));
    
  }
  
  @Test
  public void getBestPath() {
    // create tree
    Node file = tree.getFile();
    String dir1 = "dir1";
    tree.addParentDirectory(file, dir1);
    String dir2 = "dir2";
    tree.addParentDirectory(file.getParent(dir1), dir2);
    
    // a little miss-direction.
    String dir3 = "dir3";
    tree.addParentDirectory(file, dir3);
    
    // create the path that we are looking for.
    List<String> path = new ArrayList<String>();
    path.add("file");
    path.add(dir1);
    path.add(dir2);
    
    List<String> bestPath = tree.getBestPath(path);
    assertEquals(path, bestPath);
  }
  
  @Test
  public void getPartialBestPath() {
    // create tree
    Node file = tree.getFile();
    String dir1 = "dir1";
    tree.addParentDirectory(file, dir1);
    tree.addParentDirectory(file.getParent(dir1), "foo");
    
    // a little miss-direction.
    String dir3 = "dir3";
    tree.addParentDirectory(file, dir3);
    
    // create the path that we are looking for.
    List<String> path = new ArrayList<String>();
    path.add("file");
    path.add(dir1);
    
    List<String> bestPath = tree.getBestPath(path);
    assertEquals(path, bestPath);
  }
}
