package org.esa.s2tbx;

import com.jidesoft.utils.Lm;
import org.esa.beam.framework.ui.application.ApplicationDescriptor;
import org.esa.beam.visat.VisatApp;
import org.esa.beam.visat.VisatMain;

public class S2tbxMain extends VisatMain {

    @Override
    protected void verifyJideLicense() {
        Lm.verifyLicense("Brockmann Consult", "BEAM", "lCzfhklpZ9ryjomwWxfdupxIcuIoCxg2");
    }

    @Override
    protected VisatApp createApplication(ApplicationDescriptor applicationDescriptor) {
        return new S2tbxApp(applicationDescriptor);
    }


}