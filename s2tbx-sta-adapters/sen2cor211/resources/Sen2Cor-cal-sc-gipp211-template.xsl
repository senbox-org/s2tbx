<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:param name="t1B02" select="'0.18'"/>
	<xsl:param name="t2B02" select="'0.22'"/>
	<xsl:param name="t1B04" select="'0.06'"/>
	<xsl:param name="t2B04" select="'0.25'"/>
	<xsl:param name="t1B8a" select="'0.15'"/>
	<xsl:param name="t2B8a" select="'0.35'"/>
	<xsl:param name="t1B10" select="'0.012'"/>
	<xsl:param name="t2B10" select="'0.035'"/>
	<xsl:param name="t1B12" select="'0.25'"/>
	<xsl:param name="t2B12" select="'0.12'"/>
	<xsl:param name="tB02B12" select="'0.018'"/>
	<xsl:param name="tCloudLp" select="'0.0'"/>
	<xsl:param name="tCloudMp" select="'0.20'"/>
	<xsl:param name="tCloudHP" select="'0.80'"/>
	<xsl:param name="t1NdsiCld" select="'-0.24'"/>
	<xsl:param name="t2NdsiCld" select="'-0.16'"/>
	<xsl:param name="t1NdsiSnw" select="'0.35'"/>
	<xsl:param name="t2NdsiSnw" select="'0.50'"/>
	<xsl:param name="t1Ndvi" select="'0.36'"/>
	<xsl:param name="t2Ndvi" select="'0.42'"/>
	<xsl:param name="t1Snow" select="'0.12'"/>
	<xsl:param name="t2Snow" select="'0.25'"/>
	<xsl:param name="t1RB02B04" select="'0.85'"/>
	<xsl:param name="t2RB02B04" select="'0.95'"/>
	<xsl:param name="t1RB8aB03" select="'1.50'"/>
	<xsl:param name="t2RB8aB03" select="'2.50'"/>
	<xsl:param name="t1RB8aB11" select="'0.90'"/>
	<xsl:param name="t2RB8aB11" select="'1.10'"/>
	<xsl:param name="t11B02" select="'-0.40'"/>
	<xsl:param name="t12B02" select="'0.46'"/>
	<xsl:param name="t11RB02B11" select="'0.70'"/>
	<xsl:param name="t12RB02B11" select="'1.0'"/>
	<xsl:param name="t21RB02B11" select="'2.00'"/>
	<xsl:param name="t22RB02B11" select="'4.00'"/>
	<xsl:param name="t21B12" select="'0.1'"/>
	<xsl:param name="t22B12" select="'-0.09'"/>
	<xsl:param name="rvB2" select="'6.96000'"/>
	<xsl:param name="rvB3" select="'5.26667'"/>
	<xsl:param name="rvB4" select="'5.37708'"/>
	<xsl:param name="rvB8" select="'7.52000'"/>
	<xsl:param name="rvB11" select="'5.45000'"/>
	<xsl:param name="rvB12" select="'2.55000'"/>
	<xsl:param name="tSdw" select="'0.75'"/>
	<xsl:template match="/">
		<Level-2A_CAL_SC_Ground_Image_Processing_Parameter xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="L2A_CAL_SC_GIPP.xsd">
			<Scene_Classification>
				<References>
					<Snow_Map>GlobalSnowMap.tiff</Snow_Map>
					<ESACCI_WaterBodies_Map>ESACCI-LC-L4-WB-Map-150m-P13Y-2000-v4.0.tif</ESACCI_WaterBodies_Map>
					<ESACCI_LandCover_Map>ESACCI-LC-L4-LCCS-Map-300m-P1Y-2015-v2.0.7.tif</ESACCI_LandCover_Map>
					<ESACCI_SnowCondition_Map_Dir>ESACCI-LC-L4-Snow-Cond-500m-MONTHLY-2000-2012-v2.4</ESACCI_SnowCondition_Map_Dir>
				</References>
				<Classificators>
					<NO_DATA>0</NO_DATA>
					<SATURATED_DEFECTIVE>1</SATURATED_DEFECTIVE>
					<DARK_FEATURES>2</DARK_FEATURES>
					<CLOUD_SHADOWS>3</CLOUD_SHADOWS>
					<VEGETATION>4</VEGETATION>
					<NOT_VEGETATED>5</NOT_VEGETATED>
					<WATER>6</WATER>
					<UNCLASSIFIED>7</UNCLASSIFIED>
					<MEDIUM_PROBA_CLOUDS>8</MEDIUM_PROBA_CLOUDS>
					<HIGH_PROBA_CLOUDS>9</HIGH_PROBA_CLOUDS>
					<THIN_CIRRUS>10</THIN_CIRRUS>
					<SNOW_ICE>11</SNOW_ICE>
				</Classificators>
				<Thresholds>
					<T1_B02><xsl:value-of select="$t1B02"/></T1_B02>
					<T2_B02><xsl:value-of select="$t2B02"/></T2_B02>
					<T1_B04><xsl:value-of select="$t1B04"/></T1_B04>
					<!-- modif JL 20151223 (was 0.08)-->
					<T2_B04><xsl:value-of select="$t2B04"/></T2_B04>
					<T1_B8A><xsl:value-of select="$t1B8a"/></T1_B8A>
					<T2_B8A><xsl:value-of select="$t2B8a"/></T2_B8A>
					<T1_B10><xsl:value-of select="$t1B10"/></T1_B10>
					<T2_B10><xsl:value-of select="$t2B10"/></T2_B10>
					<T1_B12><xsl:value-of select="$t1B12"/></T1_B12>
					<T2_B12><xsl:value-of select="$t2B12"/></T2_B12>
					<T_B02_B12><xsl:value-of select="$tB02B12"/></T_B02_B12>
					<T_CLOUD_LP><xsl:value-of select="$tCloudLp"/></T_CLOUD_LP>
					<T_CLOUD_MP><xsl:value-of select="$tCloudMp"/></T_CLOUD_MP>
					<!-- modif JL 20151218 (was 0.35)-->
					<T_CLOUD_HP><xsl:value-of select="$tCloudHP"/></T_CLOUD_HP>
					<!-- modif JL 20151218 (was 0.65)-->
					<T1_NDSI_CLD><xsl:value-of select="$t1NdsiCld"/></T1_NDSI_CLD>
					<T2_NDSI_CLD><xsl:value-of select="$t2NdsiCld"/></T2_NDSI_CLD>
					<T1_NDSI_SNW><xsl:value-of select="$t1NdsiSnw"/></T1_NDSI_SNW>
					<!-- modif JL 20151218 (was 0.20)-->
					<T2_NDSI_SNW><xsl:value-of select="$t2NdsiSnw"/></T2_NDSI_SNW>
					<!-- modif JL 20151218 (was 0.42)-->
					<T1_NDVI><xsl:value-of select="$t1Ndvi"/></T1_NDVI>
					<T2_NDVI><xsl:value-of select="$t2Ndvi"/></T2_NDVI>
					<!-- modif JL 20151218 (was 0.47 then 0.40)-->
					<T1_SNOW><xsl:value-of select="$t1Snow"/></T1_SNOW>
					<T2_SNOW><xsl:value-of select="$t2Snow"/></T2_SNOW>
					<T1_R_B02_B04><xsl:value-of select="$t1RB02B04"/></T1_R_B02_B04>
					<T2_R_B02_B04><xsl:value-of select="$t2RB02B04"/></T2_R_B02_B04>
					<T1_R_B8A_B03><xsl:value-of select="$t1RB8aB03"/></T1_R_B8A_B03>
					<T2_R_B8A_B03><xsl:value-of select="$t2RB8aB03"/></T2_R_B8A_B03>
					<T1_R_B8A_B11><xsl:value-of select="$t1RB8aB11"/></T1_R_B8A_B11>
					<T2_R_B8A_B11><xsl:value-of select="$t2RB8aB11"/></T2_R_B8A_B11>
					<T11_B02><xsl:value-of select="$t11B02"/></T11_B02>
					<T12_B02><xsl:value-of select="$t12B02"/></T12_B02>
					<T11_R_B02_B11><xsl:value-of select="$t11RB02B11"/></T11_R_B02_B11>
					<!-- modif JL 20151218 (was 0.8 then 0.55)-->
					<T12_R_B02_B11><xsl:value-of select="$t12RB02B11"/></T12_R_B02_B11>
					<!-- modif JL 20151218 (was 1.5 then 0.8)-->
					<T21_R_B02_B11><xsl:value-of select="$t21RB02B11"/></T21_R_B02_B11>
					<T22_R_B02_B11><xsl:value-of select="$t22RB02B11"/></T22_R_B02_B11>
					<T21_B12><xsl:value-of select="$t21B12"/></T21_B12>
					<T22_B12><xsl:value-of select="$t22B12"/></T22_B12>
					<RV_B2><xsl:value-of select="$rvB2"/></RV_B2>
					<RV_B3><xsl:value-of select="$rvB3"/></RV_B3>
					<RV_B4><xsl:value-of select="$rvB4"/></RV_B4>
					<RV_B8><xsl:value-of select="$rvB8"/></RV_B8>
					<RV_B11><xsl:value-of select="$rvB11"/></RV_B11>
					<RV_B12><xsl:value-of select="$rvB12"/></RV_B12>
					<T_SDW><xsl:value-of select="$tSdw"/></T_SDW>
				</Thresholds>
			</Scene_Classification>
		</Level-2A_CAL_SC_Ground_Image_Processing_Parameter>
	</xsl:template>
</xsl:stylesheet>
