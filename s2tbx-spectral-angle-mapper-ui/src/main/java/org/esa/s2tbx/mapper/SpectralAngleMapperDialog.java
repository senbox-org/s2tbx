package org.esa.s2tbx.mapper;

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.swing.binding.BindingContext;
import org.esa.s2tbx.mapper.util.SpectrumInput;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.descriptor.OperatorDescriptor;
import org.esa.snap.core.gpf.ui.DefaultIOParametersPanel;
import org.esa.snap.core.gpf.ui.OperatorMenu;
import org.esa.snap.core.gpf.ui.OperatorParameterSupport;
import org.esa.snap.core.gpf.ui.SingleTargetProductDialog;
import org.esa.snap.core.gpf.ui.TargetProductSelector;
import org.esa.snap.core.util.StringUtils;
import org.esa.snap.ui.AppContext;

import java.util.BitSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * dialog
 *
 * @author Dumitrascu Razvan.
 */
public class SpectralAngleMapperDialog extends SingleTargetProductDialog {

    private final SpectralAngleMapperForm form;
    private final OperatorDescriptor operatorDescriptor;

    SpectralAngleMapperDialog(String title, String helpID, AppContext appContext) {
        super(appContext, title, helpID);

        final TargetProductSelector selector = getTargetProductSelector();
        selector.getModel().setSaveToFileSelected(false);
        selector.getModel().setProductName("Spectral Angle Mapper");
        selector.getSaveToFileCheckBox().setEnabled(true);

        final OperatorSpi operatorSpi = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi("SpectralAngleMapperOp");
        if (operatorSpi == null) {
            throw new IllegalArgumentException("No SPI found for operator name '" + "SpectralAngleMapperOp" + "'");
        }
        operatorDescriptor = operatorSpi.getOperatorDescriptor();

        form = new SpectralAngleMapperForm(operatorDescriptor, selector, appContext, getTargetProductSelector());
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
        if(spectra != null && spectra.length != 0){
            return true;
        }
        return false;
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
        if (referenceBands != null && referenceBands.length >= 2) {
            return true;
        }
        return false;
    }

    @Override
    public int show() {
        form.prepareShow();
        setContent(form);
        int result = super.show();
        return result;
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