package com.juginabi.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jukka on 20.2.2015.
 */
public class EventHandler implements InputProcessor {
    // Keyboard
    boolean keyUP       = false;
    boolean keyDOWN     = false;
    boolean keyLEFT     = false;
    boolean keyRIGHT    = false;

    // Mouse and touch input
    Map<Integer, CursorStatus> cursorStatusMap = null;

    EventHandler() {
        cursorStatusMap = new HashMap<Integer, CursorStatus>();
        Gdx.input.setInputProcessor(this);
    }

    public Map<Integer, CursorStatus> getCursorStatus() {
        return cursorStatusMap;
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
        if (cursorStatusMap.get(pointer) == null)
            cursorStatusMap.put(pointer, new CursorStatus());
        switch (button) {
            case Input.Buttons.LEFT:
                // Button left is also same as touch on Android
                cursorStatusMap.get(pointer).setMouseButton(button, true);
                cursorStatusMap.get(pointer).setPosition(screenX, screenY);
                cursorStatusMap.get(pointer).setTimeSinceUpdate(System.currentTimeMillis());
                break;
            case Input.Buttons.RIGHT:
                cursorStatusMap.get(pointer).setMouseButton(button, true);
                cursorStatusMap.get(pointer).setPosition(screenX, screenY);
                cursorStatusMap.get(pointer).setTimeSinceUpdate(System.currentTimeMillis());
                break;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        switch (button) {
            case Input.Buttons.LEFT:
                cursorStatusMap.get(pointer).setMouseButton(button, false);
                cursorStatusMap.get(pointer).setPosition(screenX, screenY);
                cursorStatusMap.get(pointer).setTimeSinceUpdate(System.currentTimeMillis());
                break;
            case Input.Buttons.RIGHT:
                cursorStatusMap.get(pointer).setMouseButton(button, false);
                cursorStatusMap.get(pointer).setPosition(screenX, screenY);
                cursorStatusMap.get(pointer).setTimeSinceUpdate(System.currentTimeMillis());
                break;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        cursorStatusMap.get(pointer).setPosition(screenX, screenY);
        cursorStatusMap.get(pointer).setTimeSinceUpdate(System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }


    /**
     * Encapsulates all cursory actions like mouse and touch
     */
    public class CursorStatus {
        private Vector2 position = null;
        private boolean buttonLEFT  = false;
        private boolean buttonRIGHT = false;
        private long timeSinceUpdate = 0;

        CursorStatus() {
            this.position = new Vector2(0,0);
            this.buttonLEFT = false;
            this.buttonRIGHT = false;
            timeSinceUpdate = System.currentTimeMillis();
        }

        CursorStatus(int x, int y, boolean left, boolean right) {
            this.position = new Vector2(x,y);
            this.buttonLEFT = left;
            this.buttonRIGHT = right;
        }

        /**
         * See if button left is pressed. On android this indicates touch.
         * @return Boolean if buttonLeft (on desktop) or touch (on Android) is active.
         */
        public boolean getMouseLeft() {
            return this.buttonLEFT;
        }

        /**
         * See if button right is pressed.
         * @return Boolean if buttonRight (on desktop) is active.
         */
        public boolean getMouseRight() {
            return this.buttonRIGHT;
        }

        /**
         * Get position of this cursor.
         * @return Gdx.Math.Vector2 position
         */
        public Vector2 getPosition() {
            return this.position;
        }

        /**
         * Get time of last update of this cursor
         * @return System.currentTimeInMillis when button or touch press/drag/release activity last happened
         */
        public long getTimeSinceUpdate() {
            return this.timeSinceUpdate;
        }

        public void setPosition(int x, int y) {
            this.position.x = x;
            this.position.y = y;
        }

        public void setTimeSinceUpdate(long time) {
            this.timeSinceUpdate = time;
        }

        public void setMouseButton(int keycode, boolean status) {
            switch (keycode) {
                case Input.Buttons.LEFT:
                    this.buttonLEFT = status;
                    break;
                case Input.Buttons.RIGHT:
                    this.buttonRIGHT = status;
                    break;
            }
        }
    }
}
