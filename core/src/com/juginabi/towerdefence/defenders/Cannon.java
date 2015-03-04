package com.juginabi.towerdefence.defenders;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Juginabi on 03.03.2015.
 */
public class Cannon extends GODefender {
    private Sprite sprite;
    private Texture spriteTexture;

    public Cannon() {
        this.setTilePosition(new Vector2(0,0));
        spriteTexture = new Texture("tankBlack.png");
        sprite = new Sprite(spriteTexture);
    }

    public void Update(float deltaTime) {
        /// TODO: Acquire target, fire a projectile, idle

    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
