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

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ossindex.eclipse.common.IJavaUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/** Java platform specific utilities
 * 
 * @author Ken Duck
 *
 */
public class JavaUtils extends CommonUtils implements IJavaUtils
{
	private List<IPath> sourcePaths = new LinkedList<IPath>();
	private List<IPath> classPaths = new LinkedList<IPath>();
	private List<IPath> targetPaths = new LinkedList<IPath>();
	
	public JavaUtils()
	{
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.IJavaUtils#setProject(org.eclipse.core.resources.IProject)
	 */
	public void setProject(IProject project)
	{
		super.setProject(project);
		IJavaProject javaProject = JavaCore.create(project);
		if(javaProject.exists())
		{
			loadSourcePaths(javaProject);
			loadClassPaths(javaProject);
		}
	}
	

	/** Load the source paths for the project
	 * 
	 * @param javaProject
	 */
	private void loadSourcePaths(IJavaProject javaProject)
	{
		try
		{
			if(javaProject.exists())
			{
				// Get source information specifically
				IPackageFragmentRoot[] roots = javaProject.getAllPackageFragmentRoots();
				if(roots != null)
				{
					for (IPackageFragmentRoot root : roots)
					{
						switch(root.getKind())
						{
						case IPackageFragmentRoot.K_SOURCE:
						{
							IPath sourcePath = root.getPath();
							sourcePaths.add(sourcePath);
							break;
						}
						case IPackageFragmentRoot.K_BINARY:
						{
							// This gives you a bunch of jar files only.
							//						IPath classPath = root.getPath();
							//						classPaths.add(classPath);
							//						System.err.println("CLASS PATH: " + classPath);
							//						break;
						}
						}
					}
				}
			}
		}
		catch (JavaModelException e)
		{
			e.printStackTrace();
		}
	}

	/** Get the class paths for the project
	 * 
	 * @param javaProject
	 */
	private void loadClassPaths(IJavaProject javaProject)
	{
		try
		{
			// Prime with the java project output location
			IPath outputLocation = javaProject.getOutputLocation();
			if(outputLocation != null)
			{
				if(!targetPaths.contains(outputLocation))
				{
					targetPaths.add(outputLocation);
				}
			}
			
			
			// Now look for extra configured classpaths
			if(javaProject.exists())
			{
				IClasspathEntry[] classpathEntries = javaProject.getRawClasspath();
				if(classpathEntries != null)
				{
					for (IClasspathEntry entry : classpathEntries)
					{
						// This technically works for finding source paths, but it
						// also gets a few other strange package type definitions
						// so to avoid confusion I use the above solution instead.
						//					IPath sourcePath = entry.getPath();
						//					sourcePaths.add(sourcePath);
						IPath classPath = entry.getOutputLocation();
						if(classPath != null)
						{
							classPaths.add(classPath);
							if(!targetPaths.contains(classPath))
							{
								targetPaths.add(classPath);
							}
						}
					}
				}
			}
		}
		catch (JavaModelException e)
		{
			e.printStackTrace();
		}
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
	
	@Override
	public List<IPath> getTargetPaths() {
		return targetPaths;
	}


	/** Get the paths to source directories.
	 * 
	 * @param resource
	 * @return
	 */
	public String[] getSourcePaths(IResource resource)
	{
		IJavaProject javaProject = JavaCore.create(resource.getProject());
		List<String> results = new LinkedList<String>();
		try
		{
			if(javaProject.exists())
			{
				IPackageFragmentRoot[] roots = javaProject.getAllPackageFragmentRoots();
				if(roots != null)
				{
					for (IPackageFragmentRoot root : roots)
					{
						if(root.getKind() == IPackageFragmentRoot.K_SOURCE)
						{
							File file = root.getPath().toFile();
							results.add(file.getAbsolutePath());
						}
					}
				}
			}
		}
		catch (JavaModelException e)
		{
			e.printStackTrace();
		}

		return results.toArray(new String[results.size()]);
	}

	/** Get the class paths for the project
	 * 
	 * @param javaProject
	 */
	public String[] getClassPaths(IResource resource)
	{
		IProject project = resource.getProject();
		IJavaProject javaProject = JavaCore.create(project);
		List<String> results = new LinkedList<String>();
		try
		{
			if(javaProject.exists())
			{
				IClasspathEntry[] classpathEntries = javaProject.getRawClasspath();
				if(classpathEntries != null)
				{
					for (IClasspathEntry entry : classpathEntries)
					{
						// This technically works for finding source paths, but it
						// also gets a few other strange package type definitions
						// so to avoid confusion I use the above solution instead.
						//					IPath sourcePath = entry.getPath();
						//					sourcePaths.add(sourcePath);
						if(entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY)
						{
							IPath classPath = entry.getOutputLocation();
							if(classPath != null)
							{
								IFile ifile = project.getWorkspace().getRoot().getFile(classPath);
								results.add(ifile.getLocation().toString());
							}
						}
					}
				}
			}
		}
		catch (JavaModelException e)
		{
			e.printStackTrace();
		}
		return results.toArray(new String[results.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.IJavaUtils#getTargetPaths(org.eclipse.core.resources.IResource)
	 */
	@Override
	public String[] getTargetPaths(IResource resource)
	{
		IProject project = resource.getProject();
		IJavaProject javaProject = JavaCore.create(project);
		List<String> results = new LinkedList<String>();
		try
		{
			if(javaProject.exists())
			{
				Set<IResource> paths = new HashSet<IResource>();
				
				// Prime with the java project output location
				IPath outputLocation = javaProject.getOutputLocation();
				if(outputLocation != null)
				{
					IFolder ifile = project.getWorkspace().getRoot().getFolder(outputLocation);
					if(!paths.contains(ifile))
					{
						results.add(ifile.getLocation().toString());
						paths.add(ifile);
					}
				}
				
				// Now look for extra configured classpaths
				IClasspathEntry[] classpathEntries = javaProject.getRawClasspath();
				if(classpathEntries != null)
				{
					for (IClasspathEntry entry : classpathEntries)
					{
						// This first case works for "standard" Eclipse projects
						if(entry.getContentKind() == IPackageFragmentRoot.K_SOURCE)
						{
							IPath classPath = entry.getOutputLocation();
							if(classPath != null)
							{
								IFolder ifile = project.getWorkspace().getRoot().getFolder(classPath);
								if(!paths.contains(ifile))
								{
									results.add(ifile.getLocation().toString());
									paths.add(ifile);
								}
							}
						}

					}
				}
			}
		}
		catch (JavaModelException e)
		{
			e.printStackTrace();
		}
		return results.toArray(new String[results.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.IJavaUtils#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.IJavaUtils#getResource(java.lang.Object)
	 */
	@Override
	public Object getResource(Object resource) {
    	// If this is a C element then convert it to an IResource
    	if(resource instanceof IJavaProject)
    	{
    		resource = ((IJavaProject)resource).getResource();
    	}
		return resource;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.IJavaUtils#getSourceFile(org.eclipse.core.resources.IFile)
	 */
	@Override
	public IFile getSourceFile(IFile ifile)
	{
		// First find a matching source path to get the package hierarchy for the file
		IPath filePath = ifile.getFullPath();
		String filePathString = filePath.toString();
		String packageString = null;
		for(IPath classPath: targetPaths)
		{
			String classPathString = classPath.toString();
			if(filePathString.startsWith(classPathString))
			{
				packageString = filePathString.replace(classPathString, "");
			}
		}

		// The package string is the java class' package name followed by the
		// class name and .class
		if(packageString != null)
		{
			// Get the expected source file package path
			String javaPath = getJavaPath(packageString);

			// We want to strip the project path from the beginning of
			// the source paths we come up with, since creating an IFile
			// requires a project relative path.
			String projectPath = project.getFullPath().toString();
			int projectPathSize = projectPath.length();

			// Look through the source paths trying to find one within which
			// the expected source package path exists. If we find one it must
			// be the one we want, unless there is a collision in which case
			// Eclipse should be complaining bitterly.
			for(IPath sourcePath: sourcePaths)
			{
				// Make sure to remove the project path from the string
				String sourcePathString = sourcePath.toString().substring(projectPathSize);
				IFile file = project.getFile(sourcePathString + "/" + javaPath);
				if(file.exists())
				{
					return file;
				}
			}

			System.err.println("WARNING: Could not find java file matching " + filePathString);
		}
		return ifile;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.IJavaUtils#getClassFile(org.eclipse.core.resources.IFile)
	 */
	@Override
	public IFile getClassFile(IFile sourceFile)
	{
		if(sourceFile == null || !sourceFile.getName().endsWith(".java")) return null;
		
		String myPath = sourceFile.getFullPath().toString();
		// Remove .java
		myPath = myPath.substring(0, myPath.length() - 5);
		
		// Find matching source path
		for(IPath path: sourcePaths)
		{
			String spath = path.toString();
			
			if(myPath.startsWith(spath))
			{
				myPath = myPath.substring(spath.length());
				break;
			}
		}
		
		String myClass = myPath + ".class";
		
		// Find matching class path
		for(IPath classPath: targetPaths)
		{
			classPath = classPath.removeFirstSegments(1);
			IFolder cp = project.getFolder(classPath);
			if(cp != null)
			{
				IFile cls = cp.getFile(myClass);
				if(cls.exists())
				{
					return cls;
				}
			}
		}
		return null;
	}


	/** Given a "class" string, create the "*.java" string.
	 * 
	 * @param packageString
	 * @return
	 */
	private String getJavaPath(String path)
	{
		// Remove the .class suffix
		if(path.endsWith(".class"))
		{
			path = path.substring(0, path.length() - 6);
		}

		// Remove 'nested class' indicator
		int index = path.lastIndexOf('$');
		if(index > 0)
		{
			path = path.substring(0, index);
		}

		// Add the .java suffix
		path = path + ".java";
		return path;
	}

}
