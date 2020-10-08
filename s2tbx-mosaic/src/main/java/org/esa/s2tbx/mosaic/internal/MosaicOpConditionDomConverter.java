package org.esa.s2tbx.mosaic.internal;

import com.bc.ceres.binding.dom.DomConverter;
import com.bc.ceres.binding.dom.DomElement;
import org.esa.snap.core.gpf.common.MosaicOp;

import java.util.ArrayList;
import java.util.List;

public class MosaicOpConditionDomConverter implements DomConverter {

    @Override
    public Class<?> getValueType() {
        return MosaicOp.Condition[].class;
    }

    @Override
    public MosaicOp.Condition[] convertDomToValue(DomElement parentElement, Object value) {

        final List<MosaicOp.Condition> mosaicOpConditionList = new ArrayList<>();

        final DomElement[] mosaicOpConditionElements = parentElement.getChildren();
        for (DomElement mosaicOpConditionElem : mosaicOpConditionElements) {

            String name = null;
            String expression = null;
            boolean output = false;

            DomElement nameDom = mosaicOpConditionElem.getChild("name");
            if (nameDom != null) {
                name = nameDom.getValue();
            }

            DomElement expressionDom = mosaicOpConditionElem.getChild("expression");
            if (expressionDom != null) {
                expression = expressionDom.getValue();
            }

            DomElement outputDom = mosaicOpConditionElem.getChild("output");
            if (outputDom != null) {
                output = Boolean.getBoolean(outputDom.getValue());
            }

            final MosaicOp.Condition variable = new MosaicOp.Condition(name, expression, output);
            mosaicOpConditionList.add(variable);
        }

        return mosaicOpConditionList.toArray(new MosaicOp.Condition[0]);
    }


    @Override
    public void convertValueToDom(Object value, DomElement parentElement) {
        final MosaicOp.Condition[] mosaicOpConditions = (MosaicOp.Condition[]) value;

        if (mosaicOpConditions != null) {
            for (MosaicOp.Condition mosaicOpCondition : mosaicOpConditions) {
                DomElement mosaicOpConditionDom = parentElement.createChild("variable");

                final DomElement nameDom = mosaicOpConditionDom.createChild("name");
                nameDom.setValue(mosaicOpCondition.getName());

                DomElement expressionDom = mosaicOpConditionDom.createChild("expression");
                expressionDom.setValue(mosaicOpCondition.getExpression());

                DomElement outputDom = mosaicOpConditionDom.createChild("output");
                outputDom.setValue(Boolean.toString(mosaicOpCondition.isOutput()));
            }
        }
    }
}
