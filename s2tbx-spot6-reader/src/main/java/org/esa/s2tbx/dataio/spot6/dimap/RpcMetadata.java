package org.esa.s2tbx.dataio.spot6.dimap;

import com.bc.ceres.core.Assert;
import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.RationalFunctionModel;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by kraftek on 12/10/2015.
 */
public class RpcMetadata extends GenericXmlMetadata {

    private double[] lineNumCoeffs;
    private double[] lineDenCoeffs;
    private double[] sampleNumCoeffs;
    private double[] sampleDenCoeffs;
    int numCoeffs = 20;

    private RationalFunctionModel rowFunction;
    private RationalFunctionModel colFunction;

    static class MetadataParser extends XmlMetadataParser<RpcMetadata> {

        public MetadataParser(Class metadataFileClass) {
            super(metadataFileClass);
        }

        @Override
        protected ProductData inferType(String elementName, String value) {
            return ProductData.createInstance(value);
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public static RpcMetadata create(Path path) throws IOException {
        Assert.notNull(path);
        RpcMetadata result = null;
        try (InputStream inputStream = Files.newInputStream(path)) {
            MetadataParser parser = new MetadataParser(RpcMetadata.class);
            result = parser.parse(inputStream);
            result.setFileName(path.getFileName().toString());
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return result;
    }

    public RpcMetadata(String name) {
        super(name);
    }

    @Override
    public String getFileName() {
        return this.name;
    }

    @Override
    public String getMetadataProfile() {
        return getAttributeValue(Spot6Constants.PATH_VOL_METADATA_PROFILE, Spot6Constants.DIMAP_DESCRIPTION);
    }

    public RationalFunctionModel getRowFunction() {
        if (rowFunction == null) {
            readCoefficients();
        }
        return rowFunction;
    }

    private void readCoefficients() {
        if (lineNumCoeffs == null) {
            lineNumCoeffs = new double[numCoeffs];
            lineDenCoeffs = new double[numCoeffs];
            sampleNumCoeffs = new double[numCoeffs];
            sampleDenCoeffs = new double[numCoeffs];
            String idx;
            for (int i = 0; i < numCoeffs; i++) {
                idx = String.valueOf(i);
                lineNumCoeffs[i] = Double.parseDouble(getAttributeValue(Spot6Constants.PATH_RPC_DM_LINE_NUM + idx, String.valueOf(Double.NaN)));
                lineDenCoeffs[i] = Double.parseDouble(getAttributeValue(Spot6Constants.PATH_RPC_DM_LINE_DEN + idx, String.valueOf(Double.NaN)));
                sampleNumCoeffs[i] = Double.parseDouble(getAttributeValue(Spot6Constants.PATH_RPC_DM_SAMP_NUM + idx, String.valueOf(Double.NaN)));
                sampleDenCoeffs[i] = Double.parseDouble(getAttributeValue(Spot6Constants.PATH_RPC_DM_SAMP_DEN + idx, String.valueOf(Double.NaN)));
            }
        }
    }
}
