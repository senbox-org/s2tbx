<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:param name="row0" select="'OFF'"/>
	<xsl:param name="col0" select="'OFF'"/>
	<xsl:param name="nRowWin" select="'1200'"/>
	<xsl:param name="nColWin" select="'1200'"/>
	<xsl:param name="nrThreads" select="'AUTO'" />
	<xsl:param name="productGenerator" select="'NONE'"/>
	<xsl:param name="demDirectory" select="'NONE'"/>
	<xsl:param name="demReference" select="'NONE'"/>
	<xsl:param name="generateDEMoutput" select="'FALSE'"/>
	<xsl:param name="forceExitOnDEMerror" select="'FALSE'"/>
	<xsl:param name="generateTCIoutput" select="'TRUE'"/>
	<xsl:param name="generateDDVoutput" select="'FALSE'"/>
	<xsl:param name="handleL1CQLTmask" select="'FALSE'"/>
	<xsl:param name="downsample" select="'TRUE'"/>
	<xsl:param name="psdVersion" select="'DEFAULT'"/>
	<xsl:param name="medianFilter" select="0" />
	<xsl:param name="aerosol" select="'RURAL'"/>
	<xsl:param name="midLat" select="'SUMMER'"/>
	<xsl:param name="ozone" select="'0'"/>
	<xsl:param name="wvCorrection" select="1"/>
	<xsl:param name="visUpdateMode" select="1"/>
	<xsl:param name="wvWatermask" select="1"/>
	<xsl:param name="cirrusCorrection" select="'FALSE'"/>
	<xsl:param name="terrainCorrection" select="'TRUE'"/>
	<xsl:param name="brdfCorrection" select="0"/>
	<xsl:param name="brdfLower" select="0.22"/>
	<xsl:param name="adjacencyRange" select="1.000"/>
	<xsl:param name="visibility" select="40.0"/>
	<xsl:param name="altitude" select="0.100"/>
	<xsl:param name="smoothWVMap" select="100.0"/>
	<xsl:param name="wvThresCirrus" select="0.25"/>
	<xsl:param name="compressionLevel" select="0"/>
	<xsl:template match="/">
		<Level-2A_Ground_Image_Processing_Parameter xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="L2A_GIPP.xsd">
			<Common_Section>
				<Log_Level>INFO</Log_Level>
				<!-- can be: NOTSET, DEBUG, INFO, WARNING, ERROR, CRITICAL -->
				<Region_Of_Interest>
					<!-- this defines a configurable Region of Interest (ROI) -->
					<row0><xsl:value-of select="$row0"/></row0>
					<!-- if row0 == OFF: standard processing without ROI detection -->
					<!-- if row0 == AUTO: region of interest is detected automatically via an algorithm -->
					<!-- else: row0: specifies the midpoint of the region of interest -->
					<!-- row0 must be integer divisible by 6, to prevent rounding errors for downsampling -->
					<!-- specify always a 10m resolution ROI, it will be automatically adapted to the lower resolutions -->
					<col0><xsl:value-of select="$col0"/></col0>
					<!-- if col0 == OFF: standard processing without ROI detection -->
					<!-- if col0 == AUTO: region of interest is detected automatically via an algorithm -->
					<!-- else: col0: specifies the midpoint of the region of interest -->
					<!-- col0 must be integer divisible by 6, to prevent rounding errors for downsampling -->
					<!-- specify always a 10m resolution ROI, it will be automatically adapted to the lower resolutions -->
					<nrow_win><xsl:value-of select="$nRowWin"/></nrow_win>
					<!-- if row0, col0 not OFF or AUTO: nrow_win, ncol_win defines a rectangle around the midpoint in pixel -->
					<!-- nrow_win must be integer divisible by 6, to prevent rounding errors for downsampling -->
					<!-- specify always a 10m resolution ROI, it will be automatically adapted to the lower resolutions -->
					<ncol_win><xsl:value-of select="$nColWin"/></ncol_win>
					<!-- if row0, col0 not OFF or AUTO: ncol_win, ncol_win defines a rectangle around the midpoint in pixel -->
					<!-- ncol_win must be integer divisible by 6, to prevent rounding errors for downsampling -->
					<!-- specify always a 10m resolution ROI, it will be automatically adapted to the lower resolutions -->
				</Region_Of_Interest>
				<Product_DOI>https://doi.org/10.5270/S2_-znk9xsj</Product_DOI>
				<Nr_Threads><xsl:value-of select="$nrThreads"/></Nr_Threads>
				<!-- can be an unsigned integer value specifying the number or processes you intend to operate in parallel or: AUTO. If AUTO is chosen, the processor determines the number of processes automatically, using cpu_count() -->
				<Product_Generator><xsl:value-of select="$productGenerator"/></Product_Generator>
				<!-- should be either a directory in the sen2cor home folder or 'NONE'. If NONE, no Product Generator will be used -->
				<DEM_Directory><xsl:value-of select="$demDirectory"/></DEM_Directory>
				<!-- should be either a directory in the sen2cor home folder or 'NONE'. If NONE, no DEM will be used -->
				<DEM_Reference><xsl:value-of select="$demReference"/></DEM_Reference>
				<!-- will be ignored, if DEM is NONE. A SRTM DEM will be downloaded from this reference, if no local DEM is available -->
				<Generate_DEM_Output><xsl:value-of select="$generateDEMoutput"/></Generate_DEM_Output>
				<!-- FALSE: no DEM output, TRUE: store DEM in the AUX data directory -->
				<Force_Exit_On_DEM_Error><xsl:value-of select="$forceExitOnDEMerror"/></Force_Exit_On_DEM_Error>
				<!-- FALSE: Processing continues with a flat surface, TRUE: processing will be stopped -->
				<Generate_TCI_Output><xsl:value-of select="$generateTCIoutput"/></Generate_TCI_Output>
				<!-- FALSE: no TCI output, TRUE: store TCI in the IMAGE data directory -->
				<Generate_DDV_Output><xsl:value-of select="$generateDDVoutput"/></Generate_DDV_Output>
				<!-- FALSE: no DDV output, TRUE: store DDV in the QI_DATA directory -->
				<Handle_L1C_QLT_Mask><xsl:value-of select="$handleL1CQLTmask"/></Handle_L1C_QLT_Mask>
				<!-- FALSE: no handling of L1C Quality Mask, TRUE: handling L1C Quality Mask -->
				<Downsample_20_to_60><xsl:value-of select="$downsample"/></Downsample_20_to_60>
				<!-- TRUE: create additional 60m bands when 20m is processed -->
				<PSD_Version><xsl:value-of select="$psdVersion"/></PSD_Version>
				<!-- Special entry for forcing a special PSD Version to be processed leave it to DEFAULT if you are not 100 % sure that you need a dedicated version -->
				<PSD_Scheme PSD_Version="14.2" PSD_Reference="S2-PDGS-TAS-DI-PSD-V14.2_Schema">
					<UP_Scheme_1C>S2_User_Product_Level-1C_Metadata</UP_Scheme_1C>
					<UP_Scheme_2A>S2_User_Product_Level-2A_Metadata</UP_Scheme_2A>
					<Tile_Scheme_1C>S2_PDI_Level-1C_Tile_Metadata</Tile_Scheme_1C>
					<Tile_Scheme_2A>S2_PDI_Level-2A_Tile_Metadata</Tile_Scheme_2A>
					<DS_Scheme_1C>S2_PDI_Level-1C_Datastrip_Metadata</DS_Scheme_1C>
					<DS_Scheme_2A>S2_PDI_Level-2A_Datastrip_Metadata</DS_Scheme_2A>
				</PSD_Scheme>
				<PSD_Scheme PSD_Version="14.6" PSD_Reference="S2-PDGS-TAS-DI-PSD-V14.6_Schema">
					<UP_Scheme_1C>S2_User_Product_Level-1C_Metadata</UP_Scheme_1C>
					<UP_Scheme_2A>S2_User_Product_Level-2A_Metadata</UP_Scheme_2A>
					<Tile_Scheme_1C>S2_PDI_Level-1C_Tile_Metadata</Tile_Scheme_1C>
					<Tile_Scheme_2A>S2_PDI_Level-2A_Tile_Metadata</Tile_Scheme_2A>
					<DS_Scheme_1C>S2_PDI_Level-1C_Datastrip_Metadata</DS_Scheme_1C>
					<DS_Scheme_2A>S2_PDI_Level-2A_Datastrip_Metadata</DS_Scheme_2A>
				</PSD_Scheme>
				<PSD_Scheme PSD_Version="14.7" PSD_Reference="S2-PDGS-TAS-DI-PSD-V14.7_Schema">
					<UP_Scheme_1C>S2_User_Product_Level-1C_Metadata</UP_Scheme_1C>
					<UP_Scheme_2A>S2_User_Product_Level-2A_Metadata</UP_Scheme_2A>
					<Tile_Scheme_1C>S2_PDI_Level-1C_Tile_Metadata</Tile_Scheme_1C>
					<Tile_Scheme_2A>S2_PDI_Level-2A_Tile_Metadata</Tile_Scheme_2A>
					<DS_Scheme_1C>S2_PDI_Level-1C_Datastrip_Metadata</DS_Scheme_1C>
					<DS_Scheme_2A>S2_PDI_Level-2A_Datastrip_Metadata</DS_Scheme_2A>
				</PSD_Scheme>
				<PSD_Scheme PSD_Version="14.8" PSD_Reference="S2-PDGS-TAS-DI-PSD-V14.8_Schema">
					<UP_Scheme_1C>S2_User_Product_Level-1C_Metadata</UP_Scheme_1C>
					<UP_Scheme_2A>S2_User_Product_Level-2A_Metadata</UP_Scheme_2A>
					<Tile_Scheme_1C>S2_PDI_Level-1C_Tile_Metadata</Tile_Scheme_1C>
					<Tile_Scheme_2A>S2_PDI_Level-2A_Tile_Metadata</Tile_Scheme_2A>
					<DS_Scheme_1C>S2_PDI_Level-1C_Datastrip_Metadata</DS_Scheme_1C>
					<DS_Scheme_2A>S2_PDI_Level-2A_Datastrip_Metadata</DS_Scheme_2A>
				</PSD_Scheme>
				<PSD_Scheme PSD_Version="14.9" PSD_Reference="S2-PDGS-TAS-DI-PSD-V14.9_Schema">
					<UP_Scheme_1C>S2_User_Product_Level-1C_Metadata</UP_Scheme_1C>
					<UP_Scheme_2A>S2_User_Product_Level-2A_Metadata</UP_Scheme_2A>
					<Tile_Scheme_1C>S2_PDI_Level-1C_Tile_Metadata</Tile_Scheme_1C>
					<Tile_Scheme_2A>S2_PDI_Level-2A_Tile_Metadata</Tile_Scheme_2A>
					<DS_Scheme_1C>S2_PDI_Level-1C_Datastrip_Metadata</DS_Scheme_1C>
					<DS_Scheme_2A>S2_PDI_Level-2A_Datastrip_Metadata</DS_Scheme_2A>
				</PSD_Scheme>
				<GIPP_Scheme>L2A_GIPP</GIPP_Scheme>
				<SC_Scheme>L2A_CAL_SC_GIPP</SC_Scheme>
				<AC_Scheme>L2A_CAL_AC_GIPP</AC_Scheme>
				<PB_Scheme>L2A_PB_GIPP</PB_Scheme>
				<QI_Scheme>L2A_QUALITY</QI_Scheme>
			</Common_Section>
			<Scene_Classification>
				<Filters>
					<Median_Filter><xsl:value-of select="$medianFilter"/></Median_Filter>
				</Filters>
			</Scene_Classification>
			<Atmospheric_Correction>
				<Look_Up_Tables>
					<Aerosol_Type><xsl:value-of select="$aerosol"/></Aerosol_Type>
					<!-- RURAL, MARITIME, AUTO -->
					<Mid_Latitude><xsl:value-of select="$midLat"/></Mid_Latitude>
					<!-- SUMMER, WINTER, AUTO -->

					<xsl:if test="$ozone = '0'">
						<Ozone_Content>0</Ozone_Content>
					</xsl:if>
					<xsl:if test="$ozone = 'f - 250'">
						<Ozone_Content>250</Ozone_Content>
					</xsl:if>
					<xsl:if test="$ozone = 'g - 290'">
						<Ozone_Content>290</Ozone_Content>
					</xsl:if>
					<xsl:if test="$ozone = 'h - 331'">
						<Ozone_Content>331</Ozone_Content>
					</xsl:if>
					<xsl:if test="$ozone = 'i - 370'">
						<Ozone_Content>370</Ozone_Content>
					</xsl:if>
					<xsl:if test="$ozone = 'j - 410'">
						<Ozone_Content>410</Ozone_Content>
					</xsl:if>
					<xsl:if test="$ozone = 'k - 450'">
						<Ozone_Content>450</Ozone_Content>
					</xsl:if>
					<xsl:if test="$ozone = 't - 250'">
						<Ozone_Content>250</Ozone_Content>
					</xsl:if>
					<xsl:if test="$ozone = 'u - 290'">
						<Ozone_Content>290</Ozone_Content>
					</xsl:if>
					<xsl:if test="$ozone = 'v - 330'">
						<Ozone_Content>330</Ozone_Content>
					</xsl:if>
					<xsl:if test="$ozone = 'w - 377'">
						<Ozone_Content>377</Ozone_Content>
					</xsl:if>
					<xsl:if test="$ozone = 'x - 420'">
						<Ozone_Content>420</Ozone_Content>
					</xsl:if>
					<xsl:if test="$ozone = 'y - 460'">
						<Ozone_Content>460</Ozone_Content>
					</xsl:if>


					<!-- The atmospheric temperature profile and ozone content in Dobson Unit (DU)
    				0: to get the best approximation from metadata
    				(this is the smallest difference between metadata and column DU),
    				else select one of:
    				==========================================
  					For midlatitude summer (MS) atmosphere:
  					250, 290, 331 (standard MS), 370, 410, 450
  					==========================================
  					For midlatitude winter (MW) atmosphere:
  					250, 290, 330, 377 (standard MW), 420, 460
  					==========================================
 					-->
				</Look_Up_Tables>
				<Flags>
					<WV_Correction><xsl:value-of select="$wvCorrection"/></WV_Correction>
					<!-- 0: No WV correction, 1: only 940 nm bands, 2: only 1130 nm bands , 3: both regions used during wv retrieval, 4: Thermal region -->
					<VIS_Update_Mode><xsl:value-of select="$visUpdateMode"/></VIS_Update_Mode>
					<!-- 0: constant, 1: variable visibility -->
					<WV_Watermask><xsl:value-of select="$wvWatermask"/></WV_Watermask>
					<!-- 0: not replaced, 1: land-average, 2: line-average -->
					<Cirrus_Correction><xsl:value-of select="$cirrusCorrection"/></Cirrus_Correction>
					<!-- 0: no, 1: yes -->
					<DEM_Terrain_Correction><xsl:value-of select="$terrainCorrection"/></DEM_Terrain_Correction>
					<!--Use DEM for Terrain Correction, otherwise only used for WVP and AOT -->
					<BRDF_Correction><xsl:value-of select="$brdfCorrection"/></BRDF_Correction>
					<!-- 0: no BRDF correction, 1: , 2: ,11, 12, 22, 21: -->
					<BRDF_Lower_Bound><xsl:value-of select="$brdfLower"/></BRDF_Lower_Bound>
					<!-- In most cases, g=0.2 to 0.25 is adequate, in extreme cases of overcorrection g=0.1 should be applied -->
				</Flags>
				<Calibration>
					<Adj_Km><xsl:value-of select="$adjacencyRange"/></Adj_Km>
					<!-- Adjancency Range [km] -->
					<Visibility><xsl:value-of select="$visibility"/></Visibility>
					<!-- visibility (5 <= visib <= 120 km) -->
					<Altitude><xsl:value-of select="$altitude"/></Altitude>
					<!-- [km] -->
					<Smooth_WV_Map><xsl:value-of select="$smoothWVMap"/></Smooth_WV_Map>
					<!-- length of square box, [meters] -->
					<WV_Threshold_Cirrus><xsl:value-of select="$wvThresCirrus"/></WV_Threshold_Cirrus>
					<!-- water vapor threshold to switch off cirrus algorithm [cm]Range: 0.1-1.0 -->
					<Database_Compression_Level><xsl:value-of select="$compressionLevel"/></Database_Compression_Level>
					<!-- zlib compression level for image database [0-9, 0: best speed, 9: best size] -->
				</Calibration>
			</Atmospheric_Correction>
		</Level-2A_Ground_Image_Processing_Parameter>
	</xsl:template>
</xsl:stylesheet>
