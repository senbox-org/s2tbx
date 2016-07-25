package org.esa.s2tbx.colourmanipulation;

/**
 * Created by dmihailescu on 13/07/2016.
 */


import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

@TopComponent.Description(
        preferredID = "BrightnessContrastTopComponent",
        iconBase = "org/esa/s2tbx/icons/BrightnessContrast.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(
        mode = "rightSlidingSide",
        openAtStartup = true,
        position = 30
)
@ActionID(category = "Window", id = "org.esa.s2tbx.colourmanipulation.BrightnessContrastToolTopComponent")
@ActionReferences({
        @ActionReference(path = "Menu/View/Tool Windows"),
        @ActionReference(path = "Toolbars/Tool Windows")
})
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_BrightnessContrastTopComponent_Name",
        preferredID = "BrightnessContrastTopComponent"
)
@NbBundle.Messages({
        "CTL_BrightnessContrastTopComponent_Name=Brightness and Contrast",
        "CTL_BrightnessContrastTopComponent_HelpId=showBrightnessContrastWnd"
})


public class BrightnessContrastToolTopComponent extends AbstractBrightnessContrastTopComponent {

    public static final String ID = BrightnessContrastToolTopComponent.class.getName();

    public BrightnessContrastToolTopComponent() {
        initUI();
    }

    @Override
    protected String getTitle() {
        return Bundle.CTL_BrightnessContrastTopComponent_Name();
    }

    @Override
    protected String getHelpId() {
        return Bundle.CTL_BrightnessContrastTopComponent_HelpId();
    }

}
