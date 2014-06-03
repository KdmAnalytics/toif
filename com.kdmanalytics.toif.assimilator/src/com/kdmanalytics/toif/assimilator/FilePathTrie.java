/**
 * 
 */

package com.kdmanalytics.toif.assimilator;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Trie representing the file paths.
 * 
 * @author adam
 * 
 */
public class FilePathTrie
{
    
    /**
     * The root node. this will always be the file name.
     */
    private Node file;
    
    /**
     * Create a new tree with the fileName as the roots information.
     * 
     * @param fileName
     *            the name to give the root.
     */
    public FilePathTrie(String fileName)
    {
        checkNotNull(fileName);
        this.file = new Node(fileName);
        
    }
    
    /**
     * find the best fitting path
     * 
     * @param path
     *            the path for which to find the best fit for.
     * @return the resulting best fit path.
     */
    public List<String> getBestPath(List<String> path)
    {
        List<String> resultPath = new ArrayList<String>();
        Node node = null;
        
        // null paths get an empty list
        if (path == null)
        {
            return resultPath;
        }
        // empty paths also get an empty list.
        else if (path.isEmpty())
        {
            return resultPath;
        }
        else
        {
            // the name of the file (the root of this tree).
            String rootFile = file.getName();
            
            // if the file names dont match, then return an empty list.
            if (!path.get(0).equals(rootFile))
            {
                return resultPath;
            }
            // if the files do match then set the first node as the file,
            // because we're gonna start here!
            else
            {
                node = file;
                resultPath.add(rootFile);
            }
        }
        
        // for each of the directories in the path that we are trying to find.
        // notice that we are starting from 1. this is the first directory.
        for (int i = 1; i < path.size(); i++)
        {
            String directory = path.get(i);
            
            // if the file has a parent that matches this directory, add it to
            // the results and use it as the next file to search from.
            if (node.hasParent(directory))
            {
                // move to the next directory.
                node = node.getParent(directory);
                // add it to the results.
                resultPath.add(directory);
            }
            // if there is no match for the next directory, return with what we
            // have.
            else
            {
                return resultPath;
            }
            
        }
        
        return resultPath;
    }
    
    /**
     * returns the root file.
     * 
     * @return the root
     */
    public Node getFile()
    {
        return file;
    }
    
    /**
     * Add a parent-directory to the directory. Remember that we are working
     * from the file, up the directories.
     * 
     * @param dir
     *            The directory that we want to add the parent to.
     * @param parentDirectory
     *            The parent of dir that we want to add to the path.
     * @return The parent, as a node, which we've just added to the path.
     */
    public Node addParentDirectory(Node dir, String parentDirectory)
    {
        Node parent = new Node(parentDirectory);
        dir.addParentDirectory(parent);
        return parent;
    }
    
    /**
     * The node for each of the directories/file in the tree.
     * 
     * @author adam
     * 
     */
    public class Node
    {
        
        /**
         * the name of this node. This will most likely be the directory name.
         */
        private String name;
        
        /**
         * A set of the nodes children.
         */
        private List<Node> parents = new ArrayList<Node>();
        
        /**
         * Create a new node which represents a directory/file.
         * 
         * @param name
         *            the name of the directory or file.
         */
        public Node(String name)
        {
            this.name = name;
        }
        
        /**
         * Add a parent to this directory. It sounds count-intuitive that this
         * directory might have many parents. However, we must remember that it
         * is not a directory but a directory-name. This means that there may be
         * more then one directory with the same name, but they are contained in
         * different directories.
         * 
         * @param node
         */
        public void addParentDirectory(Node node)
        {
            if (!hasParent(node.getName()))
            {
                parents.add(node);
            }
        }
        
        /**
         * check to see if this node has a perent with the given name.
         * 
         * @param parentName
         *            the name to search for in the set of parents.
         * @return true if a parent is found with this name.
         */
        public boolean hasParent(String parentName)
        {
            return parentsContains(parentName);
        }
        
        /**
         * is there a node within this node with the name ...
         * 
         * @param parentName
         *            the name to try to find.
         * @return returns true if a node with the same name as parent name.
         */
        private boolean parentsContains(String parentName)
        {
            for (Node node : parents)
            {
                if (parentName.equals(node.getName()))
                {
                    return true;
                }
            }
            return false;
        }
        
        /**
         * get the parent from this file's parents which has the same name as
         * parentName
         * 
         * @param parentName
         *            the name of the parent that we are trying to find.
         * @return the matching parent node. null if none is found.
         */
        public Node getParent(String parentName)
        {
            Iterator<Node> nodes = parents.iterator();
            
            while (nodes.hasNext())
            {
                FilePathTrie.Node node = (FilePathTrie.Node) nodes.next();
                
                if (parentName.equals(node.getName()))
                {
                    return node;
                }
                
            }
            
            return null;
        }
        
        /**
         * Get the name of this node.
         * 
         * @return the name The string name of this node.
         */
        public String getName()
        {
            return name;
        }
        
        /**
         * set the name of this node.
         * 
         * @param name
         *            the name to set
         */
        public void setName(String name)
        {
            this.name = name;
        }
        
        public int hashCode()
        {
            return name.hashCode();
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (!(obj instanceof Node) && !(obj instanceof String))
            {
                return false;
            }
            
            String name2 = null;
            if (obj instanceof Node)
            {
                Node other = (Node) obj;
                
                name2 = other.name;
            }
            
            if (obj instanceof String)
            {
                name2 = (String) obj;
            }
            
            if (name == null)
            {
                if (name2 != null)
                {
                    return false;
                }
            }
            else if (!name.equals(name2))
            {
                return false;
            }
            return true;
        }
        
    }
}
