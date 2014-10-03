/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.login.cxf;

import org.geoserver.geofence.api.AuthProvider;
import org.geoserver.geofence.api.dto.Authority;
import org.geoserver.geofence.api.dto.GrantedAuths;
import org.geoserver.geofence.login.util.GrantAll;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Map;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.interceptor.AbstractInDatabindingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 
 * Adds basic authentication to CXF services by using login operation.
 * 
 * @author ETj (etj at geo-solutions.it)
 * 
 * @see http://chrisdail.com/2008/03/31/apache-cxf-with-http-basic-authentication/
 */
public class BasicAuthInterceptor extends AbstractInDatabindingInterceptor {

    protected Logger LOGGER = LogManager.getLogger(getClass());

    private AuthProvider authProvider = new GrantAll(); // reassign this, maybe by injection

    private String realm = "Geofence";

    public BasicAuthInterceptor() {
        super(Phase.UNMARSHAL);
        // addAfter(BareInInterceptor.class.getName());
        // addAfter(RPCInInterceptor.class.getName());
        // addAfter(DocLiteralInInterceptor.class.getName());
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        // This is set by CXF
        AuthorizationPolicy policy = message.get(AuthorizationPolicy.class);

        // If the policy is not set, the user did not specify
        // credentials. A 401 is sent to the client to indicate
        // that authentication is required
        if (policy == null) {
            sendErrorResponse(message, HttpURLConnection.HTTP_UNAUTHORIZED);
            return;
        }

        // // allow auth to anybody
        // GrantedAuths ga = new GrantedAuths();
        // ga.setAuthorities(Arrays.asList(Authority.LOGIN, Authority.REMOTE));
        // message.put("grantedAuths", ga);
        // return;

        GrantedAuths ga = null;

        try {
            ga = authProvider.login(policy.getUserName(), policy.getPassword(), "");
        } catch (Exception ex) {
            LOGGER.warn("Login failed:" + ex.getMessage());
            sendErrorResponse(message, HttpURLConnection.HTTP_FORBIDDEN);
            return;
        }

        if (!ga.getAuthorities().contains(Authority.REMOTE)) {
            sendErrorResponse(message, HttpURLConnection.HTTP_FORBIDDEN);
            return;
        }

        message.put("grantedAuths", ga);

        // sendErrorResponse(message, HttpURLConnection.HTTP_FORBIDDEN);
        // connectid is not set, the called method should issue an Auth error
    }

    private void sendErrorResponse(Message message, int responseCode) {

        Message outMessage = getOutMessage(message);
        outMessage.put(Message.RESPONSE_CODE, responseCode);
        // Set the response headers
        Map responseHeaders = (Map) message.get(Message.PROTOCOL_HEADERS);

        if (responseHeaders != null) {
            responseHeaders.put("WWW-Authenticate", Arrays.asList("Basic realm=\"" + realm + "\""));
            responseHeaders.put("Content-Length", Arrays.asList("0"));
        }

        message.getInterceptorChain().abort();
        try {
            getConduit(message).prepare(outMessage);

            OutputStream os = outMessage.getContent(OutputStream.class);
            String errmsg = "Error " + responseCode + ": ";
            os.write(errmsg.getBytes());
            LOGGER.info("Sending error " + responseCode);

            close(outMessage);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    private Message getOutMessage(Message inMessage) {
        Exchange exchange = inMessage.getExchange();
        Message outMessage = exchange.getOutMessage();
        if (outMessage == null) {
            Endpoint endpoint = exchange.get(Endpoint.class);
            outMessage = endpoint.getBinding().createMessage();
            exchange.setOutMessage(outMessage);
        }
        outMessage.putAll(inMessage);
        return outMessage;
    }

    private Conduit getConduit(Message inMessage) throws IOException {
        Exchange exchange = inMessage.getExchange();
        EndpointReferenceType target = exchange.get(EndpointReferenceType.class);
        Conduit conduit = exchange.getDestination().getBackChannel(inMessage, null, target);
        exchange.setConduit(conduit);
        return conduit;
    }

    private void close(Message outMessage) throws IOException {
        OutputStream os = outMessage.getContent(OutputStream.class);
        os.flush();
        os.close();
    }

    // ==========================================================================

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

}