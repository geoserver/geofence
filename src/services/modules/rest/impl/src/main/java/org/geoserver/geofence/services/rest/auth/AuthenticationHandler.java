/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.auth;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;
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
 * Adds basic authentication to CXF services by using login operation.
 *
 * @author Tobia di Pisa (tobia.dipisa at geo-solutions.it)
 *
 * @see http://chrisdail.com/2008/08/13/http-basic-authentication-with-apache-cxf-revisited/
 *
 */
public class AuthenticationHandler extends AbstractInDatabindingInterceptor
{

    private static final Logger LOGGER = LogManager.getLogger(AuthenticationHandler.class);

    private String realm;

    public AuthenticationHandler()
    {
        super(Phase.UNMARSHAL);
    }

    /**
     * @param realm the realm to set
     */
    public void setRealm(String realm)
    {
        this.realm = realm;
    }

    /**
     * @param username
     * @param password
     * @return boolean
     */
    private boolean isAuthenticated(String username, String password)
    {
        LOGGER.warn("FORCING AUTH");

        return true;
    }

    /* (non-Javadoc)
     * @see org.apache.cxf.interceptor.Interceptor#handleMessage(org.apache.cxf.message.Message)
     */
    @Override
    public void handleMessage(Message message) throws Fault
    {
        AuthorizationPolicy policy = (AuthorizationPolicy) message.get(AuthorizationPolicy.class);

        //
        // TODO: To manage the public access (guest).
        //
        if (policy == null)
        {
            sendErrorResponse(message, HttpURLConnection.HTTP_UNAUTHORIZED);

            return;
        }

        String username = policy.getUserName();
        String password = policy.getPassword();

        if (isAuthenticated(username, password))
        {
            // ////////////////////////////////////////
            // let request to continue
            // ////////////////////////////////////////
            return;
        }
        else
        {
            // /////////////////////////////////////////////////////////////////////
            // authentication failed, request the authetication,
            // add the realm name if needed to the value of WWW-Authenticate
            // /////////////////////////////////////////////////////////////////////
            sendErrorResponse(message, HttpURLConnection.HTTP_UNAUTHORIZED);

            return;
        }
    }

    /**
     * @param message
     * @param responseCode
     */
    @SuppressWarnings("unchecked")
    private void sendErrorResponse(Message message, int responseCode)
    {

        Message outMessage = getOutMessage(message);
        outMessage.put(Message.RESPONSE_CODE, responseCode);

        // ////////////////////////////////////////
        // Set the response headers
        // ////////////////////////////////////////
        Map<String, List<String>> responseHeaders = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);

        if (responseHeaders != null)
        {
            responseHeaders.put("WWW-Authenticate", Arrays.asList("Basic realm=\"" + realm + "\""));
            responseHeaders.put("Content-Length", Arrays.asList("0"));
        }

        message.getInterceptorChain().abort();
        try
        {
            getConduit(message).prepare(outMessage);

            OutputStream os = outMessage.getContent(OutputStream.class);
            String errmsg = "Error " + responseCode + ": ";
            os.write(errmsg.getBytes());
            LOGGER.info("Sending error " + responseCode);

            close(outMessage);
        }
        catch (IOException e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * @param inMessage
     * @return Message
     */
    private Message getOutMessage(Message inMessage)
    {
        Exchange exchange = inMessage.getExchange();
        Message outMessage = exchange.getOutMessage();
        if (outMessage == null)
        {
            Endpoint endpoint = exchange.get(Endpoint.class);
            outMessage = endpoint.getBinding().createMessage();
            exchange.setOutMessage(outMessage);
        }
        outMessage.putAll(inMessage);

        return outMessage;
    }

    /**
     * @param inMessage
     * @return Conduit
     * @throws IOException
     */
    private Conduit getConduit(Message inMessage) throws IOException
    {
        Exchange exchange = inMessage.getExchange();
        EndpointReferenceType target = exchange.get(EndpointReferenceType.class);
        Conduit conduit = exchange.getDestination().getBackChannel(inMessage, null, target);
        exchange.setConduit(conduit);

        return conduit;
    }

    /**
     * @param outMessage
     * @throws IOException
     */
    private void close(Message outMessage) throws IOException
    {
        OutputStream os = outMessage.getContent(OutputStream.class);
        os.flush();
        os.close();
    }

}
