<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:param name="Scaling_Limiter" select="'TRUE'"/>
	<xsl:param name="Scaling_Disabler" select="'TRUE'"/>
	<xsl:param name="Rho_Retrieval_Step2" select="'TRUE'"/>
	<xsl:param name="Lib_Dir" select="'lib'"/>
	<xsl:param name="fwhm_Band_B1" select="'0.020'"/>
	<xsl:param name="fwhm_Band_B2" select="'0.065'"/>
	<xsl:param name="fwhm_Band_B3" select="'0.035'"/>
	<xsl:param name="fwhm_Band_B4" select="'0.030'"/>
	<xsl:param name="fwhm_Band_B5" select="'0.015'"/>
	<xsl:param name="fwhm_Band_B6" select="'0.015'"/>
	<xsl:param name="fwhm_Band_B7" select="'0.020'"/>
	<xsl:param name="fwhm_Band_B8" select="'0.115'"/>
	<xsl:param name="fwhm_Band_B8A" select="'0.020'"/>
	<xsl:param name="fwhm_Band_B9" select="'0.020'"/>
	<xsl:param name="fwhm_Band_B10" select="'0.030'"/>
	<xsl:param name="fwhm_Band_B11" select="'0.090'"/>
	<xsl:param name="fwhm_Band_B12" select="'0.180'"/>
	<xsl:param name="min_sc_blu" select="'0.9'"/>
	<xsl:param name="max_sc_blu" select="'1.1'"/>
	<xsl:param name="wavelength_Band_B1" select="'0.443'"/>
	<xsl:param name="wavelength_Band_B1_C0" select="'0.00000'"/>
	<xsl:param name="wavelength_Band_B1_C1" select="'0.001'"/>
	<xsl:param name="wavelength_Band_B2" select="'0.490'"/>
	<xsl:param name="wavelength_Band_B2_C0" select="'0.00000'"/>
	<xsl:param name="wavelength_Band_B2_C1" select="'0.001'"/>
	<xsl:param name="wavelength_Band_B3" select="'0.560'"/>
	<xsl:param name="wavelength_Band_B3_C0" select="'0.00000'"/>
	<xsl:param name="wavelength_Band_B3_C1" select="'0.001'"/>
	<xsl:param name="wavelength_Band_B4" select="'0.665'"/>
	<xsl:param name="wavelength_Band_B4_C0" select="'0.00000'"/>
	<xsl:param name="wavelength_Band_B4_C1" select="'0.001'"/>
	<xsl:param name="wavelength_Band_B5" select="'0.705'"/>
	<xsl:param name="wavelength_Band_B5_C0" select="'0.00000'"/>
	<xsl:param name="wavelength_Band_B5_C1" select="'0.001'"/>
	<xsl:param name="wavelength_Band_B6" select="'0.740'"/>
	<xsl:param name="wavelength_Band_B6_C0" select="'0.00000'"/>
	<xsl:param name="wavelength_Band_B6_C1" select="'0.001'"/>
	<xsl:param name="wavelength_Band_B7" select="'0.783'"/>
	<xsl:param name="wavelength_Band_B7_C0" select="'0.00000'"/>
	<xsl:param name="wavelength_Band_B7_C1" select="'0.001'"/>
	<xsl:param name="wavelength_Band_B8" select="'0.842'"/>
	<xsl:param name="wavelength_Band_B8_C0" select="'0.00000'"/>
	<xsl:param name="wavelength_Band_B8_C1" select="'0.001'"/>
	<xsl:param name="wavelength_Band_B8A" select="'0.865'"/>
	<xsl:param name="wavelength_Band_B8A_C0" select="'0.00000'"/>
	<xsl:param name="wavelength_Band_B8A_C1" select="'0.001'"/>
	<xsl:param name="wavelength_Band_B9" select="'0.945'"/>
	<xsl:param name="wavelength_Band_B9_C0" select="'0.00000'"/>
	<xsl:param name="wavelength_Band_B9_C1" select="'0.001'"/>
	<xsl:param name="wavelength_Band_B10" select="'1.375'"/>
	<xsl:param name="wavelength_Band_B10_C0" select="'0.00000'"/>
	<xsl:param name="wavelength_Band_B10_C1" select="'0.0005'"/>
	<xsl:param name="wavelength_Band_B11" select="'1.610'"/>
	<xsl:param name="wavelength_Band_B11_C0" select="'0.00000'"/>
	<xsl:param name="wavelength_Band_B11_C1" select="'0.0002'"/>
	<xsl:param name="wavelength_Band_B12" select="'2.190'"/>
	<xsl:param name="wavelength_Band_B12_C0" select="'0.00000'"/>
	<xsl:param name="wavelength_Band_B12_C1" select="'0.00005'"/>
	<xsl:param name="AC_Min_Ddv_Area" select="'2.0'"/>
	<xsl:param name="AC_Swir_Refl_Lower_Th" select="'0.01'"/>
	<xsl:param name="AC_Swir_22um_Red_Refl_Ratio" select="'0.5'"/>
	<xsl:param name="AC_Red_Blue_Refl_Ratio" select="'0.5'"/>
	<xsl:param name="AC_Cut_Off_Aot_Iter_Vegetation" select="'0.01'"/>
	<xsl:param name="AC_Cut_Off_Aot_Iter_Water" select="'-0.005'"/>
	<xsl:param name="AC_Aerosol_Type_Ratio_Th" select="'0.05'"/>
	<xsl:param name="AC_Topo_Corr_Th" select="'0.01'"/>
	<xsl:param name="AC_Slope_Th" select="'6.0'"/>
	<xsl:param name="AC_Dem_P2p_Val" select="'50.0'"/>
	<xsl:param name="AC_Swir_Refl_Ndvi_Th" select="'0.10'"/>
	<xsl:param name="AC_Ddv_Swir_Refl_Th1" select="'0.05'"/>
	<xsl:param name="AC_Ddv_Swir_Refl_Th2" select="'0.10'"/>
	<xsl:param name="AC_Ddv_Swir_Refl_Th3" select="'0.12'"/>
	<xsl:param name="AC_Ddv_16um_Refl_Th1" select="'0.10'"/>
	<xsl:param name="AC_Ddv_16um_Refl_Th2" select="'0.15'"/>
	<xsl:param name="AC_Ddv_16um_Refl_Th3" select="'0.18'"/>
	<xsl:param name="AC_Dbv_Nir_Refl_Th" select="'0.35'"/>
	<xsl:param name="AC_Dbv_Ndvi_Th" select="'0.66'"/>
	<xsl:param name="AC_Red_Ref_Refl_Th" select="'0.02'"/>
	<xsl:param name="AC_Dbv_Red_Veget_Tst_Ndvi_Th" select="'0.40'"/>
	<xsl:param name="AC_Dbv_Red_Veget_Refl_Th" select="'0.15'"/>
	<xsl:param name="AC_Wv_Iter_Start_Summer" select="'1.0'"/>
	<xsl:param name="AC_Wv_Iter_Start_Winter" select="'0.4'"/>
	<xsl:param name="AC_Rng_Nbhd_Terrain_Corr" select="'0.5'"/>
	<xsl:param name="AC_Max_Nr_Topo_Iter" select="'3'"/>
	<xsl:param name="AC_Topo_Corr_Cutoff" select="'1.5'"/>
	<xsl:param name="AC_Vegetation_Index_Th" select="'3.0'"/>
	<xsl:param name="AC_Limit_Area_Path_Rad_Scale" select="'0'"/>
	<xsl:param name="AC_Ddv_Smooting_Window" select="'1.0'"/>
	<xsl:param name="AC_Terrain_Refl_Start" select="'0.1'"/>
	<xsl:param name="AC_Spr_Refl_Percentage" select="'0.25'"/>
	<xsl:param name="AC_Spr_Refl_Promille" select="'0.3'"/>
	<xsl:template match="/">
		<Level-2A_CAL_AC_Ground_Image_Processing_Parameter xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="L2A_CAL_AC_GIPP.xsd">
			<Flags>
				<Scaling_Limiter><xsl:value-of select="$Scaling_Limiter"/></Scaling_Limiter>
				<Scaling_Disabler><xsl:value-of select="$Scaling_Disabler"/></Scaling_Disabler>
				<Rho_Retrieval_Step2><xsl:value-of select="$Rho_Retrieval_Step2"/></Rho_Retrieval_Step2>
			</Flags>
			<References>
				<Lib_Dir><xsl:value-of select="$Lib_Dir"/></Lib_Dir>
			</References>
			<Sensor>
				<Resolution>
					<Band_List>
						<!-- full width half maximum -->
						<fwhm><xsl:value-of select="$fwhm_Band_B1"/></fwhm>
						<fwhm><xsl:value-of select="$fwhm_Band_B2"/></fwhm>
						<fwhm><xsl:value-of select="$fwhm_Band_B3"/></fwhm>
						<fwhm><xsl:value-of select="$fwhm_Band_B4"/></fwhm>
						<fwhm><xsl:value-of select="$fwhm_Band_B5"/></fwhm>
						<fwhm><xsl:value-of select="$fwhm_Band_B6"/></fwhm>
						<fwhm><xsl:value-of select="$fwhm_Band_B7"/></fwhm>
						<fwhm><xsl:value-of select="$fwhm_Band_B8"/></fwhm>
						<fwhm><xsl:value-of select="$fwhm_Band_B8A"/></fwhm>
						<fwhm><xsl:value-of select="$fwhm_Band_B9"/></fwhm>
						<fwhm><xsl:value-of select="$fwhm_Band_B10"/></fwhm>
						<fwhm><xsl:value-of select="$fwhm_Band_B11"/></fwhm>
						<fwhm><xsl:value-of select="$fwhm_Band_B12"/></fwhm>
					</Band_List>
				</Resolution>
				<Calibration>
					<min_sc_blu><xsl:value-of select="$min_sc_blu"/></min_sc_blu>
					<max_sc_blu><xsl:value-of select="$max_sc_blu"/></max_sc_blu>
					<Band_List>
						<wavelength c0="{$wavelength_Band_B1_C0}" c1="{$wavelength_Band_B1_C1}"><xsl:value-of select="$wavelength_Band_B1"/></wavelength>
						<wavelength c0="{$wavelength_Band_B2_C0}" c1="{$wavelength_Band_B2_C1}"><xsl:value-of select="$wavelength_Band_B2"/></wavelength>
						<wavelength c0="{$wavelength_Band_B3_C0}" c1="{$wavelength_Band_B3_C1}"><xsl:value-of select="$wavelength_Band_B3"/></wavelength>
						<wavelength c0="{$wavelength_Band_B4_C0}" c1="{$wavelength_Band_B4_C1}"><xsl:value-of select="$wavelength_Band_B4"/></wavelength>
						<wavelength c0="{$wavelength_Band_B5_C0}" c1="{$wavelength_Band_B5_C1}"><xsl:value-of select="$wavelength_Band_B5"/></wavelength>
						<wavelength c0="{$wavelength_Band_B6_C0}" c1="{$wavelength_Band_B6_C1}"><xsl:value-of select="$wavelength_Band_B6"/></wavelength>
						<wavelength c0="{$wavelength_Band_B7_C0}" c1="{$wavelength_Band_B7_C1}"><xsl:value-of select="$wavelength_Band_B7"/></wavelength>
						<wavelength c0="{$wavelength_Band_B8_C0}" c1="{$wavelength_Band_B8_C1}"><xsl:value-of select="$wavelength_Band_B8"/></wavelength>
						<wavelength c0="{$wavelength_Band_B8A_C0}" c1="{$wavelength_Band_B8A_C1}"><xsl:value-of select="$wavelength_Band_B8A"/></wavelength>
						<wavelength c0="{$wavelength_Band_B9_C0}" c1="{$wavelength_Band_B9_C1}"><xsl:value-of select="$wavelength_Band_B9"/></wavelength>
						<wavelength c0="{$wavelength_Band_B10_C0}" c1="{$wavelength_Band_B10_C1}"><xsl:value-of select="$wavelength_Band_B10"/></wavelength>
						<wavelength c0="{$wavelength_Band_B11_C0}" c1="{$wavelength_Band_B11_C1}"><xsl:value-of select="$wavelength_Band_B11"/></wavelength>
						<wavelength c0="{$wavelength_Band_B12_C0}" c1="{$wavelength_Band_B12_C1}"><xsl:value-of select="$wavelength_Band_B12"/></wavelength>
					</Band_List>
				</Calibration>
			</Sensor>
			<ACL_Prio_1>
				<AC_Min_Ddv_Area><xsl:value-of select="$AC_Min_Ddv_Area"/></AC_Min_Ddv_Area>
				<AC_Swir_Refl_Lower_Th><xsl:value-of select="$AC_Swir_Refl_Lower_Th"/></AC_Swir_Refl_Lower_Th>
				<AC_Swir_22um_Red_Refl_Ratio><xsl:value-of select="$AC_Swir_22um_Red_Refl_Ratio"/></AC_Swir_22um_Red_Refl_Ratio>
				<AC_Red_Blue_Refl_Ratio><xsl:value-of select="$AC_Red_Blue_Refl_Ratio"/></AC_Red_Blue_Refl_Ratio>
				<AC_Cut_Off_Aot_Iter_Vegetation><xsl:value-of select="$AC_Cut_Off_Aot_Iter_Vegetation"/></AC_Cut_Off_Aot_Iter_Vegetation>
				<AC_Cut_Off_Aot_Iter_Water><xsl:value-of select="$AC_Cut_Off_Aot_Iter_Water"/></AC_Cut_Off_Aot_Iter_Water>
				<AC_Aerosol_Type_Ratio_Th><xsl:value-of select="$AC_Aerosol_Type_Ratio_Th"/></AC_Aerosol_Type_Ratio_Th>
				<AC_Topo_Corr_Th><xsl:value-of select="$AC_Topo_Corr_Th"/></AC_Topo_Corr_Th>
				<AC_Slope_Th><xsl:value-of select="$AC_Slope_Th"/></AC_Slope_Th>
				<AC_Dem_P2p_Val><xsl:value-of select="$AC_Dem_P2p_Val"/></AC_Dem_P2p_Val>
			</ACL_Prio_1>
			<ACL_Prio_2>
				<AC_Swir_Refl_Ndvi_Th><xsl:value-of select="$AC_Swir_Refl_Ndvi_Th"/></AC_Swir_Refl_Ndvi_Th>
				<AC_Ddv_Swir_Refl_Th1><xsl:value-of select="$AC_Ddv_Swir_Refl_Th1"/></AC_Ddv_Swir_Refl_Th1>
				<AC_Ddv_Swir_Refl_Th2><xsl:value-of select="$AC_Ddv_Swir_Refl_Th2"/></AC_Ddv_Swir_Refl_Th2>
				<AC_Ddv_Swir_Refl_Th3><xsl:value-of select="$AC_Ddv_Swir_Refl_Th3"/></AC_Ddv_Swir_Refl_Th3>
				<AC_Ddv_16um_Refl_Th1><xsl:value-of select="$AC_Ddv_16um_Refl_Th1"/></AC_Ddv_16um_Refl_Th1>
				<AC_Ddv_16um_Refl_Th2><xsl:value-of select="$AC_Ddv_16um_Refl_Th2"/></AC_Ddv_16um_Refl_Th2>
				<AC_Ddv_16um_Refl_Th3><xsl:value-of select="$AC_Ddv_16um_Refl_Th3"/></AC_Ddv_16um_Refl_Th3>
				<AC_Dbv_Nir_Refl_Th><xsl:value-of select="$AC_Dbv_Nir_Refl_Th"/></AC_Dbv_Nir_Refl_Th>
				<AC_Dbv_Ndvi_Th><xsl:value-of select="$AC_Dbv_Ndvi_Th"/></AC_Dbv_Ndvi_Th>
				<AC_Red_Ref_Refl_Th><xsl:value-of select="$AC_Red_Ref_Refl_Th"/></AC_Red_Ref_Refl_Th>
				<AC_Dbv_Red_Veget_Tst_Ndvi_Th><xsl:value-of select="$AC_Dbv_Red_Veget_Tst_Ndvi_Th"/></AC_Dbv_Red_Veget_Tst_Ndvi_Th>
				<AC_Dbv_Red_Veget_Refl_Th><xsl:value-of select="$AC_Dbv_Red_Veget_Refl_Th"/></AC_Dbv_Red_Veget_Refl_Th>
				<AC_Wv_Iter_Start_Summer><xsl:value-of select="$AC_Wv_Iter_Start_Summer"/></AC_Wv_Iter_Start_Summer>
				<AC_Wv_Iter_Start_Winter><xsl:value-of select="$AC_Wv_Iter_Start_Winter"/></AC_Wv_Iter_Start_Winter>
				<AC_Rng_Nbhd_Terrain_Corr><xsl:value-of select="$AC_Rng_Nbhd_Terrain_Corr"/></AC_Rng_Nbhd_Terrain_Corr>
				<AC_Max_Nr_Topo_Iter><xsl:value-of select="$AC_Max_Nr_Topo_Iter"/></AC_Max_Nr_Topo_Iter>
				<AC_Topo_Corr_Cutoff><xsl:value-of select="$AC_Topo_Corr_Cutoff"/></AC_Topo_Corr_Cutoff>
				<AC_Vegetation_Index_Th><xsl:value-of select="$AC_Vegetation_Index_Th"/></AC_Vegetation_Index_Th>
			</ACL_Prio_2>
			<ACL_Prio_3>
				<AC_Limit_Area_Path_Rad_Scale><xsl:value-of select="$AC_Limit_Area_Path_Rad_Scale"/></AC_Limit_Area_Path_Rad_Scale>
				<AC_Ddv_Smooting_Window><xsl:value-of select="$AC_Ddv_Smooting_Window"/></AC_Ddv_Smooting_Window>
				<AC_Terrain_Refl_Start><xsl:value-of select="$AC_Terrain_Refl_Start"/></AC_Terrain_Refl_Start>
				<AC_Spr_Refl_Percentage><xsl:value-of select="$AC_Spr_Refl_Percentage"/></AC_Spr_Refl_Percentage>
				<AC_Spr_Refl_Promille><xsl:value-of select="$AC_Spr_Refl_Promille"/></AC_Spr_Refl_Promille>
			</ACL_Prio_3>
		</Level-2A_CAL_AC_Ground_Image_Processing_Parameter>
	</xsl:template>
</xsl:stylesheet>
