/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.configuration;

import org.geoserver.geofence.gui.client.model.Authorization;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.widget.form.ComboBox;

// TODO: Auto-generated Javadoc
/**
 * The Class DropdownClientTool.
 */
public class DropdownClientTool extends GenericClientTool {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8377394857738349837L;

    /** The label. */
    private String label;

    /** The enabled. */
    private boolean enabled = true;

    /** The default value. */
    private String defaultValue;

    /** The allow blank. */
    private boolean allowBlank = false;

    /** The dropdown options. */
    private List<DropdownOption> dropdownOptions = new ArrayList<DropdownOption>();

    /**
     * Gets the label.
     * 
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label.
     * 
     * @param label
     *            the new label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Checks if is the enabled.
     * 
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled.
     * 
     * @param enabled
     *            the new enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets the dropdown options.
     * 
     * @param options
     *            the new dropdown options
     */
    public void setDropdownOptions(List<DropdownOption> options) {
        this.dropdownOptions = options;
    }

    /**
     * Gets the dropdown options.
     * 
     * @return the dropdown options
     */
    public List<DropdownOption> getDropdownOptions() {
        return this.dropdownOptions;
    }

    /**
     * Adds the option.
     * 
     * @param d
     *            the d
     */
    public void addOption(DropdownOption d) {
        this.dropdownOptions.add(d);
    }

    /**
     * Gets the default value.
     * 
     * @return the default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value.
     * 
     * @param defaultValue
     *            the new default value
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Checks if is the allow blank.
     * 
     * @return the allow blank
     */
    public boolean isAllowBlank() {
        return allowBlank;
    }

    /**
     * Sets the allow blank.
     * 
     * @param allowBlank
     *            the new allow blank
     */
    public void setAllowBlank(boolean allowBlank) {
        this.allowBlank = allowBlank;
    }

    /**
     * Gets the dropdown option display value.
     * 
     * @param dataValue
     *            the data value
     * @return the dropdown option display value
     */
    public String getDropdownOptionDisplayValue(String dataValue) {
        return null;
    }

    /**
     * Inject security.
     * 
     * @param combo
     *            the combo
     * @param auths
     *            the auths
     */
    public void injectSecurity(ComboBox combo, List<Authorization> auths) {
        // default implementation - do nothing
    }
}