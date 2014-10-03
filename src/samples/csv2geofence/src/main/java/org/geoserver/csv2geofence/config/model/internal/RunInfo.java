/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.csv2geofence.config.model.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Info parsed from the command line.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RunInfo {

    private File configurationFile;

    private List<File> userFiles = new ArrayList<File>();
    private List<File> ruleFiles = new ArrayList<File>();

    private File outputFile;
    private boolean sendRequested;
//    private boolean groupAlignRequested = false;
    private boolean deleteObsoleteRules = false;


    public File getConfigurationFile() {
        return configurationFile;
    }

    public void setConfigurationFile(File configurationFile) {
        this.configurationFile = configurationFile;
    }

    public List<File> getUserFiles() {
        return userFiles;
    }

    public void setUserFiles(List<File> userFiles) {
        this.userFiles = userFiles;
    }

    public List<File> getRuleFiles() {
        return ruleFiles;
    }

    public void setRuleFiles(List<File> ruleFiles) {
        this.ruleFiles = ruleFiles;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public boolean isSendRequested() {
        return sendRequested;
    }

    public void setSendRequested(boolean sendRequested) {
        this.sendRequested = sendRequested;
    }

//    public boolean isGroupAlignRequested() {
//        return groupAlignRequested;
//    }
//
//    public void setGroupAlignRequested(boolean groupAlignRequested) {
//        this.groupAlignRequested = groupAlignRequested;
//    }

    public boolean isDeleteObsoleteRules() {
        return deleteObsoleteRules;
    }

    public void setDeleteObsoleteRules(boolean deleteObsoleteRules) {
        this.deleteObsoleteRules = deleteObsoleteRules;
    }

}
