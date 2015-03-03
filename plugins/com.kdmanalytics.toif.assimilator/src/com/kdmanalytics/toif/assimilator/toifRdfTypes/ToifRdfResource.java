/**
 * 
 */

package com.kdmanalytics.toif.assimilator.toifRdfTypes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openrdf.model.ValueFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * represents the toif resource.
 * 
 * @author adam
 * 
 */
public class ToifRdfResource
{
    
    /**
     * the rdf element
     */
    protected Element element;
    
    /**
     * current file
     */
    protected File file;
    
    /**
     * resource id
     */
    protected String id;
    
    /**
     * list of details about this element. used for unique identification.
     */
    protected List<String> details = new ArrayList<String>();
    
    /**
     * the type of resource.
     */
    private String toifType;
    
    public ToifRdfResource()
    {
    }
    
    /**
     * create a new resource
     * 
     * @param element
     *            the rdf element this resource is based on.
     * @param currentFile
     *            the current file this element is in.
     */
    public ToifRdfResource(Element element, File currentFile)
    {
        this.element = element;
        file = currentFile;
        setDetails(extractElementDetails(element));
    }
    
    public String getType()
    {
        return toifType;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((details == null) ? 0 : details.hashCode());
        return result;
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
        if (!(obj instanceof ToifRdfResource))
        {
            return false;
        }
        ToifRdfResource other = (ToifRdfResource) obj;
        if (getDetails() == null)
        {
            if (other.getDetails() != null)
            {
                return false;
            }
        }
        else if (!getDetails().equals(other.getDetails()))
        {
            return false;
        }
        return true;
    }
    
    /**
     * get the details for this node
     * 
     * @return the details
     */
    public List<String> getDetails()
    {
        List<String> result = details;
        // result.add(directoryStructure);
        return result;
    }
    
    /**
     * set the details for this node.
     * 
     * @param details
     *            the details to set
     */
    public void setDetails(List<String> details)
    {
        this.details = details;
    }
    
    /**
     * @return the element
     */
    public Element getElement()
    {
        return element;
    }
    
    /**
     * gets the local id for this resource
     * 
     * @return the local id.
     */
    public Long getLocalId()
    {
        if (element == null)
        {
            return 0L;
        }
        // String id = element.getAttribute("id");
        return Long.parseLong(id);
    }
    
    /**
     * the file for this resource.
     * 
     * @return the file
     */
    public File getFile()
    {
        return file;
    }
    
    /**
     * set the element for this resource.
     * 
     * @param element
     *            the element to set
     */
    public void setElement(Element element)
    {
        this.element = element;
    }
    
    /**
     * set the file for this resource.
     * 
     * @param file
     *            the file to set
     */
    public void setFile(File file)
    {
        this.file = file;
    }
    
    /**
     * extract all the interesting data from this node. Put it in a list and
     * sort it so that other lists with the same data can be equal to it.
     * 
     * @param node
     *            the node that we are trying to extract the details from
     * @return return the sorted list of details.
     */
    protected List<String> extractElementDetails(Element node)
    {
        if (node == null)
        {
            return null;
        }
        
        final List<String> results = new ArrayList<String>();
        
        final NodeList children = node.getChildNodes();
        
        String type = node.getAttribute("xsi:type");
        id = node.getAttribute("id");
        
        if ((type.equals("toif:Finding")) || (type.equals("toif:Statement")))
        {
            
            // use the file path as an attribute to make it unique.
            String absolutePath = file.getAbsolutePath();
            
            absolutePath = absolutePath.replace("\\", "/");
            
            String[] split = absolutePath.split("/");
            
            String ident = split[split.length - 2] + "/" + split[split.length - 1];
            
            node.setAttribute("file", ident);
            results.add(node.getAttribute("file"));
            
            results.add("id=" + id);
        }
        
        if (type.equals("toif:TOIFSegment"))
        {
            results.add(node.getAttribute("parent"));
            results.add("id=" + id);
        }
        
        if ("toif:WeaknessDescription".equals(type))
        {
            results.add(node.getAttribute("text"));
        }
        
        // for all the children of this node, add them to the results.
        for (int i = 0; i < children.getLength(); i++)
        {
            final Node child = children.item(i);
            
            final String nodeName = child.getNodeName();
            
            final NamedNodeMap attributes = child.getAttributes();
            
            if ("description".equals(nodeName))
            {
                results.add((attributes.getNamedItem("text").toString()));
            }
            if ("checksum".equals(nodeName))
            {
                continue;
            }
            else
            {
                if (attributes != null)
                {
                    results.add((attributes.getNamedItem(nodeName) + ""));
                }
                
            }
            
        }
        
        toifType = type;
        
        results.add(type);
        
        Collections.sort(results);
        
        return results;
        
    }
    
    /**
     * the element to string.
     * 
     * @param factory
     *            the chosen value factory
     * @return the string value.
     */
    public String getStringValue(ValueFactory factory)
    {
        if (getElement() == null)
        {
            return "testing testing testing";
        }
        return factory.createLiteral(getElement().toString()).toString();
    }
}
