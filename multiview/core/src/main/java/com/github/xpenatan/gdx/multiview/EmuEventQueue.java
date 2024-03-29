package com.github.xpenatan.gdx.multiview;

import com.badlogic.gdx.utils.TimeUtils;

public class EmuEventQueue extends CustomEventQueue {

    public EmuEventQueue() {
    }

    private long getTime() {
        return TimeUtils.nanoTime();
    }

    public synchronized boolean keyDown(int keycode) {
        return keyDown(keycode, getTime());
    }

    public synchronized boolean keyUp(int keycode) {
        return keyUp(keycode, getTime());
    }

    public synchronized boolean keyTyped(char character) {
        return keyTyped(character, getTime());
    }

    public synchronized boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return touchDown(screenX, screenY, pointer, button, getTime());
    }

    public synchronized boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return touchUp(screenX, screenY, pointer, button, getTime());
    }

    public synchronized boolean touchDragged(int screenX, int screenY, int pointer) {
        return touchDragged(screenX, screenY, pointer, getTime());
    }

    public synchronized boolean mouseMoved(int screenX, int screenY) {
        return mouseMoved(screenX, screenY, getTime());
    }

    public synchronized boolean scrolled(float amountX, float amountY) {
        return scrolled(amountX, amountY, getTime());
    }

    public synchronized boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        queue.add(TOUCH_CANCELED);
        queueTime(getTime());
        queue.add(screenX);
        queue.add(screenY);
        queue.add(pointer);
        queue.add(button);
        return false;
    }
}
