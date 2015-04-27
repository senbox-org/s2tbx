package org.esa.snap.ui.tooladapter.actions;

import org.esa.snap.framework.gpf.GPF;
import org.esa.snap.framework.gpf.OperatorSpi;
import org.esa.snap.framework.gpf.OperatorSpiRegistry;
import org.esa.snap.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.snap.framework.gpf.operators.tooladapter.ToolAdapterIO;
import org.esa.snap.framework.gpf.operators.tooladapter.ToolAdapterOpSpi;
import org.esa.snap.rcp.SnapDialogs;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.OnStart;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for creating menu entries for tool adapter operators.
 * The inner runnable class should be invoked when the IDE starts, and will
 * register the available adapters as menu actions.
 *
 * @author Cosmin Cara
 */

public class ToolAdapterActionRegistrar {

    private static final String MENU_PATH = "Menu/Tools";
    private static final String MENU_TEXT = "External tools";

    private static final Map<String, ToolAdapterOperatorDescriptor> actionMap = new HashMap<>();

    /**
     * Returns the map of menu items (actions) and operator descriptors.
     *
     * @return
     */
    public static Map<String, ToolAdapterOperatorDescriptor> getActionMap() {
        return actionMap;
    }

    /**
     * Creates a menu entry in the default menu location (Tools > External Tools) for the given adapter operator.
     *
     * @param operator  The operator descriptor
     */
    public static void registerOperatorMenu(ToolAdapterOperatorDescriptor operator) {
        registerOperatorMenu(operator, MENU_TEXT, MENU_PATH, true);
    }

    /**
     * Creates a menu entry in the given menu location for the given adapter operator.
     *
     * @param operator  The operator descriptor
     * @param groupName The menu group name
     * @param menu      The parent menu
     */
    public static void registerOperatorMenu(ToolAdapterOperatorDescriptor operator, String groupName, String menu, boolean hasChanged) {
        FileObject menuFolder = FileUtil.getConfigFile(menu);
        try {
            FileObject groupItem = menuFolder.getFileObject(groupName);
            if (groupItem == null) {
                groupItem = menuFolder.createFolder(groupName);
                groupItem.setAttribute("position", 1001);
            }

            String candidateMenuKey = operator.getAlias();
            FileObject newItem = groupItem.getFileObject(candidateMenuKey, "instance");
            if (newItem == null) {
                newItem = groupItem.createData(candidateMenuKey, "instance");
            }
            ExecuteToolAdapterAction action = new ExecuteToolAdapterAction(candidateMenuKey);
            newItem.setAttribute("instanceCreate", action);
            newItem.setAttribute("instanceClass", action.getClass().getName());
            if (actionMap.containsKey(candidateMenuKey)) {
                actionMap.remove(candidateMenuKey);
            }
            actionMap.put(candidateMenuKey, operator);
        } catch (IOException e) {
            SnapDialogs.showError("Error:" + e.getMessage());
        }
    }

    public static void removeOperatorMenu(ToolAdapterOperatorDescriptor operator) {
        removeOperatorMenu(operator, MENU_TEXT, MENU_PATH);
    }

    public static void removeOperatorMenu(ToolAdapterOperatorDescriptor operator, String groupName, String menu) {
        if (!operator.isSystem()) {
            FileObject menuFolder = FileUtil.getConfigFile(menu);
            try {
                FileObject groupItem = menuFolder.getFileObject(groupName);
                if (groupItem != null) {
                    String operatorAlias = operator.getAlias();
                    FileObject newItem = groupItem.getFileObject(operatorAlias, "instance");
                    if (newItem != null) {
                        newItem.delete();
                    }
                    if (actionMap.containsKey(operatorAlias)) {
                        actionMap.remove(operatorAlias);
                    }
                }
            } catch (IOException e) {
                SnapDialogs.showError("Error:" + e.getMessage());
            }
        }
    }

    /**
     * Startup class that performs menu initialization to be invoked by NetBeans.
     */
    @OnStart
    public static class StartOp implements Runnable {
        @Override
        public void run() {
            OperatorSpiRegistry spiRegistry = GPF.getDefaultInstance().getOperatorSpiRegistry();
            if (spiRegistry != null) {
                Collection<OperatorSpi> operatorSpis = spiRegistry.getOperatorSpis();
                if (operatorSpis != null) {
                    if (operatorSpis.size() == 0) {
                        operatorSpis.addAll(ToolAdapterIO.searchAndRegisterAdapters());
                    }
                    operatorSpis.stream().filter(spi -> spi instanceof ToolAdapterOpSpi).forEach(spi -> {
                        ToolAdapterOperatorDescriptor operatorDescriptor = (ToolAdapterOperatorDescriptor) spi.getOperatorDescriptor();
                        registerOperatorMenu(operatorDescriptor, MENU_TEXT, MENU_PATH, false);
                    });
                }
            }
        }
    }
}
