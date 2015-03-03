
package com.kdmanalytics.toif.mergers;

import java.util.HashMap;

/**
 * 
 * Statements that have the same codeLocation are the same statements. this
 * class carries a mapping of these local statements to global codeLocations.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class StatementMapping
{
    
    HashMap<Long, Long> localStatementToLocalCodeLocation = new HashMap<Long, Long>();
    
    HashMap<Long, Long> localCodeLocationToGlobalCodeLocation = new HashMap<Long, Long>();
    
    HashMap<Long, Long> globalCodeLocationToGlobalStatement = new HashMap<Long, Long>();
    
    /**
     * get the global statement for this local resource
     * 
     * @param statementLocal
     *            the local resource id
     * @return the global resource id
     */
    public Long getGlobalStatement(Long statementLocal)
    {
        Long clLocal = localStatementToLocalCodeLocation.get(statementLocal);
        
        Long clGlobal = localCodeLocationToGlobalCodeLocation.get(clLocal);
        
        Long SGlobal = globalCodeLocationToGlobalStatement.get(clGlobal);
        
        return SGlobal;
    }
    
    /**
     * call this every time that a codelocation is mapped to a global.
     * 
     * @param clLocal
     *            the local id of the codelocation
     * @param clGlobal
     *            the global id of the code location.
     */
    public void setNewCodeLocation(Long clLocal, Long clGlobal)
    {
        localCodeLocationToGlobalCodeLocation.put(clLocal, clGlobal);
    }
    
    /**
     * call this every time that a statement resource is made.
     * 
     * @param sLocal
     *            the local id of the statement
     * @param clLocal
     *            the local id of the code location for the statement.
     */
    public void setNewLocalStatement(Long sLocal, Long clLocal)
    {
        localStatementToLocalCodeLocation.put(sLocal, clLocal);
    }
    
    /**
     * called when a statement is made global
     * 
     * @param clGlobal
     * @param sGlobal
     */
    public void setNewGlobalStatement(Long clGlobal, Long sGlobal)
    {
        globalCodeLocationToGlobalStatement.put(clGlobal, sGlobal);
    }
    
}
