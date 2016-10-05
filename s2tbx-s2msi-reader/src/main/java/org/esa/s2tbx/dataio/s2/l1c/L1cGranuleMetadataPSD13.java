package org.esa.s2tbx.dataio.s2.l1c;

import com.bc.ceres.core.Assert;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.s2.S2BandInformation;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.snap.core.datamodel.MetadataAttribute;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.ProductData;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by obarrile on 30/09/2016.
 */
public class L1cGranuleMetadataPSD13 extends XmlMetadata implements IL1cGranuleMetadata {


    MetadataElement simplifiedMetadataElement;

    private static class L1cGranuleMetadataPSD13Parser extends XmlMetadataParser<L1cGranuleMetadataPSD13> {

        public L1cGranuleMetadataPSD13Parser(Class metadataFileClass) {
            super(metadataFileClass);
            setSchemaLocations(L1cMetadataPSD13Helper.getSchemaLocations());
        }

        //TODO validate schema
        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public static L1cGranuleMetadataPSD13 create(Path path) throws IOException {
        Assert.notNull(path);
        L1cGranuleMetadataPSD13 result = null;
        InputStream stream = null;
        try {
            if (Files.exists(path)) {
                stream = Files.newInputStream(path, StandardOpenOption.READ);
                //noinspection unchecked
                L1cGranuleMetadataPSD13Parser parser = new L1cGranuleMetadataPSD13Parser(L1cGranuleMetadataPSD13.class);
                result = parser.parse(stream);
                result.setName("Level-1C_Tile_ID");
                String metadataProfile = result.getMetadataProfile();

            }
        } catch (Exception e) {
            //Logger.getLogger(GenericXmlMetadata.class.getName()).severe(e.getMessage());
            //TODO
        } finally {
            if (stream != null) try {
                stream.close();
            } catch (IOException e) {
                // swallowed exception
            }
        }
        return result;

    }


    public L1cGranuleMetadataPSD13(String name) {
        super(name);
    }

    @Override
    public int getNumBands() {
        return 0;
    }

    @Override
    public String getProductName() {
        return null;
    }

    @Override
    public String getFormatName() {
        return null;
    }

    @Override
    public int getRasterWidth() {
        return 0;
    }

    @Override
    public int getRasterHeight() {
        return 0;
    }

    @Override
    public String[] getRasterFileNames() {
        return new String[0];
    }

    @Override
    public ProductData.UTC getProductStartTime() {
        return null;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        return null;
    }

    @Override
    public ProductData.UTC getCenterTime() {
        return null;
    }

    @Override
    public String getProductDescription() {
        return null;
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public String getMetadataProfile() {
        return null;
    }

    @Override
    public S2Metadata.ProductCharacteristics getTileProductOrganization() {

        S2Metadata.ProductCharacteristics characteristics = new S2Metadata.ProductCharacteristics();
        characteristics.setSpacecraft("Sentinel-2");

        characteristics.setProcessingLevel("Level-1C");

        double toaQuantification = 10000; //Default value, the value is only in the metadata product
        characteristics.setQuantificationValue(toaQuantification);

        List<S2BandInformation> aInfo = L1cMetadataProc.getBandInformationList (toaQuantification);
        int size = aInfo.size();
        characteristics.setBandInformations(aInfo.toArray(new S2BandInformation[size]));

        return characteristics;
    }

    @Override
    public Map<S2SpatialResolution, L1cMetadata.TileGeometry> getTileGeometries() {

        Map<S2SpatialResolution, L1cMetadata.TileGeometry> resolutions = new HashMap<>();
        for (String res : getAttributeValues(L1cPSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION)) {
            S2SpatialResolution resolution = S2SpatialResolution.valueOfResolution(Integer.parseInt(res));
            L1cMetadata.TileGeometry tgeox = new L1cMetadata.TileGeometry();

            tgeox.setUpperLeftX(Double.parseDouble(getAttributeSiblingValue(L1cPSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION, res,
                                                                            L1cPSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_ULX, "0")));
            tgeox.setUpperLeftY(Double.parseDouble(getAttributeSiblingValue(L1cPSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION, res,
                                                                            L1cPSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_ULY, "0")));
            tgeox.setxDim(Double.parseDouble(getAttributeSiblingValue(L1cPSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION, res,
                                                                      L1cPSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_XDIM, "0")));
            tgeox.setyDim(Double.parseDouble(getAttributeSiblingValue(L1cPSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION, res,
                                                                      L1cPSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_YDIM, "0")));
            tgeox.setNumCols(Integer.parseInt(getAttributeSiblingValue(L1cPSD13Constants.PATH_GRANULE_METADATA_SIZE_RESOLUTION, res,
                                                                       L1cPSD13Constants.PATH_GRANULE_METADATA_SIZE_NCOLS, "0")));
            tgeox.setNumRows(Integer.parseInt(getAttributeSiblingValue(L1cPSD13Constants.PATH_GRANULE_METADATA_SIZE_RESOLUTION, res,
                                                                       L1cPSD13Constants.PATH_GRANULE_METADATA_SIZE_NROWS, "0")));
            resolutions.put(resolution, tgeox);
        }

        return resolutions;
    }

    @Override
    public String getTileID() {
        return getAttributeValue(L1cPSD13Constants.PATH_GRANULE_METADATA_TILE_ID, null);
    }

    @Override
    public String getHORIZONTAL_CS_CODE() {
        return getAttributeValue(L1cPSD13Constants.PATH_GRANULE_METADATA_HORIZONTAL_CS_CODE, null);
    }

    @Override
    public String getHORIZONTAL_CS_NAME() {
        return getAttributeValue(L1cPSD13Constants.PATH_GRANULE_METADATA_HORIZONTAL_CS_NAME, null);
    }

    @Override
    public int getAnglesResolution() {
        return Integer.parseInt(getAttributeValue(L1cPSD13Constants.PATH_GRANULE_METADATA_ANGLE_RESOLUTION, "0"));
    }

    @Override
    public L1cMetadata.AnglesGrid getSunGrid() {

        return S2Metadata.wrapAngles(getAttributeValues(L1cPSD13Constants.PATH_GRANULE_METADATA_SUN_ZENITH_ANGLES),
                                     getAttributeValues(L1cPSD13Constants.PATH_GRANULE_METADATA_SUN_AZIMUTH_ANGLES));


        /*L1cMetadata.AnglesGrid ag = null;

        String[] sunZenith = getAttributeValues(L1cPSD13Constants.PATH_GRANULE_METADATA_SUN_ZENITH_ANGLES);
        String[] sunAzimuth = getAttributeValues(L1cPSD13Constants.PATH_GRANULE_METADATA_SUN_AZIMUTH_ANGLES);

        if(sunAzimuth == null || sunZenith == null) {
            return ag;
        }
        int nRows = sunZenith.length;
        int nCols = sunZenith[0].split(" ").length;

        ag = new L1cMetadata.AnglesGrid();
        ag.setAzimuth(new float[nRows][nCols]);
        ag.setZenith(new float[nRows][nCols]);

        for (int rowindex = 0; rowindex < nRows; rowindex++) {
            String[] zenithSplit = sunZenith[rowindex].split(" ");
            String[] azimuthSplit = sunAzimuth[rowindex].split(" ");
            if(zenithSplit == null || azimuthSplit == null || zenithSplit.length != nCols ||azimuthSplit.length != nCols) {
                return null;
            }
            for (int colindex = 0; colindex < nCols; colindex++) {
                ag.getZenith()[rowindex][colindex] = Float.parseFloat(zenithSplit[colindex]);
                ag.getAzimuth()[rowindex][colindex] = Float.parseFloat(azimuthSplit[colindex]);
            }
        }

        return ag;*/
    }

    @Override
    public S2Metadata.AnglesGrid[] getViewingAnglesGrid() {
        return S2Metadata.wrapStandardViewingAngles(rootElement);

        /*ArrayList<L1cMetadata.AnglesGrid> darr = new ArrayList<>();
        MetadataElement viewingAnglesElements = getMetadataElement().getElement("Geometric_Info").getElement("Tile_Angles");
        for(MetadataElement viewingAnglesElement : viewingAnglesElements.getElements()) {
            if(!viewingAnglesElement.getName().equals("Viewing_Incidence_Angles_Grids")) {
                continue;
            }

            MetadataAttribute[] azAngles = viewingAnglesElement.getElement("Azimuth").getElement("Values_List").getAttributes();
            MetadataAttribute[] zenAngles = viewingAnglesElement.getElement("Zenith").getElement("Values_List").getAttributes();
            int azrows = azAngles.length;
            String[] split = azAngles[0].getData().toString().split(" ");
            int azcolumns = split.length;

            int zenrows = zenAngles.length;
            split = zenAngles[0].getData().toString().split(" ");
            int zencolumns = split.length;

            L1cMetadata.AnglesGrid ag2 = new L1cMetadata.AnglesGrid();
            ag2.setAzimuth(new float[azrows][azcolumns]);
            ag2.setZenith(new float[zenrows][zencolumns]);

            for (int rowindex = 0; rowindex < azrows; rowindex++) {
                split = azAngles[rowindex].getData().toString().split(" ");
                for (int colindex = 0; colindex < azcolumns; colindex++) {
                    ag2.getAzimuth()[rowindex][colindex] = Float.parseFloat(split[colindex]);
                }
            }

            for (int rowindex = 0; rowindex < zenrows; rowindex++) {
                split = zenAngles[rowindex].getData().toString().split(" ");
                for (int colindex = 0; colindex < zencolumns; colindex++) {
                    ag2.getZenith()[rowindex][colindex] = Float.parseFloat(split[colindex]);
                }
            }

            ag2.setBandId(Integer.parseInt(viewingAnglesElement.getAttributeString("bandId")));
            ag2.setDetectorId(Integer.parseInt(viewingAnglesElement.getAttributeString("detectorId")));
            darr.add(ag2);

        }
        return darr.toArray(new L1cMetadata.AnglesGrid[darr.size()]);*/
    }

    @Override
    public S2Metadata.MaskFilename[] getMasks(Path path) {
        S2Metadata.MaskFilename[] maskFileNamesArray;
        List<S2Metadata.MaskFilename> aMaskList = new ArrayList<>();
        String[] maskFilenames = getAttributeValues(L1cPSD13Constants.PATH_GRANULE_METADATA_MASK_FILENAME);
        if(maskFilenames == null) {
            return null;
        }
        for (String maskFilename : maskFilenames) {
            Path QIData = path.resolveSibling("QI_DATA");
            File GmlData = new File(QIData.toFile(), maskFilename);

            aMaskList.add(new S2Metadata.MaskFilename(getAttributeSiblingValue(L1cPSD13Constants.PATH_GRANULE_METADATA_MASK_FILENAME, maskFilename,
                                                                                L1cPSD13Constants.PATH_GRANULE_METADATA_MASK_BAND, null),
                                                       getAttributeSiblingValue(L1cPSD13Constants.PATH_GRANULE_METADATA_MASK_FILENAME, maskFilename,
                                                                                L1cPSD13Constants.PATH_GRANULE_METADATA_MASK_TYPE, null),
                                                       GmlData));
        }

        maskFileNamesArray = aMaskList.toArray(new S2Metadata.MaskFilename[aMaskList.size()]);

        return maskFileNamesArray;
    }

    @Override
    public MetadataElement getMetadataElement() {
        return rootElement;
    }

    @Override
    public MetadataElement getSimplifiedMetadataElement() {
        //TODO ? new parse? or clone rootElement and remove some elements?
        return rootElement;
    }

    @Override
    public void updateName() {
        String tileId = getAttributeValue(L1cPSD13Constants.PATH_GRANULE_METADATA_TILE_ID, null);
        setName("Level-1C_Tile_" + tileId.substring(50, 55));
    }
}
