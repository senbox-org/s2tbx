/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
@OptionsPanelController.ContainerRegistration(
        id = "S2TBX",
        categoryName = "#LBL_S2TBXOptionsCategory_Name",
        iconBase = "org/esa/s2tbx/dataio/s2/preferences/s2tbx-icon-32.jpg",
        keywords = "#LBL_S2TBXOptionsCategory_Keywords",
        keywordsCategory = "S2TBX",
        position = 1100
)
@NbBundle.Messages(value = {
    "LBL_S2TBXOptionsCategory_Name=S2TBX",
    "LBL_S2TBXOptionsCategory_Keywords=s2tbx",
})
package org.esa.s2tbx.dataio.s2.preferences.ui;

import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;