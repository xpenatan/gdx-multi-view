package com.github.xpenatan.gdx.frame.viewport.example;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameViewportExample implements ApplicationListener {
    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1080;
        config.height = 800;
        config.title = "gdx-frame-viewport";
        config.vSyncEnabled = true;
        new LwjglApplication(new GameViewportExample(), config);
    }

    SpriteBatch batch;
    private OrthographicCamera camera;

    private GameFrame gameFrame1;
    private GameFrame gameFrame2;
    private GameFrame gameFrame3;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(true);
        batch = new SpriteBatch();

        gameFrame1 = new GameFrame(20, 20, 600, 375);
        gameFrame2 = new GameFrame(20, 410, 600, 375);
        gameFrame3 = new GameFrame(650, 20, 400, 765);
        gameFrame1.emuWindow.setApplicationListener(new Box2dLightTest());
        gameFrame2.emuWindow.setApplicationListener(new Box2dLightTest());
        gameFrame3.emuWindow.setApplicationListener(new GameApp());

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(gameFrame1.emuWindow.getInput());
        multiplexer.addProcessor(gameFrame2.emuWindow.getInput());
        multiplexer.addProcessor(gameFrame3.emuWindow.getInput());
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        gameFrame1.update();
        gameFrame2.update();
        gameFrame3.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        gameFrame1.draw(batch);
        gameFrame2.draw(batch);
        gameFrame3.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
