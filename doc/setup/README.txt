==================================================================================
======= Installation Overview on PostGIS
==================================================================================

Create DB as user postgres

  createdb geofence
  createlang plpgsql geofence
  psql -d geofence -f /usr/share/postgresql/9.1/contrib/postgis-1.5/postgis.sql
  psql -d geofence-f /usr/share/postgresql/9.1/contrib/postgis-1.5/spatial_ref_sys.sql
  psql -d geofence -f /usr/share/postgresql/9.1/contrib/postgis_comments.sql

Create users and schemas for test and production.
 - Run as user postgres the script 001_setup_db.sql
     psql -d geofence -f 001_setup_db.sql

Create DB schema.
 - Run 002_create_schema_postgres.sql as the desired user: 
    psql -U geofence      -f 002_create_schema_postgres.sql
    psql -U geofence_test -f 002_create_schema_postgres.sql

