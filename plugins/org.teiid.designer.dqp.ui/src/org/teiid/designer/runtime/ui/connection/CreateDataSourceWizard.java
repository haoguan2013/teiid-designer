/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IProfileListener;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.internal.ui.wizards.NewCPWizard;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.ui.actions.EditConnectionProfileAction;
import org.teiid.designer.runtime.ExecutionAdmin;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * 
 */
public class CreateDataSourceWizard extends AbstractWizard {
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(CreateDataSourceWizard.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$

    //private static final ImageDescriptor IMAGE = DqpUiPlugin.getDefault().getImageDescriptor("icons/full/wizban/dataPolicyWizard.png"); //$NON-NLS-1$

    private static final String DEFAULT_NAME = getString("defaultName"); //$NON-NLS-1$
    private static final String PASSWORD = getString("passwordStr"); //$NON-NLS-1$
    private static final String HIDDEN_PASSWORD = "********"; //$NON-NLS-1$
    private static final String INVALID_CHARS = "; @ # $ % ^ & * ( ) [ ] { } = | ! < > ? \\"; //$NON-NLS-1$
    private static final String NEW_BUTTON = DqpUiConstants.UTIL.getString("Button.newLabel"); //$NON-NLS-1$
    private static final String EDIT_BUTTON = DqpUiConstants.UTIL.getString("Button.editLabel"); //$NON-NLS-1$

    private static String getString( final String id ) {
        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id);
    }

    private static String getString( final String id,
                                     final Object value ) {
        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id, value);
    }

    private ModelResource selectedModelResource;
    // private TeiidDataSource teiidDataSource;
    private String dataSourceName;

    private WizardPage wizardPage;
    private Combo modelsCombo;
    private Button useModelCheckBox;
    private Combo connectionProfilesCombo;
    private Button useConnectionProfileCheckBox;
    private Button newCPButton;
    private Button editCPButton;
    private ILabelProvider profileLabelProvider;
    private Text dataSourceNameText;
    private TableViewer propsViewer;
    private TableContentProvider propertiesContentProvider;

    Map<String, ModelResource> relationalModelsMap;

    private ExecutionAdmin admin;
//    private JdbcManager jdbcManager;
    private ConnectionInfoProviderFactory providerFactory;
    private IConnectionProfile selectedProfile;
    private StringNameValidator dataSourceNameValidator;

    private boolean hasModelResources = false;
    private boolean hasConnectionProfiles = false;

    private Properties teiidDataSourceProperties;
    private IConnectionInfoProvider currentProvider;

    /**
     * @since 4.0
     */
    public CreateDataSourceWizard( ExecutionAdmin admin,
                                   Collection<ModelResource> relationalModels,
                                   ModelResource initialSelection ) {
        super(DqpUiPlugin.getDefault(), TITLE, null);
        this.relationalModelsMap = new HashMap<String, ModelResource>();
        for (ModelResource mr : relationalModels) {
            this.relationalModelsMap.put(ModelUtil.getName(mr), mr);
        }
        this.hasModelResources = !relationalModelsMap.isEmpty();
        this.admin = admin;
        this.selectedModelResource = initialSelection;
        this.hasConnectionProfiles = ProfileManager.getInstance().getProfiles().length > 0; //!jdbcManager.getJdbcSources().isEmpty();
        this.providerFactory = new ConnectionInfoProviderFactory();
        this.teiidDataSourceProperties = new Properties();
    }

    /**
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init( IWorkbench workbench,
                      IStructuredSelection selection ) {
        this.wizardPage = new WizardPage(CreateDataSourceWizard.class.getSimpleName(), TITLE, null) {
            public void createControl( final Composite parent ) {
                setControl(createPageControl(parent));
            }
        };

        this.wizardPage.setPageComplete(false);
        this.wizardPage.setMessage(getString("initialMessage")); //$NON-NLS-1$

        addPage(wizardPage);
    }

    /**
     * @param parent
     * @return composite the page
     * @since 4.0
     */
    Composite createPageControl( final Composite parent ) {

        // ===========>>>> Create page composite
        final Composite mainPanel = new Composite(parent, SWT.NONE);
        mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
        mainPanel.setLayout(new GridLayout(2, false));

        WidgetFactory.createLabel(mainPanel, getString("teiidServer.label")); //$NON-NLS-1$
        WidgetFactory.createLabel(mainPanel, GridData.FILL_HORIZONTAL, 1, admin.getServer().getUrl());

        WidgetFactory.createLabel(mainPanel, getString("name.label")); //$NON-NLS-1$

        dataSourceName = DEFAULT_NAME;
        if (selectedModelResource != null) {
            dataSourceName = ModelUtil.getName(selectedModelResource);
        }

        this.dataSourceNameText = WidgetFactory.createTextField(mainPanel, GridData.FILL_HORIZONTAL, 1, dataSourceName);

        this.dataSourceNameText.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent e ) {
                dataSourceName = dataSourceNameText.getText();
                validateInputs();
            }
        });

        // ===========>>>> Create Connections Group
        final Group connectionSourceGroup = WidgetFactory.createGroup(mainPanel,
                                                                      getString("connectionSourceGroup.label"), GridData.FILL_HORIZONTAL, 2, 4); //$NON-NLS-1$

        // ===========>>>> Models selection
        boolean useModelRes = selectedModelResource != null;
        useModelCheckBox = WidgetFactory.createCheckBox(connectionSourceGroup, getString("useModelInfo.label"), 0, 4, useModelRes); //$NON-NLS-1$
        useModelCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                if (hasModelResources) {
                    modelsCombo.setEnabled(useModelCheckBox.getSelection());
                    connectionProfilesCombo.setEnabled(!useModelCheckBox.getSelection());
                    useConnectionProfileCheckBox.setSelection(!useModelCheckBox.getSelection());

                    handleModelResourceSelection();

                    setConnectionProperties();

                    propsViewer.refresh();

                    validateInputs();
                }
            }
        });

        WidgetFactory.createLabel(connectionSourceGroup, getString("model.label")); //$NON-NLS-1$
        ArrayList modelsList = new ArrayList(relationalModelsMap.values());

        ILabelProvider modelsLabelProvider = new LabelProvider() {

            @Override
            public String getText( final Object source ) {
                return ModelUtil.getName((ModelResource)source);
            }

            @Override
            public Image getImage( final Object source ) {
                return ModelIdentifier.getModelImage((ModelResource)source);
            }
        };
        if (useModelRes) {
            this.modelsCombo = WidgetFactory.createCombo(connectionSourceGroup,
                                                         SWT.READ_ONLY,
                                                         GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER,
                                                         modelsList,
                                                         selectedModelResource,
                                                         modelsLabelProvider,
                                                         true);
        } else {
            this.modelsCombo = WidgetFactory.createCombo(connectionSourceGroup,
                                                         SWT.READ_ONLY,
                                                         GridData.FILL_HORIZONTAL,
                                                         modelsList,
                                                         modelsLabelProvider,
                                                         true);
        }
        this.modelsCombo.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
                if (useModelCheckBox.getSelection()) {
                    handleModelResourceSelection();
                }
            }
        });

        this.modelsCombo.setVisibleItemCount(10);
        GridData modelsComboGD = new GridData(GridData.FILL_BOTH);
        modelsComboGD.horizontalSpan = 3;
        this.modelsCombo.setLayoutData(modelsComboGD);

        // ===========>>>> Connection Profiles
        useConnectionProfileCheckBox = WidgetFactory.createCheckBox(connectionSourceGroup, getString("useProfileInfo.label"), //$NON-NLS-1$
        															0,
                                                                    4,
                                                                    !useModelRes);
        useConnectionProfileCheckBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                connectionProfilesCombo.setEnabled(useConnectionProfileCheckBox.getSelection());
                modelsCombo.setEnabled(!useConnectionProfileCheckBox.getSelection());
                useModelCheckBox.setSelection(!useConnectionProfileCheckBox.getSelection());

                handleConnectionProfileSelected();

                propsViewer.refresh();

                validateInputs();
            }
        });

        WidgetFactory.createLabel(connectionSourceGroup, getString("connectionProfile.label")); //$NON-NLS-1$
        ArrayList sourceList = new ArrayList(); //this.jdbcManager.getJdbcSources().size());
        for( IConnectionProfile prof : ProfileManager.getInstance().getProfiles()) {
        	sourceList.add(prof);
        }

        profileLabelProvider = new LabelProvider() {

            @Override
            public String getText( final Object source ) {
                return ((IConnectionProfile)source).getName();
            }
        };
        this.connectionProfilesCombo = WidgetFactory.createCombo(connectionSourceGroup,
                                                                 SWT.READ_ONLY,
                                                                 GridData.FILL_HORIZONTAL,
                                                                 sourceList,
                                                                 profileLabelProvider,
                                                                 true);
        this.connectionProfilesCombo.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
                if (useConnectionProfileCheckBox.getSelection()) {
                    handleConnectionProfileSelected();
                }
            }
        });

        this.connectionProfilesCombo.setVisibleItemCount(10);
        newCPButton = WidgetFactory.createButton(connectionSourceGroup, NEW_BUTTON);
        newCPButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                launchSourceWizard();
            }
        });
        
        editCPButton = WidgetFactory.createButton(connectionSourceGroup, EDIT_BUTTON);
        editCPButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                editConnectionProfile();
            }
        });

        this.useConnectionProfileCheckBox.setSelection(!useModelRes);
        this.connectionProfilesCombo.setEnabled(!useModelRes);
        this.modelsCombo.setEnabled(useModelRes);

        // ===========>>>>
        Group propsGroup = WidgetFactory.createGroup(mainPanel, getString("connectionProperties.label"), GridData.FILL_BOTH, 2, 2); //$NON-NLS-1$

        final GridData propertiesGridData = new GridData(GridData.FILL_BOTH);
        propertiesGridData.horizontalSpan = 2;
        propertiesGridData.heightHint = 220;
        propertiesGridData.minimumHeight = 220;
        propertiesGridData.grabExcessVerticalSpace = true;
        propsGroup.setLayoutData(propertiesGridData);

        int tableStyle = SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION;
        propsViewer = new TableViewer(propsGroup, tableStyle);
        Table table = propsViewer.getTable();

        final GridData gridData = new GridData(GridData.FILL_BOTH); // SWT.FILL, SWT.FILL, true, true);
        gridData.grabExcessHorizontalSpace = true;
        table.setLayoutData(gridData);

        /*** Tree table specific code starts ***/

        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        TableColumn column1 = new TableColumn(table, SWT.NONE);
        column1.setText(getString("name.label")); //$NON-NLS-1$
        column1.setWidth(200);
        TableColumn column2 = new TableColumn(table, SWT.NONE);
        column2.setText(getString("value.label")); //$NON-NLS-1$
        column2.setWidth(50);
        table.pack();

        GridData columnData = new GridData(GridData.FILL_BOTH);
        table.setLayoutData(columnData);

        // table.setLayout(layout);

        propertiesContentProvider = new TableContentProvider();
        propsViewer.setContentProvider(new ArrayContentProvider());
        propsViewer.setLabelProvider(propertiesContentProvider);

        if (!hasModelResources) {
            useModelCheckBox.setEnabled(false);
            modelsCombo.setEnabled(false);
        }

        if (!hasConnectionProfiles) {
            useConnectionProfileCheckBox.setEnabled(false);
            connectionProfilesCombo.setEnabled(false);
        } else {
            this.connectionProfilesCombo.select(0);
        }

        if (!hasConnectionProfiles && !hasModelResources) {
            wizardPage.setErrorMessage(getString("noConnectionDataError.message")); //$NON-NLS-1$
        } else {
            setConnectionProperties();
        }

        // ===========>>>> If we're in edit mode, load the UI objects with the info from the input dataRole

        return mainPanel;
    }

    private void handleModelResourceSelection() {
        if (modelsCombo.getSelectionIndex() >= 0) {
            String selectedItem = modelsCombo.getItem(modelsCombo.getSelectionIndex());

            this.selectedModelResource = this.relationalModelsMap.get(selectedItem);
        } else {
            this.selectedModelResource = null;
        }
        setConnectionProperties();

    }

    private void handleConnectionProfileSelected() {
        if (connectionProfilesCombo.getSelectionIndex() >= 0) {
            String selectedItem = connectionProfilesCombo.getItem(connectionProfilesCombo.getSelectionIndex());
            this.selectedProfile = ProfileManager.getInstance().getProfileByName(selectedItem);
        } else {
            this.selectedProfile = null;
        }
        setConnectionProperties();
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     * @since 4.0
     */
    @Override
    public boolean canFinish() {
        return wizardPage.isPageComplete();
    }

    @Override
    public boolean finish() {
        return true;
    }

    public TeiidDataSourceInfo getTeiidDataSourceInfo() {
        TeiidDataSourceInfo info = new TeiidDataSourceInfo(dataSourceName, dataSourceName, teiidDataSourceProperties,
                                                           currentProvider, admin);
        return info;
    }

    private void validateInputs() {
        // Check that name != null
        // TODO: Add NAME EXISTS ERROR
        if (this.dataSourceName == null || this.dataSourceName.length() == 0) {
            wizardPage.setErrorMessage(getString("nullNameError.message")); //$NON-NLS-1$
            wizardPage.setPageComplete(false);
        } else if ( useModelCheckBox != null && useModelCheckBox.getSelection() && teiidDataSourceProperties.isEmpty()) {
            wizardPage.setErrorMessage(getString("noValidTeiidPropertiesInModelError.message")); //$NON-NLS-1$
            wizardPage.setPageComplete(false);
        } else if ( useConnectionProfileCheckBox != null && useConnectionProfileCheckBox.getSelection() && teiidDataSourceProperties.isEmpty()) {
            wizardPage.setErrorMessage(getString("noValidTeiidPropertiesError.message")); //$NON-NLS-1$
            wizardPage.setPageComplete(false);
        } else if (!isValidName(this.dataSourceName)) {
            wizardPage.setErrorMessage(getString("invalidName.message", INVALID_CHARS)); //$NON-NLS-1$
            wizardPage.setPageComplete(false);
        } else {
            wizardPage.setErrorMessage(null);
            wizardPage.setMessage(getString("finish.message")); //$NON-NLS-1$
            wizardPage.setPageComplete(true);
        }

    }

    private void setConnectionProperties() {
        Properties props = new Properties();
        currentProvider = null;

        if (useModelCheckBox.getSelection()) {
            if (selectedModelResource != null) {
                try {
                    IConnectionInfoProvider provider = getProvider(selectedModelResource);
                    if (provider != null) {
                        currentProvider = provider;
                        props = provider.getConnectionProperties(selectedModelResource);
                    }
                } catch (ModelWorkspaceException e) {
                    DqpUiConstants.UTIL.log(e);
                } catch (Exception e) {
                    //DqpUiConstants.UTIL.log(e);
                }
            }
        } else if (selectedProfile != null) {
            try {
                IConnectionInfoProvider provider = getProvider(selectedProfile);
                if (provider != null) {
                    currentProvider = provider;
                    props = provider.getTeiidRelatedProperties(selectedProfile);
                }
            } catch (ModelWorkspaceException e) {
                DqpUiConstants.UTIL.log(e);
            } catch (Exception e) {
                DqpUiConstants.UTIL.log(e);
            }
        }

        if (props != null && !props.isEmpty()) {
            teiidDataSourceProperties = props;
            Collection<StringKeyValuePair> propsColl = new ArrayList<StringKeyValuePair>();
            for (Object key : props.keySet()) {
                String keyStr = (String)key;
                String value = (String)props.getProperty((String)key);
                if (keyStr.equalsIgnoreCase(PASSWORD)) {
                    value = HIDDEN_PASSWORD;
                }
                propsColl.add(new StringKeyValuePair(keyStr, value));
            }
            propsViewer.setInput(propsColl);
        } else {
            teiidDataSourceProperties.clear();
            propsViewer.setInput(new ArrayList<StringKeyValuePair>());
        }

        validateInputs();
    }

    private IConnectionInfoProvider getProvider( Object obj ) throws Exception {
        IConnectionInfoProvider provider = null;
        if (obj instanceof ModelResource) {
            provider = providerFactory.getProvider((ModelResource)obj);
        } else if (obj instanceof IConnectionProfile) {
            provider = providerFactory.getProvider((IConnectionProfile)obj);
        }

        return provider;
    }

    private boolean isValidName( String name ) {
        if (dataSourceNameValidator == null) {
            dataSourceNameValidator = new StringNameValidator(StringNameValidator.DEFAULT_MINIMUM_LENGTH,
                                                              StringNameValidator.DEFAULT_MAXIMUM_LENGTH, new char[] {';', '@',
                                                                  '#', '$', '%', '^', '&', '*', '(', ')', '[', ']', '{', '}',
                                                                  '=', '|', '!', '<', '>', '?', '\''});
        }
        return dataSourceNameValidator.isValidName(name);
    }

    /**
     * @since 7.0
     */
    void launchSourceWizard() {
        NewCPWizard wiz = new NewCPWizard();
        WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), wiz);
        wizardDialog.setBlockOnOpen(true);

        CPListener listener = new CPListener();
        ProfileManager.getInstance().addProfileListener(listener);
        if (wizardDialog.open() == Window.OK) {
        	IConnectionProfile newProfile = listener.getLatestProfile();
        	
        	ArrayList sourceList = new ArrayList();
            for( IConnectionProfile prof : ProfileManager.getInstance().getProfiles()) {
            	sourceList.add(prof);
            }
            WidgetUtil.setComboItems(this.connectionProfilesCombo, sourceList, this.profileLabelProvider, true);

            selectConnectionProfile(newProfile.getName());
        }
        ProfileManager.getInstance().removeProfileListener(listener);
    }
    
    void editConnectionProfile() {
    	if( this.selectedProfile != null ) {
    		IConnectionProfile currentProfile = this.selectedProfile;
    		EditConnectionProfileAction action = new EditConnectionProfileAction(getShell(), currentProfile);
    		
    		CPListener listener = new CPListener();
            ProfileManager.getInstance().addProfileListener(listener);
            
    		action.run();
    		
        	ArrayList sourceList = new ArrayList();
            for( IConnectionProfile prof : ProfileManager.getInstance().getProfiles()) {
            	sourceList.add(prof);
            }
            
            WidgetUtil.setComboItems(connectionProfilesCombo, sourceList, profileLabelProvider, true);
    		
    		// Update the Combo Box
                
            selectConnectionProfile(currentProfile.getName());

    	}
    }
    
    void selectConnectionProfile(String name) {
    	if( name == null ) {
    		return;
    	}
    	
    	int cpIndex = -1;
    	int i = 0;
    	for( String item : connectionProfilesCombo.getItems()) {
    		if( item != null && item.length() > 0 ) {
    			if( item.toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
    				cpIndex = i;
    				break;
    			}
    		}
    		i++;
    	}
    	if( cpIndex > -1 ) {
    		connectionProfilesCombo.select(cpIndex);
    	}
    }
    
    class StringKeyValuePair {

        private String key;
        private String value;

        public StringKeyValuePair( String key,
                                   String value ) {
            this.key = key;
            this.value = value;
        }

        /**
         * @return key
         */
        public String getKey() {
            return key;
        }

        /**
         * @return value
         */
        public String getValue() {
            return value;
        }

    }

    class TableContentProvider implements ITableLabelProvider {

        public Image getColumnImage( Object theElement,
                                     int theIndex ) {
            return null;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText( Object theElement,
                                     int theColumnIndex ) {
            if (theElement instanceof StringKeyValuePair) {
                StringKeyValuePair prop = (StringKeyValuePair)theElement;
                if (theColumnIndex == 0) {
                    return prop.getKey();
                }

                return prop.getValue();
            }

            return StringUtilities.EMPTY_STRING;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        @Override
        public void addListener( ILabelProviderListener listener ) {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
         */
        @Override
        public boolean isLabelProperty( Object element,
                                        String property ) {
            return false;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        @Override
        public void removeListener( ILabelProviderListener listener ) {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
         */
        @Override
        public void dispose() {
        }

    }
    
    public class CPListener implements IProfileListener {

        IConnectionProfile latestProfile;

        @Override
        public void profileAdded( IConnectionProfile profile ) {
            latestProfile = profile;
        }

        @Override
        public void profileChanged( IConnectionProfile profile ) {
        	latestProfile = profile;
        }

        @Override
        public void profileDeleted( IConnectionProfile profile ) {
            // nothing
        }

        public IConnectionProfile getLatestProfile() {
            return latestProfile;
        }
    }

}
