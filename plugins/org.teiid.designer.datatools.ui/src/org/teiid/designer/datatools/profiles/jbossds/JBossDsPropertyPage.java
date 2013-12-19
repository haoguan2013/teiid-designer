package org.teiid.designer.datatools.profiles.jbossds;

import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ui.wizards.ProfileDetailsPropertyPage;
import org.eclipse.datatools.help.ContextProviderDelegate;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;
import org.teiid.designer.ui.common.util.WidgetFactory;

/**
 * JBossDsPropertyPage
 */
public class JBossDsPropertyPage extends ProfileDetailsPropertyPage implements IContextProvider, DatatoolsUiConstants {

    private ContextProviderDelegate contextProviderDelegate = new ContextProviderDelegate(DatatoolsUiPlugin.getDefault().getBundle().getSymbolicName());
    
    private Composite scrolled;
    private Label jndiLabel;
    private Text jndiText;
    private Label translatorLabel;
    private Text translatorText;

    /**
     * JBossDSPropertyPage constructor
     */
    public JBossDsPropertyPage() {
        super();
    }

    @Override
    public IContext getContext( Object target ) {
        return contextProviderDelegate.getContext(target);
    }

    @Override
    public int getContextChangeMask() {
        return contextProviderDelegate.getContextChangeMask();
    }

    @Override
    public String getSearchExpression( Object target ) {
        return contextProviderDelegate.getSearchExpression(target);
    }

    
    @Override
	protected Control createContents(Composite parent) {
		Control result = super.createContents(parent);
        this.setPingButtonEnabled(false);
        this.setPingButtonVisible(false);
        return result;
	}

	@Override
    protected void createCustomContents( Composite parent ) {
        GridData gd;

        Group group = WidgetFactory.createSimpleGroup(parent, UTIL.getString("Common.Properties.Label")); //$NON-NLS-1$;

        scrolled = new Composite(group, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        scrolled.setLayout(gridLayout);

        jndiLabel = new Label(scrolled, SWT.NONE);
        jndiLabel.setText(UTIL.getString("JBossDsPropertyPage.jndi.Label")); //$NON-NLS-1$
        jndiLabel.setToolTipText(UTIL.getString("JBossDsPropertyPage.jndi.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        jndiLabel.setLayoutData(gd);

        jndiText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        jndiText.setToolTipText(UTIL.getString("JBossDsPropertyPage.jndi.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        jndiText.setLayoutData(gd);

        translatorLabel = new Label(scrolled, SWT.NONE);
        translatorLabel.setText(UTIL.getString("JBossDsPropertyPage.translator.Label")); //$NON-NLS-1$
        translatorLabel.setToolTipText(UTIL.getString("JBossDsPropertyPage.translator.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        translatorLabel.setLayoutData(gd);

        translatorText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        translatorText.setToolTipText(UTIL.getString("JBossDsPropertyPage.translator.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        translatorText.setLayoutData(gd);

        initControls();
        addlisteners();
    }

    /**
     * 
     */
    private void addlisteners() {
        jndiText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText( ModifyEvent e ) {
                validate();
            }
        });

        translatorText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText( ModifyEvent e ) {
                validate();
            }
        });

    }

    protected void validate() {
        String errorMessage = null;
        boolean valid = true;
        if (null == jndiText.getText() || jndiText.getText().isEmpty()) {
            errorMessage = UTIL.getString("JBossDsPropertyPage.jndi.Error.Message"); //$NON-NLS-1$
            valid = false;
        }
        if (null == translatorText.getText() || translatorText.getText().isEmpty()) {
            errorMessage = UTIL.getString("JBossDsPropertyPage.translator.Error.Message"); //$NON-NLS-1$
            valid = false;
        }
        setErrorMessage(errorMessage);
        setValid(valid);

    }

    /**
     * 
     */
    private void initControls() {
        IConnectionProfile profile = getConnectionProfile();
        Properties props = profile.getBaseProperties();
        if (null != props.get(IJBossDsProfileConstants.JNDI_PROP_ID)) {
            jndiText.setText((String)props.get(IJBossDsProfileConstants.JNDI_PROP_ID));
        }
        if (null != props.get(IJBossDsProfileConstants.TRANSLATOR_PROP_ID)) {
            translatorText.setText((String)props.get(IJBossDsProfileConstants.TRANSLATOR_PROP_ID));
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.connectivity.ui.wizards.ProfileDetailsPropertyPage#collectProperties()
     */
    @Override
    protected Properties collectProperties() {
        Properties result = super.collectProperties();
        if (null == result) {
            result = new Properties();
        }
        result.setProperty(IJBossDsProfileConstants.JNDI_PROP_ID, jndiText.getText());
        result.setProperty(IJBossDsProfileConstants.TRANSLATOR_PROP_ID, translatorText.getText());
        return result;
    }

}
