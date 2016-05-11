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

import java.util.concurrent.ExecutionException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

/** Runs the concurrent build manager in a separate eclipse job. This will detach
 * it from the build.
 * 
 * @author Ken Duck
 *
 */
public class ConcurrentBuildManagerJob extends Job implements IBuildJobListener
{

	private ConcurrentBuildManager buildManager;
	private SubMonitor progress;
	private int size;
	private int started = 0;
	private int completed = 0;

	public ConcurrentBuildManagerJob(ConcurrentBuildManager manager)
	{
		super("Concurrent build manager");
		this.buildManager = manager;
		manager.addBuildJobListener(this);
	}

	/** Run each of the concurrent builds, showing progress as they complete.
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor)
	{
		this.size = buildManager.getSize();
		SubMonitor progress = SubMonitor.convert(monitor, size);
		this.progress = progress;
		
		// Fun all the jobs.
		//for(Future<ConcurrentBuildJob> future: buildManager)
		while(!buildManager.done())
		{
			System.err.println("Check completion...");
			// Get out if cancelled
			if(progress.isCanceled() || size == 0)
			{
				buildManager.shutdownNow();
				break;
			}
			
//			try
//			{
//				future.get();
//			}
//			catch (InterruptedException | ExecutionException e)
//			{
//				e.printStackTrace();
//			}
		}
		
		try
		{
			if(buildManager.isRunning())
			{
				buildManager.shutdown(false);
			}
		}
		catch (InterruptedException | ExecutionException e)
		{
			e.printStackTrace();
		}
		return Status.OK_STATUS;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.builder.IBuildJobListener#buildStarted(org.eclipse.core.resources.IFile)
	 */
	@Override
	public void buildStarted(IFile file) {
		started++;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.builder.IBuildJobListener#buildCompleted(org.eclipse.core.resources.IFile)
	 */
	@Override
	public void buildCompleted(IFile file) {
		completed++;
		progress.setTaskName("Concurrent build: [" + completed + "/" + size + "] " + file.getName());
		System.err.println("COMPLETED: [" + completed + "/" + size + "] " + file.getName());
		progress.worked(1);
		if(completed >= size)
		{
			buildManager.shutdownNow();
		}
	}

}
