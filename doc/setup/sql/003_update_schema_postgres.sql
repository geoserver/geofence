ALTER TABLE gf_layer_details ADD COLUMN spatial_filter_type character varying(16) DEFAULT 'INTERSECT';

ALTER TABLE gf_rule_limits ADD COLUMN spatial_filter_type character varying(16) DEFAULT 'INTERSECT';