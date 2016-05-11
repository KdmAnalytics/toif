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

import net.ossindex.eclipse.common.impl.CUtilsStub;
import net.ossindex.eclipse.common.impl.JavaUtilsStub;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

/**
 * 
 * @author Ken Duck
 *
 */
public class Utils
{
	public static IJavaUtils jutils = getJavaUtils();
	public static ICUtils cutils = getCUtils();

	/** Get the IResource for JDT and CDT elements
	 *
	 * @param object
	 * @return
	 */
	@Deprecated
	public static Object getResource(Object object)
	{
		return jutils.getResource(cutils.getResource(object));
	}

	/** Get the JavaUtils if possible.
	 * 
	 * @param project
	 * @return
	 */
	public static IJavaUtils getJavaUtils(IProject project)
	{
		IJavaUtils utils = getJavaUtils();
		if(utils != null)
		{
			utils.setProject(project);
		}
		return utils;
	}

	/** Get the JavaUtils if possible.
	 * 
	 * @return
	 */
	public static IJavaUtils getJavaUtils() {
		try
		{
			Class<?> c = Class.forName("net.ossindex.eclipse.common.impl.JavaUtils");
			IJavaUtils utils = (IJavaUtils)c.newInstance();
			return utils;
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoClassDefFoundError e)
		{
			return new JavaUtilsStub();
		} 
	}

	/** Get the CUtils if possible.
	 * 
	 * @param project
	 * @return
	 */
	public static ICUtils getCUtils(IProject project)
	{
		ICUtils utils = getCUtils();
		if(utils != null)
		{
			utils.setProject(project);
		}
		return utils;
	}

	/** Get the CUtils if possible.
	 * 
	 * @return
	 */
	public static ICUtils getCUtils()
	{
		try
		{
			Class<?> c = Class.forName("net.ossindex.eclipse.common.impl.CUtils");
			ICUtils utils = (ICUtils)c.newInstance();
			return utils;
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoClassDefFoundError e)
		{
			return new CUtilsStub();
		} 
	}

	/** Clear builder timestamps that are used to prevent re-build.
	 * 
	 * @param resource
	 * @param builderId
	 */
	public static void resetBuilderTimestamp(IResource resource, String builderId)
	{
		try
		{
			QualifiedName timestampQualifier = new QualifiedName(builderId, ".TIMESTAMP");
			resetBuilderTimestamp(resource, timestampQualifier);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	/** Clear builder timestamps that are used to prevent re-build.
	 * 
	 * @param resource
	 * @param qname
	 * @throws CoreException 
	 */
	private static void resetBuilderTimestamp(IResource resource, QualifiedName qname) throws CoreException
	{
		resource.setPersistentProperty(qname, null);

		if(resource instanceof IContainer)
		{
			IResource[] resources = ((IContainer)resource).members();
			if(resources != null)
			{
				for (IResource child : resources)
				{
					resetBuilderTimestamp(child, qname);
				}
			}
		}
	}

}
