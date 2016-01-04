
package com.kdmanalytics.toif.framework.toolAdaptor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class ToolAdaptorUtil
{
    
    private static final String ADAPTOR_EXT_POINT_ID = "com.kdmanalytics.toif.adaptor";
    
    public static Set<AbstractAdaptor> getAdaptors()
    {
        Set<AbstractAdaptor> adaptorSet = new HashSet<AbstractAdaptor>();
        
        IConfigurationElement config[] = Platform.getExtensionRegistry().getConfigurationElementsFor(ADAPTOR_EXT_POINT_ID);
        
        try
        {
            for (IConfigurationElement element : config)
            {
                final Object object = element.createExecutableExtension("class");
                if (object instanceof AbstractAdaptor)
                {
                    AbstractAdaptor adaptor = (AbstractAdaptor) object;
                    
                    adaptorSet.add(adaptor);
                }
            }
            
        }
        catch (CoreException ex)
        {
            System.err.println("Adaptor module failure: " + ex.getMessage());
        }
        return adaptorSet;
    }
    
}
