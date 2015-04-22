package org.esa.snap.ui.tooladapter;

import org.esa.snap.framework.gpf.GPF;
import org.esa.snap.framework.gpf.OperatorSpi;
import org.esa.snap.framework.gpf.OperatorSpiRegistry;
import org.esa.snap.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.snap.framework.gpf.operators.tooladapter.ToolAdapterIO;
import org.esa.snap.framework.gpf.operators.tooladapter.ToolAdapterOpSpi;
import org.esa.snap.ui.tooladapter.utils.ToolAdapterActionRegistrar;
import org.openide.modules.ModuleInstall;

import java.util.Collection;

/**
 * Tool Adapter module installer class for NetBeans.
  * and menu entries.
 *
 * @author Cosmin Cara
 */
public class ModuleInstaller extends ModuleInstall {
    @Override
    public void restored() {
        OperatorSpiRegistry spiRegistry = GPF.getDefaultInstance().getOperatorSpiRegistry();
        if (spiRegistry != null) {
            Collection<OperatorSpi> operatorSpis = spiRegistry.getOperatorSpis();
            if (operatorSpis != null) {
                if (operatorSpis.size() == 0) {
                    operatorSpis.addAll(ToolAdapterIO.searchAndRegisterAdapters());
                }
                operatorSpis.stream().filter(spi -> spi instanceof ToolAdapterOpSpi).forEach(spi -> {
                    ToolAdapterOperatorDescriptor operatorDescriptor = (ToolAdapterOperatorDescriptor) spi.getOperatorDescriptor();
                    ToolAdapterActionRegistrar.registerOperatorMenu(operatorDescriptor);
                });
            }
        }
    }
}
