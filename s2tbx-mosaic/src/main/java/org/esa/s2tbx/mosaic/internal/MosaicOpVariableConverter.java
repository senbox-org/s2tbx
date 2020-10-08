package org.esa.s2tbx.mosaic.internal;

import com.bc.ceres.binding.Converter;
import org.esa.snap.core.gpf.common.MosaicOp;

import java.util.ArrayList;
import java.util.List;

public class MosaicOpVariableConverter implements Converter<MosaicOp.Variable[]> {

    private static final String MOSAIC_OP_VARIABLE_SEPARATOR = ":";
    private static final String MOSAIC_OP_VARIABLE_FIELDS_SEPARATOR = ";";

    /**
     * Gets the value type.
     *
     * @return The value type.
     */
    @Override
    public Class<? extends MosaicOp.Variable[]> getValueType() {
        return MosaicOp.Variable[].class;
    }

    /**
     * Converts a value from its plain text representation to a Java object instance
     * of the type returned by {@link #getValueType()}.
     *
     * @param text The textual representation of the value.
     * @return The converted value.
     */
    @Override
    public MosaicOp.Variable[] parse(String text) {
        final List<MosaicOp.Variable> mosaicOpVariableList = new ArrayList<>();

        final String[] mosaicOpVariableElements = text.split(MOSAIC_OP_VARIABLE_SEPARATOR);
        for (String mosaicOpVariableElem : mosaicOpVariableElements) {

            String[] mosaicOpVariableElemFields = mosaicOpVariableElem.split(MOSAIC_OP_VARIABLE_FIELDS_SEPARATOR);
            if (mosaicOpVariableElemFields.length != 2) {
                continue;
            }
            String name = mosaicOpVariableElemFields[0];
            String expression = mosaicOpVariableElemFields[1];

            final MosaicOp.Variable variable = new MosaicOp.Variable(name, expression);
            mosaicOpVariableList.add(variable);
        }

        return mosaicOpVariableList.toArray(new MosaicOp.Variable[0]);
    }

    /**
     * Converts a value of the type returned by {@link #getValueType()} to its
     * plain text representation.
     *
     * @param value The value to be converted to text.
     * @return The textual representation of the value.
     */
    @Override
    public String format(MosaicOp.Variable[] value) {
        String valueStr = "";
        if(value != null) {
            for (MosaicOp.Variable mosaicOpVariable : value) {
                String name = mosaicOpVariable.getName();
                String expression = mosaicOpVariable.getExpression();
                valueStr = (valueStr.isEmpty() ? valueStr : valueStr + MOSAIC_OP_VARIABLE_SEPARATOR) + name + MOSAIC_OP_VARIABLE_FIELDS_SEPARATOR + expression;
            }
        }
        return valueStr;
    }
}
