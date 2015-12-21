/*******************************************************************************
 * //////////////////////////////////////////////////////////////////////////////////
 * // Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and the
 * // accompanying materials are made available under the terms of the Open Source
 * // Initiative OSI - Open Software License v3.0 which accompanies this
 * // distribution, and is available at http://www.opensource.org/licenses/osl-3.0.php/
 * //////////////////////////////////////////////////////////////////////////////////
 ******************************************************************************/
package com.kdmanalytics.toif.framework.xmlElements.entities;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entity which represents the codeLocation.
 * 
 * @author Adam Nunn
 * 
 */
@XmlRootElement(name = "fact")
public class CodeLocation extends Entity
{
    
    // the linenumber
    private LineNumber lineNumber = null;
    
    // the position
    private Position position = null;
    
    // the offset.
    private Offset offset = null;
    
    /**
     * required blank constructor
     */
    public CodeLocation()
    {
        super();
        
        // set the xml type to "toif:CodeLocation"
        type = "toif:CodeLocation";
    }
    
    /**
     * make a codeLocation from the lineNumber, position, and offset.
     * 
     * @param lineNumber
     *            the line number of the location
     * @param position
     *            the position of the location
     * @param offset
     *            the offset of the location
     */
    public CodeLocation(Integer lineNumber, Integer position, Integer offset)
    {
        super();
        
        // set the xml type to "toif:CodeLocation"
        type = "toif:CodeLocation";
        
        this.lineNumber = new LineNumber(lineNumber);
        if (position != null)
            this.position = new Position(position);
        if (offset != null)
            this.offset = new Offset(offset);
        
    }
    
    /**
     * get the linenumber
     * 
     * @return the lineNumber
     */
    public LineNumber getLineNumber()
    {
        return lineNumber;
    }
    
    /**
     * set the linenumber
     * 
     * @param lineNumber
     *            the lineNumber to set
     */
    @XmlElementRef(name = "linenumber")
    public void setLineNumber(LineNumber lineNumber)
    {
        this.lineNumber = lineNumber;
    }
    
    /**
     * get the position
     * 
     * @return the position
     */
    public Position getPosition()
    {
        return position;
    }
    
    /**
     * set teh position
     * 
     * @param position
     *            the position to set
     */
    @XmlElementRef(name = "position")
    public void setPosition(Position position)
    {
        this.position = position;
    }
    
    /**
     * get the offset
     * 
     * @return the offset
     */
    public Offset getOffset()
    {
        return offset;
    }
    
    /**
     * set the offset
     * 
     * @param offset
     *            the offset to set
     */
    @XmlElementRef(name = "offset")
    public void setOffset(Offset offset)
    {
        this.offset = offset;
    }
    
    /**
     * Over ride the equals method. To find equivalent codelocations.
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean result = false;
        
        if (obj instanceof CodeLocation)
        {
            CodeLocation location = (CodeLocation) obj;
            
            if (getLineNumber() != null)
            {
                if (location.getLineNumber() == null)
                    return false;
                result = getLineNumber().getLineNumber().equals(location.getLineNumber().getLineNumber());
            }
            if (getOffset() != null)
            {
                if (location.getOffset() == null)
                    return false;
                result = result && getOffset().getOffset().equals(location.getOffset().getOffset());
            }
            if (getPosition() != null)
            {
                if (location.getPosition() == null)
                    return false;
                result = result && getPosition().getPosition().equals(location.getPosition().getPosition());
            }
        }
        return result;
        
    }
    
    /**
     * create a hash of the object.
     */
    @Override
    public int hashCode()
    {
        int result = 0;
        if (lineNumber != null)
            result ^= lineNumber.getLineNumber().hashCode();
        if (offset != null)
            result ^= offset.hashCode();
        if (position != null)
            result ^= position.hashCode();
        
        return result;
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if(lineNumber != null) sb.append(lineNumber);
        else sb.append(0);
        sb.append(":");
        if(offset != null) sb.append(offset);
        else if(position != null) sb.append(position);
        else sb.append(0);
        return sb.toString();
    }
}
