/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


// TODO: Auto-generated Javadoc
/**
 * The Class ApplicationException.
 */
public class ApplicationException extends RuntimeException implements Serializable, IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5240255747375099784L;

    /** The message. */
    private String message;

    /**
     * Instantiates a new application exception.
     */
    public ApplicationException()
    {
    }

    /**
     * Instantiates a new application exception.
     *
     * @param message
     *            the message
     */
    public ApplicationException(String message)
    {
        this.message = message;
    }

    /**
     * Instantiates a new application exception.
     *
     * @param e
     *            the e
     */
    public ApplicationException(Throwable e)
    {
        super(e);
    }

    /**
     * Instantiates a new application exception.
     *
     * @param message
     *            the message
     * @param e
     *            the e
     */
    public ApplicationException(String message, Throwable e)
    {
        super(e);
        this.message = message;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message
     *            the new message
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Gets the detailed message.
     *
     * @return the detailed message
     */
    public String getDetailedMessage()
    {
        return super.getMessage();
    }
}
