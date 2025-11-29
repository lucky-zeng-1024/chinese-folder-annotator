package com.zeng.chineseannotator.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.zeng.chineseannotator.service.ChineseNameService;
import com.zeng.chineseannotator.ui.AnnotationDialog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Action to add Chinese annotation to a selected folder.
 */
public class AddAnnotationAction extends AnAction implements DumbAware {

    @NotNull
    @Override
    public ActionUpdateThread getActionUpdateThread() {
        // Do not block EDT during update
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        VirtualFile folder = getSelectedVirtualFolder(e);
        if (folder == null) return;

        PsiDirectory directory = ReadAction.compute(() -> PsiManager.getInstance(project).findDirectory(folder));
        if (directory == null) return;

        String folderPath = folder.getPath();
        String folderName = directory.getName();

        ChineseNameService service = ChineseNameService.getInstance(project);
        ChineseNameService.FolderAnnotation existingAnnotation = service.getAnnotation(folderPath);

        String existingChineseName = existingAnnotation != null ? existingAnnotation.chineseName : "";
        boolean hideOriginal = existingAnnotation != null ? existingAnnotation.hideOriginalName : service.defaultHideOriginalOnAdd;

        AnnotationDialog dialog = new AnnotationDialog(project, folderName, existingChineseName, hideOriginal);
        if (dialog.showAndGet()) {
            String chineseName = dialog.getChineseName();
            boolean hide = dialog.isHideOriginalName();

            if (chineseName.isEmpty()) {
                service.removeAnnotation(folderPath);
            } else {
                service.addAnnotation(folderPath, chineseName, hide);
            }

            // Refresh the project view
            com.intellij.ide.projectView.ProjectView.getInstance(project).refresh();
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        boolean enabled = false;
        if (project != null) {
            VirtualFile folder = getSelectedVirtualFolder(e);
            enabled = folder != null && folder.isDirectory();
        }
        // Always visible so users can locate it in Tools menu; enabled only when a directory is selected
        e.getPresentation().setVisible(true);
        e.getPresentation().setEnabled(enabled);
    }

    @Nullable
    private VirtualFile getSelectedVirtualFolder(@NotNull AnActionEvent e) {
        // Prefer array (Project View supplies arrays)
        VirtualFile[] array = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        if (array != null && array.length == 1 && array[0] != null && array[0].isDirectory()) {
            return array[0];
        }
        // Fallback to single VIRTUAL_FILE
        VirtualFile single = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (single != null && single.isDirectory()) {
            return single;
        }
        // Fallback to PSI element
        Object psi = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (psi instanceof PsiDirectory) {
            return ((PsiDirectory) psi).getVirtualFile();
        }
        return null;
    }
}

