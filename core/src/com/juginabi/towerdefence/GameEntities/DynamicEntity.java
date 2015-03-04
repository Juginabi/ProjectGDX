package com.juginabi.towerdefence.GameEntities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Jukka on 3.3.2015.
 */
public abstract class DynamicEntity extends Sprite {
    private Vector2 heading_;
    private float velocity_;
    private float hitPoints_;

    public DynamicEntity(Texture tex) {
        super(tex);
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

    public void setVelocity(float newVelocity) {
        this.velocity_ = newVelocity;
    }

    public void setHitPoints(float newHitPoints) {
        this.hitPoints_ = newHitPoints;
    }
}
