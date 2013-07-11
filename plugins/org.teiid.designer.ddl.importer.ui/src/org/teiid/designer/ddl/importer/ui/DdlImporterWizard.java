package org.teiid.designer.ddl.importer.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.teiid.designer.core.workspace.DotProjectUtils;
import org.teiid.designer.ddl.importer.DdlImporter;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.widget.ListMessageDialog;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.ui.common.wizard.IPersistentWizardPage;
import org.teiid.designer.ui.viewsupport.ModelerUiViewUtils;


/**
 * Wizard Imports DDL and generates a Relational Model
 */
public class DdlImporterWizard extends AbstractWizard implements IImportWizard {

    DdlImporter importer;
    private DdlImporterPage srcPg;
    private DdlImportDifferencesPage diffPage;

    /**
     * DdlImporterWizard constructor
     */
    public DdlImporterWizard() {
        super(DdlImporterUiPlugin.singleton(), DdlImporterUiI18n.WIZARD_TITLE, DdlImporterUiPlugin.imageDescriptor("importWizard75x58.gif"));  //$NON-NLS-1$
        
        final IDialogSettings pluginSettings = DdlImporterUiPlugin.singleton().getDialogSettings();
        final String sectionName = DdlImporterWizard.class.getSimpleName();
        IDialogSettings section = pluginSettings.getSection(sectionName);
        if (section == null) section = pluginSettings.addNewSection(sectionName);
        setDialogSettings(section);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        addPage(srcPg);
        addPage(diffPage);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     * @since 4.0
     */
    @Override
    public void init( final IWorkbench workbench,
                      final IStructuredSelection selection ) {
        
        IStructuredSelection finalSelection = selection;
        if( (finalSelection == null | finalSelection.isEmpty()) && !ModelerUiViewUtils.workspaceHasOpenModelProjects() ) {
        	IProject newProject = ModelerUiViewUtils.queryUserToCreateModelProject();
        	
        	if( newProject != null ) {
        		finalSelection = new StructuredSelection(newProject);
        	}
        }
        final Collection<IProject> projects = DotProjectUtils.getOpenModelProjects();
        IProject[] projectArray = projects.toArray(new IProject[0]);
        
        importer = new DdlImporter(projectArray);
        
        // First Page defines source DDL and target model
        srcPg = new DdlImporterPage(importer, projectArray, finalSelection);
        
        // Second Page for presentation of differences - allows user selection
        diffPage = new DdlImportDifferencesPage(importer);
        
    }
    
    /**
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     * @since 4.0
     */
    @Override
    public boolean canFinish() {
        boolean result = false;
        IWizardPage currentPage = getContainer().getCurrentPage();

        if (currentPage == this.diffPage) {
            result = currentPage.isPageComplete();
        } 
        return result;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
        try {
        	// Use the importer to process the difference report, generating the model
            if (importer.getDifferenceReport() == null) return false;
            
            new ProgressMonitorDialog(getShell()).run(false, false, new IRunnableWithProgress() {

                @Override
                public void run( final IProgressMonitor monitor ) {
                    monitor.beginTask(DdlImporterUiI18n.IMPORTING_DDL_MSG, 100);
                    monitor.worked(50);
                    importer.save(monitor, 50);
                    monitor.done();
                }
            });
            // Show Warning Dialog if the importer finished, but with warnings
            IStatus importStatus = importer.getImportStatus();
            if(importStatus!=null && importStatus.getSeverity()==IStatus.WARNING) {
            	if(importStatus instanceof MultiStatus) {
                	final List<String> msgs = new ArrayList<String>(importStatus.getChildren().length);
                	for( int i=0; i<importStatus.getChildren().length; i++) {
                		msgs.add(importStatus.getChildren()[i].getMessage());
                	}
                	ListMessageDialog.openInformation(getShell(), DdlImporterUiI18n.IMPORT_COMPLETED_WITH_WARNINGS_TITLE, null, DdlImporterUiI18n.IMPORT_COMPLETED_WITH_WARNINGS_MESSAGE, msgs , null);
            	} else {
            		MessageDialog.openWarning(getShell(), DdlImporterUiI18n.IMPORT_COMPLETED_WITH_WARNINGS_TITLE, importStatus.getMessage());
            	}
            	return true;
            } 
        } catch (final InterruptedException error) {
            return false;
        } catch (final Exception error) {
            error.printStackTrace();
            WidgetUtil.showError(error);
            return false;
        }
        // Save user settings
        for (final IWizardPage pg : getPages())
            if (pg instanceof IPersistentWizardPage) ((IPersistentWizardPage)pg).saveSettings();
        return true;
    }
    
}
