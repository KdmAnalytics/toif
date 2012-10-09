/**
 * 
 */

package com.kdmanalytics.kdm.repositoryMerger.Utilities;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;

/**
 * @author adam
 * 
 */
public class MergerURI implements URI
{
    
    private static final long serialVersionUID = 350068407482600064L;
    
    public static String KdmNS = "http://org.omg.kdm/";
    
    public static String KdmModelNS = "http://kdmanalytics.com/";
    
    public static final String WORKBENCH_EXTENSION = "__WORKBENCH__";
    
    public static final Literal WORKBENCH_EXTENSION_LITERAL = getLiteral(WORKBENCH_EXTENSION);
    
    private String name;
    
    private String nameSpace;
    
    /**
     * 
     * @param ns
     * @param name
     */
    public MergerURI(String ns, String name)
    {
        this.name = name;
        this.nameSpace = ns;
        
    }
    
    /**
     * Generate a URI for the specified name
     * 
     * @param name
     * @return
     */
    private static Literal getLiteral(String name)
    {
        return new KdmLiteral(name);
    }
    
    /**
     * Big assumption: In KDM, currently, the namespace is not important so I am
     * not using it in my comparisons.
     * 
     */
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof MergerURI)
        {
            final MergerURI other = (MergerURI) o;
            return other.getLocalName().equals(name);
        }
        
        if (o instanceof URI)
        {
            return ((URI) o).getLocalName().equals(name);
        }
        
        return false;
    }
    
    /**
     * (non-Javadoc)
     * 
     * @see org.openrdf.model.URI#getLocalName()
     */
    @Override
    public String getLocalName()
    {
        return name;
    }
    
    /**
     * (non-Javadoc)
     * 
     * @see org.openrdf.model.URI#getNamespace()
     */
    @Override
    public String getNamespace()
    {
        return nameSpace;
    }
    
    /**
     * 
     */
    @Override
    public final int hashCode()
    {
        return toString().hashCode();
    }
    
    /**
     * (non-Javadoc)
     * 
     * @see org.openrdf.model.Value#stringValue()
     */
    @Override
    public String stringValue()
    {
        return nameSpace + name;
    }
    
    /**
     * 
     */
    @Override
    public String toString()
    {
        return stringValue();
    }
}
