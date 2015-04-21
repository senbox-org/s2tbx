package org.esa.snap.ui.tooladapter.interfaces;

import org.esa.snap.framework.datamodel.Product;
import org.esa.snap.framework.gpf.GPF;
import org.esa.snap.framework.gpf.Operator;
import org.esa.snap.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.snap.framework.gpf.operators.tooladapter.ToolAdapterOp;
import org.esa.snap.framework.gpf.ui.OperatorMenu;
import org.esa.snap.framework.gpf.ui.OperatorParameterSupport;
import org.esa.snap.framework.gpf.ui.SingleTargetProductDialog;
import org.esa.snap.framework.ui.AppContext;
import org.esa.snap.framework.ui.BasicApp;
import org.esa.snap.ui.tooladapter.ExternalToolExecutionForm;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.util.Cancellable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Lucian Barbulescu.
 */
public class ToolAdapterDialog extends SingleTargetProductDialog {

    public static final String SOURCE_PRODUCT_FIELD = "sourceProduct";
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

//    private ExecutorService executor;

    private Product result;

//    private DialogProgressMonitor dialogProgressMonitor;

//    private AsyncOperatorTask operatorTask;
    private OperatorTask operatorTask;

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
//        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onApply() {
        if (validateUserInput() && canApply()) {
            String productDir = targetProductSelector.getModel().getProductDir().getAbsolutePath();
            appContext.getPreferences().setPropertyString(BasicApp.PROPERTY_KEY_APP_LAST_SAVE_DIR, productDir);
            final Product sourceProduct = form.getSourceProduct();
            Map<String, Product> sourceProducts = new HashMap<>();
            sourceProducts.put(SOURCE_PRODUCT_FIELD, sourceProduct);
            Operator op = GPF.getDefaultInstance().createOperator(this.operatorDescriptor.getName(), parameterSupport.getParameterMap(), sourceProducts, null);
            operatorTask = new OperatorTask(op, ToolAdapterDialog.this::operatorCompleted);
            ProgressHandle progressHandle = ProgressHandleFactory.createHandle(this.getTitle());
            ((ToolAdapterOp)op).setProgressMonitor(progressHandle);
            ProgressUtils.runOffEventThreadWithProgressDialog(operatorTask, this.getTitle(), progressHandle, true, 1, 1);
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
     * Usually, this method will be implemented by invoking one of the multiple {@link org.esa.snap.framework.gpf.GPF GPF}
     * {@code createProduct} methods.
     * <p/>
     * The method should throw a {@link org.esa.snap.framework.gpf.OperatorException} in order to signal "nominal" processing errors,
     * other exeption types are treated as internal errors.
     *
     * @return The target product.
     * @throws Exception if an error occurs, an {@link org.esa.snap.framework.gpf.OperatorException} is signaling "nominal" processing errors.
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
        super.onCancel();
    }

    @Override
    protected void onClose() {
        super.onClose();
    }

    private void operatorCompleted(Product result) {
        this.result = result;
        super.onApply();
    }

    private void tearDown(Throwable throwable) {
        boolean hasBeenCancelled = operatorTask != null && !operatorTask.hasCompleted;
        if (operatorTask != null) {
            operatorTask.cancel();
        }
        if (throwable != null && !hasBeenCancelled) {
            handleInitialisationError(throwable);
        }
    }

    public class OperatorTask implements Runnable, Cancellable { //implements ProgressRunnable<Object>, Cancellable {

        private Operator operator;
        private Consumer<Product> callbackMethod;
        private Product result;
        private boolean hasCompleted;

        public OperatorTask(Operator op, Consumer<Product> callback) {
            operator = op;
            callbackMethod = callback;
        }

        @Override
        public boolean cancel() {
            if (!hasCompleted) {
                if (operator instanceof ToolAdapterOp) {
                    ((ToolAdapterOp) operator).stop();
                    onCancel();
                }
                hasCompleted = true;
            }
            return true;
        }

        @Override
//        public Object run(ProgressHandle progressHandle) {
        public void run() {
            try {

                callbackMethod.accept(operator.getTargetProduct());
            } catch (Throwable t) {
                tearDown(t);
            } finally {
                hasCompleted = true;
            }
            //return null;
        }
    }

}
