/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationFilter implements Filter {

    private static final String ANONYMOUS_ROLE = "ANONYMOUS";

    public void destroy() {
        // nothing to do
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        httpRequest.getSession().setAttribute("userLoggedToken", "");
        Authentication authentication = new AnonymousAuthenticationToken(
                "geofence", "null", Collections.singletonList(new SimpleGrantedAuthority(ANONYMOUS_ROLE)));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(httpRequest, response);
    }

    public void init(FilterConfig filterConfig) {}
}
