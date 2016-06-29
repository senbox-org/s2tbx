package org.esa.s2tbx.biophysical;

import org.esa.snap.core.util.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created by jmalik on 17/06/16.
 */
public class BiophysicalAuxdata {

    public enum BiophysicalVariableCoeffs {

        NORMALISATION("Normalisation"),
        DENORMALISATION("Denormalisation"),
        EXTREME_CASES("ExtremeCases"),
        WEIGHTS_LAYER1_NEURONS("Weights_Layer1_Neurons"),
        WEIGHTS_LAYER1_BIAS("Weights_Layer1_Bias"),
        WEIGHTS_LAYER2_NEURONS("Weights_Layer2_Neurons"),
        WEIGHTS_LAYER2_BIAS("Weights_Layer2_Bias"),
        TEST_CASES("TestCases"),
        DEFINITION_DOMAIN_MINMAX("DefinitionDomain_MinMax"),
        DEFINITION_DOMAIN_GRID("DefinitionDomain_Grid");

        private String id;

        BiophysicalVariableCoeffs(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    private final BiophysicalVariable biophysicalVariable;
    private final HashMap<BiophysicalVariableCoeffs, double [][]> coeffsMap = new HashMap<>();

    private BiophysicalAuxdata(BiophysicalVariable biophysicalVariable) throws IOException {
        this.biophysicalVariable = biophysicalVariable;
        readBiophysicalVariableData(this.biophysicalVariable);
    }

    public static BiophysicalAuxdata makeBiophysicalAuxdata(BiophysicalVariable biophysicalVariable) throws IOException {
        return new BiophysicalAuxdata(biophysicalVariable);
    }

    public double [][] getCoeffs(BiophysicalVariableCoeffs coeff) {
        return this.coeffsMap.get(coeff);
    }

    void readBiophysicalVariableData(BiophysicalVariable variable) throws IOException {
        Path biophysicalVariableDataDir = BiophysicalActivator.getAuxDataDir().resolve("2_1").resolve(variable.name());
        for (BiophysicalVariableCoeffs coeffs : BiophysicalVariableCoeffs.values()) {
            Path biophysicalVariableDataFilename = biophysicalVariableDataDir.resolve(variable.name() + "_" + coeffs.getId());
            double [][]  csvTable = null;
            try {
                csvTable = readCsvToTable(biophysicalVariableDataFilename);
            } catch (IOException e) {
                // Some coefficients are not yet available (TestCases for Cw and FCover for example).
                // They won't be available for further processing
                SystemUtils.LOG.warning(String.format("Error when loading coefficients %s for variable %s. They won't be available.", coeffs.toString(), variable.toString()));
            }
            this.coeffsMap.put(coeffs, csvTable);
        }
    }

    static double[][] readCsvToTable(Path biophysicalVariableDataFilename) throws IOException {
        int nbLines = getNumberOfLines(biophysicalVariableDataFilename);
        int nbCols = getNumberOfColumns(biophysicalVariableDataFilename);
        double[][] tableData = new double[nbLines][nbCols];

        try (BufferedReader reader = Files.newBufferedReader(biophysicalVariableDataFilename)) {
            readTable(tableData, reader);
        } catch (IOException e) {
            SystemUtils.LOG.severe("Error when reading " + biophysicalVariableDataFilename);
            throw e;
        }

        return tableData;
    }

    static int getNumberOfLines(Path biophysicalVariableDataFilename) throws IOException {
        try (LineNumberReader lnr = new LineNumberReader(Files.newBufferedReader(biophysicalVariableDataFilename))) {
            lnr.skip(Long.MAX_VALUE);
            return lnr.getLineNumber();
        } catch (IOException e) {
            SystemUtils.LOG.severe("Error when reading " + biophysicalVariableDataFilename);
            throw e;
        }
    }

    static int getNumberOfColumns(Path biophysicalVariableDataFilename) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(biophysicalVariableDataFilename)) {
            String line = reader.readLine();
            int countCommas = line.length() - line.replace(",", "").length();
            return countCommas + 1;
        } catch (IOException e) {
            SystemUtils.LOG.severe("Error when reading " + biophysicalVariableDataFilename);
            throw e;
        }
    }

    private static void readTable(double[][] table, BufferedReader reader) throws IOException {
        final int numRows = table.length;
        final int numCols = table[0].length;
        StringTokenizer st;
        String line;
        String token;
        int row = 0;
        int col;
        while ((line = reader.readLine()) != null) {
            col = 0;
            if (row >= 0 && row < numRows) {
                st = new StringTokenizer(line, ",", false);
                while (st.hasMoreTokens()) {
                    token = st.nextToken();
                    if (col >= 0 && col < numCols) {
                        table[row][col] = Double.parseDouble(token);
                    }
                    col++;
                }
            }
            row++;
        }
    }


}
