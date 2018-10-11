package org.esa.s2tbx.fcc;

import com.bc.ceres.binding.Property;
import com.bc.ceres.binding.PropertyDescriptor;
import com.bc.ceres.binding.PropertySet;
import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.binding.BindingContext;
import com.bc.ceres.swing.binding.PropertyEditor;
import com.bc.ceres.swing.binding.PropertyEditorRegistry;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.util.List;

import static com.bc.ceres.swing.TableLayout.cell;

/**
 * Created by jcoravu on 9/11/2017.
 */
public class ParametersPanel extends JPanel {

    public ParametersPanel() {
        this(false);
    }

    public ParametersPanel(boolean displayUnitColumn) {
        TableLayout layout = new TableLayout(displayUnitColumn ? 3 : 2);
        layout.setTableAnchor(TableLayout.Anchor.WEST);
        layout.setTableFill(TableLayout.Fill.HORIZONTAL);
        layout.setTablePadding(3, 3);
        setLayout(layout);
    }

    public void populate(PropertySet propertyContainer) {
        TableLayout layout = (TableLayout)getLayout();

        Property[] properties = propertyContainer.getProperties();

        boolean displayUnitColumn = wantDisplayUnitColumn(properties);

        BindingContext bindingContext = new BindingContext(propertyContainer);

        int rowIndex = 0;
        final PropertyEditorRegistry registry = PropertyEditorRegistry.getInstance();
        for (Property property : properties) {
            PropertyDescriptor descriptor = property.getDescriptor();
            if (isInvisible(descriptor)) {
                continue;
            }
            PropertyEditor propertyEditor = registry.findPropertyEditor(descriptor);
            JComponent[] components = propertyEditor.createComponents(descriptor, bindingContext);
            if (components.length == 2) {
                layout.setCellWeightX(rowIndex, 0, 0.0);
                add(components[1], cell(rowIndex, 0));
                layout.setCellWeightX(rowIndex, 1, 1.0);
                if(components[0] instanceof JScrollPane) {
                    layout.setRowWeightY(rowIndex, 1.0);
                    layout.setRowFill(rowIndex, TableLayout.Fill.BOTH);
                }
                add(components[0], cell(rowIndex, 1));
            } else {
                layout.setCellColspan(rowIndex, 0, 2);
                layout.setCellWeightX(rowIndex, 0, 1.0);
                add(components[0], cell(rowIndex, 0));
            }
            if (displayUnitColumn) {
                final JLabel label = new JLabel("");
                if (descriptor.getUnit() != null) {
                    label.setText(descriptor.getUnit());
                }
                layout.setCellWeightX(rowIndex, 2, 0.0);
                add(label, cell(rowIndex, 2));
            }
            rowIndex++;
        }

        layout.setCellColspan(rowIndex, 0, 2);
        layout.setCellWeightX(rowIndex, 0, 1.0);
        layout.setCellWeightY(rowIndex, 0, 0.5);
    }

    public void populate(List<JComponent[]> componentsList) {
        TableLayout layout = (TableLayout)getLayout();

        int rowIndex = 0;
        for (JComponent[] components : componentsList) {
            if (components.length == 2) {
                layout.setCellWeightX(rowIndex, 0, 0.0);
                add(components[1], cell(rowIndex, 0));
                layout.setCellWeightX(rowIndex, 1, 1.0);
                if(components[0] instanceof JScrollPane) {
                    layout.setRowWeightY(rowIndex, 1.0);
                    layout.setRowFill(rowIndex, TableLayout.Fill.BOTH);
                }
                add(components[0], cell(rowIndex, 1));
            } else {
                layout.setCellColspan(rowIndex, 0, 2);
                layout.setCellWeightX(rowIndex, 0, 1.0);
                add(components[0], cell(rowIndex, 0));
            }
            rowIndex++;
        }

        layout.setCellColspan(rowIndex, 0, 2);
        layout.setCellWeightX(rowIndex, 0, 1.0);
        layout.setCellWeightY(rowIndex, 0, 0.5);
    }

    public static boolean wantDisplayUnitColumn(Property[] models) {
        boolean showUnitColumn = false;
        for (Property model : models) {
            PropertyDescriptor descriptor = model.getDescriptor();
            if (isInvisible(descriptor)) {
                continue;
            }
            String unit = descriptor.getUnit();
            if (!(unit == null || unit.length() == 0)) {
                showUnitColumn = true;
                break;
            }
        }
        return showUnitColumn;
    }

    private static boolean isInvisible(PropertyDescriptor descriptor) {
        return Boolean.FALSE.equals(descriptor.getAttribute("visible")) || descriptor.isDeprecated();
    }
}
