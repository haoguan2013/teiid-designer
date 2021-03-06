/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xml.aspects.sql;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.metamodels.xml.XmlDocumentPlugin;


/**
 * XmlSequenceSqlAspect
 *
 * @since 8.0
 */
public class XmlAllSqlAspect extends XmlContainerNodeSqlAspect {

    /**
     * Construct an instance of XmlSequenceSqlAspect.
     * 
     */
    public XmlAllSqlAspect(final MetamodelEntity entity) {
        super(entity);
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getName(final EObject eObject) {
        return XmlDocumentPlugin.getPluginResourceLocator().getString("_UI_XmlAll_type"); //$NON-NLS-1$
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
