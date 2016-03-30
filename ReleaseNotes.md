New in S2TBX 3.0
-----------------

* Main features
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

* Solved issues
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


New in S2TBX 2.0
-----------------

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

* Closed issues :
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
