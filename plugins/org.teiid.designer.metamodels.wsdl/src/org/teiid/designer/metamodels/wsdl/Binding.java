/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.wsdl;

import org.eclipse.emf.common.util.EList;
import org.teiid.designer.metamodels.wsdl.http.HttpBinding;
import org.teiid.designer.metamodels.wsdl.soap.SoapBinding;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Binding</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Binding#getType <em>Type</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Binding#getDefinitions <em>Definitions</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Binding#getBindingOperations <em>Binding Operations</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Binding#getSoapBinding <em>Soap Binding</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Binding#getHttpBinding <em>Http Binding</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getBinding()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface Binding extends WsdlNameRequiredEntity, ExtensibleDocumented {

    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Type</em>' attribute.
     * @see #setType(String)
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getBinding_Type()
     * @model
     * @generated
     */
    String getType();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.wsdl.Binding#getType <em>Type</em>}' attribute. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see #getType()
     * @generated
     */
    void setType( String value );

    /**
     * Returns the value of the '<em><b>Definitions</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.Definitions#getBindings <em>Bindings</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Definitions</em>' container reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Definitions</em>' container reference.
     * @see #setDefinitions(Definitions)
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getBinding_Definitions()
     * @see org.teiid.designer.metamodels.wsdl.Definitions#getBindings
     * @model opposite="bindings" required="true"
     * @generated
     */
    Definitions getDefinitions();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.wsdl.Binding#getDefinitions <em>Definitions</em>}' container
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Definitions</em>' container reference.
     * @see #getDefinitions()
     * @generated
     */
    void setDefinitions( Definitions value );

    /**
     * Returns the value of the '<em><b>Binding Operations</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.wsdl.BindingOperation}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.BindingOperation#getBinding <em>Binding</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Binding Operations</em>' containment reference list isn't clear, there really should be more of
     * a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Binding Operations</em>' containment reference list.
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getBinding_BindingOperations()
     * @see org.teiid.designer.metamodels.wsdl.BindingOperation#getBinding
     * @model type="org.teiid.designer.metamodels.wsdl.BindingOperation" opposite="binding" containment="true"
     * @generated
     */
    EList getBindingOperations();

    /**
     * Returns the value of the '<em><b>Soap Binding</b></em>' containment reference. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.soap.SoapBinding#getBinding <em>Binding</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Soap Binding</em>' containment reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Soap Binding</em>' containment reference.
     * @see #setSoapBinding(SoapBinding)
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getBinding_SoapBinding()
     * @see org.teiid.designer.metamodels.wsdl.soap.SoapBinding#getBinding
     * @model opposite="binding" containment="true"
     * @generated
     */
    SoapBinding getSoapBinding();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.wsdl.Binding#getSoapBinding <em>Soap Binding</em>}' containment
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Soap Binding</em>' containment reference.
     * @see #getSoapBinding()
     * @generated
     */
    void setSoapBinding( SoapBinding value );

    /**
     * Returns the value of the '<em><b>Http Binding</b></em>' containment reference. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.http.HttpBinding#getBinding <em>Binding</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Http Binding</em>' containment reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Http Binding</em>' containment reference.
     * @see #setHttpBinding(HttpBinding)
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getBinding_HttpBinding()
     * @see org.teiid.designer.metamodels.wsdl.http.HttpBinding#getBinding
     * @model opposite="binding" containment="true"
     * @generated
     */
    HttpBinding getHttpBinding();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.wsdl.Binding#getHttpBinding <em>Http Binding</em>}' containment
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Http Binding</em>' containment reference.
     * @see #getHttpBinding()
     * @generated
     */
    void setHttpBinding( HttpBinding value );

} // Binding
