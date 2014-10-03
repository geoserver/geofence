/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.service.impl;

import org.geoserver.geofence.core.model.LayerAttribute;
import org.geoserver.geofence.core.model.LayerDetails;
import org.geoserver.geofence.core.model.RuleLimits;
import org.geoserver.geofence.core.model.enums.AccessType;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.core.model.enums.LayerType;
import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.GSInstance;
import org.geoserver.geofence.gui.client.model.GSUser;
import org.geoserver.geofence.gui.client.model.Rule;
import org.geoserver.geofence.gui.client.model.UserGroup;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.geoserver.geofence.core.model.IPAddressRange;
import org.geoserver.geofence.core.model.enums.CatalogMode;
import org.geoserver.geofence.gui.client.model.data.ClientCatalogMode;
import org.geoserver.geofence.services.util.IPUtils;
import org.springframework.dao.DuplicateKeyException;

/**
 * The Class RulesManagerServiceImpl.
 */
@Component("rulesManagerServiceGWT")
public class RulesManagerServiceImpl implements IRulesManagerService {

	/** The logger. */
	private final static Logger logger = LoggerFactory.getLogger(RulesManagerServiceImpl.class);

	/** The geofence remote service. */
	@Autowired
	private GeofenceRemoteService geofenceRemoteService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.geoserver.geofence.gui.server.service.IFeatureService#loadFeature
	 * (com.extjs.gxt.ui. client.data.PagingLoadConfig, java.lang.String)
	 */
	public PagingLoadResult<Rule> getRules(int offset, int limit, boolean full)
			throws ApplicationException {
		int start = offset;

		List<Rule> ruleListDTO = new ArrayList<Rule>();

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

			org.geoserver.geofence.core.model.Rule fullRule;
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

			Rule ruleDTO = new Rule();

			ruleDTO.setId(shortRule.getId());
			ruleDTO.setPriority(fullRule.getPriority());

			if (fullRule.getGsuser() == null) {
				GSUser all = new GSUser();
				all.setId(-1);
				all.setName("*");
				ruleDTO.setUser(all);
			} else {
				org.geoserver.geofence.core.model.GSUser remote_user = fullRule
						.getGsuser();
				GSUser local_user = new GSUser();
				local_user.setId(remote_user.getId());
				local_user.setName(remote_user.getName());
				ruleDTO.setUser(local_user);
			}

			if (fullRule.getUserGroup() == null) {
				UserGroup all = new UserGroup();
				all.setId(-1);
				all.setName("*");
				ruleDTO.setProfile(all);
			} else {
				org.geoserver.geofence.core.model.UserGroup remote_profile = fullRule
						.getUserGroup();
				UserGroup local_profile = new UserGroup();
				local_profile.setId(remote_profile.getId());
				local_profile.setName(remote_profile.getName());
				ruleDTO.setProfile(local_profile);
			}

			if (fullRule.getInstance() == null) {
				GSInstance all = new GSInstance();
				all.setId(-1);
				all.setName("*");
				all.setBaseURL("*");
			} else {
				org.geoserver.geofence.core.model.GSInstance remote_instance = fullRule
						.getInstance();
				GSInstance local_instance = new GSInstance();
				local_instance.setId(remote_instance.getId());
				local_instance.setName(remote_instance.getName());
				local_instance.setBaseURL(remote_instance.getBaseURL());
				local_instance.setUsername(remote_instance.getUsername());
				local_instance.setPassword(remote_instance.getPassword());
				ruleDTO.setInstance(local_instance);
			}

            ruleDTO.setSourceIPRange(fullRule.getAddressRange() != null?
                    fullRule.getAddressRange().getCidrSignature() : "*");

			ruleDTO.setService((fullRule.getService() != null) ?
                    fullRule.getService() : "*");

            ruleDTO.setRequest((fullRule.getRequest() != null) ?
                    fullRule.getRequest() : "*");

            ruleDTO.setWorkspace((fullRule.getWorkspace() != null) ?
                    fullRule.getWorkspace() : "*");

			ruleDTO.setLayer((fullRule.getLayer() != null) ?
                    fullRule.getLayer() : "*");

			ruleDTO.setGrant((fullRule.getAccess() != null) ?
                    fullRule.getAccess().toString() : "ALLOW");

			ruleListDTO.add(ruleDTO);
		}

		return new RpcPageLoadResult<Rule>(ruleListDTO, offset, t.intValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.geoserver.geofence.gui.server.service.IRulesManagerService#saveAllRules
	 * (java.util.List)
	 */
	public void saveRule(Rule rule) throws ApplicationException {

        if(logger.isDebugEnabled())
            logger.debug("Trying to save rule " + rule);

        IPAddressRange addressRange = validateSourceRange(rule.getSourceIPRange());

        org.geoserver.geofence.core.model.Rule modelRule = new org.geoserver.geofence.core.model.Rule(
                rule.getPriority(), getUser(rule.getUser()),
                getProfile(rule.getProfile()),
                getInstance(rule.getInstance()),
                addressRange,
                "*".equals(rule.getService()) ? null : rule.getService(),
                "*".equals(rule.getRequest()) ? null : rule.getRequest(),
                "*".equals(rule.getWorkspace()) ? null : rule.getWorkspace(),
                "*".equals(rule.getLayer()) ? null : rule.getLayer(),
                getAccessType(rule.getGrant()));

        try {
            if (rule.getId() == -1) { // REQUEST FOR INSERT
                modelRule.setId(null);
                    geofenceRemoteService.getRuleAdminService().insert(modelRule);
            } else {                  // REQUEST FOR UPDATE
                long ruleId = rule.getId();
                modelRule.setId(ruleId);
                try {
                    geofenceRemoteService.getRuleAdminService().update(modelRule);
                    org.geoserver.geofence.core.model.Rule loaded = geofenceRemoteService.getRuleAdminService().get(ruleId);
                    if (! loaded.getAccess().name().equalsIgnoreCase(rule.getGrant())) {
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
        if(srcIP != null && ! "*".equals(srcIP) ) {
            if ( ! IPUtils.isRangeValid(srcIP) ) {
                logger.error("Invalid IP range '"+srcIP+"'");
                throw new ApplicationException("Invalid IP range '"+srcIP+"'");
            }
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
	public void deleteRule(Rule rule) throws ApplicationException {

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
	public void updatePriorities(Rule rule, long shift) {
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
	public void saveAllRules(List<Rule> rules) throws ApplicationException {
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

		for (Rule localRule : rules) {

            IPAddressRange addressRange = validateSourceRange(localRule.getSourceIPRange());

			org.geoserver.geofence.core.model.Rule rule = new org.geoserver.geofence.core.model.Rule(
					localRule.getPriority(),
                    getUser(localRule.getUser()),
					getProfile(localRule.getProfile()),
					getInstance(localRule.getInstance()),
                    addressRange,
                    "*".equals(localRule.getService()) ?   null : localRule.getService(),
					"*".equals(localRule.getRequest()) ?   null : localRule.getRequest(),
					"*".equals(localRule.getWorkspace()) ? null : localRule.getWorkspace(),
					"*".equals(localRule.getLayer()) ?     null : localRule.getLayer(),
                    getAccessType(localRule.getGrant()));
			geofenceRemoteService.getRuleAdminService().insert(rule);
		}
	}

	/**
	 * Gets the access type.
	 * 
	 * @param grant
	 *            the grant
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
	 * @param instance
	 *            the instance
	 * @return single instance of RulesManagerServiceImpl
	 */
	private org.geoserver.geofence.core.model.GSInstance getInstance(
			GSInstance instance) {
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

	/**
	 * Gets the profile.
	 * 
	 * @param profile
	 *            the profile
	 * @return the profile
	 */
	private org.geoserver.geofence.core.model.UserGroup getProfile(
			UserGroup profile) {
		org.geoserver.geofence.core.model.UserGroup remote_profile = null;
		try {
			if ((profile != null) && (profile.getId() != -1)) {
				remote_profile = geofenceRemoteService
						.getUserGroupAdminService().get(profile.getId());
			}
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}

		return remote_profile;
	}

	/**
	 * Gets the profile.
	 * 
	 * @param profile
	 *            the profile
	 * @return the profile
	 */
	private org.geoserver.geofence.core.model.GSUser getUser(GSUser user) {
		org.geoserver.geofence.core.model.GSUser remote_user = null;
		try {
			if ((user != null) && (user.getId() != -1)) {
				remote_user = geofenceRemoteService.getUserAdminService().get(
						user.getId());
			}
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}

		return remote_user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geoserver.geofence.gui.server.service.IRulesManagerService#
	 * getLayerCustomProps(com.extjs .gxt.ui.client.data.PagingLoadConfig,
	 * org.geoserver.geofence.gui.client.model.Rule)
	 */
//	public PagingLoadResult<LayerCustomProps> getLayerCustomProps(int offset,
//			int limit, Rule rule) {
//		int start = offset;
//		Long t = new Long(0);
//
//		List<LayerCustomProps> customPropsDTO = new ArrayList<LayerCustomProps>();
//
//		if ((rule != null) && (rule.getId() >= 0)) {
//			try {
//				Map<String, String> customProperties = geofenceRemoteService
//						.getRuleAdminService().getDetailsProps(rule.getId());
//
//				if (customProperties == null) {
//					if (logger.isErrorEnabled()) {
//						logger.error("No property found on server");
//					}
//					throw new ApplicationException("No rule found on server");
//				}
//
//				long rulesCount = customProperties.size();
//
//				t = new Long(rulesCount);
//
//				int page = (start == 0) ? start : (start / limit);
//
//				SortedSet<String> sortedset = new TreeSet<String>(
//						customProperties.keySet());
//				Iterator<String> it = sortedset.iterator();
//
//				while (it.hasNext()) {
//					String key = it.next();
//					LayerCustomProps property = new LayerCustomProps();
//					property.setPropKey(key);
//					property.setPropValue(customProperties.get(key));
//					customPropsDTO.add(property);
//				}
//
//				// for (String key : customProperties.keySet()) {
//				//
//				// LayerCustomProps property = new LayerCustomProps();
//				// property.setPropKey(key);
//				// property.setPropValue(customProperties.get(key));
//				// customPropsDTO.add(property);
//				// }
//			} catch (Exception e) {
//				// do nothing!
//			}
//		}
//
//		return new RpcPageLoadResult<LayerCustomProps>(customPropsDTO, offset,
//				t.intValue());
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geoserver.geofence.gui.server.service.IRulesManagerService#
	 * setDetailsProps(java.lang .Long,
	 * org.geoserver.geofence.gui.client.model.data.LayerCustomProps)
	 */
	public void setDetailsProps(Long ruleId, List<LayerCustomProps> customProps) {

        logger.error("TODO: rule refactoring!!! custom props have been removed");

		Map<String, String> props = new HashMap<String, String>();

		for (LayerCustomProps prop : customProps) {
			props.put(prop.getPropKey(), prop.getPropValue());
		}

		LayerDetails details = null;
		try {
			details = geofenceRemoteService.getRuleAdminService().get(ruleId)
					.getLayerDetails();

			if (details == null) {
				details = new LayerDetails();

				org.geoserver.geofence.core.model.Rule rule = geofenceRemoteService
						.getRuleAdminService().get(ruleId);
				org.geoserver.geofence.core.model.GSInstance gsInstance = rule
						.getInstance();
				GeoServerRESTReader gsreader = new GeoServerRESTReader(
						gsInstance.getBaseURL(), gsInstance.getUsername(),
						gsInstance.getPassword());

				if ((rule.getWorkspace() == null)
						&& !rule.getLayer().equalsIgnoreCase("*")) {
					// RESTLayerGroup group =
					// gsreader.getLayerGroup(rule.getLayer());
					details.setType(LayerType.LAYERGROUP);
				} else {
					RESTLayer layer = gsreader.getLayer(rule.getLayer());

					if (layer.getType().equals(RESTLayer.TYPE.VECTOR)) {
						details.setType(LayerType.VECTOR);
					} else {
						details.setType(LayerType.RASTER);
					}
				}

                // REMOVED BY ETj
//				details.setCustomProps(props); 
//				geofenceRemoteService.getRuleAdminService().setDetails(ruleId,
//						details);
			} else {
//				geofenceRemoteService.getRuleAdminService().setDetailsProps(
//						ruleId, props);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geoserver.geofence.gui.server.service.IRulesManagerService#
	 * getLayerAttributes(com.extjs .gxt.ui.client.data.PagingLoadConfig,
	 * org.geoserver.geofence.gui.client.model.Rule)
	 */
	public List<LayerAttribUI> getLayerAttributes(Rule rule) {

		LayerDetails layerDetails = null;
		List<LayerAttribUI> layerAttributesDTO = new ArrayList<LayerAttribUI>();

		try {

			layerDetails = geofenceRemoteService.getRuleAdminService()
					.get(rule.getId()).getLayerDetails();

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
								String attrName = layerAttributesDTO.get(i)
										.getName();
								if (layerAttribute.getName().equalsIgnoreCase(
										attrName)) {
									LayerAttribUI layAttrUI = new LayerAttribUI();
									layAttrUI.setName(layerAttribute.getName());
									layAttrUI.setDataType(layerAttribute
											.getDatatype());
									layAttrUI.setAccessType(layerAttribute
											.getAccess().toString());

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
	private List<LayerAttribUI> loadAttribute(Rule rule) {
		List<LayerAttribUI> layerAttributesDTO = new ArrayList<LayerAttribUI>();
		Set<LayerAttribute> layerAttributes = null;

		GSInstance gsInstance = rule.getInstance();
		GeoServerRESTReader gsreader;

		try {
			gsreader = new GeoServerRESTReader(gsInstance.getBaseURL(),
					gsInstance.getUsername(), gsInstance.getPassword());

			RESTLayer layer = gsreader.getLayer(rule.getLayer());

			if (layer.getType().equals(RESTLayer.TYPE.VECTOR)) {
				layerAttributes = new HashSet<LayerAttribute>();

				// ///////////////////////
				// Vector Layer
				// ///////////////////////

				RESTFeatureType featureType = gsreader.getFeatureType(layer);

				for (RESTFeatureType.Attribute attribute : featureType
						.getAttributes()) {
					LayerAttribute attr = new LayerAttribute();
					attr.setName(attribute.getName());
					attr.setDatatype(attribute.getBinding());

					layerAttributes.add(attr);
				}

				layerAttributesDTO = new ArrayList<LayerAttribUI>();

				Iterator<LayerAttribute> iterator = layerAttributes.iterator();

				while (iterator.hasNext()) {
					LayerAttribute layerAttribute = iterator.next();

					LayerAttribUI layAttrUI = new LayerAttribUI();
					layAttrUI.setName(layerAttribute.getName());
					layAttrUI.setDataType(layerAttribute.getDatatype());

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

            org.geoserver.geofence.core.model.Rule rule = geofenceRemoteService
                    .getRuleAdminService().get(ruleId);
            org.geoserver.geofence.core.model.GSInstance gsInstance = rule
                    .getInstance();
            GeoServerRESTReader gsreader = new GeoServerRESTReader(
                    gsInstance.getBaseURL(), gsInstance.getUsername(),
                    gsInstance.getPassword());
            RESTLayer layer = gsreader.getLayer(rule.getLayer());

            if(layer != null) {
                if (layer.getType().equals(RESTLayer.TYPE.VECTOR)) {
                    details.setType(LayerType.VECTOR);
                } else {
                    details.setType(LayerType.RASTER);
                }
            } else {
                // error encountered while loading data from GeoServer
                if(oldDetails == null) {
                    logger.error("Error loading layer info from GeoServer");
                    throw new ApplicationException("Error loading layer info from GeoServer");
                } else {
                    logger.warn("Error reloading layer info from GeoServer");
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
							new Polygon[] { (Polygon) geometry }, factory);
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
	public LayerDetailsInfo getLayerDetailsInfo(Rule rule) {
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
					the_geom = new MultiPolygon(new Polygon[] {(Polygon) geometry}, factory);
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

	public void findRule(Rule rule) throws ApplicationException, Exception {
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
							new Polygon[] { (Polygon) geometry }, factory);
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

        if(mode != null ) {
            switch(mode) {
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
        if(ccm == null || ClientCatalogMode.NAME_DEFAULT.equals(ccm.getCatalogMode()))
            cm = null;
        else if (ClientCatalogMode.NAME_HIDE.equals(ccm.getCatalogMode()))
            cm = CatalogMode.HIDE;
        else if (ClientCatalogMode.NAME_CHALLENGE.equals(ccm.getCatalogMode()))
            cm = CatalogMode.CHALLENGE;
        else if (ClientCatalogMode.NAME_MIXED.equals(ccm.getCatalogMode()))
            cm = CatalogMode.MIXED;
        else
            logger.warn("Unknown catalog mode " + ccm);
        return cm;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geoserver.geofence.gui.server.service.IRulesManagerService#
	 * getLayerLimitsInfo(it. geosolutions.geofence.gui.client.model.Rule)
	 */
	public LayerLimitsInfo getLayerLimitsInfo(Rule rule) {
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
