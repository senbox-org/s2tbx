Sentinel-2 Toolbox Release Notes
================================

Changes in S2TBX 9.0
--------------------

### Main features

#### Windowed Reading of Products - Generalizing Parameters for Reader Plugins in Graph Builder

Allows specifying in Graph Builder, for read operation, a window (spatial subset) of either pixel coordinates or geographical coordinates, so that,
the reader will read directly the region of interest as a full-fledged product.

#### Support for New External Data Access Sources via Product Library

New remote data source modules are added to SNAP as plugins addressing the following remote repositories:
    * EOCAT ESA collections
    * FEDEO ESA collections
Also, several improvements added:
    * download queue for offline products
    * sequential download for query result pages (in order to improve the results waiting time)
    * query authentication for remote data sources requiring authentication

#### Update the GDAL version to 3.2.1

The GDAL version is updated from 3.0.0 to 3.2.1.
Also, a plugin for COG writer is added. Corrections made for visual artifacts noticed in produced images when writing in parallel, from multiple threads.
Allow setting which installed GDAL distribution is used by SNAP (when the user desires to use and installed distribution instead of the internal one, and there are several GDAL versions installed).

#### New plugin adapter for Sen2Cor 2.9

New plugin adapter for Sen2Cor 2.9 is added in SNAP menu.

### Plugin adapter for MAJA

New plugin adapter for MAJA 4.2.1 is added in SNAP menu.

#### Solved operators whose UI was not well functioning in Graph Builder

Updates made for operators: Reflectance to Radiance, Spectral Angle Mapper, Multi-size Mosaic, GeFolki Co-registration.


### List of solved issues
#### Bug
    * [SIITBX-434] - GenericRegionMerging creates empty output in GB and GPT
    * [SIITBX-435] - GDAL - error writing bands having data type int16
    * [SIITBX-436] - GDAL writer - visual artifacts in produced images when writing in parallel, from multiple threads
    * [SIITBX-438] - Some GeoTIFFs written with GDAL cannot be opened with SNAP GeoTIFF reader implementation
    * [SNAP-1427]  - JP2 reader - inconsistency between Int16 and UInt16
    * [SIITBX-444] - Missing resources Biophysical processing and update output tolerance
    * [SIITBX-453] - Biophysical Processor S2 (not S2_10m) not accessible from the gpt builder

#### New Feature
    * [STEP-18]     - Plugin Repository
    * [STEP-36]     - Add cookies notice on STEP website
    * [STEP-37]     - GDPR compliance for STEP forum
    * [STEP-44]     - Generalizing Parameters for Reader Plugins - Graph Builder
    * [STEP-47]     - STEP website new look and feel
    * [SIITBX-446]  - SNAP adapter plugin for Sen2Cor 2.9
    * [SNAP-1395]   - Product Library: support ESA collections
    * [SIITBX-277]  - Add maximum size option in S2TBX cache
    * [SIITBX-442]  - Third Party Sentinel-2 Products: Landsat processed by ESA, Landsat L2

#### Improvement
    * [SIITBX-437] - Update GDAL version used in SNAP to 3.2.1
    * [SIITBX-441] - Pleiades reader - ability to read pan-sharpened products (with 4 PMS bands)
    * [SIITBX-448] - CCI LandCover Data - location changed
    * [SIITBX-457] - Invalid path exception at loading native libraries from installed GDAL when multiple GDAL versions are installed
    * [SNAP-1359]  - Product Library database should allow multiple connections
    * [SNAP-1384]  - Product Library - Scientific Data Hub : add relative orbit number in search results for SAR sensors
    * [SNAP-1385]  - Product Library - Alaska Satellite Facility : add relative orbit number in search results for SAR sensors
    * [SNAP-1400]  - JP2 reader - GML metadata for EPSG:4326
    * [SNAP-1405]  - Product Library - Amazon Web Services - S2 "requester pays" bucket
    * [SNAP-1410]  - Product Library - USGS : response format changed
    * [SNAP-1411]  - Product Library - Scientific Data Hub : Too Many Requests
    * [SNAP-1435]  - Product Library - Scientific Data Hub : API URL changed
    * [SNAP-1436]  - JP2 reader - use shorter file names for decompressed tiles
    * [SIITBX-357] - Add Missing ECMWF data in Sentinel-2 MSI products

#### Task
    * [STEP-9]     - Support for plugin developers
    * [STEP-46]    - STEP Forum - increase storage
    * [SIITBX-410] - Check operators whose GUIs are not working in GraphBuilder



Changes in S2TBX 8.0
--------------------

### Main features

#### Windowed Reading of Products

Allows specifying a window (spatial subset) of either pixel coordinates or geographical coordinates, so that,
instead of first opening a full product and then subsetting it to a region of interest,
the reader will open directly the region of interest as a full-fledged product.

#### Product Library GUI Enhancements

This module is upgraded so that it can accommodate in a flexible way any type of sensor (radar, optical, atmospheric, etc.).
In addition, the display / interaction of / with product frames or quicklooks can be done on a 3D visualisation of Earth (NASA WorldWind).

#### SciHub Search UI Enhancements in Product Library

The possibility to access (and search for) remote data was introduced by regarding remote data sources as independent SNAP plugins.
This allows building a flexible GUI for searching products on the remote sources.
Besides the parametrized search (with parameters in the form of values), it also handles the selection of the area of interest on the 3D visualisation of Earth.

#### Support for New External Data Access Sources via Product Library

New remote data source modules are added to SNAP as plugins addressing the following remote repositories:
    * Copernicus Scientific Data Hub (SciHub): for Sentinel-1, Sentinel-2 and Sentinel-3 data
    * Amazon Web Services (AWS): for Sentinel-2 and Landsat-8 data
    * Alaska Satellite Facility (ASF): for Sentinel-1 and ALOS data
    * US Geological Survey (USGS): for Landsat-8 data
The searched products can be downloaded in parallel from several remote repositories (but parallel search is not allowed).

#### Update the GDAL version to 3.0.0

The GDAL version is updated to 3.0.0 in order to be able to write COG GeoTiff through the GDAL writer.
Also, GDAL binaries for MacOSX are added. (they were missing in the past)
Allow usage of installed GDAL distribution on OS (supported versions from 2.0.X to 3.0.X).
Allow setting which GDAL distribution is used by SNAP (installed or internal distribution).


### List of solved issues
#### Bug
    * [SIITBX-290] - Radiometric Index Operators do not handle GeoCoding correctly
    * [SIITBX-297] - Unit not correct after Reflectance-to-radiance conversion
    * [SIITBX-335] - Jaxa Forest Land Cover download fails
    * [SIITBX-374] - Kompsat2 data is missing wavelength information
    * [SIITBX-375] - GeoLocation of WorldView2 products not correct
    * [SIITBX-376] - WorldView2 products are missing wavelength information
    * [SIITBX-406] - JP2 reader: color model is not set on bands source images
    * [SIITBX-416] - Generic Region Merging Operator tests have different behavior on Linux and Windows
    * [SIITBX-418] - GDAL reader - windowed reading issues
    * [SIITBX-419] - Geometry bands in Reflectance to Radiance operator cause NullPointerException
    * [SIITBX-420] - Casting Error when opening the B1 and B9 of a Sentinel 2 products on SNAP-Desktop
    * [SIITBX-421] - Export S2 product to GDAL generates an error
    * [SIITBX-422] - Masks are not correctly extracted in certain cases
    * [SIITBX-424] - [GPT] Casting Error with Sentinel 2 products
    * [SIITBX-425] - Unable to view S2 bands
    * [SNAP-1285]  - ConversionException generated when opening a Beam Dimap product (Unknown type in attribute descriptor 'geometry:Point')
    * [SNAP-1289]  - GeoTiff reader fails to read Float32 bands
    * [SNAP-1298]  - "Advanced" button (from file chooser dialog) should not be visibile for File -> Export GDAL option
    * [SNAP-1195]  - Fix operators order in the menu of the Graph Builder
    * [SNAP-1168]  - Fix SubsetUI reference band issue
    * [SNAP-1169]  - Fix Subset operator for multi-resolution products
    * [SNAP-1152]  - Subset operator uses tie-point grids correctly  
    
#### New Feature
    * [STEP-12, SIITBX-373] - Parameters for Reader Plugins
    * [STEP-5,  SIITBX-373] - Windowed Reading of Products
    * [STEP-13, SNAP-1161]  - Product Library GUI Enhancements
    * [STEP-14, SNAP-1162]  - SciHub Search UI Enhancement in Product Library
    * [STEP-15, SNAP-1163]  - Support for New External Data Access Sources via Product Library
    * [SNAP-1225]  - Implement change detection validation trigger for Graph Builder
    * [SNAP-1109]  - Possibility to fix random seed value to reproduce results based on aleatory processing
	* [SNAP-1166]  - Subset operator updates scenegeocoding for single size products
	* [SIITBX-413] - Allow GPT tests to fail graciously in a controlled way
	* [SNAP-1013]  - Implement automated reports web service


#### Improvement
    * [SNAP-1160]  - Allow custom S3 VFS parameters for AWS headers
    * [SIITBX-283] - Remove UI dependencies from jp2-reader and s2tbx-commons
    * [SIITBX-383] - Update the GDAL version
    * [SIITBX-379] - Adapt Operators to use doExecute correctly (part 1)
    * [SIITBX-380, SIITBX-384] - Adapt Operators to use doExecute correctly (part 2)
    * [SIITBX-407] - Revise help material
    * [SIITBX-409] - Clean up dependencies on 3rd-party libraries (CSRO)
    * [SIITBX-431] - Improve reading time of a product/bands within a product for readers using JP2
    * [SIITBX-432] - Improve reading time of a product/bands within a product for readers using GeoTiff
    * [STEP-30, SIITBX-315] - Revise initialise method in operator implementations
    * Moving SNAP development and deployment chain to Zulu OpenJDK 8
    * Moving SNAP installer to install4j 8 

#### Task
    * [STEP-38]    - Wiki shall be used to provide MPR to ESA
    * [STEP-39]    - Unassigned issues in JIRA shall be checked
    * [STEP-40]    - Update JIRA tickets to create proper roadmap
    * [STEP-41]    - List of GUI tests should be established
    * [STEP-43]    - STEP website - SEOM link and logo to be replaced with EO Science for Society
    * [SIITBX-387] - Ignored tests shall be properly annotated
    * [SIITBX-388] - Tests need to be defined well in advance of releases
    * [SIITBX-389] - Graphical test coverage for release 8 needs to agree on early
    * [SIITBX-390] - Check skipped tests because of missing data (wrong path)
    * [SIITBX-428] - Update S2TBX about dialog with new ESA images
    * [SNAP-1262]  - Create wiki on test syntax and functionality
    * [SIITBX-401] - Clean up dependencies on 3rd-party libraries
    * [SIITBX-402] - Check separation between UI and Engine components
    * [SIITBX-322] - Integrate S2TBX tests



Changes in S2TBX 7.0
--------------------

### Main features

#### Snaphu Unwrapping tool adapter

It is now possible to download directly from SNAP the current version of SNAPHU.

#### Forest Cover Change Processor

The Forest Cover Change Processor is a multi-steps iterative process that transform two date images into one forest change product.
The overall principle of the algorithm is to identify the forest objects showing higher reflectance differences between the two images than the other forest objects.

#### Pleiades Product Reader

This module enables the Sentinel Toolbox to read Pleiades data products.

#### Kompsat2 Product Reader

This module enables the Sentinel Toolbox to read Kompsat2 data products.

#### WorldView-2 Product Reader

This module enables the Sentinel Toolbox to read WorldView-2 data products.

#### Spectral Angle Mapper

Spectral Angle Mapper (SAM) is a physically-based spectral classification that uses an n-D angle to match pixels to reference spectra.
The algorithm determines the spectral similarity between two spectra by calculating the angle between the spectra and treating them as
vectors in a space with dimensionality equal to the number of bands.

#### GeFolki Co-registration Processor

The GeFolki algorithm allows the registration of images in a nonparametric and dense way. GeFolki is based on a local method of optical flow derived from the Lucas-Kanade algorithm, with a multiscale implementation, and a specific filtering including rank filtering, rolling guidance filtering and local contrast inversion.

#### IKONOS Product Reader

This module enables the Sentinel Toolbox to read IKONOS data products.

#### ALOS AVNIR-2 Product Reader

This module enables the Sentinel Toolbox to read ALOS AVNIR-2 data products.

#### ALOS PRISM Product Reader

This module enables the Sentinel Toolbox to read ALOS PRISM data products.

### List of solved issues
#### Bug
    * Pleiades Reader, raster operations (resample, reprojection, subset) return error.
    * Pleiades Reader tests are not passed.
    * Kompsat2 Reader, pixels values are incorrectly calculated.
    * WorldView-2 Reader tests are not passed.
    * Spectral Angle Mapper help images are not displayed in Linux.
    * [SIITBX-299] - Negative values getting lost when reading jp2 files (direct mode)
    * [SIITBX-303] - Datatype uint16 is wrongly read as int16 from jp2 files
    * [SIITBX-305] - Help link does not work from menu.
    * [SIITBX-340] - Check REIP processor formula
    * [SIITBX-341] - Check WDVI processor
    * [SIITBX-345] - GeFolki Co-registration Operator not working in gpt
    * [SIITBX-347] - RapidEye reader not working in gpt
    * [SIITBX-348] - Spot reader not working in gpt
    * [SIITBX-350] - MCARI processor formula incorrect in help
    * [SIITBX-351] - TSAVI processor incorrect formula
    * [SIITBX-352] - MSAVI2 processor incorrect formula
    * [SIITBX-353] - NDPI (Normalized Difference Pond Index) processor incorrect formula
    * [SIITBX-366] - Muscate reader takes too long time to open product from both locally and VFS
    * [SIITBX-367] - Decode qualification not correctly evaluated by Sentinel-2 reader with relative path
    * [SIITBX-372] - Ikonos and AVNIR-2 RGBProfiles are allows shown

#### New Feature
    * [SIITBX-141] - Add PLEIADES reader
    * [STEP-7]     - WorldView Product Reader (ESA archive format)
    * [STEP-8]     - IKONOS Product Reader
    * [STEP-29]    - ALOS AVNIR-2 / PRISM Reader

#### Improvement
    * [SIITBX-304] - Use object tag for external link.
    * [SIITBX-306] - Entry in ToC of Help should be renamed.
    * [SIITBX-354] - Update S2TBX readers for VFS
    * [SIITBX-359] - Spectral Angle Mapper usage from GPT
    * [SIITBX-368] - Sentinel-2 L1B reader takes too long time to open zipped product due to JP2 reader bottleneck
    * [SIITBX-371] - Allow WorldView2 reader to read zipped products



Changes in S2TBX 6.0
--------------------

### Main features

#### Sentinel-2 Product Reader

##### Read detector footprint masks per detector

The MSI instrument of Sentinel-2 is composed by different detectors, which have a different viewing geometry.
Some masks representing these detector for every band have been included in the reader, in order to identify
from which detector is coming every pixel.


##### Support for ZIP inputs

The Sentinel-2 reader is now able to read the zip products directly. It is no more needed to uncompress
the product before working with it in SNAP.

##### Sentinel-2B products

The new Sentinel-2B products are also supported by the Sentinel-2 reader.

#### Segmentation processor

The Generic Region Merging Processor allows the segmentation of images of arbitrary size.
It is based on the article A Scalable Tile-Based Framework for Region Merging Segmentation written
by Pierre Lassale, Jordi Inglada, Julien Michel, Manuel Grizonnet, and Julien Malik.

(http://ieeexplore.ieee.org/document/7101250/)

#### Multi-size mosaic processor

A new algorithm that will be used when at least one of the input products is multisize,
otherwise the original mosaic operator is called.
This is fully operational for single granule Sentinel-2 products.

#### S2 Resampler processor

A new processor that allows to resample Sentinel-2 products having into account
the particularities of the angle bands.

#### OpenJp2 JNA

A new option for using a JNA wrapper for the popular OpenJpeg (OpenJp2) library has been included.
To activate it, the user have to create a new properties in the s2tbx property file. It has to be added:
use.openjp2.jna=true

#### Sen2Cor tool adapter

##### Advanced configuration

Now, it is possible to configure more parameters when running sen2cor from SNAP.
Before, the only parameter was the spatial resolution. Currently, the user can choose
whether terrain correction should be applied or not, the aerosol type, the ozone, the visibility...
and all the parameters available in the GIPP file of sen2cor.

##### Sen2Cor download and installation from SNAP

It is now possible to download directly from SNAP the current version of sen2cor and use it without installing anything else.
This beta version of the sen2cor plugin makes possible to execute sen2cor without installing Anaconda.

#### Sen2Three

Added support for the new product format introduced in autumn 2016 (PSD 14).

### List of solved issues
#### Bug
    * [SIITBX-248] - L2A SCL Masks do not appear to BandMath on GrephBuilder
    * [SIITBX-251] - Reflectance2Radiance operator is not working with Sentinel-2 new format
    * [SIITBX-253] - BaseProductReaderPlugIn getDecodeQualification is too slow
    * [SIITBX-255] - S2 jpg2000 RGB issue
    * [SIITBX-256] - GDAL reader interferes with other readers
    * [SIITBX-257] - Help is broken after updating modules
    * [SIITBX-258] - java.nio.file.ProviderNotFoundException when trying to open a *.tgz file
    * [SIITBX-259] - Link to help does not work for S2 MCI processor
    * [SIITBX-260] - Preset name in S2 MCI processor is wrong
    * [SIITBX-261] - S2 cache clean-up not working
    * [SIITBX-263] - "Generic Region Merging" help is disabled in the operator GUI.
    * [SIITBX-265] - Image link not working properly in milti-size mosaic product
    * [SIITBX-266] - In multi-size mosaic GUI, "Perform mosaic at native resolution" checkbox disables the "overlapping methods" combo
    * [SIITBX-267] - In the Generic Region Merging operator the geocoding of the input product is not copied to the target product
    * [SIITBX-268] - IllegalArgumentException in mosaic operator
    * [SIITBX-269] - Changing the mosaic bounds has no effect in the ouput (module multi-size mosaic)
    * [SIITBX-276] - Radiometric indices not working on not reprojected data
    * [SIITBX-279] - (AP.8) Radiometric Indices have problems when used in a graph
    * [SIITBX-280] - JP2 Reader is not able to open jp2 image generated with sen2cor at 10 or 20 m resolution
    * [SIITBX-285] - S2 reader cannot open by folder name the L2A product with old naming convention
    * [SIITBX-286] - S2 angles are not well computed when angle grids are not in order in the granule metadata file.

#### New Feature
    * [SIITBX-272] - Segmentation Processor should be implemented in S2TBX
    * [SIITBX-273] - Multi-size mosaic not available in SNAP
    * [SIITBX-281] - CCI Land Cover module for CCI 2015 global map cover

#### Improvement
    * [SIITBX-118] - S-2 MSI readers to use ZIP-files as input
    * [SIITBX-120] - Read DETECTOR FOOTPRINT masks per detector
    * [SIITBX-157] - S2-MSI reader is too slow
    * [SIITBX-225] - Sen2Cor adapter should allow to set advanced GIPP parameters
    * [SIITBX-235] - Improved error diagnosis for openjpeg executables
    * [SIITBX-262] - Better support for *.ers files by GDAL reader
    * [SIITBX-264] - Generic Region Merging default parameters.
    * [SIITBX-271] - Help missing for Generic Region Merging operator
    * [SIITBX-274] - Sen2Three - Support for PSD 14
    * [SIITBX-275] - Download and installation of sen2cor tool from SNAP



Changes in S2TBX 5.0
--------------------

### Main features

#### Sentinel-2 Product Reader

##### Support for PSD 14 Test product

In autumn 2016 a new product format will be introduced.
This new format features the following main evolutions:

- The product naming (including the naming of folders and files inside the product structure) has been compacted
- The products distributed on the Sentinels Data Hub will embed one single tile of the tiling grid
- A full resolution True-Colour Image (TCI) will be included in every product

(http://step.esa.int/main/sentinel_2l1c_evolution2016/)

The Sentinel-2 reader is now able to support the test product (PSD 14) available at this date for both L1C and L2A levels.
Other formats were implemented but could not be tested.
PSD 13 products are supported as before.

##### New RGB Profiles

More RGB profile are available: False-color Infrared, False-color Urban, Agriculture, Atmospheric penetration,
Healthy Vegetation, Land/Water, Atmospherical Removal, Shortwave Infrared, Vegetation Analysis.
RGB profiles over bands of different resolution can only be seen after resampling the product.

##### Tile Index Band

The different tiles/granules composing the full product overlap.
Some "tile_id" bands and masks has been included to indicate the granule from which each pixel is extracted.

##### Sentinel-2 Product Reader documentation

* Overview of the instrument
* Description of supported products
* Description of product format
* Description of some generated bands: angles, tile index

##### Other Sentinel-2 Reader improvements

Granules can now be opened without reading the main XML file

Performance optimisations were performed, it is now much faster to open a product:

- New method for reading the GML masks
- New method for metadata reading

Clean properly cache


#### New Processors

##### Reflectance to Radiance Processor

The Reflectance to Radiance Processor converts the Top Of Atmosphere (TOA) reflectance to radiance from a given product.
The formula implemented is:

radiance = pixelValue * cosinus(radians(incidenceAngle)) * solarIrradiance * scale / (pi * d2)

(the variables are described in the SNAP help)

For Sentinel-2 the incidence angle is replaced with the values from the sun_zenith band.

##### Sentinel-2 Water Processor - MCI

The Sentinel-2 MCI Processor calculates the Maximum Chlorophyll Index by exploiting the height of a measurement over a specific baseline.

##### L3 processor integrated via the Standalone Tools Adapter

A new Sen2Three plugin is available to call Sen2Three from the S2TBX, based on generic StandAlone Tools Adapter.

Sen2Three is a level 3 processor for the Spatio-Temporal Synthesis of bottom of atmosphere corrected Sentinel-2 level 2a images.

(http://step.esa.int/main/third-party-plugins-2/sen2three/)


#### Non-native API Wrapper

##### OTB Python API inside SNAP Operator

A example explaining how to access OTB C++ application via Python API inside a SNAP Operator Plugin has been created.
The example demonstrates computing ndvi for a source product opened in SNAP through OTB



### List of solved issues
#### Bug
    * [SIITBX-228] - S2-MSI reader does not load correctly the angles in some products
    * [SIITBX-229] - Sen2Three progress is going over 100%
    * [SIITBX-230] - SPOT-6 Reader test if failing
    * [SIITBX-236] - View synchronization between bands and index masks does not work properly in Sentinel-2 products
    * [SIITBX-240] - The Mosaic and SC map of Sentinel-2 level3 products are only available at one resolution
    * [SIITBX-241] - The cache in memory is not properly cleaned when the user closes a Sentinel-2 product
    * [SIITBX-243] - RGB image views do not update correctly on Mac OS X or even make OS unresponsive
    * [SIITBX-245] - Cloud_Coverage_Assessment not represented in SNAP

#### Improvement
    * [SIITBX-111] - Sentinel-2 Reader documentation
    * [SIITBX-180] - Add more RGB profiles
    * [SIITBX-216] - Add a band keeping track of tile ids in L1C/L2A product
    * [SIITBX-237] - Tile Index band : rename, group
    * [SIITBX-239] - Support for opening a granule without needing files from the full product.
    * [SIITBX-247] - Support for reading S2 products with new format (PSD14)

#### Requirement
    * [SIITBX-5] - Extension to diff. Language
    * [SIITBX-6] - non-native API Wrapper
    * [SIITBX-26] - Processor reflectance to radiance
    * [SIITBX-28] - Processor L3
    * [SIITBX-29] - Water Processor



Changes in S2TBX 4.0
--------------------

### Main features

#### Sentinel-2 Product Reader

##### "Long Paths" issue on Windows

Multiple fixes for minimizing the impact of the "Long Paths" issue
on Windows OS. As long as you managed to uncompress a Sentinel-2 product
on your HDD from the Windows Explorer, it will open correctly inside SNAP

##### Only multi-size product reading

The specific 10m, 20m and 60m readers have been removed. They were doing
an automatic nearest neighbor resampling of any band to the target resolution,
and this work is much better done with the Resampling operator, available
via the Raster->Geometric Operations->Resampling menu.
The Resampling operator allows for more choices of the target resolution,
and allows one to choose the up and downsampling algorithms.

This also has the advantage that products where all granules are in the same
UTM zone are read automatically by a single reader, so does not trigger the
combo box for choosing the actual reader to use anymore.
Products containing granules in several distincts UTM zones still require the
user to choose which UTM zone to read.

The Level-2A reader, reading products produced by Sen2Cor, has been updated
to provide multi-size products, in line with what is done for L1C products.
It chooses the most precise resolution available for each band : L1C products
processed by sen2cor up to 20m will have B1 and B9 at 60m, and all other bands at 20m.
L1C products processed by sen2cor up to 10m will have B1 and B9 at 60m, B5/B6/B7/B8a/B11/B12 at 20m,
and B2/B3/B4/B8 at 10m.

##### S2 product can be opened using their directory

It is now possible to drag-and-drop the directory containing a S2 product
into the Product Explorer window to open them. No need to go find the xml
file anymore.

##### Reflectance scaling

The scale factor of 10000 used to convert reflectance from their raw integer
value to a meaningful reflectance value in [0, 1] is applied during product reading.

In the L2A product reader, appropriate scaling factors are applied to AOT and WVP bands
to produce physical values.

##### Metadata improvements

The granule-specific metadata were not easily distinguishable.
They now contain the MGRS tile identifier.

##### L2A Products now compatible with GPT

A small issue prevented L2A products from being used with the GPT.
This is now fixed.

##### Sun and Viewing angles

* The angles are now read at their original resolution, as provided in
the metadata (5000 m). This will reduce a lot the HDD space requirements
when converting S2 products to other format (BEAM-DIMAP, GeoTIFF).

* The angles grids of adjacent tiles are automatically mosaicked.
Since their resolution (5000m) is not compatible with the distance
between two adjacent tiles (99960m), adjacent angles grid are interpolated
to produce a mosaic at 5000m resolution. This particular issue has been
reported to the S2-MPC.

* Viewing angles retrieval from the metadata was buggy in previous SNAP versions.
Now the Sentinel-2 reader produces one viewing angle grid for each band independently,
merging the different viewing angle grids of each detector into one single image.
For ease of use, a mean viewing angle grid is also provided, which averages
the data provided by each band-specific grid.
Their actual use in processors is still problematic since S2 products do not
provide precise enough information about the detector from which a reflectance
band pixel has been interpolated from. In the neighborhood of a detector switch,
the viewing angles thus cannot be trusted.

##### Product time information

S2 products now include proper time information (Sensing start/stop time).

##### Masks grouping

Masks are now grouped by category, to avoid the masks folder to be too populated.

##### Level-2A Scene Classification colors

The different indices of the Scene Classification now follow the Sen2Cor documentation.
For consistency, the masks generated for each class in the Scene Classification band 
also follow the same colors.

##### Level-3 Products support

Added support for the Level-3 products as generated by Sen2Three,
the multitemporal synthesis processor, developed by Telespazio Vega Gmbh

##### Reader optimizations

* OpenJPEG, the underlying tool which decompress the JP2 images has been updated
to include important performance optimizations.

* The reading of cloud masks (opaque_clouds and cirrus_clouds) available in L1C products,
and copied as-is in L2A products by Sen2Cor, can be disabled in the
preferences (Tools->Options->S2TBX). For cloudy S2 products, the loading time
can be very high because of the reading of the cloud masks.
In addition, L2A products provide a far more optimized opaque clouds/cirrus masks
in raster format, as part of the Scene Classification band, making the cloud masks
available in L1C products useless.

* The interpretation of GML masks has been optimized

#### Spot 6/7 reader

##### RGB rendering

RGB rendering of Spot 6/7 products appeared overly dark. These has been fixed, 
by not using the histogram provided in the metadata.

The order of the bands has been fixed for the default profile.

#### Cache management

The Sentinel-2 Toolbox caches uncompressed regions of the JPEG2000 files
read by the Sentinel-2 reader, the generic JP2 reader and the Spot 6/7 reader.
Those files are stored in the user home directory and can grow big.

The cache can now be emptied regularly at startup. The frequency of the cache
clean up can be tuned in the preferences pane.
The default behavior for new installation is to remove from the cache the files
that are more-than-a-week old.

#### New L2B Biophysical Products processor

The Sentinel-2 Toolbox now provides a biophysical products processor,
developped by CS and INRA.

The processor computes LAI, FAPAR, FCOVER, CWC, CCC from top-of-canopy reflectances.

#### New IdePix Processor

The Sentinel-2 Toolbox now includes the Idepix Processor for Sentinel-2,
developped by Brockmann Consult Gmbh.

The Idepix Processor provides a pixel classification into properties 
such as clear/cloudy, land/water, snow, ice etc.

The Sentinel-3 Toolbox provides similar functionnality for MODIS
and Landsat-8 data.

#### New Radiometric Indices Processor

The Sentinel-2 Toolbox now includes an important number of well-known
radiometric indices processors, developped by CS-Romania.

The following indices are made available :
* Vegetation indices : DVI, RVI, PVI, IPVI, WDVI, TNDVI, GNDVI, GEMI, ARVI,
NDI45, MTCI, MCARI, REIP, S2REP, IRECI, PSSRa
* Soil indices : SAVI, TSAVI, MSAVI, MSAVI2, BI, BI2, RI, CI
* Water indices : NDWI, NDWI2, MNDWI, NDPI, NDTI

### List of solved issues

#### Bug
    * [SIITBX-100] - L1B reader does not read products with several datastrips correctly
    * [SIITBX-114] - L2A readers says it is intended for all resolutions whatever the product type
    * [SIITBX-165] - [L2A] Duplicated granule metadata
    * [SIITBX-169] - Viewing angles mix data
    * [SIITBX-175] - L1B from 10/12/2015 not opened corectly
    * [SIITBX-181] - L1C products are missing time information
    * [SIITBX-187] -  Error opening S2 image : IllegalArgumentException: dataType out of range! 
    * [SIITBX-188] - Problem reading L2A files with GPT
    * [SIITBX-191] - No intended reader for Spot 6 products
    * [SIITBX-192] - L1B reader at 60m actually reads 20m bands
    * [SIITBX-193] - S2 reader cannot read "old" 12 bpp product
    * [SIITBX-194] - L2A scene classification has wrong colors
    * [SIITBX-195] - JP2 reader cache should be versionned
    * [SIITBX-196] - JP2 reader cache : use full paths of products
    * [SIITBX-197] - JP2 reader cache : invalidate cache based on dates
    * [SIITBX-202] - Infinite recursion when shortening Windows path
    * [SIITBX-203] - Sentinel-2 MSI reader displays grey image with too long paths
    * [SIITBX-204] - Milliseconds are read as microseconds
    * [SIITBX-205] - An error occurs when a L1B product is opened with "all resolution" reader.
    * [SIITBX-206] - Spot-6 images are black or dark
    * [SIITBX-207] - Spot-6 unit is "nanometer"
    * [SIITBX-208] - Wrong RGB profile for SPOT 6/7 Reader
    * [SIITBX-209] - S2TBX Cache increases continuously
    * [SIITBX-220] - Some acceptance tests for MSI reader are not passed. 

#### Improvement
    * [SIITBX-117] - S-2 MSI readers to use directories as input
    * [SIITBX-172] - [L2A] Implement multisize reader
    * [SIITBX-199] - Better error report for OpenJPEG executable binary compatibility failure
    * [SIITBX-200] - OpenJPEG binaries do not work on MacOSX 10.7
    * [SIITBX-201] - OpenJPEG binaries deployed in auxdata should be versioned
    * [SIITBX-211] - Change mono-resolution readers to be SUITABLE instead of INTENDED
    * [SIITBX-212] - S2 tile metadata improvement
    * [SIITBX-213] - Integrate multithreaded openjpeg
    * [SIITBX-214] - L2A scl masks do not have the same color as their index coding palette
    * [SIITBX-217] - Configuration of MSI readers
    * [SIITBX-221] - Scale physical bands of S2 products
    * [SIITBX-222] - Read Dark Dense Vegetation output when it is generated by sen2cor
    * [SIITBX-223] - Implement L2B Biophysical Processor

#### Requirement
    * [SIITBX-20] - 4 segmentation algo
    * [SIITBX-21] - Classification


Changes in S2TBX 3.0
--------------------

### Main features

  * S2-MSI reader: many fixes and improvements, including
    * Big S2 datasets could lead to "Too many file opened" exceptions.
    * Angles grid now strictly fit to image in terms of size.
    * The S2 and JP2 readers were not usable in a headless environment.
    * Fixed description and units for S2 bands
    * Resource leaks detected by Coverity (https://scan.coverity.com/) fixed
    * Better validation of Angles Grid data
    * Color rendering of L2A Scene Classification now fits with PDD & ATBD
  * Spot-6 Reader
  * OTB tools integration via STA :
    * HaralickTextureExtraction
    * MeanShiftSmoothing
    * MultivariateAlterationDetector
    * Pansharpening-bayes
    * Pansharpening-lmvm
    * Pansharpening-rcs
    * Rasterization-image
    * Rasterization-manual
    * Segmentation-cc
    * Segmentation-meanshift
    * Segmentation-mprofiles
    * Segmentation-watershed
    * SFSTextureExtraction

### Solved issues

  * [SIITBX-90] - 'Export mask definition(s) to XML file does not work
  * [SIITBX-103] - Add Licences for each module
  * [SIITBX-128] - Nicely handle products with subset of bands
  * [SIITBX-144] - Unable to use openjpeg in a snap-engine only environment
  * [SIITBX-145] - S2 readers : Missing band description
  * [SIITBX-146] - S2 readers:  missing band unit
  * [SIITBX-153] - Angles of S2 product don't get saved correctly
  * [SIITBX-154] - Geo-referencement of S2 product don't get saved correctly
  * [SIITBX-161] - Decoding JPEG2000 files became slow
  * [SIITBX-168] - [L2A] Wrong description/unit for quality bands
  * [SIITBX-173] - "Too many open files" when reading big S2 datasets
  * [SIITBX-176] - Can't producd RGB with product DEIMOS-1_INDONESIA-TrueColor-22m.zip
  * [SIITBX-177] - JP2 Reader has no unit tests
  * [SIITBX-179] - Scottish effect when displaying 20m bands
  * [SIITBX-182] - Sentinel-2 reader a bottleneck for generally opening products
  * [SIITBX-184] - Problem with Spot 6 masks: memory consumption
  * [SIITBX-185] - SPOT 6 masks are displayed by default
  * [SIITBX-186] - Can't open product SPOT-6_1.5m_Ortho_Bundle_12_bits
  * [SIITBX-189] - Issue to open/import/read directly jpeg2000 files on Linux
  * [SIITBX-119] - Products read by S-2 MSI readers don't work well with spectrum view
  * [SIITBX-133] - Use JP2 reader for implementing S2MSI reader
  * [SIITBX-166] - Fix copyright headers in source code
  * [SIITBX-167] - Missing description and Unit for sun/view angles grids
  * [SIITBX-170] - [OpenJPEG] only deploy the binaries
  * [SIITBX-171] - [OpenJPEG] Set permissions during deployment in auxdata folder
  * [SIITBX-142] - Add SPOT 6-7 readers
  * [SIITBX-190] - OpenJPEG 2.1 outputs TIF with wrong reflectance value


Changes in S2TBX 2.0
--------------------

### Main features

* S2-MSI reader
  * L1C
     * All spectral bands are read, either in native resolution, or resampled to a common resolution
     * Support for the latest PSD
     * Interpretation of all GML quality masks
  * L2A
     * All spectral bands are read by L2A reader, currently with resampling
     * Reading of AOT, Water Vapour, Cloud & Snow Confidence, Scene Classification
     * Support for products with tiles in different UTM zones, like in L1C reader
  * All S2 readers
     * Lots of code refactoring
     * Optimization of the mosaic operators processing chain
     * Fixed installation issues with OpenJPEG executables
     * Migration to NetBeans platform
     * Lots of bug fixes
* Sen2Cor Standalone Tool Adapter
  * Provided a new Sen2Cor plugin to call Sen2Cor from the S2TBX, based on generic StandAlone Tools Adapter
* SPOT reader
  * SPOT 1/2/3/4/5 support (DIMAP & SpotView)
  * SPOT4-Take5 & SPOT5-Take5 support
* RapidEye reader
* Deimos reader

### Solved issues

    * [SIITBX-38] - L1C Reader Implementation
    * [SIITBX-39] - L2A Reader Implementation
    * [SIITBX-40] - L1B Reader implementation
    * [SIITBX-41] - Multi-Resolution support implementation
    * [SIITBX-42] - Read product over severall UTM zones implementation
    * [SIITBX-58] - Read GML masks
    * [SIITBX-69] - Synchronize S-2 project with SNAP-2.0.0-SNAPSHOT
    * [SIITBX-70] - Check the Multi-Size product feature
    * [SIITBX-71] - Adapt the S-2 reader to multi-resolution
    * [SIITBX-72] - Update of the readers name
    * [SIITBX-73] - Copyrights to fix
    * [SIITBX-74] - Solar angles should be displayed properly
    * [SIITBX-78] - Hard-code resolution information depending on the bands
    * [SIITBX-1] - Export of S2 product to GeoTiff and ENVI fails for some combination of bands
    * [SIITBX-43] - RapidEye reader misreading start time
    * [SIITBX-82] - Check L2A_AtmCorr.py lines 4783 - 4789 with original ATCOR code and correct, if needed
    * [SIITBX-87] - Sentinel-2 readers don't all appear in 'File --> Import --> Optical Sensors menu
    * [SIITBX-88] - Wrong file filters for S2 products with File --> Open Product
    * [SIITBX-91] - RGB Combination from generated bands does not work
    * [SIITBX-94] - It is not possible anymore to open a single granule.
    * [SIITBX-95] - Most graph builder operations are not working with S2 products
    * [SIITBX-97] - Cannot read latest L1B product
    * [SIITBX-98] - S2 reader tries to read dimap product
    * [SIITBX-99] - Multi UTM zones support for L2A
    * [SIITBX-102] - L1B granule width must be dynamically found
    * [SIITBX-104] - Masks appear only when they contain polygons
    * [SIITBX-106] - Sentinel-2 readers have NetBeans dependencies
    * [SIITBX-107] - NPE occurs in J2kProductReaderPlugin when trying to open file without extension
    * [SIITBX-108] - When opening a produt at 20 or 60m, references to 10m remains
    * [SIITBX-112] - L2A bands can't be opened at 10m
    * [SIITBX-123] - JP2 reader should use SNAP cache dir
    * [SIITBX-126] - Band to Scene transform for Multi resolution products
    * [SIITBX-127] - NPE when reading a L2A product via gpt
    * [SIITBX-129] - L2A reader should read scene classification, WV and AOT bands
    * [SIITBX-130] - L2A reader band names
    * [SIITBX-131] - S2 masks have too complicated names
    * [SIITBX-135] - Missing layer.xml for JP2 reader
    * [SIITBX-143] - File->Import->Sentinel2 menu does not work anymore
    * [SIITBX-147] - S2-L1C tile JP2 file not read correctly by JP2reader
    * [SIITBX-148] - MacOSX exception when reading a product in beta8
    * [SIITBX-149] - opening different instances with Sentinel-2 reader does not behave as expected
    * [SIITBX-150] - Can't open product with "Import->OpticalSensors->Sentinel2->S2-MSI L1C"
    * [SIITBX-152] - OpenJPEG can't decode some 20m bands
    * [SIITBX-155] - opj_decompress cannot be found if s2tbx is installed after main intallation
    * [SIITBX-156] - If S2TBX is installed via the plugin manager, S2 MSI Reader does not work
    * [SIITBX-158] - Handle OpenJPEG decoding errors
    * [SIITBX-159] - Multi-resolution reader on graph builder
    * [SIITBX-160] - L1C Cloud masks generate NPE
    * [SIITBX-162] - Synchronization of image views does not work correctly for MSI L1C products
    * [SIITBX-163] - S2 masks all have the same colour
    * [SIITBX-164] - Masks are available only for 10m bands in the mask manager
    * [SIITBX-68] - Graph Builder: update the S-2 processors
    * [SIITBX-75] - Fix the tests on S2
    * [SIITBX-115] - Change the cache dir for S2 readers
    * [SIITBX-2] - S2 L1C reader is resampling all bands at 10m
    * [SIITBX-101] - L1B reader should adapt JP2 tiling scheme dynamically
    * [SIITBX-116] - Missing S-2 MSI L1C reader that reads all bands at same size
    * [SIITBX-124] - JP2 reader architecture update
    * [SIITBX-134] - Mix of J2K and JP2 in J2K reader
    * [SIITBX-3] - CLONE - RQT 87, Smart configurator
    * [SIITBX-4] - S2 reader
    * [SIITBX-7] - Standalone tool adapter
    * [SIITBX-8] - 3rd party plugin
    * [SIITBX-9] - Undo/Redo
    * [SIITBX-10] - Progress indicator
    * [SIITBX-18] - Read S2 GML masks
    * [SIITBX-22] - Extract pixel value
    * [SIITBX-25] - Basic image filtering
    * [SIITBX-27] - Processor integrate L2A module
    * [SIITBX-30] - Benchmark by optimizer
    * [SIITBX-31] - Optimizer propose setup values
    * [SIITBX-32] - Re-running the smart configurator
    * [SIITBX-33] - Processing preview
    * [SIITBX-35] - Performance Optimization (2/8)
    * [SIITBX-36] - Predefined processing chains
