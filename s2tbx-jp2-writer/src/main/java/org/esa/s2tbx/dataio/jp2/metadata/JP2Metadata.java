package org.esa.s2tbx.dataio.jp2.metadata;

import org.esa.s2tbx.dataio.jp2.internal.GmlFeatureCollection;
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
public class JP2Metadata extends IIOMetadata {
    private GmlFeatureCollection gml;

    /**
     *
     * Constructs a default stream <code>JP2Metadata</code> object appropriate
     * for the given write parameters.
     */
    public JP2Metadata(ImageWriteParam param, GmlFeatureCollection featureCollection) {
        super(false, null, null, null, null);
        this.gml = featureCollection;
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
        this.gml = null;
    }

    @Override
    public String toString() {
        return this.gml != null ? this.gml.toString() : super.toString();
    }
}
