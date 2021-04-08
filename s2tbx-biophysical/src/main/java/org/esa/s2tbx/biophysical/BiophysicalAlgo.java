package org.esa.s2tbx.biophysical;

import com.bc.jnn.JnnConnection;
import com.bc.jnn.JnnConstants;
import com.bc.jnn.JnnLayer;
import com.bc.jnn.JnnNet;
import com.bc.jnn.JnnUnit;

import static java.util.Objects.deepEquals;

/**
 * Created by jmalik on 20/06/16.
 */
public class BiophysicalAlgo {

    public class Result {

        private double outputValue = 0.0;

        // input outside definition domain
        private boolean inputOutOfRange = false;

        // Output_Min-Tolerance < Output < Output_Min  then Output = Output_Min
        private boolean outputThresholdedToMinOutput = false;

        // Output_Max < Output < Output_Max+Tolerance  then Output = Output_Max
        private boolean outputThresholdedToMaxOutput = false;

        // Output < Output_Min-Tolerance
        private boolean outputTooLow = false;

        // Output_Max+Tolerance < Output
        private boolean outputTooHigh = false;


        public double getOutputValue() {
            return outputValue;
        }

        public boolean isInputOutOfRange() {
            return inputOutOfRange;
        }

        public boolean isOutputThresholdedToMinOutput() {
            return outputThresholdedToMinOutput;
        }

        public boolean isOutputThresholdedToMaxOutput() {
            return outputThresholdedToMaxOutput;
        }

        public boolean isOutputTooLow() {
            return outputTooLow;
        }

        public boolean isOutputTooHigh() {
            return outputTooHigh;
        }

        void setOutputValue(double outputValue) {
            this.outputValue = outputValue;
        }

        void setInputOutOfRange(boolean inputOutOfRange) {
            this.inputOutOfRange = inputOutOfRange;
        }

        void setOutputThresholdedToMinOutput(boolean outputThresholdedToMinOutput) {
            this.outputThresholdedToMinOutput = outputThresholdedToMinOutput;
        }

        void setOutputThresholdedToMaxOutput(boolean outputThresholdedToMaxOutput) {
            this.outputThresholdedToMaxOutput = outputThresholdedToMaxOutput;
        }

        void setOutputTooLow(boolean outputTooLow) {
            this.outputTooLow = outputTooLow;
        }

        void setOutputTooHigh(boolean outputTooHigh) {
            this.outputTooHigh = outputTooHigh;
        }
    }

    private final BiophysicalAuxdata auxdata;
    private ThreadLocal<JnnNet> net;


    BiophysicalAlgo(BiophysicalAuxdata auxdata) {
        this.auxdata = auxdata;
        initNN();
    }

    public Result process(double[] input) {
        Result result = new Result();

        // Identify input-out-of-range
        processInputOutOfRange(input, result);
        /*if (result.isInputOutOfRange()) {
            return result;
        }*/

        // Run the Neural Network
        runNN(input, result);

        // Handle Extreme cases
        processOutputOutOfRange(result);

        return result;
    }

    private void setInputOutOfRange(Result result) {
        result.setInputOutOfRange(true);
        result.setOutputValue(Double.NaN);
    }

    private void processInputOutOfRange(double[] input, Result result) {

        // TODO for some variables, we don't have the definition domain yet (FCOVER).
        // They should be provided, and the != null tests removed.

        /*
         * First, check independently for each band if it is within bounds
         */
        double [][] bandMinMax = this.auxdata.getCoeffs(BiophysicalAuxdata.BiophysicalVariableCoeffs.DEFINITION_DOMAIN_MINMAX);

        if (bandMinMax != null) {
            for (int i = 0; i < bandMinMax[0].length; i++) {
                double bandMin = bandMinMax[0][i];
                double bandMax = bandMinMax[1][i];
                if (input[i] < bandMin || bandMax < input[i]) {
                    setInputOutOfRange(result);
                    return;
                }
            }
        }

        /*
         * Second check : be sure input is within the approximated convex hull (see ATBD)
         */

        // Build projection in grid
        double [][] definitionDomain = this.auxdata.getCoeffs(BiophysicalAuxdata.BiophysicalVariableCoeffs.DEFINITION_DOMAIN_GRID);

        if (bandMinMax != null && definitionDomain != null) {
            int [] gridProjection = new int[definitionDomain[0].length];
            for (int i = 0; i < gridProjection.length; i++) {
                double bandMin = bandMinMax[0][i];
                double bandMax = bandMinMax[1][i];
                gridProjection[i] = (int)Math.floor(10 * (input[i] - bandMin) / (bandMax - bandMin) + 1);
            }

            // Find whether gridProjection matches one of the entry in the domain definition grid
            // todo : using an HashSet would improve performance, there are 11000 entries to test
            boolean insideDefinitionDomain = false;
            for (int row = 0; row < definitionDomain.length; row++) {
                double [] definitionDomainEntry = definitionDomain[row];
                int [] definitionDomainEntryInt = new int[definitionDomainEntry.length];
                for (int i = 0; i < definitionDomainEntry.length; i++) {
                    definitionDomainEntryInt[i] = (int)definitionDomainEntry[i];
                }

                if (deepEquals(definitionDomainEntryInt, gridProjection)) {
                    insideDefinitionDomain = true;
                    break;
                }
            }
            if (!insideDefinitionDomain) {
                setInputOutOfRange(result);
                return;
            }

        }
    }

    private void runNN(double[] input, Result result) {
        double[] output = new double[1];
        this.net.get().process(input, output);
        result.setOutputValue(output[0]);
    }

    private void processOutputOutOfRange(Result result) {
        double [][] extremeCases = this.auxdata.getCoeffs(BiophysicalAuxdata.BiophysicalVariableCoeffs.EXTREME_CASES);

        double rawOutput = result.getOutputValue();

        double tolerance = extremeCases[0][0];
        double outputMin = extremeCases[0][1];
        double outputMax = extremeCases[0][2];

        if (rawOutput < outputMin - tolerance) {
            result.setOutputTooLow(true);
        }
        else if (outputMin - tolerance < rawOutput && rawOutput < outputMin ) {
            result.setOutputValue(outputMin);
            result.setOutputThresholdedToMinOutput(true);
        }
        else if (outputMax < rawOutput && rawOutput < outputMax + tolerance ) {
            result.setOutputValue(outputMax);
            result.setOutputThresholdedToMaxOutput(true);
        }
        else if (outputMax + tolerance < rawOutput) {
            result.setOutputTooHigh(true);
        }
    }


    private void initNN() {
        JnnNet localNet = new JnnNet();

        double [][] normalisation = this.auxdata.getCoeffs(BiophysicalAuxdata.BiophysicalVariableCoeffs.NORMALISATION);
        double [][] denormalisation = this.auxdata.getCoeffs(BiophysicalAuxdata.BiophysicalVariableCoeffs.DENORMALISATION);
        double [][] layer1_weights = this.auxdata.getCoeffs(BiophysicalAuxdata.BiophysicalVariableCoeffs.WEIGHTS_LAYER1_NEURONS);
        double [][] layer1_bias = this.auxdata.getCoeffs(BiophysicalAuxdata.BiophysicalVariableCoeffs.WEIGHTS_LAYER1_BIAS);
        double [][] layer2_weights = this.auxdata.getCoeffs(BiophysicalAuxdata.BiophysicalVariableCoeffs.WEIGHTS_LAYER2_NEURONS);
        double [][] layer2_bias = this.auxdata.getCoeffs(BiophysicalAuxdata.BiophysicalVariableCoeffs.WEIGHTS_LAYER2_BIAS);

        localNet.setNumLayers(3);
        localNet.setInputLayerIndex(0);
        localNet.setOutputLayerIndex(2);

        // Create source units (reflectance + angles : 11 units)
        JnnLayer sourceLayer = new JnnLayer();
        sourceLayer.setNumUnits(normalisation.length);
        sourceLayer.setInputFunction(JnnConstants.NN_FUNC_SUM_1);
        sourceLayer.setActivationFunction(JnnConstants.NN_FUNC_IDENTITY);
        sourceLayer.setOutputFunction(JnnConstants.NN_FUNC_LINEAR);
        sourceLayer.initFunctions(false, new StringBuffer());
        localNet.setLayerAt(0, sourceLayer);
        for (int i = 0; i < sourceLayer.getNumUnits(); i++) {
            JnnUnit sourceUnit = new JnnUnit();
            sourceUnit.setNumConnections(0);
            // Normalisation : X' = 2*(X-XMin)/(XMax-XMin)-1
            double xMin = normalisation[i][0];
            double xMax = normalisation[i][1];
            sourceUnit.setOutputBias(-2.0 * xMin / (xMax-xMin) - 1);
            sourceUnit.setOutputScale(2. / (xMax-xMin));
            sourceLayer.setUnitAt(i, sourceUnit);
        }

        // Create hidden layer (5 units)
        JnnLayer hiddenLayer = new JnnLayer();
        hiddenLayer.setNumUnits(layer1_weights.length);
        hiddenLayer.setInputFunction(JnnConstants.NN_FUNC_SUM_1);
        hiddenLayer.setActivationFunction(JnnConstants.NN_FUNC_TANG_SIGMOID);
        hiddenLayer.setOutputFunction(JnnConstants.NN_FUNC_IDENTITY);
        hiddenLayer.initFunctions(false, new StringBuffer());
        localNet.setLayerAt(1, hiddenLayer);
        for (int i = 0; i < hiddenLayer.getNumUnits(); i++) {
            JnnUnit hiddenUnit = new JnnUnit();
            hiddenUnit.setNumConnections(sourceLayer.getNumUnits());
            hiddenUnit.setInputBias(layer1_bias[0][i]);
            for (int j = 0; j < sourceLayer.getNumUnits(); j++) {
                JnnUnit sourceUnit = sourceLayer.getUnitAt(j);
                JnnConnection connection = new JnnConnection();
                connection.setInputUnit(sourceUnit);
                connection.setWeight(layer1_weights[i][j]);
                connection.setSourceLayerIndex(0);
                connection.setSourceUnitIndex(j);
                hiddenUnit.setConnectionAt(j, connection);

            }
            hiddenLayer.setUnitAt(i, hiddenUnit);
        }

        // Create output layer (1 unit)
        JnnLayer outputLayer = new JnnLayer();
        outputLayer.setNumUnits(layer2_weights.length);
        outputLayer.setInputFunction(JnnConstants.NN_FUNC_SUM_1);
        outputLayer.setActivationFunction(JnnConstants.NN_FUNC_IDENTITY);
        outputLayer.setOutputFunction(JnnConstants.NN_FUNC_LINEAR);
        outputLayer.initFunctions(false, new StringBuffer());
        localNet.setLayerAt(2, outputLayer);
        for (int i = 0; i < outputLayer.getNumUnits(); i++) {
            JnnUnit outputUnit = new JnnUnit();
            outputUnit.setNumConnections(hiddenLayer.getNumUnits());
            outputUnit.setInputBias(layer2_bias[0][i]);
            for (int j = 0; j < hiddenLayer.getNumUnits(); j++) {
                JnnUnit hiddenUnit = hiddenLayer.getUnitAt(j);
                JnnConnection connection = new JnnConnection();
                connection.setInputUnit(hiddenUnit);
                connection.setWeight(layer2_weights[i][j]);
                connection.setSourceLayerIndex(1);
                connection.setSourceUnitIndex(j);
                outputUnit.setConnectionAt(j, connection);
            }
            // Denormalisation : Y = 0.5*(Y'+1)*(YMax-YMin)+YMin}
            double yMin = denormalisation[i][0];
            double yMax = denormalisation[i][1];
            outputUnit.setOutputScale(0.5 * (yMax-yMin));
            outputUnit.setOutputBias(0.5 * (yMax-yMin) + yMin);
            outputLayer.setUnitAt(i, outputUnit);
        }

        this.net = new ThreadLocal<JnnNet>() {
            @Override
            protected JnnNet initialValue() {
                return localNet.clone();
            }
        };

    }

}
