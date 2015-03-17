<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="schematron_skeleton_for_eop.xsl"?>
<schema xmlns="http://www.ascc.net/xml/schematron" xmlns:phr="http://eop.cnes.fr/phr" xmlns:gml="http://www.opengis.net/gml" xmlns:eop="http://earth.esa.int/eop" xmlns:opt="http://earth.esa.int/opt" xmlns:atm="http://earth.esa.int/atm" xmlns:sar="http://earth.esa.int/sar" xmlns:alt="http://earth.esa.int/alt" xmlns:lmb="http://earth.esa.int/lmb" xmlns:ssp="http://earth.esa.int/ssp"  xmlns:om="http://www.opengis.net/om">
	<!--
	Earth Observation Metadata profile of Observations and Measurements is an OGC Standard.
	Copyright (c) 2013 Open Geospatial Consortium. 
	To obtain additional rights of use, visit http://www.opengeospatial.org/legal/ .
	
	Version: 2.0.0
	-->
	<title>Technical document schema</title>
	<key name="author-e-mails" match="author" use="@e-mail"/>
	<!-- ============================================================== -->
	<!--  eop:metaDataProperty :  -->
	<!--  	+ expected contents is eop:EarthObservationMetadata  -->
	<!-- ============================================================== -->
	<!--
	  ! if root is 'eop:EarthObservation' , content is always as expected in the eop level (i.e no extension is possible)
	  ! Since  'opt:EarthObservation' does not extend metadataProperty, expected content is the same as with eop
	  ! Note : should be the same for other thematic schemas
	  !-->
	<pattern id="metaDataProperty_strict" name="metaDataProperty_strict">
		<rule context="(eop:EarthObservation/eop:metaDataProperty)|(opt:EarthObservation/eop:metaDataProperty)|(sar:EarthObservation/eop:metaDataProperty)|(atm:EarthObservation/eop:metaDataProperty)|(alt:EarthObservation/eop:metaDataProperty)|(lmb:EarthObservation/eop:metaDataProperty)|(ssp:EarthObservation/eop:metaDataProperty)">
			<assert test="eop:EarthObservationMetaData">eop:metaDataProperty : expected content is eop:EarthObservationMetadata for a eop:EarthObservation, opt:EarthObservation, sar:EarthObservation, atm:EarthObservation, alt:EarthObservation, lmb:EarthObservation or ssp:EarthObservation</assert>
		</rule>
	</pattern>
	<!--
	  ! if root explicitaly refers to  'eop:EarthObservation' or one of the thematic root element, content can be as in the preceding rule or can be an
	  ! extension of eop:EarthObservationMetadata
	  !-->
	<pattern id="metaDataProperty_extended" name="metaDataProperty_extended">
		<rule context="*[@eop:type = 'eop:EarthObservation' or @eop:type = 'opt:EarthObservation' or @eop:type = 'sar:EarthObservation' or @eop:type = 'atm:EarthObservation' or @eop:type = 'alt:EarthObservation' or @eop:type = 'lmb:EarthObservation' or @eop:type = 'ssp:EarthObservation']/eop:metaDataProperty">
			<assert test="eop:EarthObservationMetaData|*[@eop:type= 'eop:EarthObservationMetaData']">eop:metaDataProperty : expected content is eop:EarthObservationMetadata or an extension (with appropriate attribute eop:type)</assert>
		</rule>
	</pattern>
	
	
	<!-- ============================================================== -->
	<!--  om:procedure :  -->
	<!--  	+ expected contents is eop:EarthObservationEquipment. -->
	<!-- ============================================================== -->
	<pattern id="using" name="using">
		<rule context="om:procedure">
			<assert test="eop:EarthObservationEquipment">om:procedure : expected contents is eop:EarthObservationEquipment</assert>
		</rule>
	</pattern>

	
	<!-- ============================================================== -->
	<!--  eop:EarthObservationEquipment in opt :  -->
	<!--  	+ expected contents of eop:acquisitionParameters is opt:Acquisition. -->
	<!-- ============================================================== -->
	<pattern id="opt_acquisition" name="opt_acquisition">
		<rule context="opt:EarthObservation/om:procedure/eop:EarthObservationEquipment/eop:acquisitionParameters">
			<assert test="opt:Acquisition">opt:EarthObservationEquipment: expected of contents eop:acquisitionParameters is opt:Acquisition</assert>
		</rule>
	</pattern>


	<!-- ============================================================== -->
	<!--  eop:EarthObservationEquipment in sar :  -->
	<!--  	+ expected contents of eop:acquisitionParameters is opt:Acquisition. -->
	<!-- ============================================================== -->
	<pattern id="sar_acquisition" name="sar_acquisition">
		<rule context="sar:EarthObservation/om:procedure/eop:EarthObservationEquipment/eop:acquisitionParameters">
			<assert test="sar:Acquisition">sar:EarthObservationEquipment: expected of contents eop:acquisitionParameters is sar:Acquisition</assert>
		</rule>
	</pattern>
	
	<!-- ============================================================== -->
	<!--  eop:EarthObservationEquipment in alt :  -->
	<!--  	+ expected contents of eop:acquisitionParameters is alt:Acquisition. -->
	<!-- ============================================================== -->
	<pattern id="alt_acquisition" name="alt_acquisition">
		<rule context="alt:EarthObservation/om:procedure/eop:EarthObservationEquipment/eop:acquisitionParameters">
			<assert test="alt:Acquisition">alt:EarthObservationEquipment: expected of contents eop:acquisitionParameters is alt:Acquisition</assert>
		</rule>
	</pattern>
	
    <!-- ============================================================== -->
	<!--  eop:EarthObservationEquipment in lmb :  -->
	<!--  	+ expected contents of eop:acquisitionParameters is lmb:Acquisition. -->
	<!-- ============================================================== -->
	<pattern id="lmb_acquisition" name="lmb_acquisition">
		<rule context="lmb:EarthObservation/om:procedure/eop:EarthObservationEquipment/eop:acquisitionParameters">
			<assert test="lmb:Acquisition">lmb:EarthObservationEquipment: expected of contents eop:acquisitionParameters is lmb:Acquisition</assert>
		</rule>
	</pattern>
	
	
	

	<!-- ============================================================== -->
	<!--  om:result :  -->
	<!--  	+ expected contents is eop:EarthObservationResult. -->
	<!-- ============================================================== -->
	<!--
	  ! 
	  !-->
	<pattern id="result_strict" name="result_strict">
		<rule context="eop:EarthObservation/om:result">
			<assert test="eop:EarthObservationResult">om:result : expected content is eop:EarthObservationResult for a eop:EarthObservation</assert>
		</rule>
	</pattern>
	<pattern id="opt_result_strict" name="opt_result_strict">
		<rule context="opt:EarthObservation/om:result">
			<assert test="opt:EarthObservationResult">om:result : expected content is opt:EarthObservationResult for a opt:EarthObservation</assert>
		</rule>
	</pattern>
	<pattern id="atm_result_strict" name="atm_result_strict">
		<rule context="atm:EarthObservation/om:result">
			<assert test="atm:EarthObservationResult">om:result : expected content is atm:EarthObservationResult for a atm:EarthObservation</assert>
		</rule>
	</pattern>
	
	
	<!--
	  ! 
	  !-->
	<pattern id="result_extended" name="result_extended">
		<rule context="*[@eop:type = 'eop:EarthObservation']/om:result">
			<assert test="eop:EarthObservationResult|*[@eop:type= 'eop:EarthObservationResult']">om:result : expected content is eop:EarthObservationResult or an extension (with appropriate attribute eop:type)</assert>
		</rule>
	</pattern>
	<pattern id="opt_result_extended" name="opt_result_extended">
		<rule context="*[@eop:type = 'opt:EarthObservation']/om:result">
			<assert test="opt:EarthObservationResult|*[@eop:type= 'opt:EarthObservationResult']">om:result : expected content is opt:EarthObservationResult or an extension (with appropriate attribute eop:type)</assert>
		</rule>
	</pattern>
    <pattern id="atm_result_extended" name="atm_result_extended">
		<rule context="*[@eop:type = 'atm:EarthObservation']/om:result">
			<assert test="atm:EarthObservationResult|*[@eop:type= 'atm:EarthObservationResult']">om:result : expected content is atm:EarthObservationResult or an extension (with appropriate attribute eop:type)</assert>
		</rule>
	</pattern>
	<!-- ============================================================== -->
	<!--  gml:target :  -->
	<!--  	+ expected contents is eop:Footprint. -->
	<!-- ============================================================== -->
	<pattern id="eop_featureOfInterest" name="eop_featureOfInterest">
		<rule context="eop:EarthObservation/om:featureOfInterest">
			<assert test="eop:Footprint">om:featureOfInterest : expected contents is eop:Footprint</assert>
		</rule>
	</pattern>
		<pattern id="alt_featureOfInterest" name="alt_featureOfInterest">
		<rule context="eop:EarthObservation/om:featureOfInterest">
			<assert test="alt:Footprint">om:featureOfInterest : expected contents is alt:Footprint</assert>
		</rule>
	</pattern>
    <pattern id="lmb_featureOfInterest" name="lmb_featureOfInterest">
		<rule context="eop:EarthObservation/om:featureOfInterest">
			<assert test="lmb:Footprint">om:featureOfInterest : expected contents is lmb:Footprint</assert>
		</rule>
	</pattern>
	<pattern id="ssp_featureOfInterest" name="ssp_featureOfInterest">
		<rule context="eop:EarthObservation/om:featureOfInterest">
			<assert test="ssp:Footprint">om:featureOfInterest : expected contents is ssp:Footprint</assert>
		</rule>
	</pattern>
	<!-- ============================================================== -->
	<!--  om:phenomenonTime :  -->
	<!--  	+ expected contents is gml:TimePeriod/gml:beginPosition.   -->
	<!--  	                                      gml:TimePeriod/gml:endPosition.   -->
	<!-- ============================================================== -->
	<pattern id="validTime_beginPosition" name="validTime_beginPosition">
		<rule context="om:phenomenonTime">
			<assert test="gml:TimePeriod/gml:beginPosition">gml:validTime : expected contents is gml:TimePeriod/gml:beginPosition</assert>
		</rule>
	</pattern>
	<pattern id="validTime_endPosition" name="validTime_endPosition">
		<rule context="om:phenomenonTime">
			<assert test="gml:TimePeriod/gml:endPosition">gml:validTime : expected contents is gml:TimePeriod/gml:endPosition</assert>
		</rule>
	</pattern>
	<!-- ============================================================== -->
	<!--  Footprint eop:multiExtentOf :  -->
	<!--  	+ expected contents is gml:MultiSurface/gml:surfaceMembers/gml:Polygon/gml:exterior/gml:LinearRing/gml:posList. -->
	<!-- ============================================================== -->
	<pattern id="footprint_extentOf" name="footprint_extentOf">
		<rule context="eop:multiExtentOf">
			<assert test="gml:MultiSurface/gml:surfaceMembers/gml:Polygon/gml:exterior/gml:LinearRing/gml:posList">eop:multiExtentOf: expected contents is gml:Polygon/gml:exterior/gml:LinearRing/gml:posList</assert>
		</rule>
	</pattern>
	<!-- ============================================================== -->
	<!--  Footprint eop:centerOf :  -->
	<!--  	+ expected contents is gml:Point/gml:pos. -->
	<!-- ============================================================== -->
	<pattern id="footprint_centerOf" name="footprint_centerOf">
		<rule context="eop:centerOf">
			<assert test="gml:Point/gml:pos">eop:centerOf : expected contents is gml:Point/gml:pos</assert>
		</rule>
	</pattern>
</schema>
