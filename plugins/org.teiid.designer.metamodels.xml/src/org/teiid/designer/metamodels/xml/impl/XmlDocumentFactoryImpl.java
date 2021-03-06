/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xml.impl;

import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.teiid.designer.metamodels.xml.BuildStatus;
import org.teiid.designer.metamodels.xml.ChoiceErrorMode;
import org.teiid.designer.metamodels.xml.NormalizationType;
import org.teiid.designer.metamodels.xml.ProcessingInstruction;
import org.teiid.designer.metamodels.xml.SoapEncoding;
import org.teiid.designer.metamodels.xml.ValueType;
import org.teiid.designer.metamodels.xml.XmlAll;
import org.teiid.designer.metamodels.xml.XmlAttribute;
import org.teiid.designer.metamodels.xml.XmlChoice;
import org.teiid.designer.metamodels.xml.XmlComment;
import org.teiid.designer.metamodels.xml.XmlDocument;
import org.teiid.designer.metamodels.xml.XmlDocumentFactory;
import org.teiid.designer.metamodels.xml.XmlDocumentPackage;
import org.teiid.designer.metamodels.xml.XmlElement;
import org.teiid.designer.metamodels.xml.XmlFragment;
import org.teiid.designer.metamodels.xml.XmlFragmentUse;
import org.teiid.designer.metamodels.xml.XmlNamespace;
import org.teiid.designer.metamodels.xml.XmlRoot;
import org.teiid.designer.metamodels.xml.XmlSequence;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 *
 * @since 8.0
 */
public class XmlDocumentFactoryImpl extends EFactoryImpl implements XmlDocumentFactory {
    /**
     * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlDocumentFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EObject create( EClass eClass ) {
        switch (eClass.getClassifierID()) {
            case XmlDocumentPackage.XML_FRAGMENT:
                return createXmlFragment();
            case XmlDocumentPackage.XML_DOCUMENT:
                return createXmlDocument();
            case XmlDocumentPackage.XML_ELEMENT:
                return createXmlElement();
            case XmlDocumentPackage.XML_ATTRIBUTE:
                return createXmlAttribute();
            case XmlDocumentPackage.XML_ROOT:
                return createXmlRoot();
            case XmlDocumentPackage.XML_COMMENT:
                return createXmlComment();
            case XmlDocumentPackage.XML_NAMESPACE:
                return createXmlNamespace();
            case XmlDocumentPackage.XML_SEQUENCE:
                return createXmlSequence();
            case XmlDocumentPackage.XML_ALL:
                return createXmlAll();
            case XmlDocumentPackage.XML_CHOICE:
                return createXmlChoice();
            case XmlDocumentPackage.PROCESSING_INSTRUCTION:
                return createProcessingInstruction();
            case XmlDocumentPackage.XML_FRAGMENT_USE:
                return createXmlFragmentUse();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object createFromString( EDataType eDataType,
                                    String initialValue ) {
        switch (eDataType.getClassifierID()) {
            case XmlDocumentPackage.SOAP_ENCODING: {
                SoapEncoding result = SoapEncoding.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case XmlDocumentPackage.CHOICE_ERROR_MODE: {
                ChoiceErrorMode result = ChoiceErrorMode.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case XmlDocumentPackage.VALUE_TYPE: {
                ValueType result = ValueType.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case XmlDocumentPackage.BUILD_STATUS: {
                BuildStatus result = BuildStatus.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case XmlDocumentPackage.NORMALIZATION_TYPE: {
                NormalizationType result = NormalizationType.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case XmlDocumentPackage.LIST:
                return createListFromString(eDataType, initialValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String convertToString( EDataType eDataType,
                                   Object instanceValue ) {
        switch (eDataType.getClassifierID()) {
            case XmlDocumentPackage.SOAP_ENCODING:
                return instanceValue == null ? null : instanceValue.toString();
            case XmlDocumentPackage.CHOICE_ERROR_MODE:
                return instanceValue == null ? null : instanceValue.toString();
            case XmlDocumentPackage.VALUE_TYPE:
                return instanceValue == null ? null : instanceValue.toString();
            case XmlDocumentPackage.BUILD_STATUS:
                return instanceValue == null ? null : instanceValue.toString();
            case XmlDocumentPackage.NORMALIZATION_TYPE:
                return instanceValue == null ? null : instanceValue.toString();
            case XmlDocumentPackage.LIST:
                return convertListToString(eDataType, instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XmlFragment createXmlFragment() {
        XmlFragmentImpl xmlFragment = new XmlFragmentImpl();
        return xmlFragment;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XmlDocument createXmlDocument() {
        XmlDocumentImpl xmlDocument = new XmlDocumentImpl();
        return xmlDocument;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XmlElement createXmlElement() {
        XmlElementImpl xmlElement = new XmlElementImpl();
        return xmlElement;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XmlAttribute createXmlAttribute() {
        XmlAttributeImpl xmlAttribute = new XmlAttributeImpl();
        return xmlAttribute;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XmlRoot createXmlRoot() {
        XmlRootImpl xmlRoot = new XmlRootImpl();
        return xmlRoot;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XmlComment createXmlComment() {
        XmlCommentImpl xmlComment = new XmlCommentImpl();
        return xmlComment;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XmlNamespace createXmlNamespace() {
        XmlNamespaceImpl xmlNamespace = new XmlNamespaceImpl();
        return xmlNamespace;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XmlSequence createXmlSequence() {
        XmlSequenceImpl xmlSequence = new XmlSequenceImpl();
        return xmlSequence;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XmlAll createXmlAll() {
        XmlAllImpl xmlAll = new XmlAllImpl();
        return xmlAll;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XmlChoice createXmlChoice() {
        XmlChoiceImpl xmlChoice = new XmlChoiceImpl();
        return xmlChoice;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public ProcessingInstruction createProcessingInstruction() {
        ProcessingInstructionImpl processingInstruction = new ProcessingInstructionImpl();
        return processingInstruction;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XmlFragmentUse createXmlFragmentUse() {
        XmlFragmentUseImpl xmlFragmentUse = new XmlFragmentUseImpl();
        return xmlFragmentUse;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public List createListFromString( EDataType eDataType,
                                      String initialValue ) {
        return (List)super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertListToString( EDataType eDataType,
                                       Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XmlDocumentPackage getXmlDocumentPackage() {
        return (XmlDocumentPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @deprecated
     * @generated
     */
    @Deprecated
    public static XmlDocumentPackage getPackage() { // NO_UCD
        return XmlDocumentPackage.eINSTANCE;
    }

} // XmlDocumentFactoryImpl
