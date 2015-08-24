package com.juginabi.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Jukka on 21.8.2015.
 */
public class PhysicsWorld {
    // PhysicsWorld
    private World world_;

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

    public void createDynamicBody(float x, float y) {
        // First we create a body definition
        BodyDef bodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.DynamicBody;
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
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f; // Make it bounce a little bit

        // Create our fixture and attach it to the body
        Fixture fixture = body.createFixture(fixtureDef);

        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        circle.dispose();
    }
}
