package com.zeng.chineseannotator.service;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Service to manage Chinese name annotations for folders.
 * Stores configuration persistently in project settings.
 */
@State(
        name = "ChineseNameService",
        storages = @Storage("chineseName.xml")
)
public class ChineseNameService implements PersistentStateComponent<ChineseNameService> {

    /**
     * Whether press-and-hold shortcut temporarily shows original names.
     * Persisted as part of project settings.
     */
    public boolean holdToShowOriginalEnabled = true;

    /**
     * Default behavior when adding new annotation: hide original folder name by default.
     */
    public boolean defaultHideOriginalOnAdd = false;

    /**
     * Transient runtime flag toggled while user holds the shortcut.
     */
    public transient volatile boolean previewOriginalActive = false;

    /**
     * Map of folder paths to their Chinese annotations.
     * Key: absolute folder path
     * Value: Chinese annotation
     */
    public Map<String, FolderAnnotation> annotations = new HashMap<>();

    public static ChineseNameService getInstance(Project project) {
        return project.getService(ChineseNameService.class);
    }

    @Nullable
    @Override
    public ChineseNameService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ChineseNameService state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    /**
     * Add or update Chinese annotation for a folder
     */
    public void addAnnotation(String folderPath, String chineseName, boolean hideOriginalName) {
        annotations.put(folderPath, new FolderAnnotation(chineseName, hideOriginalName));
    }

    /**
     * Get annotation for a folder
     */
    @Nullable
    public FolderAnnotation getAnnotation(String folderPath) {
        return annotations.get(folderPath);
    }

    /**
     * Remove annotation for a folder
     */
    public void removeAnnotation(String folderPath) {
        annotations.remove(folderPath);
    }

    /**
     * Check if a folder has annotation
     */
    public boolean hasAnnotation(String folderPath) {
        return annotations.containsKey(folderPath);
    }

    /**
     * Get display name for a folder
     */
    public String getDisplayName(String folderPath, String originalName) {
        // Press-and-hold: temporarily show original name
        if (holdToShowOriginalEnabled && previewOriginalActive) {
            return originalName;
        }
        FolderAnnotation annotation = getAnnotation(folderPath);
        if (annotation == null) {
            return originalName;
        }

        if (annotation.hideOriginalName) {
            return annotation.chineseName;
        } else {
            return originalName + "(" + annotation.chineseName + ")";
        }
    }

    /**
     * Data class for folder annotation
     */
    public static class FolderAnnotation {
        public String chineseName;
        public boolean hideOriginalName;

        public FolderAnnotation() {
            this("", false);
        }

        public FolderAnnotation(String chineseName, boolean hideOriginalName) {
            this.chineseName = chineseName;
            this.hideOriginalName = hideOriginalName;
        }
    }
}

