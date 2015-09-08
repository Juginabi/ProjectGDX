package com.juginabi.towerdefence.GameEntities.Monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    private static final int        FRAME_COLS = 11;         // #1
    private static final int        FRAME_ROWS = 4;         // #2

    Animation walkDownAnimation;
    Animation walkUpAnimation;
    Animation walkLeftAnimation;
    Animation walkRightAnimation;

    Texture walkSheet;

    TextureRegion[] walkDownFrames;
    TextureRegion[] walkUpFrames;
    TextureRegion[] walkLeftFrames;
    TextureRegion[] walkRightFrames;
    TextureRegion downIdleFrame;
    TextureRegion upIdleFrame;
    TextureRegion leftIdleFrame;
    TextureRegion rightIdleFrame;

    TextureRegion currentFrame;

    float stateTime;

    Body physicsBody;

    @Override
    public boolean Update(float deltaTime) {
        if (getHitPoints() < 0f)
            isAlive_ = false;
        if (!isAlive_)
            return false;
        // Move the attacker
        if (!C1reached) {
            if (posIsCloseTo(4, 1, 12)) {
                C1reached = true;
                Vector2 vec = physicsBody.getLinearVelocity();
                float len = vec.len();
                Vector2 heading = new Vector2(1, 0);
                heading.x *= len;
                heading.y *= len;
                physicsBody.setLinearVelocity(heading);
                setHeading(1, 0);
            }
        } else if (!C2reached) {
            if (posIsCloseTo(11, 1, 12)) {
                C2reached = true;
                Vector2 vec = physicsBody.getLinearVelocity();
                float len = vec.len();
                Vector2 heading = new Vector2(0, 1);
                heading.x *= len;
                heading.y *= len;
                physicsBody.setLinearVelocity(heading);
                setHeading(0, 1);
            }
        } else if (!C3reached) {
            if (posIsCloseTo(11, 8, 12)) {
                C3reached = true;
                Vector2 vec = physicsBody.getLinearVelocity();
                float len = vec.len();
                Vector2 heading = new Vector2(1, 0);
                heading.x *= len;
                heading.y *= len;
                physicsBody.setLinearVelocity(heading);
                TowerDefence.physicsWorld_.world_.destroyBody(physicsBody);
                setHeading(1, 0);
                isAlive_ = false;
            }
        } /*else if (!C4reached) {
            if (posIsCloseTo(19, 13, 12)) {
                C4reached = true;
                Vector2 vec = physicsBody.getLinearVelocity();
                float len = vec.len();
                Vector2 heading = new Vector2(0, -1);
                heading.x *= len;
                heading.y *= len;
                physicsBody.setLinearVelocity(heading);
                setHeading(0, -1);
            }
        } else if (!C5reached) {
            if (posIsCloseTo(19, 3, 12)) {
                C5reached = true;
                Vector2 vec = physicsBody.getLinearVelocity();
                float len = vec.len();
                Vector2 heading = new Vector2(1, 0);
                heading.x *= len;
                heading.y *= len;
                physicsBody.setLinearVelocity(heading);
                setHeading(1, 0);
            }
        } else if (!C6reached) {
            if (posIsCloseTo(26, 3, 12)) {
                C6reached = true;
                Vector2 vec = physicsBody.getLinearVelocity();
                float len = vec.len();
                Vector2 heading = new Vector2(0, 1);
                heading.x *= len;
                heading.y *= len;
                physicsBody.setLinearVelocity(heading);
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
                TowerDefence.physicsWorld_.world_.destroyBody(physicsBody);
                physicsBody = null;
                isAlive_ = false;
            }
        }*/
        stateTime += deltaTime;
        if (getHeading().x != 0) {
            if (getHeading().x > 0) {
                currentFrame = walkRightAnimation.getKeyFrame(stateTime, true);
            } else if (getHeading().x < 0) {
                currentFrame = walkLeftAnimation.getKeyFrame(stateTime, true);
            }
        }  else if (getHeading().y != 0) {
            if (getHeading().y > 0) {
                currentFrame = walkUpAnimation.getKeyFrame(stateTime, true);
            } else if (getHeading().y < 0) {
                currentFrame = walkDownAnimation.getKeyFrame(stateTime, true);
            }
        }
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
        this.setHeading(0, -1);
        this.setBounds(x, y, 1, 1);
        this.scale(0.5f);
        this.setOriginCenter();
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
            fixture.filter.categoryBits = 0x02;
            fixture.filter.maskBits = 0x01;
            physicsBody.createFixture(fixture);
        }
        setOriginCenter();
        physicsBody.setLinearDamping(0.2f);
        physicsBody.setUserData(this);
    }

    public JesseMonster(GameWorld parent, TextureAtlas.AtlasRegion entityTexture) {
        super(parent, entityTexture, GameWorld.EnemyJesse);
        this.setVelocity(1f);
        this.setHitPoints(10f);
        this.setHeading(0, -1);

        walkSheet = new Texture(Gdx.files.internal("Graphics/topdown1.png")); // #9
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth()/FRAME_COLS, walkSheet.getHeight()/FRAME_ROWS);              // #10
        walkDownFrames = new TextureRegion[6];
        walkUpFrames = new TextureRegion[6];
        walkLeftFrames = new TextureRegion[6];
        walkRightFrames = new TextureRegion[6];
        downIdleFrame = new TextureRegion(tmp[0][0]);
        upIdleFrame = new TextureRegion(tmp[1][0]);
        leftIdleFrame = new TextureRegion(tmp[2][0]);
        rightIdleFrame = new TextureRegion(tmp[3][0]);

        for (int i = 0; i < FRAME_ROWS; i++) {
            int index = 0;
            for (int j = 1; j < 7; j++) {
                switch (i) {
                    case 0:
                        walkDownFrames[index++] = tmp[i][j];
                        break;
                    case 1:
                        walkUpFrames[index++] = tmp[i][j];
                        break;
                    case 2:
                        walkLeftFrames[index++] = tmp[i][j];
                        break;
                    case 3:
                        walkRightFrames[index++] = tmp[i][j];
                        break;
                }
            }
        }

        walkDownAnimation = new Animation(0.05f, walkDownFrames);
        walkUpAnimation = new Animation(0.05f, walkUpFrames);
        walkLeftAnimation = new Animation(0.05f, walkLeftFrames);
        walkRightAnimation = new Animation(0.05f, walkRightFrames);
        stateTime = 0f;
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
        this.setRegion(currentFrame);
        super.draw(batch);
    }
}
