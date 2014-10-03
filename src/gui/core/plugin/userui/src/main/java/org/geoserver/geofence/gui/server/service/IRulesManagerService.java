/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.service;

import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadResult;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.Rule;
import org.geoserver.geofence.gui.client.model.data.LayerAttribUI;
import org.geoserver.geofence.gui.client.model.data.LayerCustomProps;
import org.geoserver.geofence.gui.client.model.data.LayerDetailsInfo;
import org.geoserver.geofence.gui.client.model.data.LayerLimitsInfo;
import org.geoserver.geofence.gui.client.model.data.LayerStyle;


// TODO: Auto-generated Javadoc
/**
 * The Interface IRulesManagerService.
 */
public interface IRulesManagerService
{

    /**
     * Gets the rules.
     *
     * @param config
     *            the config
     * @param full
     *            the full
     * @return the rules
     * @throws ApplicationException
     *             the application exception
     */
    public PagingLoadResult<Rule> getRules(int offset, int limit, boolean full) throws ApplicationException;

    /**
     * Save all rules.
     *
     * @param rules
     *            the rules
     * @throws ApplicationException
     *             the application exception
     */
    public void saveAllRules(List<Rule> rules) throws ApplicationException;

    /**
     * Gets the layer custom props.
     *
     * @param config
     *            the config
     * @param rule
     *            the rule
     * @return the layer custom props
     */
//    public PagingLoadResult<LayerCustomProps> getLayerCustomProps(int offset, int limit, Rule rule);

    /**
     * Sets the details props.
     *
     * @param ruleId
     *            the rule id
     * @param customProps
     *            the custom props
     */
    public void setDetailsProps(Long ruleId, List<LayerCustomProps> customProps);


    /**
     * Gets the layer attributes.
     *
     * @param rule
     *            the rule
     * @return the layer attributes
     */
    public List<LayerAttribUI> getLayerAttributes(Rule rule);

    /**
     * @param ruleId
     * @param layerAttributes
     */
    public void setLayerAttributes(Long ruleId, List<LayerAttribUI> layerAttributes);

    /**
     * @param layerDetailsForm
     * @return LayerDetailsForm
     */
    public LayerDetailsInfo saveLayerDetailsInfo(LayerDetailsInfo layerDetailsForm, List<LayerStyle> layerStyles);

    /**
     * @param rule
     * @return LayerDetailsForm
     */
    public LayerDetailsInfo getLayerDetailsInfo(Rule rule);

    /**
     * Save single rule
     *
     * @param rule
     */
    public void saveRule(Rule rule) throws ApplicationException;

    /**
     * Save single rule
     *
     * @param rule
     * @return
     * @throws ResourceNotFoundFault
     */
    public void findRule(Rule rule) throws ApplicationException, Exception;

    /**
     * Delete single rule
     *
     * @param rule
     */
    public void deleteRule(Rule rule) throws ApplicationException;

    /**
     * Shifts the priority of the rules having <TT>priority &gt;= priorityStart</TT>
     * down by <TT>offset</TT>.
     *
     * @return the number of rules updated.
     */
    public void shift(long priorityStart, long offset);

    /**
     * Swaps the priorities of two rules.
     */
    public void swap(long id1, long id2);

    /**
     * @param layerLimitsForm
     * @return LayerLimitsInfo
     */
    public LayerLimitsInfo saveLayerLimitsInfo(LayerLimitsInfo layerLimitsForm);

    /**
     * @param rule
     * @return LayerLimitsInfo
     */
    public LayerLimitsInfo getLayerLimitsInfo(Rule rule);

}
