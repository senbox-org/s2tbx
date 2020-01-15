package org.esa.s2tbx.dataio.s2.l3.metadata;

import com.bc.ceres.core.Assert;
import org.apache.commons.io.IOUtils;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.s2tbx.dataio.s2.l3.L3PSD13Constants;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoDatastripFilename;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleDirFilename;
import org.esa.snap.core.datamodel.MetadataElement;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by obarrile on 07/10/2016.
 */
public class L3ProductMetadataPSD13 extends GenericXmlMetadata implements IL3ProductMetadata {

    private VirtualPath virtualPath= null;

    private static class L3ProductMetadataPSD13Parser extends XmlMetadataParser<L3ProductMetadataPSD13> {

        public L3ProductMetadataPSD13Parser(Class metadataFileClass) {
            super(metadataFileClass);
            setSchemaLocations(L3PSD13Constants.getProductSchemaLocations());
            setSchemaBasePath(L3PSD13Constants.getProductSchemaBasePath());
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }



    public static L3ProductMetadataPSD13 create(VirtualPath path) throws IOException, ParserConfigurationException, SAXException {
        Assert.notNull(path);
        L3ProductMetadataPSD13 result = null;
        InputStream stream = null;
        try {
            if (path.exists()) {
                stream = path.getInputStream();
                L3ProductMetadataPSD13Parser parser = new L3ProductMetadataPSD13Parser(L3ProductMetadataPSD13.class);
                result = parser.parse(stream);
                result.setName("Level-3_User_Product");
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }

        result.virtualPath = path;
        return result;
    }
    public L3ProductMetadataPSD13(String name) {
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
    public S2Metadata.ProductCharacteristics getProductOrganization(VirtualPath path, S2SpatialResolution resolution) {
        L3Metadata.ProductCharacteristics characteristics = new L3Metadata.ProductCharacteristics();
        characteristics.setPsd(S2Metadata.getPSD(path));
        //is this right for level 3? Is datatake start time used?
        String datatakeSensingStart = getAttributeValue(L3PSD13Constants.PATH_PRODUCT_METADATA_SENSING_START, null);
        if(datatakeSensingStart!=null && datatakeSensingStart.length()>19) {
            String formattedDatatakeSensingStart = datatakeSensingStart.substring(0,4) +
                    datatakeSensingStart.substring(5,7) +
                    datatakeSensingStart.substring(8,13) +
                    datatakeSensingStart.substring(14,16)+
                    datatakeSensingStart.substring(17,19);
            characteristics.setDatatakeSensingStartTime(formattedDatatakeSensingStart);
        } else {
            characteristics.setDatatakeSensingStartTime("Unknown");
        }

        characteristics.setSpacecraft(getAttributeValue(L3PSD13Constants.PATH_PRODUCT_METADATA_SPACECRAFT, "Sentinel-2"));
        characteristics.setDatasetProductionDate(getAttributeValue(L3PSD13Constants.PATH_PRODUCT_METADATA_SENSING_START, "Unknown"));
        characteristics.setProcessingLevel(getAttributeValue(L3PSD13Constants.PATH_PRODUCT_METADATA_PROCESSING_LEVEL, "Level-3"));

        characteristics.setProductStartTime(getAttributeValue(L3PSD13Constants.PATH_PRODUCT_METADATA_PRODUCT_START_TIME, "Unknown"));
        characteristics.setProductStopTime(getAttributeValue(L3PSD13Constants.PATH_PRODUCT_METADATA_PRODUCT_STOP_TIME, "Unknown"));
        double boaQuantification = Double.valueOf(getAttributeValue(L3PSD13Constants.PATH_PRODUCT_METADATA_L2A_BOA_QUANTIFICATION_VALUE, String.valueOf(L3PSD13Constants.DEFAULT_BOA_QUANTIFICATION)));
        if(boaQuantification == 0d) {
            logger.warning("Invalid BOA quantification value, the default value will be used.");
            boaQuantification = L3PSD13Constants.DEFAULT_BOA_QUANTIFICATION;
        }
        characteristics.setQuantificationValue(boaQuantification);

        return characteristics;
    }

    @Override
    public Collection<String> getTiles() {

        String[] granuleList = getAttributeValues(L3PSD13Constants.PATH_PRODUCT_METADATA_GRANULE_LIST);
        if(granuleList == null) {
            granuleList = getAttributeValues(L3PSD13Constants.PATH_PRODUCT_METADATA_GRANULE_LIST_ALT);
            if(granuleList == null) {
                //return an empty arraylist
                ArrayList<String> tiles = new ArrayList<>();
                return tiles;
            }
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
        String[] datastripList = getAttributeValues(L3PSD13Constants.PATH_PRODUCT_METADATA_DATASTRIP_LIST);
        if(datastripList == null) {
            datastripList = getAttributeValues(L3PSD13Constants.PATH_PRODUCT_METADATA_DATASTRIP_LIST_ALT);
            if(datastripList == null) {
                return null;
            }
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
        String[] granuleList = getAttributeValues(L3PSD13Constants.PATH_PRODUCT_METADATA_GRANULE_LIST);
        String[] datastripList = getAttributeValues(L3PSD13Constants.PATH_PRODUCT_METADATA_DATASTRIP_LIST);

        if(datastripList == null) {
            datastripList = getAttributeValues(L3PSD13Constants.PATH_PRODUCT_METADATA_DATASTRIP_LIST_ALT);
            if(datastripList == null) {
                return null;
            }
        }
        if(granuleList == null) {
            granuleList = getAttributeValues(L3PSD13Constants.PATH_PRODUCT_METADATA_GRANULE_LIST_ALT);
            if(granuleList == null) {
                return null;
            }
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

    @Override
    public String getFormat() {
        String formatName = getAttributeValue(L3PSD13Constants.PATH_PRODUCT_METADATA_PRODUCT_FORMAT, "SAFE");
        if(formatName.equals("SAFE") && this.virtualPath.getFileName().toString().equals("MTD_MSIL03.xml")) {
            formatName = "SAFE_COMPACT";
        }
        return formatName;
    }
}
