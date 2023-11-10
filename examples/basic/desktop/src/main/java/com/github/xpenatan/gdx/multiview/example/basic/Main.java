package com.github.xpenatan.gdx.multiview.example.basic;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {

    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1080;
        config.height = 800;
        config.title = "gdx-multi-view";
        config.vSyncEnabled = true;
        new LwjglApplication(new GameViewportExample(), config);
    }
}
