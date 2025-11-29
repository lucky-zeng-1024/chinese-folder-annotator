package com.zeng.chineseannotator.provider;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.zeng.chineseannotator.service.ChineseNameService;

import org.jetbrains.annotations.NotNull;

/**
 * Decorates directory nodes in the Project View with Chinese annotations.
 * This is more reliable across IDE versions than mutating nodes in a TreeStructureProvider.
 */
public class ChineseNameProjectViewNodeDecorator implements com.intellij.ide.projectView.ProjectViewNodeDecorator {
    @Override
    public void decorate(@NotNull ProjectViewNode<?> node, @NotNull PresentationData data) {
        if (!(node instanceof PsiDirectoryNode)) return;
        PsiDirectoryNode dirNode = (PsiDirectoryNode) node;
        PsiDirectory directory = dirNode.getValue();
        if (directory == null) return;
        Project project = directory.getProject();
        ChineseNameService service = ChineseNameService.getInstance(project);

        String folderPath = directory.getVirtualFile().getPath();
        String originalName = directory.getName();
        String displayName = service.getDisplayName(folderPath, originalName);

        if (!displayName.equals(originalName)) {
            data.setPresentableText(displayName);
        }
    }
}

