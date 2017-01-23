package org.esa.s2tbx.dataio.jp2.metadata;

import org.esa.s2tbx.dataio.jp2.internal.JP2ImageWriter;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.PixelPos;
import org.geotools.referencing.CRS;
import org.w3c.dom.Node;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;


/**
 * Class for generating the metadata for the JPEG2000 files
 *
 *  @author  Razvan Dumitrascu
 *  @since 5.0.2
 */
public class JP2Metadata extends IIOMetadata{

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(JP2Metadata.class.getName());

    public JP2MetadataResources jp2resources  = new JP2MetadataResources();

    /**
     *
     * Constructs a default stream <code>JP2Metadata</code> object appropriate
     * for the given write parameters.
     */
    public JP2Metadata(ImageWriteParam param, JP2ImageWriter writer) {
        super(false,null,null,null,null);
    }

    /**
     *
     * @param geoCoding , geoCoding of the product
     * @param width , width of the product
     * @param height , height of the product
     */
    public void createJP2Metadata(final GeoCoding geoCoding, final int width, final int height){
        if(geoCoding==null){
            logger.warning("GeoCoding has not been received");
            throw new IllegalArgumentException("No GeoCoding has been received");
        }

        if (geoCoding instanceof CrsGeoCoding) {
            try {
                final Integer epsgCode = CRS.lookupEpsgCode(geoCoding.getMapCRS(), true);
                if (epsgCode != null) {
                    this.jp2resources.setEpsgNumber(epsgCode);
                }
            } catch (Exception e) {
                logger.severe("JP2 Metadata error " + e.getMessage());
            }
        }
        createFallbackJP2Metadata(geoCoding, width, height);
    }

    /**
     *
     *Function that computes the latitude and the longitude of the 4 corners of the product received when the geocoding is not an instance of CRSgeocoding
     */
    private void createFallbackJP2Metadata(GeoCoding geoCoding, int width, int height) {

        final PixelPos pixelPos = new PixelPos();

        final double latitude  = geoCoding.getGeoPos(new PixelPos(0, 0), null).getLat();
        final double longitude = geoCoding.getGeoPos(new PixelPos(0, 0), null).getLon();

        pixelPos.setLocation(width,0);
        final double latitude2  = geoCoding.getGeoPos(pixelPos, null).getLat();
        final double longitude2 = geoCoding.getGeoPos(pixelPos, null).getLon();

        pixelPos.setLocation(width, height);
        final double latitude3  = geoCoding.getGeoPos(pixelPos, null).getLat();
        final double longitude3 = geoCoding.getGeoPos(pixelPos, null).getLon();

        pixelPos.setLocation(0,height);
        final double latitude4  = geoCoding.getGeoPos(pixelPos, null).getLat();
        final double longitude4 = geoCoding.getGeoPos(pixelPos, null).getLon();

        this.jp2resources.setPoint2D(longitude + "", latitude + "",longitude2 + "", latitude2+ "", longitude3 + "",
                latitude3 + "",longitude4 + "", latitude4 + "" );
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public Node getAsTree(String formatName) {
        return null;
    }

    @Override
    public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {}

    @Override
    public void reset() {
        this.jp2resources = new JP2MetadataResources();
    }
}
