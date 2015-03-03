package com.juginabi.towerdefence;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Jukka on 3.3.2015.
 */
public class PencilNeckedGeek extends GameObject_Attacker {
    private Sprite sprite;
    private Texture spriteTexture;

    public PencilNeckedGeek() {
        this.setTilePosition(new Vector2(0,0));
        this.setVelocity(1f);
        this.setHitPoints(10f);
        this.setHeading(new Vector2(0,1));

        spriteTexture = new Texture("TestSprite.png");
        sprite = new Sprite(spriteTexture);
    }

    public void Update(float deltaTime) {
        // Move the attacker
        Vector2 heading = this.getHeading();
        float velocity = this.getVelocity() * deltaTime;
        Vector2 deltaMovement = new Vector2(heading.x * velocity, heading.y * velocity);
        setDeltaPosition(deltaMovement);
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
