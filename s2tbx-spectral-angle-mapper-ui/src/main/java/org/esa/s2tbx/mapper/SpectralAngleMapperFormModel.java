package org.esa.s2tbx.mapper;

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.binding.accessors.MapEntryAccessor;
import com.bc.ceres.swing.binding.BindingContext;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.annotations.ParameterDescriptorFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Dumitrascu Razvan.
 */
public class SpectralAngleMapperFormModel {

    public static final String REFERENCE_BANDS_PROPERTY = "referenceBands";
    public static final String SPECTRA_PROPERTY = "spectra";
    public static final String HIDDEN_SPECTRA_PROPERTY = "hiddenSpectra";
    public static final String THRESHOLDS_PROPERTY = "thresholds";
    public static final String RESAMPLE_TYPE_PROPERTY = "resampleType";
    public static final String UPSAMPLING_PROPERTY = "upsamplingMethod";
    public static final String DOWNSAMPLING_PROPERTY = "downsamplingMethod";

    private SpectralAngleMapperForm parentForm;
    private final PropertySet container;
    private final Map<String, Object> parameterMap;
    private final Map<File, Product> sourceProductMap = Collections.synchronizedMap(new HashMap<>());
    SpectralAngleMapperFormModel(SpectralAngleMapperForm parentForm) {
        this.parentForm = parentForm;
        parameterMap = new HashMap<>();
        container = ParameterDescriptorFactory.createMapBackedOperatorPropertyContainer("SpectralAngleMapperOp", parameterMap);
    }

    SpectralAngleMapperFormModel(PropertySet propertySet, Map<String, Object> paramMap){
        this.container = propertySet;
        this.parameterMap = paramMap;
    }

    PropertySet getPropertySet() {
        return container;
    }

    Map<String, Object> getParameterMap() {
        return parameterMap;
    }
}
