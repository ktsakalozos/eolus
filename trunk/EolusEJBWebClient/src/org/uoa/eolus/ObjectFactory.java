
package org.uoa.eolus;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.uoa.eolus package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _VMExistsException_QNAME = new QName("http://eolus.uoa.org/", "VMExistsException");
    private final static QName _UnknownVNException_QNAME = new QName("http://eolus.uoa.org/", "UnknownVNException");
    private final static QName _ReservedUserException_QNAME = new QName("http://eolus.uoa.org/", "ReservedUserException");
    private final static QName _IOException_QNAME = new QName("http://eolus.uoa.org/", "IOException");
    private final static QName _UnknownUserException_QNAME = new QName("http://eolus.uoa.org/", "UnknownUserException");
    private final static QName _VMContactErrorException_QNAME = new QName("http://eolus.uoa.org/", "VMContactErrorException");
    private final static QName _InternalErrorException_QNAME = new QName("http://eolus.uoa.org/", "InternalErrorException");
    private final static QName _UnknownParameter_QNAME = new QName("http://eolus.uoa.org/", "UnknownParameter");
    private final static QName _UnknownVMException_QNAME = new QName("http://eolus.uoa.org/", "UnknownVMException");
    private final static QName _TemplateNotReadyException_QNAME = new QName("http://eolus.uoa.org/", "TemplateNotReadyException");
    private final static QName _UnknownScriptException_QNAME = new QName("http://eolus.uoa.org/", "UnknownScriptException");
    private final static QName _DirectoryException_QNAME = new QName("http://eolus.uoa.org/", "DirectoryException");
    private final static QName _UnknownTemplateException_QNAME = new QName("http://eolus.uoa.org/", "UnknownTemplateException");
    private final static QName _VNExistsException_QNAME = new QName("http://eolus.uoa.org/", "VNExistsException");
    private final static QName _MultipleTemplatesException_QNAME = new QName("http://eolus.uoa.org/", "MultipleTemplatesException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.uoa.eolus
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UnknownVNException }
     * 
     */
    public UnknownVNException createUnknownVNException() {
        return new UnknownVNException();
    }

    /**
     * Create an instance of {@link VMExistsException }
     * 
     */
    public VMExistsException createVMExistsException() {
        return new VMExistsException();
    }

    /**
     * Create an instance of {@link TemplateNotReadyException }
     * 
     */
    public TemplateNotReadyException createTemplateNotReadyException() {
        return new TemplateNotReadyException();
    }

    /**
     * Create an instance of {@link VMContactErrorException }
     * 
     */
    public VMContactErrorException createVMContactErrorException() {
        return new VMContactErrorException();
    }

    /**
     * Create an instance of {@link DirectoryException }
     * 
     */
    public DirectoryException createDirectoryException() {
        return new DirectoryException();
    }

    /**
     * Create an instance of {@link InternalErrorException }
     * 
     */
    public InternalErrorException createInternalErrorException() {
        return new InternalErrorException();
    }

    /**
     * Create an instance of {@link UnknownUserException }
     * 
     */
    public UnknownUserException createUnknownUserException() {
        return new UnknownUserException();
    }

    /**
     * Create an instance of {@link ReservedUserException }
     * 
     */
    public ReservedUserException createReservedUserException() {
        return new ReservedUserException();
    }

    /**
     * Create an instance of {@link UnknownScriptException }
     * 
     */
    public UnknownScriptException createUnknownScriptException() {
        return new UnknownScriptException();
    }

    /**
     * Create an instance of {@link VNExistsException }
     * 
     */
    public VNExistsException createVNExistsException() {
        return new VNExistsException();
    }

    /**
     * Create an instance of {@link IOException }
     * 
     */
    public IOException createIOException() {
        return new IOException();
    }

    /**
     * Create an instance of {@link UnknownParameter }
     * 
     */
    public UnknownParameter createUnknownParameter() {
        return new UnknownParameter();
    }

    /**
     * Create an instance of {@link UnknownVMException }
     * 
     */
    public UnknownVMException createUnknownVMException() {
        return new UnknownVMException();
    }

    /**
     * Create an instance of {@link MultipleTemplatesException }
     * 
     */
    public MultipleTemplatesException createMultipleTemplatesException() {
        return new MultipleTemplatesException();
    }

    /**
     * Create an instance of {@link UnknownTemplateException }
     * 
     */
    public UnknownTemplateException createUnknownTemplateException() {
        return new UnknownTemplateException();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VMExistsException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://eolus.uoa.org/", name = "VMExistsException")
    public JAXBElement<VMExistsException> createVMExistsException(VMExistsException value) {
        return new JAXBElement<VMExistsException>(_VMExistsException_QNAME, VMExistsException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnknownVNException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://eolus.uoa.org/", name = "UnknownVNException")
    public JAXBElement<UnknownVNException> createUnknownVNException(UnknownVNException value) {
        return new JAXBElement<UnknownVNException>(_UnknownVNException_QNAME, UnknownVNException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReservedUserException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://eolus.uoa.org/", name = "ReservedUserException")
    public JAXBElement<ReservedUserException> createReservedUserException(ReservedUserException value) {
        return new JAXBElement<ReservedUserException>(_ReservedUserException_QNAME, ReservedUserException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IOException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://eolus.uoa.org/", name = "IOException")
    public JAXBElement<IOException> createIOException(IOException value) {
        return new JAXBElement<IOException>(_IOException_QNAME, IOException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnknownUserException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://eolus.uoa.org/", name = "UnknownUserException")
    public JAXBElement<UnknownUserException> createUnknownUserException(UnknownUserException value) {
        return new JAXBElement<UnknownUserException>(_UnknownUserException_QNAME, UnknownUserException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VMContactErrorException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://eolus.uoa.org/", name = "VMContactErrorException")
    public JAXBElement<VMContactErrorException> createVMContactErrorException(VMContactErrorException value) {
        return new JAXBElement<VMContactErrorException>(_VMContactErrorException_QNAME, VMContactErrorException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InternalErrorException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://eolus.uoa.org/", name = "InternalErrorException")
    public JAXBElement<InternalErrorException> createInternalErrorException(InternalErrorException value) {
        return new JAXBElement<InternalErrorException>(_InternalErrorException_QNAME, InternalErrorException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnknownParameter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://eolus.uoa.org/", name = "UnknownParameter")
    public JAXBElement<UnknownParameter> createUnknownParameter(UnknownParameter value) {
        return new JAXBElement<UnknownParameter>(_UnknownParameter_QNAME, UnknownParameter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnknownVMException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://eolus.uoa.org/", name = "UnknownVMException")
    public JAXBElement<UnknownVMException> createUnknownVMException(UnknownVMException value) {
        return new JAXBElement<UnknownVMException>(_UnknownVMException_QNAME, UnknownVMException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TemplateNotReadyException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://eolus.uoa.org/", name = "TemplateNotReadyException")
    public JAXBElement<TemplateNotReadyException> createTemplateNotReadyException(TemplateNotReadyException value) {
        return new JAXBElement<TemplateNotReadyException>(_TemplateNotReadyException_QNAME, TemplateNotReadyException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnknownScriptException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://eolus.uoa.org/", name = "UnknownScriptException")
    public JAXBElement<UnknownScriptException> createUnknownScriptException(UnknownScriptException value) {
        return new JAXBElement<UnknownScriptException>(_UnknownScriptException_QNAME, UnknownScriptException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DirectoryException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://eolus.uoa.org/", name = "DirectoryException")
    public JAXBElement<DirectoryException> createDirectoryException(DirectoryException value) {
        return new JAXBElement<DirectoryException>(_DirectoryException_QNAME, DirectoryException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnknownTemplateException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://eolus.uoa.org/", name = "UnknownTemplateException")
    public JAXBElement<UnknownTemplateException> createUnknownTemplateException(UnknownTemplateException value) {
        return new JAXBElement<UnknownTemplateException>(_UnknownTemplateException_QNAME, UnknownTemplateException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VNExistsException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://eolus.uoa.org/", name = "VNExistsException")
    public JAXBElement<VNExistsException> createVNExistsException(VNExistsException value) {
        return new JAXBElement<VNExistsException>(_VNExistsException_QNAME, VNExistsException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultipleTemplatesException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://eolus.uoa.org/", name = "MultipleTemplatesException")
    public JAXBElement<MultipleTemplatesException> createMultipleTemplatesException(MultipleTemplatesException value) {
        return new JAXBElement<MultipleTemplatesException>(_MultipleTemplatesException_QNAME, MultipleTemplatesException.class, null, value);
    }

}
