/*
 * Copyright (C) 2011 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.mosaic.ui;

import org.esa.snap.core.gpf.GPF;
import org.esa.snap.rcp.actions.AbstractSnapAction;
import org.esa.snap.ui.ModelessDialog;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

import java.awt.event.ActionEvent;

/**
 * Mosaicing action for Sentinel2 products.
 *
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
@ActionID(category = "Operators", id = "org.esa.s2tbx.dataio.mosaic.ui.S2tbxMosaicAction")
@ActionRegistration(displayName = "#CTL_S2MosaicAction_Name")
@ActionReference(path = "Menu/Raster/Geometric Operations", position = 351)
@NbBundle.Messages({"CTL_S2MosaicAction_Name=Sentinel-2-Mosaic"})
public class S2tbxMosaicAction extends AbstractSnapAction {

    public S2tbxMosaicAction(){
        putValue(SHORT_DESCRIPTION, "Generates Mosaic Image for Sentinel2 Products.");
    }
    private ModelessDialog dialog;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (dialog == null) {
            dialog = new S2tbxMosaicDialog(Bundle.CTL_S2MosaicAction_Name(),
                                      "Sentinel2MosaicAction", getAppContext());

        }
        dialog.show();
    }

    @Override
    public boolean isEnabled() {
        return GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi("S2tbx-Mosaic") != null;
    }
}
