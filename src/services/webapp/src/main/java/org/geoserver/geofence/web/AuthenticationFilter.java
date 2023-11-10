/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.web;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;

public class AuthenticationFilter implements Filter {

    private static final String ANONYMOUS_ROLE = "ANONYMOUS";

    public void destroy() {
        // nothing to do
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        httpRequest.getSession().setAttribute("userLoggedToken", "");
        Authentication authentication = new AnonymousAuthenticationToken("geofence", "null", 
                Collections.singletonList(new SimpleGrantedAuthority(ANONYMOUS_ROLE)));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(httpRequest, response);
    }

    public void init(FilterConfig filterConfig) {
    }
}
