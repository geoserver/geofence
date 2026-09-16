/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.geofence.web.rest.api.model.RESTBatchOperation.ServiceName;
import org.geofence.web.rest.api.model.RESTBatchOperation.TypeName;

/**
 * Resolves {@code payload}'s concrete type by hand from the sibling {@code service} field, the JSON equivalent of what
 * {@code @XmlElements} does for XML via the element tag name. Jackson's own polymorphism annotations
 * ({@code @JsonTypeInfo}) either require a new discriminator field in the payload itself or change its shape to a
 * wrapper object - reusing an already-present sibling field as an external discriminator without altering the wire
 * format isn't something they do cleanly, so a hand-written deserializer is more predictable here than fighting that
 * machinery for a wire format that must stay exactly as JAXB already writes/reads it.
 */
class RESTBatchOperationDeserializer extends JsonDeserializer<RESTBatchOperation> {

    @Override
    public RESTBatchOperation deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        RESTBatchOperation op = new RESTBatchOperation();
        op.setId(asLong(node, "id"));
        op.setName(asText(node, "name"));
        op.setCascade(asBoolean(node, "cascade"));
        op.setUserName(asText(node, "userName"));
        op.setGroupName(asText(node, "groupName"));

        ServiceName service = asEnum(node, "service", ServiceName.class);
        op.setService(service);
        op.setType(asEnum(node, "type", TypeName.class));

        JsonNode payloadNode = node.get("payload");
        if (payloadNode != null && !payloadNode.isNull()) {
            op.setPayload(mapper.treeToValue(payloadNode, payloadTypeFor(service)));
        }

        return op;
    }

    private static Class<? extends AbstractRESTPayload> payloadTypeFor(ServiceName service) {
        if (service == null) {
            throw new IllegalArgumentException("Cannot resolve RESTBatchOperation.payload type: 'service' is not set");
        }
        switch (service) {
            case users:
                return RESTInputUser.class;
            case groups:
                return RESTInputGroup.class;
            case instances:
                return RESTInputInstance.class;
            case rules:
                return RESTInputRule.class;
            default:
                throw new IllegalArgumentException(
                        "Cannot resolve RESTBatchOperation.payload type for service: " + service);
        }
    }

    private static String asText(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asText();
    }

    private static Long asLong(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asLong();
    }

    private static Boolean asBoolean(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asBoolean();
    }

    private static <E extends Enum<E>> E asEnum(JsonNode node, String field, Class<E> type) {
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : Enum.valueOf(type, v.asText());
    }
}
