
package main.webservice.lvl;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "ServerImplService", targetNamespace = "http://com.zexin", wsdlLocation = "http://localhost:8888/LVLServer?wsdl")
public class ServerImplService
    extends Service
{

    private final static URL SERVERIMPLSERVICE_WSDL_LOCATION;
    private final static WebServiceException SERVERIMPLSERVICE_EXCEPTION;
    private final static QName SERVERIMPLSERVICE_QNAME = new QName("http://com.zexin", "ServerImplService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8888/LVLServer?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        SERVERIMPLSERVICE_WSDL_LOCATION = url;
        SERVERIMPLSERVICE_EXCEPTION = e;
    }

    public ServerImplService() {
        super(__getWsdlLocation(), SERVERIMPLSERVICE_QNAME);
    }

    public ServerImplService(WebServiceFeature... features) {
        super(__getWsdlLocation(), SERVERIMPLSERVICE_QNAME, features);
    }

    public ServerImplService(URL wsdlLocation) {
        super(wsdlLocation, SERVERIMPLSERVICE_QNAME);
    }

    public ServerImplService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, SERVERIMPLSERVICE_QNAME, features);
    }

    public ServerImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ServerImplService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns CenterServer
     */
    @WebEndpoint(name = "ServerImplPort")
    public CenterServer getServerImplPort() {
        return super.getPort(new QName("http://com.zexin", "ServerImplPort"), CenterServer.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns CenterServer
     */
    @WebEndpoint(name = "ServerImplPort")
    public CenterServer getServerImplPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://com.zexin", "ServerImplPort"), CenterServer.class, features);
    }

    private static URL __getWsdlLocation() {
        if (SERVERIMPLSERVICE_EXCEPTION!= null) {
            throw SERVERIMPLSERVICE_EXCEPTION;
        }
        return SERVERIMPLSERVICE_WSDL_LOCATION;
    }

}
