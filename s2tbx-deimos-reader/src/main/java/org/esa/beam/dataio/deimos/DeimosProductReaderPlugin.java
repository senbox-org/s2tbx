package org.esa.beam.dataio.deimos;

import org.esa.beam.framework.dataio.DecodeQualification;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.util.io.BeamFileFilter;

import java.util.Locale;

/**
 * Created by kraftek on 9/22/2014.
 */
public class DeimosProductReaderPlugin implements ProductReaderPlugIn {
    @Override
    public DecodeQualification getDecodeQualification(Object o) {
        return null;
    }

    @Override
    public Class[] getInputTypes() {
        return new Class[0];
    }

    @Override
    public ProductReader createReaderInstance() {
        return new DeimosProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return new String[0];
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return new String[0];
    }

    @Override
    public String getDescription(Locale locale) {
        return null;
    }

    @Override
    public BeamFileFilter getProductFileFilter() {
        return null;
    }
}
