package org.esa.s2tbx.coregistration.operators;

import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
import java.awt.*;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

/**
 * @author ramonag
 */
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageLayout;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.OpImage;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;

public class ContrastDescriptor extends OperationDescriptorImpl
        implements RenderedImageFactory {

    private static final String[][] resources = {
            {"GlobalName", "Contrast"},
            {"LocalName", "Contrast"},
            {"Vendor", "csro"},
            {"Description", "A sample operation that changes source  pixels"},
            {"DocURL", "http://www.mycompany.com/SampleDescriptor.html"},
            {"Version", "1.0"},
            {"arg0Desc", "param1"},
            {"arg1Desc", "param2"}
    };
    private static final String[] paramNames = {
            "param1", "param2"
    };
    // The class types for the parameters of the "Sample" operation.
    // User defined classes can be used here as long as the fully
    // qualified name is used and the classes can be loaded.
    private static final Class[] paramClasses = {
            java.lang.Integer.class, java.lang.Integer.class
    };
    // The default parameter values for the "Sample" operation
    // when using a ParameterBlockJAI.
    private static final Object[] paramDefaults = {
            new Integer(0), new Integer(255)
    };

    // Constructor.
    public ContrastDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    // Creates a SampleOpImage with the given ParameterBlock if the
    // SampleOpImage can handle the particular ParameterBlock.
    public RenderedImage create(ParameterBlock paramBlock,
                                RenderingHints renderHints) {
        if (!validateParameters(paramBlock)) {
            return null;
        }
        return new ContrastOperator(paramBlock.getRenderedSource(0),
                new ImageLayout(),
                (Integer) paramBlock.getObjectParameter(0),
                (Integer) paramBlock.getObjectParameter(1));
    }

    public boolean validateParameters(ParameterBlock paramBlock) {
        for (int i = 0; i < this.getNumParameters(); i++) {
            Object arg = paramBlock.getObjectParameter(i);
            if (arg == null) {
                return false;
            }
            if (!(arg instanceof Integer)) {
                return false;
            }
        }
        return true;
    }


}

