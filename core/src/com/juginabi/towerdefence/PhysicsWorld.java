package com.juginabi.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.juginabi.towerdefence.GameEntities.DynamicDefender;
import com.juginabi.towerdefence.GameEntities.DynamicMonster;
import com.juginabi.towerdefence.GameEntities.GameEntity;

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
    // Bit masks
    public static final short
            SENSOR_NAVIGATION = 0x0001,
            SENSOR_GOAL = 0x0002,
            ENTITY_ENEMY = 0x0004,
            ENTITY_DEFENDER = 0x0008,
            ENTITY_DEFENDER_SENSOR = 0x0010,
            ENTITY_ILLEGAL_BUILD_SPOT = 0x0020;
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
                Fixture fixA = contact.getFixtureA();
                Fixture fixB = contact.getFixtureB();
                switch (fixA.getFilterData().categoryBits) {
                    case SENSOR_NAVIGATION:
                        if (fixB.getFilterData().categoryBits == ENTITY_ENEMY) {
                            DynamicMonster monster = (DynamicMonster)fixB.getBody().getUserData();
                            Vector2 newTarget = (Vector2)fixA.getUserData();
                            monster.setTarget(newTarget);
                        }
                        break;
                    case SENSOR_GOAL:
                        if (fixB.getFilterData().categoryBits == ENTITY_ENEMY) {
                            DynamicMonster monster = (DynamicMonster)fixB.getBody().getUserData();
                            monster.timeOfDeath = TimeUtils.millis();
                            monster.isAlive = false;
                        }
                        break;
                    case ENTITY_ENEMY:
                        DynamicMonster monster = (DynamicMonster)fixA.getBody().getUserData();
                        if (fixB.getFilterData().categoryBits == SENSOR_NAVIGATION) {
                            Vector2 newTarget = (Vector2)fixB.getUserData();
                            monster.setTarget(newTarget);
                        } else if (fixB.getFilterData().categoryBits == SENSOR_GOAL) {
                            monster.timeOfDeath = TimeUtils.millis();
                            monster.isAlive = false;
                        } else if (fixB.getFilterData().categoryBits == ENTITY_DEFENDER_SENSOR) {
                            DynamicDefender defender = (DynamicDefender) fixB.getBody().getUserData();
                            defender.addTarget(fixA.getBody());
                        }
                        break;
                    case ENTITY_DEFENDER_SENSOR:
                        if (fixB.getFilterData().categoryBits == ENTITY_ENEMY) {
                            DynamicDefender defender = (DynamicDefender) fixA.getBody().getUserData();
                            defender.addTarget(fixB.getBody());
                        }
                        break;
                }
            }

            @Override
            public void endContact(Contact contact) {
                Fixture fixA = contact.getFixtureA();
                Fixture fixB = contact.getFixtureB();
                switch (fixA.getFilterData().categoryBits) {
                    case SENSOR_NAVIGATION:
                        if (fixB.getFilterData().categoryBits == ENTITY_ENEMY) {

                        }
                        break;
                    case SENSOR_GOAL:
                        if (fixB.getFilterData().categoryBits == ENTITY_ENEMY) {

                        }
                        break;
                    case ENTITY_ENEMY:
                        DynamicMonster monster = (DynamicMonster)fixA.getBody().getUserData();
                        if (fixB.getFilterData().categoryBits == SENSOR_NAVIGATION) {

                        } else if (fixB.getFilterData().categoryBits == SENSOR_GOAL) {

                        } else if (fixB.getFilterData().categoryBits == ENTITY_DEFENDER_SENSOR) {
                            DynamicDefender defender = (DynamicDefender) fixB.getBody().getUserData();
                            defender.removeBody(fixA.getBody());
                        }
                        break;
                    case ENTITY_DEFENDER_SENSOR:
                        if (fixB.getFilterData().categoryBits == ENTITY_ENEMY) {
                            DynamicDefender defender = (DynamicDefender) fixA.getBody().getUserData();
                            defender.removeBody(fixB.getBody());
                        }
                        break;
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });

        createCheckpointSensor(4.5f, 9.5f, new Vector2(4.5f, 1.5f));
        createCheckpointSensor(4.5f, 1.5f, new Vector2(11.5f, 1.5f));
        createCheckpointSensor(11.5f,1.5f, new Vector2(11.5f,9.5f));
        createFinishlineSensor(11.5f, 9.5f);
        createIllegalBuildArea();
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

    public void createIllegalBuildArea() {
        // First we create a body definition
        BodyDef bodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // Set our body's starting position in the world
        bodyDef.position.set(4.5f, 5f);
        // Create our body in the world using our body definition
        Body body = world_.createBody(bodyDef);
        // Create a circle shape and set its radius to 6
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.65f,4.2f);
        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = ENTITY_ILLEGAL_BUILD_SPOT;
        // Create our fixture and attach it to the body
        Fixture fix = body.createFixture(fixtureDef);
        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        shape.dispose();

        // First we create a body definition
        BodyDef bodyDef2 = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef2.type = BodyDef.BodyType.StaticBody;
        // Set our body's starting position in the world
        bodyDef2.position.set(8f, 1.5f);
        // Create our body in the world using our body definition
        Body body2 = world_.createBody(bodyDef2);
        // Create a circle shape and set its radius to 6
        PolygonShape shape2 = new PolygonShape();
        shape2.setAsBox(4.2f,0.65f);
        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = shape2;
        fixtureDef2.filter.categoryBits = ENTITY_ILLEGAL_BUILD_SPOT;
        // Create our fixture and attach it to the body
        body2.createFixture(fixtureDef2);
        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        shape2.dispose();

        // First we create a body definition
        BodyDef bodyDef3 = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef3.type = BodyDef.BodyType.StaticBody;
        // Set our body's starting position in the world
        bodyDef3.position.set(11.5f, 5f);
        // Create our body in the world using our body definition
        Body body3 = world_.createBody(bodyDef3);
        // Create a circle shape and set its radius to 6
        PolygonShape shape3 = new PolygonShape();
        shape3.setAsBox(0.65f,4.2f);
        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef3 = new FixtureDef();
        fixtureDef3.shape = shape;
        fixtureDef3.filter.categoryBits = ENTITY_ILLEGAL_BUILD_SPOT;
        // Create our fixture and attach it to the body
        body3.createFixture(fixtureDef3);
        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        shape3.dispose();
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
        fixtureDef.filter.maskBits = ENTITY_ENEMY;
        fixtureDef.filter.categoryBits = SENSOR_NAVIGATION;
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
        fixtureDef.filter.categoryBits = SENSOR_GOAL;
        // Create our fixture and attach it to the body
        Fixture fix = body.createFixture(fixtureDef);
        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        circle.dispose();
    }
}
