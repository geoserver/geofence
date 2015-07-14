GeoFence
==================================================

**GeoFence** is a java web application that provides an advanced authentication/authorization engine for [GeoServer](http://www.geoserver.org) using the interface 
described in [GSIP 57](http://geoserver.org/display/GEOS/GSIP+57+-+Improving+GeoServer+authorization+framework).

**GeoFence** provides a graphical user interface to administer GeoServer users and authorization rules. Furthermore, a quite complete [REST API](https://github.com/geoserver/geofence/wiki/REST-API) allows the programmatic administration of the rules and their ancillary data.

Once integrated with **GeoFence**, GeoServer will send authorization queries to GeoFence using a configurable internal protocol (by default it uses Spring remoting over HTTP).

License
==================================================
**GeoFence** core modules and GUI, as well as the part of **GeoFence** that shall be installed as a module into GeoServer (the **probe**), are free and Open Source software, released under the [GPL v2] license,  (which is [GPL v2.0](http://www.gnu.org/licenses/old-licenses/gpl-2.0.html)), as it implements a GeoServer Java API.

Getting GeoFence
==================================================

You will need the **GeoFence** .war file, and the probe module to be installed into your GeoServer instance.

This is the list of the currently available resources for using **GeoFence**:

Stable
------

Stable branch of GeoFence (2.2.x) is compatible with GeoServer 2.6 and 2.7.

### Release

* Latest stable release: [GeoFence 2.2.0 **.war** ]
(http://ares.boundlessgeo.com/geofence/2.2.x/geofence-release-v2.2.0-war.zip)

### Nightly builds

* [Latest GeoFence 2.2.x **.war** ]
(http://ares.boundlessgeo.com/geofence/2.2.x/geofence-2.2.x-latest-war.zip)
* [Latest GeoFence **probe** for GeoServer **2.7.x**](http://ares.boundlessgeo.com/geoserver/2.7.x/community-latest/geoserver-2.7-SNAPSHOT-geofence-plugin.zip)
* [Latest GeoFence **probe** for GeoServer **2.6.x**](http://ares.boundlessgeo.com/geoserver/2.6.x/community-latest/geoserver-2.6-SNAPSHOT-geofence-plugin.zip)

Master
------
Master branch of GeoFence (3.0) is compatible with GeoServer master (2.8).

* [Latest GeoFence **probe** for GeoServer master **2.8.x**](http://ares.boundlessgeo.com/geoserver/master/community-latest/geoserver-2.8-SNAPSHOT-geofence-plugin.zip)

* [Latest GeoFence 3.0 **.war** file](http://ares.boundlessgeo.com/geofence/master/geofence-master-latest-war.zip)



Once you have downloaded the resources you need, please follow the instructions on the [GeoFence installation] (https://github.com/geoserver/geofence/wiki/GeoFence-installation) wiki page.


Documentation
==================================================
* [How to install GeoFence](https://github.com/geoserver/geofence/wiki/GeoFence-installation)
* [5 Minutes intro to using GeoFence](https://github.com/geoserver/geofence/wiki/First-steps)
* [How to configure GeoFence](https://github.com/geoserver/geofence/wiki/GeoFence-configuration)
* [How to build GeoFence](https://github.com/geoserver/geofence/wiki/Building-instructions)
* [Documentation Index](https://github.com/geoserver/geofence/wiki/Documentation-index)

Community
==================================================
The GeoFence project is now part of GeoServer, so any question can be directed to the GeoServer user mailing list, and developer collaboration discussed in the GeoServer developer mailng list. 
