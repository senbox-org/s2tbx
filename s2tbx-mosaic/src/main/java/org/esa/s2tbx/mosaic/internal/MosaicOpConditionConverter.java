package org.esa.s2tbx.mosaic.internal;

import com.bc.ceres.binding.Converter;
import org.esa.snap.core.gpf.common.MosaicOp;

import java.util.ArrayList;
import java.util.List;

public class MosaicOpConditionConverter implements Converter<MosaicOp.Condition[]> {

    private static final String MOSAIC_OP_CONDITION_SEPARATOR = ":";
    private static final String MOSAIC_OP_CONDITION_FIELDS_SEPARATOR = ";";

    /**
     * Gets the value type.
     *
     * @return The value type.
     */
    @Override
    public Class<? extends MosaicOp.Condition[]> getValueType() {
        return MosaicOp.Condition[].class;
    }

    /**
     * Converts a value from its plain text representation to a Java object instance
     * of the type returned by {@link #getValueType()}.
     *
     * @param text The textual representation of the value.
     * @return The converted value.
     */
    @Override
    public MosaicOp.Condition[] parse(String text) {
        final List<MosaicOp.Condition> mosaicOpConditionList = new ArrayList<>();

        final String[] mosaicOpConditionElements = text.split(MOSAIC_OP_CONDITION_SEPARATOR);
        for (String mosaicOpConditionElem : mosaicOpConditionElements) {

            String[] mosaicOpConditionElemFields = mosaicOpConditionElem.split(MOSAIC_OP_CONDITION_FIELDS_SEPARATOR);
            if (mosaicOpConditionElemFields.length != 3) {
                continue;
            }
            String name = mosaicOpConditionElemFields[0];
            String expression = mosaicOpConditionElemFields[1];
            String outputStr = mosaicOpConditionElemFields[2];
            boolean output = Boolean.getBoolean(outputStr);

            final MosaicOp.Condition condition = new MosaicOp.Condition(name, expression, output);
            mosaicOpConditionList.add(condition);
        }

        return mosaicOpConditionList.toArray(new MosaicOp.Condition[0]);
    }

    /**
     * Converts a value of the type returned by {@link #getValueType()} to its
     * plain text representation.
     *
     * @param value The value to be converted to text.
     * @return The textual representation of the value.
     */
    @Override
    public String format(MosaicOp.Condition[] value) {
        String valueStr = "";
        if (value != null) {
            for (MosaicOp.Condition mosaicOpCondition : value) {
                String name = mosaicOpCondition.getName();
                String expression = mosaicOpCondition.getExpression();
                boolean output = mosaicOpCondition.isOutput();
                valueStr = (valueStr.isEmpty() ? valueStr : valueStr + MOSAIC_OP_CONDITION_SEPARATOR) + name + MOSAIC_OP_CONDITION_FIELDS_SEPARATOR + expression + MOSAIC_OP_CONDITION_FIELDS_SEPARATOR + Boolean.toString(output);
            }
        }
        return valueStr;
    }
}
