=============================
 + DB Preparation
=============================

Setup the DB schema by creating a PostGIS DB and creating the base schema using the SQL script.
Refer to file setup/README.txt

=============================
 + DataSource Configuration
=============================

Update configuration files accordingly to your DB connection parameters on

  gui/web/src/main/resources
  

=============================
 + GeoServer Instance Configuration
=============================

Update GeoServer properties accordingly to your configuration by updating the file

  geoserverXXX/web-app/src/main/resources/geofence-geoserver.properties


=============================
 + Building WARs
=============================

From root directory launch the command

  mvn clean install
  
This will create 3 WARs:

  geoserver21x/web-app/target/geoserver.war
  geoserver22x/web-app/target/geoserver.war
  gui/web/target/geofence.war

