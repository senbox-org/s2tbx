package org.esa.s2tbx.tooladapter;

import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.GPF;
import org.esa.beam.framework.gpf.Operator;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.ui.DefaultSingleTargetProductDialog;
import org.esa.beam.framework.gpf.ui.OperatorParameterSupport;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.util.logging.BeamLogManager;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author Lucian Barbulescu.
 */
public class S2tbxToolAdapterDialog extends DefaultSingleTargetProductDialog {

    /** Operator identifier. */
    private String alias;
    /** Parameters related info. */
    private OperatorParameterSupport parameterSupport;

    /** Constructor.
     *
     * @param alias
     * @param appContext
     * @param title
     * @param helpID
     */
    protected S2tbxToolAdapterDialog(String alias, AppContext appContext, String title, String helpID) {
        super(alias, appContext, title, helpID);
        this.alias = alias;
        final OperatorSpi operatorSpi = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi(alias);

        this.parameterSupport = new OperatorParameterSupport(operatorSpi.getOperatorDescriptor());
    }


    /**
     * Creates the desired target product.
     * Usually, this method will be implemented by invoking one of the multiple {@link org.esa.beam.framework.gpf.GPF GPF}
     * {@code createProduct} methods.
     * <p/>
     * The method should throw a {@link org.esa.beam.framework.gpf.OperatorException} in order to signal "nominal" processing errors,
     * other exeption types are treated as internal errors.
     *
     * @return The target product.
     * @throws Exception if an error occurs, an {@link org.esa.beam.framework.gpf.OperatorException} is signaling "nominal" processing errors.
     */
    @Override
    protected Product createTargetProduct() throws Exception {
        Product sourceProduct = getAppContext().getSelectedProduct();

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("tool", "D:\\Workspaces\\ESA\\Toolbox\\workingDir\\copyTool.xml");
        Map<String, Product> sourceProducts = new HashMap<String, Product>();
        sourceProducts.put("sourceProduct", sourceProduct);


        Operator op = GPF.getDefaultInstance().createOperator(this.alias,parameters,sourceProducts,null);

        // set the output consumer
        ((S2tbxToolAdapterOp)op).setConsumer(new LogOutputConsumer());
//
        return op.getTargetProduct();
    }

    @Override
    protected boolean canApply() {
        return true;
    }

    /**
     * Add the output of the tool to the log.
     */
    private class LogOutputConsumer implements ProcessOutputCounsumer {

        /**
         * Consume a line of output obtained from a tool.
         *
         * @param line a line of output text.
         */
        @Override
        public void consumeOutpuLine(String line) {
            BeamLogManager.getSystemLogger().log(Level.INFO, line);
        }
    }
}
