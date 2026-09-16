/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Serializes a bean's own fields normally, then drops the null-valued ones - without touching the bean class's own
 * (class-level) Jackson configuration. Meant to be used as a list property's {@code @JsonSerialize(contentUsing = ...)}
 * serializer, so a rule DTO shows every field when returned standalone but omits nulls when nested inside a rule list.
 */
class NonNullFieldsSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        ObjectMapper mapper = (ObjectMapper) gen.getCodec();
        JsonNode node = mapper.valueToTree(value);
        if (node instanceof ObjectNode objectNode) {
            for (Iterator<Map.Entry<String, JsonNode>> it =
                            objectNode.properties().iterator();
                    it.hasNext(); ) {
                if (it.next().getValue().isNull()) {
                    it.remove();
                }
            }
        }
        gen.writeTree(node);
    }
}
