package com.zeng.chineseannotator.action;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.zeng.chineseannotator.service.ChineseNameService;

import org.jetbrains.annotations.NotNull;

/**
 * Toggle action: press once to show original names, press again to restore annotations.
 */
public class HoldShowOriginalAction extends AnAction implements DumbAware {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;
        ChineseNameService service = ChineseNameService.getInstance(project);
        if (!service.holdToShowOriginalEnabled) return;

        // Flip preview flag
        service.previewOriginalActive = !service.previewOriginalActive;
        ProjectView.getInstance(project).refresh();
    }
}

