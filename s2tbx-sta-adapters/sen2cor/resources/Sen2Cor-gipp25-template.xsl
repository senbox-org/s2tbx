<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:param name="nbProcesses" select="1" />
	<xsl:param name="medianFilter" select="0" />
	<xsl:param name="aerosol" select="'RURAL'"/>
	<xsl:param name="midLat" select="'SUMMER'"/>
	<xsl:param name="ozone" select="'h - 331'"/>
	<xsl:param name="wvCorrection" select="1"/>
	<xsl:param name="visUpdateMode" select="1"/>
	<xsl:param name="wvWatermask" select="1"/>
	<xsl:param name="cirrusCorrection" select="'FALSE'"/>
	<xsl:param name="brdfCorrection" select="0"/>
	<xsl:param name="brdfLower" select="0.22"/>
	<xsl:param name="visibility" select="23.0"/>
	<xsl:param name="altitude" select="0.100"/>
	<xsl:param name="wvThresCirrus" select="0.25"/>
	<xsl:param name="demDirectory" select="'NONE'"/>
	<xsl:param name="demReference" select="'http://data_public:GDdci@data.cgiar-csi.org/srtm/tiles/GeoTIFF/'"/>
	<xsl:param name="demUnit" select="'0'"/>
	<xsl:param name="adjacencyRange" select="1.000"/>
	<xsl:param name="smoothWVMap" select="100.0"/>
	<xsl:param name="generateDEMoutput" select="'FALSE'"/>
	<xsl:param name="generateTCIoutput" select="'TRUE'"/>
	<xsl:param name="generateDDVoutput" select="'FALSE'"/>
	<xsl:template match="/">
		<Level-2A_Ground_Image_Processing_Parameter xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="L2A_GIPP.xsd">
			<Common_Section>
				<Log_Level>INFO</Log_Level>
				<!-- can be: NOTSET, DEBUG, INFO, WARNING, ERROR, CRITICAL -->
				<Operation_Mode>PDGS</Operation_Mode>
				<!-- can be: TOOLBOX or PDGS. PDGS reflects changes for integration into PDGS according to SIIMPC-1119 -->
				<Nr_Processes><xsl:value-of select="$nbProcesses"/></Nr_Processes>
				<!-- can be an unsigned integer value specifying the number or processes you intend to operate in parallel or: AUTO. If AUTO is chosen, the processor determines the number of processes automatically, using cpu_count() -->
				<Target_Directory>DEFAULT</Target_Directory>
				<!-- should be either a directory or 'DEFAULT'. If default, target will be created at root of L1C product -->
				<DEM_Directory><xsl:value-of select="$demDirectory"/></DEM_Directory>
				<!-- should be either a directory in the sen2cor home folder or 'NONE'. If NONE, no DEM will be used -->
				<DEM_Reference><xsl:value-of select="$demReference"/></DEM_Reference>
				<!-- will be ignored, if DEM is NONE. A SRTM DEM will be downloaded from this reference, if no local DEM is available -->
				<Generate_DEM_Output><xsl:value-of select="$generateDEMoutput"/></Generate_DEM_Output>
				<!-- FALSE: no DEM output, TRUE: store DEM in the AUX data directory -->
				<Generate_TCI_Output><xsl:value-of select="$generateTCIoutput"/></Generate_TCI_Output>
				<!-- FALSE: no TCI output, TRUE: store TCI in the IMAGE data directory -->
				<Generate_DDV_Output><xsl:value-of select="$generateDDVoutput"/></Generate_DDV_Output>
				<!-- FALSE: no DDV output, TRUE: store DDV in the QI_DATA directory -->
				<PSD_Scheme PSD_Version="13.1" PSD_Reference="S2-PDGS-TAS-DI-PSD-V13.1_Schema">
					<UP_Scheme_1C>S2_User_Product_Level-1C_Metadata</UP_Scheme_1C>
					<UP_Scheme_2A>S2_User_Product_Level-2A_Metadata</UP_Scheme_2A>
					<Tile_Scheme_1C>S2_PDI_Level-1C_Tile_Metadata</Tile_Scheme_1C>
					<Tile_Scheme_2A>S2_PDI_Level-2A_Tile_Metadata</Tile_Scheme_2A>
					<DS_Scheme_1C>S2_PDI_Level-1C_Datastrip_Metadata</DS_Scheme_1C>
					<DS_Scheme_2A>S2_PDI_Level-2A_Datastrip_Metadata</DS_Scheme_2A>
				</PSD_Scheme>
				<PSD_Scheme PSD_Version="14.2" PSD_Reference="S2-PDGS-TAS-DI-PSD-V14.2_Schema">
					<UP_Scheme_1C>S2_User_Product_Level-1C_Metadata</UP_Scheme_1C>
					<UP_Scheme_2A>S2_User_Product_Level-2A_Metadata</UP_Scheme_2A>
					<Tile_Scheme_1C>S2_PDI_Level-1C_Tile_Metadata</Tile_Scheme_1C>
					<Tile_Scheme_2A>S2_PDI_Level-2A_Tile_Metadata</Tile_Scheme_2A>
					<DS_Scheme_1C>S2_PDI_Level-1C_Datastrip_Metadata</DS_Scheme_1C>
					<DS_Scheme_2A>S2_PDI_Level-2A_Datastrip_Metadata</DS_Scheme_2A>
				</PSD_Scheme>
				<PSD_Scheme PSD_Version="14.3" PSD_Reference="S2-PDGS-TAS-DI-PSD-V14.3_Schema">
					<UP_Scheme_1C>S2_User_Product_Level-1C_Metadata</UP_Scheme_1C>
					<UP_Scheme_2A>S2_User_Product_Level-2A_Metadata</UP_Scheme_2A>
					<Tile_Scheme_1C>S2_PDI_Level-1C_Tile_Metadata</Tile_Scheme_1C>
					<Tile_Scheme_2A>S2_PDI_Level-2A_Tile_Metadata</Tile_Scheme_2A>
					<DS_Scheme_1C>S2_PDI_Level-1C_Datastrip_Metadata</DS_Scheme_1C>
					<DS_Scheme_2A>S2_PDI_Level-2A_Datastrip_Metadata</DS_Scheme_2A>
				</PSD_Scheme>
				<PSD_Scheme PSD_Version="14.5" PSD_Reference="S2-PDGS-TAS-DI-PSD-V14.5_Schema">
					<UP_Scheme_1C>S2_User_Product_Level-1C_Metadata</UP_Scheme_1C>
					<UP_Scheme_2A>S2_User_Product_Level-2A_Metadata</UP_Scheme_2A>
					<Tile_Scheme_1C>S2_PDI_Level-1C_Tile_Metadata</Tile_Scheme_1C>
					<Tile_Scheme_2A>S2_PDI_Level-2A_Tile_Metadata</Tile_Scheme_2A>
					<DS_Scheme_1C>S2_PDI_Level-1C_Datastrip_Metadata</DS_Scheme_1C>
					<DS_Scheme_2A>S2_PDI_Level-2A_Datastrip_Metadata</DS_Scheme_2A>
				</PSD_Scheme>
				<GIPP_Scheme>L2A_GIPP.xsd</GIPP_Scheme>
				<SC_Scheme>L2A_CAL_SC_GIPP.xsd</SC_Scheme>
				<AC_Scheme>L2A_CAL_AC_GIPP.xsd</AC_Scheme>
				<PB_Scheme>L2A_PB_GIPP</PB_Scheme>
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
					<BRDF_Correction><xsl:value-of select="$brdfCorrection"/></BRDF_Correction>
					<!-- 0: no BRDF correction, 1: , 2: ,11, 12, 22, 21: -->
					<BRDF_Lower_Bound><xsl:value-of select="$brdfLower"/></BRDF_Lower_Bound>
					<!-- In most cases, g=0.2 to 0.25 is adequate, in extreme cases of overcorrection g=0.1 should be applied -->
				</Flags>
				<Calibration>
					<DEM_Unit><xsl:value-of select="$demUnit"/></DEM_Unit>
					<!-- (0=[m], 1=[dm], 2=[cm]) -->
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
				</Calibration>
			</Atmospheric_Correction>
		</Level-2A_Ground_Image_Processing_Parameter>
	</xsl:template>
</xsl:stylesheet>
