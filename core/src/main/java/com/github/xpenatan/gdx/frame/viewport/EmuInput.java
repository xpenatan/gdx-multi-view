package com.github.xpenatan.gdx.frame.viewport;

import com.badlogic.gdx.AbstractInput;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.IntSet.IntSetIterator;

/**
 * This class was originally from XpeEngine and its now public. <br><br>
 * <p>
 * Class to emulate input when its touched inside a viewport. <br>
 * ViewportX/Y needs to be updated so mouse position is converted correctly to the rendering scene. <br>
 * Needs to call drain to process events.
 *
 * @author xpenatan
 */
public class EmuInput extends AbstractInput implements InputProcessor, Disposable {

    private boolean enable = true;

    private InputProcessor inputProcessorInternal;
    private InputProcessor processor; // the viewport/game input
    private Input gdxInput;

    private EmuEventQueue eventQueue = new EmuEventQueue();
    private int mouseX, mouseY;
    private int deltaX, deltaY;
    private boolean justTouched;
    private boolean[] justPressedButtons = new boolean[5];

    private IntSet[] buttonPressed = new IntSet[10];
    private IntSet keyDown = new IntSet();

    private boolean releaseAtDrain;

    private IntSet touchDownInside = new IntSet();
    private boolean isWindowFocused;
    private boolean isWindowHovered;
    private int viewportX;
    private int viewportY;
    private int viewportWidth;
    private int viewportHeight;

    private boolean needsFocus;

    public EmuInput(Input gdxInput) {
        super();
        this.gdxInput = gdxInput;

        for(int i = 0; i < 10; i++)
            buttonPressed[i] = new IntSet();

        // Processed when calling drain
        inputProcessorInternal = new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                if(!enable)
                    return false;

                keyJustPressed = true;
                justPressedKeys[keycode] = true;
                pressedKeys[keycode] = true;
                Gdx.graphics.requestRendering();
                return processor != null && processor.keyDown(keycode);
            }

            @Override
            public boolean keyUp(int keycode) {
                if(!enable)
                    return false;

                pressedKeys[keycode] = false;
                Gdx.graphics.requestRendering();
                return processor != null && processor.keyUp(keycode);
            }

            @Override
            public boolean keyTyped(char character) {
                if(!enable)
                    return false;

                Gdx.graphics.requestRendering();
                return processor != null && processor.keyTyped(character);
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if(!enable)
                    return false;
                screenX = toWindowX(screenX);
                screenY = toWindowY(screenY);
                mouseX = screenX;
                mouseY = screenY;
                deltaX = 0;
                deltaY = 0;
                buttonPressed[pointer].add(button);
                justTouched = true;
                justPressedButtons[button] = true;
                Gdx.graphics.requestRendering();
                return processor != null && processor.touchDown(mouseX, mouseY, pointer, button);
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if(!enable)
                    return false;

                buttonPressed[pointer].remove(button);
                Gdx.graphics.requestRendering();

                return processor != null && processor.touchUp(mouseX, mouseY, pointer, button);
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if(!enable)
                    return false;

                screenX = toWindowX(screenX);
                screenY = toWindowY(screenY);

                deltaX = screenX - mouseX;
                deltaY = screenY - mouseY;
                mouseX = screenX;
                mouseY = screenY;

                Gdx.graphics.requestRendering();
                return processor != null && processor.touchDragged(mouseX, mouseY, pointer);
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                if(!enable)
                    return false;

                screenX = toWindowX(screenX);
                screenY = toWindowY(screenY);

                deltaX = screenX - mouseX;
                deltaY = screenY - mouseY;

                mouseX = screenX;
                mouseY = screenY;
                Gdx.graphics.requestRendering();
                return processor != null && processor.mouseMoved(mouseX, mouseY);
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                if(!enable)
                    return false;

                Gdx.graphics.requestRendering();
                return processor != null && processor.scrolled(amountX, amountY);
            }
        };
    }

    private int toWindowX(int screenX) {
        return screenX - viewportX;
    }

    private int toWindowY(int screenY) {
        return screenY - viewportY;
    }

    public void setWindow(boolean isWindowFocused, boolean isWindowHovered, int x, int y, int width, int height) {
        this.isWindowHovered = isWindowHovered;
        if(this.isWindowFocused != isWindowFocused) {
            this.isWindowFocused = isWindowFocused;
            if(isWindowFocused) {
                if(!keyDown.isEmpty()) {
                    IntSetIterator iterator = keyDown.iterator();
                    while(iterator.hasNext) {
                        int keyDownCode = iterator.next();
                        eventQueue.keyDown(keyDownCode);
                    }
                }
            }
        }
        this.viewportX = x;
        this.viewportY = y;
        this.viewportWidth = width;
        this.viewportHeight = height;
    }

    public void processEvents() {
        if(justTouched) {
            justTouched = false;
            for(int i = 0; i < justPressedButtons.length; i++) {
                justPressedButtons[i] = false;
            }
        }
        if(keyJustPressed) {
            keyJustPressed = false;
            for(int i = 0; i < justPressedKeys.length; i++) {
                justPressedKeys[i] = false;
            }
        }

        if(releaseAtDrain) {
            releaseAtDrain = false;
            releaseInputInternal();
        }
        deltaX = 0;
        deltaY = 0;
        eventQueue.drain(inputProcessorInternal);
    }

    public void releaseInput() {
        this.releaseAtDrain = releaseAtDrain;
    }

    private void releaseInputInternal() {
        if(!releaseAtDrain) {
            justTouched = false;
            keyJustPressed = false;
            for(int i = 0; i < justPressedKeys.length; i++) {
                justPressedKeys[i] = false;
            }

            for(int i = 0; i < justPressedButtons.length; i++)
                justPressedButtons[i] = false;

            for(int i = 0; i < 10; i++) {
                IntSet pressed = buttonPressed[i];
                IntSetIterator iterator = pressed.iterator();
                while(iterator.hasNext) {
                    int buttonKey = iterator.next();
                    iterator.remove();
                    eventQueue.touchUp(getX(), getY(), 0, buttonKey);
                }
            }

            for(int i = 0; i < pressedKeys.length; i++) {
                pressedKeys[i] = false;
            }
            deltaX = 0;
            deltaY = 0;
        }
    }

    public void clear() {
        for(int i = 0; i < 5; i++)
            justPressedButtons[i] = false;
        for(int i = 0; i < 256; i++)
            justPressedKeys[i] = false;
        justTouched = false;
        keyJustPressed = false;
        for(int i = 0; i < 10; i++) {
            IntSet pressed = buttonPressed[i];
            pressed.clear();
        }
        for(int i = 0; i < pressedKeys.length; i++) {
            pressedKeys[i] = false;
        }
        deltaX = 0;
        deltaY = 0;
        mouseX = 0;
        mouseY = 0;
        releaseAtDrain = false;
        touchDownInside.clear();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(!enable)
            return false;
        if(isWindowFocused && isWindowHovered || isWindowHovered && button == Buttons.LEFT) { // Fix when window is focus and goes out of focus and pass input to window. ImGui needs a least 1 frame delay to process input
            touchDownInside.add(button);
            eventQueue.touchDown(screenX, screenY, pointer, button);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(!enable)
            return false;
        if(isWindowFocused && !touchDownInside.isEmpty()) {
            eventQueue.touchDragged(screenX, screenY, pointer);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(!enable)
            return false;
        boolean removed = touchDownInside.remove(button); // release mouse if it was pressed inside window
        if((isWindowFocused && isWindowHovered) || removed) { // Fix when window is focus and goes out of focus and pass input to window. ImGui needs a least 1 frame delay to process input
            eventQueue.touchUp(screenX, screenY, pointer, button);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if(!enable)
            return false;
        if(isWindowFocused) {
            eventQueue.keyTyped(character);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(!enable)
            return false;
        keyDown.add(keycode);
        if(isWindowFocused) {
            eventQueue.keyDown(keycode);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(!enable)
            return false;
        keyDown.remove(keycode);
        if(isWindowFocused) {
            eventQueue.keyUp(keycode);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if(!enable)
            return false;
        if(isWindowFocused) {
            eventQueue.mouseMoved(screenX, screenY);
            return true;
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if(!enable)
            return false;
        if(isWindowFocused && isWindowHovered) {
            eventQueue.scrolled(amountX, amountY);
            return true;
        }
        return false;
    }

    public boolean needsFocus() {
        boolean focus = needsFocus;
        needsFocus = false;
        return focus;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public int getX() {
        return mouseX;
    }

    @Override
    public int getX(int pointer) {
        return mouseX;
    }

    @Override
    public int getY() {
        return mouseY;
    }

    @Override
    public int getY(int pointer) {
        return mouseY;
    }

    @Override
    public int getDeltaX() {
        return deltaX;
    }

    @Override
    public int getDeltaX(int pointer) {
        return deltaX;
    }

    @Override
    public int getDeltaY() {
        return deltaY;
    }

    @Override
    public int getDeltaY(int pointer) {
        return deltaY;
    }

    @Override
    public boolean isTouched() {
        if(!enable)
            return false;
        return buttonPressed[0].contains(0) || buttonPressed[0].contains(1) || buttonPressed[0].contains(2);
    }

    @Override
    public boolean justTouched() {
        if(!enable)
            return false;
        return justTouched;
    }

    @Override
    public boolean isTouched(int pointer) {
        if(!enable)
            return false;
        return buttonPressed[pointer].contains(pointer);
    }

    @Override
    public boolean isButtonPressed(int button) {
        if(!enable)
            return false;
        return buttonPressed[0].contains(button);
    }

    @Override
    public boolean isButtonJustPressed(int button) {
        if(!enable)
            return false;
        return justPressedButtons[button];
    }

    @Override
    public float getAccelerometerX() {
        return gdxInput.getAccelerometerX();
    }

    @Override
    public float getAccelerometerY() {
        return gdxInput.getAccelerometerY();
    }

    @Override
    public float getAccelerometerZ() {
        return gdxInput.getAccelerometerZ();
    }

    @Override
    public float getGyroscopeX() {
        return gdxInput.getGyroscopeX();
    }

    @Override
    public float getGyroscopeY() {
        return gdxInput.getGyroscopeY();
    }

    @Override
    public float getGyroscopeZ() {
        return gdxInput.getGyroscopeZ();
    }

    @Override
    public int getMaxPointers() {
        return gdxInput.getMaxPointers();
    }

    @Override
    public float getPressure() {
        return gdxInput.getPressure();
    }

    @Override
    public float getPressure(int pointer) {
        return gdxInput.getPressure();
    }

    @Override
    public void getTextInput(TextInputListener listener, String title, String text, String hint) {
        gdxInput.getTextInput(listener, title, text, hint);
    }

    @Override
    public void getTextInput(TextInputListener listener, String title, String text, String hint, OnscreenKeyboardType type) {
        gdxInput.getTextInput(listener, title, text, hint, type);
    }

    @Override
    public void setOnscreenKeyboardVisible(boolean visible) {
        gdxInput.setOnscreenKeyboardVisible(visible);
    }

    @Override
    public void setOnscreenKeyboardVisible(boolean visible, OnscreenKeyboardType type) {
        gdxInput.setOnscreenKeyboardVisible(visible, type);
    }

    @Override
    public void vibrate(int milliseconds) {
        gdxInput.vibrate(milliseconds);
    }

    @Override
    public void vibrate(long[] pattern, int repeat) {
        gdxInput.vibrate(pattern, repeat);
    }

    @Override
    public void cancelVibrate() {
        gdxInput.cancelVibrate();
    }

    @Override
    public float getAzimuth() {
        return gdxInput.getAzimuth();
    }

    @Override
    public float getPitch() {
        return gdxInput.getPitch();
    }

    @Override
    public float getRoll() {
        return gdxInput.getRoll();
    }

    @Override
    public void getRotationMatrix(float[] matrix) {
        gdxInput.getRotationMatrix(matrix);
    }

    @Override
    public long getCurrentEventTime() {
        return 0;
    }

    @Override
    public void setInputProcessor(InputProcessor processor) {
        this.processor = processor;
    }

    @Override
    public InputProcessor getInputProcessor() {
        return processor;
    }

    @Override
    public boolean isPeripheralAvailable(Peripheral peripheral) {
        if(!enable)
            return false;
        return gdxInput.isPeripheralAvailable(peripheral);
    }

    @Override
    public int getRotation() {
        return gdxInput.getRotation();
    }

    @Override
    public Orientation getNativeOrientation() {
        return gdxInput.getNativeOrientation();
    }

    @Override
    public void setCursorCatched(boolean catched) {
        gdxInput.setCursorCatched(catched);
    }

    @Override
    public boolean isCursorCatched() {
        return gdxInput.isCursorCatched();
    }

    @Override
    public void setCursorPosition(int x, int y) {
        gdxInput.setCursorPosition(x, y);
    }

    @Override
    public void dispose() {
    }
}
