OGC(r) SWE Common schema - ReadMe.txt
=========================================

OGC(r) Sensor Web Enablement (SWE) Common Data Model
-----------------------------------------------------------------------

The Sensor Web Enablement (SWE) Common Data Model Encoding Standard
defines low level data models for exchanging sensor related data
between nodes of the OGC Sensor Web Enablement (SWE) framework. These
models allow applications and/or servers to structure, encode and
transmit sensor datasets in a self describing and semantically enabled
way.

More information may be found at
 http://www.opengeospatial.org/standards/swecommon

The most current schema are available at http://schemas.opengis.net/ .

-----------------------------------------------------------------------

2012-07-21  Kevin Stegemoller
  * v1.0 - v2.0: WARNING XLink change is NOT BACKWARD COMPATIBLE.
  * Changed OGC XLink (xlink:simpleLink) to W3C XLink (xlink:simpleAttrs)
    per an approved TC and PC motion during the Dec. 2011 Brussels meeting.
    See http://www.opengeospatial.org/blog/1597
  * v1.0 - v2.0: Per 11-025, all leaf documents of a namespace shall retroactively
    and explicitly require/add an <include/> of the all-components schema.
  * v1.0 - v2.0: Included swe.xsd as the all-components document (06-135r11 #14)
  * v1.0.0: Updated xsd:schema/@version to 1.0.0.2 (06-135r11 s#13.4)
  * v1.0.1: Updated xsd:schema/@version to 1.0.1.2 (06-135r11 s#13.4)
  * v2.0: Updated xsd:schema/@version to 2.0.1 (06-135r11 s#13.4)

2011-01-06  Alexandre Robin
  * v2.0.0: Published sweCommon/2.0 from OGC 08-094r1

2010-02-01  Kevin Stegemoller
   * v1.0.1, v1.0.0:
    + Updated xsd:schema/@version attribute (06-135r7 s#13.4)
    + Update relative schema imports to absolute URLs (06-135r7 s#15)
    + Update/verify copyright (06-135r7 s#3.2)
    + Add archives (.zip) files of previous versions
    + Create/update ReadMe.txt (06-135r7 s#17)

2007-11-12  Kevin Stegemoller
  * v1.0.1: Published sweCommon/1.0.1 from OGC 07-000 + 07-122r2
  * v1.0.1: Added copyright statement
  * v1.0.1: Minor documentation changes
  * See ChangeLog.txt for additional details

2007-07-25  Mike Botts
  * Released sensorML 1.0.0 (OGC 07-000), ic 2.0 and sweCommon 1.0.0 (from OGC 07-000)
  * SensorML/1.0.0 (OGC 07-000) references ic/2.0 and sweCommon/1.0.0 (from OGC 07-000)
  * See ChangeLog.txt for additional details

-----------------------------------------------------------------------

Policies, Procedures, Terms, and Conditions of OGC(r) are available
  http://www.opengeospatial.org/ogc/legal/ .

Copyright (c) 2011 Open Geospatial Consortium.

-----------------------------------------------------------------------
