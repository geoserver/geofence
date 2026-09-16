package org.geofence.web.rest.api.model.xmladapters;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    @Override
    public LocalDateTime unmarshal(String v) {
        return v == null ? null : LocalDateTime.parse(v);
    }

    @Override
    public String marshal(LocalDateTime v) {
        return v == null ? null : v.toString();
    }
}
