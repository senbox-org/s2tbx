/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esa.s2tbx.about;

import javax.swing.JLabel;
import org.esa.snap.rcp.about.AboutBox;

/**
 * @author Norman
 */
@AboutBox(displayName = "S2TBX", position = 20)
public class S2tbxAboutBox extends JLabel {
    
    public S2tbxAboutBox() {
        super("<html>This is the wonderful <b>Sentinel-2 Toolbox</b>");
    }
}
