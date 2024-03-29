
package org.uoa.eolus;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.3-b02-
 * Generated source version: 2.0
 * 
 */
@WebFault(name = "UnknownVNException", targetNamespace = "http://eolus.uoa.org/")
public class UnknownVNException_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private UnknownVNException faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public UnknownVNException_Exception(String message, UnknownVNException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public UnknownVNException_Exception(String message, UnknownVNException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: org.uoa.eolus.UnknownVNException
     */
    public UnknownVNException getFaultInfo() {
        return faultInfo;
    }

}
