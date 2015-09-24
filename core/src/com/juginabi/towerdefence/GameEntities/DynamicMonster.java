package com.juginabi.towerdefence.GameEntities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.TimeUtils;
import com.juginabi.towerdefence.GameWorld;
import com.juginabi.towerdefence.PhysicsWorld;
import com.juginabi.towerdefence.helpers.EntityInitializer;

/**
 * Created by Jukka on 3.3.2015.
 */
public class DynamicMonster extends GameEntity {
    private static final String TAG = "DynamicMonster";
    private final GameWorld gameWorld;
    private final PhysicsWorld physicsWorld;
    private Vector2 target;
    public float velocity;
    public float hitpoints;
    private long deathAnimationTime = 1000;
    public long timeOfDeath = 0;
    private Vector2 headingImpulse;

    // Animations; down, up, left, right
    Animation[] walkAnimations = new Animation[4];
    Animation[] deathAnimations = new Animation[4];
    TextureRegion[] idleFrames = new TextureRegion[4];
    // Current frame of animation to be displayed. Update loop updates this
    TextureRegion currentFrame;
    // Animation state time which is used to pick correct key frame from animation
    private float stateTime = 0;

    public DynamicMonster(GameWorld gameWorld, PhysicsWorld physicsWorld, EntityInitializer initData) {
        super(initData.idleFrames[0]);
        this.gameWorld = gameWorld;
        this.physicsWorld = physicsWorld;
        this.walkAnimations = initData.walkAnimations;
        this.deathAnimations = initData.deathAnimations;
        this.idleFrames = initData.idleFrames;
        this.target = new Vector2(0, 0);
        this.hitpoints = initData.hitpoints;
        this.velocity = initData.velocity;
        this.typeId = initData.id;
        this.currentFrame = idleFrames[0];
        this.headingImpulse = new Vector2(0,0);
    }

    @Override
    public void initialize(float posX, float posY) {
        this.isAlive = true;
        this.removeThisEntity = false;
        this.stateTime = 0f;
        this.hitpoints = 50;

        // First we create a body definition
        BodyDef bodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set our body's starting position in the world
        bodyDef.position.set(posX+0.5f, posY+0.5f);
        // Create our body in the world using our body definition
        body = physicsWorld.world_.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.3f, 0.4f);
        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.density = 0.8f;
        fixture.friction = 0.4f;
        fixture.restitution = 0.6f;
        fixture.filter.categoryBits = PhysicsWorld.ENTITY_ENEMY;
        fixture.filter.maskBits = PhysicsWorld.SENSOR_NAVIGATION | PhysicsWorld.SENSOR_GOAL | PhysicsWorld.ENTITY_DEFENDER_SPOTTING_SENSOR | PhysicsWorld.ENTITY_DEFENDER_FIRE_SENSOR;
        body.createFixture(fixture);
        shape.dispose();

        body.setUserData(this);
        body.setLinearDamping(1.3f);

        this.setBounds(posX, posY, 1, 1);
        this.setOriginCenter();
    }

    @Override
    public void Update(float tickMilliseconds) {
        if (hitpoints <= 0 && isAlive) {
            isAlive = false;
            timeOfDeath = TimeUtils.millis();
        }
        stateTime += tickMilliseconds;
        if (isAlive) {
            Vector2 linear = body.getLinearVelocity();
            if (linear.y < -0.5f) {
                currentFrame = walkAnimations[0].getKeyFrame(stateTime, true);
            } else if (linear.y > 0.5f) {
                currentFrame = walkAnimations[1].getKeyFrame(stateTime, true);
            } else if (linear.x < -0.5f) {
                currentFrame = walkAnimations[2].getKeyFrame(stateTime, true);
            } else if (linear.x > 0.5f) {
                currentFrame = walkAnimations[3].getKeyFrame(stateTime, true);
            }
            if (body != null) {
                this.headingImpulse.x = target.x - body.getPosition().x;
                this.headingImpulse.y = target.y - body.getPosition().y;
                body.applyForce(this.headingImpulse.nor(), body.getWorldCenter(), true);
                setX(body.getPosition().x - 0.5f);
                setY(body.getPosition().y - 0.25f);
                //Gdx.app.log(TAG, "My sprite pos: " + getX() + ", " + getY());
            }
        } else if (!isAlive && TimeUtils.millis() - timeOfDeath < deathAnimationTime) {
            currentFrame = deathAnimations[0].getKeyFrame(stateTime, true);
        } else {
            removeThisEntity = true;
            setX(-1f);
            setY(-1f);
        }
    }

    @Override
    public void Draw(Batch batch) {
        this.setRegion(currentFrame);
        //Gdx.app.log(TAG, "Drawing dynamic entity: " + getX() + ", " + getY());
        super.draw(batch);
    }

    public void setTarget(Vector2 vec) {
        this.target = vec;
    }
}
