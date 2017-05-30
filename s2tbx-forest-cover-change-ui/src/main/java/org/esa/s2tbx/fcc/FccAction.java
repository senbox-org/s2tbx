package org.esa.s2tbx.fcc;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.esa.snap.core.gpf.ui.DefaultOperatorAction;
import org.esa.snap.ui.ModelessDialog;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
public class FccAction  extends DefaultOperatorAction {
    private static final Set<String> KNOWN_KEYS = new HashSet<>(Arrays.asList("displayName", "operatorName", "dialogTitle", "helpId", "targetProductNameSuffix"));

    public FccAction() {
        super();
    }

    public static FccAction create(Map<String, Object> properties) {
        FccAction action = new FccAction();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if (KNOWN_KEYS.contains(entry.getKey())) {
                action.putValue(entry.getKey(), entry.getValue());
            }
        }
        return action;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ModelessDialog dialog = createOperatorDialog();
        dialog.show();
    }

    @Override
    protected ModelessDialog createOperatorDialog() {
        FccDialog productDialog = new FccDialog(getOperatorName(), getAppContext(), getDialogTitle(), getHelpId());
        if (getTargetProductNameSuffix() != null) {
            productDialog.setTargetProductNameSuffix(getTargetProductNameSuffix());
        }
        return productDialog;
    }
}
