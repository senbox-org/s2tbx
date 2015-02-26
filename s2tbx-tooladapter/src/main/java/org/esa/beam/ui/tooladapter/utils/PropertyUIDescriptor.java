package org.esa.beam.ui.tooladapter.utils;

import org.esa.beam.framework.gpf.descriptor.ToolAdapterOperatorDescriptor;
import org.esa.beam.framework.gpf.descriptor.ToolParameterDescriptor;
import org.esa.beam.framework.ui.UIUtils;
import org.esa.beam.framework.ui.tool.ToolButtonFactory;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * @author Ramona Manda
 */
public class PropertyUIDescriptor {

    private AbstractButton delButton;
    private AbstractButton editButton;

    private HashMap<String, PropertyMemberUIWrapper> UIcomponentsMap;

    public static PropertyUIDescriptor buildUIMinimalDescriptor(ToolParameterDescriptor parameter, String property, ToolAdapterOperatorDescriptor operator, ActionListener deleteActionListener, ActionListener editActionListener, PropertyMemberUIWrapper.CallBackAfterEdit callback) {
        PropertyUIDescriptor descriptor = new PropertyUIDescriptor();

        AbstractButton delButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/DeleteShapeTool16.gif"),
                false);
        delButton.addActionListener(deleteActionListener);
        descriptor.setDelButton(delButton);
        AbstractButton editButton = new JButton("...");
        editButton.addActionListener(editActionListener);
        descriptor.setEditButton(editButton);

        HashMap<String, PropertyMemberUIWrapper> UIcomponentsMap = new HashMap<>();
        UIcomponentsMap.put(property, PropertyMemberUIWrapperFactory.buildPropertyWrapper(property, parameter, operator, callback));
        descriptor.setUIcomponentsMap(UIcomponentsMap);

        return descriptor;
    }

    public static PropertyUIDescriptor buildUIDescriptor(ToolParameterDescriptor prop, String[] columnsMembers, ToolAdapterOperatorDescriptor context, ActionListener deleteActionListener, ActionListener editActionListener, PropertyMemberUIWrapper.CallBackAfterEdit callback) {
        PropertyUIDescriptor descriptor = new PropertyUIDescriptor();

        AbstractButton delButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/DeleteShapeTool16.gif"),
                false);
        delButton.addActionListener(deleteActionListener);
        descriptor.setDelButton(delButton);
        AbstractButton editButton = new JButton("...");
        editButton.addActionListener(editActionListener);
        descriptor.setEditButton(editButton);

        HashMap<String, PropertyMemberUIWrapper> UIcomponentsMap = new HashMap<>();
        for (String col : columnsMembers) {
            if (!col.equals("del")) {
                UIcomponentsMap.put(col, PropertyMemberUIWrapperFactory.buildPropertyWrapper(col, prop, context, callback));
            }
        }
        descriptor.setUIcomponentsMap(UIcomponentsMap);

        return descriptor;
    }

    public void fillDescriptor(String[] columnsMembers) {
        for (String col : columnsMembers) {
            if (!col.equals("del")) {
                //UIcomponentsMap.put(col, PropertyMemberUIWrapperFactory.buildPropertyWrapper(col, prop, context, callback));
            }
        }
    }

    public void setAttributeEditCallback(String attributeName, PropertyMemberUIWrapper.CallBackAfterEdit callback) {
        UIcomponentsMap.get(attributeName).setCallback(callback);
    }

    public void setEditCallback(PropertyMemberUIWrapper.CallBackAfterEdit callback) {
        while (UIcomponentsMap.keySet().iterator().hasNext()) {
            UIcomponentsMap.get(UIcomponentsMap.keySet().iterator().next()).setCallback(callback);
        }
    }

    public AbstractButton getDelButton() {
        return delButton;
    }

    public AbstractButton getEditButton() {
        return editButton;
    }

    public HashMap<String, PropertyMemberUIWrapper> getUIcomponentsMap() {
        return UIcomponentsMap;
    }

    public void setDelButton(AbstractButton delButton) {
        this.delButton = delButton;
    }

    public void setEditButton(AbstractButton editButton) {
        this.editButton = editButton;
    }

    public void setUIcomponentsMap(HashMap<String, PropertyMemberUIWrapper> UIcomponentsMap) {
        this.UIcomponentsMap = UIcomponentsMap;
    }
}
