package com.juginabi.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.juginabi.towerdefence.GameEntities.DynamicEntity;

/**
 * Created by Jukka on 21.8.2015.
 */
public class PhysicsWorld {
    public static final String TAG = "PhysicsWorld";
    // PhysicsWorld
    public World world_;

    // Constants
    private int VELOCITY_ITERATIONS = 6;
    private int POSITION_ITERATIONS = 2;
    private float TIME_STEP = 0.008f;

    // Accumulator used in physics step
    private float accumulator_ = 0;

    // Debug renderer if any
    Box2DDebugRenderer debugRenderer_;
boolean debugRenderingEnabled_ = false;

    public PhysicsWorld(Vector2 gravityVector, boolean sleepingObjects, boolean enableDebugRendering) {
        world_ = new World(gravityVector, sleepingObjects);
        if (enableDebugRendering) {
            debugRenderer_ = new Box2DDebugRenderer();
            debugRenderingEnabled_ = true;
        }

        world_.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                DynamicEntity e = (DynamicEntity)contact.getFixtureA().getBody().getUserData();
                if (e != null) {
                    if (contact.getFixtureB().getFilterData().categoryBits == 0x0A) {
                        // This is heading sensor
                        e.heading = (Vector2)contact.getFixtureB().getUserData();
                    } else if (contact.getFixtureB().getFilterData().categoryBits == 0x0B) {
                        // This is finishline sensor
                        e.timeOfDeath = TimeUtils.millis();
                        e.isAlive = false;
                    }

                } else {
                    e = (DynamicEntity)contact.getFixtureB().getBody().getUserData();
                    if (contact.getFixtureA().getFilterData().categoryBits == 0x0A) {
                        // This is heading sensor
                        e.heading = (Vector2)contact.getFixtureA().getUserData();
                    } else if (contact.getFixtureA().getFilterData().categoryBits == 0x0B) {
                        // This is finishline sensor
                        e.isAlive = false;
                        e.timeOfDeath = TimeUtils.millis();
                    }

                }
            }

            @Override
            public void endContact(Contact contact) {
                DynamicEntity e = (DynamicEntity)contact.getFixtureA().getBody().getUserData();

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });

        createCheckpointSensor(4.5f, 1.5f, new Vector2(1,0));
        createCheckpointSensor(11.5f,1.5f, new Vector2(0,1));
        createCheckpointSensor(4.5f,9.5f, new Vector2(0,-1));
        createFinishlineSensor(11.5f,9.5f);
    }

    public void setVelocityIterations(int iterations) {
        this. VELOCITY_ITERATIONS = iterations;
    }

    public void setPositionIterations(int iterations) {
        this.POSITION_ITERATIONS = iterations;
    }

    public void doPhysicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator_ += frameTime;
        while (accumulator_ >= this.TIME_STEP) {
            world_.step(this.TIME_STEP, this.VELOCITY_ITERATIONS, this.POSITION_ITERATIONS);
            accumulator_ -= this.TIME_STEP;
        }
    }

    public void render(OrthographicCamera camera) {
        if (debugRenderingEnabled_) {
            debugRenderer_.render(world_, camera.combined);
        }
    }

    public void createCheckpointSensor(float x, float y, Vector2 heading) {
        // First we create a body definition
        BodyDef bodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // Set our body's starting position in the world
        bodyDef.position.set(x, y);

        // Create our body in the world using our body definition
        Body body = world_.createBody(bodyDef);

        // Create a circle shape and set its radius to 6
        CircleShape circle = new CircleShape();
        circle.setRadius(0.25f);

        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = 0x0A;

        // Create our fixture and attach it to the body
        Fixture fix = body.createFixture(fixtureDef);
        fix.setUserData(heading);

        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        circle.dispose();
    }

    public void createFinishlineSensor(float x, float y) {
        // First we create a body definition
        BodyDef bodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // Set our body's starting position in the world
        bodyDef.position.set(x, y);

        // Create our body in the world using our body definition
        Body body = world_.createBody(bodyDef);

        // Create a circle shape and set its radius to 6
        CircleShape circle = new CircleShape();
        circle.setRadius(0.25f);

        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = 0x0B;

        // Create our fixture and attach it to the body
        Fixture fix = body.createFixture(fixtureDef);

        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        circle.dispose();
    }
}
