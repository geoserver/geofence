/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.login.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.geoserver.geofence.api.dto.GrantedAuths;
import org.geoserver.geofence.api.exception.AuthException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 *
 * @author etj
 */
public class SessionManager
{

    private static final Logger LOGGER = LogManager.getLogger(SessionManager.class);

    private static final int MAXSESSIONS = 100;

    private final SessionMap activeSessions = new SessionMap(MAXSESSIONS);

    public String createSession(String username, GrantedAuths grantedAuths)
    {

        String token = TokenEncoder.encode(username + grantedAuths.hashCode(), ("" +
                    System.nanoTime() + "" + System.currentTimeMillis()).substring(0, 16));

        LoggedInMember loggedInMember = new LoggedInMember();
        loggedInMember.grantedAuths = grantedAuths;
        loggedInMember.username = username;
        activeSessions.put(token, loggedInMember);

        return token;
    }

    public void closeSession(String token)
    {
        LoggedInMember member = activeSessions.remove(token);
        if (member == null)
        {
            LOGGER.warn("Tried to close non existent session. Token " + token);
        }
        else
        {
            LOGGER.info("Closing session for user [" + member.username + "] token " + token);
        }
    }

    public GrantedAuths getGrantedAuthorities(String token) throws AuthException
    {
        LoggedInMember loggedInMember = activeSessions.get(token);
        if (loggedInMember != null)
        {
            return loggedInMember.grantedAuths;
        }
        else
        {
            throw new AuthException("No active session for token " + token);
        }
    }

    private static class LoggedInMember
    {
        public GrantedAuths grantedAuths;

        public String username;
    }

    /**
     * If too many sessions, throw away the older ones. Todo: has to be improved and made
     * customizable.
     */
    class SessionMap extends LinkedHashMap<String, LoggedInMember>
    {

        private final int MAX_ENTRIES;

        public SessionMap(int maxEntries)
        {
            this.MAX_ENTRIES = maxEntries;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest)
        {
            if (size() > MAX_ENTRIES)
            {
                LoggedInMember member = (LoggedInMember) eldest.getValue();
                LOGGER.info("Removing stale token " + eldest.getKey() + " for member " +
                    member.username);

                // todo: have to logout related member?
                return true;
            }
            else
            {
                return false;
            }
        }
    }

}
