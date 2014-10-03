/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

import org.geoserver.geofence.gui.client.GeofenceEvents;


// TODO: Auto-generated Javadoc
/**
 * The Class UserSearchComponent.
 */
public class UserSearchComponent
{

    /** The form panel. */
    private FormPanel formPanel;

    /** The search. */
    private Button search;

    /**
     * Instantiates a new user search component.
     */
    public UserSearchComponent()
    {
        this.createFormPanel();
    }

    /**
     * Creates the form panel.
     */
    private void createFormPanel()
    {
        formPanel = new FormPanel();
        formPanel.setFrame(true);
        formPanel.setHeaderVisible(false);
        formPanel.setAutoHeight(true);

        FieldSet fieldSet = new FieldSet();
        fieldSet.setHeading("Search Management");
        fieldSet.setCheckboxToggle(false);
        fieldSet.setCollapsible(false);

        FormLayout layout = new FormLayout();
        fieldSet.setLayout(layout);

        search = new Button("Search", new SelectionListener<ButtonEvent>()
                {

                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        Dispatcher.forwardEvent(GeofenceEvents.SHOW_SEARCH_USER_WIDGET);
                    }
                });

        ButtonBar bar = new ButtonBar();
        bar.setAlignment(HorizontalAlignment.CENTER);

        bar.add(search);

        Button p = new Button("get AOIs");

        Button q = new Button("get Features");

        bar.add(p);
        bar.add(q);

        fieldSet.add(bar);

        formPanel.add(fieldSet);
    }

    /**
     * Gets the form panel.
     *
     * @return the form panel
     */
    public FormPanel getFormPanel()
    {
        return formPanel;
    }

}
