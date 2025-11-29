package com.zeng.chineseannotator.provider;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.zeng.chineseannotator.service.ChineseNameService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Tree structure provider that modifies the display names of folders
 * to include Chinese annotations.
 */
public class ChineseNameTreeStructureProvider implements TreeStructureProvider {

    @NotNull
    @Override
    public Collection<AbstractTreeNode<?>> modify(@NotNull AbstractTreeNode<?> parent,
                                                   @NotNull Collection<AbstractTreeNode<?>> children,
                                                   ViewSettings settings) {
        Collection<AbstractTreeNode<?>> result = new ArrayList<>(children);

        Project project = parent.getProject();
        if (project == null) {
            return result;
        }

        ChineseNameService service = ChineseNameService.getInstance(project);

        for (AbstractTreeNode<?> child : result) {
            if (child instanceof PsiDirectoryNode) {
                PsiDirectoryNode dirNode = (PsiDirectoryNode) child;
                PsiDirectory directory = dirNode.getValue();

                if (directory != null) {
                    String folderPath = directory.getVirtualFile().getPath();
                    String originalName = directory.getName();

                    String displayName = service.getDisplayName(folderPath, originalName);

                    if (!displayName.equals(originalName)) {
                        // Update the presentation
                        PresentationData presentation = dirNode.getPresentation();
                        if (presentation != null) {
                            presentation.setPresentableText(displayName);
                        }
                    }
                }
            }
        }

        return result;
    }

}

