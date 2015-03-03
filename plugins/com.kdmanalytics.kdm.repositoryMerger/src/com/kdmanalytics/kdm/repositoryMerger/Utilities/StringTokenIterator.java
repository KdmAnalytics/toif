package com.kdmanalytics.kdm.repositoryMerger.Utilities;
import java.util.Iterator;
/**
 * Extremely simple StringTokenizer.  Tokenizes a string on a single character.
 * 
 * You can set a boolean to true preserve all tokens or false to skip empty
 * tokens
 * 
 * @author Kyle Girard
 *
 */
public class StringTokenIterator implements Iterable<String>, Iterator<String>
{
    private char delim;
    
    private String srcString;
    
    int srcStringLength;
    
    int startIndex;
    
    int endIndex;
    
    boolean match;
    
    boolean lastMatch;
    
    boolean preserveTokens;
    
    /** Create a StringTokenIterator
     * 
     * @param sourceString String to be tokenized
     * @param delimiter
     * @param preserveAllTokens Should the delimeter be preserved
     */
    public StringTokenIterator(String sourceString, char delimiter, boolean preserveAllTokens)
    {
        if (sourceString == null)
        {
            throw new NullArgumentException("aString");
        }
        
        if (sourceString.isEmpty())
        {
            throw new IllegalArgumentException("'sourceString' cannot be an empty String");
        }
        
        lastMatch = false;
        match = false;
        startIndex = 0;
        endIndex = 0;
        srcString = sourceString;
        srcStringLength = srcString.length();
        delim = delimiter;
        preserveTokens = preserveAllTokens;
    }
    
    @Override
    public Iterator<String> iterator()
    {
        return this;
    }
    
    @Override
    public boolean hasNext()
    {       
        if (endIndex > srcStringLength)
        {
            return false;
        }
        
        while (endIndex < srcStringLength)
        {
            if (srcString.charAt(endIndex) == delim)
            {
                if (match || preserveTokens)
                {
                    return true;
                }
                startIndex = ++endIndex;
                continue;
            }
            match = true;
            lastMatch = false;
            ++endIndex;
        }
        return (match || (preserveTokens && lastMatch));
    }
    
    @Override
    public String next()
    {
        String retStr;
        if (startIndex == 0 && endIndex == 0)
        {
            hasNext();
        }
        retStr = srcString.substring(startIndex, endIndex);
        match = false;
        lastMatch = true;
        startIndex = ++endIndex;        
        return retStr;
    }
    
    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Remove not supported");
    }
}
