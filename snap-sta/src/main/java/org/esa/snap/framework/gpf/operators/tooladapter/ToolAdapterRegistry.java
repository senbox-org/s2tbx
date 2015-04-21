package org.esa.snap.framework.gpf.operators.tooladapter;

import org.esa.snap.framework.gpf.GPF;
import org.esa.snap.framework.gpf.OperatorException;
import org.esa.snap.framework.gpf.OperatorSpi;
import org.esa.snap.framework.gpf.OperatorSpiRegistry;
import org.esa.snap.framework.gpf.descriptor.OperatorDescriptor;
import org.esa.snap.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kraftek on 4/20/2015.
 */
public enum ToolAdapterRegistry {
    INSTANCE;

    private final Map<String, ToolAdapterOpSpi> registeredAdapters;

    ToolAdapterRegistry() {
        registeredAdapters = new HashMap<>();
    }

    public Map<String, ToolAdapterOpSpi> getOperatorMap() {
        return registeredAdapters;
    }

    public void registerOperator(ToolAdapterOpSpi operatorSpi) throws OperatorException {
        OperatorDescriptor operatorDescriptor = operatorSpi.getOperatorDescriptor();
        String operatorName = operatorDescriptor.getName() != null ? operatorDescriptor.getName() : operatorDescriptor.getAlias();
        OperatorSpiRegistry operatorSpiRegistry = GPF.getDefaultInstance().getOperatorSpiRegistry();
        if (operatorSpiRegistry.getOperatorSpi(operatorName) == null) {
            operatorSpiRegistry.addOperatorSpi(operatorName, operatorSpi);
        }
        if (registeredAdapters.containsKey(operatorName)) {
            throw new OperatorException("An operator with the same name is already registered!");
        }
        registeredAdapters.put(operatorName, operatorSpi);
    }

    public void removeOperator(ToolAdapterOperatorDescriptor operatorDescriptor) {
        if (!operatorDescriptor.isSystem()) {
            String operatorDescriptorName = operatorDescriptor.getName();
            if (registeredAdapters.containsKey(operatorDescriptorName)) {
                registeredAdapters.remove(operatorDescriptorName);
            }
            OperatorSpiRegistry operatorSpiRegistry = GPF.getDefaultInstance().getOperatorSpiRegistry();
            OperatorSpi spi = operatorSpiRegistry.getOperatorSpi(operatorDescriptor.getName());
            if (spi != null) {
                operatorSpiRegistry.removeOperatorSpi(spi);
            }
        }
    }
}
