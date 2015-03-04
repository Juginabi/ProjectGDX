package com.juginabi.towerdefence.GameEntities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by Juginabi on 03.03.2015.
 */
public class Cannon extends DynamicEntity {
    Sound oggSound3 = Gdx.audio.newSound(Gdx.files.internal("audio/powerUp1.ogg"));
    Sound oggSound4 = Gdx.audio.newSound(Gdx.files.internal("audio/powerUp2.ogg"));
    Sound oggSound5 = Gdx.audio.newSound(Gdx.files.internal("audio/powerUp3.ogg"));
    Sound oggSound6 = Gdx.audio.newSound(Gdx.files.internal("audio/powerUp4.ogg"));
    Sound oggSound7 = Gdx.audio.newSound(Gdx.files.internal("audio/powerUp5.ogg"));
    Sound oggSound8= Gdx.audio.newSound(Gdx.files.internal("audio/powerUp6.ogg"));
    Sound oggSound9 = Gdx.audio.newSound(Gdx.files.internal("audio/powerUp7.ogg"));
    Sound oggSound10 = Gdx.audio.newSound(Gdx.files.internal("audio/powerUp8.ogg"));

    Sound laser1 = Gdx.audio.newSound(Gdx.files.internal("audio/Laser/laser1.ogg"));
    Sound laser2 = Gdx.audio.newSound(Gdx.files.internal("audio/Laser/laser2.ogg"));
    Sound laser3 = Gdx.audio.newSound(Gdx.files.internal("audio/Laser/laser3.ogg"));
    Sound laser4 = Gdx.audio.newSound(Gdx.files.internal("audio/Laser/laser4.ogg"));
    Sound laser5 = Gdx.audio.newSound(Gdx.files.internal("audio/Laser/laser5.ogg"));
    Sound laser6 = Gdx.audio.newSound(Gdx.files.internal("audio/Laser/laser6.ogg"));
    Sound laser7 = Gdx.audio.newSound(Gdx.files.internal("audio/Laser/laser7.ogg"));
    Sound laser8 = Gdx.audio.newSound(Gdx.files.internal("audio/Laser/laser8.ogg"));
    Sound laser9 = Gdx.audio.newSound(Gdx.files.internal("audio/Laser/laser9.ogg"));

    double timeSinceLastFire = 0;

    public Cannon(Texture tex) {
        super(tex);
        double rand = Math.random();
        if ( rand < 0.1f)
            oggSound3.play();
        else if (rand < 0.3f)
            oggSound3.play();
        else if (rand < 0.4f)
            oggSound4.play();
        else if (rand < 0.5f)
            oggSound5.play();
        else if (rand < 0.6f)
            oggSound6.play();
        else if (rand < 0.7f)
            oggSound7.play();
        else if (rand < 0.8f)
            oggSound8.play();
        else if (rand < 0.9f)
            oggSound9.play();
        else if (rand < 1.0f)
            oggSound10.play();

    }

    public void Update(float deltaTime) {
        /// TODO: Acquire target, fire a projectile, idle
        if (TimeUtils.millis() - timeSinceLastFire > 1000) {
            Fire();
            timeSinceLastFire = TimeUtils.millis();
        }
    }

    private void Fire() {
        double rand = Math.random();
        if ( rand < 0.1f)
            laser1.play();
        else if (rand < 0.3f)
            laser2.play();
        else if (rand < 0.4f)
            laser3.play();
        else if (rand < 0.5f)
            laser4.play();
        else if (rand < 0.6f)
            laser5.play();
        else if (rand < 0.7f)
            laser6.play();
        else if (rand < 0.8f)
            laser7.play();
        else if (rand < 0.9f)
            laser8.play();
        else if (rand < 1.0f)
            laser9.play();
    }
}
