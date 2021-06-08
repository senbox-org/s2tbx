package org.esa.s2tbx.mosaic.internal;

import com.bc.ceres.binding.dom.DomConverter;
import com.bc.ceres.binding.dom.DomElement;
import org.esa.snap.core.gpf.common.MosaicOp;

import java.util.ArrayList;
import java.util.List;

public class MosaicOpVariableDomConverter implements DomConverter {

    @Override
    public Class<?> getValueType() {
        return MosaicOp.Variable[].class;
    }

    @Override
    public MosaicOp.Variable[] convertDomToValue(DomElement parentElement, Object value) {

        final List<MosaicOp.Variable> mosaicOpVariableList = new ArrayList<>();

        final DomElement[] mosaicOpVariableElements = parentElement.getChildren();
        for (DomElement mosaicOpVariableElem : mosaicOpVariableElements) {

            String name = null;
            String expression = null;

            DomElement nameDom = mosaicOpVariableElem.getChild("name");
            if (nameDom != null) {
                name = nameDom.getValue();
            }

            DomElement expressionDom = mosaicOpVariableElem.getChild("expression");
            if (expressionDom != null) {
                expression = expressionDom.getValue();
            }

            final MosaicOp.Variable variable = new MosaicOp.Variable(name, expression);
            mosaicOpVariableList.add(variable);
        }

        return mosaicOpVariableList.toArray(new MosaicOp.Variable[0]);
    }


    @Override
    public void convertValueToDom(Object value, DomElement parentElement) {
        final MosaicOp.Variable[] mosaicOpVariables = (MosaicOp.Variable[]) value;

        if (mosaicOpVariables != null) {
            for (MosaicOp.Variable mosaicOpVariable : mosaicOpVariables) {
                DomElement mosaicOpVariableDom = parentElement.createChild("variable");

                final DomElement nameDom = mosaicOpVariableDom.createChild("name");
                nameDom.setValue(mosaicOpVariable.getName());

                DomElement expressionDom = mosaicOpVariableDom.createChild("expression");
                expressionDom.setValue(mosaicOpVariable.getExpression());
            }
        }
    }
}
