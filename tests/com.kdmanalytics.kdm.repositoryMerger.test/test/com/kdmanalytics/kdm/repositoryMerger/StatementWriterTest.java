
package com.kdmanalytics.kdm.repositoryMerger;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;

import com.kdmanalytics.store.rdf.impl.SimpleURI;
import com.kdmanalytics.store.rdf.impl.SimpleValueFactory;

/**
 * 
 * @author Ken Duck
 * 
 */
public class StatementWriterTest
{
    
    @Test
    public void escapeFirstCharacterTest()
    {
        StringWriter outString = new StringWriter();
        PrintWriter pw = new PrintWriter(outString);
        StatementWriter statementWriter = new StatementWriter(pw, RepositoryMerger.NTRIPLES);
        URI subject = SimpleValueFactory.getSimpleValueFactory().createURI(1);
        URI predicate = SimpleValueFactory.getSimpleValueFactory().createURI(SimpleURI.kdmNS, "predicate");
        Literal object = SimpleValueFactory.getSimpleValueFactory().createLiteral("\bescaped\tcharacters");
        statementWriter.print(subject, predicate, object);
        pw.close();
        StringBuffer sb = outString.getBuffer();
        //System.err.println("WAT: " + getLiteral(sb.toString()));
        // escaped to unicode.
        assertEquals("\\u0008escaped\\tcharacters", getLiteral(sb.toString()));
    }
    
    @Test
    public void escapeSecondCharacterTest()
    {
        StringWriter outString = new StringWriter();
        PrintWriter pw = new PrintWriter(outString);
        StatementWriter statementWriter = new StatementWriter(pw, RepositoryMerger.NTRIPLES);
        URI subject = SimpleValueFactory.getSimpleValueFactory().createURI(1);
        URI predicate = SimpleValueFactory.getSimpleValueFactory().createURI(SimpleURI.kdmNS, "predicate");
        Literal object = SimpleValueFactory.getSimpleValueFactory().createLiteral("an\tescaped character");
        statementWriter.print(subject, predicate, object);
        pw.close();
        StringBuffer sb = outString.getBuffer();
        //System.err.println("WAT: " + getLiteral(sb.toString()));
        // escape the escaped character.
        assertEquals("an\\tescaped character", getLiteral(sb.toString()));
    }
    
    /**
     * 
     * @param string
     * @return
     */
    private String getLiteral(String statement)
    {
        String[] tokens = statement.trim().split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < tokens.length; i++)
        {
            if (i > 2)
                sb.append(' ');
            sb.append(tokens[i]);
        }
        String result = sb.toString();
        if (result.endsWith("."))
            result = result.substring(0, result.length() - 1).trim();
        if (result.startsWith("\"") && result.endsWith("\""))
        {
            result = result.substring(1, result.length() - 1).trim();
        }
        return result;
    }
}
