package org.esa.s2tbx.mapper;

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
        return true;
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