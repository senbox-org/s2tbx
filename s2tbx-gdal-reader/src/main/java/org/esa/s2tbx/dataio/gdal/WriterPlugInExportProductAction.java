package org.esa.s2tbx.dataio.gdal;

import org.esa.snap.core.dataio.*;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.rcp.SnapApp;
import org.esa.snap.rcp.actions.file.ExportProductAction;
import org.esa.snap.rcp.actions.file.ProductFileChooser;
import org.esa.snap.rcp.util.Dialogs;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.MessageFormat;

/**
 * @author Jean Coravu
 */
public class WriterPlugInExportProductAction extends ExportProductAction {

    public WriterPlugInExportProductAction() {
        super();
    }

    @Override
    public boolean isEnabled() {
        Product product = SnapApp.getDefault().getAppContext().getSelectedProduct();
        return (product != null);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Product product = SnapApp.getDefault().getAppContext().getSelectedProduct();
        setProduct(product);
        String formatName = GDALProductWriterPlugIn.FORMAT_NAME;
        setFormatName(formatName);
        exportProduct(product, formatName);
    }

    private Boolean exportProduct(Product product, String formatName) {
        ProductWriter productWriter = findProductWriter(product, formatName);
        if (productWriter == null) {
            return null;
        }

        GDALProductWriterPlugIn plugIn = (GDALProductWriterPlugIn)productWriter.getWriterPlugIn();
        GDALDriverInfo[] writerDrivers = plugIn.getWriterDrivers();
        ExportDriversFileFilter[] filters = new ExportDriversFileFilter[writerDrivers.length];
        for (int i=0; i< writerDrivers.length; i++) {
            String description = writerDrivers[i].getDriverDisplayName() + " (*" + writerDrivers[i].getExtensionName() + ")";
            filters[i] = new ExportDriversFileFilter(description, writerDrivers[i].getExtensionName());
        }

        ProductFileChooser fc = buildFileChooserDialog(product, formatName, false, null);
        for (int i=0; i<filters.length; i++) {
            fc.addChoosableFileFilter(filters[i]);
        }
        int returnVal = fc.showSaveDialog(SnapApp.getDefault().getMainFrame());
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            // cancelled
            return null;
        }

        File newFile = fc.getSelectedFile();
        if (newFile == null) {
            // cancelled
            return null;
        }
        FileFilter selectedFileFilter = fc.getFileFilter();
        if (!selectedFileFilter.accept(newFile)) {
            String message = MessageFormat.format("The extension of the selected file\n" +
                            "''{0}''\n" +
                            "does not match the selected file type.\n" +
                            "Please set the file extension according to the selected file type.",
                    newFile.getPath());
            Dialogs.showWarning(getDisplayName(), message, null);
            return false;
        }
        if (!canWriteSelectedFile(newFile)) {
            return false;
        }

        Product exportProduct = fc.getSubsetProduct() != null ? fc.getSubsetProduct() : product;

        return exportProduct(exportProduct, newFile, formatName);
    }
}
