/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class GeofenceWidgetsData.
 */
public class GeofenceWidgetsData {

    /**
     * Gets the send types.
     * 
     * @return the send types
     */
    public static List<SendType> getSendTypes() {
        List<SendType> sendTypes = new ArrayList<SendType>();

        sendTypes.add(new SendType("Email"));
        sendTypes.add(new SendType("RSS"));
        // sendTypes.add(new SendType("Both"));
        sendTypes.add(new SendType("EMailAndRSS"));

        return sendTypes;
    }

    /**
     * Gets the times.
     * 
     * @return the times
     */
    public static List<UpdateInterval> getTimes() {
        List<UpdateInterval> times = new ArrayList<UpdateInterval>();

        times.add(new UpdateInterval("1h"));
        times.add(new UpdateInterval("4h"));
        times.add(new UpdateInterval("24h"));

        return times;
    }

    /**
     * Gets the distrib times.
     * 
     * @return the distrib times
     */
    public static List<DistribUpdateInterval> getDistribTimes() {
        List<DistribUpdateInterval> times = new ArrayList<DistribUpdateInterval>();

        times.add(new DistribUpdateInterval("Manual"));
        times.add(new DistribUpdateInterval("Ongoing"));

        return times;
    }

    /**
     * Gets the retrieval type.
     * 
     * @return the retrieval type
     */
    public static List<RetrievalType> getRetrievalType() {
        List<RetrievalType> retrievalType = new ArrayList<RetrievalType>();

        retrievalType.add(new RetrievalType("Incremental"));
        retrievalType.add(new RetrievalType("Cumulative"));

        return retrievalType;
    }

    /**
     * Gets the content types.
     * 
     * @return the content types
     */
    public static List<ContentType> getContentTypes() {
        List<ContentType> types = new ArrayList<ContentType>();

        types.add(new ContentType("Metadata"));
        types.add(new ContentType("MetadataAndGeometry"));
        types.add(new ContentType("BareNotification"));

        return types;
    }

    /**
     * Gets the distrib content types.
     * 
     * @return the distrib content types
     */
    public static List<DistribContentType> getDistribContentTypes() {
        List<DistribContentType> types = new ArrayList<DistribContentType>();

        types.add(new DistribContentType("Metadata"));
        types.add(new DistribContentType("MetadataAndContent"));

        return types;
    }

    /**
     * Gets the val filter op.
     * 
     * @return the val filter op
     */
    public static List<FilterType> getValFilterOp() {
        List<FilterType> filterTypes = new ArrayList<FilterType>();

        filterTypes.add(new FilterType("isEqualTo"));
        filterTypes.add(new FilterType("isNotEqualTo"));
        filterTypes.add(new FilterType("isGreaterThan"));
        filterTypes.add(new FilterType("isLessThan"));
        filterTypes.add(new FilterType("isGreaterOrEqualTo"));
        filterTypes.add(new FilterType("isLessOrEqualTo"));

        return filterTypes;
    }

    /**
     * Gets the text filter op.
     * 
     * @return the text filter op
     */
    public static List<FilterType> getTextFilterOp() {
        List<FilterType> filterTypes = new ArrayList<FilterType>();

        filterTypes.add(new FilterType("isEqualTo"));
        filterTypes.add(new FilterType("like"));

        return filterTypes;
    }

}
