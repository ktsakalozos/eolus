
package org.uoa.eolus;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.3-b02-
 * Generated source version: 2.0
 * 
 */
@WebFault(name = "VNExistsException", targetNamespace = "http://eolus.uoa.org/")
public class VNExistsException_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private VNExistsException faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public VNExistsException_Exception(String message, VNExistsException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public VNExistsException_Exception(String message, VNExistsException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: org.uoa.eolus.VNExistsException
     */
    public VNExistsException getFaultInfo() {
        return faultInfo;
    }

}
