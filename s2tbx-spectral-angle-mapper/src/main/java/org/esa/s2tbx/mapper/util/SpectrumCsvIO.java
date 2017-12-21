package org.esa.s2tbx.mapper.util;

import org.esa.snap.core.util.PropertyMap;
import org.esa.snap.core.util.io.CsvReader;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.esa.snap.ui.SnapFileChooser;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Razvan Dumitrascu
 */

public class SpectrumCsvIO {
    public static final SnapFileFilter CSV_FILE_FILTER = new SnapFileFilter("CSV", ".csv", "CSV (plain text)");
    public static final String DIAGRAM_GRAPH_IO_LAST_DIR_KEY = "diagramGraphIO.lastDir";

    public static SpectrumInput[] readGraphs(Reader reader) throws IOException {

        CsvReader csvReader = new CsvReader(reader, new char[]{','});
        List<SpectrumInput> graphGroup = new ArrayList<>(5);
        List<int[]> dataRecords = new ArrayList<>(20);
        String[] headerRecord = csvReader.readRecord();
        while (true) {
            if (headerRecord.length < 2) {
                throw new IOException("Invalid format.");
            }
            String[] record = csvReader.readRecord();
            if (record == null) {
                break;
            }
            int[] dataRecord = getRecords(record);
            if (dataRecord != null) {
                if (dataRecord.length != headerRecord.length) {
                    throw new IOException("Invalid format.");
                }
                dataRecords.add(dataRecord);
            } else {
                readGraphGroup(headerRecord, dataRecords, graphGroup);
                headerRecord = record;
            }
        }
        readGraphGroup(headerRecord, dataRecords, graphGroup);
        return graphGroup.toArray(new SpectrumInput[0]);
    }

    public static int[] getRecords(String[] textRecord) throws IOException {
        int[] intRecord = new int[textRecord.length];
        for (int i = 0; i < textRecord.length; i++) {
            try {
                intRecord[i] = Integer.valueOf(textRecord[i]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return intRecord;
    }



    public static SpectrumInput[] readGraphs(Component parentComponent,
                                             String title,
                                             SnapFileFilter[] fileFilters,
                                             PropertyMap preferences) {
        File selectedFile = selectGraphFile(parentComponent, title, fileFilters, preferences, true);
        if (selectedFile != null) {
            try {
                FileReader fileReader = new FileReader(selectedFile);
                try {
                    return readGraphs(fileReader);
                } finally {
                    fileReader.close();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parentComponent, "I/O error: " + e.getMessage());
            }
        }
        return new SpectrumInput[0];
    }

    private static void readGraphGroup(String[] headerRecord, List<int[]> dataRecords, List<SpectrumInput> graphs) {
        if (dataRecords.size() > 0) {
            int[] dataRecord0 = dataRecords.get(0);
            int[] xValues = new int[dataRecord0.length];
            int[] yValues = new int[dataRecord0.length];
            for (int j = 0; j < dataRecord0.length; j++) {
                xValues[j] = dataRecords.get(0)[j];
                yValues[j] = dataRecords.get(1)[j];
                graphs.add(new SpectrumInput(headerRecord[j], xValues[j], yValues[j]));
            }
        }
        dataRecords.clear();
    }

    private static File selectGraphFile(Component parentComponent,
                                        String title,
                                        SnapFileFilter[] fileFilters,
                                        PropertyMap preferences,
                                        boolean open) {
        String lastDirPath = preferences.getPropertyString(DIAGRAM_GRAPH_IO_LAST_DIR_KEY, ".");
        SnapFileChooser fileChooser = new SnapFileChooser(new File(lastDirPath));
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setDialogTitle(title);
        for (SnapFileFilter fileFilter : fileFilters) {
            fileChooser.addChoosableFileFilter(fileFilter);
        }
        fileChooser.setFileFilter(fileFilters[0]);
        if (open) {
            fileChooser.setDialogType(SnapFileChooser.OPEN_DIALOG);
        } else {
            fileChooser.setDialogType(SnapFileChooser.SAVE_DIALOG);
        }
        File selectedFile;
        while (true) {
            int i = fileChooser.showDialog(parentComponent, null);
            if (i == SnapFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                if (open || !selectedFile.exists()) {
                    break;
                }
                i = JOptionPane.showConfirmDialog(parentComponent,
                        "The file\n" + selectedFile + "\nalready exists.\nOverwrite?",
                        "File exists", JOptionPane.YES_NO_CANCEL_OPTION);
                if (i == JOptionPane.CANCEL_OPTION) {
                    // Canceled
                    selectedFile = null;
                    break;
                } else if (i == JOptionPane.YES_OPTION) {
                    // Overwrite existing file
                    break;
                }
            } else {
                // Canceled
                selectedFile = null;
                break;
            }
        }
        if (selectedFile != null) {
            preferences.setPropertyString(DIAGRAM_GRAPH_IO_LAST_DIR_KEY, selectedFile.getParent());
        }
        return selectedFile;
    }
}
