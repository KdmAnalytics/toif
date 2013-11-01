
package com.kdmanalytics.toif.findbugs;

/*******************************************************************************
 * Copyright (c) 2012 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/
import java.util.ArrayList;
import java.util.Properties;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.kdmanalytics.toif.framework.utils.FindingCreator;
import com.kdmanalytics.toif.framework.xmlElements.entities.CodeLocation;
import com.kdmanalytics.toif.framework.xmlElements.entities.Element;
import com.kdmanalytics.toif.framework.xmlElements.entities.File;

/**
 * The parser for the findbugs output.
 * 
 * @author adam
 * 
 */
public class FindBugsParser extends DefaultHandler
{
    
    private FindingCreator findingCreator;
    
    private String id;
    
    private Integer line;
    
    private boolean canParse = true;
    
    private Integer offset;
    
    private File file;
    
    private Properties props;
    
    private boolean first;
    
    private String description;
    
    private ArrayList<Element> traces = new ArrayList<>();
    
    /**
     * construct a findbugs parser.
     */
    public FindBugsParser(Properties props, File file, String name, boolean unknownCWE)
    {
        findingCreator = new FindingCreator(props, name, unknownCWE);
        this.file = file;
        this.props = props;
    }
    
    /**
     * start the parse. The details are handed to the findingCreator.
     */
    public void startElement(String uri, String localName, String qName, Attributes attrs)
    {
        if ("BugInstance".equals(qName))
        {
            id = attrs.getValue("type");
            first = true;
            traces.clear();
        }
        
        if (!"SourceLine".equals(qName))
        {
            canParse = false;
        }
        
        if ("SourceLine".equals(qName) && (attrs.getLength() >= 7) && canParse)
        {
            
            if (first)
            {
                line = Integer.parseInt(attrs.getValue("start"));
                offset = Integer.parseInt(attrs.getValue("startBytecode"));
                description = props.getProperty(id + "Msg");
                first = false;
            }
            else
            {
                traces.add(new CodeLocation(Integer.parseInt(attrs.getValue("start")), null, Integer.parseInt(attrs.getValue("startBytecode"))));
            }
            
        }
    }
    
    /**
     * called on the end element.
     */
    public void endElement(String uri, String localName, String qName)
    {
        if (!"SourceLine".equals(qName))
        {
            canParse = true;
        }
        if ("BugInstance".equals(qName))
            findingCreator.create(description, id, line, offset, null, file, null, null, traces.toArray(new CodeLocation[traces.size()]));
    }
    
    /**
     * reuturn the gathered elements.
     * 
     * @return
     */
    public ArrayList<Element> getElements()
    {
        return findingCreator.getElements();
    }
}
