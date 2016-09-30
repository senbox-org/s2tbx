package org.esa.s2tbx.dataio.s2.l1c;

import com.bc.ceres.core.Assert;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.s2.S2BandInformation;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripDirFilename;
import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.ProductData;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;

/**
 * Created by obarrile on 29/09/2016.
 */

public class L1cProductMetadataPSD13 extends XmlMetadata implements IL1cProductMetadata {

    MetadataElement metadataElement;

    private static class L1cProductMetadataPSD13Parser extends XmlMetadataParser<L1cProductMetadataPSD13> {

        public L1cProductMetadataPSD13Parser(Class metadataFileClass) {
            super(metadataFileClass);
            setSchemaLocations(L1cMetadataPSD13Helper.getSchemaLocations());
        }

        /*@Override
        protected ProductData inferType(String elementName, String value) {
            return L1cMetadataPSD13Helper.createProductData(elementName, value);
        }*/

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }



    public static L1cProductMetadataPSD13 create(Path path) throws IOException {
        Assert.notNull(path);
        L1cProductMetadataPSD13 result = /*new L1cProductMetadataPSD13("Level-1C_User_Product")*/null;
        InputStream stream = null;
        try {
            if (Files.exists(path)) {
                stream = Files.newInputStream(path, StandardOpenOption.READ);
                //noinspection unchecked
                L1cProductMetadataPSD13Parser parser = new L1cProductMetadataPSD13Parser(L1cProductMetadataPSD13.class);
                result = parser.parse(stream);
                result.setName("Level-1C_User_Product"); //TODO probar
                String metadataProfile = result.getMetadataProfile();
                if (metadataProfile != null)
                    result.setName(metadataProfile);
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


    public L1cProductMetadataPSD13(String name) {
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
    public S2Metadata.ProductCharacteristics getProductOrganization() {

        S2Metadata.ProductCharacteristics characteristics = new S2Metadata.ProductCharacteristics();

        characteristics.setSpacecraft(getAttributeValue("/General_Info/Product_Info/PROCESSING_LEVEL","gggg"));
        /*characteristics.setDatasetProductionDate(product.getGeneral_Info().getProduct_Info().getDatatake().getDATATAKE_SENSING_START().toString());

        characteristics.setProductStartTime(((Element) product.getGeneral_Info().getProduct_Info().getPRODUCT_START_TIME()).getFirstChild().getNodeValue());
        characteristics.setProductStopTime(((Element) product.getGeneral_Info().getProduct_Info().getPRODUCT_STOP_TIME()).getFirstChild().getNodeValue());

        characteristics.setProcessingLevel(product.getGeneral_Info().getProduct_Info().getPROCESSING_LEVEL().getValue().value());
        characteristics.setMetaDataLevel(product.getGeneral_Info().getProduct_Info().getQuery_Options().getMETADATA_LEVEL());

        double toaQuantification = product.getGeneral_Info().getProduct_Image_Characteristics().getQUANTIFICATION_VALUE().getValue();
        characteristics.setQuantificationValue(toaQuantification);

        List<S2BandInformation> aInfo = getBandInformationList (toaQuantification);
        int size = aInfo.size();
        characteristics.setBandInformations(aInfo.toArray(new S2BandInformation[size]));*/

        return characteristics;
    }

    @Override
    public Collection<String> getTiles() {
        return null;
    }

    @Override
    public S2DatastripFilename getDatastrip() {
        return null;
    }

    @Override
    public S2DatastripDirFilename getDatastripDir() {
        return null;
    }

    @Override
    public MetadataElement getMetadataElement() {
        return null;
    }
}
