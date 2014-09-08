package org.esa.s2tbx;

import org.esa.beam.framework.ui.ModalDialog;
import org.esa.beam.framework.ui.application.ApplicationDescriptor;
import org.esa.beam.visat.VisatAboutBox;
import org.esa.beam.visat.VisatApp;

public final class S2tbxApp extends VisatApp {

    public S2tbxApp(ApplicationDescriptor applicationDescriptor) {
        super(applicationDescriptor);
    }

    @Override
    protected ModalDialog createAboutBox() {
        return new VisatAboutBox() {
        };
    }
}
