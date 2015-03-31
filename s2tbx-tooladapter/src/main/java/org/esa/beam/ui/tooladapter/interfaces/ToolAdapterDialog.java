package org.esa.beam.ui.tooladapter.interfaces;

import com.bc.ceres.swing.progress.DialogProgressMonitor;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.GPF;
import org.esa.beam.framework.gpf.Operator;
import org.esa.beam.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.beam.framework.gpf.operators.tooladapter.ToolAdapterOp;
import org.esa.beam.framework.gpf.ui.OperatorMenu;
import org.esa.beam.framework.gpf.ui.OperatorParameterSupport;
import org.esa.beam.framework.gpf.ui.SingleTargetProductDialog;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.framework.ui.BasicApp;
import org.esa.beam.ui.tooladapter.ExternalToolExecutionForm;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * @author Lucian Barbulescu.
 */
public class ToolAdapterDialog extends SingleTargetProductDialog {

    /**
     * Operator identifier.
     */
    private ToolAdapterOperatorDescriptor operatorDescriptor;
    /**
     * Parameters related info.
     */
    private OperatorParameterSupport parameterSupport;
    /**
     * The form used to get the user's input
     */
    private ExternalToolExecutionForm form;

    private ExecutorService executor;

    private Product result;

    private DialogProgressMonitor dialogProgressMonitor;

    private AsyncOperatorTask operatorTask;

    /**
     * Constructor.
     *
     * @param operatorSpi
     * @param appContext
     * @param title
     * @param helpID
     */
    public ToolAdapterDialog(ToolAdapterOperatorDescriptor operatorSpi, AppContext appContext, String title, String helpID) {
        super(appContext, title, helpID);
        this.operatorDescriptor = operatorSpi;

        this.parameterSupport = new OperatorParameterSupport(operatorSpi);

        form = new ExternalToolExecutionForm(appContext, operatorSpi, parameterSupport.getPropertySet(),
                getTargetProductSelector());
        OperatorMenu operatorMenu = new OperatorMenu(this.getJDialog(),
                operatorSpi,
                parameterSupport,
                appContext,
                helpID);
        getJDialog().setJMenuBar(operatorMenu.createDefaultMenu());
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onApply() {
        if (validateUserInput() && canApply()) {
            String productDir = targetProductSelector.getModel().getProductDir().getAbsolutePath();
            appContext.getPreferences().setPropertyString(BasicApp.PROPERTY_KEY_APP_LAST_SAVE_DIR, productDir);
            final Product sourceProduct = form.getSourceProduct();
            Map<String, Product> sourceProducts = new HashMap<>();
            sourceProducts.put("sourceProduct", sourceProduct);
            Operator op = GPF.getDefaultInstance().createOperator(this.operatorDescriptor.getName(), parameterSupport.getParameterMap(), sourceProducts, null);
            // set the output consumer
            //((ToolAdapterOp) op).setConsumer(new DefaultOutputConsumer());
            operatorTask = new AsyncOperatorTask(op, ToolAdapterDialog.this::operatorCompleted);
            dialogProgressMonitor = new CancellableDialogProgressMonitor(getJDialog(), this.getTitle(), Dialog.ModalityType.MODELESS,
                    aVoid -> operatorTask.cancel());
            ((ToolAdapterOp) op).setProgressMonitor(dialogProgressMonitor);
            executor.submit(operatorTask);
        }
    }

    private boolean validateUserInput() {
        // future validation of input parameters goes here
        return true;
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
        return result;
    }

    @Override
    protected boolean canApply() {
        return true;
    }

    @Override
    protected void onCancel() {
        tearDown(null);
        executor.shutdown();
        super.onCancel();
    }

    @Override
    protected void onClose() {
        tearDown(null);
        executor.shutdown();
        super.onClose();
    }

    private void operatorCompleted(Product result) {
        this.result = result;
        tearDown(null);
        super.onApply();
    }

    private void tearDown(Throwable throwable) {
        if (operatorTask != null) {
            operatorTask.cancel();
        }
        if (dialogProgressMonitor != null) {
            dialogProgressMonitor.done();
        }
        if (throwable != null) {
            handleInitialisationError(throwable);
        }
    }

    public class AsyncOperatorTask implements Callable<Void> {

        private Operator operator;
        private Consumer<Product> callbackMethod;
        private Product result;
        private boolean hasCompleted;

        public AsyncOperatorTask(Operator op, Consumer<Product> callback) {
            operator = op;
            callbackMethod = callback;
        }

        @Override
        public Void call() throws Exception {
            try {
                callbackMethod.accept(operator.getTargetProduct());
            } catch (Throwable t) {
                tearDown(t);
            } finally {
                hasCompleted = true;
            }
            return null;
        }

        public void cancel() {
            if (!hasCompleted) {
                if (operator instanceof ToolAdapterOp) {
                    ((ToolAdapterOp) operator).stop();
                }
            }
        }
    }
}
