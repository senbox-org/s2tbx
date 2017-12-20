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
    public static final String THRESHOLDS_PROPERTY = "thresholds";

    private SpectralAngleMapperForm parentForm;
    private final PropertySet container;
    private final Map<String, Object> parameterMap = new HashMap<>();
    private final Map<File, Product> sourceProductMap = Collections.synchronizedMap(new HashMap<>());
    private BindingContext bindingContext;
    SpectralAngleMapperFormModel(SpectralAngleMapperForm parentForm) {
        this.parentForm = parentForm;
        container = ParameterDescriptorFactory.createMapBackedOperatorPropertyContainer("SpectralAngleMapperOp", parameterMap);
    }

    PropertySet getPropertySet() {
        return container;
    }

    Map<String, Object> getParameterMap() {
        return parameterMap;
    }
}
