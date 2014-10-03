/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.view;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.i18n.I18nProvider;
import org.geoserver.geofence.gui.client.model.GSUser;
import org.geoserver.geofence.gui.client.model.Rule;
import org.geoserver.geofence.gui.client.model.UserGroup;
import org.geoserver.geofence.gui.client.model.data.LayerCustomProps;
import org.geoserver.geofence.gui.client.model.data.LayerDetailsInfo;
import org.geoserver.geofence.gui.client.model.data.LayerLimitsInfo;
import org.geoserver.geofence.gui.client.model.data.ProfileCustomProps;
import org.geoserver.geofence.gui.client.model.data.UserLimitsInfo;
import org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.InstancesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.RulesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.WorkspacesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.widget.EditRuleWidget;
import org.geoserver.geofence.gui.client.widget.GridStatus;
import org.geoserver.geofence.gui.client.widget.dialog.ProfileDetailsEditDialog;
import org.geoserver.geofence.gui.client.widget.dialog.RuleDetailsEditDialog;
import org.geoserver.geofence.gui.client.widget.dialog.UserDetailsEditDialog;
import org.geoserver.geofence.gui.client.widget.rule.detail.LayerAttributesGridWidget;
import org.geoserver.geofence.gui.client.widget.rule.detail.LayerAttributesTabItem;
import org.geoserver.geofence.gui.client.widget.rule.detail.LayerCustomPropsGridWidget;
import org.geoserver.geofence.gui.client.widget.rule.detail.LayerCustomPropsTabItem;
import org.geoserver.geofence.gui.client.widget.rule.detail.ProfileDetailsGridWidget;
import org.geoserver.geofence.gui.client.widget.rule.detail.ProfileDetailsTabItem;
import org.geoserver.geofence.gui.client.widget.rule.detail.RuleDetailsGridWidget;
import org.geoserver.geofence.gui.client.widget.rule.detail.RuleDetailsInfoWidget;
import org.geoserver.geofence.gui.client.widget.rule.detail.RuleDetailsTabItem;
import org.geoserver.geofence.gui.client.widget.rule.detail.RuleLimitsInfoWidget;
import org.geoserver.geofence.gui.client.widget.rule.detail.RuleLimitsTabItem;
import org.geoserver.geofence.gui.client.widget.rule.detail.UserDetailsInfoWidget;
import org.geoserver.geofence.gui.client.widget.rule.detail.UserDetailsTabItem;
import it.geosolutions.geogwt.gui.client.GeoGWTEvents;

import java.util.Map;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

// TODO: Auto-generated Javadoc
/**
 * The Class RulesView.
 */
public class RulesView extends View {

	/** The rules manager service remote. */
	private RulesManagerRemoteServiceAsync rulesManagerServiceRemote = RulesManagerRemoteServiceAsync.Util
			.getInstance();

	/** The workspace manager service remote. */
	private WorkspacesManagerRemoteServiceAsync workspacesManagerServiceRemote = WorkspacesManagerRemoteServiceAsync.Util
			.getInstance();

	/** The rules manager service remote. */
	private GsUsersManagerRemoteServiceAsync usersManagerServiceRemote = GsUsersManagerRemoteServiceAsync.Util
			.getInstance();

	/** The rules manager service remote. */
	private InstancesManagerRemoteServiceAsync instancesManagerServiceRemote = InstancesManagerRemoteServiceAsync.Util
			.getInstance();

	/** The rules manager service remote. */
	private ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote = ProfilesManagerRemoteServiceAsync.Util
			.getInstance();

	/** The rule editor dialog. */
	private RuleDetailsEditDialog ruleEditorDialog;

	/** The profile editor dialog. */
	private ProfileDetailsEditDialog profileEditorDialog;

	/** The user editor dialog. */
	private UserDetailsEditDialog userDetailsEditDialog;

	public EditRuleWidget ruleRowEditor;

	/**
	 * Instantiates a new rules view.
	 * 
	 * @param controller
	 *            the controller
	 */
	public RulesView(Controller controller) {
		super(controller);

		this.ruleEditorDialog = new RuleDetailsEditDialog(
				rulesManagerServiceRemote, workspacesManagerServiceRemote);
		ruleEditorDialog.setClosable(false);

		this.profileEditorDialog = new ProfileDetailsEditDialog(
				profilesManagerServiceRemote);
		profileEditorDialog.setClosable(false);

		this.userDetailsEditDialog = new UserDetailsEditDialog(
				usersManagerServiceRemote, profilesManagerServiceRemote);
		userDetailsEditDialog.setClosable(false);

		this.ruleRowEditor = new EditRuleWidget(GeofenceEvents.SAVE_USER, true,
				rulesManagerServiceRemote, null, null, null, null);
		this.ruleRowEditor.setGsUserService(usersManagerServiceRemote);
		this.ruleRowEditor.setRuleService(rulesManagerServiceRemote);
		this.ruleRowEditor.setInstanceService(instancesManagerServiceRemote);
		this.ruleRowEditor.setWorkspaceService(workspacesManagerServiceRemote);
		this.ruleRowEditor.setProfileService(profilesManagerServiceRemote);
		this.ruleRowEditor.setRulesView(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extjs.gxt.ui.client.mvc.View#handleEvent(com.extjs.gxt.ui.client.
	 * mvc.AppEvent)
	 */
	@Override
	protected void handleEvent(AppEvent event) {
		if (event.getType() == GeofenceEvents.EDIT_RULE_DETAILS) {
			onEditRuleDetails(event);
		}
		if (event.getType() == GeofenceEvents.EDIT_RULE) {
			onEditRowRuleDetails(event);
		}
		if (event.getType() == GeofenceEvents.EDIT_RULE_UPDATE) {
			onEditRowUpdateRuleDetails(event);
		}
		if (event.getType() == GeofenceEvents.RULE_CUSTOM_PROP_ADD) {
			onRuleCustomPropAdd(event);
		}

		if (event.getType() == GeofenceEvents.RULE_CUSTOM_PROP_DEL) {
			onRuleCustomPropRemove(event);
		}

		if (event.getType() == GeofenceEvents.RULE_CUSTOM_PROP_UPDATE_KEY) {
			onRuleCustomPropUpdateKey(event);
		}

		if (event.getType() == GeofenceEvents.RULE_CUSTOM_PROP_UPDATE_VALUE) {
			onRuleCustomPropUpdateValue(event);
		}

		if (event.getType() == GeofenceEvents.RULE_CUSTOM_PROP_APPLY_CHANGES) {
			onRuleCustomPropSave(event);
		}

		if (event.getType() == GeofenceEvents.ATTRIBUTE_UPDATE_GRID_COMBO) {
			onRuleLayerAttributesSave(event);
		}

		if (event.getType() == GeofenceEvents.SAVE_LAYER_DETAILS) {
			onSaveLayerDetailsInfo(event);
		}

		if (event.getType() == GeofenceEvents.LOAD_LAYER_DETAILS) {
			onLoadLayerDetailsInfo(event);
		}

		if (event.getType() == GeofenceEvents.UPDATE_SOUTH_SIZE) {
			// logger
		}

		if (event.getType() == GeofenceEvents.RULE_PROFILE_CUSTOM_PROP_UPDATE_KEY) {
			onRuleProfileCustomPropUpdateKey(event);
		}

		if (event.getType() == GeofenceEvents.RULE_PROFILE_CUSTOM_PROP_UPDATE_VALUE) {
			onRuleProfileCustomPropUpdateValue(event);
		}

		if (event.getType() == GeofenceEvents.RULE_PROFILE_CUSTOM_PROP_DEL) {
			onRuleProfileCustomPropRemove(event);
		}

		if (event.getType() == GeofenceEvents.RULE_PROFILE_CUSTOM_PROP_ADD) {
			onRuleProfileCustomPropAdd(event);
		}

		if (event.getType() == GeofenceEvents.RULE_PROFILE_CUSTOM_PROP_APPLY_CHANGES) {
			onRuleProfileCustomPropSave(event);
		}

		if (event.getType() == GeofenceEvents.RULE_EDITOR_DIALOG_HIDE) {
			ruleEditorDialog.hide();
		}
		if (event.getType() == GeofenceEvents.RULE_EDITOR_DIALOG_SHOW) {
			ruleEditorDialog.show();
		}

		if (event.getType() == GeofenceEvents.EDIT_PROFILE_DETAILS) {
			onEditProfileDetails(event);
		}

		if (event.getType() == GeofenceEvents.SAVE_LAYER_LIMITS) {
			onSaveLayerLimits(event);
		}

		if (event.getType() == GeofenceEvents.LOAD_LAYER_LIMITS) {
			onLoadLayerLimits(event);
		}

		if (event.getType() == GeofenceEvents.EDIT_USER_DETAILS) {
			onEditUserDetails(event);
		}

		if (event.getType() == GeofenceEvents.LOAD_USER_LIMITS) {
			onLoadUserLimits(event);
		}

		if (event.getType() == GeofenceEvents.SAVE_USER_LIMITS) {
			onSaveUserLimits(event);
		}

		if (event.getType() == GeofenceEvents.SAVE_USER_GROUPS) {
			onSaveUserGroups(event);
		}

		if (event.getType() == GeofenceEvents.INJECT_WKT
				|| event.getType() == GeoGWTEvents.INJECT_WKT) {
			onInjectWKT(event);
		}

	}

	/**
	 * @param event
	 */
	private void onSaveUserLimits(AppEvent event) {
		UserLimitsInfo userInfo = event.getData();

		this.usersManagerServiceRemote.saveUserLimitsInfo(userInfo,
				new AsyncCallback<UserLimitsInfo>() {

					public void onFailure(Throwable caught) {
						Dispatcher
								.forwardEvent(
										GeofenceEvents.SEND_ERROR_MESSAGE,
										new String[] {
												I18nProvider.getMessages()
														.ruleServiceName(),
												"Error occurred while saving User Details!" });
					}

					public void onSuccess(UserLimitsInfo result) {
						UserDetailsTabItem userDetailsTabItem = (UserDetailsTabItem) userDetailsEditDialog
								.getTabWidget()
								.getItemByItemId(
										UserDetailsEditDialog.USER_DETAILS_DIALOG_ID);

						UserDetailsInfoWidget userDetailsInfoWidget = userDetailsTabItem
								.getUserDetailsWidget().getUserDetailsInfo();
						userDetailsInfoWidget.bindModelData(result);

						Dispatcher.forwardEvent(
								GeofenceEvents.SEND_INFO_MESSAGE, new String[] {
										I18nProvider.getMessages()
												.userServiceName(),
										I18nProvider.getMessages()
												.userFetchSuccessMessage() });
					}
				});
	}

	/**
	 * @param event
	 */
	private void onSaveUserGroups(AppEvent event) {
		GSUser user = event.getData();

		this.usersManagerServiceRemote.saveGsUser(user,
				new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {
						Dispatcher
								.forwardEvent(
										GeofenceEvents.SEND_ERROR_MESSAGE,
										new String[] {
												I18nProvider.getMessages()
														.ruleServiceName(),
												"Error occurred while saving User Details!" });
					}

					public void onSuccess(Void result) {
						Dispatcher.forwardEvent(
								GeofenceEvents.SEND_INFO_MESSAGE, new String[] {
										I18nProvider.getMessages()
												.userServiceName(),
										I18nProvider.getMessages()
												.userFetchSuccessMessage() });
					}
				});
	}

	/**
	 * @param event
	 */
	private void onLoadUserLimits(AppEvent event) {
		GSUser user = event.getData();

		this.usersManagerServiceRemote.getUserLimitsInfo(user,
				new AsyncCallback<UserLimitsInfo>() {

					public void onFailure(Throwable caught) {
						Dispatcher
								.forwardEvent(
										GeofenceEvents.SEND_ERROR_MESSAGE,
										new String[] {
												I18nProvider.getMessages()
														.ruleServiceName(),
												"Error occurred while getting User Details!" });
					}

					public void onSuccess(UserLimitsInfo result) {
						if (result != null) {
							UserDetailsTabItem userDetailsTabItem = (UserDetailsTabItem) userDetailsEditDialog
									.getTabWidget()
									.getItemByItemId(
											UserDetailsEditDialog.USER_DETAILS_DIALOG_ID);

							UserDetailsInfoWidget userDetailsWidget = userDetailsTabItem
									.getUserDetailsWidget()
									.getUserDetailsInfo();

							userDetailsWidget.bindModelData(result);

							Dispatcher
									.forwardEvent(
											GeofenceEvents.SEND_INFO_MESSAGE,
											new String[] {
													I18nProvider.getMessages()
															.userServiceName(),
													I18nProvider
															.getMessages()
															.userFetchSuccessMessage() });
						}
					}
				});
	}

	/**
	 * @param event
	 */
	private void onEditUserDetails(AppEvent event) {
		if ((event.getData() != null) && (event.getData() instanceof GSUser)) {
			this.userDetailsEditDialog.reset();
			this.userDetailsEditDialog.setModel((GSUser) event.getData());
			this.userDetailsEditDialog.show();
		} else {
			// TODO: i18n!!
			Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE,
					new String[] { "User Details Editor",
							"Could not found any associated user!" });
		}
	}

	/**
	 * @param event
	 */
	private void onLoadLayerLimits(AppEvent event) {
		Rule rule = event.getData();

		this.rulesManagerServiceRemote.getLayerLimitsInfo(rule,
				new AsyncCallback<LayerLimitsInfo>() {

					public void onFailure(Throwable caught) {
						Dispatcher
								.forwardEvent(
										GeofenceEvents.SEND_ERROR_MESSAGE,
										new String[] {
												I18nProvider.getMessages()
														.ruleServiceName(),
												"Error occurred while getting Rule Layer Details!" });
					}

					public void onSuccess(LayerLimitsInfo result) {
						if (result != null) {
							RuleLimitsTabItem ruleLimitsTabItem = (RuleLimitsTabItem) ruleEditorDialog
									.getTabWidget()
									.getItemByItemId(
											RuleDetailsEditDialog.RULE_LIMITS_DIALOG_ID);

							RuleLimitsInfoWidget ruleDetailsWidget = ruleLimitsTabItem
									.getRuleLimitsWidget().getRuleLimitsInfo();

							ruleDetailsWidget.bindModelData(result);

							Dispatcher
									.forwardEvent(
											GeofenceEvents.SEND_INFO_MESSAGE,
											new String[] {
													I18nProvider.getMessages()
															.ruleServiceName(),
													I18nProvider
															.getMessages()
															.ruleFetchSuccessMessage() });
						}
					}
				});
	}

	/**
	 * @param event
	 */
	private void onSaveLayerLimits(AppEvent event) {
		LayerLimitsInfo layerLimitsInfo = event.getData();

		this.rulesManagerServiceRemote.saveLayerLimitsInfo(layerLimitsInfo,
				new AsyncCallback<LayerLimitsInfo>() {

					public void onFailure(Throwable caught) {
						Dispatcher
								.forwardEvent(
										GeofenceEvents.SEND_ERROR_MESSAGE,
										new String[] {
												I18nProvider.getMessages()
														.ruleServiceName(),
												"Error occurred while saving Rule Layer Limits!" });
					}

					public void onSuccess(LayerLimitsInfo result) {
						RuleLimitsTabItem ruleLimitsTabItem = (RuleLimitsTabItem) ruleEditorDialog
								.getTabWidget()
								.getItemByItemId(
										RuleDetailsEditDialog.RULE_LIMITS_DIALOG_ID);

						RuleLimitsInfoWidget ruleLimitsWidget = ruleLimitsTabItem
								.getRuleLimitsWidget().getRuleLimitsInfo();
						ruleLimitsWidget.bindModelData(result);

						Dispatcher.forwardEvent(
								GeofenceEvents.SEND_INFO_MESSAGE, new String[] {
										I18nProvider.getMessages()
												.ruleServiceName(),
										I18nProvider.getMessages()
												.ruleFetchSuccessMessage() });
					}
				});
	}

	/**
	 * @param event
	 */
	private void onEditProfileDetails(AppEvent event) {
		if ((event.getData() != null) && (event.getData() instanceof UserGroup)) {
			this.profileEditorDialog.reset();
			this.profileEditorDialog.setModel((UserGroup) event.getData());
			this.profileEditorDialog.show();
		} else {
			// TODO: i18n!!
			Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE,
					new String[] { "Groups Details Editor",
							"Could not found any associated groups!" });
		}
	}

	/**
	 * @param event
	 */
	private void onRuleProfileCustomPropSave(AppEvent event) {

		Long profileId = event.getData();

		ProfileDetailsTabItem profileDetailsTabItem = (ProfileDetailsTabItem) this.profileEditorDialog
				.getTabWidget().getItemByItemId(
						ProfileDetailsEditDialog.PROFILE_DETAILS_DIALOG_ID);

		final ProfileDetailsGridWidget profileCustomPropsInfo = profileDetailsTabItem
				.getProfileDetailsWidget().getProfileDetailsInfo();

		profilesManagerServiceRemote.setProfileProps(profileId,
				profileCustomPropsInfo.getStore().getModels(),
				new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {

						Dispatcher
								.forwardEvent(
										GeofenceEvents.SEND_ERROR_MESSAGE,
										new String[] {
												I18nProvider.getMessages()
														.ruleServiceName(),
												"Error occurred while saving Rule Custom Properties!" });
					}

					public void onSuccess(Void result) {
						profileCustomPropsInfo.getStore().getLoader().load();

						Dispatcher.forwardEvent(
								GeofenceEvents.SEND_INFO_MESSAGE, new String[] {
										I18nProvider.getMessages()
												.ruleServiceName(),
										I18nProvider.getMessages()
												.ruleFetchSuccessMessage() });
					}
				});
	}

	/**
	 * @param event
	 */
	private void onRuleProfileCustomPropAdd(AppEvent event) {

		if (event.getData() != null) {

			ProfileDetailsTabItem profileDetailsTabItem = (ProfileDetailsTabItem) this.profileEditorDialog
					.getTabWidget().getItemByItemId(
							ProfileDetailsEditDialog.PROFILE_DETAILS_DIALOG_ID);

			final ProfileDetailsGridWidget profileCustomPropsInfo = profileDetailsTabItem
					.getProfileDetailsWidget().getProfileDetailsInfo();

			ProfileCustomProps model = new ProfileCustomProps();
			model.setPropKey("_new");
			profileCustomPropsInfo.getStore().add(model);

			Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
					new String[] { "GeoServer Rules: Layer Custom Properties",
							"Add Property" });

		} else {
			// TODO: i18n!!
			Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE,
					new String[] { "Rules Details Editor",
							"Could not found any associated rule!" });
		}
	}

	/**
	 * @param event
	 */
	private void onRuleProfileCustomPropRemove(AppEvent event) {
		if (event.getData() != null) {
			Map<Long, ProfileCustomProps> updateDTO = event.getData();

			ProfileDetailsTabItem profileDetailsTabItem = (ProfileDetailsTabItem) this.profileEditorDialog
					.getTabWidget().getItemByItemId(
							ProfileDetailsEditDialog.PROFILE_DETAILS_DIALOG_ID);

			final ProfileDetailsGridWidget profileCustomPropsInfo = profileDetailsTabItem
					.getProfileDetailsWidget().getProfileDetailsInfo();

			for (Long ruleId : updateDTO.keySet()) {
				ProfileCustomProps dto = updateDTO.get(ruleId);

				for (ProfileCustomProps prop : profileCustomPropsInfo
						.getStore().getModels()) {
					if (prop.getPropKey().equals(dto.getPropKey())) {
						profileCustomPropsInfo.getStore().remove(prop);
					}
				}
			}

			profileCustomPropsInfo.getGrid().repaint();

		} else {
			// TODO: i18n!!
			Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE,
					new String[] { "Rules Details Editor",
							"Could not found any associated rule!" });
		}
	}

	/**
	 * @param event
	 */
	private void onRuleProfileCustomPropUpdateValue(AppEvent event) {

		if (event.getData() != null) {

			ProfileDetailsTabItem profileDetailsTabItem = (ProfileDetailsTabItem) this.profileEditorDialog
					.getTabWidget().getItemByItemId(
							ProfileDetailsEditDialog.PROFILE_DETAILS_DIALOG_ID);

			final ProfileDetailsGridWidget profileCustomPropsInfo = profileDetailsTabItem
					.getProfileDetailsWidget().getProfileDetailsInfo();

			Map<String, ProfileCustomProps> updateDTO = event.getData();

			for (String key : updateDTO.keySet()) {
				for (ProfileCustomProps prop : profileCustomPropsInfo
						.getStore().getModels()) {
					if (prop.getPropKey().equals(key)) {
						profileCustomPropsInfo.getStore().remove(prop);

						ProfileCustomProps newModel = updateDTO.get(key);
						profileCustomPropsInfo.getStore().add(newModel);
					}
				}
			}

			profileCustomPropsInfo.getGrid().repaint();

		} else {
			// TODO: i18n!!
			Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE,
					new String[] { "Group Details Editor",
							"Could not found any associated rule!" });
		}
	}

	/**
	 * @param event
	 */
	private void onRuleProfileCustomPropUpdateKey(AppEvent event) {

		if (event.getData() != null) {

			ProfileDetailsTabItem profileDetailsTabItem = (ProfileDetailsTabItem) this.profileEditorDialog
					.getTabWidget().getItemByItemId(
							ProfileDetailsEditDialog.PROFILE_DETAILS_DIALOG_ID);

			final ProfileDetailsGridWidget profileCustomPropsInfo = profileDetailsTabItem
					.getProfileDetailsWidget().getProfileDetailsInfo();

			Map<String, ProfileCustomProps> updateDTO = event.getData();

			for (String key : updateDTO.keySet()) {
				for (ProfileCustomProps prop : profileCustomPropsInfo
						.getStore().getModels()) {
					if (prop.getPropKey().equals(key)) {
						profileCustomPropsInfo.getStore().remove(prop);

						ProfileCustomProps newModel = updateDTO.get(key);
						profileCustomPropsInfo.getStore().add(newModel);
					}
				}
			}

			profileCustomPropsInfo.getGrid().repaint();
		} else {
			// TODO: i18n!!
			Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE,
					new String[] { "Group Details Editor",
							"Could not found any associated rule!" });
		}
	}

	/**
	 * @param event
	 */
	private void onLoadLayerDetailsInfo(AppEvent event) {
		Rule rule = event.getData();

		this.rulesManagerServiceRemote.getLayerDetailsInfo(rule,
				new AsyncCallback<LayerDetailsInfo>() {

					public void onFailure(Throwable caught) {
						Dispatcher
								.forwardEvent(
										GeofenceEvents.SEND_ERROR_MESSAGE,
										new String[] {
												I18nProvider.getMessages()
														.ruleServiceName(),
												"Error occurred while getting Rule Layer Details!" });
					}

					public void onSuccess(LayerDetailsInfo result) {
						if (result != null) {
							RuleDetailsTabItem ruleDetailsTabItem = (RuleDetailsTabItem) ruleEditorDialog
									.getTabWidget()
									.getItemByItemId(
											RuleDetailsEditDialog.RULE_DETAILS_DIALOG_ID);

							RuleDetailsInfoWidget ruleDetailsWidget = ruleDetailsTabItem
									.getRuleDetailsWidget()
									.getRuleDetailsInfo();

							ruleDetailsWidget.bindModelData(result);

							if (result.getType().equalsIgnoreCase("raster")) {
								ruleDetailsWidget.disableCQLFilterButtons();
							} else {
								ruleDetailsWidget.enableCQLFilterButtons();
							}

							Dispatcher
									.forwardEvent(
											GeofenceEvents.SEND_INFO_MESSAGE,
											new String[] {
													I18nProvider.getMessages()
															.ruleServiceName(),
													I18nProvider
															.getMessages()
															.ruleFetchSuccessMessage() });
						}
					}
				});

	}

	/**
	 * @param event
	 */
	private void onSaveLayerDetailsInfo(AppEvent event) {
		LayerDetailsInfo layerDetailsInfo = event.getData();

		RuleDetailsTabItem ruleDetailsTabItem = (RuleDetailsTabItem) ruleEditorDialog
				.getTabWidget().getItemByItemId(
						RuleDetailsEditDialog.RULE_DETAILS_DIALOG_ID);

		final RuleDetailsGridWidget ruleDetailsGridWidget = ruleDetailsTabItem
				.getRuleDetailsWidget().getRuleDetailsGrid();

		this.rulesManagerServiceRemote.saveLayerDetailsInfo(layerDetailsInfo,
				ruleDetailsGridWidget.getStore().getModels(),
				new AsyncCallback<LayerDetailsInfo>() {

					public void onFailure(Throwable caught) {
						Dispatcher
								.forwardEvent(
										GeofenceEvents.SEND_ERROR_MESSAGE,
										new String[] {
												I18nProvider.getMessages()
														.ruleServiceName(),
												"Error occurred while saving Rule Layer Details!" });
					}

					public void onSuccess(LayerDetailsInfo result) {
						RuleDetailsTabItem ruleDetailsTabItem = (RuleDetailsTabItem) ruleEditorDialog
								.getTabWidget()
								.getItemByItemId(
										RuleDetailsEditDialog.RULE_DETAILS_DIALOG_ID);

						RuleDetailsInfoWidget ruleDetailsWidget = ruleDetailsTabItem
								.getRuleDetailsWidget().getRuleDetailsInfo();
						ruleDetailsWidget.bindModelData(result);

						ruleDetailsGridWidget.clearGridElements();
						ruleDetailsGridWidget.getStore().getLoader().load();

						Dispatcher.forwardEvent(
								GeofenceEvents.SEND_INFO_MESSAGE, new String[] {
										I18nProvider.getMessages()
												.ruleServiceName(),
										I18nProvider.getMessages()
												.ruleFetchSuccessMessage() });
					}
				});
	}

	/**
	 * On edit rule details.
	 * 
	 * @param event
	 *            the event
	 */
	private void onEditRuleDetails(AppEvent event) {
		if ((event.getData() != null) && (event.getData() instanceof Rule)) {
			this.ruleEditorDialog.reset();
			this.ruleEditorDialog.setModel((Rule) event.getData());
			this.ruleEditorDialog.show();
			
			
		} else {
			// TODO: i18n!!
			Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE,
					new String[] { "Rules Editor",
							"Could not found any associated rule!" });
		}
	}

	/**
	 * On edit rule details.
	 * 
	 * @param event
	 *            the event
	 */
	private void onEditRowRuleDetails(AppEvent event) {
		if ((event.getData() != null) && (event.getData() instanceof Rule)) {
			this.ruleRowEditor.reset();
			this.ruleRowEditor.status = "INSERT";
			showPanel(event);
		} else if ((event.getData() != null)
				&& (event.getData() instanceof GridStatus)) {
			this.ruleRowEditor.reset();
			this.ruleRowEditor.status = "INSERT";
			this.ruleRowEditor.parentGrid = ((GridStatus) event.getData())
					.getGrid();
			this.ruleRowEditor.model = ((GridStatus) event.getData())
					.getModel();
			showPanelData(event);

		} else {
			// TODO: i18n!!
			Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE,
					new String[] { "Rules Editor",
							"Could not found any associated rule!" });
		}
	}

	/**
	 * On edit rule update details.
	 * 
	 * @param event
	 *            the event
	 */
	private void onEditRowUpdateRuleDetails(AppEvent event) {
		if ((event.getData() != null) && (event.getData() instanceof Rule)) {
			this.ruleRowEditor.reset();
			this.ruleRowEditor.status = "UPDATE";
			showPanel(event);

		} else if ((event.getData() != null)
				&& (event.getData() instanceof GridStatus)) {
			this.ruleRowEditor.reset();
			this.ruleRowEditor.parentGrid = ((GridStatus) event.getData())
					.getGrid();
			this.ruleRowEditor.model = ((GridStatus) event.getData())
					.getModel();
			this.ruleRowEditor.status = "UPDATE";
			showPanelData(event);

		} else {
			// TODO: i18n!!
			Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE,
					new String[] { "Rules Editor",
							"Could not found any associated rule!" });
		}
	}

	/*
     *
     */
	private void showPanel(AppEvent event) {
		if (this.ruleRowEditor.store != null) {
			this.ruleRowEditor.store.removeAll();
		}
		this.ruleRowEditor.createStore();
		this.ruleRowEditor.store.add((Rule) event.getData());
		this.ruleRowEditor.initGrid();

		if (!this.ruleRowEditor.formPanel.isRendered()) {
			this.ruleRowEditor.formPanel.setFrame(true);
			this.ruleRowEditor.formPanel.setLayout(new FlowLayout());
		}
		if (!this.ruleRowEditor.isRendered()) {
			this.ruleRowEditor.initializeFormPanel();
			this.ruleRowEditor.setFrame(true);
			this.ruleRowEditor.setLayout(new FlowLayout());
			this.ruleRowEditor.initSizeFormPanel();
		}

		this.ruleRowEditor.setModel((Rule) event.getData());
		this.ruleRowEditor.addComponentToForm();

		this.ruleRowEditor.setClosable(false);
		this.ruleRowEditor.show();
		this.ruleRowEditor.formPanel.show();
		this.ruleRowEditor.repaint();
		this.ruleRowEditor.formPanel.repaint();
	}

	/*
     *
     */
	private void showPanelData(AppEvent event) {
		if (this.ruleRowEditor.store != null) {
			this.ruleRowEditor.store.removeAll();
		} else {
			this.ruleRowEditor.createStore();
		}
		if (!this.ruleRowEditor.isRendered()) {
			this.ruleRowEditor.initGrid();

			this.ruleRowEditor.setFrame(true);
			this.ruleRowEditor.setLayout(new FlowLayout());
			this.ruleRowEditor.formPanel.setFrame(true);
			this.ruleRowEditor.formPanel.setLayout(new FlowLayout());
			this.ruleRowEditor.initSizeFormPanel();
			this.ruleRowEditor.addComponentToForm();
		}

		this.ruleRowEditor.store.add(((GridStatus) event.getData()).getModel());

		this.ruleRowEditor.setClosable(false);
		this.ruleRowEditor.show();
		this.ruleRowEditor.formPanel.show();

		this.ruleRowEditor.grid.repaint();
	}

	/**
	 * On rule custom prop add.
	 * 
	 * @param event
	 *            the event
	 */
	private void onRuleCustomPropAdd(AppEvent event) {
		if (event.getData() != null) {
			LayerCustomPropsTabItem layersCustomPropsItem = (LayerCustomPropsTabItem) this.ruleEditorDialog
					.getTabWidget()
					.getItemByItemId(
							RuleDetailsEditDialog.RULE_LAYER_CUSTOM_PROPS_DIALOG_ID);
			final LayerCustomPropsGridWidget layerCustomPropsInfo = layersCustomPropsItem
					.getLayerCustomPropsWidget().getLayerCustomPropsInfo();
			LayerCustomProps model = new LayerCustomProps();
			model.setPropKey("_new");
			layerCustomPropsInfo.getStore().add(model);

			layerCustomPropsInfo.getGrid().repaint();
		} else {
			// TODO: i18n!!
			Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE,
					new String[] { "Rules Details Editor",
							"Could not found any associated rule!" });
		}
	}

	/**
	 * On rule custom prop remove.
	 * 
	 * @param event
	 *            the event
	 */
	private void onRuleCustomPropRemove(AppEvent event) {
		if (event.getData() != null) {
			Map<Long, LayerCustomProps> updateDTO = event.getData();

			LayerCustomPropsTabItem layersCustomPropsItem = (LayerCustomPropsTabItem) this.ruleEditorDialog
					.getTabWidget()
					.getItemByItemId(
							RuleDetailsEditDialog.RULE_LAYER_CUSTOM_PROPS_DIALOG_ID);
			final LayerCustomPropsGridWidget layerCustomPropsInfo = layersCustomPropsItem
					.getLayerCustomPropsWidget().getLayerCustomPropsInfo();

			for (Long ruleId : updateDTO.keySet()) {
				LayerCustomProps dto = updateDTO.get(ruleId);

				for (LayerCustomProps prop : layerCustomPropsInfo.getStore()
						.getModels()) {
					if (prop.getPropKey().equals(dto.getPropKey())) {
						layerCustomPropsInfo.getStore().remove(prop);
					}
				}
			}

			layerCustomPropsInfo.getGrid().repaint();

		} else {
			// TODO: i18n!!
			Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE,
					new String[] { "Rules Details Editor",
							"Could not found any associated rule!" });
		}
	}

	/**
	 * On rule custom prop update key.
	 * 
	 * @param event
	 *            the event
	 */
	private void onRuleCustomPropUpdateKey(AppEvent event) {
		if (event.getData() != null) {
			LayerCustomPropsTabItem layersCustomPropsItem = (LayerCustomPropsTabItem) this.ruleEditorDialog
					.getTabWidget()
					.getItemByItemId(
							RuleDetailsEditDialog.RULE_LAYER_CUSTOM_PROPS_DIALOG_ID);
			final LayerCustomPropsGridWidget layerCustomPropsInfo = layersCustomPropsItem
					.getLayerCustomPropsWidget().getLayerCustomPropsInfo();

			Map<String, LayerCustomProps> updateDTO = event.getData();

			for (String key : updateDTO.keySet()) {
				for (LayerCustomProps prop : layerCustomPropsInfo.getStore()
						.getModels()) {
					if (prop.getPropKey().equals(key)) {
						layerCustomPropsInfo.getStore().remove(prop);

						LayerCustomProps newModel = updateDTO.get(key);
						layerCustomPropsInfo.getStore().add(newModel);
					}
				}
			}

			layerCustomPropsInfo.getGrid().repaint();
		} else {
			// TODO: i18n!!
			Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE,
					new String[] { "Rules Details Editor",
							"Could not found any associated rule!" });
		}
	}

	/**
	 * On rule custom prop update value.
	 * 
	 * @param event
	 *            the event
	 */
	private void onRuleCustomPropUpdateValue(AppEvent event) {
		if (event.getData() != null) {
			LayerCustomPropsTabItem layersCustomPropsItem = (LayerCustomPropsTabItem) this.ruleEditorDialog
					.getTabWidget()
					.getItemByItemId(
							RuleDetailsEditDialog.RULE_LAYER_CUSTOM_PROPS_DIALOG_ID);
			final LayerCustomPropsGridWidget layerCustomPropsInfo = layersCustomPropsItem
					.getLayerCustomPropsWidget().getLayerCustomPropsInfo();

			Map<String, LayerCustomProps> updateDTO = event.getData();

			for (String key : updateDTO.keySet()) {
				for (LayerCustomProps prop : layerCustomPropsInfo.getStore()
						.getModels()) {
					if (prop.getPropKey().equals(key)) {
						layerCustomPropsInfo.getStore().remove(prop);

						LayerCustomProps newModel = updateDTO.get(key);
						layerCustomPropsInfo.getStore().add(newModel);
					}
				}
			}

			layerCustomPropsInfo.getGrid().repaint();
		} else {
			// TODO: i18n!!
			Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE,
					new String[] { "Rules Details Editor",
							"Could not found any associated rule!" });
		}
	}

	/**
	 * 
	 * @param event
	 */
	private void onRuleCustomPropSave(AppEvent event) {
		Long ruleId = event.getData();

		LayerCustomPropsTabItem layersCustomPropsItem = (LayerCustomPropsTabItem) this.ruleEditorDialog
				.getTabWidget()
				.getItemByItemId(
						RuleDetailsEditDialog.RULE_LAYER_CUSTOM_PROPS_DIALOG_ID);
		final LayerCustomPropsGridWidget layerCustomPropsInfo = layersCustomPropsItem
				.getLayerCustomPropsWidget().getLayerCustomPropsInfo();

		rulesManagerServiceRemote.setDetailsProps(ruleId, layerCustomPropsInfo
				.getStore().getModels(), new AsyncCallback<Void>() {

			public void onFailure(Throwable caught) {

				Dispatcher
						.forwardEvent(
								GeofenceEvents.SEND_ERROR_MESSAGE,
								new String[] {
										I18nProvider.getMessages()
												.ruleServiceName(),
										"Error occurred while saving Rule Custom Properties!" });
			}

			public void onSuccess(Void result) {

				layerCustomPropsInfo.getStore().getLoader().load();

				Dispatcher.forwardEvent(
						GeofenceEvents.BIND_MEMBER_DISTRIBUTION_NODES, result);
				Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
						new String[] {
								I18nProvider.getMessages().ruleServiceName(),
								I18nProvider.getMessages()
										.ruleFetchSuccessMessage() });
			}
		});
	}

	/**
	 * 
	 * @param event
	 */
	private void onRuleLayerAttributesSave(AppEvent event) {
		Long ruleId = event.getData();

		LayerAttributesTabItem layerAttributesTabItem = (LayerAttributesTabItem) this.ruleEditorDialog
				.getTabWidget().getItemByItemId(
						RuleDetailsEditDialog.RULE_LAYER_ATTRIBUTES_DIALOG_ID);

		final LayerAttributesGridWidget layerAttributesInfo = layerAttributesTabItem
				.getLayerAttributesWidget().getLayerAttributesInfo();

		rulesManagerServiceRemote.setLayerAttributes(ruleId,
				layerAttributesInfo.getStore().getModels(),
				new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {

						Dispatcher
								.forwardEvent(
										GeofenceEvents.SEND_ERROR_MESSAGE,
										new String[] {
												I18nProvider.getMessages()
														.ruleServiceName(),
												"Error occurred while saving Rule Layer Attributes!" });
					}

					public void onSuccess(Void result) {

						layerAttributesInfo.clearGridElements();
						layerAttributesInfo.getStore().getLoader().load();

						Dispatcher.forwardEvent(
								GeofenceEvents.SEND_INFO_MESSAGE, new String[] {
										I18nProvider.getMessages()
												.ruleServiceName(),
										I18nProvider.getMessages()
												.ruleFetchSuccessMessage() });
					}
				});
	}

	/**
	 * On inject wkt.
	 * 
	 * @param event
	 *            the event
	 */
	private void onInjectWKT(AppEvent event) {

		final String area = (String) event.getData();
		Rule rule = ruleEditorDialog.getModel();

		if (rule != null) {
			ruleEditorDialog.reset();
			ruleEditorDialog.setModel(rule);
			ruleEditorDialog.show(false);
			
			RuleDetailsTabItem ruleDetailsTabItem = (RuleDetailsTabItem) ruleEditorDialog
					.getTabWidget().getItemByItemId(
							RuleDetailsEditDialog.RULE_DETAILS_DIALOG_ID);

			RuleLimitsTabItem ruleLimitsTabItem = (RuleLimitsTabItem) ruleEditorDialog
					.getTabWidget().getItemByItemId(
							RuleDetailsEditDialog.RULE_LIMITS_DIALOG_ID);

			if (ruleDetailsTabItem != null) {
				final RuleDetailsInfoWidget ruleDetailsInfoWidget = ruleDetailsTabItem
						.getRuleDetailsWidget().getRuleDetailsInfo();

                // ETJ: FIXME
				ruleDetailsInfoWidget.getAllowedArea().setValue("SRID=4326;" + area);
				
				rulesManagerServiceRemote.getLayerDetailsInfo(rule,
                        new AsyncCallback<LayerDetailsInfo>() {

					public void onFailure(Throwable caught) {
						Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE,
                                new String[] {  /* I18nProvider.getMessages().ruleServiceName() */"Rule Service",
                                    /* I18nProvider.getMessages().ruleFetchFailureMessage() */ "Error occurred while retrieveing rule layer details!" });
					}

					public void onSuccess(LayerDetailsInfo result) {
						result.setAllowedArea(area);
						result.setSrid("4326");
                        // ETJ: FIXME: may be binding incomplete bean
						ruleDetailsInfoWidget.bindModelData(result);
						ruleDetailsInfoWidget.getRuleDetailsWidget().enableSaveButton();						
					}
				});
			}

			if (ruleLimitsTabItem != null) {
				final RuleLimitsInfoWidget ruleLimitsInfoWidget = ruleLimitsTabItem
						.getRuleLimitsWidget().getRuleLimitsInfo();

                ruleLimitsInfoWidget.setArea(area, "4326");
				ruleLimitsInfoWidget.getRuleLimitsWidget().enableSaveButton();
			}
		}
	}
}
