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

    public enum BiophysicalIndicatorCoeffs {

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

        BiophysicalIndicatorCoeffs(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    private final BiophysicalIndicator biophysicalIndicator;
    private final HashMap<BiophysicalIndicatorCoeffs, double [][]> coeffsMap = new HashMap<>();

    private BiophysicalAuxdata(BiophysicalIndicator biophysicalIndicator) throws IOException {
        this.biophysicalIndicator = biophysicalIndicator;
        readIndicatorData(this.biophysicalIndicator);
    }

    public static BiophysicalAuxdata makeBiophysicalAuxdata(BiophysicalIndicator biophysicalIndicator) throws IOException {
        return new BiophysicalAuxdata(biophysicalIndicator);
    }

    public double [][] getCoeffs(BiophysicalIndicatorCoeffs coeff) {
        return this.coeffsMap.get(coeff);
    }

    void readIndicatorData(BiophysicalIndicator indicator) throws IOException {
        Path indicatorDataDir = BiophysicalActivator.getAuxDataDir().resolve("2_1").resolve(indicator.name());
        for (BiophysicalIndicatorCoeffs coeffs : BiophysicalIndicatorCoeffs.values()) {
            Path indicatorDataFilename = indicatorDataDir.resolve(indicator.name() + "_" + coeffs.getId());
            double [][]  csvTable = null;
            try {
                csvTable = readCsvToTable(indicatorDataFilename);
            } catch (IOException e) {
                // Some coefficients are not yet available (TestCases for Cw and FCover for example).
                // They won't be available for further processing
                SystemUtils.LOG.warning(String.format("Error when loading coefficients %s for indicator %s. They won't be available.", coeffs.toString(), indicator.toString()));
            }
            this.coeffsMap.put(coeffs, csvTable);
        }
    }

    static double[][] readCsvToTable(Path indicatorDataFilename) throws IOException {
        int nbLines = getNumberOfLines(indicatorDataFilename);
        int nbCols = getNumberOfColumns(indicatorDataFilename);
        double[][] tableData = new double[nbLines][nbCols];

        try (BufferedReader reader = Files.newBufferedReader(indicatorDataFilename)) {
            readTable(tableData, reader);
        } catch (IOException e) {
            SystemUtils.LOG.severe("Error when reading " + indicatorDataFilename);
            throw e;
        }

        return tableData;
    }

    static int getNumberOfLines(Path indicatorDataFilename) throws IOException {
        try (LineNumberReader lnr = new LineNumberReader(Files.newBufferedReader(indicatorDataFilename))) {
            lnr.skip(Long.MAX_VALUE);
            return lnr.getLineNumber();
        } catch (IOException e) {
            SystemUtils.LOG.severe("Error when reading " + indicatorDataFilename);
            throw e;
        }
    }

    static int getNumberOfColumns(Path indicatorDataFilename) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(indicatorDataFilename)) {
            String line = reader.readLine();
            int countCommas = line.length() - line.replace(",", "").length();
            return countCommas + 1;
        } catch (IOException e) {
            SystemUtils.LOG.severe("Error when reading " + indicatorDataFilename);
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
