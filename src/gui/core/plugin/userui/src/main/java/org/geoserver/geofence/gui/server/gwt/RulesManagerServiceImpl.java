/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.gwt;

import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.Rule;
import org.geoserver.geofence.gui.client.model.data.LayerAttribUI;
import org.geoserver.geofence.gui.client.model.data.LayerCustomProps;
import org.geoserver.geofence.gui.client.model.data.LayerDetailsInfo;
import org.geoserver.geofence.gui.client.model.data.LayerLimitsInfo;
import org.geoserver.geofence.gui.client.model.data.LayerStyle;
import org.geoserver.geofence.gui.client.service.RulesManagerRemoteService;
import org.geoserver.geofence.gui.server.service.IRulesManagerService;
import org.geoserver.geofence.gui.spring.ApplicationContextUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: Auto-generated Javadoc
/**
 * The Class RulesManagerServiceImpl.
 */
public class RulesManagerServiceImpl extends RemoteServiceServlet implements RulesManagerRemoteService
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5342510982782032063L;

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The rules manager service. */
    private IRulesManagerService rulesManagerService;

    /**
     * Instantiates a new rules manager service impl.
     */
    public RulesManagerServiceImpl()
    {
        this.rulesManagerService = (IRulesManagerService) ApplicationContextUtil.getInstance().getBean("rulesManagerServiceGWT");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteService#getRules(com.extjs
     * .gxt.ui.client.data.PagingLoadConfig)
     */
    public PagingLoadResult<Rule> getRules(int offset, int limit, boolean full) throws ApplicationException
    {
        PagingLoadResult<Rule> ret = rulesManagerService.getRules(offset, limit, full);

        return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.client.service.RulesManagerRemoteService#saveAllRules(java.util
     * .List)
     */
    public void saveRule(Rule rule) throws ApplicationException
    {
        rulesManagerService.saveRule(rule);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.client.service.RulesManagerRemoteService#saveAllRules(java.util
     * .List)
     */
    public void deleteRule(Rule rule) throws ApplicationException
    {
        rulesManagerService.deleteRule(rule);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.client.service.RulesManagerRemoteService#saveAllRules(java.util
     * .List)
     */
    public void saveAllRules(List<Rule> rules) throws ApplicationException
    {
        rulesManagerService.saveAllRules(rules);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.client.service.RulesManagerRemoteService#getLayerCustomProps(
     * com.extjs.gxt.ui.client.data.PagingLoadConfig, org.geoserver.geofence.gui.client.model.Rule)
     */
//    public PagingLoadResult<LayerCustomProps> getLayerCustomProps(int offset, int limit, Rule rule)
//        throws ApplicationException
//    {
//        return rulesManagerService.getLayerCustomProps(offset, limit, rule);
//    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.client.service.RulesManagerRemoteService#setDetailsProps(java
     * .lang.Long, org.geoserver.geofence.gui.client.model.data.LayerCustomProps)
     */
    public void setDetailsProps(Long ruleId, List<LayerCustomProps> customProps) throws ApplicationException
    {
        rulesManagerService.setDetailsProps(ruleId, customProps);
    }

    public void shift(long priorityStart, long offset) throws ApplicationException
    {
        rulesManagerService.shift(priorityStart, offset);
    }

    public void swap(long id1, long id2) throws ApplicationException
    {
        rulesManagerService.swap(id1, id2);
    }

    public void findRule(Rule rule) throws ApplicationException, Exception
    {
        rulesManagerService.findRule(rule);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.client.service.RulesManagerRemoteService#setLayerAttributes(java
     * .lang.Long, java.util.List)
     */
    public void setLayerAttributes(Long ruleId, List<LayerAttribUI> layerAttributes) throws ApplicationException
    {
        rulesManagerService.setLayerAttributes(ruleId, layerAttributes);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.client.service.RulesManagerRemoteService#saveLayerDetails(it.
     * geosolutions.geofence.gui.client.model.data.LayerDetailsForm)
     */
    public LayerDetailsInfo saveLayerDetailsInfo(LayerDetailsInfo layerDetailsForm, List<LayerStyle> layerStyles)
        throws ApplicationException
    {
        return rulesManagerService.saveLayerDetailsInfo(layerDetailsForm, layerStyles);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.client.service.RulesManagerRemoteService#getLayerDetailsInfo(
     * org.geoserver.geofence.gui.client.model.Rule)
     */
    public LayerDetailsInfo getLayerDetailsInfo(Rule rule) throws ApplicationException
    {
        return rulesManagerService.getLayerDetailsInfo(rule);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.client.service.RulesManagerRemoteService#getLayerAttributes(com
     * .extjs.gxt.ui.client.data.PagingLoadConfig, org.geoserver.geofence.gui.client.model.Rule)
     */
    public List<LayerAttribUI> getLayerAttributes(Rule rule) throws ApplicationException
    {
        return rulesManagerService.getLayerAttributes(rule);
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.service.RulesManagerRemoteService#saveLayerLimitsInfo(org.geoserver.geofence.gui.client.model.data.LayerLimitsInfo)
     */
    public LayerLimitsInfo saveLayerLimitsInfo(LayerLimitsInfo layerLimitsForm) throws ApplicationException
    {
        return rulesManagerService.saveLayerLimitsInfo(layerLimitsForm);
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.service.RulesManagerRemoteService#getLayerLimitsInfo(org.geoserver.geofence.gui.client.model.Rule)
     */
    public LayerLimitsInfo getLayerLimitsInfo(Rule rule) throws ApplicationException
    {
        return rulesManagerService.getLayerLimitsInfo(rule);
    }
}
