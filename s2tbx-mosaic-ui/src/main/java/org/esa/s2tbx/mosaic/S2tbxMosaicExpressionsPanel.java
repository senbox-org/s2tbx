/*
 * Copyright (C) 2011 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.mosaic;

import com.bc.ceres.swing.TableLayout;
import com.bc.ceres.swing.binding.BindingContext;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.jexp.impl.Tokenizer;
import org.esa.snap.core.util.ArrayUtils;
import org.esa.snap.core.util.MouseEventFilterFactory;
import org.esa.snap.core.util.StringUtils;
import org.esa.snap.ui.AppContext;
import org.esa.snap.ui.ModalDialog;
import org.esa.snap.ui.UIUtils;
import org.esa.snap.ui.product.BandChooser;
import org.esa.snap.ui.product.ProductExpressionPane;
import org.esa.snap.ui.tool.ToolButtonFactory;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.AbstractButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */

class S2tbxMosaicExpressionsPanel extends JPanel {

    private static final int PREFERRED_TABLE_WIDTH = 520;


    private final AppContext appContext;
    private final BindingContext bindingCtx;

    private JTable variablesTable;
    private S2tbxMosaicFormModel mosaicModel;

    S2tbxMosaicExpressionsPanel(AppContext appContext, S2tbxMosaicFormModel model) {
        this.appContext = appContext;
        mosaicModel = model;
        this.bindingCtx = new BindingContext(model.getPropertySet());
        init();
    }

    private void init() {
        final TableLayout tableLayout = new TableLayout(1);
        tableLayout.setTableAnchor(TableLayout.Anchor.WEST);
        tableLayout.setTableFill(TableLayout.Fill.BOTH);
        tableLayout.setTableWeightX(1.0);
        tableLayout.setTableWeightY(1.0);
        tableLayout.setTablePadding(3, 3);
        setLayout(tableLayout);

        add(createVariablesPanel());
    }

    private Component createVariablesPanel() {
        final String labelName = "Variables";  /*I18N*/

        final TableLayout layout = new TableLayout(1);
        layout.setTableAnchor(TableLayout.Anchor.WEST);
        layout.setTableFill(TableLayout.Fill.BOTH);
        layout.setTablePadding(3, 3);
        layout.setTableWeightX(1.0);
        layout.setTableWeightY(1.0);
        layout.setRowWeightY(0, 0.0);
        final JPanel panel = new JPanel(layout);
        panel.setBorder(BorderFactory.createTitledBorder(labelName));
        panel.setName(labelName);

        panel.add(createVariablesButtonPanel(labelName));
        panel.add(createVariablesTable(labelName));

        return panel;
    }



    private JPanel createVariablesButtonPanel(String labelName) {
        final JPanel variableButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        variableButtonsPanel.setName(labelName);

        final Component bandFilterButton = createBandFilterButton();
        bandFilterButton.setName(labelName + "_bandFilter");
        variableButtonsPanel.add(bandFilterButton);

        final Component removeVariableButton = createRemoveVariableButton();
        removeVariableButton.setName(labelName + "_removeVariable");
        variableButtonsPanel.add(removeVariableButton);

        final Component moveVariableUpButton = createMoveVariableUpButton();
        moveVariableUpButton.setName(labelName + "moveVariableUp");
        variableButtonsPanel.add(moveVariableUpButton);

        final Component moveVariableDownButton = createMoveVariableDownButton();
        moveVariableDownButton.setName(labelName + "moveVariableDown");
        variableButtonsPanel.add(moveVariableDownButton);

        return variableButtonsPanel;
    }


    private Component createBandFilterButton() {
        AbstractButton variableFilterButton = createButton("icons/Copy16.gif", "bandButton");
        variableFilterButton.setToolTipText("Choose the bands to process"); /*I18N*/
        variableFilterButton.addActionListener(
                (ActionEvent e)-> {
                    Product product;
                    try {
                        product = mosaicModel.getReferenceProduct();
                    } catch (IOException ioe) {
                        appContext.handleError(ioe.getMessage(), ioe);
                        return;
                    }
                    if (product != null) {
                        final String[] availableBandNames = product.getBandNames();
                        final Band[] allBands = product.getBands();
                        final List dataVector = ((DefaultTableModel) variablesTable.getModel()).getDataVector();
                        final List<Band> existingBands = new ArrayList<>(dataVector.size());
                        for (Object aDataVector : dataVector) {
                            List row = (List) aDataVector;
                            final String name = (String) row.get(0);
                            final String expression = (String) row.get(1);
                            if (name == null || expression == null
                                    || !StringUtils.contains(availableBandNames, name.trim())
                                    || !name.trim().equals(expression.trim())) {
                                continue;
                            }
                            existingBands.add(product.getBand(name.trim()));
                        }
                        final BandChooser bandChooser = new BandChooser(appContext.getApplicationWindow(), "Band Chooser",
                                null,
                                allBands, /*I18N*/
                                existingBands.toArray(
                                        new Band[existingBands.size()]), true
                        );
                        if (bandChooser.show() == ModalDialog.ID_OK) {
                            final Band[] selectedBands = bandChooser.getSelectedBands();
                            for (Band selectedBand : selectedBands) {
                                if (!existingBands.contains(selectedBand)) {
                                    final String name = selectedBand.getName();
                                    final String expression = Tokenizer.createExternalName(name);
                                    addRow(variablesTable, new Object[]{name, expression});
                                } else {
                                    existingBands.remove(selectedBand);
                                }
                            }
                            final int[] rowsToRemove = new int[0];
                            final List newDataVector = ((DefaultTableModel) variablesTable.getModel()).getDataVector();
                            for (Band existingBand : existingBands) {
                                String bandName = existingBand.getName();
                                final int rowIndex = getBandRow(newDataVector, bandName);
                                if (rowIndex > -1) {
                                    ArrayUtils.addToArray(rowsToRemove, rowIndex);
                                }
                            }
                            removeRows(variablesTable, rowsToRemove);
                        }
                    }
                });
        return variableFilterButton;
    }

    private static int getBandRow(List newDataVector, String bandName) {
        for (int i = 0; i < newDataVector.size(); i++) {
            List row = (List) newDataVector.get(i);
            if (bandName.equals(row.get(0)) && bandName.equals(row.get(1))) {
                return i;
            }
        }
        return -1;
    }


    private Component createRemoveVariableButton() {
        AbstractButton removeVariableButton = createButton("icons/Minus24.gif", "removeVariable");
        removeVariableButton.setToolTipText("Remove selected rows."); /*I18N*/
        removeVariableButton.addActionListener(
                (ActionEvent e)-> removeRows(variablesTable, variablesTable.getSelectedRows()));
        return removeVariableButton;
    }

    private Component createMoveVariableUpButton() {
        AbstractButton moveVariableUpButton = createButton("icons/MoveUp24.gif", "moveVariableUp");
        moveVariableUpButton.setToolTipText("Move up selected rows."); /*I18N*/
        moveVariableUpButton.addActionListener(
                (ActionEvent e)-> moveRowsUp(variablesTable, variablesTable.getSelectedRows()));
        return moveVariableUpButton;
    }

    private Component createMoveVariableDownButton() {
        AbstractButton moveVariableDownButton = createButton("icons/MoveDown24.gif", "moveVariableDown");
        moveVariableDownButton.setToolTipText("Move down selected rows."); /*I18N*/
        moveVariableDownButton.addActionListener(
                (ActionEvent e)->moveRowsDown(variablesTable, variablesTable.getSelectedRows()));
        return moveVariableDownButton;
    }

    private JScrollPane createVariablesTable(final String labelName) {
        variablesTable = new JTable();
        variablesTable.setName(labelName);
        variablesTable.setRowSelectionAllowed(true);
        bindingCtx.bind("variables", new S2tbxVariablesTableAdapter(variablesTable));
        bindingCtx.bindEnabledState("variables", false, "updateMode", true);
        variablesTable.addMouseListener(createExpressionEditorMouseListener(variablesTable, false));

        final JTableHeader tableHeader = variablesTable.getTableHeader();
        tableHeader.setName(labelName);
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(true);

        final TableColumnModel columnModel = variablesTable.getColumnModel();
        columnModel.setColumnSelectionAllowed(false);

        final TableColumn nameColumn = columnModel.getColumn(0);
        nameColumn.setPreferredWidth(100);
        nameColumn.setCellRenderer(new TCR());

        final TableColumn expressionColumn = columnModel.getColumn(1);
        expressionColumn.setPreferredWidth(400);
        expressionColumn.setCellRenderer(new TCR());
        final JScrollPane scrollPane = new JScrollPane(variablesTable);
        scrollPane.setName(labelName);
        scrollPane.setPreferredSize(new Dimension(PREFERRED_TABLE_WIDTH, 150));

        return scrollPane;
    }

    private static AbstractButton createButton(final String path, String name) {
        final AbstractButton button = ToolButtonFactory.createButton(UIUtils.loadImageIcon(path), false);
        button.setName(name);
        return button;
    }

    private MouseListener createExpressionEditorMouseListener(final JTable table, final boolean booleanExpected) {
        final MouseAdapter mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    final int column = table.getSelectedColumn();
                    if (column == 1) {
                        table.removeEditor();
                        final int row = table.getSelectedRow();
                        final String[] value = new String[]{(String) table.getValueAt(row, column)};
                        final int i = editExpression(value, booleanExpected);
                        if (ModalDialog.ID_OK == i) {
                            table.setValueAt(value[0], row, column);
                        }
                    }
                }
            }
        };
        return MouseEventFilterFactory.createFilter(mouseListener);
    }

    private int editExpression(String[] value, final boolean booleanExpected) {
        Product product;
        try {
            product = mosaicModel.getReferenceProduct();
        } catch (IOException ioe) {
            appContext.handleError(ioe.getMessage(), ioe);
            return 0;
        }
        if (product == null) {
            final String msg = "No source product specified.";
            appContext.handleError(msg, new IllegalStateException(msg));
            return 0;
        }
        final ProductExpressionPane pep;
        if (booleanExpected) {
            pep = ProductExpressionPane.createBooleanExpressionPane(new Product[]{product}, product,
                    appContext.getPreferences());
        } else {
            pep = ProductExpressionPane.createGeneralExpressionPane(new Product[]{product}, product,
                    appContext.getPreferences());
        }
        pep.setCode(value[0]);
        final int i = pep.showModalDialog(appContext.getApplicationWindow(), value[0]);
        if (i == ModalDialog.ID_OK) {
            value[0] = pep.getCode();
        }
        return i;
    }

    private static void addRow(final JTable table, final Object[] rowData) {
        table.removeEditor();
        ((DefaultTableModel) table.getModel()).addRow(rowData);
        final int row = table.getRowCount() - 1;
        final int numCols = table.getColumnModel().getColumnCount();
        for (int i = 0; i < Math.min(numCols, rowData.length); i++) {
            Object o = rowData[i];
            table.setValueAt(o, row, i);
        }
        selectRows(table, row, row);
    }

    private static void moveRowsDown(final JTable table, final int[] rows) {
        final int maxRow = table.getRowCount() - 1;
        for (int row1 : rows) {
            if (row1 == maxRow) {
                return;
            }
        }
        table.removeEditor();
        int[] selectedRows = rows.clone();
        for (int i = rows.length - 1; i > -1; i--) {
            int row = rows[i];
            ((DefaultTableModel) table.getModel()).moveRow(row, row, row + 1);
            selectedRows[i] = row + 1;
        }
        selectRows(table, selectedRows);
    }

    private static void moveRowsUp(final JTable table, final int[] rows) {
        for (int row1 : rows) {
            if (row1 == 0) {
                return;
            }
        }
        table.removeEditor();
        int[] selectedRows = rows.clone();
        for (int i = 0; i < rows.length; i++) {
            int row = rows[i];
            ((DefaultTableModel) table.getModel()).moveRow(row, row, row - 1);
            selectedRows[i] = row - 1;
        }
        selectRows(table, selectedRows);
    }

    private static void removeRows(final JTable table, final int[] rows) {
        table.removeEditor();
        for (int i = rows.length - 1; i > -1; i--) {
            int row = rows[i];
            ((DefaultTableModel) table.getModel()).removeRow(row);
        }
    }

    private static void selectRows(final JTable table, final int[] rows) {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.clearSelection();
        for (int row : rows) {
            selectionModel.addSelectionInterval(row, row);
        }
    }

    private static void selectRows(JTable table, int min, int max) {
        final int numRows = max + 1 - min;
        if (numRows <= 0) {
            return;
        }
        selectRows(table, prepareRows(numRows, min));
    }

    private static int[] prepareRows(final int numRows, int min) {
        final int[] rows = new int[numRows];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = min + i;
        }
        return rows;
    }

    private static class TCR extends JLabel implements TableCellRenderer {

        private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

        /**
         * Creates a <code>JLabel</code> instance with no image and with an empty string for the title. The label is
         * centered vertically in its display area. The label's contents, once set, will be displayed on the leading
         * edge of the label's display area.
         */
        private TCR() {
            setOpaque(true);
            setBorder(noFocusBorder);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            final boolean enabled = table.isEnabled();
            setText((String) value);

            if (isSelected) {
                super.setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            } else if (!enabled) {
                super.setForeground(UIManager.getColor("TextField.inactiveForeground"));
                super.setBackground(table.getBackground());
            } else {
                super.setForeground(table.getForeground());
                super.setBackground(table.getBackground());
            }

            setFont(table.getFont());

            if (hasFocus) {
                setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
                if (table.isCellEditable(row, column)) {
                    super.setForeground(UIManager.getColor("Table.focusCellForeground"));
                    super.setBackground(UIManager.getColor("Table.focusCellBackground"));
                }
            } else {
                setBorder(noFocusBorder);
            }

            setValue(value);

            return this;
        }

        private void setValue(Object value) {
            setText(value == null ? "" : value.toString());
        }
    }
}
