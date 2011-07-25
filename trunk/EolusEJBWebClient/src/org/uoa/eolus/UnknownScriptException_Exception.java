
package org.uoa.eolus;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.3-b02-
 * Generated source version: 2.0
 * 
 */
@WebFault(name = "UnknownScriptException", targetNamespace = "http://eolus.uoa.org/")
public class UnknownScriptException_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private UnknownScriptException faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public UnknownScriptException_Exception(String message, UnknownScriptException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public UnknownScriptException_Exception(String message, UnknownScriptException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: org.uoa.eolus.UnknownScriptException
     */
    public UnknownScriptException getFaultInfo() {
        return faultInfo;
    }

}