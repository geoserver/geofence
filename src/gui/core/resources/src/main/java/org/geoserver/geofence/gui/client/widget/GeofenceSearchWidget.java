/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import org.geoserver.geofence.gui.client.widget.SearchStatus.EnumSearchStatus;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;

// TODO: Auto-generated Javadoc
/**
 * The Class GeofenceSearchWidget.
 * 
 * @param <T>
 *            the generic type
 */
public abstract class GeofenceSearchWidget<T extends BaseModel> extends Window {

    /** The vp. */
    private VerticalPanel vp;

    /** The form panel. */
    protected FormPanel formPanel;

    /** The store. */
    protected ListStore<T> store;

    /** The grid. */
    protected Grid<T> grid;

    /** The search. */
    protected TextField<String> search;

    /** The proxy. */
    protected RpcProxy<PagingLoadResult<T>> proxy;

    /** The loader. */
    protected PagingLoader<PagingLoadResult<ModelData>> loader;

    /** The tool bar. */
    protected PagingToolBar toolBar;

    /** The select. */
    protected Button select;

    /** The cancel. */
    protected Button cancel;

    /** The search status. */
    protected SearchStatus searchStatus;

    /** The search text. */
    protected String searchText;

    /**
     * Instantiates a new geo repo search widget.
     */
    public GeofenceSearchWidget() {
        initWindow();
        initVerticalPanel();
        initFormPanel();
        add(vp);
    }

    /**
     * Inits the window.
     */
    private void initWindow() {
        setModal(true);
        setResizable(false);
        setLayout(new FlowLayout());
        setPlain(true);
        setMaximizable(false);

        addWindowListener(new WindowListener() {

            @Override
            public void windowHide(WindowEvent we) {
                cancel();
            }

        });

        setWindowProperties();
    }

    /**
     * Inits the vertical panel.
     */
    private void initVerticalPanel() {
        vp = new VerticalPanel();
        vp.setSpacing(10);
        createStore();
        initGrid();
    }

    /**
     * Inits the form panel.
     */
    private void initFormPanel() {
        formPanel = new FormPanel();
        formPanel.setHeaderVisible(false);
        formPanel.setFrame(true);
        formPanel.setLayout(new FlowLayout());

        FieldSet searchFieldSet = new FieldSet();
        searchFieldSet.setHeading("Search");

        FormLayout layout = new FormLayout();
        layout.setLabelWidth(80);
        searchFieldSet.setLayout(layout);

        search = new TextField<String>();
        search.setFieldLabel("Find");

        search.addKeyListener(new KeyListener() {

            @Override
            public void componentKeyUp(ComponentEvent event) {
                if ((event.getKeyCode() == 8) && (search.getValue() == null)) {
                    reset();
                    loader.load(0, 25);
                }
            }

            @Override
            public void componentKeyPress(ComponentEvent event) {
                if ((event.getKeyCode() == 13)) {
                    // && (!search.getValue().equals(""))) {
                    // searchStatus.setBusy("Connection to the Server");
                    if (search.getValue() != null) {
                        searchText = search.getValue() == null ? "" : search.getValue();
                        loader.load(0, 25);
                    }
                }
            }

        });

        BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER);
        data.setMargins(new Margins(5, 5, 5, 5));

        searchFieldSet.add(search, data);

        formPanel.add(searchFieldSet);

        formPanel.add(this.grid);

        this.searchStatus = new SearchStatus();
        searchStatus.setAutoWidth(true);

        formPanel.getButtonBar().add(this.searchStatus);

        formPanel.getButtonBar().add(new LabelToolItem("    "));

        formPanel.setButtonAlign(HorizontalAlignment.RIGHT);

        select = new Button("Select", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                select();
            }
        });

        select.setIconStyle("x-geofence-select");
        select.disable();

        formPanel.addButton(this.select);

        cancel = new Button("Cancel", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                cancel();
            }
        });

        cancel.setIconStyle("x-geofence-cancel");

        formPanel.addButton(cancel);

        formPanel.setBottomComponent(this.toolBar);

        vp.add(formPanel);
    }

    /**
     * Inits the grid.
     */
    private void initGrid() {
        ColumnModel cm = prepareColumnModel();

        grid = new Grid<T>(store, cm);
        grid.setBorders(true);

        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        grid.addListener(Events.CellClick, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                if (!grid.getSelectionModel().getSelection().isEmpty())
                    select.enable();
                else
                    select.disable();
            }
        });

        grid.addListener(Events.CellDoubleClick, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                select();
            }
        });

        setGridProperties();
    }

    /**
     * Cancel.
     */
    @SuppressWarnings("deprecation")
    public void cancel() {
        super.close();
        reset();
    }

    /**
     * Reset.
     */
    public void reset() {
        this.search.reset();
        this.store.removeAll();
        this.toolBar.clear();
        this.select.disable();
        this.searchStatus.clearStatus("");
    }

    /**
     * Clear grid elements.
     */
    public void clearGridElements() {
        this.store.removeAll();
        this.toolBar.clear();
    }

    /**
     * Sets the search status.
     * 
     * @param status
     *            the status
     * @param message
     *            the message
     */
    public void setSearchStatus(EnumSearchStatus status, EnumSearchStatus message) {
        this.searchStatus.setIconStyle(status.getValue());
        this.searchStatus.setText(message.getValue());
    }

    /**
     * Sets the window properties.
     */
    public abstract void setWindowProperties();

    /**
     * Creates the store.
     */
    public abstract void createStore();

    /**
     * Sets the grid properties.
     */
    public abstract void setGridProperties();

    /**
     * Prepare column model.
     * 
     * @return the column model
     */
    public abstract ColumnModel prepareColumnModel();

    /**
     * Select.
     */
    public abstract void select();

}
