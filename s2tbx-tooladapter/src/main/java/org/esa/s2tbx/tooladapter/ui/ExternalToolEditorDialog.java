package org.esa.s2tbx.tooladapter.ui;

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertyContainer;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.PropertyPane;
import com.bc.ceres.swing.binding.internal.TextFieldEditor;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.descriptor.DefaultOperatorDescriptor;
import org.esa.beam.framework.gpf.descriptor.OperatorDescriptor;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.framework.ui.ModelessDialog;
import org.esa.s2tbx.tooladapter.ui.utils.OperatorDescriptorTableModel;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;

import static com.bc.ceres.swing.TableLayout.cell;

/**
 * Created by ramonag on 1/13/2015.
 */
public class ExternalToolEditorDialog extends ModelessDialog {

    private OperatorSpi operatorSpi;
    private boolean operatorIsNew;
    private BindingContext bindingContext;

    public ExternalToolEditorDialog(AppContext appContext, String title, String helpID){
        super(appContext.getApplicationWindow(), title, ID_CLOSE, helpID);
    }

    public ExternalToolEditorDialog(AppContext appContext, String title, String helpID, OperatorSpi operatorSpi, BindingContext bindingContext, boolean operatorIsNew){
        this(appContext, title, helpID);
        this.bindingContext = bindingContext;
        this.operatorSpi = operatorSpi;
        this.operatorIsNew = operatorIsNew;
        getJDialog().setSize(1000, 150);
        setContent(createPanel());
    }

    private JPanel createOperatorDescriptorPanel() {

        TableLayout descriptorLayout = new TableLayout(2);
        descriptorLayout.setTableAnchor(TableLayout.Anchor.NORTHWEST);
        descriptorLayout.setTableFill(TableLayout.Fill.HORIZONTAL);
        descriptorLayout.setTablePadding(3, 3);

        final JPanel descriptorPanel = new JPanel(descriptorLayout);

        descriptorPanel.add(new JLabel("Operator alias:"));
        //descriptorPanel.add(new JTextField(operatorSpi.getOperatorDescriptor().getAlias()));

        TextFieldEditor textEditor = new TextFieldEditor();
        PropertyContainer propertyContainer = PropertyContainer.createObjectBacked((DefaultOperatorDescriptor)operatorSpi.getOperatorDescriptor());
        BindingContext bindingContext = new BindingContext(propertyContainer);
        PropertyDescriptor propertyDescriptor = propertyContainer.getDescriptor("alias");
        JComponent editorComponent = textEditor.createEditorComponent(propertyDescriptor, bindingContext);
        descriptorPanel.add(editorComponent);

        descriptorPanel.add(new JLabel("Operator unique name:"));
        descriptorPanel.add(new JTextField(operatorSpi.getOperatorDescriptor().getName()));

        descriptorPanel.add(new JLabel("Operator class:"));
        descriptorPanel.add(new JTextField(operatorSpi.getOperatorDescriptor().getOperatorClass().getCanonicalName()));

        descriptorPanel.add(new JLabel("Operator version:"));
        descriptorPanel.add(new JTextField(operatorSpi.getOperatorDescriptor().getVersion()));

        descriptorPanel.add(new JLabel("Operator copyright:"));
        descriptorPanel.add(new JTextField(operatorSpi.getOperatorDescriptor().getCopyright()));

        descriptorPanel.add(new JLabel("Operator authors:"));
        descriptorPanel.add(new JTextField(operatorSpi.getOperatorDescriptor().getAuthors()));

        descriptorPanel.add(new JLabel("Operator description:"));
        descriptorPanel.add(new JTextField(operatorSpi.getOperatorDescriptor().getDescription()));

        TitledBorder title = BorderFactory.createTitledBorder("Operator Descriptor");
        descriptorPanel.setBorder(title);

        return descriptorPanel;
    }

    public JPanel createPanel() {
        JPanel toolDescriptorPanel = new JPanel();
        toolDescriptorPanel.setLayout(new BoxLayout(toolDescriptorPanel, BoxLayout.Y_AXIS));

        toolDescriptorPanel.add(createOperatorDescriptorPanel(), BorderLayout.WEST);

        OperatorDescriptorTableModel tableModel = new OperatorDescriptorTableModel(bindingContext);
        JScrollPane tableScrollPane = new JScrollPane(new JTable(tableModel));
        TitledBorder title = BorderFactory.createTitledBorder("Operator Parameters");
        tableScrollPane.setBorder(title);
        toolDescriptorPanel.add(tableScrollPane);
/*

        TableLayout layout = new TableLayout(3);
        layout.setTableAnchor(TableLayout.Anchor.NORTHWEST);
        layout.setTableFill(TableLayout.Fill.HORIZONTAL);
        layout.setTablePadding(3, 3);
        final JPanel paramsPanel = new JPanel(layout);

        int rowIndex = 0;
        final PropertyEditorRegistry registry = PropertyEditorRegistry.getInstance();



        for (Property property : properties) {
            PropertyDescriptor descriptor = property.getDescriptor();
            if (isInvisible(descriptor)) {
                continue;
            }
            PropertyEditor propertyEditor = registry.findPropertyEditor(descriptor);
            JComponent component = propertyEditor.createEditorComponent(descriptor, getBindingContext());
                layout.setCellColspan(rowIndex, 0, 2);
                layout.setCellWeightX(rowIndex, 0, 1.0);
                paramsPanel.add(component, cell(rowIndex, 0));
                final JLabel label = new JLabel("");
                if (descriptor.getUnit() != null) {
                    label.setText(descriptor.getUnit());
                }
                layout.setCellWeightX(rowIndex, 2, 0.0);
                paramsPanel.add(label, cell(rowIndex, 2));
            rowIndex++;
        }
        layout.setCellColspan(rowIndex, 0, 2);
        layout.setCellWeightX(rowIndex, 0, 1.0);
        layout.setCellWeightY(rowIndex, 0, 0.5);
        paramsPanel.add(new JPanel());

        TitledBorder title;
        title = BorderFactory.createTitledBorder("Operator Parameters");
        paramsPanel.setBorder(title);

        toolDescriptorPanel.add(paramsPanel);
*/
        return toolDescriptorPanel;
    }

    private boolean isInvisible(PropertyDescriptor descriptor) {
        return Boolean.FALSE.equals(descriptor.getAttribute("visible")) || descriptor.isDeprecated();
    }
}
