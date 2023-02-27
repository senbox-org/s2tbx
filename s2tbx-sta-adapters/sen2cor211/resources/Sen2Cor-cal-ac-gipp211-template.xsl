<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:param name="scalingLimiter" select="'TRUE'"/>
	<xsl:param name="scalingDisabler" select="'TRUE'"/>
	<xsl:param name="rhoRetrievalStep2" select="'TRUE'"/>
	<xsl:param name="fwhmBandB1" select="'0.020'"/>
	<xsl:param name="fwhmBandB2" select="'0.065'"/>
	<xsl:param name="fwhmBandB3" select="'0.035'"/>
	<xsl:param name="fwhmBandB4" select="'0.030'"/>
	<xsl:param name="fwhmBandB5" select="'0.015'"/>
	<xsl:param name="fwhmBandB6" select="'0.015'"/>
	<xsl:param name="fwhmBandB7" select="'0.020'"/>
	<xsl:param name="fwhmBandB8" select="'0.115'"/>
	<xsl:param name="fwhmBandB8A" select="'0.020'"/>
	<xsl:param name="fwhmBandB9" select="'0.020'"/>
	<xsl:param name="fwhmBandB10" select="'0.030'"/>
	<xsl:param name="fwhmBandB11" select="'0.090'"/>
	<xsl:param name="fwhmBandB12" select="'0.180'"/>
	<xsl:param name="minScBlu" select="'0.9'"/>
	<xsl:param name="maxScBlu" select="'1.1'"/>
	<xsl:param name="wavelengthBandB1" select="'0.443'"/>
	<xsl:param name="wavelengthBandB1C0" select="'0.00000'"/>
	<xsl:param name="wavelengthBandB1C1" select="'0.001'"/>
	<xsl:param name="wavelengthBandB2" select="'0.490'"/>
	<xsl:param name="wavelengthBandB2C0" select="'0.00000'"/>
	<xsl:param name="wavelengthBandB2C1" select="'0.001'"/>
	<xsl:param name="wavelengthBandB3" select="'0.560'"/>
	<xsl:param name="wavelengthBandB3C0" select="'0.00000'"/>
	<xsl:param name="wavelengthBandB3C1" select="'0.001'"/>
	<xsl:param name="wavelengthBandB4" select="'0.665'"/>
	<xsl:param name="wavelengthBandB4C0" select="'0.00000'"/>
	<xsl:param name="wavelengthBandB4C1" select="'0.001'"/>
	<xsl:param name="wavelengthBandB5" select="'0.705'"/>
	<xsl:param name="wavelengthBandB5C0" select="'0.00000'"/>
	<xsl:param name="wavelengthBandB5C1" select="'0.001'"/>
	<xsl:param name="wavelengthBandB6" select="'0.740'"/>
	<xsl:param name="wavelengthBandB6C0" select="'0.00000'"/>
	<xsl:param name="wavelengthBandB6C1" select="'0.001'"/>
	<xsl:param name="wavelengthBandB7" select="'0.783'"/>
	<xsl:param name="wavelengthBandB7C0" select="'0.00000'"/>
	<xsl:param name="wavelengthBandB7C1" select="'0.001'"/>
	<xsl:param name="wavelengthBandB8" select="'0.842'"/>
	<xsl:param name="wavelengthBandB8C0" select="'0.00000'"/>
	<xsl:param name="wavelengthBandB8C1" select="'0.001'"/>
	<xsl:param name="wavelengthBandB8A" select="'0.865'"/>
	<xsl:param name="wavelengthBandB8AC0" select="'0.00000'"/>
	<xsl:param name="wavelengthBandB8AC1" select="'0.001'"/>
	<xsl:param name="wavelengthBandB9" select="'0.945'"/>
	<xsl:param name="wavelengthBandB9C0" select="'0.00000'"/>
	<xsl:param name="wavelengthBandB9C1" select="'0.001'"/>
	<xsl:param name="wavelengthBandB10" select="'1.375'"/>
	<xsl:param name="wavelengthBandB10C0" select="'0.00000'"/>
	<xsl:param name="wavelengthBandB10C1" select="'0.0005'"/>
	<xsl:param name="wavelengthBandB11" select="'1.610'"/>
	<xsl:param name="wavelengthBandB11C0" select="'0.00000'"/>
	<xsl:param name="wavelengthBandB11C1" select="'0.0002'"/>
	<xsl:param name="wavelengthBandB12" select="'2.190'"/>
	<xsl:param name="wavelengthBandB12C0" select="'0.00000'"/>
	<xsl:param name="wavelengthBandB12C1" select="'0.00005'"/>
	<xsl:param name="ACMinDdvArea" select="'2.0'"/>
	<xsl:param name="ACSwirReflLowerTh" select="'0.01'"/>
	<xsl:param name="ACSwir22umRedReflRatio" select="'0.5'"/>
	<xsl:param name="ACRedBlueReflRatio" select="'0.5'"/>
	<xsl:param name="ACCutOffAotIterVegetation" select="'0.01'"/>
	<xsl:param name="ACCutOffAotIterWater" select="'-0.005'"/>
	<xsl:param name="ACAerosolTypeRatioTh" select="'0.05'"/>
	<xsl:param name="ACTopoCorrTh" select="'0.01'"/>
	<xsl:param name="ACSlopeTh" select="'6.0'"/>
	<xsl:param name="ACDemP2pVal" select="'50.0'"/>
	<xsl:param name="ACSwirReflNdviTh" select="'0.10'"/>
	<xsl:param name="ACDdvSwirReflTh1" select="'0.05'"/>
	<xsl:param name="ACDdvSwirReflTh2" select="'0.10'"/>
	<xsl:param name="ACDdvSwirReflTh3" select="'0.12'"/>
	<xsl:param name="ACDdv16umReflTh1" select="'0.10'"/>
	<xsl:param name="ACDdv16umReflTh2" select="'0.15'"/>
	<xsl:param name="ACDdv16umReflTh3" select="'0.18'"/>
	<xsl:param name="ACDbvNirReflTh" select="'0.35'"/>
	<xsl:param name="ACDbvNdviTh" select="'0.66'"/>
	<xsl:param name="ACRedRefReflTh" select="'0.02'"/>
	<xsl:param name="ACDbvRedVegetTstNdviTh" select="'0.40'"/>
	<xsl:param name="ACDbvRedVegetReflTh" select="'0.15'"/>
	<xsl:param name="ACWvIterStartSummer" select="'1.0'"/>
	<xsl:param name="ACWvIterStartWinter" select="'0.4'"/>
	<xsl:param name="ACRngNbhdTerrainCorr" select="'0.5'"/>
	<xsl:param name="ACMaxNrTopoIter" select="'3'"/>
	<xsl:param name="ACTopoCorrCutoff" select="'1.5'"/>
	<xsl:param name="ACVegetationIndexTh" select="'3.0'"/>
	<xsl:param name="ACLimitAreaPathRadScale" select="'0'"/>
	<xsl:param name="ACDdvSmootingWindow" select="'1.0'"/>
	<xsl:param name="ACTerrainReflStart" select="'0.1'"/>
	<xsl:param name="ACSprReflPercentage" select="'0.25'"/>
	<xsl:param name="ACSprReflPromille" select="'0.3'"/>
	<xsl:template match="/">
		<Level-2A_CAL_AC_Ground_Image_Processing_Parameter xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="L2A_CAL_AC_GIPP.xsd">
			<Flags>
				<Scaling_Limiter><xsl:value-of select="$scalingLimiter"/></Scaling_Limiter>
				<Scaling_Disabler><xsl:value-of select="$scalingDisabler"/></Scaling_Disabler>
				<Rho_Retrieval_Step2><xsl:value-of select="$rhoRetrievalStep2"/></Rho_Retrieval_Step2>
			</Flags>
			<References>
				<Lib_Dir>lib</Lib_Dir>
			</References>
			<Sensor>
				<Resolution>
					<Band_List>
						<!-- full width half maximum -->
						<fwhm><xsl:value-of select="$fwhmBandB1"/></fwhm>
						<fwhm><xsl:value-of select="$fwhmBandB2"/></fwhm>
						<fwhm><xsl:value-of select="$fwhmBandB3"/></fwhm>
						<fwhm><xsl:value-of select="$fwhmBandB4"/></fwhm>
						<fwhm><xsl:value-of select="$fwhmBandB5"/></fwhm>
						<fwhm><xsl:value-of select="$fwhmBandB6"/></fwhm>
						<fwhm><xsl:value-of select="$fwhmBandB7"/></fwhm>
						<fwhm><xsl:value-of select="$fwhmBandB8"/></fwhm>
						<fwhm><xsl:value-of select="$fwhmBandB8A"/></fwhm>
						<fwhm><xsl:value-of select="$fwhmBandB9"/></fwhm>
						<fwhm><xsl:value-of select="$fwhmBandB10"/></fwhm>
						<fwhm><xsl:value-of select="$fwhmBandB11"/></fwhm>
						<fwhm><xsl:value-of select="$fwhmBandB12"/></fwhm>
					</Band_List>
				</Resolution>
				<Calibration>
					<min_sc_blu><xsl:value-of select="$minScBlu"/></min_sc_blu>
					<max_sc_blu><xsl:value-of select="$maxScBlu"/></max_sc_blu>
					<Band_List>
						<wavelength c0="{$wavelengthBandB1C0}" c1="{$wavelengthBandB1C1}"><xsl:value-of select="$wavelengthBandB1"/></wavelength>
						<wavelength c0="{$wavelengthBandB2C0}" c1="{$wavelengthBandB2C1}"><xsl:value-of select="$wavelengthBandB2"/></wavelength>
						<wavelength c0="{$wavelengthBandB3C0}" c1="{$wavelengthBandB3C1}"><xsl:value-of select="$wavelengthBandB3"/></wavelength>
						<wavelength c0="{$wavelengthBandB4C0}" c1="{$wavelengthBandB4C1}"><xsl:value-of select="$wavelengthBandB4"/></wavelength>
						<wavelength c0="{$wavelengthBandB5C0}" c1="{$wavelengthBandB5C1}"><xsl:value-of select="$wavelengthBandB5"/></wavelength>
						<wavelength c0="{$wavelengthBandB6C0}" c1="{$wavelengthBandB6C1}"><xsl:value-of select="$wavelengthBandB6"/></wavelength>
						<wavelength c0="{$wavelengthBandB7C0}" c1="{$wavelengthBandB7C1}"><xsl:value-of select="$wavelengthBandB7"/></wavelength>
						<wavelength c0="{$wavelengthBandB8C0}" c1="{$wavelengthBandB8C1}"><xsl:value-of select="$wavelengthBandB8"/></wavelength>
						<wavelength c0="{$wavelengthBandB8AC0}" c1="{$wavelengthBandB8AC1}"><xsl:value-of select="$wavelengthBandB8A"/></wavelength>
						<wavelength c0="{$wavelengthBandB9C0}" c1="{$wavelengthBandB9C1}"><xsl:value-of select="$wavelengthBandB9"/></wavelength>
						<wavelength c0="{$wavelengthBandB10C0}" c1="{$wavelengthBandB10C1}"><xsl:value-of select="$wavelengthBandB10"/></wavelength>
						<wavelength c0="{$wavelengthBandB11C0}" c1="{$wavelengthBandB11C1}"><xsl:value-of select="$wavelengthBandB11"/></wavelength>
						<wavelength c0="{$wavelengthBandB12C0}" c1="{$wavelengthBandB12C1}"><xsl:value-of select="$wavelengthBandB12"/></wavelength>
					</Band_List>
				</Calibration>
			</Sensor>
			<ACL_Prio_1>
				<AC_Min_Ddv_Area><xsl:value-of select="$ACMinDdvArea"/></AC_Min_Ddv_Area>
				<AC_Swir_Refl_Lower_Th><xsl:value-of select="$ACSwirReflLowerTh"/></AC_Swir_Refl_Lower_Th>
				<AC_Swir_22um_Red_Refl_Ratio><xsl:value-of select="$ACSwir22umRedReflRatio"/></AC_Swir_22um_Red_Refl_Ratio>
				<AC_Red_Blue_Refl_Ratio><xsl:value-of select="$ACRedBlueReflRatio"/></AC_Red_Blue_Refl_Ratio>
				<AC_Cut_Off_Aot_Iter_Vegetation><xsl:value-of select="$ACCutOffAotIterVegetation"/></AC_Cut_Off_Aot_Iter_Vegetation>
				<AC_Cut_Off_Aot_Iter_Water><xsl:value-of select="$ACCutOffAotIterWater"/></AC_Cut_Off_Aot_Iter_Water>
				<AC_Aerosol_Type_Ratio_Th><xsl:value-of select="$ACAerosolTypeRatioTh"/></AC_Aerosol_Type_Ratio_Th>
				<AC_Topo_Corr_Th><xsl:value-of select="$ACTopoCorrTh"/></AC_Topo_Corr_Th>
				<AC_Slope_Th><xsl:value-of select="$ACSlopeTh"/></AC_Slope_Th>
				<AC_Dem_P2p_Val><xsl:value-of select="$ACDemP2pVal"/></AC_Dem_P2p_Val>
			</ACL_Prio_1>
			<ACL_Prio_2>
				<AC_Swir_Refl_Ndvi_Th><xsl:value-of select="$ACSwirReflNdviTh"/></AC_Swir_Refl_Ndvi_Th>
				<AC_Ddv_Swir_Refl_Th1><xsl:value-of select="$ACDdvSwirReflTh1"/></AC_Ddv_Swir_Refl_Th1>
				<AC_Ddv_Swir_Refl_Th2><xsl:value-of select="$ACDdvSwirReflTh2"/></AC_Ddv_Swir_Refl_Th2>
				<AC_Ddv_Swir_Refl_Th3><xsl:value-of select="$ACDdvSwirReflTh3"/></AC_Ddv_Swir_Refl_Th3>
				<AC_Ddv_16um_Refl_Th1><xsl:value-of select="$ACDdv16umReflTh1"/></AC_Ddv_16um_Refl_Th1>
				<AC_Ddv_16um_Refl_Th2><xsl:value-of select="$ACDdv16umReflTh2"/></AC_Ddv_16um_Refl_Th2>
				<AC_Ddv_16um_Refl_Th3><xsl:value-of select="$ACDdv16umReflTh3"/></AC_Ddv_16um_Refl_Th3>
				<AC_Dbv_Nir_Refl_Th><xsl:value-of select="$ACDbvNirReflTh"/></AC_Dbv_Nir_Refl_Th>
				<AC_Dbv_Ndvi_Th><xsl:value-of select="$ACDbvNdviTh"/></AC_Dbv_Ndvi_Th>
				<AC_Red_Ref_Refl_Th><xsl:value-of select="$ACRedRefReflTh"/></AC_Red_Ref_Refl_Th>
				<AC_Dbv_Red_Veget_Tst_Ndvi_Th><xsl:value-of select="$ACDbvRedVegetTstNdviTh"/></AC_Dbv_Red_Veget_Tst_Ndvi_Th>
				<AC_Dbv_Red_Veget_Refl_Th><xsl:value-of select="$ACDbvRedVegetReflTh"/></AC_Dbv_Red_Veget_Refl_Th>
				<AC_Wv_Iter_Start_Summer><xsl:value-of select="$ACWvIterStartSummer"/></AC_Wv_Iter_Start_Summer>
				<AC_Wv_Iter_Start_Winter><xsl:value-of select="$ACWvIterStartWinter"/></AC_Wv_Iter_Start_Winter>
				<AC_Rng_Nbhd_Terrain_Corr><xsl:value-of select="$ACRngNbhdTerrainCorr"/></AC_Rng_Nbhd_Terrain_Corr>
				<AC_Max_Nr_Topo_Iter><xsl:value-of select="$ACMaxNrTopoIter"/></AC_Max_Nr_Topo_Iter>
				<AC_Topo_Corr_Cutoff><xsl:value-of select="$ACTopoCorrCutoff"/></AC_Topo_Corr_Cutoff>
				<AC_Vegetation_Index_Th><xsl:value-of select="$ACVegetationIndexTh"/></AC_Vegetation_Index_Th>
			</ACL_Prio_2>
			<ACL_Prio_3>
				<AC_Limit_Area_Path_Rad_Scale><xsl:value-of select="$ACLimitAreaPathRadScale"/></AC_Limit_Area_Path_Rad_Scale>
				<AC_Ddv_Smooting_Window><xsl:value-of select="$ACDdvSmootingWindow"/></AC_Ddv_Smooting_Window>
				<AC_Terrain_Refl_Start><xsl:value-of select="$ACTerrainReflStart"/></AC_Terrain_Refl_Start>
				<AC_Spr_Refl_Percentage><xsl:value-of select="$ACSprReflPercentage"/></AC_Spr_Refl_Percentage>
				<AC_Spr_Refl_Promille><xsl:value-of select="$ACSprReflPromille"/></AC_Spr_Refl_Promille>
			</ACL_Prio_3>
		</Level-2A_CAL_AC_Ground_Image_Processing_Parameter>
	</xsl:template>
</xsl:stylesheet>
