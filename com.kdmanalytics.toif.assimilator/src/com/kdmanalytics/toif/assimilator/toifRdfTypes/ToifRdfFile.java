/**
 * 
 */

package com.kdmanalytics.toif.assimilator.toifRdfTypes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class representing a toif rdf file/directory.
 * 
 * @author adam
 * 
 */
public class ToifRdfFile extends ToifRdfResource
{
    
    /**
     * directory structure of the file
     */
    private String directoryStructure = "";
    
    /**
     * the next file/directory in the path
     */
    private ToifRdfFile nextFile;
    
    /**
     * create a new rdf file/directory
     * 
     * @param subjectElement
     * @param currentFile
     */
    public ToifRdfFile(Element resourceElement, File currentFile)
    {
        super(resourceElement, currentFile);
    }
    
    /**
     * get the details for this node
     * 
     * @return the details
     */
    public List<String> getDetails()
    {
        String dirCopy = directoryStructure;
        
        // trim the last directory off so that the path is the same as the name
        // of the directory (not going down to the child.)
        if ("toif:Directory".equals(getType()) && !dirCopy.equals(""))
        {
            
            // correct any windows mojo that might occur.
            final int lastIndexOf = dirCopy.lastIndexOf("/") == -1 ? dirCopy.lastIndexOf("\\") : dirCopy.lastIndexOf("/");
            
            dirCopy = dirCopy.substring(0, lastIndexOf);
        }
        
        List<String> result = new ArrayList<String>(details);
        result.add(dirCopy);
        return result;
    }
    
    /**
     * sets the child of this element.
     * 
     * @param toifFile
     */
    public void setNextNode(ToifRdfFile toifFile)
    {
        nextFile = toifFile;
        String name = getFileName(toifFile);
        
        if (name != null)
        {
            propagateFile(name);
        }
    }
    
    private void propagateFile(String name)
    {
        // sandwich the name between a root and the existing directory.
        directoryStructure = "/" + name.concat(directoryStructure);
        
        if (nextFile != null)
        {
            nextFile.propagateFile(name);
        }
    }
    
    /**
     * get the file name for the file
     * 
     * @param toifNode
     *            the node for which to get the name
     * @return the file name
     */
    public String getFileName(ToifRdfFile toifNode)
    {
        
        Element element = toifNode.getElement();
        NodeList list = element.getChildNodes();
        String name = null;
        
        for (int i = 0; i < list.getLength(); i++)
        {
            Node child = list.item(i);
            
            if ("name".equals(child.getNodeName()))
            {
                name = child.getAttributes().getNamedItem("name").getTextContent();
                break;
            }
            
        }
        
        if (name == null)
        {
            return null;
        }
        
        return name;
        
    }
    
    /**
     * get the directory structure.
     * 
     * @param projectRoot
     *            the root name of the project
     * @return the directoryStructure the directory structure of the file.
     *         (shortened if the project root has been set.)
     */
    public String getDirectoryStructure(String projectRoot)
    {
        String dirCopy = directoryStructure;
        
        // trim the last directory off so that the path is the same as the name
        // of the directory (not going down to the child.)
        if ("toif:Directory".equals(getType()) && !dirCopy.equals(""))
        {
            
            // correct any windows mojo that might occur.
            final int lastIndexOf = dirCopy.lastIndexOf("/") == -1 ? dirCopy.lastIndexOf("\\") : dirCopy.lastIndexOf("/");
            
            dirCopy = dirCopy.substring(0, lastIndexOf);
        }
        
        if (projectRoot != null && !projectRoot.isEmpty())
        {
            int start = dirCopy.indexOf(projectRoot);
            dirCopy = dirCopy.substring(start);
        }
        
        return dirCopy;
    }
    
    /**
     * set the directory structure.
     * 
     * @param name
     */
    public void setDirectoryStructure(String name)
    {
        directoryStructure = "/" + name;
    }
    
    /**
     * returns the name of this "file".
     * 
     * @return returns the name of this file.
     */
    public String getName()
    {
        for (String detail : getDetails())
        {
            if (detail.startsWith("name="))
            {
                String name = detail.replace("name=", "");
                name = name.replace("\"", "");
                return name;
            }
        }
        
        return "";
    }
    
}
