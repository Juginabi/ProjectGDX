package com.juginabi.towerdefence.defenders;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Jukka on 3.3.2015.
 */
public abstract class GODefender {
    private Vector2 tilePosition_;
    private float range_;

    public Vector2 getTilePosition() {
        return tilePosition_;
    }

    public void setTilePosition(Vector2 newPos) {
        this.tilePosition_ = newPos;
    }

}
