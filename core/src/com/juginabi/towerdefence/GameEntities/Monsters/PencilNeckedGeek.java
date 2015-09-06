package com.juginabi.towerdefence.GameEntities.Monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.juginabi.towerdefence.GameEntities.DynamicEntity;
import com.juginabi.towerdefence.GameWorld;

import java.util.concurrent.TimeUnit;

/**
 * Created by Jukka on 3.3.2015.
 */
public class PencilNeckedGeek extends DynamicEntity {
    private boolean C1reached = false;
    private boolean C2reached = false;
    private boolean C3reached = false;
    private boolean C4reached = false;
    private boolean C5reached = false;
    private boolean C6reached = false;
    private boolean C7reached = false;
    private long birthTime;

    public PencilNeckedGeek(GameWorld parent, TextureAtlas.AtlasRegion entityTexture) {
        super(parent, entityTexture, GameWorld.EnemyGeek);
        this.setVelocity(1f);
        this.setHitPoints(10f);
        this.setHeading(0,-1);
    }

    public void Initialize() {
        C1reached = false;
        C2reached = false;
        C3reached = false;
        C4reached = false;
        C5reached = false;
        C6reached = false;
        C7reached = false;
        this.setVelocity(1f);
        this.setHitPoints(50f);
        this.setHeading(0,-1);
        this.setBounds(4, 17, 1, 1);
        isAlive_ = true;
        this.birthTime = TimeUtils.millis();
    }

    @Override
    public boolean Update(float deltaTime) {
        if (getHitPoints() < 0f)
            isAlive_ = false;
        if (TimeUtils.millis() - birthTime > 10000) {
            isAlive_ = false;
            setPosition(-1, -1);
        }
        if (!isAlive_)
            return false;
        // Move the attacker
        if (!C1reached && posIsCloseTo(4, 3, 12)) {
            C1reached = true;
            setHeading(1,0);
        } else if (!C2reached && posIsCloseTo(11, 3, 12)) {
            C2reached = true;
            setHeading(0,1);
        } else if (!C3reached && posIsCloseTo(11, 13, 12)) {
            C3reached = true;
            setHeading(1,0);
        } else if (!C4reached && posIsCloseTo(19, 13, 12)) {
            C4reached = true;
            setHeading(0,-1);
        } else if (!C5reached && posIsCloseTo(19, 3, 12)) {
            C5reached = true;
            setHeading(1,0);
        } else if (!C6reached && posIsCloseTo(26, 3, 12)) {
            C6reached = true;
            setHeading(0,1);
        } else if (!C7reached && posIsCloseTo(26, 17, 20)) {
            C1reached = false;
            C2reached = false;
            C3reached = false;
            C4reached = false;
            C5reached = false;
            C6reached = false;
            C7reached = false;
            setPosition(4, 17);
            setHeading(0, -1);
        }
        Vector2 heading = this.getHeading();
        float velocity = this.getVelocity();
        float deltaMovementX = heading.x * velocity * deltaTime;
        float deltaMovementY = heading.y * velocity * deltaTime;
        float x = super.getX();
        float y = super.getY();
        super.setPosition(x+deltaMovementX, y+deltaMovementY);
        return true;
    }

    private boolean posIsCloseTo(float x, float y, float distance) {
        float myPosX = getX();
        float myPosY = getY();

        float distanceX = myPosX - x;
        float distanceY = myPosY - y;
        float dist = (float) Math.sqrt(distanceX*distanceX + distanceY*distanceY);
        boolean returnValue = dist < 0.05;
        return returnValue;
    }

    @Override
    public void Draw(Batch batch) {
        if (!isAlive_)
            return;
        super.draw(batch);
    }
}
