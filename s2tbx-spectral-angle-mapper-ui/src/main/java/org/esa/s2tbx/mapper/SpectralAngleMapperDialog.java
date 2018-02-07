package org.esa.s2tbx.mapper;


import org.esa.s2tbx.mapper.common.SpectrumInput;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.descriptor.OperatorDescriptor;
import org.esa.snap.core.gpf.ui.OperatorMenu;
import org.esa.snap.core.gpf.ui.OperatorParameterSupport;
import org.esa.snap.core.gpf.ui.SingleTargetProductDialog;
import org.esa.snap.core.gpf.ui.TargetProductSelector;
import org.esa.snap.ui.AppContext;
import java.util.Map;

/**
 * dialog
 *
 * @author Dumitrascu Razvan.
 */
public class SpectralAngleMapperDialog extends SingleTargetProductDialog {

    private final SpectralAngleMapperForm form;

    SpectralAngleMapperDialog(String title, String helpID, AppContext appContext) {
        super(appContext, title, helpID);

        final TargetProductSelector selector = getTargetProductSelector();
        selector.getModel().setSaveToFileSelected(true);
        selector.getModel().setProductName("Spectral Angle Mapper");
        selector.getSaveToFileCheckBox().setEnabled(false);

        final OperatorSpi operatorSpi = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi("SpectralAngleMapperOp");
        if (operatorSpi == null) {
            throw new IllegalArgumentException("No SPI found for operator name '" + "SpectralAngleMapperOp" + "'");
        }
        OperatorDescriptor operatorDescriptor = operatorSpi.getOperatorDescriptor();

        form = new SpectralAngleMapperForm(operatorDescriptor, appContext, getTargetProductSelector());
        SpectralAngleMapperFormModel formModel = form.getFormModel();
        OperatorParameterSupport parameterSupport = new OperatorParameterSupport(operatorSpi.getOperatorDescriptor(),
                formModel.getPropertySet(),
                formModel.getParameterMap(),
                null);
        OperatorMenu operatorMenu = new OperatorMenu(this.getJDialog(),
                operatorSpi.getOperatorDescriptor(),
                parameterSupport,
                appContext,
                helpID);
        getJDialog().setJMenuBar(operatorMenu.createDefaultMenu());
    }

    @Override
    protected boolean verifyUserInput() {
        final SpectralAngleMapperFormModel formModel = form.getFormModel();
        if (!validateNumberOfBands(formModel)) {
            showErrorDialog("classification has to be done on at least 2 source product bans");
            return false;
        }
        if (!validateSpectrumClassInput(formModel)) {
            showErrorDialog("No Spectrum Class set");
            return false;
        }
        if (!validateThresholds(formModel)) {
            showErrorDialog("No thresholds set");
            return false;
        }
        return true;
    }

    private boolean validateSpectrumClassInput(SpectralAngleMapperFormModel formModel) {
        SpectrumInput[] spectra =  formModel.getPropertySet().getProperty(SpectralAngleMapperFormModel.SPECTRA_PROPERTY).getValue();
        SpectrumInput[] hiddenSpectra =  formModel.getPropertySet().getProperty(SpectralAngleMapperFormModel.HIDDEN_SPECTRA_PROPERTY).getValue();
        if(spectra.length != hiddenSpectra.length) {
            StringBuffer message  = new StringBuffer("Selection different than selected classes to be used. Spectrum classes to be used ");
            for (int spectrumIndex = 0; spectrumIndex < hiddenSpectra.length-1; spectrumIndex++) {
                message.append(hiddenSpectra[spectrumIndex].getName());
                message.append(", ");
            }
            message.append((hiddenSpectra[hiddenSpectra.length-1].getName()));
            showInformationDialog(message.toString());
        }
        return hiddenSpectra != null && hiddenSpectra.length != 0;

    }

    private boolean validateThresholds(SpectralAngleMapperFormModel formModel) {
        String thresholds = formModel.getPropertySet().getProperty(SpectralAngleMapperFormModel.THRESHOLDS_PROPERTY).getValue();
        if(thresholds != null) {
            if(!thresholds.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean validateNumberOfBands(SpectralAngleMapperFormModel formModel) {
        String[] referenceBands = formModel.getPropertySet().getProperty(SpectralAngleMapperFormModel.REFERENCE_BANDS_PROPERTY).getValue();
        return referenceBands != null && referenceBands.length >= 2;
    }

    @Override
    public int show() {
        form.prepareShow();
        setContent(form);
        return super.show();
    }

    @Override
    public void hide() {
        form.prepareHide();
        super.hide();
    }
    @Override
    protected Product createTargetProduct() throws Exception {
        final SpectralAngleMapperFormModel formModel = form.getFormModel();
        final Map<String, Object> parameterMap = formModel.getParameterMap();
        final Map<String, Product>sourceProducts = form.getSourceProductMap();
        return GPF.createProduct("SpectralAngleMapperOp", parameterMap, sourceProducts);
    }
}