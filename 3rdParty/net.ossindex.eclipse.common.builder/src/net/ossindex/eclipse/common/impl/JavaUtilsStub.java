/**
 *	Copyright (c) 2015 Vör Security Inc.
 *	All rights reserved.
 *	
 *	Redistribution and use in source and binary forms, with or without
 *	modification, are permitted provided that the following conditions are met:
 *	    * Redistributions of source code must retain the above copyright
 *	      notice, this list of conditions and the following disclaimer.
 *	    * Redistributions in binary form must reproduce the above copyright
 *	      notice, this list of conditions and the following disclaimer in the
 *	      documentation and/or other materials provided with the distribution.
 *	    * Neither the name of the <organization> nor the
 *	      names of its contributors may be used to endorse or promote products
 *	      derived from this software without specific prior written permission.
 *	
 *	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *	ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *	WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *	DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 *	DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *	ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.ossindex.eclipse.common.impl;

import java.util.LinkedList;
import java.util.List;

import net.ossindex.eclipse.common.IJavaUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/** Java platform specific utilities stubs
 * 
 * @author Ken Duck
 *
 */
public class JavaUtilsStub implements IJavaUtils
{
	private List<IPath> sourcePaths = new LinkedList<IPath>();
	private List<IPath> classPaths = new LinkedList<IPath>();
	private List<IPath> targetPaths = new LinkedList<IPath>();
	
	public JavaUtilsStub()
	{
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.IJavaUtils#setProject(org.eclipse.core.resources.IProject)
	 */
	public void setProject(IProject project)
	{
	}
	
	/**
	 * 
	 * @return
	 */
	public List<IPath> getSourcePaths()
	{
		return sourcePaths;
	}

	/**
	 * 
	 * @return
	 */
	public List<IPath> getClassPaths()
	{
		return classPaths;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.IJavaUtils#getTargetPaths()
	 */
	@Override
	public List<IPath> getTargetPaths()
	{
		return targetPaths;
	}

	@Override
	public String[] getSourcePaths(IResource resource) {
		return new String[0];
	}

	@Override
	public String[] getClassPaths(IResource resource) {
		return new String[0];
	}

	@Override
	public String[] getTargetPaths(IResource resource) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.IJavaUtils#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.IJavaUtils#getResource(java.lang.Object)
	 */
	@Override
	public Object getResource(Object resource) {
		return resource;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.IJavaUtils#getSourceFile(org.eclipse.core.resources.IFile)
	 */
	@Override
	public IFile getSourceFile(IFile classFile) {
		return classFile;
	}

	@Override
	public void clean(String[] builderIds) {
	}

	@Override
	public IFile getClassFile(IFile sourceFile) {
		return null;
	}



}
