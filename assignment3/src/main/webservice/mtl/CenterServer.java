
package main.webservice.mtl;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "CenterServer", targetNamespace = "http://server.main/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface CenterServer {


    /**
     * 
     * @param arg3
     * @param arg2
     * @param arg5
     * @param arg4
     * @param arg1
     * @param arg0
     * @param arg6
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "createTRecord", targetNamespace = "http://server.main/", className = "main.webservice.mtl.CreateTRecord")
    @ResponseWrapper(localName = "createTRecordResponse", targetNamespace = "http://server.main/", className = "main.webservice.mtl.CreateTRecordResponse")
    @Action(input = "http://server.main/CenterServer/createTRecordRequest", output = "http://server.main/CenterServer/createTRecordResponse")
    public String createTRecord(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        String arg2,
        @WebParam(name = "arg3", targetNamespace = "")
        String arg3,
        @WebParam(name = "arg4", targetNamespace = "")
        String arg4,
        @WebParam(name = "arg5", targetNamespace = "")
        String arg5,
        @WebParam(name = "arg6", targetNamespace = "")
        String arg6);

    /**
     * 
     * @param arg3
     * @param arg2
     * @param arg5
     * @param arg4
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "createSRecord", targetNamespace = "http://server.main/", className = "main.webservice.mtl.CreateSRecord")
    @ResponseWrapper(localName = "createSRecordResponse", targetNamespace = "http://server.main/", className = "main.webservice.mtl.CreateSRecordResponse")
    @Action(input = "http://server.main/CenterServer/createSRecordRequest", output = "http://server.main/CenterServer/createSRecordResponse")
    public String createSRecord(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        String arg2,
        @WebParam(name = "arg3", targetNamespace = "")
        String arg3,
        @WebParam(name = "arg4", targetNamespace = "")
        String arg4,
        @WebParam(name = "arg5", targetNamespace = "")
        String arg5);

    /**
     * 
     * @param arg2
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "transferRecord", targetNamespace = "http://server.main/", className = "main.webservice.mtl.TransferRecord")
    @ResponseWrapper(localName = "transferRecordResponse", targetNamespace = "http://server.main/", className = "main.webservice.mtl.TransferRecordResponse")
    @Action(input = "http://server.main/CenterServer/transferRecordRequest", output = "http://server.main/CenterServer/transferRecordResponse")
    public String transferRecord(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        String arg2);

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getRecordCounts", targetNamespace = "http://server.main/", className = "main.webservice.mtl.GetRecordCounts")
    @ResponseWrapper(localName = "getRecordCountsResponse", targetNamespace = "http://server.main/", className = "main.webservice.mtl.GetRecordCountsResponse")
    @Action(input = "http://server.main/CenterServer/getRecordCountsRequest", output = "http://server.main/CenterServer/getRecordCountsResponse")
    public String getRecordCounts(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @param arg3
     * @param arg2
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "editRecord", targetNamespace = "http://server.main/", className = "main.webservice.mtl.EditRecord")
    @ResponseWrapper(localName = "editRecordResponse", targetNamespace = "http://server.main/", className = "main.webservice.mtl.EditRecordResponse")
    @Action(input = "http://server.main/CenterServer/editRecordRequest", output = "http://server.main/CenterServer/editRecordResponse")
    public String editRecord(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        String arg2,
        @WebParam(name = "arg3", targetNamespace = "")
        String arg3);

}