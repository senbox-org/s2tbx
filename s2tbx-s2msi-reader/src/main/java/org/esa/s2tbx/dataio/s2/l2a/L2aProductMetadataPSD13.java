package org.esa.s2tbx.dataio.s2.l2a;

import com.bc.ceres.core.Assert;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.s2.S2BandInformation;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoDatastripFilename;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleDirFilename;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.ProductData;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by obarrile on 04/10/2016.
 */

public class L2aProductMetadataPSD13 extends XmlMetadata implements IL2aProductMetadata {

    private static class L2aProductMetadataPSD13Parser extends XmlMetadataParser<L2aProductMetadataPSD13> {

        public L2aProductMetadataPSD13Parser(Class metadataFileClass) {
            super(metadataFileClass);
            setSchemaLocations(L2aMetadataPSD13Helper.getSchemaLocations());
        }

        //TODO validate schema
        /*@Override
        protected ProductData inferType(String elementName, String value) {
            return L1cMetadataPSD13Helper.createProductData(elementName, value);
        }*/

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }



    public static L2aProductMetadataPSD13 create(Path path) throws IOException {
        Assert.notNull(path);
        L2aProductMetadataPSD13 result = null;
        InputStream stream = null;
        try {
            if (Files.exists(path)) {
                stream = Files.newInputStream(path, StandardOpenOption.READ);
                //noinspection unchecked
                L2aProductMetadataPSD13Parser parser = new L2aProductMetadataPSD13Parser(L2aProductMetadataPSD13.class);
                result = parser.parse(stream);
                result.setName("Level-2A_User_Product");
                String metadataProfile = result.getMetadataProfile();
                //if (metadataProfile != null)
                //    result.setName(metadataProfile);
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


    public L2aProductMetadataPSD13(String name) {
        super(name);
    }


    @Override
    public int getNumBands() {
        String bandList[] = getBandList();
        if (bandList == null) {
            return 0;
        }
        return bandList.length;
    }

    @Override
    public String getProductName() {
        return null;
    }

    @Override
    public String getFormatName() {
        return getAttributeValue(L2aPSD13Constants.PATH_PRODUCT_METADATA_PRODUCT_FORMAT, null);
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
        return getAttributeValues(L2aPSD13Constants.PATH_PRODUCT_METADATA_IMAGE_ID);
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
    public L2aMetadata.ProductCharacteristics getProductOrganization(S2SpatialResolution resolution) {
        S2Metadata.ProductCharacteristics characteristics = new S2Metadata.ProductCharacteristics();
        characteristics.setSpacecraft(getAttributeValue(L2aPSD13Constants.PATH_PRODUCT_METADATA_SPACECRAFT, "Unknown"));
        characteristics.setDatasetProductionDate(getAttributeValue(L2aPSD13Constants.PATH_PRODUCT_METADATA_SENSING_START, "Unknown"));

        characteristics.setProductStartTime(getAttributeValue(L2aPSD13Constants.PATH_PRODUCT_METADATA_PRODUCT_START_TIME, "Unknown"));
        characteristics.setProductStopTime(getAttributeValue(L2aPSD13Constants.PATH_PRODUCT_METADATA_PRODUCT_STOP_TIME, "Unknown"));

        characteristics.setProcessingLevel(getAttributeValue(L2aPSD13Constants.PATH_PRODUCT_METADATA_PROCESSING_LEVEL, "Unknown"));
        characteristics.setMetaDataLevel(getAttributeValue(L2aPSD13Constants.PATH_PRODUCT_METADATA_METADATA_LEVEL, "Unknown"));

        double boaQuantification = Double.valueOf(getAttributeValue(L2aPSD13Constants.PATH_PRODUCT_METADATA_L2A_BOA_QUANTIFICATION_VALUE, "10000"));
        characteristics.setQuantificationValue(boaQuantification);

        double aotQuantification = Double.valueOf(getAttributeValue(L2aPSD13Constants.PATH_PRODUCT_METADATA_L2A_AOT_QUANTIFICATION_VALUE, "1000"));
        double wvpQuantification = Double.valueOf(getAttributeValue(L2aPSD13Constants.PATH_PRODUCT_METADATA_L2A_WVP_QUANTIFICATION_VALUE, "1000"));


        List<S2BandInformation> aInfo = L2aMetadataProc.getBandInformationList(resolution,boaQuantification,aotQuantification,wvpQuantification);
        int size = aInfo.size();
        characteristics.setBandInformations(aInfo.toArray(new S2BandInformation[size]));

        return characteristics;
    }

    @Override
    public Collection<String> getTiles() {
        String[] granuleList = getAttributeValues(L2aPSD13Constants.PATH_PRODUCT_METADATA_GRANULE_LIST);
        if(granuleList == null) {
            return null;
        }

        //New list with only granules with different granuleIdentifier
        List<String> granuleListReduced = new ArrayList<>();
        Map<String, String> mapGranules = new LinkedHashMap<>(granuleList.length);
        for (String granule : granuleList) {
            mapGranules.put(granule, granule);
        }
        for (Map.Entry<String, String> granule : mapGranules.entrySet()) {
            granuleListReduced.add(granule.getValue());
        }

        return granuleListReduced;
    }

    @Override
    public S2DatastripFilename getDatastrip() {
        String[] datastripList = getAttributeValues(L2aPSD13Constants.PATH_PRODUCT_METADATA_DATASTRIP_LIST);
        if(datastripList == null) {
            return null;
        }

        S2DatastripDirFilename dirDatastrip = S2DatastripDirFilename.create(datastripList[0], null);

        S2DatastripFilename datastripFilename = null;
        if (dirDatastrip != null) {
            String fileName = dirDatastrip.getFileName(null);

            if (fileName != null) {
                datastripFilename = S2OrthoDatastripFilename.create(fileName);
            }
        }

        return datastripFilename;
    }

    @Override
    public S2DatastripDirFilename getDatastripDir() {
        String[] granuleList = getAttributeValues(L2aPSD13Constants.PATH_PRODUCT_METADATA_GRANULE_LIST);
        String[] datastripList = getAttributeValues(L2aPSD13Constants.PATH_PRODUCT_METADATA_DATASTRIP_LIST);
        if(granuleList == null || datastripList == null) {
            return null;
        }
        S2OrthoGranuleDirFilename grafile = S2OrthoGranuleDirFilename.create(granuleList[0]);

        S2DatastripDirFilename datastripDirFilename = null;
        if (grafile != null) {
            String fileCategory = grafile.fileCategory;
            String dataStripMetadataFilenameCandidate = datastripList[0];
            datastripDirFilename = S2DatastripDirFilename.create(dataStripMetadataFilenameCandidate, fileCategory);

        }
        return datastripDirFilename;
    }

    @Override
    public MetadataElement getMetadataElement() {
        return rootElement;
    }

    private String[] getBandList() {
        return getAttributeValues(L2aPSD13Constants.PATH_PRODUCT_METADATA_BAND_LIST);
    }
}
