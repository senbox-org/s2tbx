package org.esa.s2tbx.mapper;

import org.esa.snap.rcp.actions.AbstractSnapAction;
import org.esa.snap.ui.ModelessDialog;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Dumitrascu Razvan.
 */
public class SpectralAngleMapperAction extends AbstractSnapAction {
    private static final Set<String> KNOWN_KEYS = new HashSet<>(Arrays.asList("displayName", "operatorName", "dialogTitle", "helpId", "targetProductNameSuffix"));

    public SpectralAngleMapperAction() {
        super();
    }

    public static SpectralAngleMapperAction create(Map<String, Object> properties) {
        SpectralAngleMapperAction action = new SpectralAngleMapperAction();
        properties.entrySet().stream().filter(entry -> KNOWN_KEYS.contains(entry.getKey())).forEach(entry -> {
            action.putValue(entry.getKey(), entry.getValue());
        });
        return action;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ModelessDialog dialog = new SpectralAngleMapperDialog("SpectralAngleMapperAction",
                "SAMProcessor", getAppContext());
        dialog.show();
    }

}
