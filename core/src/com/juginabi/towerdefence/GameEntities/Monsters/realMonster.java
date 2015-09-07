package com.juginabi.towerdefence.GameEntities.Monsters;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.juginabi.towerdefence.GameEntities.DynamicEntity;
import com.juginabi.towerdefence.GameWorld;

/**
 * Created by Jukka on 7.9.2015.
 */
public class realMonster extends DynamicEntity {
    public float velocity;
    public int hitpoints;

    protected realMonster(GameWorld parent, TextureAtlas.AtlasRegion region, int type) {
        super(parent, region, type);
    }

    @Override
    public void initialize(float x, float y) {

    }

    @Override
    public boolean Update(float tickMilliseconds) {
        return false;
    }

    @Override
    public void Draw(Batch batch) {

    }
}
