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
package net.ossindex.eclipse.common;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/** Provide utilities that work with JavaProjects. This helps isolate the code
 * so that it can be loaded or not depending on the installation situation.
 * 
 * @author Ken Duck
 *
 */
public interface IJavaUtils
{
	/**
	 * 
	 * @param project
	 */
	public void setProject(IProject project);
	
	/**
	 * 
	 * @return
	 */
	List<IPath> getSourcePaths();

	/**
	 * 
	 * @return
	 */
	List<IPath> getClassPaths();

	/**
	 * 
	 * @return
	 */
	List<IPath> getTargetPaths();
	
	/**
	 * 
	 * @param resource
	 * @return
	 */
	public String[] getSourcePaths(IResource resource);

	/** Get the class paths for the project
	 * 
	 * @param javaProject
	 */
	public String[] getClassPaths(IResource resource);

	/** Get the target directory for compiled classes
	 * 
	 * @param resource
	 * @return
	 */
	public String[] getTargetPaths(IResource resource);

	/** Returns true if the java utilities are available
	 * 
	 * @return
	 */
	public boolean isAvailable();

	/**
	 * 
	 * @param resource
	 * @return
	 */
	public Object getResource(Object resource);

	/** Given a class file, identify the source file that it corresponds to.
	 * 
	 * @param resource
	 * @return
	 */
	public IFile getSourceFile(IFile classFile);

	/**
	 * Clean all build related fields for all projects.
	 */
	public void clean(String[] builderIds);

	/**
	 * 
	 * @return
	 */
	public IFile getClassFile(IFile sourceFile);

}
