/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.impl;

import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.model.GSInstance;
import org.geofence.core.model.IPAddressRange;
import org.geofence.core.model.LayerAttribute;
import org.geofence.core.model.LayerDetails;
import org.geofence.core.model.Rule;
import org.geofence.core.model.UserGroup;
import org.geofence.core.model.enums.AccessType;
import org.geofence.core.model.enums.AdminGrantType;
import org.geofence.core.model.enums.GrantType;
import org.geofence.core.model.enums.InsertPosition;
import org.geofence.core.model.enums.LayerType;
import org.geofence.core.model.enums.SpatialFilterType;
import org.geofence.core.services.InstanceAdminService;
import org.geofence.core.services.dto.ShortGroup;
import org.geofence.core.services.dto.ShortInstance;
import org.geofence.core.services.exception.NotFoundServiceEx;
import org.geofence.web.rest.api.exception.BadRequestRestEx;
import org.geofence.web.rest.api.exception.NotFoundRestEx;
import org.geofence.web.rest.api.model.RESTInputRule;
import org.geofence.web.rest.api.model.RESTLayerAttribute;
import org.geofence.web.rest.api.model.RESTLayerConstraints;
import org.geofence.web.rest.api.model.RESTOutputGroup;
import org.geofence.web.rest.api.model.RESTOutputRule;
import org.geofence.web.rest.api.model.RESTRulePosition;
import org.geofence.web.rest.api.model.RESTShortInstance;
import org.geofence.web.rest.api.model.enums.RESTAccessType;
import org.geofence.web.rest.api.model.enums.RESTAdminGrantType;
import org.geofence.web.rest.api.model.enums.RESTGrantType;
import org.geofence.web.rest.api.model.enums.RESTLayerType;
import org.geofence.web.rest.api.model.enums.RESTSpatialFilterType;
import org.geofence.web.rest.api.model.util.IdName;
import org.geofence.web.rest.utils.GeomUtils;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author etj */
@Component
public class RESTMapper {

    private static final Logger LOGGER = LogManager.getLogger(RESTConfigServiceImpl.class);

    @Autowired
    private InstanceAdminService instanceAdminService;

    public static RESTGrantType map(GrantType in) {
        return in == null ? null : RESTGrantType.valueOf(in.name());
    }

    public static GrantType map(RESTGrantType in) {
        return in == null ? null : GrantType.valueOf(in.name());
    }

    public static RESTAdminGrantType map(AdminGrantType in) {
        return in == null ? null : RESTAdminGrantType.valueOf(in.name());
    }

    public static AdminGrantType map(RESTAdminGrantType in) {
        return in == null ? null : AdminGrantType.valueOf(in.name());
    }

    public static RESTAccessType map(AccessType in) {
        return in == null ? null : RESTAccessType.valueOf(in.name());
    }

    public static AccessType map(RESTAccessType in) {
        return in == null ? null : AccessType.valueOf(in.name());
    }

    public static RESTLayerType map(LayerType in) {
        return in == null ? null : RESTLayerType.valueOf(in.name());
    }

    public static LayerType map(RESTLayerType in) {
        return in == null ? null : LayerType.valueOf(in.name());
    }

    public static RESTSpatialFilterType map(SpatialFilterType in) {
        return in == null ? null : RESTSpatialFilterType.valueOf(in.name());
    }

    public static SpatialFilterType map(RESTSpatialFilterType in) {
        return in == null ? null : SpatialFilterType.valueOf(in.name());
    }

    public static InsertPosition map(RESTRulePosition.RESTPositionReference in) {
        if (in == null) return null;

        return switch (in) {
            case fixedPriority -> InsertPosition.FIXED;
            case offsetFromTop -> InsertPosition.FROM_START;
            case offsetFromBottom -> InsertPosition.FROM_END;
            default -> throw new AssertionError("Unknown RESTPositionReference " + in);
        };
    }

    public Rule map(RESTOutputRule in) {

        Map<String, GSInstance> instances = new HashMap<>();

        Rule out = new Rule();
        out.setAccess(RESTMapper.map(in.getGrant()));
        out.setPriority(in.getPriority());
        out.setUsername(in.getUsername());
        out.setRolename(in.getRolename());

        if (in.getInstance() != null) {
            String name = in.getInstance().getName();
            GSInstance instance = instances.get(name);
            if (instance == null) {
                instance = instanceAdminService.get(name);
                instances.put(name, instance);
            }
            out.setInstance(instance);
        }
        out.setService(in.getService());
        out.setRequest(in.getRequest());
        out.setWorkspace(in.getWorkspace());
        out.setLayer(in.getLayer());

        if (in.getConstraints() != null) {
            LOGGER.warn("TODO::: Constraints exist but will not be parsed for rule " + in);
        }

        return out;
    }

    public static RESTOutputGroup map(UserGroup in) {
        RESTOutputGroup out = new RESTOutputGroup();

        out.setId(in.getId());
        out.setName(in.getName());
        out.setEnabled(in.getEnabled());
        out.setExtId(in.getExtId());

        return out;
    }

    public static RESTOutputGroup map(ShortGroup in) {
        RESTOutputGroup out = new RESTOutputGroup();

        out.setId(in.getId());
        out.setName(in.getName());
        out.setEnabled(in.isEnabled());
        out.setExtId(in.getExtId());

        return out;
    }

    public static ShortGroup map(RESTOutputGroup in) {
        ShortGroup out = new ShortGroup();

        out.setId(in.getId());
        out.setName(in.getName());
        out.setEnabled(in.isEnabled());
        out.setExtId(in.getExtId());

        return out;
    }

    public static RESTShortInstance map(ShortInstance in) {
        RESTShortInstance out = new RESTShortInstance();
        out.setId(in.getId());
        out.setName(in.getName());
        out.setDescription(in.getDescription());
        out.setUrl(in.getUrl());
        return out;
    }

    public static RESTLayerAttribute map(LayerAttribute in) {
        RESTLayerAttribute out = new RESTLayerAttribute();
        out.setName(in.getName());
        out.setDatatype(in.getDatatype());
        out.setAccess(map(in.getAccess()));
        return out;
    }

    public static LayerAttribute map(RESTLayerAttribute in) {
        LayerAttribute out = new LayerAttribute();
        out.setName(in.getName());
        out.setDatatype(in.getDatatype());
        out.setAccess(map(in.getAccess()));
        return out;
    }

    public static RESTOutputRule map(Rule rule) {
        RESTOutputRule out = new RESTOutputRule();
        out.setId(rule.getId());
        out.setPriority(rule.getPriority());
        out.setGrant(map(rule.getAccess()));

        out.setUsername(rule.getUsername());
        out.setRolename(rule.getRolename());
        if (rule.getInstance() != null) {
            out.setInstance(
                    new IdName(rule.getInstance().getId(), rule.getInstance().getName()));
        }

        out.setIpaddress(rule.getAddressRangeString());
        out.setValidafter(rule.getValidAfterString());
        out.setValidbefore(rule.getValidBeforeString());

        out.setService(rule.getService());
        out.setRequest(rule.getRequest());
        out.setSubfield(rule.getSubfield());
        out.setWorkspace(rule.getWorkspace());
        out.setLayer(rule.getLayer());

        if (rule.getLayerDetails() != null) {
            LayerDetails details = rule.getLayerDetails();
            RESTLayerConstraints constraints = new RESTLayerConstraints();
            if (details.getAllowedStyles() != null) {
                constraints.setAllowedStyles(new HashSet(details.getAllowedStyles()));
            }
            if (details.getAttributes() != null) {
                constraints.setAttributes(new HashSet(details.getAttributes()));
            }
            constraints.setCqlFilterRead(details.getCqlFilterRead());
            constraints.setCqlFilterWrite(details.getCqlFilterWrite());
            constraints.setDefaultStyle(details.getDefaultStyle());
            MultiPolygon area = details.getArea();
            if (area != null) {
                String areaWKT = "SRID=" + area.getSRID() + ";" + area.toText();
                constraints.setRestrictedAreaWkt(areaWKT);
            }
            if (details.getSpatialFilterType() != null) {
                constraints.setSpatialFilterType(map(details.getSpatialFilterType()));
            }

            constraints.setType(map(details.getType()));

            out.setConstraints(constraints);
        }

        return out;
    }

    public Rule map(RESTInputRule in) {
        Rule rule = new Rule();

        rule.setPriority(in.getPosition().getValue());
        rule.setAccess(RESTMapper.map(in.getGrant()));

        rule.setUsername(in.getUsername());
        rule.setRolename(in.getRolename());

        if (in.getInstance() != null) {
            rule.setInstance(_getInstance(in.getInstance()));
        }

        if (StringUtils.isNotBlank(in.getIpaddress())) {
            rule.setAddressRange(new IPAddressRange(in.getIpaddress()));
        }

        if (StringUtils.isNotBlank(in.getValidafter())) {
            rule.setValidAfter(Date.valueOf(in.getValidafter()));
        }
        if (StringUtils.isNotBlank(in.getValidbefore())) {
            rule.setValidBefore(Date.valueOf(in.getValidbefore()));
        }

        rule.setService(in.getService());
        rule.setRequest(in.getRequest());
        rule.setSubfield(in.getSubfield());
        rule.setWorkspace(in.getWorkspace());
        rule.setLayer(in.getLayer());

        return rule;
    }

    public static LayerDetails map(RESTLayerConstraints constraints) {
        if (constraints == null) {
            return null;
        }

        LayerDetails details = new LayerDetails();

        if (constraints.getAllowedStyles() != null) {
            details.setAllowedStyles(new HashSet(constraints.getAllowedStyles()));
        }
        if (constraints.getAttributes() != null) {
            details.setAttributes(new HashSet(
                    constraints.getAttributes().stream().map(a -> map(a)).toList()));
        }
        details.setCqlFilterRead(constraints.getCqlFilterRead());
        details.setCqlFilterWrite(constraints.getCqlFilterWrite());
        details.setDefaultStyle(constraints.getDefaultStyle());
        if (constraints.getRestrictedAreaWkt() != null) {
            try {
                Geometry g = GeomUtils.wktToGeom(constraints.getRestrictedAreaWkt());
                details.setArea((MultiPolygon) g);
            } catch (ParseException ex) {
                throw new BadRequestRestEx("Error parsing WKT:" + ex.getMessage());
            }
        }

        details.setSpatialFilterType(map(constraints.getSpatialFilterType()));
        details.setType(map(constraints.getType()));

        return details;
    }

    private GSInstance _getInstance(IdName filter) throws BadRequestRestEx, NotFoundRestEx {

        try {
            if (filter.getId() != null) {
                return instanceAdminService.get(filter.getId());
            } else if (filter.getName() != null) {
                return instanceAdminService.get(filter.getName());
            } else {
                throw new BadRequestRestEx("Bad GSInstance filter " + filter);
            }
        } catch (NotFoundServiceEx e) {
            LOGGER.warn("GSInstance not found " + filter);
            throw new NotFoundRestEx("GSInstance not found " + filter);
        }
    }
}
