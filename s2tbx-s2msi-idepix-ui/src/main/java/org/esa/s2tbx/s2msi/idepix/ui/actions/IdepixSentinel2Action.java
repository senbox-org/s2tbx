/* 
 * Copyright (C) 2002-2008 by Brockmann Consult
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.esa.s2tbx.s2msi.idepix.ui.actions;

import org.esa.s2tbx.s2msi.idepix.algorithms.sentinel2.S2IdepixOp;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.ui.DefaultSingleTargetProductDialog;
import org.esa.snap.rcp.actions.AbstractSnapAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Idepix action for Sentinel-2 algorithm.
 *
 * @author Olaf Danne
 */
@ActionID(category = "Processing", id = "org.esa.s2tbx.s2msi.idepix.ui.actions.IdepixSentinel2Action")
@ActionRegistration(displayName = "#CTL_IdepixSentinel2Action_Text")
@ActionReference(path = "Menu/Optical/Preprocessing/IdePix Pixel Classification", position = 200)
@NbBundle.Messages({"CTL_IdepixSentinel2Action_Text=Sentinel-2"})
public class IdepixSentinel2Action extends AbstractSnapAction {

    private static final String HELP_ID = "idepixTool";

    public IdepixSentinel2Action() {
        putValue(Action.SHORT_DESCRIPTION, "Performs pixel classification on a Sentinel-2 product.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final OperatorMetadata opMetadata = S2IdepixOp.class.getAnnotation(OperatorMetadata.class);
//        final IdepixDefaultDialog dialog = new IdepixDefaultDialog(opMetadata.alias(),
//                                                                   getAppContext(),
//                                                                   "Idepix - Pixel Identification and Classification (MSI mode)",
//                                                                   HELP_ID,
//                                                                   "_idepix");

        final DefaultSingleTargetProductDialog dialog =
                new DefaultSingleTargetProductDialog(opMetadata.alias(),
                                                     getAppContext(),
                                                     "Idepix - Pixel Identification and Classification (Sentinel-2 mode)",
                                                     HELP_ID);

        dialog.getJDialog().pack();
        dialog.setTargetProductNameSuffix("_idepix");
        dialog.show();
    }
}

