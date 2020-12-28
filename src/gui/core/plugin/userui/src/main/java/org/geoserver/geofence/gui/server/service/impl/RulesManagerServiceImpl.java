/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.gui.server.service.impl;

import it.geosolutions.geoserver.rest.decoder.RESTLayerGroup;
import org.geoserver.geofence.core.model.LayerAttribute;
import org.geoserver.geofence.core.model.LayerDetails;
import org.geoserver.geofence.core.model.RuleLimits;
import org.geoserver.geofence.core.model.enums.AccessType;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.core.model.enums.LayerType;
import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.GSInstanceModel;
import org.geoserver.geofence.gui.client.model.RuleModel;
import org.geoserver.geofence.gui.client.model.data.LayerAttribUI;
import org.geoserver.geofence.gui.client.model.data.LayerCustomProps;
import org.geoserver.geofence.gui.client.model.data.LayerDetailsInfo;
import org.geoserver.geofence.gui.client.model.data.LayerLimitsInfo;
import org.geoserver.geofence.gui.client.model.data.LayerStyle;
import org.geoserver.geofence.gui.client.model.data.rpc.RpcPageLoadResult;
import org.geoserver.geofence.gui.server.service.IRulesManagerService;
import org.geoserver.geofence.gui.service.GeofenceRemoteService;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.RuleFilter.SpecialFilterType;
import org.geoserver.geofence.services.dto.ShortRule;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTFeatureType;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.apache.commons.lang.StringUtils;
import org.geoserver.geofence.core.model.GSInstance;
import org.geoserver.geofence.core.model.IPAddressRange;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.core.model.enums.CatalogMode;
import org.geoserver.geofence.gui.client.model.data.ClientCatalogMode;
import org.geoserver.geofence.services.util.IPUtils;
import org.springframework.dao.DuplicateKeyException;

/**
 * The Class RulesManagerServiceImpl.
 */
@Component("rulesManagerServiceGWT")
public class RulesManagerServiceImpl implements IRulesManagerService {

    private final static Logger logger = LoggerFactory.getLogger(RulesManagerServiceImpl.class);

    @Autowired
    private GeofenceRemoteService geofenceRemoteService;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.server.service.IFeatureService#loadFeature
     * (com.extjs.gxt.ui. client.data.PagingLoadConfig, java.lang.String)
     */
    public PagingLoadResult<RuleModel> getRules(int offset, int limit, boolean full)
            throws ApplicationException {
        int start = offset;

        List<RuleModel> ruleListDTO = new ArrayList<RuleModel>();

        long rulesCount = geofenceRemoteService.getRuleAdminService()
                .getCountAll();

        Long t = new Long(rulesCount);

        int page = (start == 0) ? start : (start / limit);

        RuleFilter any = new RuleFilter(SpecialFilterType.ANY);
        List<ShortRule> rulesList = geofenceRemoteService.getRuleAdminService()
                .getList(any, page, limit);

        if (rulesList == null) {
            if (logger.isErrorEnabled()) {
                logger.error("No rule found on server");
            }
            throw new ApplicationException("No rule found on server");
        }

        Iterator<ShortRule> it = rulesList.iterator();

        while (it.hasNext()) {
            ShortRule shortRule = it.next();

            Rule fullRule;
            try {
                fullRule = geofenceRemoteService.getRuleAdminService().get(shortRule.getId());
            } catch (NotFoundServiceEx e) {
                if (logger.isErrorEnabled()) {
                    logger.error("Details for rule " + shortRule.getPriority()
                            + " not found on Server!");
                }
                throw new ApplicationException("Details for profile "
                        + shortRule.getPriority() + " not found on Server!");
            }

            RuleModel ruleDTO = new RuleModel();

            ruleDTO.setId(shortRule.getId());
            ruleDTO.setPriority(fullRule.getPriority());

            ruleDTO.setUsername(fullRule.getUsername() == null ? "*" : fullRule.getUsername());
            ruleDTO.setRolename(fullRule.getRolename() == null ? "*" : fullRule.getRolename());

            if (fullRule.getInstance() == null) {
                GSInstanceModel all = new GSInstanceModel();
                all.setId(-1);
                all.setName("*");
                all.setBaseURL("*");
            } else {
                GSInstance remote_instance = fullRule.getInstance();
                GSInstanceModel local_instance = new GSInstanceModel();
                local_instance.setId(remote_instance.getId());
                local_instance.setName(remote_instance.getName());
                local_instance.setBaseURL(remote_instance.getBaseURL());
                local_instance.setUsername(remote_instance.getUsername());
                local_instance.setPassword(remote_instance.getPassword());
                ruleDTO.setInstance(local_instance);
            }

            ruleDTO.setSourceIPRange(fullRule.getAddressRange() != null
                    ? fullRule.getAddressRange().getCidrSignature() : "*");

            ruleDTO.setService((fullRule.getService() != null)
                    ? fullRule.getService() : "*");

            ruleDTO.setRequest((fullRule.getRequest() != null)
                    ? fullRule.getRequest() : "*");

            ruleDTO.setWorkspace((fullRule.getWorkspace() != null)
                    ? fullRule.getWorkspace() : "*");

            ruleDTO.setLayer((fullRule.getLayer() != null)
                    ? fullRule.getLayer() : "*");

            ruleDTO.setGrant((fullRule.getAccess() != null)
                    ? fullRule.getAccess().toString() : "ALLOW");

            ruleListDTO.add(ruleDTO);
        }

        return new RpcPageLoadResult<RuleModel>(ruleListDTO, offset, t.intValue());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.server.service.IRulesManagerService#saveAllRules
     * (java.util.List)
     */
    public void saveRule(RuleModel ruleModel) throws ApplicationException {

        if (logger.isDebugEnabled()) {
            logger.debug("Trying to save rule " + ruleModel);
        }

        IPAddressRange addressRange = validateSourceRange(ruleModel.getSourceIPRange());

        Rule rule = new Rule(
                ruleModel.getPriority(),
                "*".equals(ruleModel.getUsername()) ? null : ruleModel.getUsername(),
                "*".equals(ruleModel.getRolename()) ? null : ruleModel.getRolename(),
                getInstance(ruleModel.getInstance()),
                addressRange,
                "*".equals(ruleModel.getService()) ? null : ruleModel.getService(),
                "*".equals(ruleModel.getRequest()) ? null : ruleModel.getRequest(),
                "*".equals(ruleModel.getWorkspace()) ? null : ruleModel.getWorkspace(),
                "*".equals(ruleModel.getLayer()) ? null : ruleModel.getLayer(),
                getAccessType(ruleModel.getGrant()));

        try {
            if (ruleModel.getId() == -1) { // REQUEST FOR INSERT
                rule.setId(null);
                geofenceRemoteService.getRuleAdminService().insert(rule);
            } else {                  // REQUEST FOR UPDATE
                long ruleId = ruleModel.getId();
                rule.setId(ruleId);
                try {
                    geofenceRemoteService.getRuleAdminService().update(rule);
                    Rule loaded = geofenceRemoteService.getRuleAdminService().get(ruleId);
                    if (!loaded.getAccess().name().equalsIgnoreCase(ruleModel.getGrant())) {
                        geofenceRemoteService.getRuleAdminService().setDetails(ruleId, null);
                        geofenceRemoteService.getRuleAdminService().setLimits(ruleId, null);
                    }
                } catch (NotFoundServiceEx e) {
                    logger.error("Error updating a rule", e);
                    throw new ApplicationException("Error updating a rule");
                }
            }
        } catch (DuplicateKeyException e) {
            String message = "A similar rule is already present";
            logger.error(message);
            throw new ApplicationException(message);
        }
    }

    protected IPAddressRange validateSourceRange(String srcIP) throws ApplicationException {
        IPAddressRange addressRange = null;
        if (srcIP == null || "*".equals(srcIP) || "".equals(srcIP.trim())) {
            addressRange = null;
        } else if (!IPUtils.isRangeValid(srcIP)) {
            logger.error("Invalid IP range '" + srcIP + "'");
            throw new ApplicationException("Invalid IP range '" + srcIP + "'");
        } else {
            addressRange = new IPAddressRange(srcIP);
        }
        return addressRange;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.server.service.IRulesManagerService#saveAllRules
     * (java.util.List)
     */
    public void deleteRule(RuleModel rule) throws ApplicationException {

        if (rule.getId() != -1) {
            try {
                geofenceRemoteService.getRuleAdminService()
                        .delete(rule.getId());
            } catch (NotFoundServiceEx e) {
                logger.warn("Trying to delete not existing rule #" + rule.getId());
            }
        }

    }

    /*
     *
     */
    public void updatePriorities(RuleModel rule, long shift) {
        geofenceRemoteService.getRuleAdminService()
                .shift(rule.getPriority(), 1);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.server.service.IRulesManagerService#saveAllRules
     * (java.util.List)
     */
    public void saveAllRules(List<RuleModel> rules) throws ApplicationException {
        for (ShortRule rule : geofenceRemoteService.getRuleAdminService()
                .getAll()) {
            try {
                geofenceRemoteService.getRuleAdminService()
                        .delete(rule.getId());
            } catch (NotFoundServiceEx e) {
                logger.error(e.getMessage(), e);
                throw new ApplicationException(e.getMessage(), e);
            }
        }

        for (RuleModel localRule : rules) {

            IPAddressRange addressRange = validateSourceRange(localRule.getSourceIPRange());

            Rule rule = new Rule(
                    localRule.getPriority(),
                    "*".equals(localRule.getUsername()) ? null : localRule.getUsername(),
                    "*".equals(localRule.getRolename()) ? null : localRule.getRolename(),
                    getInstance(localRule.getInstance()),
                    addressRange,
                    "*".equals(localRule.getService()) ? null : localRule.getService(),
                    "*".equals(localRule.getRequest()) ? null : localRule.getRequest(),
                    "*".equals(localRule.getWorkspace()) ? null : localRule.getWorkspace(),
                    "*".equals(localRule.getLayer()) ? null : localRule.getLayer(),
                    getAccessType(localRule.getGrant()));
            geofenceRemoteService.getRuleAdminService().insert(rule);
        }
    }

    /**
     * Gets the access type.
     *
     * @param grant the grant
     * @return the access type
     */
    private GrantType getAccessType(String grant) {
        if (grant != null) {
            return GrantType.valueOf(grant);
        } else {
            return GrantType.ALLOW;
        }
    }

    /**
     * Gets the single instance of RulesManagerServiceImpl.
     *
     * @param instance the instance
     * @return single instance of RulesManagerServiceImpl
     */
    private org.geoserver.geofence.core.model.GSInstance getInstance(
            GSInstanceModel instance) {
        org.geoserver.geofence.core.model.GSInstance remote_instance = null;
        try {
            if ((instance != null) && (instance.getId() != -1)) {
                remote_instance = geofenceRemoteService
                        .getInstanceAdminService().get(instance.getId());
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        }

        return remote_instance;
    }

    public void setDetailsProps(Long ruleId, List<LayerCustomProps> customProps) {
        logger.error("TODO: rule refactoring!!! custom props have been removed");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.server.service.IRulesManagerService#
     * getLayerAttributes(com.extjs .gxt.ui.client.data.PagingLoadConfig,
     * org.geoserver.geofence.gui.client.model.Rule)
     */
    public List<LayerAttribUI> getLayerAttributes(RuleModel rule) {

        LayerDetails layerDetails = null;
        List<LayerAttribUI> layerAttributesDTO = new ArrayList<LayerAttribUI>();

        try {

            layerDetails = geofenceRemoteService.getRuleAdminService()
                    .get(rule.getId()).getLayerDetails();

            if (!layerDetails.getType().equals(LayerType.LAYERGROUP))
                layerAttributesDTO = loadAttribute(rule);

            if ((layerDetails != null) && (layerAttributesDTO != null)) {
                Set<LayerAttribute> layerAttributes = layerDetails
                        .getAttributes();

                if (layerAttributes.size() > 0) {
                    if (layerDetails.getType().equals(LayerType.VECTOR)) {
						// ///////////////////////
                        // Vector Layer
                        // ///////////////////////

                        Iterator<LayerAttribute> iterator = layerAttributes
                                .iterator();

                        while (iterator.hasNext()) {
                            LayerAttribute layerAttribute = iterator.next();

                            for (int i = 0; i < layerAttributesDTO.size(); i++) {

                                String attrName = layerAttributesDTO.get(i).getName();

                                if (layerAttribute.getName().equalsIgnoreCase(attrName)) {

                                    LayerAttribUI layAttrUI = new LayerAttribUI();
                                    layAttrUI.setName(layerAttribute.getName());
                                    layAttrUI.setDataType(layerAttribute.getDatatype());
                                    layAttrUI.setAccessType(layerAttribute.getAccess().toString());

                                    layerAttributesDTO.set(i, layAttrUI);
                                }
                            }
                        }
                    }
                }
            }

        } catch (NotFoundServiceEx e) {
            logger.error(e.getMessage(), e);
            throw new ApplicationException(e.getMessage(), e);
        }

        return layerAttributesDTO;
    }

    /**
     * @param rule
     * @return List<LayerAttribUI>
     */
    private List<LayerAttribUI> loadAttribute(RuleModel rule) {
        List<LayerAttribUI> layerAttributesDTO = new ArrayList<LayerAttribUI>();
//		Set<LayerAttribute> layerAttributes = null;

        GSInstanceModel gsInstance = rule.getInstance();
        GeoServerRESTReader gsreader;

        String ws = rule.getWorkspace();
        if(StringUtils.isBlank(ws) || "*".equals(ws))
            throw new ApplicationException("A workspace name is needed");

        String layername = rule.getLayer();
        if(StringUtils.isBlank(layername) || "*".equals(layername))
            throw new ApplicationException("A layer name is needed");

        try {
            gsreader = new GeoServerRESTReader(
                    gsInstance.getBaseURL(),
                    gsInstance.getUsername(), gsInstance.getPassword());

            RESTLayer layer = gsreader.getLayer(ws, layername);

            if (layer.getType().equals(RESTLayer.Type.VECTOR)) {
                // ///////////////////////
                // Vector Layer
                // ///////////////////////
                RESTFeatureType featureType = gsreader.getFeatureType(layer);

                for (RESTFeatureType.Attribute attrFromGS : featureType.getAttributes()) {
                    LayerAttribUI layAttrUI = new LayerAttribUI();
                    layAttrUI.setName(attrFromGS.getName());
                    layAttrUI.setDataType(attrFromGS.getBinding());
                    layAttrUI.setAccessType("READWRITE");

                    layerAttributesDTO.add(layAttrUI);
                }

            } else {
                // ///////////////////////
                // Raster Layer
                // ///////////////////////
                layerAttributesDTO = null;
            }

        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            throw new ApplicationException(e.getMessage(), e);
        }

        return layerAttributesDTO;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.server.service.IRulesManagerService#
     * setLayerAttributes(java.lang .Long, java.util.List)
     */
    public void setLayerAttributes(Long ruleId,
            List<LayerAttribUI> layerAttributes) {

        LayerDetails details = null;

        try {
            details = geofenceRemoteService.getRuleAdminService().get(ruleId)
                    .getLayerDetails();

            if (details == null) {
                details = new LayerDetails();
                details.setType(LayerType.VECTOR);
            }

            Set<LayerAttribute> layerAttribs = new HashSet<LayerAttribute>();

            Iterator<LayerAttribUI> iterator = layerAttributes.iterator();
            while (iterator.hasNext()) {
                LayerAttribUI layerAttribUI = iterator.next();

                String accessType = layerAttribUI.getAccessType();

                if (accessType != null) {
                    LayerAttribute attr = new LayerAttribute();

                    attr.setName(layerAttribUI.getName());
                    attr.setDatatype(layerAttribUI.getDataType());

                    if (accessType.equalsIgnoreCase("NONE")) {
                        attr.setAccess(AccessType.NONE);
                    } else if (accessType.equalsIgnoreCase("READONLY")) {
                        attr.setAccess(AccessType.READONLY);
                    } else {
                        attr.setAccess(AccessType.READWRITE);
                    }

                    layerAttribs.add(attr);
                }

            }

            details.setAttributes(layerAttribs);
            geofenceRemoteService.getRuleAdminService().setDetails(ruleId,
                    details);

        } catch (NotFoundServiceEx e) {
            logger.error(e.getMessage(), e);
            throw new ApplicationException(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.server.service.IRulesManagerService#
     * saveLayerDetails(org.geoserver
     * .geofence.gui.client.model.data.LayerDetailsForm)
     */
    public LayerDetailsInfo saveLayerDetailsInfo(
            LayerDetailsInfo layerDetailsInfo, List<LayerStyle> layerStyles) {

        Long ruleId = layerDetailsInfo.getRuleId();
        LayerDetails oldDetails;

        try {
            oldDetails = geofenceRemoteService.getRuleAdminService()
                    .get(ruleId).getLayerDetails();

            LayerDetails details;
            if (oldDetails == null) {
                logger.info("Creating new details for rule " + ruleId);
                details = new LayerDetails();
            } else {
                details = oldDetails;
            }

            // Reload layer info from GeoServer
            // (in the rule we may have changed the layer)
            org.geoserver.geofence.core.model.Rule rule =
                    geofenceRemoteService.getRuleAdminService().get(ruleId);
            org.geoserver.geofence.core.model.GSInstance gsInstance =
                    rule.getInstance();

            String ws = rule.getWorkspace();


            String layername = rule.getLayer();
            if(StringUtils.isBlank(layername) || "*".equals(layername))
                throw new ApplicationException("Rule is missing the needed layer name");

            GeoServerRESTReader gsreader = new GeoServerRESTReader(
                    gsInstance.getBaseURL(),
                    gsInstance.getUsername(),gsInstance.getPassword());

            RESTLayerGroup layerGroup=gsreader.getLayerGroup(ws,layername);
            if (layerGroup!=null){
                details.setType(LayerType.LAYERGROUP);
            } else {

                if(StringUtils.isBlank(ws) || "*".equals(ws))
                    throw new ApplicationException("Rule is missing the needed workspace name");
                RESTLayer layer = gsreader.getLayer(ws, layername);

                if (layer != null) {
                    if (layer.getType().equals(RESTLayer.Type.VECTOR)) {
                        details.setType(LayerType.VECTOR);
                    } else {
                        details.setType(LayerType.RASTER);
                    }
                } else {
                    // error encountered while loading data from GeoServer
                    if (oldDetails == null) {
                        logger.error("Error loading layer info from GeoServer");
                        throw new ApplicationException("Error loading layer info from GeoServer");
                    } else {
                        logger.warn("Error reloading layer info from GeoServer");
                    }
                }
            }

			// ///////////////////////////////////
            // Saving the layer details info
            // ///////////////////////////////////
            if (details.getType().equals(LayerType.VECTOR)) {
                details.setCqlFilterRead(layerDetailsInfo.getCqlFilterRead());
                details.setCqlFilterWrite(layerDetailsInfo.getCqlFilterWrite());
            } else {
                // todo: set cql filters to null?
            }

            details.setDefaultStyle(layerDetailsInfo.getDefaultStyle());

            CatalogMode cm = fromClientCM(layerDetailsInfo.getCatalogMode());
            details.setCatalogMode(cm);

            String allowedArea = layerDetailsInfo.getAllowedArea();
            if (allowedArea != null) {
                MultiPolygon the_geom = null;
                WKTReader wktReader = new WKTReader();
                Geometry geometry = wktReader.read(allowedArea);
                if (geometry instanceof MultiPolygon) {
                    the_geom = (MultiPolygon) geometry;
                    the_geom.setSRID(Integer
                            .valueOf(layerDetailsInfo.getSrid()).intValue());
                    details.setArea(the_geom);
                } else if (geometry instanceof Polygon) {
                    GeometryFactory factory = new GeometryFactory();
                    the_geom = new MultiPolygon(
                            new Polygon[]{(Polygon) geometry}, factory);
                }

                if (the_geom != null) {
                    the_geom.setSRID(Integer
                            .valueOf(layerDetailsInfo.getSrid()).intValue());
                    details.setArea(the_geom);
                }
            } else {
                details.setArea(null);
            }

			// ///////////////////////////////////
            // Saving the available styles if any
            // ///////////////////////////////////
            Set<String> allowedStyles = new HashSet<String>();

            Iterator<LayerStyle> iterator = layerStyles.iterator();

            while (iterator.hasNext()) {
                LayerStyle style = iterator.next();

                if (style.isEnabled()) {
                    allowedStyles.add(style.getStyle());
                }
            }

            if (oldDetails == null) {
                details.setAllowedStyles(allowedStyles);
            } else {
                geofenceRemoteService.getRuleAdminService().setAllowedStyles(
                        ruleId, allowedStyles);
            }

            geofenceRemoteService.getRuleAdminService().setDetails(ruleId,
                    details);

        } catch (NotFoundServiceEx e) {
            logger.error(e.getMessage(), e);
            throw new ApplicationException(e.getMessage(), e);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            throw new ApplicationException(e.getMessage(), e);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            throw new ApplicationException(e.getMessage(), e);
        }

        return layerDetailsInfo;
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.geoserver.geofence.gui.server.service.IRulesManagerService#
     * getLayerDetailsInfo(it. geosolutions.geofence.gui.client.model.Rule)
     */
    public LayerDetailsInfo getLayerDetailsInfo(RuleModel rule) {
        Long ruleId = rule.getId();
        LayerDetails layerDetails = null;
        LayerDetailsInfo layerDetailsInfo = null;

        try {
            layerDetails = geofenceRemoteService.getRuleAdminService()
                    .get(ruleId).getLayerDetails();

            if (layerDetails != null) {
                layerDetailsInfo = new LayerDetailsInfo();
                layerDetailsInfo.setRuleId(ruleId);
                layerDetailsInfo.setCqlFilterRead(layerDetails
                        .getCqlFilterRead());
                layerDetailsInfo.setCqlFilterWrite(layerDetails
                        .getCqlFilterWrite());
                layerDetailsInfo
                        .setDefaultStyle(layerDetails.getDefaultStyle());

                ClientCatalogMode ccm = toClientCM(layerDetails.getCatalogMode());
                layerDetailsInfo.setCatalogMode(ccm);

                MultiPolygon the_geom = null;
                Geometry geometry = layerDetails.getArea();

                if (geometry instanceof MultiPolygon) {
                    the_geom = (MultiPolygon) geometry;
                } else if (geometry instanceof Polygon) {
                    GeometryFactory factory = new GeometryFactory();
                    the_geom = new MultiPolygon(new Polygon[]{(Polygon) geometry}, factory);
                }

                if (the_geom != null) {
                    layerDetailsInfo.setAllowedArea(the_geom.toText());
                    layerDetailsInfo
                            .setSrid(String.valueOf(the_geom.getSRID()));
                } else {
                    layerDetailsInfo.setAllowedArea(null);
                    layerDetailsInfo.setSrid(null);
                }

                if (layerDetails.getType().equals(LayerType.RASTER)) {
                    layerDetailsInfo.setType("raster");
                } else if (layerDetails.getType().equals(LayerType.LAYERGROUP)){
                    layerDetailsInfo.setType("layergroup");
                } else {
                    layerDetailsInfo.setType("vector");
                }
            }

        } catch (NotFoundServiceEx e) {
            logger.error(e.getMessage(), e);
            throw new ApplicationException(e.getMessage(), e);
        }

        return layerDetailsInfo;
    }

    public void shift(long priorityStart, long offset) {
        if (priorityStart != -1) {
            geofenceRemoteService.getRuleAdminService().shift(priorityStart,
                    offset);
        }
    }

    public void swap(long id1, long id2) {
        if ((id1 != -1) && (id2 != -1)) {
            geofenceRemoteService.getRuleAdminService().swap(id1, id2);
        }

    }

    public void findRule(RuleModel rule) throws ApplicationException, Exception {
        org.geoserver.geofence.core.model.Rule ret = null;
        ret = geofenceRemoteService.getRuleAdminService().get(rule.getId());
        // return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.server.service.IRulesManagerService#
     * saveLayerLimitsInfo(it.
     * geosolutions.geofence.gui.client.model.data.LayerLimitsInfo)
     */
    public LayerLimitsInfo saveLayerLimitsInfo(LayerLimitsInfo layerLimitsForm) {

        Long ruleId = layerLimitsForm.getRuleId();
        RuleLimits ruleLimits = null;

        try {
            ruleLimits = geofenceRemoteService.getRuleAdminService()
                    .get(ruleId).getRuleLimits();

            if (ruleLimits == null) {
                ruleLimits = new RuleLimits();
            }

            String allowedArea = layerLimitsForm.getAllowedArea();

            if (allowedArea != null) {
                MultiPolygon the_geom = null;
                WKTReader wktReader = new WKTReader();
                Geometry geometry = wktReader.read(allowedArea);
                if (geometry instanceof MultiPolygon) {
                    the_geom = (MultiPolygon) geometry;
                    the_geom.setSRID(Integer
                            .valueOf(layerLimitsForm.getSrid()).intValue());
                    ruleLimits.setAllowedArea(the_geom);
                } else if (geometry instanceof Polygon) {
                    GeometryFactory factory = new GeometryFactory();
                    the_geom = new MultiPolygon(
                            new Polygon[]{(Polygon) geometry}, factory);
                }

                if (the_geom != null) {
                    the_geom.setSRID(Integer
                            .valueOf(layerLimitsForm.getSrid()).intValue());
                    ruleLimits.setAllowedArea(the_geom);
                }
            } else {
                ruleLimits.setAllowedArea(null);
            }

            CatalogMode cm = fromClientCM(layerLimitsForm.getCatalogMode());
            ruleLimits.setCatalogMode(cm);

            geofenceRemoteService.getRuleAdminService().setLimits(ruleId,
                    ruleLimits);

        } catch (NotFoundServiceEx e) {
            logger.error(e.getMessage(), e);
            throw new ApplicationException(e.getMessage(), e);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            throw new ApplicationException(e.getMessage(), e);
        }

        return layerLimitsForm;
    }

    private static ClientCatalogMode toClientCM(CatalogMode mode) {
        ClientCatalogMode ccm = ClientCatalogMode.DEFAULT;

        if (mode != null) {
            switch (mode) {
                case CHALLENGE:
                    ccm = ClientCatalogMode.CHALLENGE;
                    break;
                case MIXED:
                    ccm = ClientCatalogMode.MIXED;
                    break;
                case HIDE:
                    ccm = ClientCatalogMode.HIDE;
                    break;
                default:
                    ccm = ClientCatalogMode.DEFAULT;
            }
        }

        return ccm;
    }

    private static CatalogMode fromClientCM(ClientCatalogMode ccm) {
        CatalogMode cm = null;
        if (ccm == null || ClientCatalogMode.NAME_DEFAULT.equals(ccm.getCatalogMode())) {
            cm = null;
        } else if (ClientCatalogMode.NAME_HIDE.equals(ccm.getCatalogMode())) {
            cm = CatalogMode.HIDE;
        } else if (ClientCatalogMode.NAME_CHALLENGE.equals(ccm.getCatalogMode())) {
            cm = CatalogMode.CHALLENGE;
        } else if (ClientCatalogMode.NAME_MIXED.equals(ccm.getCatalogMode())) {
            cm = CatalogMode.MIXED;
        } else {
            logger.warn("Unknown catalog mode " + ccm);
        }
        return cm;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.server.service.IRulesManagerService#
     * getLayerLimitsInfo(it. geosolutions.geofence.gui.client.model.Rule)
     */
    public LayerLimitsInfo getLayerLimitsInfo(RuleModel rule) {
        Long ruleId = rule.getId();
        RuleLimits ruleLimits = null;
        LayerLimitsInfo layerLimitsInfo = null;

        try {
            ruleLimits = geofenceRemoteService.getRuleAdminService()
                    .get(ruleId).getRuleLimits();

            if (ruleLimits != null) {
                layerLimitsInfo = new LayerLimitsInfo();
                layerLimitsInfo.setRuleId(ruleId);

                MultiPolygon the_geom = ruleLimits.getAllowedArea();

                if (the_geom != null) {
                    layerLimitsInfo.setAllowedArea(the_geom.toText());
                    layerLimitsInfo.setSrid(String.valueOf(the_geom.getSRID()));
                } else {
                    layerLimitsInfo.setAllowedArea(null);
                    layerLimitsInfo.setSrid(null);
                }

                ClientCatalogMode ccm = toClientCM(ruleLimits.getCatalogMode());
                layerLimitsInfo.setCatalogMode(ccm);
            }
        } catch (NotFoundServiceEx e) {
            logger.error(e.getMessage(), e);
            throw new ApplicationException(e.getMessage(), e);
        }

        return layerLimitsInfo;
    }
}
