package com.juginabi.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * Created by Jukka on 20.2.2015.
 */
public class EventHandler implements InputProcessor {
    boolean keyUP       = false;
    boolean keyDOWN     = false;
    boolean keyLEFT     = false;
    boolean keyRIGHT    = false;

    EventHandler() {
        Gdx.input.setInputProcessor(this);
    }

    public boolean isKeyPressed(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
            case Input.Keys.W:
                return this.keyUP;
            case Input.Keys.DOWN:
            case Input.Keys.S:
                return this.keyDOWN;
            case Input.Keys.LEFT:
            case Input.Keys.A:
                return this.keyLEFT;
            case Input.Keys.RIGHT:
            case Input.Keys.D:
                return this.keyRIGHT;
            default:
                return false;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        // Handle all keyDown inputs here. Set the appropriate flags
        switch (keycode) {
            case Input.Keys.UP:
            case Input.Keys.W:
                this.keyUP = true;
                break;
            case Input.Keys.DOWN:
            case Input.Keys.S:
                this.keyDOWN = true;
                break;
            case Input.Keys.LEFT:
            case Input.Keys.A:
                this.keyLEFT = true;
                break;
            case Input.Keys.RIGHT:
            case Input.Keys.D:
                this.keyRIGHT = true;
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        // Handle all keyUp inputs here. Set the appropriate flags
        switch (keycode) {
            case Input.Keys.UP:
            case Input.Keys.W:
                this.keyUP = false;
                break;
            case Input.Keys.DOWN:
            case Input.Keys.S:
                this.keyDOWN = false;
                break;
            case Input.Keys.LEFT:
            case Input.Keys.A:
                this.keyLEFT = false;
                break;
            case Input.Keys.RIGHT:
            case Input.Keys.D:
                this.keyRIGHT = false;
                break;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
