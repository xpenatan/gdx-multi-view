package com.github.xpenatan.gdx.multiview;

import com.badlogic.gdx.ApplicationListener;

/**
 * This class was originally from XpeEngine and its now public. <br><br>
 * <p>
 * This class is to emulate Gdx game frame.
 *
 * @author xpenatan
 */
public class EmuApplicationWindow extends EmuWindow {
    private ApplicationListener oldListener;
    private ApplicationListener applicationListener;

    private boolean created = false;

    public EmuApplicationWindow() {
    }

    public EmuApplicationWindow(EmuInput input) {
        super(input);
    }

    public void setApplicationListener(ApplicationListener applicationListener) {
        if(this.applicationListener != applicationListener) {
            oldListener = this.applicationListener;
            this.applicationListener = applicationListener;
            reset();
            created = false; // Old listener will dispose and the new listener won't.
        }
    }

    @Override
    protected void onCreate() {
        if(applicationListener != null) {
            created = true;
            applicationListener.create();
        }
    }

    @Override
    protected void onResize(int width, int height) {
        if(applicationListener != null)
            applicationListener.resize(width, height);
    }

    @Override
    protected void onRender() {
        if(applicationListener != null)
            applicationListener.render();
    }

    @Override
    protected void onPause() {
        if(applicationListener != null)
            applicationListener.pause();
    }

    @Override
    protected void onResume() {
        if(applicationListener != null)
            applicationListener.resume();
    }

    @Override
    protected void onDispose() {
        if(oldListener != null) {
            oldListener.dispose();
            oldListener = null;
        }
        if(created) {
            if(applicationListener != null) {
                applicationListener.dispose();
                applicationListener = null;
            }
            created = false;
        }
    }

    public ApplicationListener getApplicationListener() {
        return applicationListener;
    }
}
