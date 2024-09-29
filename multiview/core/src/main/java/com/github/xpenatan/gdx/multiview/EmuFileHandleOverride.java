package com.github.xpenatan.gdx.multiview;

import com.badlogic.gdx.files.FileHandle;

public interface EmuFileHandleOverride {
    FileHandle getFileHandle(FileHandle fileHandle);
}