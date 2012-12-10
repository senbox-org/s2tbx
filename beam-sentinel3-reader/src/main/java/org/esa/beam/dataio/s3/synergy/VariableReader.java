package org.esa.beam.dataio.s3.synergy;

import ucar.ma2.Array;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;

class VariableReader {

    final NetcdfFile netcdfFile;

    VariableReader(NetcdfFile netcdfFile) {
        this.netcdfFile = netcdfFile;
    }

    double[] read(String name) throws IOException {
        return getDoubles(netcdfFile, name);
    }

    private double[] getDoubles(NetcdfFile ncFile, String name) throws IOException {
        final Variable variable = ncFile.findVariable(name);

        if (variable != null) {
            final double scaleFactor = getAttributeDouble(variable, "scale_factor", 1.0);
            final double addOffset = getAttributeDouble(variable, "add_offset", 0.0);
            final Array array = variable.read();

            final double[] data = new double[(int) variable.getSize()];
            for (int i = 0; i < data.length; i++) {
                data[i] = addOffset + array.getDouble(i) * scaleFactor;
            }
            return data;
        }

        return null;
    }

    private double getAttributeDouble(Variable variable, String attributeName, double defaultValue) {
        final Attribute attribute = variable.findAttribute(attributeName);
        if (attribute == null) {
            return defaultValue;
        }
        return attribute.getNumericValue().doubleValue();
    }
}
