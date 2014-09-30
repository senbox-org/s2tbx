package org.esa.snap;

import com.bc.ceres.core.ProgressMonitor;
import com.jidesoft.plaf.LookAndFeelFactory;
import org.esa.beam.framework.ui.ModalDialog;
import org.esa.beam.framework.ui.application.ApplicationDescriptor;
import org.esa.beam.visat.VisatApp;

import javax.swing.UIManager;

public final class S2tbxApp extends VisatApp {

    public S2tbxApp(ApplicationDescriptor applicationDescriptor) {
        super(applicationDescriptor);
    }

    @Override
    protected void initClientUI(ProgressMonitor pm) {
        super.initClientUI(pm);
    }

    protected void loadJideExtension() {
        LookAndFeelFactory.installJideExtension(LookAndFeelFactory.EXTENSION_STYLE_ECLIPSE);
        UIManager.getDefaults().put("DockableFrameTitlePane.showIcon", Boolean.TRUE);
        UIManager.getDefaults().put("SidePane.alwaysShowTabText", Boolean.TRUE);
        UIManager.getDefaults().put("SidePane.orientation", 1);
    }

    @Override
    protected String getMainFrameTitle() {
        final String ver = System.getProperty("snap.version");
        return getAppName() + ' ' + ver;
    }

    @Override
    protected ModalDialog createAboutBox() {
        //todo about box
        return null;
    }

}
