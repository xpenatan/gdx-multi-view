package com.github.xpenatan.gdx.multiview;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;

public class EmuFiles implements Files {

    private Files gdxFiles;

    private String internalPrefix = "";
    private String localPrefix = "";

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
        return gdxFiles.getFileHandle(path, type);
    }

    @Override
    public FileHandle classpath(String path) {
        return gdxFiles.classpath(path);
    }

    @Override
    public FileHandle internal(String path) {
        if(!path.contains(internalPrefix)) {
            path = internalPrefix + path;
        }
        return gdxFiles.internal(path);
    }

    @Override
    public FileHandle external(String path) {
        return gdxFiles.external(path);
    }

    @Override
    public FileHandle absolute(String path) {
        return gdxFiles.absolute(path);
    }

    @Override
    public FileHandle local(String path) {
        if(!path.contains(localPrefix)) {
            path = localPrefix + path;
        }
        return gdxFiles.local(path);
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