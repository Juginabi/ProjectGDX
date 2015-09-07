package com.juginabi.towerdefence.GameEntities.Monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.juginabi.towerdefence.GameEntities.DynamicEntity;
import com.juginabi.towerdefence.GameWorld;
import com.juginabi.towerdefence.TowerDefence;

/**
 * Created by Juginabi on 07.03.2015.
 */
public class JesseMonster extends DynamicEntity {
    private static String TAG = "JesseMonster";

    private boolean C1reached = false;
    private boolean C2reached = false;
    private boolean C3reached = false;
    private boolean C4reached = false;
    private boolean C5reached = false;
    private boolean C6reached = false;
    private boolean C7reached = false;

    Body physicsBody;

    @Override
    public boolean Update(float deltaTime) {
        if (getHitPoints() < 0f)
            isAlive_ = false;
        if (!isAlive_)
            return false;
        // Move the attacker
        if (!C1reached) {
            if (posIsCloseTo(4, 3, 12)) {
                C1reached = true;
                setHeading(1, 0);
            }
        } else if (!C2reached) {
            if (posIsCloseTo(11, 3, 12)) {
                C2reached = true;
                setHeading(0, 1);
            }
        } else if (!C3reached) {
            if (posIsCloseTo(11, 13, 12)) {
                C3reached = true;
                setHeading(1, 0);
            }
        } else if (!C4reached) {
            if (posIsCloseTo(19, 13, 12)) {
                C4reached = true;
                setHeading(0, -1);
            }
        } else if (!C5reached) {
            if (posIsCloseTo(19, 3, 12)) {
                C5reached = true;
                setHeading(1, 0);
            }
        } else if (!C6reached) {
            if (posIsCloseTo(26, 3, 12)) {
                C6reached = true;
                setHeading(0, 1);
            }
        } else if (!C7reached) {
            if (posIsCloseTo(26, 17, 20)) {
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
        }
        /*Vector2 heading = this.getHeading();
        float velocity = this.getVelocity();
        float deltaMovementX = heading.x * velocity * deltaTime;
        float deltaMovementY = heading.y * velocity * deltaTime;
        float x = super.getX();
        float y = super.getY();
        super.setPosition(x+deltaMovementX, y+deltaMovementY);*/
        return true;
    }

    @Override
    public void initialize(float x, float y) {
        C1reached = false;
        C2reached = false;
        C3reached = false;
        C4reached = false;
        C5reached = false;
        C6reached = false;
        C7reached = false;
        this.setVelocity(1f);
        this.setHitPoints(500f);
        this.setHeading(0,-1);
        this.setBounds(x,y,1,1);
        this.SetStatusAlive(true);

        if (physicsBody == null) {
            // First we create a body definition
            BodyDef bodyDef = new BodyDef();
            // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            // Set our body's starting position in the world
            bodyDef.position.set(x+0.5f, y+0.5f);

            // Create our body in the world using our body definition
            physicsBody = TowerDefence.physicsWorld_.world_.createBody(bodyDef);

            CircleShape circle = new CircleShape();
            circle.setRadius(0.5f);
            FixtureDef fixture = new FixtureDef();
            fixture.shape = circle;
            fixture.density = 0.5f;
            fixture.friction = 0.4f;
            fixture.restitution = 0.6f; // Make it bounce a little bit
            physicsBody.createFixture(fixture);
        }
        setOriginCenter();

        physicsBody.setUserData(this);
        //CircleShape circle = new CircleShape();
        //circle.setRadius(2.5f);

        /*FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = (short) 0xFFFF;
        fixtureDef.filter.maskBits = (short) 0xFFFF;
        physicsBody.createFixture(fixtureDef);*/

        //TowerDefence.physicsWorld_.createDynamicBody(4, 17);
    }

    public JesseMonster(GameWorld parent, TextureAtlas.AtlasRegion entityTexture) {
        super(parent, entityTexture, GameWorld.EnemyJesse);
        this.setVelocity(1f);
        this.setHitPoints(10f);
        this.setHeading(0,-1);
    }

    private boolean posIsCloseTo(float x, float y, float distance) {
        float myPosX = getX();
        float myPosY = getY();

        float distanceX = myPosX - x;
        float distanceY = myPosY - y;
        float dist = (float) Math.sqrt(distanceX*distanceX + distanceY*distanceY);
        boolean returnValue = dist < 0.1;
        return returnValue;
    }

    @Override
    public void Draw(Batch batch) {
        if (!IsAlive())
            return;
        super.draw(batch);
    }
}
