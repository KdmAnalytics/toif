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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

/** 
 * 
 * @author Ken Duck
 *
 */
public abstract class CommonBuilder extends IncrementalProjectBuilder
{
	/**
	 * For debug purposes only
	 */
	private static final boolean IGNORE_BATCH = false;

	/**
	 * Indicate whether we want the concurrent build to block or not
	 */
	private static final boolean CONCURRENT_BLOCKING = true;

	public CommonBuilder()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException
	{
		// get the project to build  
		getProject();  

		if(args != null)
		{
			// On a manual build all, force a full clean
			if(ManualBuildJob.MANUAL_BUILD_ALL.equals(args.get("type")))
			{
				CommonBuildVisitor visitor = (CommonBuildVisitor)getBuildVisitor(null);

				if(visitor != null)
				{
					visitor.setProgressMonitor(monitor);

					visitor.clean(getProject());
				}
			}
			// On a manual build, force a special incremental build
			if(ManualBuildJob.MANUAL_BUILD.equals(args.get("type")))
			{
				manualIncrementalBuild(args, monitor);
				return null;
			}
		}

		if(kind == FULL_BUILD)
		{
			fullBuild(monitor);
		}
		else
		{
			IResourceDelta delta = getDelta(getProject());
			incrementalBuild(delta, monitor);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#clean(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void clean(IProgressMonitor monitor)
	{
		// Handle cancellation
		if(monitor.isCanceled()) return;
	}

	/**
	 * 
	 * @param monitor
	 * @param args 
	 */
	private void fullBuild(IProgressMonitor monitor)
	{
		try
		{
			CommonBuildVisitor visitor = (CommonBuildVisitor)getBuildVisitor(null);
			if(!IGNORE_BATCH && (visitor instanceof IBatchBuildVisitor))
			{
				if(((IBatchBuildVisitor)visitor).areFilesDirty(getProject()))
				{
					visitor.setProgressMonitor(monitor);

					((IBatchBuildVisitor)visitor).buildAll(getProject());
					((IBatchBuildVisitor)visitor).markAllBuilt(getProject());
				}
			}
			else
			{
				if(visitor != null)
				{
					visitor.setProgressMonitor(monitor);
					List<IFile> changed = getFilesToBuild(getProject(), visitor);
					buildFiles(changed, monitor);
				}
			}
		}
		catch (CoreException e)
		{
			System.err.println("Exception performing full build");
			e.printStackTrace();
		}
	}

	/** A Build Visitor is a class that actually performs the build operations.
	 * @param monitor 
	 * 
	 * @return
	 */
	protected abstract IResourceVisitor getBuildVisitor(IProgressMonitor monitor);

	/** Special manual incremental build
	 * 
	 * @param args
	 * @param monitor
	 */
	private void manualIncrementalBuild(Map<String, String> args, IProgressMonitor monitor)
	{
		final CommonBuildVisitor visitor = (CommonBuildVisitor)getDeltaVisitor(null);

		if(visitor != null)
		{
			IProject project = getProject();
			// Get a full list of requested files
			final List<IFile> changed = getChangedFiles(project, args, visitor);
			
			// Clean the files
			for (IFile file : changed)
			{
				visitor.clean(file);
			}
			buildFiles(changed, monitor);
		}
	}

	/** Get a list of files from a project that were requested for a manual build
	 * 
	 * @param resource
	 * @param args
	 * @param visitor
	 * @return
	 */
	private List<IFile> getChangedFiles(IResource resource, Map<String, String> args, CommonBuildVisitor visitor)
	{
		final List<IFile> changed = new LinkedList<IFile>();
		if(resource instanceof IFile)
		{
			if(visitor.accepts((IFile)resource) && args.containsKey(resource.getLocation().toString()))
			{
				changed.add((IFile) resource);
			}
		}
		else if(resource instanceof IContainer)
		{
			try
			{
				IResource[] children = ((IContainer)resource).members();
				for (IResource child : children)
				{
					List<IFile> files = getChangedFiles(child, args, visitor);
					changed.addAll(files);
				}
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}
		}
		return changed;
	}

	/**
	 * 
	 * @param delta
	 * @param monitor
	 */
	private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor)
	{
		try
		{
			final CommonBuildVisitor visitor = (CommonBuildVisitor)getDeltaVisitor(null);

			if(visitor != null)
			{
				// Get a full list of changed files. We want to do this instead of the
				// visitor so we know exactly how many files there are. This will be
				// used to provide better progress monitoring, but more importantly
				// it will allow us to decide whether to do batch processing
				// or not.
				final List<IFile> changed = getChangedFiles(delta, visitor);

				if(changed.size() > 0)
				{
					if(!IGNORE_BATCH && (visitor instanceof IBatchBuildVisitor))
					{
						if(changed.size() > getFullBuildThreshold())
						{
							visitor.setProgressMonitor(monitor);
							((IBatchBuildVisitor)visitor).buildAll(getProject());
							((IBatchBuildVisitor)visitor).markAllBuilt(getProject());
							return;
						}
					}

					// If we get here, then perform individual builds
					buildFiles(changed, monitor);
				}
			}
		}
		catch (CoreException e)
		{
			System.err.println("Exception performing incremental build");
			e.printStackTrace();
		}
	}

	/** Given a delta, get a list of all changed files
	 * 
	 * @param delta
	 * @param visitor
	 * @return
	 * @throws CoreException
	 */
	private List<IFile> getChangedFiles(IResourceDelta delta, final CommonBuildVisitor visitor) throws CoreException
	{
		final List<IFile> changed = new LinkedList<IFile>();
		IResourceDeltaVisitor deltaVisitor = new IResourceDeltaVisitor()
		{
			public boolean visit(IResourceDelta delta)
			{
				//only interested in content changes and added files
				if ((delta.getFlags() & IResourceDelta.CONTENT) == 0 &&
						(delta.getKind() & IResourceDelta.ADDED) == 0) return true;

				IResource resource = delta.getResource();
				if (resource instanceof IFile)
				{
					if(visitor.accepts((IFile)resource))
					{
						changed.add((IFile)resource);
					}
					else if(resource instanceof IContainer)
					{
						return visitor.acceptsContainer((IContainer)resource);
					}
				}
				return true;
			}
		};

		delta.accept(deltaVisitor);
		return changed;
	}

	/** Find all files in the project that we are interested in building.
	 * 
	 * @param project
	 * @return
	 * @throws CoreException 
	 */
	private List<IFile> getFilesToBuild(IProject project, final CommonBuildVisitor visitor) throws CoreException
	{
		final List<IFile> changed = new LinkedList<IFile>();

		IResourceVisitor deltaVisitor = new IResourceVisitor ()
		{
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
			 */
			@Override
			public boolean visit(IResource resource) throws CoreException
			{
				if (resource instanceof IFile)
				{
					if(visitor.accepts((IFile)resource))
					{
						changed.add((IFile)resource);
					}
				}
				else if(resource instanceof IContainer)
				{
					return visitor.acceptsContainer((IContainer)resource);
				}
				return true;
			}

		};

		project.accept(deltaVisitor);
		return changed;
	}

	/** Number of individual files we are willing to build before forcing a full build.
	 * 
	 * @return
	 */
	protected int getFullBuildThreshold()
	{
		return 10;
	}

	/** Build a list of files
	 * 
	 * @param changed
	 * @param monitor
	 */
	private void buildFiles(List<IFile> changed, IProgressMonitor monitor)
	{
		IResourceVisitor visitor = getBuildVisitor(null);
		((CommonBuildVisitor)visitor).setProgressMonitor(monitor);
		if(visitor instanceof IConcurrentBuildVisitor)
		{
			buildConcurrent(visitor, changed, monitor);
		}
		else
		{
			buildSequential(visitor, changed, monitor);
		}

		// Tell the visitor that the project is complete. This allows it
		// to perform post-build actions. This happens for DelayedBuilds
		// and ConcurrentBuilds.
		if(visitor instanceof IDelayedBuild)
		{
			((IDelayedBuild)visitor).finish(getProject());
		}
	}

	/** Build each file sequentially
	 * 
	 * @param visitor
	 * @param changed
	 * @param monitor
	 */
	private void buildSequential(IResourceVisitor visitor, List<IFile> changed, IProgressMonitor monitor)
	{
		SubMonitor progress = SubMonitor.convert(monitor);
		progress.setWorkRemaining(changed.size());

		int index = 0;
		int size = changed.size();

		for (IFile file : changed)
		{
			index++;
			progress.setTaskName("Processing [" + index + "/" + size + "] " + file.getName() + " {" + getName() + "}");
			try
			{
				visitor.visit(file);
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}
			progress.worked(1);
		}
	}

	/** Get a name for this builder. Used in progress messages.
	 * 
	 * @return
	 */
	protected abstract String getName();

	/** Build concurrently. This involves having a thread pool which we spool the
	 * various "visits" to. We need to wait at the end for all threads to exit.
	 * 
	 * @param visitor
	 * @param changed
	 * @param monitor
	 */
	private void buildConcurrent(IResourceVisitor visitor, List<IFile> changed, IProgressMonitor monitor)
	{
		SubMonitor progress = SubMonitor.convert(monitor);
		int size = changed.size();
		progress.setWorkRemaining(size);

		ConcurrentBuildManager manager = new ConcurrentBuildManager(visitor, CONCURRENT_BLOCKING);

		int index = 0;
		for (IFile file : changed)
		{
			index++;
			progress.setTaskName("Scheduling [" + index + "/" + size + "] " + file.getName() + " {" + getName() + "}");
			try
			{
				manager.schedule(file);
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}
			progress.worked(1);
		}

		// Non-blocking mode requires a separate job
		if(!CONCURRENT_BLOCKING)
		{
			// Non-blocking mode
			// Make a job to monitor the concurrent build process
			Job job = new ConcurrentBuildManagerJob(manager);
			// Start the Job
			//job.setUser(true);
			job.setPriority(Job.BUILD);
			job.schedule();

			// Blocking mode
			//		// Wait for all jobs to complete
			//		try
			//		{
			//			progress.setTaskName("Finalizing concurrent build");
			//			manager.shutdown();
			//		}
			//		catch (InterruptedException | ExecutionException e)
			//		{
			//			e.printStackTrace();
			//		}
		}
		else
		{
			try
			{
				manager.shutdown(true);
			}
			catch (InterruptedException | ExecutionException e)
			{
				e.printStackTrace();
			}
		}
	}

	protected abstract IResourceDeltaVisitor getDeltaVisitor(IProgressMonitor monitor);
}
