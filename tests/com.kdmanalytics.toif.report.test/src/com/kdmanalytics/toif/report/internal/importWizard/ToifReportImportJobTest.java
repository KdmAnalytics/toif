
package com.kdmanalytics.toif.report.internal.importWizard;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.kdmanalytics.toif.report.internal.handlers.ModelUtil;

/**
 * @author "Adam Nunn <adam@kdmanalytics.com>"
 * 
 */
public class ToifReportImportJobTest
{
    
    private IProject project;
    
    private SWTWorkbenchBot bot = new SWTWorkbenchBot();
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }
    
    @Before
    public void setUp() throws Exception
    {
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IWorkspaceRoot root = workspace.getRoot();
        final IProject newProject = root.getProject("testProject");
        
        try
        {
            
            final IProjectDescription description = workspace.newProjectDescription(newProject.getName());
            
            project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
            project.create(description, null);
            project.open(null);
        }
        catch (CoreException exception_p)
        {
            exception_p.printStackTrace();
        }
        
        System.out.println("TestProject Created...");
    }
    
    @After
    public void tearDown() throws Exception
    {
        
        project.delete(true, null);
        
        System.out.println("testProject Deleted...");
    }
    
    @Test
    public void ToifReportImportJobTest() throws InterruptedException
    {
        System.out.println("Testing ToifReportImportJob... " + project);
        
        final ToifReportImportJob job = new ToifReportImportJob("Import SFP/CWE Data", project, "inputs/output.kdm");
        job.setUser(true);
        job.setPriority(Job.BUILD);
        job.setRule(project);
        job.schedule();
        
        job.join();
        
        StructuredSelection s = new StructuredSelection(project);
        ModelUtil.buildModel(s);
        
        
        
        SWTBotView notesBot = bot.viewById("com.kdmanalytics.toif.report.view");
        
        SWTBotTable table = bot.activeShell().bot().table();
        
        assertEquals(2, table.rowCount());
        
        System.out.println("Finished ToifReportImportJob...");
    }
   
}
