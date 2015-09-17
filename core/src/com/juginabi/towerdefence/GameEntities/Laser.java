package com.juginabi.towerdefence.GameEntities;

/**
 * Laser Class
 * @author Trenton Shaffer Initial working implementation.
 * @author Jukka Vatjus-Anttila Modifications to match structure with TowerDefence class.
 * Also made setter and getter methods and all the attribute variables are now private.
 * Removed requirement of spritebatch through static method. Uses Batch from TiledMapRenderer or whatnot.
 * Changed origins of mid and end sections of lasers so rotation now revolves around begin1 origin accurately.
 */
public class Laser /*extends DynamicEntity*/ {
    /*private Vector2 position;
    private float distance;
    private Color beamColor;
    private Color rayColor;
    private float degrees;
    private Sprite begin1,begin2,mid1,mid2,end1,end2;

    private float lifeTime;
    private float totalTime;

    public Laser(GameWorld parent, TextureAtlas.AtlasRegion[] tex) {
        super(parent, tex[0], GameWorld.ProjectileLaser);

        this.begin1 = new Sprite(tex[0]);
        this.begin2 = new Sprite(tex[1]);
        this.mid1 = new Sprite(tex[2]);
        this.mid2 = new Sprite(tex[3]);
        this.end1 = new Sprite(tex[4]);
        this.end2 = new Sprite(tex[5]);

        this.totalTime = 0f;
        position = new Vector2();
    }


    @Override
    public boolean Update(float tickMilliseconds) {
        if (!isAlive_)
            return false;
        this.totalTime += tickMilliseconds;
        if (this.totalTime > this.lifeTime)
            SetStatusAlive(false);
        else {
            // TODO: What should laser do
        }
        return true;
    }

    @Override
    public void initialize(float x, float y) {
        setPosition(x, y);
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
        mid1.setSize(mid1.getWidth(), distance);
        mid2.setSize(mid1.getWidth(), distance);

        // Stack up sprites and make laser complete
        begin1.setPosition(position.x, position.y);
        begin2.setPosition(position.x, position.y);
        mid1.setPosition(begin1.getX(), begin1.getY()+begin1.getHeight());
        mid2.setPosition(begin1.getX(), begin1.getY()+begin1.getHeight());
        end1.setPosition(begin1.getX(), begin1.getY()+begin1.getHeight()+mid1.getHeight());
        end2.setPosition(begin1.getX(), begin1.getY()+begin1.getHeight()+mid1.getHeight());

        // Rotation correctly around begin1 origin
        begin1.setOrigin(begin1.getWidth()/2, begin1.getHeight()/2);
        begin2.setOrigin(begin1.getWidth()/2, begin1.getHeight()/2);
        mid1.setOrigin(mid1.getWidth()/2, -begin1.getHeight()/2);
        mid2.setOrigin(mid2.getWidth()/2, -begin1.getHeight()/2);
        end1.setOrigin(mid1.getWidth()/2, -begin1.getHeight()/2-mid1.getHeight());
        end2.setOrigin(mid2.getWidth()/2, -begin1.getHeight()/2-mid2.getHeight());

        // Set absolute rotation value
        begin1.setRotation(degrees);
        begin2.setRotation(degrees);
        mid1.setRotation(degrees);
        mid2.setRotation(degrees);
        end1.setRotation(degrees);
        end2.setRotation(degrees);

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
    }*/
}