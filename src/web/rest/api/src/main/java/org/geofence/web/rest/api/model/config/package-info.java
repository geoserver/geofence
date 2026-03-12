@XmlJavaTypeAdapters({@XmlJavaTypeAdapter(type = LocalDateTime.class, value = LocalDateTimeAdapter.class)})
package org.geofence.web.rest.api.model.config;

import java.time.LocalDateTime;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.time.LocalDateTime;
import org.geofence.web.rest.api.model.xmladapters.LocalDateTimeAdapter;
