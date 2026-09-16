/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.services.util;

import java.util.HashSet;
import java.util.Set;
import org.geofence.core.model.LayerAttribute;
import org.geofence.core.model.Rule;
import org.geofence.core.model.enums.AccessType;
import org.geofence.core.model.enums.GrantType;
import org.geofence.core.services.dto.AccessTypeDTO;
import org.geofence.core.services.dto.GrantTypeDTO;
import org.geofence.core.services.dto.LayerAttributeDTO;
import org.geofence.core.services.dto.ShortRule;

/** Maps internal, Hibernate-backed model classes to the plain DTOs exposed by {@code geofence-services-api}. */
public final class DtoMapper {

    private DtoMapper() {}

    public static GrantTypeDTO map(GrantType in) {
        return in == null ? null : GrantTypeDTO.valueOf(in.name());
    }

    public static AccessTypeDTO map(AccessType in) {
        return in == null ? null : AccessTypeDTO.valueOf(in.name());
    }

    public static LayerAttributeDTO map(LayerAttribute in) {
        if (in == null) return null;
        LayerAttributeDTO out = new LayerAttributeDTO();
        out.setName(in.getName());
        out.setDatatype(in.getDatatype());
        out.setAccess(map(in.getAccess()));
        return out;
    }

    public static Set<LayerAttributeDTO> mapAttributes(Set<LayerAttribute> in) {
        if (in == null) return null;
        Set<LayerAttributeDTO> out = new HashSet<>();
        for (LayerAttribute attribute : in) {
            out.add(map(attribute));
        }
        return out;
    }

    public static ShortRule toShortRule(Rule rule) {
        ShortRule out = new ShortRule();
        out.setId(rule.getId());
        out.setPriority(rule.getPriority());
        out.setUserName(rule.getUsername());
        out.setRoleName(rule.getRolename());

        if (rule.getInstance() != null) {
            out.setInstanceId(rule.getInstance().getId());
            out.setInstanceName(rule.getInstance().getName());
        }

        out.setService(rule.getService());
        out.setAddressRange(rule.getAddressRangeString());
        out.setValidAfter(rule.getValidAfterString());
        out.setValidBefore(rule.getValidBeforeString());
        out.setRequest(rule.getRequest());
        out.setSubfield(rule.getSubfield());
        out.setWorkspace(rule.getWorkspace());
        out.setLayer(rule.getLayer());
        out.setAccess(map(rule.getAccess()));
        return out;
    }
}
