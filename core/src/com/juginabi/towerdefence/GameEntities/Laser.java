package com.juginabi.towerdefence.GameEntities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

/**
 * Laser Class
 * @author Trenton Shaffer Initial working implementation.
 * @author Jukka Vatjus-Anttila Modifications to match structure with TowerDefence class.
 * Also made setter and getter methods and all the attribute variables are now private.
 * Removed requirement of spritebatch through static method. Uses Batch from TiledMapRenderer or whatnot.
 * Changed origins of mid and end sections of lasers so rotation now revolves around begin1 origin accurately.
 */
public class Laser {
    private boolean isAlive_;
    private Vector2 position;
    private float distance;
    private Color beamColor;
    private Color rayColor;
    private float degrees;
    private Sprite begin1,begin2,mid1,mid2,end1,end2;

    private float lifeTime = 1;
    private float totalTime;

    public Laser(float x, float y) {

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("Graphics/laseratlas.atlas.txt"));
        this.begin1 = new Sprite(atlas.findRegion("beamstart1"));
        this.begin2 = new Sprite(atlas.findRegion("beamstart2"));
        this.mid1 = new Sprite(atlas.findRegion("beammid1"));
        this.mid2 = new Sprite(atlas.findRegion("beammid2"));
        this.end1 = new Sprite(atlas.findRegion("beamend1"));
        this.end2 = new Sprite(atlas.findRegion("beamend2"));

        this.totalTime = 0f;
        position = new Vector2(x, y);
        isAlive_ = true;
    }


    public boolean Update(float tickMilliseconds) {
        if (!isAlive_)
            return false;
        this.totalTime += tickMilliseconds;
        if (this.totalTime > this.lifeTime)
            isAlive_ = false;
        else {
            // TODO: What should laser do
        }
        return true;
    }

    public void Draw(Batch batch) {
        if (!isAlive_)
            return;
        // Set color of sprites
        float alpha = 1*(float)Math.pow(0.5f, totalTime*5);
        beamColor.a = alpha;
        rayColor.a = alpha;
        begin1.setColor(beamColor);
        begin2.setColor(rayColor);
        mid1.setColor(beamColor);
        mid2.setColor(rayColor);
        end1.setColor(beamColor);
        end2.setColor(rayColor);

        // Scale mid-sprites according to distance. This is the length of laser.
        /*mid1.setSize(mid1.getWidth(), distance);
        mid2.setSize(mid1.getWidth(), distance);*/

        // Rotation correctly around begin1 origin
        begin1.setOrigin(begin1.getWidth() / 2, begin1.getHeight() / 2);
        begin2.setOrigin(begin1.getWidth() / 2, begin1.getHeight() / 2);
        mid1.setOrigin(mid1.getWidth() / 2, -begin1.getHeight() / 2);
        mid2.setOrigin(mid2.getWidth() / 2, -begin1.getHeight() / 2);
        end1.setOrigin(mid1.getWidth() / 2, -begin1.getHeight() / 2 - mid1.getHeight());
        end2.setOrigin(mid2.getWidth() / 2, -begin1.getHeight() / 2 - mid2.getHeight());

        // Set absolute rotation value
        begin1.setRotation(degrees);
        begin2.setRotation(degrees);
        mid1.setRotation(degrees);
        mid2.setRotation(degrees);
        end1.setRotation(degrees);
        end2.setRotation(degrees);

        float laserSize = 1f;
        // Stack up sprites and make laser complete
        begin1.setBounds(position.x - begin1.getWidth() / 2, position.y - begin1.getHeight() / 2, laserSize, laserSize);
        begin2.setBounds(position.x - begin2.getWidth() / 2, position.y - begin2.getHeight() / 2, laserSize, laserSize);
        mid1.setBounds(begin1.getX(), begin1.getY() + begin1.getHeight(), laserSize, distance);
        mid2.setBounds(begin2.getX(), begin2.getY() + begin2.getHeight(), laserSize, distance);
        end1.setBounds(begin1.getX(), begin1.getY() + begin1.getHeight() + mid1.getHeight(), laserSize, laserSize);
        end2.setBounds(begin2.getX(), begin2.getY() + begin2.getHeight() + mid1.getHeight(), laserSize, laserSize);

        // Draw using batch
        begin1.draw(batch);
        begin2.draw(batch);
        mid1.draw(batch);
        mid2.draw(batch);
        end1.draw(batch);
        end2.draw(batch);
    }

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }

    public Vector2 getPosition() {
        return this.position;
    }

    public void setDistance(float distance) {
        if (distance < 0)
            distance = 0;
        this.distance = distance;
    }

    public float getDistance() {
        return this.distance;
    }

    public void setBeamColor(Color color) {
        this.beamColor = color;
    }

    public Color getBeamColor() {
        return this.beamColor;
    }

    public void setRayColor(Color rayColor) {
        this.rayColor = rayColor;
    }

    public void setDegrees(float degrees) {
        this.degrees = degrees;
    }

    public float getDegrees() {
        return this.degrees;
    }

    public void setLifeTime(float time) {
        this.lifeTime = time;
    }

    public void resetTotalTime() {
        this.totalTime = 0f;
    }
}