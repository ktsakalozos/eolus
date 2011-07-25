
package org.uoa.nefeli;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.3-b02-
 * Generated source version: 2.0
 * 
 */
@WebService(name = "Nefeli", targetNamespace = "http://nefeli.uoa.org/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface Nefeli {


    /**
     * 
     * @param arg0
     * @return
     *     returns long
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(partName = "return")
    public long addWorkload(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0)
        throws Exception_Exception
    ;

    /**
     * 
     * @param arg2
     * @param arg1
     * @param arg0
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(partName = "return")
    public boolean magicWord(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0,
        @WebParam(name = "arg1", partName = "arg1")
        int arg1,
        @WebParam(name = "arg2", partName = "arg2")
        String arg2);

    /**
     * 
     * @param arg0
     * @return
     *     returns boolean
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(partName = "return")
    public boolean removeWorkload(
        @WebParam(name = "arg0", partName = "arg0")
        long arg0)
        throws Exception_Exception
    ;

}