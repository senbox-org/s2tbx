package org.esa.s2tbx.tooladapter.ui.utils;

import com.bc.ceres.binding.Property;
import com.bc.ceres.swing.binding.BindingContext;
import org.esa.beam.framework.gpf.descriptor.S2tbxOperatorDescriptor;
import org.esa.beam.framework.gpf.descriptor.S2tbxParameterDescriptor;
import org.esa.beam.framework.ui.UIUtils;
import org.esa.beam.framework.ui.tool.ToolButtonFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * Created by ramonag on 1/24/2015.
 */
public class PropertyUIDescriptor {

    private AbstractButton delButton;

    private HashMap<String, PropertyMemberUIWrapper> UIcomponentsMap;

    public static PropertyUIDescriptor buildUIDescriptor(S2tbxParameterDescriptor prop, String[] columnsMembers, S2tbxOperatorDescriptor context, ActionListener deleteActionListener,PropertyMemberUIWrapper.CallBackAfterEdit callback){
        PropertyUIDescriptor descriptor = new PropertyUIDescriptor();

        AbstractButton delButton = ToolButtonFactory.createButton(UIUtils.loadImageIcon("/org/esa/beam/resources/images/icons/DeleteShapeTool16.gif"),
                false);
        delButton.addActionListener(deleteActionListener);
        descriptor.setDelButton(delButton);

        HashMap<String, PropertyMemberUIWrapper> UIcomponentsMap = new HashMap<>();
        for(String col : columnsMembers){
            if(!col.equals("del")){
                UIcomponentsMap.put(col, PropertyMemberUIWrapperFactory.buildPropertyWrapper(col, prop, context, callback));
            }
        }
        descriptor.setUIcomponentsMap(UIcomponentsMap);

        return  descriptor;
    }

    public void setAttributeEditCallback(String attributeName, PropertyMemberUIWrapper.CallBackAfterEdit callback){
        UIcomponentsMap.get(attributeName).setCallback(callback);
    }

    public void setEditCallback(PropertyMemberUIWrapper.CallBackAfterEdit callback){
        while(UIcomponentsMap.keySet().iterator().hasNext()) {
            UIcomponentsMap.get(UIcomponentsMap.keySet().iterator().next()).setCallback(callback);
        }
    }

    public AbstractButton getDelButton() {
        return delButton;
    }

    public HashMap<String, PropertyMemberUIWrapper> getUIcomponentsMap() {
        return UIcomponentsMap;
    }

    public void setDelButton(AbstractButton delButton) {
        this.delButton = delButton;
    }

    public void setUIcomponentsMap(HashMap<String, PropertyMemberUIWrapper> UIcomponentsMap) {
        this.UIcomponentsMap = UIcomponentsMap;
    }
}
