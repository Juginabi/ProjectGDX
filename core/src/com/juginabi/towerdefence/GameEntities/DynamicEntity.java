package com.juginabi.towerdefence.GameEntities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.juginabi.towerdefence.GameWorld;
import com.juginabi.towerdefence.PhysicsWorld;
import com.juginabi.towerdefence.TowerDefence;

import java.util.concurrent.TimeUnit;

/**
 * Created by Jukka on 3.3.2015.
 */
public class DynamicEntity extends Sprite {
    public final GameWorld gameWorld;
    public final PhysicsWorld physicsWorld;
    public Vector2 heading;
    public float velocity;
    public float hitpoints;
    public int type;
    public boolean isAlive;
    private long deathAnimationTime = 1000;
    public long timeOfDeath = 0;
    public Body body;
    public boolean removeThisEntity = false;

    // Animations; down, up, left, right
    Animation[] walkAnimations = new Animation[4];
    Animation[] deathAnimations = new Animation[4];
    TextureRegion[] idleFrames = new TextureRegion[4];
    // Sprite sheet contains all animation frames for down, up, left, right
    // For every row; Frame1: idle, Frame2-7: move, Frame 8-11: death
    Texture spriteSheet;
    // Current frame of animation to be displayed. Update loop updates this
    TextureRegion currentFrame;
    // Animation state time which is used to pick correct key frame from animation
    private float stateTime = 0;

    public DynamicEntity(GameWorld gameWorld, PhysicsWorld physicsWorld, EntityInitializer initData) {
        this.gameWorld = gameWorld;
        this.physicsWorld = physicsWorld;
        this.walkAnimations = initData.walkAnimations;
        this.deathAnimations = initData.deathAnimations;
        this.idleFrames = initData.idleFrames;
        this.heading = new Vector2(0, 0);
        this.hitpoints = initData.hitpoints;
        this.velocity = initData.velocity;
        this.stateTime = 0f;
        this.currentFrame = idleFrames[0];
    }

    public void initialize(Vector2 position) {
        this.isAlive = true;
        this.removeThisEntity = false;
        if (body == null) {
            // First we create a body definition
            BodyDef bodyDef = new BodyDef();
            // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            // Set our body's starting position in the world
            bodyDef.position.set(position.x+0.5f, position.y+0.5f);

            // Create our body in the world using our body definition
            body = physicsWorld.world_.createBody(bodyDef);

            CircleShape circle = new CircleShape();
            circle.setRadius(0.5f);
            FixtureDef fixture = new FixtureDef();
            fixture.shape = circle;
            fixture.density = 0.5f;
            fixture.friction = 0.4f;
            fixture.restitution = 0.6f; // Make it bounce a little bit
            fixture.filter.categoryBits = 0x10;
            fixture.filter.maskBits = 0x0A | 0x0B;
            body.createFixture(fixture);
        }
        body.setLinearDamping(0.2f);
        body.setUserData(this);
        body.setLinearDamping(1.3f);

        this.setBounds(position.x, position.y, 1, 1);
        this.setOriginCenter();
    }

    public void Update(float tickMilliseconds) {
        stateTime += tickMilliseconds;
        if (isAlive) {
            if (heading.y != 0) {
                if (heading.y < 0) {
                    if (velocity == 0)
                        currentFrame = idleFrames[0];
                    else
                        currentFrame = walkAnimations[0].getKeyFrame(stateTime, true);
                } else if (heading.y > 0) {
                    if (velocity == 0)
                        currentFrame = idleFrames[1];
                    else
                        currentFrame = walkAnimations[1].getKeyFrame(stateTime, true);
                }
            } else if (heading.x != 0) {
                if (heading.x < 0) {
                    if (velocity == 0)
                        currentFrame = idleFrames[2];
                    else
                        currentFrame = walkAnimations[2].getKeyFrame(stateTime, true);
                } else if (heading.x > 0) {
                    if (velocity == 0)
                        currentFrame = idleFrames[3];
                    else
                        currentFrame = walkAnimations[3].getKeyFrame(stateTime, true);
                }
            }
            if (body != null) {
                body.applyForce(heading, body.getWorldCenter(), true);
                setX(body.getPosition().x - 0.5f);
                setY(body.getPosition().y - 0.25f);
            }
        } else if (!isAlive && TimeUtils.millis() - timeOfDeath < deathAnimationTime) {
            if (heading.y != 0) {
                if (heading.y < 0) {
                        currentFrame = deathAnimations[0].getKeyFrame(stateTime, true);
                } else if (heading.y > 0) {
                        currentFrame = deathAnimations[1].getKeyFrame(stateTime, true);
                }
            } else if (heading.x != 0) {
                if (heading.x < 0) {
                        currentFrame = deathAnimations[2].getKeyFrame(stateTime, true);
                } else if (heading.x > 0) {
                        currentFrame = deathAnimations[3].getKeyFrame(stateTime, true);
                }
            }
        } else {
            removeThisEntity = true;
            setX(-1f);
            setY(-1f);
        }
    }

    public void Draw(Batch batch) {
        this.setRegion(currentFrame);
        super.draw(batch);
    }

    public static final int
            ID_ENEMY_NAZI = 1,
            ID_ENEMY_SPEARMAN = 2;
}
