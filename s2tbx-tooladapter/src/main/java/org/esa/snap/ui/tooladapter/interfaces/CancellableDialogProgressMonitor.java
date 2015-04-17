package org.esa.snap.ui.tooladapter.interfaces;

import com.bc.ceres.swing.progress.DialogProgressMonitor;
import com.bc.ceres.swing.progress.ProgressDialog;
import org.esa.snap.utils.PrivilegedAccessor;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * Created by kraftek on 3/12/2015.
 */
public class CancellableDialogProgressMonitor extends DialogProgressMonitor {

    private Consumer<Void> cancelDelegate;
    private ProgressDialog progressDialog;

    public CancellableDialogProgressMonitor(Component parentComponent, String title, Dialog.ModalityType modalityType, Consumer<Void> cancelDelegate) {
        super(parentComponent, title, modalityType);
        this.cancelDelegate = cancelDelegate;
        try {
            Field field = PrivilegedAccessor.getField(DialogProgressMonitor.class, "progressDialog");
            field.setAccessible(true);
            this.progressDialog = (ProgressDialog) field.get(this);
            this.progressDialog.setCancelable(false);
        } catch (Exception e) {
        }
    }

    public CancellableDialogProgressMonitor(ProgressDialog progressDialog, Consumer<Void> cancelDelegate) {
        super(progressDialog);
        this.cancelDelegate = cancelDelegate;
    }

    @Override
    public void setCanceled(boolean canceled) {
        if (this.cancelDelegate != null) {
            this.cancelDelegate.accept(null);
        }
        super.setCanceled(canceled);
    }
}
