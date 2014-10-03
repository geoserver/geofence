-- Run this script inside a connection to the existing "geofence" database. 
-- If geofence does not exist, this command will terminate the sql script evaluation

\c geofence

-- CREATE SCHEMA geofence
CREATE user geofence LOGIN PASSWORD 'geofence' NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE;

CREATE SCHEMA geofence;

GRANT USAGE ON SCHEMA geofence TO geofence;
GRANT ALL ON SCHEMA geofence TO geofence;

GRANT SELECT ON public.spatial_ref_sys to geofence;
GRANT SELECT,INSERT,DELETE ON public.geometry_columns to geofence;

alter user geofence set search_path to geofence, public;

-- CREATE SCHEMA geofence_test
CREATE user geofence_test LOGIN PASSWORD 'geofence_test' NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE;

CREATE SCHEMA geofence_test;

GRANT USAGE ON SCHEMA geofence_test TO geofence_test;
GRANT ALL ON SCHEMA geofence_test TO geofence_test;

GRANT SELECT ON public.spatial_ref_sys to geofence_test;
GRANT SELECT,INSERT,DELETE ON public.geometry_columns to geofence_test;

alter user geofence_test set search_path to geofence_test, public;