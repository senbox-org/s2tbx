package org.esa.s2tbx.dataio.s2.l3.metadata;

import com.bc.ceres.core.Assert;
import org.apache.commons.io.IOUtils;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.filepatterns.NamingConventionFactory;
import org.esa.s2tbx.dataio.s2.filepatterns.SAFECOMPACTNamingConvention;
import org.esa.s2tbx.dataio.s2.l3.L3PSD13Constants;
import org.esa.snap.core.datamodel.MetadataElement;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by obarrile on 07/10/2016.
 */
public class L3GranuleMetadataPSD13 extends GenericXmlMetadata implements IL3GranuleMetadata  {

   String format = "";

    private static class L3GranuleMetadataPSD13Parser extends XmlMetadataParser<L3GranuleMetadataPSD13> {

        public L3GranuleMetadataPSD13Parser(Class metadataFileClass) {
            super(metadataFileClass);
            setSchemaLocations(L3PSD13Constants.getGranuleSchemaLocations());
            setSchemaBasePath(L3PSD13Constants.getGranuleSchemaBasePath());
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public static L3GranuleMetadataPSD13 create(VirtualPath path) throws IOException, ParserConfigurationException, SAXException {
        Assert.notNull(path);
        L3GranuleMetadataPSD13 result = null;
        InputStream stream = null;
        try {
            if (path.exists()) {
                stream = path.getInputStream();
                L3GranuleMetadataPSD13Parser parser = new L3GranuleMetadataPSD13Parser(L3GranuleMetadataPSD13.class);
                result = parser.parse(stream);
                result.updateName();
                result.format = NamingConventionFactory.getGranuleFormat(path);
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return result;
    }
    public L3GranuleMetadataPSD13(String name) {
        super(name);
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
    public S2Metadata.ProductCharacteristics getTileProductOrganization(VirtualPath path, S2SpatialResolution resolution) {

        S2Metadata.ProductCharacteristics characteristics = new S2Metadata.ProductCharacteristics();
        characteristics.setPsd(S2Metadata.getPSD(path));
        //DatatakeSensingStart is not in the metadata, but is it needed for the image templates in level3??. We read it from the file system
        VirtualPath folder = path.resolveSibling("IMG_DATA");
        Pattern pattern = Pattern.compile(SAFECOMPACTNamingConvention.SPECTRAL_BAND_REGEX);
        characteristics.setDatatakeSensingStartTime("Unknown");
        boolean bFound = false;
        if(folder.existsAndHasChildren()) {
            VirtualPath[] resolutions = null;
            try {
                resolutions = folder.listPaths();
            } catch (IOException e) {
            }

            if(resolutions != null) {
                for (VirtualPath resolutionFolder : resolutions) {
                    if (resolutionFolder.existsAndHasChildren()) {
                        String[] images = null;
                        try {
                            images = resolutionFolder.list();
                        } catch (IOException e) {
                        }
                        if (images != null && images.length > 0) {
                            for (String image : images) {
                                Matcher matcher = pattern.matcher(image);
                                if (matcher.matches()) {
                                    characteristics.setDatatakeSensingStartTime(matcher.group(2));
                                    bFound = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (bFound) {
                        break;
                    }
                }
            }
        }
        characteristics.setSpacecraft("Sentinel-2");
        characteristics.setProcessingLevel("Level-3");
        characteristics.setMetaDataLevel("Standard");

        double boaQuantification = L3PSD13Constants.DEFAULT_BOA_QUANTIFICATION;
        characteristics.setQuantificationValue(boaQuantification);

        return characteristics;
    }

    @Override
    public Map<S2SpatialResolution, S2Metadata.TileGeometry> getTileGeometries() {
        Map<S2SpatialResolution, S2Metadata.TileGeometry> resolutions = new HashMap<>();
        String[] resolutionsValues = getAttributeValues(L3PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION);
        if(resolutionsValues == null) {
            return resolutions;
        }
        for (String res : resolutionsValues) {
            S2SpatialResolution resolution = S2SpatialResolution.valueOfResolution(Integer.parseInt(res));
            S2Metadata.TileGeometry tgeox = new S2Metadata.TileGeometry();

            tgeox.setUpperLeftX(Double.parseDouble(getAttributeSiblingValue(L3PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION, res,
                                                                            L3PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_ULX, "0")));
            tgeox.setUpperLeftY(Double.parseDouble(getAttributeSiblingValue(L3PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION, res,
                                                                            L3PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_ULY, "0")));
            tgeox.setxDim(Double.parseDouble(getAttributeSiblingValue(L3PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION, res,
                                                                      L3PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_XDIM, "0")));
            tgeox.setyDim(Double.parseDouble(getAttributeSiblingValue(L3PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_RESOLUTION, res,
                                                                      L3PSD13Constants.PATH_GRANULE_METADATA_GEOPOSITION_YDIM, "0")));
            tgeox.setNumCols(Integer.parseInt(getAttributeSiblingValue(L3PSD13Constants.PATH_GRANULE_METADATA_SIZE_RESOLUTION, res,
                                                                       L3PSD13Constants.PATH_GRANULE_METADATA_SIZE_NCOLS, "0")));
            tgeox.setNumRows(Integer.parseInt(getAttributeSiblingValue(L3PSD13Constants.PATH_GRANULE_METADATA_SIZE_RESOLUTION, res,
                                                                       L3PSD13Constants.PATH_GRANULE_METADATA_SIZE_NROWS, "0")));
            resolutions.put(resolution, tgeox);
        }

        return resolutions;
    }

    @Override
    public String getTileID() {
        return getAttributeValue(L3PSD13Constants.PATH_GRANULE_METADATA_TILE_ID, null);
    }

    @Override
    public String getHORIZONTAL_CS_CODE() {
        return getAttributeValue(L3PSD13Constants.PATH_GRANULE_METADATA_HORIZONTAL_CS_CODE, null);
    }

    @Override
    public String getHORIZONTAL_CS_NAME() {
        return getAttributeValue(L3PSD13Constants.PATH_GRANULE_METADATA_HORIZONTAL_CS_NAME, null);
    }

    @Override
    public int getAnglesResolution() {
        int anglesResolution = L3PSD13Constants.DEFAULT_ANGLES_RESOLUTION;
        try {
            anglesResolution = Integer.parseInt(getAttributeValue(L3PSD13Constants.PATH_GRANULE_METADATA_ANGLE_RESOLUTION, String.valueOf(L3PSD13Constants.DEFAULT_ANGLES_RESOLUTION)));
        } catch (Exception e) {
            //do nothing to return default value
        }
        return anglesResolution;
    }

    @Override
    public S2Metadata.AnglesGrid getSunGrid() {
        return S2Metadata.wrapAngles(getAttributeValues(L3PSD13Constants.PATH_GRANULE_METADATA_SUN_ZENITH_ANGLES),
                                     getAttributeValues(L3PSD13Constants.PATH_GRANULE_METADATA_SUN_AZIMUTH_ANGLES));
    }

    @Override
    public S2Metadata.AnglesGrid[] getViewingAnglesGrid() {
        MetadataElement geometricElement = rootElement.getElement("Geometric_Info");
        if(geometricElement == null) {
            return null;
        }
        MetadataElement tileAnglesElement = geometricElement.getElement("Tile_Angles");
        if(tileAnglesElement == null) {
            return null;
        }
        return S2Metadata.wrapStandardViewingAngles(tileAnglesElement);
    }

    @Override
    public MetadataElement getMetadataElement() {
        return rootElement;
    }

    @Override
    public MetadataElement getSimplifiedMetadataElement() {
        //TODO
        return rootElement;
    }

    @Override
    public int getMaximumMosaicIndex() {
        int maxIndex = 1;
        String[] attributeValuesPVI = getAttributeValues(L3PSD13Constants.PATH_GRANULE_METADATA_PVI_FILENAME);
        if(attributeValuesPVI != null) {
            for (String pviName : attributeValuesPVI) {
                try {
                    int aux = Integer.parseInt(pviName.substring(pviName.lastIndexOf("_") + 1));
                    if (aux > maxIndex) maxIndex = aux;
                } catch (Exception e) {
                    //do nothing
                }
            }
        }

        //check also the tileNumbers in /Level-3_Tile_ID/Quality_Indicators_Info/L3_Mosaic_QI/L3_Mosaic_Content
        String[] mosaicContentTileNumbers = getAttributeValues(L3PSD13Constants.PATH_GRANULE_METADATA_MOSAIC_CONTENT_TILE_NUMBER);
        if(mosaicContentTileNumbers != null) {
            for (String tileNumber : mosaicContentTileNumbers) {
                try {
                    int aux = Integer.parseInt(tileNumber);
                    if (aux > maxIndex) maxIndex = aux;
                } catch (Exception e) {
                    //do nothing
                }
            }
        }
        return maxIndex;
    }

    @Override
    public String getFormat() {
        return format;
    }

    private void updateName() {
        String tileId = getAttributeValue(L3PSD13Constants.PATH_GRANULE_METADATA_TILE_ID, null);
        if(tileId == null || tileId.length()<56) {
            setName("Level-03_Tile_ID");
            return;
        }
        setName("Level-03_Tile_" + tileId.substring(50, 55));
    }
}
