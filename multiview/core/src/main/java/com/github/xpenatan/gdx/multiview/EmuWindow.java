package com.github.xpenatan.gdx.multiview;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * This class was originally from XpeEngine and its now public. <br><br>
 * <p>
 * A lower level class that combines inputs and rendering calls to draw to a texture using frameBuffer.
 *
 * @author xpenatan
 */
public abstract class EmuWindow {

    private boolean begin;

    protected FrameBuffer frameBuffer;

    private Graphics gdxGraphics;
    private Input gdxInput;
    private GL20 gdxGL20;
    private GL30 gdxGL30;
    private Files gdxFiles;

    private EmuGraphics emuGraphics;
    private EmuInput emuInput;
    private EmuGL20 emuGL20;
    private EmuFiles emuFiles;

    private boolean disposed;
    private boolean initialized = false;

    private boolean processInput = true;

    private boolean resume = true;
    private boolean resumeStateChanged = true;

    private int defaultHandler;

    private boolean isWindowFocused;
    private int windowWidth;
    private int windowHeight;

    public float u;
    public float v;
    public float u2;
    public float v2;

    private boolean inputReleased = false;
    private boolean isViewportFocused;

    private boolean lastFrameFocus = isViewportFocused;

    private boolean reset;

    public EmuWindow() {
        this(true);
    }

    public EmuWindow(boolean rightClickFocus) {
        this(new EmuInput(Gdx.input, rightClickFocus));
    }

    public EmuWindow(EmuInput input) {
        if(input == null)
            throw new GdxRuntimeException("Input cannot be null");
        this.emuGraphics = new EmuGraphics(this, Gdx.graphics);
        this.emuInput = input;
        this.emuFiles = new EmuFiles(Gdx.files);
        this.emuGL20 = Gdx.gl30 != null ? new EmuGL30(Gdx.gl30) : new EmuGL20(Gdx.gl20);
    }

    public boolean begin(boolean isWindowFocused, boolean isWindowHovered, int windowX, int windowY, int windowWidth, int windowHeight) {
        if(begin == false) {
            begin = true;
            lastFrameFocus = this.isWindowFocused;
            this.isWindowFocused = isWindowFocused;
            this.windowWidth = windowWidth;
            this.windowHeight = windowHeight;

            if(frameBuffer == null || frameBuffer.getWidth() != windowWidth || frameBuffer.getHeight() != windowHeight) {
                if(frameBuffer != null) {
                    frameBuffer.dispose();
                }
                frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, windowWidth, windowHeight, true);
            }

            defaultHandler = EmuFrameBuffer.getDefaultFramebufferHandle();
            int framebufferHandle = frameBuffer.getFramebufferHandle();
            frameBuffer.begin();
            EmuFrameBuffer.setDefaultFramebufferHandle(framebufferHandle);
            ScreenUtils.clear(Color.BLACK, true);

            emuGL20.begin(0, 0, windowWidth, windowHeight);

            gdxGraphics = Gdx.graphics;
            gdxInput = Gdx.input;
            gdxGL20 = Gdx.gl20;
            gdxGL30 = Gdx.gl30;
            gdxFiles = Gdx.files;
            Gdx.graphics = emuGraphics;
            Gdx.input = emuInput;
            Gdx.files = emuFiles;

            if(gdxGL30 != null)
                Gdx.gl30 = (GL30)emuGL20;
            Gdx.gl20 = emuGL20;
            Gdx.gl = emuGL20;

            emuGraphics.setSize(windowWidth, windowHeight);
            emuInput.setWindow(isWindowFocused, isWindowHovered, windowX, windowY, windowWidth, windowHeight);
            return true;
        }
        return false;
    }

    public boolean end() {
        if(begin) {
            begin = false;
            Gdx.graphics = gdxGraphics;
            Gdx.input = gdxInput;
            Gdx.gl = gdxGL30 != null ? gdxGL30 : gdxGL20;
            Gdx.gl20 = gdxGL20;
            Gdx.gl30 = gdxGL30;
            Gdx.files = gdxFiles;
            EmuFrameBuffer.setDefaultFramebufferHandle(defaultHandler);
            frameBuffer.end();

            emuGL20.end();
            reset = false;
            return true;
        }
        return false;
    }

    public Texture getTexture() {
        Texture texture = frameBuffer.getColorBufferTexture();
        float srcX = 0;
        float srcY = 0;
        float srcWidth = windowWidth;
        float srcHeight = windowHeight;
        float invTexWidth = 1.0f / texture.getWidth();
        float invTexHeight = 1.0f / texture.getHeight();
        u = srcX * invTexWidth;
        v = (srcY + srcHeight) * invTexHeight;
        u2 = (srcX + srcWidth) * invTexWidth;
        v2 = srcY * invTexHeight;
        return texture;
    }

    public int getTextureID() {
        return getTexture().getTextureObjectHandle();
    }

    public boolean canUpdate() {
        return disposed == false && (resume || resumeStateChanged);
    }

    public void loop() {
        updateInput();
        if(canUpdate()) {
            update();
            Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            onRender();
        }
    }

    public void updateInput() {
        if(lastFrameFocus && !isWindowFocused) {
            inputReleased = false;
        }

        setProcessInput(isWindowFocused);
        if(!isWindowFocused && !inputReleased) {
            inputReleased = true;
            getEmuInput().releaseInput();
        }
    }

    public void update() {
        if(reset) {
            onDispose();
            emuInput.setInputProcessor(null);
            emuGL20.reset();
            initialized = false;
        }

        if(initialized == false) {
            onCreate();
            emuGraphics.sizeChanged = true;
            initialized = true;
        }

        if(resumeStateChanged) {
            resumeStateChanged = false;
            if(resume == true)
                onResume();
            else
                onPause();
        }
        emuGraphics.update();
        if(processInput)
            emuInput.processEvents();
    }

    public void setProcessInput(boolean flag) {
        processInput = flag;
    }

    public void setResume(boolean toResume) {
        if(this.resume != toResume) {
            this.resume = toResume;
            resumeStateChanged = true;
        }
    }

    public EmuFiles getEmuFiles() {
        return emuFiles;
    }

    public EmuInput getEmuInput() {
        return emuInput;
    }

    public void dispose() {
        if(disposed == false) {
            disposed = true;
            onPause();
            onDispose();
            emuGraphics.dispose();
            emuInput.dispose();
        }
    }

    public void reset() {
        reset = true;
    }

    protected abstract void onCreate();

    protected abstract void onResize(int width, int height);

    protected abstract void onRender();

    protected abstract void onPause();

    protected abstract void onResume();

    protected abstract void onDispose();
}
