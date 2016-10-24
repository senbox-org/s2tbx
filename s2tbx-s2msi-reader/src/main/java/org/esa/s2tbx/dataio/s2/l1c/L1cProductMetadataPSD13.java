package org.esa.s2tbx.dataio.s2.l1c;

import com.bc.ceres.core.Assert;
import org.apache.commons.io.IOUtils;
import org.esa.s2tbx.dataio.metadata.GenericXmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.s2.S2BandInformation;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingItems;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoDatastripFilename;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleDirFilename;
import org.esa.snap.core.datamodel.MetadataElement;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by obarrile on 29/09/2016.
 */

public class L1cProductMetadataPSD13 extends GenericXmlMetadata implements IL1cProductMetadata {


    private static class L1cProductMetadataPSD13Parser extends XmlMetadataParser<L1cProductMetadataPSD13> {

        public L1cProductMetadataPSD13Parser(Class metadataFileClass) {
            super(metadataFileClass);
            setSchemaLocations(L1cPSD13Constants.getProductSchemaLocations());
            setSchemaBasePath(L1cPSD13Constants.getProductSchemaBasePath());
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public static L1cProductMetadataPSD13 create(Path path) throws IOException, ParserConfigurationException, SAXException {
        Assert.notNull(path);
        L1cProductMetadataPSD13 result = null;
        InputStream stream = null;
        try {
            if (Files.exists(path)) {
                stream = Files.newInputStream(path, StandardOpenOption.READ);
                L1cProductMetadataPSD13Parser parser = new L1cProductMetadataPSD13Parser(L1cProductMetadataPSD13.class);
                result = parser.parse(stream);
                result.setName("Level-1C_User_Product");
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return result;
    }


    public L1cProductMetadataPSD13(String name) {
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
    public S2Metadata.ProductCharacteristics getProductOrganization(INamingConvention namingConvention) {

        S2Metadata.ProductCharacteristics characteristics = new S2Metadata.ProductCharacteristics();

        characteristics.setSpacecraft(getAttributeValue(L1cPSD13Constants.PATH_PRODUCT_METADATA_SPACECRAFT, "Sentinel-2"));
        characteristics.setDatasetProductionDate(getAttributeValue(L1cPSD13Constants.PATH_PRODUCT_METADATA_SENSING_START, "Unknown"));

        characteristics.setProductStartTime(getAttributeValue(L1cPSD13Constants.PATH_PRODUCT_METADATA_PRODUCT_START_TIME, "Unknown"));
        characteristics.setProductStopTime(getAttributeValue(L1cPSD13Constants.PATH_PRODUCT_METADATA_PRODUCT_STOP_TIME, "Unknown"));

        characteristics.setProcessingLevel(getAttributeValue(L1cPSD13Constants.PATH_PRODUCT_METADATA_PROCESSING_LEVEL, "Level-1C"));
        characteristics.setMetaDataLevel(getAttributeValue(L1cPSD13Constants.PATH_PRODUCT_METADATA_METADATA_LEVEL, "Standard"));

        double toaQuantification = Double.valueOf(getAttributeValue(L1cPSD13Constants.PATH_PRODUCT_METADATA_QUANTIFICATION_VALUE, String.valueOf(L1cPSD13Constants.DEFAULT_TOA_QUANTIFICATION)));
        characteristics.setQuantificationValue(toaQuantification);

        List<S2BandInformation> aInfo = L1cMetadataProc.getBandInformationList (toaQuantification, namingConvention);
        int size = aInfo.size();
        characteristics.setBandInformations(aInfo.toArray(new S2BandInformation[size]));

        return characteristics;
    }

    @Override
    public Collection<String> getTiles() {
        String[] granuleList = getAttributeValues(L1cPSD13Constants.PATH_PRODUCT_METADATA_GRANULE_LIST);
        if(granuleList == null) {
            return null;
        }
        return new ArrayList<>(Arrays.asList(granuleList));
    }

    @Override
    public S2DatastripFilename getDatastrip() {
        String[] datastripList = getAttributeValues(L1cPSD13Constants.PATH_PRODUCT_METADATA_DATASTRIP_LIST);
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
        String[] granuleList = getAttributeValues(L1cPSD13Constants.PATH_PRODUCT_METADATA_GRANULE_LIST);
        String[] datastripList = getAttributeValues(L1cPSD13Constants.PATH_PRODUCT_METADATA_DATASTRIP_LIST);
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
        return getAttributeValues(L1cPSD13Constants.PATH_PRODUCT_METADATA_BAND_LIST);
    }

    public HashMap<S2NamingItems,String> getNamingItems() {
        //TODO review
        HashMap<S2NamingItems,String> namingItems = new HashMap<>();
        String missionID = getAttributeValue(L1cPSD13Constants.PATH_PRODUCT_METADATA_SPACECRAFT, null);
        if(missionID != null)
        {
            if(missionID.equals("Sentinel-2A")) {
                namingItems.put(S2NamingItems.MISSION_ID,"S2A");
            } else if (missionID.equals("Sentinel-2B")) {
                namingItems.put(S2NamingItems.MISSION_ID,"S2B");
            }
        }

        String datastripId = getAttributeValue(L1cPSD13Constants.PATH_PRODUCT_METADATA_DATASTRIP_LIST, null);
        if(datastripId != null && datastripId.length()>24);
        {
            namingItems.put(S2NamingItems.FILE_CLASS,datastripId.substring(4,8));
            namingItems.put(S2NamingItems.SITE_CENTRE,datastripId.substring(20,24));
            namingItems.put(S2NamingItems.CREATION_DATE,datastripId.substring(25,40));
        }

        namingItems.put(S2NamingItems.FILE_TYPE_PRODUCT,"PRD_MSIL1C");
        namingItems.put(S2NamingItems.SITE_CENTRE_PRODUCT,"PDMC");

        String productDiscriminator = getAttributeValue(L1cPSD13Constants.PATH_PRODUCT_METADATA_GENERATION_TIME, null);
        productDiscriminator = S2NamingItems.formatS2Time(productDiscriminator);
        if(productDiscriminator != null);
        {
            namingItems.put(S2NamingItems.PRODUCT_DISCRIMINATOR,productDiscriminator);
        }


        String relativeOrbit = getAttributeValue(L1cPSD13Constants.PATH_PRODUCT_METADATA_SENSING_ORBIT_NUMBER, null);
        if(relativeOrbit != null) {
            while(relativeOrbit.length()<3) {
                relativeOrbit = "0" + relativeOrbit;
            }
            namingItems.put(S2NamingItems.RELATIVE_ORBIT,relativeOrbit);
        }

        String startTime = getAttributeValue(L1cPSD13Constants.PATH_PRODUCT_METADATA_PRODUCT_START_TIME, null);
        startTime = S2NamingItems.formatS2Time(startTime);
        if(startTime != null);
        {
            namingItems.put(S2NamingItems.START_TIME,startTime);
        }

        String stopTime = getAttributeValue(L1cPSD13Constants.PATH_PRODUCT_METADATA_PRODUCT_STOP_TIME, null);
        stopTime = S2NamingItems.formatS2Time(stopTime);
        if(stopTime != null);
        {
            namingItems.put(S2NamingItems.STOP_TIME,stopTime);
        }

        namingItems.put(S2NamingItems.FILE_TYPE_PRODUCT_XML,"MTD_SAFL1C"); //By default always SAFE

        String format = getAttributeValue(L1cPSD13Constants.PATH_PRODUCT_METADATA_PRODUCT_FORMAT, null);
        if(format != null);
        {
            namingItems.put(S2NamingItems.FORMAT,format);
            if(format.equals("DIMAP")) {
                namingItems.put(S2NamingItems.FILE_TYPE_PRODUCT_XML,"MTD_DMPL1C");
            }
        }

        namingItems.put(S2NamingItems.FILE_TYPE_DATASTRIP,"MSI_L1C_DS");

        namingItems.put(S2NamingItems.FILE_TYPE_DATASTRIP_XML,"MTD_L1C_DS");
        namingItems.put(S2NamingItems.FILE_TYPE_GRANULE,"MSI_L1C_TL");
        namingItems.put(S2NamingItems.FILE_TYPE_GRANULE_XML,"MTD_L1C_TL");

        String baseline = getAttributeValue(L1cPSD13Constants.PATH_PRODUCT_METADATA_PROCESSING_BASELINE, null);
        if(baseline != null);
        {
            namingItems.put(S2NamingItems.PRODUCTION_BASELINE,baseline);
        }


        String absoluteOrbit = getAttributeValue(L1cPSD13Constants.PATH_PRODUCT_METADATA_GRANULE_LIST, null);
        if(absoluteOrbit != null && absoluteOrbit.length()>48);
        {
            namingItems.put(S2NamingItems.ABSOLUTE_ORBIT,absoluteOrbit.substring(42,48));
        }

        return  namingItems;
    }
}
