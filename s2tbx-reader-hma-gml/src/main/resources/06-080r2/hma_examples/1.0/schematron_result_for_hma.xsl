<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet xmlns:atm="http://earth.esa.int/atm" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:gml="http://www.opengis.net/gml" xmlns:hma="http://earth.esa.int/hma" xmlns:ohr="http://earth.esa.int/ohr" xmlns:om="http://www.opengis.net/om" xmlns:phr="http://hma.cnes.fr/phr" xmlns:sar="http://earth.esa.int/sar" xmlns:sch="http://www.ascc.net/xml/schematron" version="1.0">
	<xsl:template match="*|@*" mode="schematron-get-full-path">
		<xsl:apply-templates select="parent::*" mode="schematron-get-full-path"/>
		<xsl:text>/</xsl:text>
		<xsl:if test="count(. | ../@*) = count(../@*)">@</xsl:if>
		<xsl:value-of select="name()"/>
		<xsl:text>[</xsl:text>
		<xsl:value-of select="1+count(preceding-sibling::*[name()=name(current())])"/>
		<xsl:text>]</xsl:text>
	</xsl:template>
	<xsl:key match="author" name="author-e-mails" use=""/>
	<xsl:template match="/">
		<xsl:apply-templates select="/" mode="M2"/>
		<xsl:apply-templates select="/" mode="M3"/>
		<xsl:apply-templates select="/" mode="M4"/>
		<xsl:apply-templates select="/" mode="M5"/>
		<xsl:apply-templates select="/" mode="M6"/>
		<xsl:apply-templates select="/" mode="M7"/>
		<xsl:apply-templates select="/" mode="M8"/>
		<xsl:apply-templates select="/" mode="M9"/>
		<xsl:apply-templates select="/" mode="M10"/>
		<xsl:apply-templates select="/" mode="M11"/>
		<xsl:apply-templates select="/" mode="M12"/>
		<xsl:apply-templates select="/" mode="M13"/>
		<xsl:apply-templates select="/" mode="M14"/>
		<xsl:apply-templates select="/" mode="M15"/>
	</xsl:template>
	<xsl:template match="(hma:EarthObservation/gml:metaDataProperty)|(ohr:EarthObservation/gml:metaDataProperty)|(sar:EarthObservation/gml:metaDataProperty)|(atm:EarthObservation/gml:metaDataProperty)" priority="4000" mode="M2">
		<xsl:choose>
			<xsl:when test="hma:EarthObservationMetaData"/>
			<xsl:otherwise>gml:metaDataProperty : expected content is hma:EarthObservationMetadata for a hma:EarthObservation, ohr:EarthObservation, sar:EarthObservation or atm:EarthObservation</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates mode="M2"/>
	</xsl:template>
	<xsl:template match="text()" priority="-1" mode="M2"/>
	<xsl:template match="*[@hma:type = 'hma:EarthObservation' or @hma:type = 'ohr:EarthObservation' or @hma:type = 'sar:EarthObservation' or @hma:type = 'atm:EarthObservation']/gml:metaDataProperty" priority="4000" mode="M3">
		<xsl:choose>
			<xsl:when test="hma:EarthObservationMetaData|*[@hma:type= 'hma:EarthObservationMetaData']"/>
			<xsl:otherwise>gml:metaDataProperty : expected content is hma:EarthObservationMetadata or an extension (with appropriate attribute hma:type)</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates mode="M3"/>
	</xsl:template>
	<xsl:template match="text()" priority="-1" mode="M3"/>
	<xsl:template match="gml:using" priority="4000" mode="M4">
		<xsl:choose>
			<xsl:when test="hma:EarthObservationEquipment"/>
			<xsl:otherwise>gml:using : expected contents is hma:EarthObservationEquipment</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates mode="M4"/>
	</xsl:template>
	<xsl:template match="text()" priority="-1" mode="M4"/>
	<xsl:template match="ohr:EarthObservation/gml:using/hma:EarthObservationEquipment/hma:acquisitionParameters" priority="4000" mode="M5">
		<xsl:choose>
			<xsl:when test="ohr:Acquisition"/>
			<xsl:otherwise>ohr:EarthObservationEquipment: expected of contents hma:acquisitionParameters is ohr:Acquisition</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates mode="M5"/>
	</xsl:template>
	<xsl:template match="text()" priority="-1" mode="M5"/>
	<xsl:template match="sar:EarthObservation/gml:using/hma:EarthObservationEquipment/hma:acquisitionParameters" priority="4000" mode="M6">
		<xsl:choose>
			<xsl:when test="sar:Acquisition"/>
			<xsl:otherwise>sar:EarthObservationEquipment: expected of contents hma:acquisitionParameters is sar:Acquisition</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates mode="M6"/>
	</xsl:template>
	<xsl:template match="text()" priority="-1" mode="M6"/>
	<xsl:template match="hma:EarthObservation/gml:resultOf" priority="4000" mode="M7">
		<xsl:choose>
			<xsl:when test="hma:EarthObservationResult"/>
			<xsl:otherwise>gml:resultOf : expected content is hma:EarthObservationResult for a hma:EarthObservation</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates mode="M7"/>
	</xsl:template>
	<xsl:template match="text()" priority="-1" mode="M7"/>
	<xsl:template match="ohr:EarthObservation/gml:resultOf" priority="4000" mode="M8">
		<xsl:choose>
			<xsl:when test="ohr:EarthObservationResult"/>
			<xsl:otherwise>gml:resultOf : expected content is ohr:EarthObservationResult for a ohr:EarthObservation</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates mode="M8"/>
	</xsl:template>
	<xsl:template match="text()" priority="-1" mode="M8"/>
	<xsl:template match="*[@hma:type = 'hma:EarthObservation']/gml:resultOf" priority="4000" mode="M9">
		<xsl:choose>
			<xsl:when test="hma:EarthObservationResult|*[@hma:type= 'hma:EarthObservationResult']"/>
			<xsl:otherwise>gml:resultOf : expected content is hma:EarthObservationResult or an extension (with appropriate attribute hma:type)</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates mode="M9"/>
	</xsl:template>
	<xsl:template match="text()" priority="-1" mode="M9"/>
	<xsl:template match="*[@hma:type = 'ohr:EarthObservation']/gml:resultOf" priority="4000" mode="M10">
		<xsl:choose>
			<xsl:when test="ohr:EarthObservationResult|*[@hma:type= 'ohr:EarthObservationResult']"/>
			<xsl:otherwise>gml:resultOf : expected content is ohr:EarthObservationResult or an extension (with appropriate attribute hma:type)</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates mode="M10"/>
	</xsl:template>
	<xsl:template match="text()" priority="-1" mode="M10"/>
	<xsl:template match="gml:target" priority="4000" mode="M11">
		<xsl:choose>
			<xsl:when test="hma:Footprint"/>
			<xsl:otherwise>gml:target : expected contents is hma:Footprint</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates mode="M11"/>
	</xsl:template>
	<xsl:template match="text()" priority="-1" mode="M11"/>
	<xsl:template match="gml:validTime" priority="4000" mode="M12">
		<xsl:choose>
			<xsl:when test="gml:TimePeriod/gml:beginPosition"/>
			<xsl:otherwise>gml:validTime : expected contents is gml:TimePeriod/gml:beginPostition</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates mode="M12"/>
	</xsl:template>
	<xsl:template match="text()" priority="-1" mode="M12"/>
	<xsl:template match="gml:validTime" priority="4000" mode="M13">
		<xsl:choose>
			<xsl:when test="gml:TimePeriod/gml:endPosition"/>
			<xsl:otherwise>gml:validTime : expected contents is gml:TimePeriod/gml:endPostition</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates mode="M13"/>
	</xsl:template>
	<xsl:template match="text()" priority="-1" mode="M13"/>
	<xsl:template match="gml:multiExtentOf" priority="4000" mode="M14">
		<xsl:choose>
			<xsl:when test="gml:MultiSurface/gml:surfaceMembers/gml:Polygon/gml:exterior/gml:LinearRing/gml:posList"/>
			<xsl:otherwise>gml:extentOf : expected contents is gml:MultiSurface/gml:surfaceMembers/gml:Polygon/gml:exterior/gml:LinearRing/gml:posList</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates mode="M14"/>
	</xsl:template>
	<xsl:template match="text()" priority="-1" mode="M14"/>
	<xsl:template match="gml:centerOf" priority="4000" mode="M15">
		<xsl:choose>
			<xsl:when test="gml:Point/gml:pos"/>
			<xsl:otherwise>gml:centerOf : expected contents is gml:Point/gml:pos</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates mode="M15"/>
	</xsl:template>
	<xsl:template match="text()" priority="-1" mode="M15"/>
	<xsl:template match="text()" priority="-1"/>
</xsl:stylesheet>