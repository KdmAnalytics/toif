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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

/** This class manages the running of build visitors concurrently. It can run
 * in two modes (*):
 * 
 *   1. Blocking: This blocks the build process until completion
 *   2. Non-blocking: Runs these builders as a separate non-blocking job
 * 
 *   (*) eventually. Currently only non-blocking is supported.
 * 
 * @author Ken Duck
 *
 */
public class ConcurrentBuildManager implements IBuildJobListener
{
	/**
	 * Assume that jobs should last no longer than 2 minutes each for now.
	 */
	private static final long MAX_TIMEOUT = 120;

	/**
	 * Maximum number of jobs to run on the machine. This version constrains
	 * by the number of processors.
	 */
	private int MAX_JOBS = Runtime.getRuntime().availableProcessors() * 2;
	// private int MAX_JOBS = 2;

	private IResourceVisitor visitor;
	private ExecutorService executor;

	/**
	 * Current set of jobs to execute
	 */
	private Collection<IFile> jobs;

	/**
	 * True while the executor has not been shut down
	 */
	private boolean isRunning = true;

	private List<IBuildJobListener> listeners = new LinkedList<IBuildJobListener>();

	/** Start a concurrent build manager.
	 * 
	 * @param visitor
	 * @param blocking Indicate whether the job queue is blocking or not. Non-blocking
	 *                 will be used when the concurrent manager is running under
	 *                 its own job.
	 */
	public ConcurrentBuildManager(IResourceVisitor visitor, boolean blocking)
	{
		this.visitor = visitor;
		int jobCount = MAX_JOBS;
		
		if(((CommonBuildVisitor)visitor).getMaxJobs() > 0)
		{
			jobCount = ((CommonBuildVisitor)visitor).getMaxJobs();
		}
		
		executor = Executors.newFixedThreadPool(jobCount);
		
		if(blocking)
		{
			jobs = new ArrayBlockingQueue<IFile>(jobCount);
		}
		else
		{
			jobs = new HashSet<IFile>();
		}

		// We need to track completion for the sake of the blocking queue
		listeners.add(this);
	}

	/** Request a new file be visited. This may block depending on the status of
	 * the job pool and whether we are running in blocking mode or not.
	 * 
	 * @param file
	 * @throws CoreException 
	 */
	public void schedule(IFile file) throws CoreException
	{
		// This will block once enough jobs are already submitted
		if(jobs instanceof BlockingQueue)
		{
			try
			{
				((BlockingQueue<IFile>)jobs).put(file);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			jobs.add(file);
		}
		Callable<ConcurrentBuildJob> worker = new ConcurrentBuildJob(visitor, file, listeners);
		executor.submit(worker);
	}

	/**
	 * 
	 * @param file
	 */
	public void build(IFile file)
	{
		executor.execute(new ConcurrentBuildJob(visitor, file, listeners));
	}

	/** Shut down the executor and optionally wait for completion
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void shutdown(boolean wait) throws InterruptedException, ExecutionException
	{
		if(isRunning)
		{
			isRunning = false;
			executor.shutdown();
			if(wait)
			{
				if(!executor.awaitTermination(MAX_TIMEOUT, TimeUnit.SECONDS))
				{
					// Failed to shutdown in time. Force the issue.
					executor.shutdownNow();
				}
			}
		}
	}

	/** Get the number of jobs
	 * 
	 * @return
	 */
	public int getSize()
	{
		return jobs.size();
	}

	public void shutdownNow()
	{
		if(isRunning)
		{
			isRunning = false;
			executor.shutdownNow();
		}
	}

	public boolean isRunning()
	{
		return isRunning;
	}

	/**
	 * 
	 * @param listener
	 */
	public void addBuildJobListener(IBuildJobListener listener)
	{
		this.listeners .add(listener);
	}

	/** Return true if the jobs are all complete
	 * 
	 * @return
	 */
	public boolean done()
	{
		try
		{
			return executor.awaitTermination(500, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.builder.IBuildJobListener#buildStarted(org.eclipse.core.resources.IFile)
	 */
	@Override
	public void buildStarted(IFile file)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.builder.IBuildJobListener#buildCompleted(org.eclipse.core.resources.IFile)
	 */
	@Override
	public void buildCompleted(IFile file)
	{
		// A completed job should be removed from the blocking queue
		jobs.remove(file);
	}

}
