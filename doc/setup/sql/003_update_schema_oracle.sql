    alter table gr_layer_details add (
        spatial_filter_type varchar2(16 char) default 'INTERSECT'
    );

    alter table gr_rule_limits add (
        spatial_filter_type varchar2(16 char) default 'INTERSECT'
    );
