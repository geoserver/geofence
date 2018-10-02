set search_path to geofence;

-- CLEAN-UP
--drop table gf_gfuser cascade;
--drop table gf_rule_limits;
--drop table gf_layer_styles;
--drop table gf_layer_custom_props ;
--drop table gf_layer_attributes;
--drop table gf_layer_details;
--drop table gf_rule cascade;
--drop table gf_gsuser cascade;
--drop table gf_gsinstance cascade;
--drop table gf_user_usergroups;
--drop table gf_usergroup cascade;

--drop sequence hibernate_sequence;

-- TABLE CREATION
CREATE TABLE gf_adminrule (
    id bigint NOT NULL,
    grant_type character varying(255) NOT NULL,
    ip_high bigint,
    ip_low bigint,
    ip_size integer,
    priority bigint NOT NULL,
    rolename character varying(255),
    username character varying(255),
    workspace character varying(255),
    instance_id bigint
);

CREATE TABLE gf_gfuser (
    id bigint NOT NULL,
    datecreation timestamp without time zone,
    emailaddress character varying(255),
    enabled boolean NOT NULL,
    extid character varying(255),
    fullname character varying(255),
    name character varying(255) NOT NULL,
    password character varying(255)
);

CREATE TABLE gf_gsinstance (
    id bigint NOT NULL,
    baseurl character varying(255) NOT NULL,
    datecreation timestamp without time zone,
    description character varying(255),
    name character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    username character varying(255) NOT NULL
);

CREATE TABLE gf_gsuser (
    id bigint NOT NULL,
    admin boolean NOT NULL,
    datecreation timestamp without time zone,
    emailaddress character varying(255),
    enabled boolean NOT NULL,
    extid character varying(255),
    fullname character varying(255),
    name character varying(255) NOT NULL,
    password character varying(255)
);

CREATE TABLE gf_layer_attributes (
    details_id bigint NOT NULL,
    access_type character varying(255),
    data_type character varying(255),
    name character varying(255) NOT NULL
);

CREATE TABLE gf_layer_details (
    id bigint NOT NULL,
    area geometry,
    catalog_mode character varying(255),
    cqlfilterread character varying(4000),
    cqlfilterwrite character varying(4000),
    defaultstyle character varying(255),
    type character varying(255),
    rule_id bigint NOT NULL
);

CREATE TABLE gf_layer_styles (
    details_id bigint NOT NULL,
    stylename character varying(255)
);

CREATE TABLE gf_rule (
    id bigint NOT NULL,
    grant_type character varying(255) NOT NULL,
    ip_high bigint,
    ip_low bigint,
    ip_size integer,
    layer character varying(255),
    priority bigint NOT NULL,
    request character varying(255),
    rolename character varying(255),
    service character varying(255),
    username character varying(255),
    workspace character varying(255),
    instance_id bigint
);

CREATE TABLE gf_rule_limits (
    id bigint NOT NULL,
    area geometry,
    catalog_mode character varying(255),
    rule_id bigint NOT NULL
);

CREATE TABLE gf_user_usergroups (
    user_id bigint NOT NULL,
    group_id bigint NOT NULL
);

CREATE TABLE gf_usergroup (
    id bigint NOT NULL,
    datecreation timestamp without time zone,
    enabled boolean NOT NULL,
    extid character varying(255),
    name character varying(255) NOT NULL
);

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

    
ALTER TABLE ONLY gf_adminrule
    ADD CONSTRAINT gf_adminrule_pkey PRIMARY KEY (id);
ALTER TABLE ONLY gf_adminrule
    ADD CONSTRAINT gf_adminrule_username_rolename_instance_id_workspace_key UNIQUE (username, rolename, instance_id, workspace);
ALTER TABLE ONLY gf_gfuser
    ADD CONSTRAINT gf_gfuser_extid_key UNIQUE (extid);
ALTER TABLE ONLY gf_gfuser
    ADD CONSTRAINT gf_gfuser_name_key UNIQUE (name);
ALTER TABLE ONLY gf_gfuser
    ADD CONSTRAINT gf_gfuser_pkey PRIMARY KEY (id);
ALTER TABLE ONLY gf_gsinstance
    ADD CONSTRAINT gf_gsinstance_pkey PRIMARY KEY (id);
ALTER TABLE ONLY gf_gsuser
    ADD CONSTRAINT gf_gsuser_extid_key UNIQUE (extid);
ALTER TABLE ONLY gf_gsuser
    ADD CONSTRAINT gf_gsuser_name_key UNIQUE (name);
ALTER TABLE ONLY gf_gsuser
    ADD CONSTRAINT gf_gsuser_pkey PRIMARY KEY (id);
ALTER TABLE ONLY gf_layer_attributes
    ADD CONSTRAINT gf_layer_attributes_pkey PRIMARY KEY (details_id, name);
ALTER TABLE ONLY gf_layer_details
    ADD CONSTRAINT gf_layer_details_pkey PRIMARY KEY (id);
ALTER TABLE ONLY gf_layer_details
    ADD CONSTRAINT gf_layer_details_rule_id_key UNIQUE (rule_id);
ALTER TABLE ONLY gf_rule_limits
    ADD CONSTRAINT gf_rule_limits_pkey PRIMARY KEY (id);
ALTER TABLE ONLY gf_rule_limits
    ADD CONSTRAINT gf_rule_limits_rule_id_key UNIQUE (rule_id);
ALTER TABLE ONLY gf_rule
    ADD CONSTRAINT gf_rule_pkey PRIMARY KEY (id);
ALTER TABLE ONLY gf_rule
    ADD CONSTRAINT gf_rule_username_rolename_instance_id_service_request_works_key UNIQUE (username, rolename, instance_id, service, request, workspace, layer);
ALTER TABLE ONLY gf_user_usergroups
    ADD CONSTRAINT gf_user_usergroups_pkey PRIMARY KEY (user_id, group_id);
ALTER TABLE ONLY gf_usergroup
    ADD CONSTRAINT gf_usergroup_extid_key UNIQUE (extid);
ALTER TABLE ONLY gf_usergroup
    ADD CONSTRAINT gf_usergroup_name_key UNIQUE (name);
ALTER TABLE ONLY gf_usergroup
    ADD CONSTRAINT gf_usergroup_pkey PRIMARY KEY (id);

CREATE INDEX idx_adminrule_priority ON gf_adminrule USING btree (priority);
CREATE INDEX idx_adminrule_workspace ON gf_adminrule USING btree (workspace);
CREATE INDEX idx_gsuser_name ON gf_gsuser USING btree (name);
CREATE INDEX idx_rule_layer ON gf_rule USING btree (layer);
CREATE INDEX idx_rule_priority ON gf_rule USING btree (priority);
CREATE INDEX idx_rule_request ON gf_rule USING btree (request);
CREATE INDEX idx_rule_service ON gf_rule USING btree (service);
CREATE INDEX idx_rule_workspace ON gf_rule USING btree (workspace);

ALTER TABLE ONLY gf_adminrule
    ADD CONSTRAINT fk_adminrule_instance FOREIGN KEY (instance_id) REFERENCES gf_gsinstance(id);
ALTER TABLE ONLY gf_layer_attributes
    ADD CONSTRAINT fk_attribute_layer FOREIGN KEY (details_id) REFERENCES gf_layer_details(id);
ALTER TABLE ONLY gf_layer_details
    ADD CONSTRAINT fk_details_rule FOREIGN KEY (rule_id) REFERENCES gf_rule(id);
ALTER TABLE ONLY gf_rule_limits
    ADD CONSTRAINT fk_limits_rule FOREIGN KEY (rule_id) REFERENCES gf_rule(id);
ALTER TABLE ONLY gf_rule
    ADD CONSTRAINT fk_rule_instance FOREIGN KEY (instance_id) REFERENCES gf_gsinstance(id);
ALTER TABLE ONLY gf_layer_styles
    ADD CONSTRAINT fk_styles_layer FOREIGN KEY (details_id) REFERENCES gf_layer_details(id);
ALTER TABLE ONLY gf_user_usergroups
    ADD CONSTRAINT fk_uug_group FOREIGN KEY (group_id) REFERENCES gf_usergroup(id);
ALTER TABLE ONLY gf_user_usergroups
    ADD CONSTRAINT fk_uug_user FOREIGN KEY (user_id) REFERENCES gf_gsuser(id);

--GRANTS
alter table gf_gfuser owner to geofence;
alter table gf_rule_limits owner to geofence;
alter table gf_layer_styles owner to geofence;
alter table gf_layer_attributes owner to geofence;
alter table gf_layer_details owner to geofence;
alter table gf_rule owner to geofence;
alter table gf_gsuser owner to geofence;
alter table gf_gsinstance owner to geofence;
alter table gf_user_usergroups owner to geofence;
alter table gf_usergroup owner to geofence;

alter sequence hibernate_sequence owner to geofence;

--DEFAULTS
insert into gf_gfuser(id, datecreation, emailaddress, enabled, extid, fullname, "name", "password") values (0, 'now', null, true, 0, 'admin', 'admin', '21232f297a57a5a743894ae4a801fc3');
