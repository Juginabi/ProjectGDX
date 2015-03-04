package com.juginabi.towerdefence.GameEntities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Jukka on 3.3.2015.
 */
public class PencilNeckedGeek extends DynamicEntity {
    boolean C1reached = false;
    boolean C2reached = false;
    boolean C3reached = false;
    boolean C4reached = false;
    boolean C5reached = false;
    boolean C6reached = false;

    public PencilNeckedGeek(Texture entityTexture) {
        super(entityTexture);
        this.setVelocity(128f);
        this.setHitPoints(10f);
        this.setHeading(new Vector2(0,-1));
    }

    public void Update(float deltaTime) {
        // Move the attacker
        if (!C1reached && posIsCloseTo(4*64, 3*64)) {
            C1reached = true;
            setHeading(new Vector2(1,0));
        } else if (!C2reached && posIsCloseTo(11*64, 3*64)) {
            C2reached = true;
            setHeading(new Vector2(0,1));
        } else if (!C3reached && posIsCloseTo(11*64, 13*64)) {
            C3reached = true;
            setHeading(new Vector2(1,0));
        } else if (!C4reached && posIsCloseTo(19*64, 13*64)) {
            C4reached = true;
            setHeading(new Vector2(0,-1));
        } else if (!C5reached && posIsCloseTo(19*64, 3*64)) {
            C5reached = true;
            setHeading(new Vector2(1,0));
        } else if (!C6reached && posIsCloseTo(26*64, 3*64)) {
            C6reached = true;
            setHeading(new Vector2(0,1));
        }
        Vector2 heading = this.getHeading();
        float velocity = this.getVelocity();
        Vector2 deltaMovement = new Vector2(heading.x * velocity * deltaTime, heading.y * velocity * deltaTime);
        float x = super.getX();
        float y = super.getY();
        super.setPosition(x+deltaMovement.x, y+deltaMovement.y);
    }

    private boolean posIsCloseTo(float x, float y) {
        float myPosX = getX();
        float myPosY = getY();

        float distanceX = myPosX - x;
        float distanceY = myPosY - y;
        double dist = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY,2));

        if (dist < 10)
            return true;
        else
            return false;
    }
}
