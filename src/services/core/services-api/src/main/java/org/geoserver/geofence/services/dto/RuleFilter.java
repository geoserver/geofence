/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.dto;

import org.geoserver.geofence.core.model.Rule;

import java.io.Serializable;


/**
 * A Filter for selecting {@link Rule}s. <P> For every given field, you may choose to select <LI>a given value</LI> <LI>any values
 * (no filtering)</LI> <LI>only default rules (null value in a field) </LI> </UL>
 *
 * For users, groups, instances (i.e., classes represented by DB entities) you may specify either the ID or the name.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RuleFilter implements Serializable {

    private static final long serialVersionUID = 5629211135629700041L;

    public enum FilterType {

        NAMEVALUE,
        IDVALUE,
        ANY,
        DEFAULT;
    }

    public enum SpecialFilterType {

        ANY(FilterType.ANY),
        DEFAULT(FilterType.DEFAULT);
        private FilterType relatedType;

        private SpecialFilterType(FilterType relatedType) {
            this.relatedType = relatedType;
        }

        public FilterType getRelatedType() {
            return relatedType;
        }
    }
    private final IdNameFilter user;
    private final IdNameFilter userGroup;
    private final IdNameFilter instance;
    private final TextFilter sourceAddress;
    private final TextFilter service;
    private final TextFilter request;
    private final TextFilter workspace;
    private final TextFilter layer;

    public RuleFilter() {
        this(SpecialFilterType.DEFAULT);
    }

    /**
     * Creates a RuleFilter by setting all fields filtering either to ANY or DEFAULT. <BR>
     * If no other field is set, you will get <UL>
     * <LI>with <B>ANY</B>, all Rules will be returned</LI>
     * <LI>with <B>DEFAULT</B>, only the default Rule will be returned</LI>
     * </UL>
     */
    public RuleFilter(SpecialFilterType type) {
        FilterType ft = type.getRelatedType();

        user = new IdNameFilter(ft);
        userGroup = new IdNameFilter(ft);
        instance = new IdNameFilter(ft);
        sourceAddress = new TextFilter(ft);
        service = new TextFilter(ft, true);
        request = new TextFilter(ft, true);
        workspace = new TextFilter(ft);
        layer = new TextFilter(ft);
    }

    public RuleFilter(SpecialFilterType type, boolean includeDefault) {
        FilterType ft = type.getRelatedType();

        user = new IdNameFilter(ft, includeDefault);
        userGroup = new IdNameFilter(ft, includeDefault);
        instance = new IdNameFilter(ft, includeDefault);
        sourceAddress = new TextFilter(ft);
        sourceAddress.setIncludeDefault(includeDefault);
        service = new TextFilter(ft, true);
        service.setIncludeDefault(includeDefault);
        request = new TextFilter(ft, true);
        request.setIncludeDefault(includeDefault);
        workspace = new TextFilter(ft);
        workspace.setIncludeDefault(includeDefault);
        layer = new TextFilter(ft);
        layer.setIncludeDefault(includeDefault);
    }

    /**
     * Creates a RuleFilter by heuristically converting special string values into Fitler behaviour:<UL> <LI>a null value will
     * match only with null</LI> <LI>a '*' value will match everything (no filter condition on that given field)</LI> <LI>any
     * other string will match literally</LI> </UL>
     *
     * @deprecated Please use plain setters if you want to handle by hand special values or filter conditions.
     */
    public RuleFilter(String userName, String groupName, String instanceName,
            String sourceAddress,
            String service, String request,
            String workspace, String layer) {
        this(SpecialFilterType.DEFAULT);


        this.user.setHeuristically(userName);
        this.userGroup.setHeuristically(groupName);
        this.instance.setHeuristically(instanceName);
        this.sourceAddress.setHeuristically(sourceAddress);

        this.service.setHeuristically(service);
        this.request.setHeuristically(request);
        this.workspace.setHeuristically(workspace);
        this.layer.setHeuristically(layer);
    }

    /**
     * Creates a RuleFilter by heuristically converting special string values into Fitler behaviour:<UL> <LI>a null value will
     * match only with null</LI> <LI>a '*' value will match everything (no filter condition on that given field)</LI> <LI>any
     * other string will match literally</LI> </UL>
     *
     * @deprecated Please use plain setters if you want to handle by hand special values or filter conditions.
     */
    public RuleFilter(Long userId, Long groupId, Long instanceId,
            String sourceAddress,
            String service, String request, String workspace,
            String layer) {
        this(SpecialFilterType.DEFAULT);

        this.user.setHeuristically(userId);
        this.userGroup.setHeuristically(groupId);
        this.instance.setHeuristically(instanceId);
        this.sourceAddress.setHeuristically(sourceAddress);

        this.service.setHeuristically(service);
        this.request.setHeuristically(request);
        this.workspace.setHeuristically(workspace);
        this.layer.setHeuristically(layer);
    }

    public RuleFilter setUser(Long id) {
        if(id == null)
            throw new NullPointerException();
        user.setId(id);
        return this;
    }

    public RuleFilter setUser(String name) {
        if(name == null)
            throw new NullPointerException();
        user.setName(name);
        return this;
    }

    public RuleFilter setUser(SpecialFilterType type) {
        if(type == null)
            throw new NullPointerException();
        user.setType(type);
        return this;
    }

    public RuleFilter setUserGroup(Long id) {
        userGroup.setId(id);
        return this;
    }

    public RuleFilter setUserGroup(String name) {
        userGroup.setName(name);
        return this;
    }

    public RuleFilter setUserGroup(SpecialFilterType type) {
        userGroup.setType(type);
        return this;
    }

    public RuleFilter setInstance(Long id) {
        instance.setId(id);
        return this;
    }

    public RuleFilter setInstance(String name) {
        instance.setName(name);
        return this;
    }

    public RuleFilter setInstance(SpecialFilterType type) {
        instance.setType(type);
        return this;
    }

    public RuleFilter setSourceAddress(String dotted) {
        sourceAddress.setText(dotted);
        return this;
    }

    public RuleFilter setSourceAddress(SpecialFilterType type) {
        sourceAddress.setType(type);
        return this;
    }

    public RuleFilter setService(String name) {
        service.setText(name);
        return this;
    }

    public RuleFilter setService(SpecialFilterType type) {
        service.setType(type);
        return this;
    }

    public RuleFilter setRequest(String name) {
        request.setText(name);
        return this;
    }

    public RuleFilter setRequest(SpecialFilterType type) {
        request.setType(type);
        return this;
    }

    public RuleFilter setWorkspace(String name) {
        workspace.setText(name);
        return this;
    }

    public RuleFilter setWorkspace(SpecialFilterType type) {
        workspace.setType(type);
        return this;
    }

    public RuleFilter setLayer(String name) {
        layer.setText(name);
        return this;
    }

    public RuleFilter setLayer(SpecialFilterType type) {
        layer.setType(type);
        return this;
    }

    public IdNameFilter getInstance() {
        return instance;
    }

    public TextFilter getSourceAddress() {
        return sourceAddress;
    }

    public TextFilter getLayer() {
        return layer;
    }

    public IdNameFilter getUserGroup() {
        return userGroup;
    }

    public TextFilter getRequest() {
        return request;
    }

    public TextFilter getService() {
        return service;
    }

    public IdNameFilter getUser() {
        return user;
    }

    public TextFilter getWorkspace() {
        return workspace;
    }



//    public InetAddress getSourceAddress() {
//        return sourceAddress;
//    }
//
//    public RuleFilter setSourceAddress(InetAddress sourceAddress) {
//        this.sourceAddress = sourceAddress;
//        return this;
//    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RuleFilter other = (RuleFilter) obj;
        if (this.user != other.user && (this.user == null || !this.user.equals(other.user))) {
            return false;
        }
        if (this.userGroup != other.userGroup && (this.userGroup == null || !this.userGroup.equals(other.userGroup))) {
            return false;
        }
        if (this.instance != other.instance && (this.instance == null || !this.instance.equals(other.instance))) {
            return false;
        }
        if (this.service != other.service && (this.service == null || !this.service.equals(other.service))) {
            return false;
        }
        if (this.request != other.request && (this.request == null || !this.request.equals(other.request))) {
            return false;
        }
        if (this.workspace != other.workspace && (this.workspace == null || !this.workspace.equals(other.workspace))) {
            return false;
        }
        if (this.layer != other.layer && (this.layer == null || !this.layer.equals(other.layer))) {
            return false;
        }
        //NOTE: ipaddress not in equals() bc it is not used for caching
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.user != null ? this.user.hashCode() : 0);
        hash = 37 * hash + (this.userGroup != null ? this.userGroup.hashCode() : 0);
        hash = 37 * hash + (this.instance != null ? this.instance.hashCode() : 0);
        hash = 37 * hash + (this.sourceAddress != null ? this.sourceAddress.hashCode() : 0);
        hash = 37 * hash + (this.service != null ? this.service.hashCode() : 0);
        hash = 37 * hash + (this.request != null ? this.request.hashCode() : 0);
        hash = 37 * hash + (this.workspace != null ? this.workspace.hashCode() : 0);
        hash = 37 * hash + (this.layer != null ? this.layer.hashCode() : 0);
        //NOTE: ipaddress not in hashcode bc it is not used for caching
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append('[');
        sb.append("user:").append(user);
        sb.append(" grp:").append(userGroup);
        sb.append(" inst:").append(instance);
        sb.append(" ip:").append(sourceAddress);
        sb.append(" serv:").append(service);
        sb.append(" req:").append(request);
        sb.append(" ws:").append(workspace);
        sb.append(" layer:").append(layer);
        sb.append(']');

        return sb.toString();
    }

    /**
     * A filter that can be either:
     *  - an id
     *  - a string
     *  - a special constraint (DEFAULT, ANY)
     */
    public static class IdNameFilter implements Serializable {

        private static final long serialVersionUID = -5984311150423659545L;
        private Long id;
        private String name;
        private FilterType type;
        /**
         * Only used in TYPE_ID and TYPE_NAME, tells if also default Rules should be matched. Old code automatically included
         * default rules.
         */
        private boolean includeDefault = true;

        public IdNameFilter(FilterType type) {
            this.type = type;
        }

        public IdNameFilter(FilterType type, boolean includeDefault) {
            this.type = type;
            this.includeDefault = includeDefault;
        }

        public IdNameFilter(long id) {
            setId(id);
        }

        public IdNameFilter(long id, boolean includeDefault) {
            setId(id);
            this.includeDefault = includeDefault;
        }

        public IdNameFilter(String name, boolean includeDefault) {
            setName(name);
            this.includeDefault = includeDefault;
        }

        public void setHeuristically(String name) {
            if ( name == null ) {
                this.type = FilterType.DEFAULT;
            } else if ( name.equals("*") ) {
                this.type = FilterType.ANY;
            } else {
                this.type = FilterType.NAMEVALUE;
                this.name = name;
            }
        }

        public void setHeuristically(Long id) {
            if ( id == null ) {
                this.type = FilterType.DEFAULT;
            } else {
                this.type = FilterType.IDVALUE;
                this.id = id;
            }
        }

        public void setId(Long id) {
            this.id = id;
            this.type = FilterType.IDVALUE;
        }

        public void setName(String name) {
            this.name = name;
            this.type = FilterType.NAMEVALUE;
        }

        public void setType(SpecialFilterType type) {
            this.type = type.getRelatedType();
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public FilterType getType() {
            return type;
        }

        public boolean isIncludeDefault() {
            return includeDefault;
        }

        public void setIncludeDefault(boolean includeDefault) {
            this.includeDefault = includeDefault;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final IdNameFilter other = (IdNameFilter) obj;
            if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
                return false;
            }
            if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
                return false;
            }
            if (this.type != other.type) {
                return false;
            }
            if (this.includeDefault != other.includeDefault) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 83 * hash + (this.id != null ? this.id.hashCode() : 0);
            hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
            hash = 83 * hash + (this.type != null ? this.type.hashCode() : 0);
            hash = 83 * hash + (this.includeDefault ? 1 : 0);
            return hash;
        }

        @Override
        public String toString() {
            switch (type) {
                case ANY:
                case DEFAULT:
                    return type.toString();

                case IDVALUE:
                    return "id" + (includeDefault?"+:":":") + (id==null?"(null)":id.toString());

                case NAMEVALUE:
                    String tmp;
                    if(name == null)
                        tmp = "(null)";
                    else if (name.isEmpty())
                        tmp = "(empty)";
                    else
                        tmp = name;
                    return "name" + (includeDefault?"+:":":") + tmp;

                default:
                    throw new AssertionError();
            }
        }
    }

    /**
     * Contains a fixed text OR a special filtering condition (i.e. ANY, DEFAULT).
     */
    public static class TextFilter implements Serializable {

        private static final long serialVersionUID = 6565336016075974626L;
        private String text;
        private FilterType type;
        private boolean forceUppercase = false;
        /**
         * Only used in TYPE_NAME, tells if also default Rules should be matched. 
         */
        private boolean includeDefault = true;

        public TextFilter(FilterType type) {
            this.type = type;
        }

        public TextFilter(FilterType type, boolean forceUppercase) {
            this.type = type;
            this.forceUppercase = forceUppercase;
        }

        public void setHeuristically(String text) {
            if ( text == null ) {
                this.type = FilterType.DEFAULT;
            } else if ( text.equals("*") ) {
                this.type = FilterType.ANY;
            } else {
                this.type = FilterType.NAMEVALUE;
                this.text = forceUppercase ? text.toUpperCase() : text;
            }
        }

        public void setText(String name) {
            this.text = forceUppercase ? name.toUpperCase() : name;
            this.type = FilterType.NAMEVALUE;
        }

        public void setType(SpecialFilterType type) {
            this.type = type.getRelatedType();
        }

        public String getText() {
            return text;
        }

        public FilterType getType() {
            return type;
        }

        public boolean isIncludeDefault() {
            return includeDefault;
        }

        public void setIncludeDefault(boolean includeDefault) {
            this.includeDefault = includeDefault;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TextFilter other = (TextFilter) obj;
            if ((this.text == null) ? (other.text != null) : !this.text.equals(other.text)) {
                return false;
            }
            if (this.type != other.type) {
                return false;
            }
            if (this.forceUppercase != other.forceUppercase) {
                return false;
            }
            if (this.includeDefault != other.includeDefault) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 61 * hash + (this.text != null ? this.text.hashCode() : 0);
            hash = 61 * hash + (this.type != null ? this.type.hashCode() : 0);
            hash = 61 * hash + (this.forceUppercase ? 1 : 0);
            hash = 61 * hash + (this.includeDefault ? 1 : 0);
            return hash;
        }

        @Override
        public String toString() {
            switch (type) {
                case ANY:
                case DEFAULT:
                    return type.toString();

                case NAMEVALUE:
                    return (text == null ? "(null)"
                            : text.isEmpty() ? "(empty)"
                            : '"'+text+'"')
                            + (includeDefault?"+":"") ;

                case IDVALUE:
                default:
                    throw new AssertionError();
            }
        }
    }
}
