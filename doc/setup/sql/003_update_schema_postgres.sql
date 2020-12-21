ALTER TABLE gf_layer_details ADD COLUMN spatial_filter_type character varying(255) DEFAULT 'INTERSECTS';

ALTER TABLE gf_rule_limits ADD COLUMN spatial_filter_type character varying(255) DEFAULT 'INTERSECTS';