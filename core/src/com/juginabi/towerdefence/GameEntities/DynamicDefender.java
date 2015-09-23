package com.juginabi.towerdefence.GameEntities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.juginabi.towerdefence.GameWorld;
import com.juginabi.towerdefence.PhysicsWorld;
import com.juginabi.towerdefence.helpers.CollisionBoxLoader;

import java.util.Random;
import java.util.Stack;

/**
 * Created by Jukka on 15.9.2015.
 */
public class DynamicDefender extends GameEntity {
    private static final String TAG = "DynamicDefender";
    private final GameWorld gameWorld;
    private final PhysicsWorld physicsWorld;

    public Stack<Body> allTargets;
    public Body targetBody;

    private long reload = 500;
    private long lastFire;

    private Body barrelBody;
    private RevoluteJoint joint;

    Sprite barrel;

    Laser currentLaser;

    public DynamicDefender(GameWorld gameworld, PhysicsWorld physicsWorld) {
        //super(TowerDefence.getAssetManager().get("Graphics/EntityAtlas.txt", TextureAtlas.class).findRegion("tankBlack"));
        super(new Texture(Gdx.files.internal("Graphics/tankRed_outline.png")));
        barrel = new Sprite(new Texture(Gdx.files.internal("Graphics/barrelRed_outline.png")));
        this.gameWorld = gameworld;
        this.physicsWorld = physicsWorld;
        this.isAlive = true;
        this.lastFire = 0;
        this.allTargets = new Stack<Body>();
    }

    @Override
    public void Update(float tickMilliseconds) {
        long time = TimeUtils.millis();
        if (body != null) {
            setX(body.getPosition().x - getWidth() / 2);
            setY(body.getPosition().y - getHeight()/2);
            setRotation(MathUtils.radiansToDegrees * body.getAngle());
        }

        if (barrelBody != null) {
            barrel.setX(barrelBody.getPosition().x - barrel.getWidth() / 2);
            barrel.setY(barrelBody.getPosition().y - barrel.getOriginY());
            barrel.setRotation(MathUtils.radiansToDegrees * barrelBody.getAngle());
        }



        if (targetBody == null && !allTargets.isEmpty()) {
            targetBody = allTargets.get(0);
        }
        if ( targetBody != null && time - lastFire > reload) {
            // We can fire
            Fire();
            lastFire = time;
        }

        // Check angle to target
        if (targetBody != null) {
            float bodyAngle = barrelBody.getAngle();
            Vector2 toTarget = targetBody.getPosition().sub(barrelBody.getPosition());
            float desiredAngle = MathUtils.atan2(-toTarget.x, toTarget.y);
            if (desiredAngle > bodyAngle) {
                joint.setMotorSpeed(90 * MathUtils.degreesToRadians);
                joint.enableMotor(true);
            } else if (desiredAngle < bodyAngle) {
                joint.setMotorSpeed(-90 * MathUtils.degreesToRadians);
                joint.enableMotor(true);
            } else {
                joint.setMotorSpeed(0);
            }
        } else {
            joint.setMotorSpeed(0);
        }

        if (currentLaser != null) {
            currentLaser.setDegrees(barrel.getRotation());
            if (!currentLaser.Update(tickMilliseconds)) {
                Gdx.app.log(TAG, "Setting laser null!");
                currentLaser = null;
            }
        }
    }

    @Override
    public void Draw(Batch batch) {
        super.draw(batch);
        if (currentLaser != null) {
            currentLaser.Draw(batch);
        }
        barrel.draw(batch);
    }

    @Override
    public void initialize(float x, float y) {
        //Create Body definition
        CollisionBoxLoader loader = new CollisionBoxLoader(Gdx.files.internal("Graphics/TowerDefenceCollisionBoxes"));

        BodyDef bd = new BodyDef();
        bd.position.set(x, y);
        bd.type = BodyDef.BodyType.StaticBody;

        CircleShape circle = new CircleShape();
        circle.setRadius(3f);

        FixtureDef fd = new FixtureDef();
        fd.isSensor = true;
        fd.shape = circle;
        fd.filter.categoryBits = PhysicsWorld.ENTITY_DEFENDER_SENSOR;
        fd.filter.maskBits = PhysicsWorld.ENTITY_ENEMY;

        body = physicsWorld.world_.createBody(bd);
        body.createFixture(fd);
        body.setUserData(this);
        FixtureDef fd2 = new FixtureDef();
        fd2.density = 5f;
        fd2.friction = 2f;
        fd2.filter.categoryBits = PhysicsWorld.ENTITY_DEFENDER;
        fd2.filter.maskBits = PhysicsWorld.ENTITY_DEFENDER | PhysicsWorld.ENTITY_ILLEGAL_BUILD_SPOT;
        Vector2 origin = loader.getOrigin("TankRed", 1).cpy();
        setOrigin(origin.x, origin.y);

        loader.attachFixture(body, "TankRed", fd2, 0.8f);
        this.setBounds(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2, 0.8f, 0.8f * this.getHeight() / this.getWidth());

        BodyDef barrelDef = new BodyDef();
        barrelDef.position.set(body.getPosition());
        barrelDef.type = BodyDef.BodyType.DynamicBody;

        FixtureDef barrelFix = new FixtureDef();
        barrelFix.friction = 0.1f;
        barrelFix.density = 0.05f;
        barrelFix.filter.categoryBits = PhysicsWorld.ENTITY_DEFENDER;
        barrelFix.filter.maskBits = PhysicsWorld.ENTITY_ENEMY;
        barrelBody = physicsWorld.world_.createBody(barrelDef);
        origin = loader.getOrigin("BarrelRed",0.2f).cpy();
        barrel.setOrigin(origin.x, origin.y);
        loader.attachFixture(barrelBody, "BarrelRed", barrelFix, 0.2f);
        barrel.setBounds(barrelBody.getPosition().x - barrel.getWidth() / 2, barrelBody.getPosition().y - barrel.getHeight() / 2, 0.2f, 0.2f * barrel.getHeight() / barrel.getWidth());
        RevoluteJointDef joint = new RevoluteJointDef();
        joint.bodyA = body;
        joint.bodyB = barrelBody;
        joint.localAnchorA.set(0,0);
        joint.localAnchorB.set(0,0);
        joint.enableMotor = false;
        joint.collideConnected = false;
        joint.maxMotorTorque = 20;
        joint.motorSpeed = 90 * MathUtils.degreesToRadians;
        this.joint = (RevoluteJoint) physicsWorld.world_.createJoint(joint);
    }

    public void addTarget(Body body) {
        allTargets.push(body);
    }

    public void removeBody(Body body) {
        for (int i = 0; i < allTargets.size(); ++i)
            if (allTargets.get(i) == body) {
                allTargets.remove(i);
                if (body == targetBody)
                    targetBody = null;
            }
    }

    private void Fire() {
        Vector2 myPos = body.getPosition();
        Vector2 targetPos = targetBody.getPosition();
        Vector2 impulseDir = new Vector2(targetPos.x - myPos.x, targetPos.y - myPos.y);
        targetBody.applyForce(impulseDir, targetBody.getWorldCenter(), true);
        DynamicMonster monster = (DynamicMonster)targetBody.getUserData();
        monster.hitpoints -= 2;

        if (currentLaser == null) {
            currentLaser = new Laser(barrelBody.getPosition().x, barrelBody.getPosition().y);
            currentLaser.setLifeTime(reload/2000f);
            currentLaser.setBeamColor(new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 255/255f));
            currentLaser.setRayColor(new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 255/255f));
            currentLaser.setDistance(2);
        }

    }
}
