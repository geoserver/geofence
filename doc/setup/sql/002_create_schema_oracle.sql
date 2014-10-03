
    create table gr_gsinstance (
        id number(19,0) not null,
        baseURL varchar2(255 char) not null,
        dateCreation timestamp,
        description varchar2(255 char),
        name varchar2(255 char) not null,
        password varchar2(255 char) not null,
        username varchar2(255 char) not null,
        primary key (id)
    );

    create table gr_gsuser (
        id number(19,0) not null,
        admin number(1,0) not null,
        allowedArea MDSYS.SDO_GEOMETRY,
        dateCreation timestamp,
        emailAddress varchar2(255 char),
        enabled number(1,0) not null,
        extId varchar2(255 char) unique,
        fullName varchar2(255 char),
        name varchar2(255 char) not null unique,
        password varchar2(255 char),
        profile_id number(19,0) not null,
        primary key (id)
    );

    create table gr_layer_attributes (
        details_id number(19,0) not null,
        access_type varchar2(255 char),
        data_type varchar2(255 char),
        name varchar2(255 char) not null,
        primary key (details_id, name),
        unique (details_id, name)
    );

    create table gr_layer_custom_props (
        details_id number(19,0) not null,
        propvalue varchar2(255 char),
        propkey varchar2(255 char),
        primary key (details_id, propkey)
    );

    create table gr_layer_details (
        id number(19,0) not null,
        area MDSYS.SDO_GEOMETRY,
        cqlFilterRead varchar2(4000 char),
        cqlFilterWrite varchar2(4000 char),
        defaultStyle varchar2(255 char),
        type varchar2(255 char),
        rule_id number(19,0) not null,
        primary key (id),
        unique (rule_id)
    );

    create table gr_layer_styles (
        details_id number(19,0) not null,
        styleName varchar2(255 char)
    );

    create table gr_profile (
        id number(19,0) not null,
        dateCreation timestamp,
        enabled number(1,0) not null,
        extId varchar2(255 char) unique,
        name varchar2(255 char) not null unique,
        primary key (id)
    );

    create table gr_profile_custom_props (
        profile_id number(19,0) not null,
        propvalue varchar2(255 char),
        propkey varchar2(255 char),
        primary key (profile_id, propkey)
    );

    create table gr_rule (
        id number(19,0) not null,
        grant_type varchar2(255 char) not null,
        layer varchar2(255 char),
        priority number(19,0) not null,
        request varchar2(255 char),
        service varchar2(255 char),
        workspace varchar2(255 char),
        gsuser_id number(19,0),
        instance_id number(19,0),
        profile_id number(19,0),
        primary key (id),
        unique (gsuser_id, profile_id, instance_id, service, request, workspace, layer)
    );

    create table gr_rule_limits (
        id number(19,0) not null,
        area MDSYS.SDO_GEOMETRY,
        rule_id number(19,0) not null,
        primary key (id),
        unique (rule_id)
    );

    alter table gr_gsuser 
        add constraint fk_user_profile 
        foreign key (profile_id) 
        references gr_profile;

    alter table gr_layer_attributes 
        add constraint fk_attribute_layer 
        foreign key (details_id) 
        references gr_layer_details;

    alter table gr_layer_custom_props 
        add constraint fk_custom_layer 
        foreign key (details_id) 
        references gr_layer_details;

    alter table gr_layer_details 
        add constraint fk_details_rule 
        foreign key (rule_id) 
        references gr_rule;

    alter table gr_layer_styles 
        add constraint fk_styles_layer 
        foreign key (details_id) 
        references gr_layer_details;

    alter table gr_profile_custom_props 
        add constraint fk_custom_profile 
        foreign key (profile_id) 
        references gr_profile;

    create index idx_rule_request on gr_rule (request);

    create index idx_rule_layer on gr_rule (layer);

    create index idx_rule_service on gr_rule (service);

    create index idx_rule_workspace on gr_rule (workspace);

    create index idx_rule_priority on gr_rule (priority);

    alter table gr_rule 
        add constraint fk_rule_user 
        foreign key (gsuser_id) 
        references gr_gsuser;

    alter table gr_rule 
        add constraint fk_rule_profile 
        foreign key (profile_id) 
        references gr_profile;

    alter table gr_rule 
        add constraint fk_rule_instance 
        foreign key (instance_id) 
        references gr_gsinstance;

    alter table gr_rule_limits 
        add constraint fk_limits_rule 
        foreign key (rule_id) 
        references gr_rule;

    create sequence hibernate_sequence;
