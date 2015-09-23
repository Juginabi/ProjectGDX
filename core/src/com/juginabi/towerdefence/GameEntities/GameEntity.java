package com.juginabi.towerdefence.GameEntities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by Jukka on 16.9.2015.
 */
public abstract class GameEntity extends Sprite {

    public boolean isAlive = false;
    public boolean removeThisEntity = false;
    public int typeId;
    public Body body;

    public GameEntity(TextureRegion region) {
        super(region);
    }

    public GameEntity(TextureAtlas.AtlasRegion tex) {
        super(tex);
    }

    public abstract void Update(float tick);

    public abstract void Draw(Batch batch);

    public abstract void initialize(float x, float y);

    public static final int
            ID_ENEMY_NAZI = 1,
            ID_ENEMY_SPEARMAN = 2,
            ID_DEFENDER_TANK = 3;
}
