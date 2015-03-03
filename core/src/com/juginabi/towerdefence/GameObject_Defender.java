package com.juginabi.towerdefence;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Jukka on 3.3.2015.
 */
public abstract class GameObject_Defender {
    private Vector2 tilePosition_;
    private Vector2 heading_;
    private float range_;

    public Vector2 getPosition() {
        return tilePosition_;
    }

    public void setPosition(Vector2 newPos) {
        this.tilePosition_ = newPos;
    }
}
