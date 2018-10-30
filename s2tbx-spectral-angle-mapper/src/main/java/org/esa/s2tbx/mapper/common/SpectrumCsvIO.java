package org.esa.s2tbx.mapper.common;

import org.esa.snap.core.util.PropertyMap;
import org.esa.snap.core.util.io.CsvReader;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.esa.snap.ui.SnapFileChooser;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Razvan Dumitrascu
 */

public class SpectrumCsvIO {
    public static final SnapFileFilter CSV_FILE_FILTER = new SnapFileFilter("CSV", ".csv", "CSV (plain text)");
    private static final String DIAGRAM_GRAPH_IO_LAST_DIR_KEY = "diagramGraphIO.lastDir";

    public static SpectrumInput[] readSpectrum(Reader reader) throws IOException {

        CsvReader csvReader = new CsvReader(reader, new char[]{','});
        List<SpectrumInput> graphGroup = new ArrayList<>(5);
        List<int[]> dataRecords = new ArrayList<>(20);
        String[] headerRecord = csvReader.readRecord();
        String[] shapeDefinedRecord = csvReader.readRecord();
        while (true) {
            if (headerRecord.length < 1) {
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
                readGraphGroup(headerRecord,shapeDefinedRecord, dataRecords, graphGroup);
                headerRecord = record;
            }
        }
        readGraphGroup(headerRecord,shapeDefinedRecord, dataRecords, graphGroup);
        return graphGroup.toArray(new SpectrumInput[0]);
    }

    private  static int[] getRecords(String[] textRecord) throws IOException {
        int[] intRecord = new int[textRecord.length];
        for (int i = 0; i < textRecord.length; i++) {
            try {
                intRecord[i] = Integer.valueOf(textRecord[i]);
            } catch (NumberFormatException e) {
                intRecord[i] = -1;
            }
        }
        return intRecord;
    }

    public static SpectrumInput[] readSpectrum(Component parentComponent,
                                               String title,
                                               SnapFileFilter[] fileFilters,
                                               PropertyMap preferences) {
        File selectedFile = selectGraphFile(parentComponent, title, fileFilters, preferences, true);
        if (selectedFile != null) {
            try {
                try (FileReader fileReader = new FileReader(selectedFile)) {
                    return readSpectrum(fileReader);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parentComponent, "I/O error: " + e.getMessage());
            }
        }
        return new SpectrumInput[0];
    }

    private static void readGraphGroup(String[] headerRecord, String[]shapeDefinedRecord, List<int[]> dataRecords, List<SpectrumInput> graphs) {
        if ((dataRecords.size() > 0)&& (dataRecords.size() % 2 ==0)) {
            for (int index = 0; index < dataRecords.get(0).length; index++) {
                int[] xValues = new int[dataRecords.size() / 2];
                int[] yValues = new int[dataRecords.size() / 2];
                int counter = 0;
                for (int listIndex = 0; listIndex < dataRecords.size(); listIndex+=2) {
                    xValues[counter] = dataRecords.get(listIndex)[index];
                    yValues[counter] = dataRecords.get(listIndex+1)[index];
                    counter++;
                }
                SpectrumInput spec = new SpectrumInput(headerRecord[index], xValues, yValues);
                spec.setIsShapeDefined(Boolean.valueOf(shapeDefinedRecord[index]));
                graphs.add(spec);
            }
        } else {
            try {
                throw new IOException("Invalid format.");
            } catch (IOException e) {
                e.printStackTrace();
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

    public static void writeSpectrumList(Component parentComponent,
                                         String title,
                                         SnapFileFilter[] fileFilters,
                                         PropertyMap preferences,
                                         List<SpectrumInput> spectrumInputs) {
        if (spectrumInputs.size() == 0) {
            JOptionPane.showMessageDialog(parentComponent, "Nothing to save.");
            return;
        }
        File selectedFile = selectGraphFile(parentComponent, title, fileFilters, preferences, false);
        if (selectedFile != null) {
            try {
                try (FileWriter fileWriter = new FileWriter(selectedFile)) {
                    writeSpectrumList(spectrumInputs, fileWriter);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parentComponent, "I/O error: " + e.getMessage());
            }
        }
    }

    public static void writeSpectrumInputs(SpectrumInput[] spectrumInputs, Writer writer) throws IOException {
        List<SpectrumInput> spectrumInputsList = new ArrayList<>();
        Collections.addAll(spectrumInputsList, spectrumInputs);
        writeSpectrumList(spectrumInputsList, writer);
    }

    private static void writeSpectrumList(List<SpectrumInput> spectrumInputs, Writer writer) throws IOException {
        int numOfElements = 0;
        for(int index = 0; index <spectrumInputs.size()-1; index++){
            writer.write(spectrumInputs.get(index).getName());
            writer.write((int) ',');
        }
        writer.write(spectrumInputs.get(spectrumInputs.size()-1).getName());
        writer.write((int) '\n');
        for(int index = 0; index <spectrumInputs.size()-1; index++){
            writer.write(String.valueOf(spectrumInputs.get(index).getIsShapeDefined()));
            writer.write((int) ',');
        }
        writer.write(String.valueOf(spectrumInputs.get(spectrumInputs.size()-1).getIsShapeDefined()));
        writer.write((int) '\n');
        for (SpectrumInput spectrumInput : spectrumInputs) {
            int counter = 0;
            int length = spectrumInput.getXPixelPolygonPositions().length;
            for (int elementList = 0; elementList < length; elementList++) {
                int value = spectrumInput.getXPixelPolygonPositions()[elementList];
                if (value < 0) {
                    counter++;
                }
            }
            if (length - counter > numOfElements) {
                numOfElements = length - counter;
            }
        }

        for (int elementPosition = 0; elementPosition < numOfElements; elementPosition++) {
            for (int spectrumIndex = 0; spectrumIndex < spectrumInputs.size()-1; spectrumIndex++) {
                SpectrumInput spec = spectrumInputs.get(spectrumIndex);
                if (elementPosition < spec.getXPixelPolygonPositions().length) {
                    int value = spec.getXPixelPolygonPositions()[elementPosition];
                    if (value >= 0) {
                        writeValue(writer, String.valueOf(spec.getXPixelPolygonPositions()[elementPosition]));
                    } else {
                        writeValue(writer, "N/A");
                    }
                } else {
                    writeValue(writer, "N/A");
                }
            }
            SpectrumInput lastSpec = spectrumInputs.get(spectrumInputs.size()-1);
            if (elementPosition < lastSpec.getXPixelPolygonPositions().length) {
                int value = lastSpec.getXPixelPolygonPositions()[elementPosition];
                if (value >= 0) {
                    writer.write(String.valueOf(lastSpec.getXPixelPolygonPositions()[elementPosition]));
                } else {
                    writer.write("N/A");
                }
            } else {
                writer.write("N/A");
            }
            writer.write((int) '\n');

            for (int spectrumIndex = 0; spectrumIndex < spectrumInputs.size()-1; spectrumIndex++) {
                SpectrumInput spec = spectrumInputs.get(spectrumIndex);
                if(elementPosition < spec.getYPixelPolygonPositions().length){
                    int value = spec.getYPixelPolygonPositions()[elementPosition];
                    if (value >= 0) {
                        writeValue(writer, String.valueOf(spec.getYPixelPolygonPositions()[elementPosition]));
                    } else {
                        writeValue(writer, "N/A");
                    }
                } else {
                    writeValue(writer, "N/A");
                }
            }
            lastSpec = spectrumInputs.get(spectrumInputs.size()-1);
            if (elementPosition < lastSpec.getYPixelPolygonPositions().length) {
                int value = lastSpec.getYPixelPolygonPositions()[elementPosition];
                if (value >= 0) {
                    writer.write(String.valueOf(lastSpec.getYPixelPolygonPositions()[elementPosition]));
                } else {
                    writer.write("N/A");
                }
            } else {
                writer.write("N/A");
            }
            writer.write((int) '\n');
        }
    }

    private static void writeValue(Writer writer, String message) throws IOException {
        writer.write(message);
        writer.write((int) ',');
    }
}
