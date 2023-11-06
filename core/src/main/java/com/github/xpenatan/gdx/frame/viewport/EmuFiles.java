package com.github.xpenatan.gdx.frame.viewport;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;

public class EmuFiles implements Files {

    private Files gdxFiles;

    private String internalPrefix = "";

    public EmuFiles(Files gdxFiles) {
        this.gdxFiles = gdxFiles;
    }

    public void setInternalPrefix(String internalPrefix) {
        this.internalPrefix = internalPrefix;
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
        path = internalPrefix + path;
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