/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.designer.relational.model.RelationalModelFactory;
import org.teiid.designer.relational.model.RelationalProcedure;
import org.teiid.designer.relational.ui.Messages;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.relational.ui.UiPlugin;
import org.teiid.designer.relational.ui.edit.EditRelationalObjectDialog;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.designer.ui.actions.INewChildAction;
import org.teiid.designer.ui.actions.INewSiblingAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

/**
 *
 */
public class CreateRelationalProcedureAction extends Action implements INewChildAction, INewSiblingAction {
	private IFile selectedModel;
	/**
	 * 
	 */
	public static final String TITLE = Messages.createRelationalProcedureActionText;
	 
	private Collection<String> datatypes;
	 
	/**
	 * 
	 */
	public CreateRelationalProcedureAction() {
		super(TITLE);
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor( UiConstants.Images.NEW_PROCEDURE_ICON));
		
		IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
		Set<String> unsortedDatatypes = service.getAllDataTypeNames();
		datatypes = new ArrayList<String>();
		
		String[] sortedStrings = unsortedDatatypes.toArray(new String[unsortedDatatypes.size()]);
		Arrays.sort(sortedStrings);
		for( String dType : sortedStrings ) {
			datatypes.add(dType);
		}
	}
	
    /* (non-Javadoc)
     * @See org.teiid.designer.ui.actions.INewChildAction#canCreateChild(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean canCreateChild(EObject parent) {
    	return false;
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.ui.actions.INewChildAction#canCreateChild(org.eclipse.core.resources.IFile)
     */
    @Override
	public boolean canCreateChild(IFile modelFile) {
    	return isApplicable(new StructuredSelection(modelFile));
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.ui.actions.INewSiblingAction#canCreateChild(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean canCreateSibling(EObject parent) {
    	//Convert eObject selection to IFile
    	ModelResource mr = ModelUtilities.getModelResourceForModelObject(parent);
    	if( mr != null ) {
    		IFile modelFile = null;
    		
    		try {
				modelFile = (IFile)mr.getCorrespondingResource();
			} catch (ModelWorkspaceException ex) {
				UiConstants.Util.log(ex);
			}
    		if( modelFile != null ) {
    			return isApplicable(new StructuredSelection(modelFile));
    		}
    	}
    	
    	return false;
    }
    
	/**
	 * @param selection the selected object
	 * @return if applicable to selection
	 */
	public boolean isApplicable(ISelection selection) {
		boolean result = false;
		if (!SelectionUtilities.isMultiSelection(selection)) {
			Object obj = SelectionUtilities.getSelectedObject(selection);
			if (obj instanceof IResource) {
				IResource iRes = (IResource) obj;
				if (ModelIdentifier.isRelationalSourceModel(iRes)) {
					this.selectedModel = (IFile) obj;
					result = true;
				}
			}
		}

		return result;
	}

	@Override
   public void run() {
		if( selectedModel != null ) {
	        ModelResource mr = ModelUtilities.getModelResource(selectedModel);
	        final Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
	        
	        RelationalProcedure procedure = new RelationalProcedure();
	        
	        SelectProcedureTypeDialog procedureTypeDialog = new SelectProcedureTypeDialog(shell, procedure);
	        
	        procedureTypeDialog.open();
	        
	        if (procedureTypeDialog.getReturnCode() == Window.OK) {
		        // Hand the table off to the generic edit dialog
		        EditRelationalObjectDialog dialog = new EditRelationalObjectDialog(shell, procedure, selectedModel);
		        
		        dialog.open();
		        
		        if (dialog.getReturnCode() == Window.OK) {
		        	createProcedureInTxn(mr, procedure);
		        }
	        }
		}
		
	}

    private void createProcedureInTxn(ModelResource modelResource, RelationalProcedure procedure) {
        boolean requiredStart = ModelerCore.startTxn(true, true, Messages.createRelationalProcedureTitle, this);
        boolean succeeded = false;
        try {
            ModelEditor editor = ModelEditorManager.getModelEditorForFile((IFile)modelResource.getCorrespondingResource(), true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();

                RelationalModelFactory factory = new RelationalModelFactory();
                
                RelationalModel relModel = new RelationalModel("dummy"); //$NON-NLS-1$
                relModel.addChild(procedure);
                
                factory.build(modelResource, relModel, new NullProgressMonitor());
    	        //factory.buildObject(table, modelResource, new NullProgressMonitor());

                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                succeeded = true;
            }
        } catch (Exception e) {
        	MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.createRelationalProcedureExceptionMessage, e.getMessage());
            IStatus status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, Messages.createRelationalProcedureExceptionMessage, e);
            UiConstants.Util.log(status);

            return;
        } finally {
            // if we started the txn, commit it.
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }
    
    class SelectProcedureTypeDialog extends TitleAreaDialog {
    	Button procedureRB, sourceFunctionRB, userDefinedFunctionRB;
    	
    	RelationalProcedure relationalProcedure;
    	
    	public SelectProcedureTypeDialog(Shell parentShell, RelationalProcedure procedure) {
    		super(parentShell);
    		setShellStyle(getShellStyle() | SWT.RESIZE);
    		relationalProcedure = procedure;
    	}
    	
    	/**
    	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
    	 * @since 5.5.3
    	 */
    	@Override
    	protected void configureShell(Shell shell) {
    		super.configureShell(shell);
    		shell.setText(Messages.selectProcedureTypeDialogTitle);
    	}

    	/**
    	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
    	 * @since 5.5.3
    	 */
    	@SuppressWarnings("unused")
		@Override
    	protected Control createDialogArea(Composite parent) {
    		Composite pnlOuter = (Composite) super.createDialogArea(parent);
    		
    		Composite panel = new Composite(pnlOuter, SWT.NONE);
    		GridLayout gridLayout = new GridLayout();
    		gridLayout.numColumns = 1;
    		panel.setLayout(gridLayout);
    		panel.setLayoutData(new GridData(GridData.FILL_BOTH));
    		
    		// COLUMN 1 will be the radio buttons
    		// COLUMN 2 will be the "description" of the procedure type

    		// set title
    		setTitle(Messages.selectProcedureTypeDialogSubTitle);
    		
    		SIMPLE_PROCEDURE : {
	        	this.procedureRB = new Button(panel, SWT.RADIO | SWT.RIGHT);
	            this.procedureRB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
	            this.procedureRB.setText(Messages.procedureLabel);
	            this.procedureRB.addSelectionListener(new SelectionAdapter() {
	                /**            		
	                 * {@inheritDoc}
	                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	                 */
	                @Override
	                public void widgetSelected( SelectionEvent e ) {
	                	handleInfoChanged();
	                }
	            });
	            this.procedureRB.setSelection(!relationalProcedure.isFunction());
	            
	    		Composite subPanel = new Composite(panel, SWT.NONE);
	    		subPanel.setLayout(new GridLayout(2, false));
	    		subPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
	    		
	    		Label spaceLabel = new Label(subPanel, SWT.NONE);
	    		spaceLabel.setText("   "); //$NON-NLS-1$
	    		
    	    	Text descText = new Text(subPanel, SWT.WRAP | SWT.READ_ONLY);
    	    	descText.setBackground(parent.getBackground());
    	    	descText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
    	    	descText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    	    	((GridData)descText.getLayoutData()).horizontalSpan = 1;
    	    	((GridData)descText.getLayoutData()).heightHint = 30;
    	    	((GridData)descText.getLayoutData()).widthHint = 450;
    	    	descText.setText(Messages.createRelationalProcedureDescription);
    		}
            
    		SOURCE_FUNCTION : {
	            this.sourceFunctionRB = new Button(panel, SWT.RADIO | SWT.RIGHT);
	            this.sourceFunctionRB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
	            this.sourceFunctionRB.setText(Messages.sourceFunctionLabel);
	            this.sourceFunctionRB.addSelectionListener(new SelectionAdapter() {
	                /**            		
	                 * {@inheritDoc}
	                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	                 */
	                @Override
	                public void widgetSelected( SelectionEvent e ) {
	                	handleInfoChanged();
	                }
	            });
	            
	    		Composite subPanel = new Composite(panel, SWT.NONE);
	    		subPanel.setLayout(new GridLayout(2, false));
	    		subPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
	    		
	    		Label spaceLabel = new Label(subPanel, SWT.NONE);
	    		spaceLabel.setText("   "); //$NON-NLS-1$
	    		
    	    	Text descText = new Text(subPanel, SWT.WRAP | SWT.READ_ONLY);
    	    	descText.setBackground(parent.getBackground());
    	    	descText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
    	    	descText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    	    	((GridData)descText.getLayoutData()).horizontalSpan = 1;
    	    	((GridData)descText.getLayoutData()).heightHint = 40;
    	    	((GridData)descText.getLayoutData()).widthHint = 450;
    	    	descText.setText(Messages.createRelationalSourceFunctionDescription);
    		}
            
    		USER_DEFINED_FUNCTION : {
	            this.userDefinedFunctionRB = new Button(panel, SWT.RADIO | SWT.RIGHT);
	            this.userDefinedFunctionRB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
	            this.userDefinedFunctionRB.setText(Messages.userDefinedFunctionLabel);
	            this.userDefinedFunctionRB.addSelectionListener(new SelectionAdapter() {
	                /**            		
	                 * {@inheritDoc}
	                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	                 */
	                @Override
	                public void widgetSelected( SelectionEvent e ) {
	                	handleInfoChanged();
	                }
	            });
	            
	    		Composite subPanel = new Composite(panel, SWT.NONE);
	    		subPanel.setLayout(new GridLayout(2, false));
	    		subPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
	    		
	    		Label spaceLabel = new Label(subPanel, SWT.NONE);
	    		spaceLabel.setText("   "); //$NON-NLS-1$
	    		
    	    	Text descText = new Text(subPanel, SWT.WRAP | SWT.READ_ONLY);
    	    	descText.setBackground(parent.getBackground());
    	    	descText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
    	    	descText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    	    	((GridData)descText.getLayoutData()).horizontalSpan = 1;
    	    	((GridData)descText.getLayoutData()).heightHint = 40;
    	    	((GridData)descText.getLayoutData()).widthHint = 450;
    	    	descText.setText(Messages.createRelationalUserDefinedFunctionDescription);
    		}
            return panel;
    	}
    	
    	private void handleInfoChanged() {
        	boolean isUDF = userDefinedFunctionRB.getSelection();
        	boolean isSourceFunction = sourceFunctionRB.getSelection();
        	if( isUDF ) {
        		relationalProcedure.setFunction(true);
        		relationalProcedure.setSourceFunction(false);
        	} else if( isSourceFunction ) {
        		relationalProcedure.setFunction(true);
        		relationalProcedure.setSourceFunction(true);
        	} else {
        		relationalProcedure.setFunction(false);
        		relationalProcedure.setSourceFunction(false);
        	}
    	}

    }
}