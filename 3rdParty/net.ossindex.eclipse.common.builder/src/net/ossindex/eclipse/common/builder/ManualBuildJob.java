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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.PlatformUI;

import net.ossindex.eclipse.common.IJavaUtils;
import net.ossindex.eclipse.common.Utils;
import net.ossindex.eclipse.common.builder.service.ICommonBuildService;

/** Provide a "manual build" which is separate from the standard Eclipse build
 * process. The manual build is always a full build.
 * 
 * @author Ken Duck
 *
 */
public class ManualBuildJob extends Job
{
	public static final String MANUAL_BUILD = "MANUAL_BUILD";
	public static final String MANUAL_BUILD_ALL = "MANUAL_BUILD_ALL";
	public List<IResource> resources;
	private IJavaUtils jutils = Utils.getJavaUtils();

	public ManualBuildJob(List<IResource> resources)
	{
		super("Manual Build");

		this.resources = resources;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor)
	{
		if(!resources.isEmpty())
		{
			IProject project = null;
			for (IResource resource : resources)
			{
				if(resource instanceof IProject)
				{
					project = (IProject) resource;
				}
			}
			// One of the selected items is the project, do full build
			if(project != null)
			{
				// Full build
				return run(project, null, monitor);
			}
			else
			{
				// Otherwise do an incremental build
				project = resources.get(0).getProject();
				return run(project, resources, monitor);
			}
		}
		return Status.OK_STATUS;
	}
	/** Run a build on the selected project, optionally passing a list of resources
	 * as build arguments.
	 * 
	 * @param project
	 * @param resources 
	 * @param monitor
	 * @return
	 */
	private IStatus run(IProject project, List<IResource> resources, IProgressMonitor monitor)
	{
		System.err.println("Start manual build... ");

		SubMonitor progress = SubMonitor.convert(monitor);
		jutils.setProject(project);

		try
		{
			// Only perform builds on builders registered with our build service
			ICommonBuildService buildService = (ICommonBuildService) PlatformUI.getWorkbench().getService(ICommonBuildService.class);

			List<ICommand> commands = new LinkedList<ICommand>();
			IProjectDescription desc = project.getDescription();
			for (ICommand command : desc.getBuildSpec())
			{
				String name = command.getBuilderName();
				if(buildService.containsBuilder(name))
				{
					commands.add(command);
				}
			}

			Map<String,String> args = new HashMap<String,String>();

			if(resources != null)
			{
				// For a build of selected resources, ensure the resources
				// are added to a lookup table.
				args.put("type", MANUAL_BUILD);
				for(IResource resource: resources)
				{
					addFiles(args, resource);
				}
			}
			else
			{
				args.put("type", MANUAL_BUILD_ALL);
			}

			progress.setWorkRemaining(commands.size());
			for (ICommand command : commands)
			{
				System.err.println("Running builder " + command.getBuilderName() + "...");
				try
				{
					project.build(IncrementalProjectBuilder.FULL_BUILD, command.getBuilderName(), args, progress.newChild(1));
				}
				catch (CoreException e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}

		System.err.println("Manual build complete");
		return Status.OK_STATUS;
	}

	/** Recursively add all files to the lookup map
	 * 
	 * @param map
	 * @param resource
	 */
	private void addFiles(Map<String, String> map, IResource resource)
	{
		if(resource instanceof IFile)
		{
			map.put(resource.getLocation().toString(), "BUILD");
			IFile classFile = jutils.getClassFile((IFile)resource);
			if(classFile != null) map.put(classFile.getLocation().toString(), "BUILD");
		}
		else if(resource instanceof IContainer)
		{
			try
			{
				IResource[] children = ((IContainer)resource).members();
				for (IResource child : children)
				{
					addFiles(map, child);
				}
			}
			catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
