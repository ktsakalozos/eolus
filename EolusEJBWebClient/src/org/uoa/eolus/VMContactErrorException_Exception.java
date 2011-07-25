
package org.uoa.eolus;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.3-b02-
 * Generated source version: 2.0
 * 
 */
@WebFault(name = "VMContactErrorException", targetNamespace = "http://eolus.uoa.org/")
public class VMContactErrorException_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private VMContactErrorException faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public VMContactErrorException_Exception(String message, VMContactErrorException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public VMContactErrorException_Exception(String message, VMContactErrorException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: org.uoa.eolus.VMContactErrorException
     */
    public VMContactErrorException getFaultInfo() {
        return faultInfo;
    }

}