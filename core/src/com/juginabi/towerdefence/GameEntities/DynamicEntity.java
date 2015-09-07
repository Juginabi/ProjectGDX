package com.juginabi.towerdefence.GameEntities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.juginabi.towerdefence.GameWorld;

/**
 * Created by Jukka on 3.3.2015.
 */
public abstract class DynamicEntity extends Sprite {
    private final GameWorld parentWorld;
    private Vector2 heading_;
    private float velocity_;
    private float hitPoints_;
    protected int type;

    protected boolean isAlive_;

    protected DynamicEntity(GameWorld parent, TextureAtlas.AtlasRegion region, int type) {
        super(region);
        this.type = type;
        this.parentWorld = parent;
        this.heading_ = new Vector2(0,0);
        this.isAlive_ = false;
    }

    public boolean IsAlive() {
        return isAlive_;
    }

    public void SetStatusAlive(boolean status) {
        this.isAlive_ = status;
    }

    public int getType() {
        return this.type;
    }

    protected float getVelocity() {
        return this.velocity_;
    }

    protected float getHitPoints() {
        return this.hitPoints_;
    }

    public Vector2 getHeading() {
        return this.heading_;
    }

    protected void setHeading(float x, float y) {
        this.heading_.x = x;
        this.heading_.y = y;
    }

    public abstract void initialize(float x, float y);

    protected void setVelocity(float newVelocity) {
        this.velocity_ = newVelocity;
    }

    protected void setHitPoints(float newHitPoints) {
        this.hitPoints_ = newHitPoints;
    }

    public abstract boolean Update(float tickMilliseconds);

    protected GameWorld GetParentWorld() {
        return this.parentWorld;
    }

    public boolean inflictDamage(float damage) {
        return (this.hitPoints_ -= damage) < 0;
    }

    public abstract void Draw(Batch batch);
}
