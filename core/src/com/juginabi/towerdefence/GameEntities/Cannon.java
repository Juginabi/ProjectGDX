package com.juginabi.towerdefence.GameEntities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.juginabi.towerdefence.TowerDefence;

/**
 * Created by Juginabi on 03.03.2015.
 */
public class Cannon extends DynamicEntity {
    public static String TAG = "Cannon";
    double timeSinceLastFire = 0;
    private Sound fireSound;
    private boolean soundLoaded = false;

    public Cannon(Texture tex) {
        super(tex);
        final AssetManager manager = TowerDefence.getAssetManager();
        if (manager.isLoaded("Audio/defaultLaser.ogg")) {
            fireSound = manager.get("Audio/defaultLaser.ogg");
            soundLoaded = true;
        }
        timeSinceLastFire = TimeUtils.millis();
    }

    public void Update(float deltaTime) {
        /// TODO: Acquire target, fire a projectile, idle
        if (TimeUtils.millis() - timeSinceLastFire > 1000) {
            Fire();
            timeSinceLastFire = TimeUtils.millis();
        }
    }

    private void Fire() {
        if (soundLoaded)
            fireSound.play();
    }
}
