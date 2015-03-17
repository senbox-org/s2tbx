OGC(r) Observations and Measurements XML ReadMe.txt
===================================================

OGC 07-022r1 defines the GML Application Schema version of the O&M 1.0.0 model. 
OGC 10-025r1 defines the Observations and Measurements XML (OMXML) 2.0 standard.

More information may be found at 
 http://www.opengeospatial.org/standards/om

The most current schema are available at http://schemas.opengis.net/ .

-----------------------------------------------------------------------

2012-07-21  Kevin Stegemoller
  * v1.0: Per 11-025, all leaf documents of a namespace shall retroactively
    and explicitly require/add an <include/> of the all-components schema.
  * v1.0: included om.xsd as the all-components document (06-135r11 #14)
  * v1.0/extensions: included om_extended.xsd as the all-components document (06-135r11 #14)
  * v1.0: xsd:schema/@version unchanged but version 1.0.3 noted in XML comment.
  * v1.0 - 2.0: Updated ISO 19139 schemaLocation to utilize the ISO ITTF repository

2011-03-22  Simon Cox
  * v2.0.0: Published om/2.0 from OGC 10-025r1
  * v2.0.0: Published sampling/2.0 from OGC 10-025r1
  * v2.0.0: Published samplingSpatial/2.0 from OGC 10-025r1
  * v2.0.0: Published samplingSpecimen/2.0 from OGC 10-025r1

2010-02-15  Simon Cox
  * v1.0.0: reverted xsd:schema/@version attribute to 1.0.0 to align version with
    path and documentation.  The @version attribute for 1.0.1 will not be used.

2010-01-29  Kevin Stegemoller 
  * v1.0.0: update/verify copyright (06-135r7 s#3.2)
  * v1.0.0: update relative schema imports to absolute URLs (06-135r7 s#15)
  * v1.0.0: updated xsd:schema/@version attribute (06-135r7 s#13.4)
  * v1.0.0: add archives (.zip) files of previous versions
  * v1.0.0: create/update ReadMe.txt (06-135r7 s#17)

2008-02-18  Simon Cox
  * The Observation Extensions schema were moved from here to
    http://schemas.opengis.net/omx/1.0.0/ and also moved into a separate
    namespace 'http://www.opengis.net/omx/1.0' per OGC 08-022r1.

2008-02-07  Simon Cox
  * v1.0.0: extensions/observationSpecialization_constraint.xsd: 
    fix namespace for swe in sch namespace prefix binding
  * v1.0.0: extensions/om_extended.xsd: fix om namespace (unused)
    see attached unified diff

2007-10-05  Simon Cox
  * Published om/1.0.0 schemas from 07-022r1

-----------------------------------------------------------------------

Policies, Procedures, Terms, and Conditions of OGC(r) are available
  http://www.opengeospatial.org/ogc/legal/ .

Copyright (c) 2012 Open Geospatial Consortium.

-----------------------------------------------------------------------

#######################################################################
# 2008-02-07 unified diff fix for om/1.0.0
Index: extensions/om_extended.xsd
===================================================================
--- extensions/om_extended.xsd	(revision 3013)
+++ extensions/om_extended.xsd	(working copy)
@@ -1,5 +1,5 @@
 <?xml version="1.0" encoding="UTF-8"?>
-<schema xmlns="http://www.w3.org/2001/XMLSchema" xmlns:om="http://www.opengis.net/om/1.0.1" targetNamespace="http://www.opengis.net/om/1.0" elementFormDefault="qualified" attributeFormDefault="unqualified" version="1.0.0">
+<schema xmlns="http://www.w3.org/2001/XMLSchema" xmlns:om="http://www.opengis.net/om/1.0" targetNamespace="http://www.opengis.net/om/1.0" elementFormDefault="qualified" attributeFormDefault="unqualified" version="1.0.0">
 	<annotation>
 		<documentation>om.xsd
 
Index: extensions/observationSpecialization_constraint.xsd
===================================================================
--- extensions/observationSpecialization_constraint.xsd	(revision 3013)
+++ extensions/observationSpecialization_constraint.xsd	(working copy)
@@ -11,7 +11,7 @@
 			<sch:title>Schematron validation</sch:title>
 			<sch:ns prefix="gml" uri="http://www.opengis.net/gml"/>
 			<sch:ns prefix="om" uri="http://www.opengis.net/om/1.0"/>
-			<sch:ns prefix="swe" uri="http://www.opengis.net/swe/1.0"/>
+			<sch:ns prefix="swe" uri="http://www.opengis.net/swe/1.0.1"/>
 			<sch:ns prefix="xlink" uri="http://www.w3.org/1999/xlink"/>
 			<sch:ns prefix="xs" uri="http://www.w3.org/2001/XMLSchema"/>
 			<sch:ns prefix="xsi" uri="http://www.w3.org/2001/XMLSchema-instance"/>

