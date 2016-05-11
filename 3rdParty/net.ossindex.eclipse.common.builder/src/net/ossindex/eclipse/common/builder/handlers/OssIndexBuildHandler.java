package net.ossindex.eclipse.common.builder.handlers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import net.ossindex.eclipse.common.builder.ManualBuildJob;
import net.ossindex.eclipse.common.builder.service.ICommonBuildService;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class OssIndexBuildHandler extends AbstractHandler implements IElementUpdater
{
	/**
	 * The constructor.
	 */
	public OssIndexBuildHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				IWorkbench wb = PlatformUI.getWorkbench();
				final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				ISelectionService selectionService = win.getSelectionService();
				ISelection selection = selectionService.getSelection();
				if(selection instanceof ITreeSelection)
				{
					ITreeSelection s = (ITreeSelection)selection;
					List<IResource> resources = new LinkedList<IResource>();
					for(@SuppressWarnings("unchecked")
					Iterator<Object> it = s.iterator(); it.hasNext();)
					{
						Object o = it.next();
						if (o instanceof IAdaptable)
						{
							IAdaptable adapt=(IAdaptable)o;
							o = (IResource)adapt.getAdapter(IResource.class);
						}
						if(o instanceof IResource)
						{
							resources.add((IResource)o);
						}
					}
					if(!resources.isEmpty())
					{
						ManualBuildJob job = new ManualBuildJob(resources);
						job.setUser(true);
						job.setPriority(Job.LONG);
						job.schedule();
					}
				}
			}
		});
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.commands.IElementUpdater#updateElement(org.eclipse.ui.menus.UIElement, java.util.Map)
	 */
	@Override
	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters)
	{
		ICommonBuildService buildService = (ICommonBuildService) PlatformUI.getWorkbench().getService(ICommonBuildService.class);
		element.setText(buildService.getMenuText());
		element.setTooltip(buildService.getMenuText());
		ImageDescriptor icon = buildService.getIcon();
		if(icon != null) element.setIcon(icon);
	}
}
