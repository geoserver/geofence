/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.config;

import org.geofence.web.rest.api.exception.GeoFenceRestEx;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Turns a thrown {@link GeoFenceRestEx} into an HTTP response using its status code and message, for every
 * {@code @RestController} under {@code org.geofence.web.rest}.
 */
@ControllerAdvice(basePackages = "org.geofence.web.rest")
public class GeofenceRestExceptionHandler {

    @ExceptionHandler(GeoFenceRestEx.class)
    public ResponseEntity<String> handle(GeoFenceRestEx ex) {
        return ResponseEntity.status(HttpStatus.valueOf(ex.getStatus()))
                .contentType(MediaType.TEXT_PLAIN)
                .body(ex.getMessage());
    }
}
