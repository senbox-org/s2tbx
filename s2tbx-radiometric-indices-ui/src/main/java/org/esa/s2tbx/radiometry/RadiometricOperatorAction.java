package org.esa.s2tbx.radiometry;

import org.esa.snap.core.gpf.ui.DefaultOperatorAction;
import org.esa.snap.ui.ModelessDialog;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Jean Coravu.
 */
public class RadiometricOperatorAction extends DefaultOperatorAction {
    private static final Set<String> KNOWN_KEYS = new HashSet<>(Arrays.asList("displayName", "operatorName", "dialogTitle", "helpId", "targetProductNameSuffix"));

    public RadiometricOperatorAction() {
        super();
    }

    public static RadiometricOperatorAction create(Map<String, Object> properties) {
        RadiometricOperatorAction action = new RadiometricOperatorAction();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if (KNOWN_KEYS.contains(entry.getKey())) {
                action.putValue(entry.getKey(), entry.getValue());
            }
        }
        return action;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ModelessDialog dialog = createOperatorDialog();
        dialog.show();
    }

    @Override
    protected ModelessDialog createOperatorDialog() {
        RadiometricIndicesDialog productDialog = new RadiometricIndicesDialog(getOperatorName(), getAppContext(), getDialogTitle(), getHelpId());
        if (getTargetProductNameSuffix() != null) {
            productDialog.setTargetProductNameSuffix(getTargetProductNameSuffix());
        }
        return productDialog;
    }}
