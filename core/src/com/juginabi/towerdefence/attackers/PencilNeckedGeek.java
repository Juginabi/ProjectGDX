package com.juginabi.towerdefence.attackers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Jukka on 3.3.2015.
 */
public class PencilNeckedGeek extends GOAttacker {
    private Sprite sprite;
    private Texture spriteTexture;

    public PencilNeckedGeek() {
        this.setTilePosition(4,17);
        this.setVelocity(1f);
        this.setHitPoints(10f);
        this.setHeading(new Vector2(0,-1));

        spriteTexture = new Texture("smiley.png");
        sprite = new Sprite(spriteTexture);
    }

    public void Update(float deltaTime) {
        // Move the attacker
        Vector2 heading = this.getHeading();
        float velocity = this.getVelocity();
        Vector2 deltaMovement = new Vector2(heading.x * velocity * deltaTime, heading.y * velocity * deltaTime);
        setDeltaPosition(deltaMovement);
        sprite.setPosition(getTilePosition().x, getTilePosition().y);
    }

    public void Draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public Vector2 GetLocation() {
        return getTilePosition();
    }
}
