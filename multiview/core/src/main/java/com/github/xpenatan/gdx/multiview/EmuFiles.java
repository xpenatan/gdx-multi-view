package com.github.xpenatan.gdx.multiview;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;

public class EmuFiles implements Files {

    private Files gdxFiles;

    private String internalPrefix = "";
    private String localPrefix = "";

    public EmuFileHandleOverride fileHandleOverride;

    public EmuFiles(Files gdxFiles) {
        this.gdxFiles = gdxFiles;
    }

    public void setInternalPrefix(String internalPrefix) {
        this.internalPrefix = internalPrefix;
    }

    public void setLocalPrefix(String localPrefix) {
        this.localPrefix = localPrefix;
    }

    @Override
    public FileHandle getFileHandle(String path, FileType type) {
        FileHandle fileHandle = null;
        switch(type) {
            case Classpath:
                fileHandle = gdxFiles.classpath(path);
                break;
            case Internal:
                fileHandle = gdxFiles.internal(path);
                break;
            case External:
                fileHandle = gdxFiles.external(path);
                break;
            case Absolute:
                fileHandle = gdxFiles.absolute(path);
                break;
            case Local:
                fileHandle = gdxFiles.local(path);
                break;
        }
        if(fileHandle != null && fileHandleOverride != null) {
            return fileHandleOverride.getFileHandle(fileHandle);
        }
        return fileHandle;
    }

    @Override
    public FileHandle classpath(String path) {
        return getFileHandle(path, FileType.Classpath);
    }

    @Override
    public FileHandle internal(String path) {
        if(!path.contains(internalPrefix)) {
            path = internalPrefix + path;
        }
        return getFileHandle(path, FileType.Internal);
    }

    @Override
    public FileHandle external(String path) {
        return getFileHandle(path, FileType.External);
    }

    @Override
    public FileHandle absolute(String path) {
        return getFileHandle(path, FileType.Absolute);
    }

    @Override
    public FileHandle local(String path) {
        if(!path.contains(localPrefix)) {
            path = localPrefix + path;
        }
        return getFileHandle(path, FileType.Local);
    }

    @Override
    public String getExternalStoragePath() {
        return gdxFiles.getExternalStoragePath();
    }

    @Override
    public boolean isExternalStorageAvailable() {
        return gdxFiles.isExternalStorageAvailable();
    }

    @Override
    public String getLocalStoragePath() {
        return gdxFiles.getLocalStoragePath();
    }

    @Override
    public boolean isLocalStorageAvailable() {
        return gdxFiles.isLocalStorageAvailable();
    }
}