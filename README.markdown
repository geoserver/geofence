GeoFence
==================================================

**GeoFence** is a java web application that provides an advanced authentication/authorization engine for [GeoServer](http://www.geoserver.org) using the interface 
described in [GSIP 57](http://geoserver.org/display/GEOS/GSIP+57+-+Improving+GeoServer+authorization+framework).

**GeoFence** can be run either as a standalone application, or embedded into GeoServer.

The **GeoFence** *standalone application* provides a graphical user interface to administer GeoServer users and authorization rules. Furthermore, a quite complete [REST API](https://github.com/geoserver/geofence/wiki/REST-API) allows the programmatic administration of the rules and their ancillary data.  
In this configuration GeoServer needs a module (the **probe**) that will send authorization queries to GeoFence using a configurable internal protocol (by default it uses Spring remoting over HTTP).

The *embedded* configuration will make the GeoFence engine run within GeoServer itself. The administration GUI will be seamlessly embedded into GeoServer itself.


License
==================================================
**GeoFence** core modules and GUI, as well as the part of **GeoFence** that shall be installed as a module into GeoServer (either the **probe** or the **embedded** logic), are free and Open Source software, released under the [GPL v2] license,  (which is [GPL v2.0](http://www.gnu.org/licenses/old-licenses/gpl-2.0.html)), as it implements a GeoServer Java API.

Getting GeoFence
==================================================

Since there are two different ways to run **GeoFence**, you'll need different set of files according to your configuration.

<dl>
  <dt>Standalone</dt>
  <dd>You'll need the <em>GeoFence</em> .war file, and the probe module to be deployed into GeoServer.</dd>

  <dt>Embedded</dt>
  <dd>You'll only need to deploy the linked embedded module into GeoServer. The embedded version is only available starting from the 3.0 version.</dd>
</dl>


Since GeoFence and GeoServer run side to side, every change of the API in either side requires a change on the other one.
Here's a compatibility table for the versions of both applications:

| GeoFence         | GeoServer  |   Main changes                        |
|------------------|------------|---------------------------------------|
| master branch: <br/>- stable: [3.2.0] <br/>- nightly: [3.2.x] | 2.9        | Spring 4, JDK 8                       |
| 3.1.x branch:  <br/>- stable: [3.1.0] <br/>- nightly: [3.1.x] | 2.8  ([probe][2.8_probe]) ([embedded][2.8_embedded])    | Handle Workspace admin <br/> (feature for embedded version only)
| 3.0.x            | 2.8        | GeoFence embedded into GeoServer  <br/>(Only for older 2.8 releases; Not recommended)
| 2.2.x branch: <br/>- stable: [2.2.0] <br/>- nightly: [2.2.x]  | [2.7], [2.6]   | 

[3.2.0]: http://ares.boundlessgeo.com/geofence/master/release-v3.2.0-geofence-war.zip
[3.2.x]: http://ares.boundlessgeo.com/geofence/master/geofence-master-latest-war.zip
[3.1.0]: http://ares.boundlessgeo.com/geofence/master/release-v3.1.0-geofence-war.zip
[3.1.x]: http://ares.boundlessgeo.com/geofence/master
[2.2.0]: http://ares.boundlessgeo.com/geofence/2.2.x/geofence-release-v2.2.0-war.zip
[2.2.x]: http://ares.boundlessgeo.com/geofence/2.2.x/geofence-2.2.x-latest-war.zip
[2.8_probe]:    http://ares.boundlessgeo.com/geoserver/2.8.x/community-latest/geoserver-2.8-SNAPSHOT-geofence-plugin.zip
[2.8_embedded]: http://ares.boundlessgeo.com/geoserver/2.8.x/community-latest/geoserver-2.8-SNAPSHOT-geofence-server-plugin.zip
[2.7]: http://ares.boundlessgeo.com/geoserver/2.7.x/community-latest/geoserver-2.7-SNAPSHOT-geofence-plugin.zip
[2.6]: http://ares.boundlessgeo.com/geoserver/2.6.x/community-latest/geoserver-2.6-SNAPSHOT-geofence-plugin.zip


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
