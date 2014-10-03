/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.view;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.widget.AddProfileWidget;


// TODO: Auto-generated Javadoc
/**
 * The Class UsersView.
 */
public class ProfilesView extends View
{

    /** The profiles manager service remote. */
    private ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote =
        ProfilesManagerRemoteServiceAsync.Util.getInstance();

    private AddProfileWidget addProfile;

    /**
     * Instantiates a new users view.
     *
     * @param controller
     *            the controller
     */
    public ProfilesView(Controller controller)
    {
        super(controller);

        this.addProfile = new AddProfileWidget(GeofenceEvents.SAVE_PROFILE, true);
        this.addProfile.setProfileService(profilesManagerServiceRemote);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.mvc.View#handleEvent(com.extjs.gxt.ui.client.mvc.AppEvent)
     */
    @Override
    protected void handleEvent(AppEvent event)
    {
        if (event.getType() == GeofenceEvents.CREATE_NEW_PROFILE)
        {
            onCreateNewProfile(event);
        }

    }

    /**
     * On create new profile.
     *
     * @param event
     *            the event
     */
    private void onCreateNewProfile(AppEvent event)
    {
        this.addProfile.show();
    }

//    /**
//     * On edit profile.
//     *
//     * @param event
//     *            the event
//     */
//    private void onEditUser(AppEvent event) {
//        if (event.getData() != null && event.getData() instanceof GSUser) {
//            this.userEditorDialog.reset();
//            this.userEditorDialog.setModel((GSUser) event.getData());
//            this.userEditorDialog.show();
//        } else {
//            // TODO: i18n!!
//            Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE, new String[] {
//                    "Users Editor", "Could not found any associated profile!" });
//        }
//    }

//    /**
//     * On rule custom prop add.
//     *
//     * @param event
//     *            the event
//     */
//    private void onRuleCustomPropAdd(AppEvent event) {
//        if (event.getData() != null) {
//            LayerCustomPropsTabItem layersCustomPropsItem = (LayerCustomPropsTabItem) this.userEditorDialog
//                    .getTabWidget().getItemByItemId(
//                            RuleDetailsEditDialog.RULE_LAYER_CUSTOM_PROPS_DIALOG_ID);
//            final LayerCustomPropsGridWidget layerCustomPropsInfo = layersCustomPropsItem
//                    .getLayerCustomPropsWidget().getLayerCustomPropsInfo();
//            LayerCustomProps model = new LayerCustomProps();
//            model.setPropKey("_new");
//            layerCustomPropsInfo.getStore().add(model);
//
//            layerCustomPropsInfo.getGrid().repaint();
//        } else {
//            // TODO: i18n!!
//            Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE, new String[] {
//                    "Rules Details Editor", "Could not found any associated rule!" });
//        }
//    }
//
//    /**
//     * On rule custom prop remove.
//     *
//     * @param event
//     *            the event
//     */
//    private void onRuleCustomPropRemove(AppEvent event) {
//        if (event.getData() != null) {
//            Map<Long, LayerCustomProps> updateDTO = event.getData();
//
//            LayerCustomPropsTabItem layersCustomPropsItem = (LayerCustomPropsTabItem) this.userEditorDialog
//                    .getTabWidget().getItemByItemId(
//                            RuleDetailsEditDialog.RULE_LAYER_CUSTOM_PROPS_DIALOG_ID);
//            final LayerCustomPropsGridWidget layerCustomPropsInfo = layersCustomPropsItem
//                    .getLayerCustomPropsWidget().getLayerCustomPropsInfo();
//
//            for (Long ruleId : updateDTO.keySet()) {
//                LayerCustomProps dto = updateDTO.get(ruleId);
//
//                for (LayerCustomProps prop : layerCustomPropsInfo.getStore().getModels()) {
//                    if (prop.getPropKey().equals(dto.getPropKey()))
//                        layerCustomPropsInfo.getStore().remove(prop);
//                }
//            }
//
//            layerCustomPropsInfo.getGrid().repaint();
//
//        } else {
//            // TODO: i18n!!
//            Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE, new String[] {
//                    "Rules Details Editor", "Could not found any associated rule!" });
//        }
//    }
//
//    /**
//     * On rule custom prop update key.
//     *
//     * @param event
//     *            the event
//     */
//    private void onRuleCustomPropUpdateKey(AppEvent event) {
//        if (event.getData() != null) {
//            LayerCustomPropsTabItem layersCustomPropsItem = (LayerCustomPropsTabItem) this.userEditorDialog
//                    .getTabWidget().getItemByItemId(
//                            RuleDetailsEditDialog.RULE_LAYER_CUSTOM_PROPS_DIALOG_ID);
//            final LayerCustomPropsGridWidget layerCustomPropsInfo = layersCustomPropsItem
//                    .getLayerCustomPropsWidget().getLayerCustomPropsInfo();
//
//            Map<String, LayerCustomProps> updateDTO = event.getData();
//
//            for (String key : updateDTO.keySet()) {
//                for (LayerCustomProps prop : layerCustomPropsInfo.getStore().getModels()) {
//                    if (prop.getPropKey().equals(key)) {
//                        layerCustomPropsInfo.getStore().remove(prop);
//                        LayerCustomProps newModel = updateDTO.get(key);
//                        layerCustomPropsInfo.getStore().add(newModel);
//                    }
//                }
//            }
//
//            layerCustomPropsInfo.getGrid().repaint();
//        } else {
//            // TODO: i18n!!
//            Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE, new String[] {
//                    "Rules Details Editor", "Could not found any associated rule!" });
//        }
//    }
//
//    /**
//     * On rule custom prop update value.
//     *
//     * @param event
//     *            the event
//     */
//    private void onRuleCustomPropUpdateValue(AppEvent event) {
//        if (event.getData() != null) {
//            LayerCustomPropsTabItem layersCustomPropsItem = (LayerCustomPropsTabItem) this.userEditorDialog
//                    .getTabWidget().getItemByItemId(
//                            RuleDetailsEditDialog.RULE_LAYER_CUSTOM_PROPS_DIALOG_ID);
//            final LayerCustomPropsGridWidget layerCustomPropsInfo = layersCustomPropsItem
//                    .getLayerCustomPropsWidget().getLayerCustomPropsInfo();
//
//            Map<String, LayerCustomProps> updateDTO = event.getData();
//
//            for (String key : updateDTO.keySet()) {
//                for (LayerCustomProps prop : layerCustomPropsInfo.getStore().getModels()) {
//                    if (prop.getPropKey().equals(key)) {
//                        layerCustomPropsInfo.getStore().remove(prop);
//                        LayerCustomProps newModel = updateDTO.get(key);
//                        layerCustomPropsInfo.getStore().add(newModel);
//                    }
//                }
//            }
//
//            layerCustomPropsInfo.getGrid().repaint();
//        } else {
//            // TODO: i18n!!
//            Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE, new String[] {
//                    "Rules Details Editor", "Could not found any associated rule!" });
//        }
//    }
//
//    /**
//     * On rule custom prop save.
//     *
//     * @param event
//     *            the event
//     */
//    private void onRuleCustomPropSave(AppEvent event) {
//        Long ruleId = event.getData();
//
//        LayerCustomPropsTabItem layersCustomPropsItem = (LayerCustomPropsTabItem) this.userEditorDialog
//                .getTabWidget().getItemByItemId(
//                        RuleDetailsEditDialog.RULE_LAYER_CUSTOM_PROPS_DIALOG_ID);
//        final LayerCustomPropsGridWidget layerCustomPropsInfo = layersCustomPropsItem
//                .getLayerCustomPropsWidget().getLayerCustomPropsInfo();
//
//        rulesManagerServiceRemote.setDetailsProps(ruleId, layerCustomPropsInfo.getStore()
//                .getModels(), new AsyncCallback<PagingLoadResult<LayerCustomProps>>() {
//
//            public void onFailure(Throwable caught) {
//
//                Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE, new String[] {
//                        I18nProvider.getMessages().ruleServiceName(),
//                        "Error occurred while saving Rule Custom Properties!" });
//            }
//
//            public void onSuccess(PagingLoadResult<LayerCustomProps> result) {
//
//                // grid.getStore().sort(BeanKeyValue.PRIORITY.getValue(),
//                // SortDir.ASC);
//                layerCustomPropsInfo.getStore().getLoader().load();
//                layerCustomPropsInfo.getGrid().repaint();
//
//                Dispatcher.forwardEvent(GeofenceEvents.BIND_MEMBER_DISTRIBUTION_NODES, result);
//                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE, new String[] {
//                        I18nProvider.getMessages().ruleServiceName(),
//                        I18nProvider.getMessages().ruleFetchSuccessMessage() });
//            }
//        });
//    }
}
