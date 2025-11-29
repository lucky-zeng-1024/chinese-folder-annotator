package com.zeng.chineseannotator.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.zeng.chineseannotator.service.ChineseNameService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Settings panel for managing Chinese name annotations.
 */
public class ChineseNameSettingsPanel {

    private JPanel mainPanel;
    private JTable annotationsTable;
    private AnnotationsTableModel tableModel;
    private JCheckBox holdPreviewCheckBox;
    private JCheckBox defaultHideOnAddCheckBox;
    private final Project project;
    private final ChineseNameService service;

    public ChineseNameSettingsPanel(Project project) {
        this.project = project;
        this.service = ChineseNameService.getInstance(project);
        initializeUI();
    }

    private void initializeUI() {
        mainPanel = new JPanel(new BorderLayout());

        // Title + Options (north panel)
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

        JBLabel titleLabel = new JBLabel("Folder Annotations");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        northPanel.add(titleLabel);

        holdPreviewCheckBox = new JCheckBox("Enable toggle to show original names (Tools → Toggle Show Original Names)");
        holdPreviewCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        northPanel.add(Box.createVerticalStrut(6));
        northPanel.add(holdPreviewCheckBox);

        defaultHideOnAddCheckBox = new JCheckBox("Default: hide original on add (show only Chinese name)");
        defaultHideOnAddCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        northPanel.add(Box.createVerticalStrut(6));
        northPanel.add(defaultHideOnAddCheckBox);

        mainPanel.add(northPanel, BorderLayout.NORTH);

        // Table
        tableModel = new AnnotationsTableModel();
        annotationsTable = new JTable(tableModel);
        annotationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        annotationsTable.setRowHeight(25);

        JBScrollPane scrollPane = new JBScrollPane(annotationsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            int selectedRow = annotationsTable.getSelectedRow();
            if (selectedRow >= 0) {
                tableModel.removeRow(selectedRow);
                annotationsTable.repaint();
            }
        });
        buttonsPanel.add(deleteButton);

        JButton clearAllButton = new JButton("Clear All");
        clearAllButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(mainPanel,
                    "Are you sure you want to clear all annotations?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                tableModel.clear();
                annotationsTable.repaint();
            }
        });
        buttonsPanel.add(clearAllButton);

        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        reset();
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public boolean isModified() {
        boolean tableChanged = tableModel.isModified();
        boolean holdChanged = holdPreviewCheckBox != null && holdPreviewCheckBox.isSelected() != service.holdToShowOriginalEnabled;
        boolean defaultHideChanged = defaultHideOnAddCheckBox != null && defaultHideOnAddCheckBox.isSelected() != service.defaultHideOriginalOnAdd;
        return tableChanged || holdChanged || defaultHideChanged;
    }

    public void apply() {
        // Persist toggles
        if (holdPreviewCheckBox != null) {
            service.holdToShowOriginalEnabled = holdPreviewCheckBox.isSelected();
        }
        if (defaultHideOnAddCheckBox != null) {
            service.defaultHideOriginalOnAdd = defaultHideOnAddCheckBox.isSelected();
        }
        // Persist annotations from table (unchanged rows will be re-added as-is)
        service.annotations.clear();
        for (AnnotationRow row : tableModel.getRows()) {
            service.addAnnotation(row.folderPath, row.chineseName, row.hideOriginal);
        }
    }

    public void reset() {
        if (holdPreviewCheckBox != null) {
            holdPreviewCheckBox.setSelected(service.holdToShowOriginalEnabled);
        }
        if (defaultHideOnAddCheckBox != null) {
            defaultHideOnAddCheckBox.setSelected(service.defaultHideOriginalOnAdd);
        }
        tableModel.setRows(service.annotations);
    }

    /**
     * Table model for displaying annotations.
     */
    private static class AnnotationsTableModel extends AbstractTableModel {

        private final List<AnnotationRow> rows = new ArrayList<>();
        private boolean modified = false;

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int column) {
            return switch (column) {
                case 0 -> "Folder Path";
                case 1 -> "Chinese Name";
                case 2 -> "Hide Original";
                default -> "";
            };
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            AnnotationRow row = rows.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> row.folderPath;
                case 1 -> row.chineseName;
                case 2 -> row.hideOriginal;
                default -> "";
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 2 ? Boolean.class : String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 2; // Only hide original column is editable
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 2) {
                rows.get(rowIndex).hideOriginal = (Boolean) aValue;
                modified = true;
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }

        public void setRows(Map<String, ChineseNameService.FolderAnnotation> annotations) {
            rows.clear();
            for (Map.Entry<String, ChineseNameService.FolderAnnotation> entry : annotations.entrySet()) {
                rows.add(new AnnotationRow(entry.getKey(), entry.getValue().chineseName, entry.getValue().hideOriginalName));
            }
            modified = false;
            fireTableDataChanged();
        }

        public List<AnnotationRow> getRows() {
            return rows;
        }

        public void removeRow(int rowIndex) {
            rows.remove(rowIndex);
            modified = true;
            fireTableRowsDeleted(rowIndex, rowIndex);
        }

        public void clear() {
            rows.clear();
            modified = true;
            fireTableDataChanged();
        }

        public boolean isModified() {
            return modified;
        }
    }

    /**
     * Data class for annotation row.
     */
    private static class AnnotationRow {
        String folderPath;
        String chineseName;
        boolean hideOriginal;

        AnnotationRow(String folderPath, String chineseName, boolean hideOriginal) {
            this.folderPath = folderPath;
            this.chineseName = chineseName;
            this.hideOriginal = hideOriginal;
        }
    }
}

