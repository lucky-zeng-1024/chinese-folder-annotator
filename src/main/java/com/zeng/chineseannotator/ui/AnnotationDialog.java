package com.zeng.chineseannotator.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Dialog for adding or editing Chinese annotation for a folder.
 */
public class AnnotationDialog extends DialogWrapper {

    private JTextField chineseNameField;
    private JCheckBox hideOriginalNameCheckBox;
    private final String folderName;

    public AnnotationDialog(Project project, String folderName, String existingChineseName, boolean hideOriginal) {
        super(project, true);
        this.folderName = folderName;

        setTitle("Add Chinese Annotation");
        setOKButtonText("OK");
        setCancelButtonText("Cancel");

        chineseNameField = new JTextField(20);
        hideOriginalNameCheckBox = new JCheckBox("Hide original folder name (show only Chinese name)");

        chineseNameField.setText(existingChineseName);
        hideOriginalNameCheckBox.setSelected(hideOriginal);

        init();
    }

    @Override
    protected Action[] createActions() {
        Action clearAction = new AbstractAction("clear") {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // Just clear the field and keep dialog open; user can press OK to save removal
                chineseNameField.setText("");
            }
        };
        return new Action[]{getOKAction(), clearAction, getCancelAction()};
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Folder name label
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Folder Name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JLabel folderNameLabel = new JLabel(folderName);
        folderNameLabel.setFont(folderNameLabel.getFont().deriveFont(Font.BOLD));
        panel.add(folderNameLabel, gbc);

        // Chinese name input
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Chinese Name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(chineseNameField, gbc);

        // Hide original name checkbox
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(hideOriginalNameCheckBox, gbc);

        // Preview
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Preview:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JLabel previewLabel = new JLabel();
        previewLabel.setFont(previewLabel.getFont().deriveFont(Font.ITALIC));
        previewLabel.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(previewLabel, gbc);

        // Update preview when text changes
        chineseNameField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updatePreview(previewLabel);
            }

            public void removeUpdate(DocumentEvent e) {
                updatePreview(previewLabel);
            }

            public void changedUpdate(DocumentEvent e) {
                updatePreview(previewLabel);
            }
        });

        hideOriginalNameCheckBox.addActionListener(e -> updatePreview(previewLabel));

        updatePreview(previewLabel);

        return panel;
    }

    private void updatePreview(JLabel previewLabel) {
        String chineseName = chineseNameField.getText();
        String preview;

        if (chineseName.isEmpty()) {
            preview = folderName;
        } else if (hideOriginalNameCheckBox.isSelected()) {
            preview = chineseName;
        } else {
            preview = folderName + "(" + chineseName + ")";
        }

        previewLabel.setText(preview);
    }

    public String getChineseName() {
        return chineseNameField.getText().trim();
    }

    public boolean isHideOriginalName() {
        return hideOriginalNameCheckBox.isSelected();
    }
}
