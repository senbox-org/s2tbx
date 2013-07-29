package org.esa.beam.dataio.atmcorr.ui;


import com.bc.ceres.swing.SwingHelper;
import com.bc.ceres.swing.progress.ProgressDialog;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 * @author Tonio Fincke
 */
public class AtmCorrProcessDialog extends ProgressDialog {

    public AtmCorrProcessDialog(Component parentComponent) {
        super(parentComponent);
    }

}
