OpenGIS(r) WCS schema - ReadMe.txt
==================================

OpenGIS Web Coverage Service (WCS) Implementation Standard

The OpenGIS Web Coverage Service Interface Standard (WCS) defines a standard
interface and operations that enables interoperable access to geospatial
"coverages". The term "grid coverages" typically refers to content such as
satellite images, digital aerial photos, digital elevation data, and other
phenomena represented by values at each measurement point.

More information on the OGC WCS standard may be found at
 http://www.opengeospatial.org/standards/wcs

The most current schema are available at http://schemas.opengis.net/ .

-----------------------------------------------------------------------

2012-07-21  Stephan Meissl
  * v2.0: Updates according to WCS 2.0.1
    + Updated xsd:schema/@version to 2.0.1
    + Adjusted examples to updated schema.
    + Corrected type of "SlicePoint", "TrimLow", and "TrimHigh" elements to 
    "string".
    + Added "format" and "mediaType" elements to "GetCoverageType".
    + Added reference to "CoverageSubtypeParent" to "CoverageSummaryType".
    + Added reference to "Extension" element to "ContentsType".
    + Added "CoverageSubtypeParent" and "Extension" elements and corresponding 
      type definitions to wcsCommon.xsd
    + Added "nativeFormat" element and reference to "CoverageSubtypeParent" 
      element to "ServiceParametersType".
    + Corrected "ServiceMetadataType" to not extend "ows:CapabilitiesBaseType".
    + Added "formatSupported" element to "ServiceMetadataType".
    + Corrected "Extension" element in "RequestBaseType", "ServiceMetadataType",
      and "ServiceParametersType".
    + Including all schema files in wcsAll.xsd
    + Added wcsAll.xsd as the all-components document

2012-07-21  Kevin Stegemoller
  * v1.0 - v1.1.0: WARNING XLink change is NOT BACKWARD COMPATIBLE.
  * Changed OGC XLink (xlink:simpleLink) to W3C XLink (xlink:simpleAttrs)
    per an approved TC and PC motion during the Dec. 2011 Brussels meeting.
    See http://www.opengeospatial.org/blog/1597 
  * wcs/1.1/GMLprofileForWCS: XLink changes 
  * v1.1: No Changes
  * v1.0.0, v1.1.0, v2.0: Per 11-025, all leaf documents of a namespace shall 
    retroactively and explicitly require/add an <include/> of the all-components schema.
  * v1.1.0: Updated xsd:schema/@version to 1.1.0.2
  * v1.0.0: Updated xsd:schema/@version to 1.0.0.3
  * v1.0.0: Added wcsAll.xsd as the all-components document
  * v1.0 - v1.1.0: Removed date from xs:schema/xs:annotation/xs:appinfo
  * wcs/1.1/GMLprofileForWCS: Updated xsd:schema/@version to 1.1.4 (3.1.1.2)
  * v1.0 - v2.0: Updated copyright

2010-11-05  Peter Baumann
  * v2.0: added WCS 2.0.0 (OGC 09-110r3)

2010-02-01  Kevin Stegemoller
  * v1.1, v1.1.0, 1.0.0:
    + updated xsd:schema/@version attribute (06-135r7 s#13.4)
    + update relative schema imports to absolute URLs (06-135r7 s#15)
    + update/verify copyright (06-135r7 s#3.2)
    + add archives (.zip) files of previous versions
    + create/update ReadMe.txt (06-135r7 s#17)

2008-12-05
  * v1.1: Changes made to WCS 1.1.2
  * v1.1: The Web Coverage Service (WCS) Implementation Standard version
    1.1.2 is defined in the OGC document 07-067r5.  WCS 1.1.2 is amended
    in the OGC Corrigendum document 07-066r5 .
  * v1.1.0: wcs/1.1.0 is deprecated. The directory only remains to 
    preserve wcs/1.1.0/owcsAll.xsd .

2008-01-30
  * v1.0.0: add missing wcs/1.0.0/OGC-exception.xsd

2007-09-06  Kevin Stegemoller
  * v1.0.0: Updated ReadMe.txt
  * v1.0.0: Included OGC-exception.xsd from spec

2007-02-07  Arliss Whiteside, Steven Keens
  * v1.1: added WCS 1.1.0 (OGC 06-083r8)

2006-10-11  Arliss Whiteside, Steven Keens
  * v1.0.0: The XML Schema Documents for the OpenGIS(r) Web Services
  (OWS) Common Implementation Specification version 1.0.0 (OGC 05-008)
  that are modified for use in WCS 1.1 have been moved into this WCS/1.1.0
  directory and modified to use the www.opengis.net/wcs/1.1.0/ows
  namespace identifier.
     The following XML Schema Document is essentially unchanged:
       owsGetCapabilities.xsd
     The following XML Schema Documents are changed some:
       owsServiceIdentification.xsd
       owsOperationsMetadata.xsd
       owsDataIdentification.xsd
     The following XML Schema Documents are added by WCS 1.1.0:
       owsDomainType.xsd
       owsManifest.xsd
       owsCoverages.xsd
       owsInterpolationMethod.xsd

2005-11-22  Arliss Whiteside
  * v1.0.0: This set of XML Documents for WCS Version 1.0.0 has been
  edited to reflect the corrigendum to document OGC 03-065r6 that is
  specified in document OGC 05-076 plus the corrigendum based on the 
  change requests: 
    OGC 05-068r1 "Store xlinks.xsd file at a fixed location"
    OGC 05-081r2 "Change to use relative paths"

2003-07-31
  * v1.0.0: add WCS 1.0.0 (OGC 03-065r6)

 Note: check each OGC numbered document for detailed changes.

-----------------------------------------------------------------------

Policies, Procedures, Terms, and Conditions of OGC(r) are available
  http://www.opengeospatial.org/ogc/legal/ .

Copyright (c) 2010 Open Geospatial Consortium.

-----------------------------------------------------------------------

