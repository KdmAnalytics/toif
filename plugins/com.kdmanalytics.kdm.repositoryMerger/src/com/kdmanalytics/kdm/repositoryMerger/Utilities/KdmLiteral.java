/**
 * 
 */

package com.kdmanalytics.kdm.repositoryMerger.Utilities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * @author adam
 * 
 */
public class KdmLiteral implements Literal, Value, Comparable<Value>, Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected String buf = null;
    
    /**
     * 
     * @param value
     */
    public KdmLiteral(String value)
    {
        // Use the canonical version to save space -- but costs time
        // this.buf = value.intern();
        this.buf = value;
    }
    
    @Override
    public boolean booleanValue()
    {
        return Boolean.parseBoolean(buf);
    }
    
    @Override
    public byte byteValue()
    {
        return Byte.parseByte(buf);
    }
    
    @Override
    public XMLGregorianCalendar calendarValue()
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public BigDecimal decimalValue()
    {
        return new BigDecimal(buf);
    }
    
    @Override
    public double doubleValue()
    {
        return Double.parseDouble(buf);
    }
    
    @Override
    public float floatValue()
    {
        return Float.parseFloat(buf);
    }
    
    @Override
    public URI getDatatype()
    {
        return null;
    }
    
    @Override
    public String getLabel()
    {
        return buf;
    }
    
    @Override
    public String getLanguage()
    {
        return null;
    }
    
    @Override
    public int intValue()
    {
        // return Integer.parseInt(buf);
        return IntUtils.parseInt(buf);
    }
    
    @Override
    public BigInteger integerValue()
    {
        return new BigInteger(buf);
    }
    
    @Override
    public long longValue()
    {
        return Long.parseLong(buf);
    }
    
    @Override
    public short shortValue()
    {
        return Short.parseShort(buf);
    }
    
    @Override
    public String stringValue()
    {
        return buf;
    }
    
    @Override
    public int hashCode()
    {
        return buf.hashCode();
    }
    
    /**
     * Assume we are sent a value to compare with. If not then this will fail
     * most grievously.
     * 
     */
    @Override
    public boolean equals(Object o)
    {
        Value val = (Value) o;
        return buf.equals(val.stringValue());
    }
    
    /**
     * Assume we are sent a value to compare with. If not then this will fail
     * most grievously.
     * 
     */
    @Override
    public int compareTo(Value val)
    {
        return buf.compareTo(val.stringValue());
    }
    
    /**
     * 
     */
    @Override
    public String toString()
    {
        return "\"" + buf + "\"";
    }
}
