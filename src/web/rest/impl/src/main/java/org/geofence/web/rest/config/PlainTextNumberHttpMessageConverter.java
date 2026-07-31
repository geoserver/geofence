/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;

/**
 * Writes a bare {@link Number} (e.g. the {@code Long} id returned by the various {@code insert} endpoints) as a
 * {@code text/plain} body. Spring's own {@code StringHttpMessageConverter} only writes {@link CharSequence}, so without
 * this a {@code produces = text/plain} endpoint returning a {@code Long} fails whenever a client's {@code Accept}
 * header doesn't force JSON/XML (e.g. a wildcard accept, the curl/most-HTTP-clients default).
 */
public class PlainTextNumberHttpMessageConverter extends AbstractHttpMessageConverter<Number> {

    public PlainTextNumberHttpMessageConverter() {
        super(MediaType.TEXT_PLAIN);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Number.class.isAssignableFrom(clazz);
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    protected Number readInternal(Class<? extends Number> clazz, HttpInputMessage inputMessage) throws IOException {
        throw new UnsupportedOperationException("This converter only writes Numbers as text/plain");
    }

    @Override
    protected void writeInternal(Number number, HttpOutputMessage outputMessage) throws IOException {
        outputMessage.getBody().write(number.toString().getBytes(StandardCharsets.UTF_8));
    }
}
