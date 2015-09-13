package com.juginabi.towerdefence.Obsolete;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.juginabi.towerdefence.GameEntities.DynamicEntity;
import com.juginabi.towerdefence.GameWorld;

import java.util.concurrent.TimeUnit;

/**
 * Created by Jukka on 3.3.2015.
 */
public class NaziSoldier /*extends DynamicEntity*/ {
    /*private boolean C1reached = false;
    private boolean C2reached = false;
    private boolean C3reached = false;
    private boolean C4reached = false;
    private boolean C5reached = false;
    private boolean C6reached = false;
    private boolean C7reached = false;
    private long birthTime;

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

    public NaziSoldier(GameWorld parent, TextureAtlas.AtlasRegion entityTexture) {
        super(parent, entityTexture, GameWorld.EnemyGeek);
        this.setVelocity(1f);
        this.setHitPoints(10f);
        this.setHeading(0,-1);

        walkSheet = new Texture(Gdx.files.internal("Graphics/topdown-nazi.png")); // #9
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

    @Override
    public void initialize(float x, float y) {
        C1reached = false;
        C2reached = false;
        C3reached = false;
        C4reached = false;
        C5reached = false;
        C6reached = false;
        C7reached = false;
        this.setVelocity(1.5f);
        this.setHitPoints(50f);
        this.setHeading(0, -1);
        this.setBounds(x, y, 1, 1);
        this.scale(0.5f);
        this.setOriginCenter();
        isAlive_ = true;
        this.birthTime = TimeUtils.millis();
    }

    @Override
    public boolean Update(float deltaTime) {
        if (getHitPoints() < 0f)
            isAlive_ = false;
        if (TimeUtils.millis() - birthTime > 100000) {
            isAlive_ = false;
            setPosition(-1, -1);
        }
        if (!isAlive_)
            return false;
        // Move the attacker
        if (!C1reached && posIsCloseTo(4, 1, 12)) {
            C1reached = true;
            setHeading(1,0);
        } else if (!C2reached && posIsCloseTo(11, 1, 12)) {
            C2reached = true;
            setHeading(0,1);
        } else if (!C3reached && posIsCloseTo(11, 8, 12)) {
            setPosition(4, 9);
            C1reached = false;
            C2reached = false;
            C3reached = false;
            setHeading(0, -1);
        } else if (!C4reached && posIsCloseTo(19, 13, 12)) {
            C4reached = true;
            setHeading(0,-1);
        } else if (!C5reached && posIsCloseTo(19, 3, 12)) {
            C5reached = true;
            setHeading(1,0);
        } else if (!C6reached && posIsCloseTo(26, 3, 12)) {
            C6reached = true;
            setHeading(0,1);
        } else if (!C7reached && posIsCloseTo(26, 17, 20)) {
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
        Vector2 heading = this.getHeading();
        float velocity = this.getVelocity();
        float deltaMovementX = heading.x * velocity * deltaTime;
        float deltaMovementY = heading.y * velocity * deltaTime;
        float x = super.getX();
        float y = super.getY();
        super.setPosition(x+deltaMovementX, y+deltaMovementY);

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

    private boolean posIsCloseTo(float x, float y, float distance) {
        float myPosX = getX();
        float myPosY = getY();

        float distanceX = myPosX - x;
        float distanceY = myPosY - y;
        float dist = (float) Math.sqrt(distanceX*distanceX + distanceY*distanceY);
        boolean returnValue = dist < 0.05;
        return returnValue;
    }

    @Override
    public void Draw(Batch batch) {
        if (!isAlive_)
            return;
        this.setRegion(currentFrame);
        super.draw(batch);
    }*/
}
