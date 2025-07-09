GeoFence
==================================================

**GeoFence** is an advanced authentication/authorization engine for [GeoServer](https://www.geoserver.org) implementing the interface described in [GSIP 57](https://github.com/geoserver/geoserver/wiki/GSIP-57).

Features
--------------------------------------------------

**GeoFence** allows you to create authorization rules on GeoServer resources based on multiple parameters, such as the user requesting the data, its role, the source IP address of the web request, the used OGC service/request, the requested layer or its workspace.

You can setup authorization rules with the granularity you need: this means that you can allow or deny access to a given layer at a whole, or simply hide some attributes, restrict the output to only a given area, or only allow access to a subset of the features by filtering them using a CQL expression. 

You can find more details on [this page](https://github.com/geoserver/geofence/wiki/Main-concepts#rules).

Architecture
--------------------------------------------------

**GeoFence** can be run either as a standalone Java web application, or embedded into GeoServer.

The GeoFence **standalone application** runs as a java webapp, and can be queried for auth by one or more GeoServer instances.   
It provides a complete [REST API](https://github.com/geoserver/geofence/wiki/REST-API) for the programmatic administration of the rules and their ancillary data; a GUI is no longer provided [since version 3.7](https://github.com/geoserver/geofence/wiki/GS-GF-Compatibility-matrix).  
In this configuration GeoServer needs the [GeoFence client plugin](https://github.com/geoserver/geoserver/tree/main/src/extension/geofence/geofence) that will send authorization queries to GeoFence using a configurable protocol (by default it uses Spring remoting over HTTP).

The **embedded configuration** will make the GeoFence engine run within GeoServer itself. The administration GUI will be seamlessly embedded into GeoServer. The embedded GeoFence should be installed as a [GeoServer plugin](https://github.com/geoserver/geoserver/tree/main/src/extension/geofence/geofence-server) as well.

More info in the related [wiki page](https://github.com/geoserver/geofence/wiki/GeoFence-setup-architectures).

License
==================================================

**GeoFence** core modules and the **GeoFence** plugins in GeoServer are free and Open Source software, released under the GPL license (which is [GPL v2.0](http://www.gnu.org/licenses/old-licenses/gpl-2.0.html)), as it implements a GeoServer Java API.


Getting GeoFence
==================================================

Since there are two different ways to run **GeoFence**, you'll need different set of files according to your configuration.

- *Standalone*:  
  You'll need the <code>geofence.war</code> file, and the <code>geofence</code> plugin to be deployed into GeoServer.

- *Embedded*:  
  You'll only need to deploy the <code>geofence-server</code> plugin into GeoServer.

Since GeoFence and GeoServer run side to side, every change of the API in either side requires a change on the other one.

Here's a compatibility table for the most recent versions of both applications (you may also want to refer to the [comprehensive compatibility matrix](https://github.com/geoserver/geofence/wiki/GS-GF-Compatibility-matrix)):

| GeoFence       | GeoServer       |   Main changes                        |
|----------------|-----------------|---------------------------------------|
| 3.8.1          | 2.28.0 <br/> 2.27.2 - 2.27.x          | JDBC deps fixes          |   
| 3.8.0          | 2.27.0 - 2.27.1                       | Major libs refactoring, hibernate upgrade |   
| 3.7.2          | 2.26.1 - 2.26.x </br> 2.25.5 - 2.25.x |                 |   
| 3.7.x          | 2.26.0 <br/> 2.25.0 - 2.25.4          |
| 3.7.1          | 2.24.1 - 2.24.x                       | Fix log4j libs <br/> |
| 3.7-RC         | 2.24.0                    | GeoTools 30: opengis packages renaming <br/> JDK11 compatibility <br/> GUI removed|
| 3.6.1          | 2.23.4 - 2.23.x           | Fix log4j libs |
| [3.6.0][3.6.x] | 2.23.0 - 2.23.3           | DTO changes (#226 - subfield) |
| 3.5.1          | 2.22.0 - 2.22.x  ([client][2.22_client]) ([embedded][2.22_embedded]) <br/> 2.21.2 - 2.21.x <br/> 2.20.6 - 2.20.x| #222: Improve filtering by role <br/> #87: Exclude JPA1 dep |
| 3.5.0          | 2.21.0 - 2.21.1 <br/> 2.20.0 - 2.20.5 <br/>2.19.0 - 2.19.6  | DTO changes (#163 - restricted area, clip) |
| [3.4.6.1]      | 2.18.x <br/> 2.17.x      | #166: JTS Version update <br/> Minor DTO changes |

[main_war]: https://build.geoserver.org/geofence/nightly/main/geofence-main-latest-war.zip
[3.6.x]: https://build.geoserver.org/geofence/nightly/3.6.x/geofence-3.6.x-latest-war.zip
[3.5.x]: https://build.geoserver.org/geofence/nightly/3.5.x/geofence-3.5.x-latest-war.zip
[3.4.x]: https://build.geoserver.org/geofence/3.4.x/geofence-3.4.x-latest-war.zip
[3.4.6]: https://github.com/geoserver/geofence/releases/download/v3.4.6/geofence.war
[3.4.6.1]: https://github.com/geoserver/geofence/releases/download/v3.4.6.1/geofence.war


[2.22_client]:   https://build.geoserver.org/geoserver/2.22.x/ext-latest/geoserver-2.22-SNAPSHOT-geofence-plugin.zip
[2.22_embedded]: https://build.geoserver.org/geoserver/2.22.x/ext-latest/geoserver-2.22-SNAPSHOT-geofence-server-plugin.zip
[2.19_client]:   https://build.geoserver.org/geoserver/2.19.x/ext-latest/geoserver-2.19-SNAPSHOT-geofence-plugin.zip
[2.19_embedded]: https://build.geoserver.org/geoserver/2.19.x/ext-latest/geoserver-2.19-SNAPSHOT-geofence-server-plugin.zip

Once you have downloaded the resources you need, please follow the instructions on the [GeoFence installation](https://github.com/geoserver/geofence/wiki/GeoFence-installation) wiki page.


Documentation
==================================================
* [How to install GeoFence](https://github.com/geoserver/geofence/wiki/GeoFence-installation)
* [5 Minutes intro to using GeoFence](https://github.com/geoserver/geofence/wiki/First-steps)
* [How to configure GeoFence](https://github.com/geoserver/geofence/wiki/GeoFence-configuration)
* [How to build GeoFence](https://github.com/geoserver/geofence/wiki/Building-instructions)
* [Documentation Index](https://github.com/geoserver/geofence/wiki/Documentation-index)

Community
==================================================
The GeoFence project is part of GeoServer, so any question can be directed to the GeoServer user mailing list, and developer collaboration discussed in the GeoServer developer mailng list. 
