package com.juginabi.towerdefence;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Jukka on 3.3.2015.
 */
public abstract class GameObject_Attacker {
    private Vector2 tilePosition_;
    private Vector2 heading_;
    private float velocity_;
    private float hitPoints_;

    public Vector2 getTilePosition() {
        return tilePosition_;
    }

    public float getVelocity() {
        return this.velocity_;
    }

    public float getHitPoints() {
        return this.hitPoints_;
    }

    public Vector2 getHeading() {
        return this.heading_;
    }

    public void setHeading(Vector2 newHeading) {
        this.heading_ = newHeading;
    }

    public void setTilePosition(Vector2 newPos) {
        this.tilePosition_ = newPos;
    }

    public void setVelocity(float newVelocity) {
        this.velocity_ = newVelocity;
    }

    public void setHitPoints(float newHitPoints) {
        this.hitPoints_ = newHitPoints;
    }

    public void setDeltaPosition(Vector2 deltaPos) {
        this.tilePosition_.add(deltaPos);
    }
}
