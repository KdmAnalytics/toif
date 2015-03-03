/**
 * 
 */

package com.kdmanalytics.toif.assimilator.toifRdfTypes;

import java.io.File;

import org.w3c.dom.Element;

/**
 * represents an rdf value for toif.
 * 
 * @author adam
 * 
 */
public class ToifRdfValue extends ToifRdfResource
{
    
    private String value;
    
    public ToifRdfValue()
    {
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ToifRdfValue other = (ToifRdfValue) obj;
        if (value == null)
        {
            if (other.value != null)
                return false;
        }
        else if (!value.equals(other.value))
            return false;
        return true;
    }
    
    /**
     * create a new value
     * 
     * @param element
     *            the rdf element that this value will be based from
     * @param currentFile
     *            the current file this element is in.
     */
    public ToifRdfValue(Element element, File currentFile)
    {
        super(element, currentFile);
    }
    
    /**
     * create a new value
     * 
     * @param value
     *            the value ot set
     * @param element
     *            the rdf element this value will be based on
     * @param currentFile
     *            the file the element is in.
     */
    public ToifRdfValue(String value, Element element, File currentFile)
    {
        super(element, currentFile);
        
        this.value = value;
    }
    
    /**
     * get the value.
     * 
     * @return the value.
     */
    public String getValue()
    {
        return value;
    }
    
}
