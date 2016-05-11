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
package net.ossindex.eclipse.common.builder;

import java.util.HashMap;
import java.util.Map;

import net.ossindex.eclipse.common.IJavaUtils;
import net.ossindex.eclipse.common.Utils;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

/** Build visitor that identifies appropriate files, collects interesting
 * build information, and calls a method that will be useful for sub-classing.
 * 
 * This class is responsible for managing progress.
 * 
 * @author Ken Duck
 *
 */
public abstract class JavaBuildVisitor extends CommonBuildVisitor implements IResourceVisitor, IResourceDeltaVisitor
{

	/**
	 * Progress monitor
	 */
	private SubMonitor progress;

	/**
	 * 
	 */
	private IJavaUtils utils = Utils.getJavaUtils();

	/**
	 * Map for project specific utils
	 */
	private Map<IProject,IJavaUtils> utilMap;

	/**
	 * 
	 * @param builderId
	 * @param monitor
	 */
	public JavaBuildVisitor(String builderId, IProgressMonitor monitor)
	{
		super(builderId);
		utilMap = new HashMap<IProject,IJavaUtils>();
		progress = SubMonitor.convert(monitor);
	}
	
	/**
	 * 
	 * @param monitor
	 */
	public void setProgressMonitor(IProgressMonitor monitor)
	{
		progress = SubMonitor.convert(monitor);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
	 */
	@Override
	public boolean visit(IResource resource) throws CoreException
	{
		// Handle cancellation
		if(progress.isCanceled()) return false;
		
		IJavaUtils utils = null;
		IProject project = resource.getProject();
		synchronized(this)
		{
			if(!utilMap.containsKey(project)) utilMap.put(project, Utils.getJavaUtils(project));
			utils = utilMap.get(project);
		}
		
		// Handle cancellation
		if(progress.isCanceled()) return false;

		//		System.err.println("VISIT: " + resource);
		if(!buildsClass())
		{
			if(isJavaFile(resource))
			{
				//System.out.println("  Java VISIT: " + resource);

				if(isDirty((IFile)resource))
				{
					buildSource(resource);
					markBuilt((IFile)resource);
				}
			}
		}
		else
		{
			if(isClassFile(resource))
			{
				//System.out.println("  Class VISIT: " + resource);

				IFile sourceFile = utils.getSourceFile((IFile)resource);
				if(isDirty(sourceFile))
				{
					buildClass(resource);
					markBuilt(sourceFile);
				}
			}
		}
		return true;
	}

	/** Indicates whether this visitor is intended for class builders or
	 * source builders. It cannot be for both.
	 * 
	 * @return
	 */
	protected boolean buildsClass()
	{
		return true;
	}

	/** Perform any build operations that require the source file.
	 * 
	 * @param resource
	 */
	protected abstract void buildSource(IResource resource);

	/** Perform any build operations that require the class file.
	 * 
	 * @param resource
	 */
	protected abstract void buildClass(IResource resource);

	/**
	 * 
	 * @param resource
	 * @return
	 */
	private boolean isJavaFile(IResource resource)
	{
		if(resource instanceof IFile)
		{
			String name = resource.getName();
			if(name.endsWith(".java")) return true;
		}
		return false;
	}

	/**
	 * 
	 * @param resource
	 * @return
	 */
	private boolean isClassFile(IResource resource)
	{
		if(resource instanceof IFile)
		{
			String name = resource.getName();
			if(name.endsWith(".class")) return true;
		}
		return false;
	}

	/** For our purposes we only care if a source file has changed. If the source
	 * files have not changed then the class files should not have changed.
	 * 
	 * @see net.ossindex.eclipse.common.builder.CommonBuildVisitor#accepts(org.eclipse.core.resources.IFile)
	 */
	@Override
	protected boolean accepts(IFile resource)
	{
		return isClassFile(resource);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.builder.CommonBuildVisitor#acceptsAsArtifact(org.eclipse.core.resources.IFile)
	 */
	@Override
	protected boolean acceptsSource(IFile resource)
	{
		return isJavaFile(resource);
	}


	/** Get the paths to source directories.
	 * 
	 * @param resource
	 * @return
	 */
	protected String[] getSourcePaths(IResource resource)
	{
		return utils.getSourcePaths(resource);
	}

	/** Get the class paths for the project
	 * 
	 * @param javaProject
	 */
	protected String[] getClassPaths(IResource resource)
	{
		return utils.getClassPaths(resource);
	}

	/**
	 * 
	 * @param resource
	 * @return
	 */
	protected String[] getTargetPaths(IResource resource)
	{
		return utils.getTargetPaths(resource);
	}

	/** Override the task name for the progress monitor
	 * 
	 * @param name
	 */
	protected void setTaskName(String name)
	{
		progress.setTaskName(name);
	}
	
	/** Un-mark the files as being built.
	 * 
	 * @param project
	 */
	public void clean(IResource resource)
	{
		
		if(resource instanceof IFile)
		{
			
			if(!buildsClass())
			{
				if(isJavaFile(resource))
				{
					IFile classFile = utils.getClassFile((IFile)resource);
					super.clean(classFile);
				}
			}
		}
		
		super.clean(resource);
	}

}
