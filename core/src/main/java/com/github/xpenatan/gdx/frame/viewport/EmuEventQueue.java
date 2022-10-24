package com.github.xpenatan.gdx.frame.viewport;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class EmuEventQueue implements InputProcessor {
    static private final int SKIP = -1;
    static private final int KEY_DOWN = 0;
    static private final int KEY_UP = 1;
    static private final int KEY_TYPED = 2;
    static private final int TOUCH_DOWN = 3;
    static private final int TOUCH_UP = 4;
    static private final int TOUCH_DRAGGED = 5;
    static private final int MOUSE_MOVED = 6;
    static private final int SCROLLED = 7;

    private final IntArray queue = new IntArray();
    private final IntArray processingQueue = new IntArray();
    private long currentEventTime;

    public EmuEventQueue() {
    }

    public void drain(@Null InputProcessor processor) {
        synchronized(this) {
            if(processor == null) {
                queue.clear();
                return;
            }
            processingQueue.addAll(queue);
            queue.clear();
        }
        int[] q = processingQueue.items;
        for(int i = 0, n = processingQueue.size; i < n; ) {
            int type = q[i++];
            currentEventTime = (long)q[i++] << 32 | q[i++] & 0xFFFFFFFFL;
            switch(type) {
                case SKIP:
                    i += q[i];
                    break;
                case KEY_DOWN:
                    processor.keyDown(q[i++]);
                    break;
                case KEY_UP:
                    processor.keyUp(q[i++]);
                    break;
                case KEY_TYPED:
                    processor.keyTyped((char)q[i++]);
                    break;
                case TOUCH_DOWN:
                    processor.touchDown(q[i++], q[i++], q[i++], q[i++]);
                    break;
                case TOUCH_UP:
                    processor.touchUp(q[i++], q[i++], q[i++], q[i++]);
                    break;
                case TOUCH_DRAGGED:
                    processor.touchDragged(q[i++], q[i++], q[i++]);
                    break;
                case MOUSE_MOVED:
                    processor.mouseMoved(q[i++], q[i++]);
                    break;
                case SCROLLED:
                    processor.scrolled(NumberUtils.intBitsToFloat(q[i++]), NumberUtils.intBitsToFloat(q[i++]));
                    break;
                default:
                    throw new RuntimeException();
            }
        }
        processingQueue.clear();
    }

    private synchronized int next(int nextType, int i) {
        int[] q = queue.items;
        for(int n = queue.size; i < n; ) {
            int type = q[i];
            if(type == nextType) return i;
            i += 3;
            switch(type) {
                case SKIP:
                    i += q[i];
                    break;
                case KEY_DOWN:
                    i++;
                    break;
                case KEY_UP:
                    i++;
                    break;
                case KEY_TYPED:
                    i++;
                    break;
                case TOUCH_DOWN:
                    i += 4;
                    break;
                case TOUCH_UP:
                    i += 4;
                    break;
                case TOUCH_DRAGGED:
                    i += 3;
                    break;
                case MOUSE_MOVED:
                    i += 2;
                    break;
                case SCROLLED:
                    i += 2;
                    break;
                default:
                    throw new RuntimeException();
            }
        }
        return -1;
    }

    private void queueTime() {
        long time = TimeUtils.nanoTime();
        queue.add((int)(time >> 32));
        queue.add((int)time);
    }

    public synchronized boolean keyDown(int keycode) {
        queue.add(KEY_DOWN);
        queueTime();
        queue.add(keycode);
        return false;
    }

    public synchronized boolean keyUp(int keycode) {
        queue.add(KEY_UP);
        queueTime();
        queue.add(keycode);
        return false;
    }

    public synchronized boolean keyTyped(char character) {
        queue.add(KEY_TYPED);
        queueTime();
        queue.add(character);
        return false;
    }

    public synchronized boolean touchDown(int screenX, int screenY, int pointer, int button) {
        queue.add(TOUCH_DOWN);
        queueTime();
        queue.add(screenX);
        queue.add(screenY);
        queue.add(pointer);
        queue.add(button);
        return false;
    }

    public synchronized boolean touchUp(int screenX, int screenY, int pointer, int button) {
        queue.add(TOUCH_UP);
        queueTime();
        queue.add(screenX);
        queue.add(screenY);
        queue.add(pointer);
        queue.add(button);
        return false;
    }

    public synchronized boolean touchDragged(int screenX, int screenY, int pointer) {
        // Skip any queued touch dragged events for the same pointer.
        for(int i = next(TOUCH_DRAGGED, 0); i >= 0; i = next(TOUCH_DRAGGED, i + 6)) {
            if(queue.get(i + 5) == pointer) {
                queue.set(i, SKIP);
                queue.set(i + 3, 3);
            }
        }
        queue.add(TOUCH_DRAGGED);
        queueTime();
        queue.add(screenX);
        queue.add(screenY);
        queue.add(pointer);
        return false;
    }

    public synchronized boolean mouseMoved(int screenX, int screenY) {
        // Skip any queued mouse moved events.
        for(int i = next(MOUSE_MOVED, 0); i >= 0; i = next(MOUSE_MOVED, i + 5)) {
            queue.set(i, SKIP);
            queue.set(i + 3, 2);
        }
        queue.add(MOUSE_MOVED);
        queueTime();
        queue.add(screenX);
        queue.add(screenY);
        return false;
    }

    @Override
    public synchronized boolean scrolled(float amountX, float amountY) {
        queue.add(SCROLLED);
        queueTime();
        queue.add(NumberUtils.floatToIntBits(amountX));
        queue.add(NumberUtils.floatToIntBits(amountY));
        return false;
    }

    public long getCurrentEventTime() {
        return currentEventTime;
    }
}
