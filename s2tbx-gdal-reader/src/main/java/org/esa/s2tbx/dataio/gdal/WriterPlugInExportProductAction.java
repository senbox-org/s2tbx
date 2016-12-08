package org.esa.s2tbx.dataio.gdal;

import org.esa.s2tbx.dataio.gdal.activator.GDALDriverInfo;
import org.esa.snap.core.dataio.*;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.rcp.SnapApp;
import org.esa.snap.rcp.actions.file.ExportProductAction;
import org.esa.snap.rcp.actions.file.ProductFileChooser;
import org.esa.snap.rcp.actions.file.ProductOpener;
import org.esa.snap.rcp.actions.file.WriteProductOperation;
import org.esa.snap.rcp.util.Dialogs;
import org.gdal.gdal.gdal;
import org.netbeans.api.progress.ProgressUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.prefs.Preferences;

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
            filters[i] = new ExportDriversFileFilter(description, writerDrivers[i]);
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
        ExportDriversFileFilter selectedFileFilter = (ExportDriversFileFilter)fc.getFileFilter();
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
        Band sourceBand = exportProduct.getBandAt(0);
        int gdalDataType = GDALProductWriter.getGDALDataType(sourceBand.getDataType());
        GDALDriverInfo driverInfo = selectedFileFilter.getDriverInfo();
        if (!driverInfo.canExportProduct(gdalDataType)) {
            Dialogs.showWarning(getDisplayName(), driverInfo.getFailedMessageToExportProduct(gdalDataType, "\n"), null);
            return false;
        }

        return exportProduct(exportProduct, newFile, formatName);
    }

    private boolean canWriteSelectedFile(File newFile) {
        if (newFile.isFile() && !newFile.canWrite()) {
            Dialogs.showWarning(getDisplayName(),
                    MessageFormat.format("The product\n" +
                                    "''{0}''\n" +
                                    "exists and cannot be overwritten, because it is read only.\n" +
                                    "Please choose another file or remove the write protection.",
                            newFile.getPath()),
                    null);
            return false;
        }
        return true;
    }

    private Boolean exportProduct(Product exportProduct, File newFile, String formatName) {
        SnapApp.getDefault().setStatusBarMessage(MessageFormat.format("Exporting product ''{0}'' to {1}...", exportProduct.getDisplayName(), newFile));

        WriteProductOperation operation = new WriteProductOperation(exportProduct, newFile, formatName, false);
        ProgressUtils.runOffEventThreadWithProgressDialog(operation,
                getDisplayName(),
                operation.getProgressHandle(),
                true,
                50,
                1000);

        SnapApp.getDefault().setStatusBarMessage("");

        return operation.getStatus();
    }

    private ProductWriter findProductWriter(Product product, String formatName) {
        final ProductWriter productWriter = ProductIO.getProductWriter(formatName);
        if (productWriter == null) {
            Dialogs.showError(getDisplayName(), MessageFormat.format("No writer found for format {0}.", formatName));
            return null;
        }
        final EncodeQualification encodeQualification = productWriter.getWriterPlugIn().getEncodeQualification(product);
        if (encodeQualification.getPreservation() == EncodeQualification.Preservation.UNABLE) {
            Dialogs.showError(getDisplayName(), MessageFormat.format("Writing this product as {0} is not possible:\n"
                            + encodeQualification.getInfoString(),
                    formatName
            ));
            return null;
        }
        return productWriter;
    }

    private ProductFileChooser buildFileChooserDialog(Product product, String formatName, boolean useSubset, FileFilter filter) {
        Preferences preferences = SnapApp.getDefault().getPreferences();
        ProductFileChooser fc = new ProductFileChooser(new File(preferences.get(ProductOpener.PREFERENCES_KEY_LAST_PRODUCT_DIR, ".")));
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        fc.setSubsetEnabled(useSubset);
        if (filter != null) {
            fc.addChoosableFileFilter(filter);
        }
        fc.setProductToExport(product);
        return fc;
    }
}
