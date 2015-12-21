/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.findbugs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.kdmanalytics.toif.framework.files.IFileResolver;
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

	private Integer offset;

	private File file;

	private Properties props;

	private boolean first;

	private String description;

	private ArrayList<Element> traces = new ArrayList<>();

	private Stack<String> stack = new Stack<>();

	/**
	 * Used to resolve absolute file paths from relative paths
	 */
	private IFileResolver resolver;

	/**
	 * Table of files so we don't create duplicates
	 */
	private Map<String,File> files = new HashMap<String,File>();

	/**
	 * construct a findbugs parser.
	 */
	public FindBugsParser(Properties props, IFileResolver resolver, String name, boolean unknownCWE)
	{
		findingCreator = new FindingCreator(props, name, unknownCWE);
		this.resolver = resolver;
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
			// Reset the file to ensure we get a clean one each time
			file = null;
		}

		if (!"SourceLine".equals(qName))
		{
			stack.push(qName);
			first = true;
		}

		//        if ("SourceLine".equals(qName) && (attrs.getLength() >= 5))
		//        {

		if ("SourceLine".equals(qName) && "BugInstance".equals(stack.peek())) {

			String startByte = attrs.getValue("startBytecode");
			if (first)
			{
				String start = attrs.getValue("start");
				if (start!= null) {
					line = Integer.parseInt(start);
				} else {
					line = 1;
				}
				if (startByte != null)
				{
					offset = Integer.parseInt(startByte);
				}
				description = props.getProperty(id + "Msg");
				first = false;
			}
			else
			{
				String start = attrs.getValue("start");
				traces.add(new CodeLocation(Integer.parseInt(start), null, Integer.parseInt(startByte)));
			}

		} else if ("SourceLine".equals(qName)) {
			String startByte = attrs.getValue("startBytecode");
			String start = attrs.getValue("start");
			if (start != null) {
				line = Integer.parseInt(start);
			} else {
				line = 1;
			}
			if (startByte != null)
			{
				offset = Integer.parseInt(startByte);
			}
			description = props.getProperty(id + "Msg");
			first = false;

			// Get appropriate file information
			if(file == null) {
				String fname = null;

				// FIXME: Disabled until we have a more clever FileResolver that knows about sourcepath
				//				// Try using the source path first
				//				fname = attrs.getValue("sourcepath");
				if(fname == null) {
					fname = attrs.getValue("classname");
					fname = fname.replace('.', '/');
					fname = fname + ".class";
				}
				if(fname != null) {
					if(files.containsKey(fname)) {
						file = files.get(fname);
					}
					else {
						file = new File(fname);
						files.put(fname, file);
					}
				}
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
			stack.pop();
			first = true;
		}
		if ("BugInstance".equals(qName))
		{
			if(file == null) {
				System.err.println("Cannot find file for: [" + id + "] " + description);
			}
			else {
				File afile = resolver.resolve(file);
				findingCreator.create(description, id, line, offset, null, afile, null, null, traces.toArray(new CodeLocation[traces.size()]));
			}
		}
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
