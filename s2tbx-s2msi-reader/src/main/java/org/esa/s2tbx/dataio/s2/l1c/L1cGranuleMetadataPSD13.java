package org.esa.s2tbx.dataio.s2.l1c;

import com.bc.ceres.core.Assert;
import org.apache.commons.math3.util.Pair;
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
import java.util.LinkedHashMap;
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
                result.setName("Level-1C_User_Product"); //TODO
                String metadataProfile = result.getMetadataProfile();

            }
        } catch (Exception e) {
            //Logger.getLogger(GenericXmlMetadata.class.getName()).severe(e.getMessage());
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

        for (String res : getAttributeValues(PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION)) {
            S2SpatialResolution resolution = S2SpatialResolution.valueOfResolution(Integer.getInteger(res));
            L1cMetadata.TileGeometry tgeox = new L1cMetadata.TileGeometry();

            tgeox.setUpperLeftX(Double.parseDouble(getAttributeSiblingValue(PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION, res,
                                                                            PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_ULX, "0")));
            tgeox.setUpperLeftY(Double.parseDouble(getAttributeSiblingValue(PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION, res,
                                                                            PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_ULY, "0")));
            tgeox.setxDim(Double.parseDouble(getAttributeSiblingValue(PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION, res,
                                                                      PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_XDIM, "0")));
            tgeox.setyDim(Double.parseDouble(getAttributeSiblingValue(PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION, res,
                                                                      PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_YDIM, "0")));
            tgeox.setNumCols(Integer.getInteger(getAttributeSiblingValue(PSD13Constants.PATH_GRANULE_METADATA_SIZE_RESOLUTION, res,
                                                                         PSD13Constants.PATH_GRANULE_METADATA_SIZE_NCOLS, "0")));
            tgeox.setNumRows(Integer.getInteger(getAttributeSiblingValue(PSD13Constants.PATH_GRANULE_METADATA_SIZE_RESOLUTION, res,
                                                                         PSD13Constants.PATH_GRANULE_METADATA_SIZE_NROWS, "0")));
            resolutions.put(resolution, tgeox);
        }

        return resolutions;
    }

    @Override
    public String getTileID() {
        return getAttributeValue(PSD13Constants.PATH_GRANULE_METADATA_TILE_ID, null);
    }

    @Override
    public String getHORIZONTAL_CS_CODE() {
        return getAttributeValue(PSD13Constants.PATH_GRANULE_METADATA_HORIZONTAL_CS_CODE, null);
    }

    @Override
    public String getHORIZONTAL_CS_NAME() {
        return getAttributeValue(PSD13Constants.PATH_GRANULE_METADATA_HORIZONTAL_CS_NAME, null);
    }

    @Override
    public int getAnglesResolution() {
        return Integer.getInteger(getAttributeValue(PSD13Constants.PATH_GRANULE_METADATA_ANGLE_RESOLUTION, "0"));
    }

    @Override
    public L1cMetadata.AnglesGrid getSunGrid() {
        L1cMetadata.AnglesGrid ag = null;

        String[] sunZenith = getAttributeValues(PSD13Constants.PATH_GRANULE_METADATA_SUN_ZENITH_ANGLES);
        String[] sunAzimuth = getAttributeValues(PSD13Constants.PATH_GRANULE_METADATA_SUN_AZIMUTH_ANGLES);

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

        return ag;
    }

    @Override
    public L1cMetadata.AnglesGrid[] getAnglesGrid() {
        //TODO
        L1cMetadata.AnglesGrid[] darr = null;

        ///////////////////////////////////////////////////////////////////////
        /*A_GEOMETRIC_INFO_TILE.Tile_Angles ang = product.getGeometric_Info().getTile_Angles();

        L1cMetadata.AnglesGrid[] darr = null;
        if (ang != null) {
            List<AN_INCIDENCE_ANGLE_GRID> filteredListe = ang.getViewing_Incidence_Angles_Grids();

            Map<Pair<String, String>, AN_INCIDENCE_ANGLE_GRID> theMap = new LinkedHashMap<>();
            for (AN_INCIDENCE_ANGLE_GRID aGrid : filteredListe) {
                theMap.put(new Pair<>(aGrid.getBandId(), aGrid.getDetectorId()), aGrid);
            }

            List<AN_INCIDENCE_ANGLE_GRID> incilist = new ArrayList<>(theMap.values());

            darr = new L1cMetadata.AnglesGrid[incilist.size()];
            for (int index = 0; index < incilist.size(); index++) {
                AN_INCIDENCE_ANGLE_GRID angleGrid = incilist.get(index);

                int azrows2 = angleGrid.getAzimuth().getValues_List().getVALUES().size();
                int azcolumns2 = angleGrid.getAzimuth().getValues_List().getVALUES().get(0).getValue().size();

                int zenrows2 = angleGrid.getZenith().getValues_List().getVALUES().size();
                int zencolumns2 = angleGrid.getZenith().getValues_List().getVALUES().get(0).getValue().size();


                L1cMetadata.AnglesGrid ag2 = new L1cMetadata.AnglesGrid();
                ag2.setAzimuth(new float[azrows2][azcolumns2]);
                ag2.setZenith(new float[zenrows2][zencolumns2]);

                for (int rowindex = 0; rowindex < azrows2; rowindex++) {
                    List<Float> azimuths = angleGrid.getAzimuth().getValues_List().getVALUES().get(rowindex).getValue();
                    for (int colindex = 0; colindex < azcolumns2; colindex++) {
                        ag2.getAzimuth()[rowindex][colindex] = azimuths.get(colindex);
                    }
                }

                for (int rowindex = 0; rowindex < zenrows2; rowindex++) {
                    List<Float> zeniths = angleGrid.getZenith().getValues_List().getVALUES().get(rowindex).getValue();
                    for (int colindex = 0; colindex < zencolumns2; colindex++) {
                        ag2.getZenith()[rowindex][colindex] = zeniths.get(colindex);
                    }
                }

                ag2.setBandId(Integer.parseInt(angleGrid.getBandId()));
                ag2.setDetectorId(Integer.parseInt(angleGrid.getDetectorId()));
                darr[index] = ag2;
            }
        }

        return darr;*/
        ///////////////////////////////////////////////////////////////////////

        return null;
    }

    @Override
    public S2Metadata.MaskFilename[] getMasks(Path path) {
        S2Metadata.MaskFilename[] maskFileNamesArray;
        List<L1cMetadata.MaskFilename> aMaskList = new ArrayList<>();
        String[] maskFilenames = getAttributeValues(PSD13Constants.PATH_GRANULE_METADATA_MASK_FILENAME);
        if(maskFilenames == null) {
            return null;
        }
        for (String maskFilename : maskFilenames) {
            Path QIData = path.resolveSibling("QI_DATA");
            File GmlData = new File(QIData.toFile(), maskFilename);
            aMaskList.add(new L1cMetadata.MaskFilename(getAttributeSiblingValue(PSD13Constants.PATH_GRANULE_METADATA_MASK_FILENAME,maskFilename,
                                                                                PSD13Constants.PATH_GRANULE_METADATA_MASK_BAND,null),
                                                       getAttributeSiblingValue(PSD13Constants.PATH_GRANULE_METADATA_MASK_FILENAME,maskFilename,
                                                                                PSD13Constants.PATH_GRANULE_METADATA_MASK_TYPE,null),
                                                       GmlData));
        }

        maskFileNamesArray = aMaskList.toArray(new L1cMetadata.MaskFilename[aMaskList.size()]);

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
}
