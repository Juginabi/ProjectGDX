package com.juginabi.towerdefence.GameEntities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.juginabi.towerdefence.GameWorld;
import com.juginabi.towerdefence.PhysicsWorld;
import com.juginabi.towerdefence.TowerDefence;
import com.juginabi.towerdefence.helpers.CollisionBoxLoader;

import java.util.Stack;

/**
 * Created by Jukka on 15.9.2015.
 */
public class DynamicDefender extends GameEntity {
    private static final String TAG = "DynamicDefender";
    private final GameWorld gameWorld;
    private final PhysicsWorld physicsWorld;

    public Stack<Body> targetsSpotted;
    public Stack<Body> targetsInRange;
    public Body spottedTarget;
    public boolean targetBodyInRange;

    private long reload = 500;
    private long lastFire;

    private Body barrelBody;
    private RevoluteJoint joint;

    Sprite barrel;

    Laser currentLaser;

    public DynamicDefender(GameWorld gameworld, PhysicsWorld physicsWorld) {
        //super(TowerDefence.getAssetManager().get("Graphics/EntityAtlas.txt", TextureAtlas.class).findRegion("tankBlack"));
        super(TowerDefence.getAssetManager().get("Graphics/tankatlas.txt", TextureAtlas.class).findRegion("tankRed_outline"));
        AssetManager manager = TowerDefence.getAssetManager();
        barrel = new Sprite(TowerDefence.getAssetManager().get("Graphics/tankatlas.txt", TextureAtlas.class).findRegion("barrelRed_outline"));
        this.gameWorld = gameworld;
        this.physicsWorld = physicsWorld;
        this.isAlive = true;
        this.lastFire = 0;
        this.targetsSpotted = new Stack<Body>();
        this.targetsInRange = new Stack<Body>();
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



        if (spottedTarget == null && !targetsSpotted.isEmpty()) {
            spottedTarget = targetsSpotted.get(0);
        }
        if ( spottedTarget != null && time - lastFire > reload && targetsInRange.contains(spottedTarget)) {
            // We can fire
            Fire();
            lastFire = time;
        }

        // Check angle to target
        if (spottedTarget != null) {
            float bodyAngle = barrelBody.getAngle();
            Vector2 toTarget = spottedTarget.getPosition().sub(barrelBody.getPosition());
            float desiredAngle = MathUtils.atan2(-toTarget.x, toTarget.y);
            float totalRotation = desiredAngle - bodyAngle;
            while ( totalRotation < -180 * MathUtils.degreesToRadians ) totalRotation += 360 * MathUtils.degreesToRadians;
            while ( totalRotation >  180 * MathUtils.degreesToRadians ) totalRotation -= 360 * MathUtils.degreesToRadians;
            if (totalRotation > 0) {
                if (totalRotation > (25 * MathUtils.degreesToRadians))
                    joint.setMotorSpeed(180 * MathUtils.degreesToRadians);
                else
                    joint.setMotorSpeed(25 * MathUtils.degreesToRadians);
                joint.enableMotor(true);
            } else if (totalRotation < 0) {
                if (totalRotation < (-25 * MathUtils.degreesToRadians))
                    joint.setMotorSpeed(-180 * MathUtils.degreesToRadians);
                else
                    joint.setMotorSpeed(-25 * MathUtils.degreesToRadians);
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
        this.targetBodyInRange = false;
        //Create Body definition
        CollisionBoxLoader loader = new CollisionBoxLoader(Gdx.files.internal("Graphics/TowerDefenceCollisionBoxes"));

        BodyDef bd = new BodyDef();
        bd.position.set(x, y);
        bd.type = BodyDef.BodyType.StaticBody;

        // This fixture is used as spotting sensor. If enemy is in this sensor radius, we start turning the barrel towards it.
        CircleShape circle = new CircleShape();
        circle.setRadius(3f);
        FixtureDef spottingSensorFixture = new FixtureDef();
        spottingSensorFixture.isSensor = true;
        spottingSensorFixture.shape = circle;
        spottingSensorFixture.filter.categoryBits = PhysicsWorld.ENTITY_DEFENDER_SPOTTING_SENSOR;
        spottingSensorFixture.filter.maskBits = PhysicsWorld.ENTITY_ENEMY;

        body = physicsWorld.world_.createBody(bd);
        body.createFixture(spottingSensorFixture);
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


        // This fixture is used as fireing sensor. If enemy is in this sensor radius, we start fireing.
        PolygonShape cone = new PolygonShape();
        Vector2[] vertices = new Vector2[8];
        vertices[0] = new Vector2(0,0);
        float radius = 3;
        float desiredAngle = 20; // degrees
        for (int i = 0; i < 7; i++) {
            float angle = (float) (i / 6.0 * desiredAngle * MathUtils.degreesToRadians) + ((90-desiredAngle/2) * MathUtils.degreesToRadians);
            vertices[i+1] = new Vector2(radius * MathUtils.cos(angle), radius * MathUtils.sin(angle));
        }
        cone.set(vertices);
        FixtureDef fireSensorFixture = new FixtureDef();
        fireSensorFixture.isSensor = true;
        fireSensorFixture.shape = cone;
        fireSensorFixture.filter.categoryBits = PhysicsWorld.ENTITY_DEFENDER_FIRE_SENSOR;
        fireSensorFixture.filter.maskBits = PhysicsWorld.ENTITY_ENEMY;

        FixtureDef barrelFix = new FixtureDef();
        barrelFix.friction = 0.1f;
        barrelFix.density = 0.05f;
        barrelFix.filter.categoryBits = PhysicsWorld.ENTITY_DEFENDER;
        barrelFix.filter.maskBits = PhysicsWorld.ENTITY_ENEMY;
        barrelBody = physicsWorld.world_.createBody(barrelDef);
        barrelBody.setUserData(this);
        origin = loader.getOrigin("BarrelRed",0.2f).cpy();
        barrel.setOrigin(origin.x, origin.y);
        loader.attachFixture(barrelBody, "BarrelRed", barrelFix, 0.2f);
        barrelBody.createFixture(fireSensorFixture);
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

    public void EnemyEnteredSpotRange(Body body) {
        targetsSpotted.push(body);
    }

    public void EnemyExitedSpotRange(Body body) {
        targetsSpotted.remove(body);
        if (spottedTarget == body)
            spottedTarget = null;
    }

    private void Fire() {
        Vector2 myPos = body.getPosition();
        Vector2 targetPos = spottedTarget.getPosition();
        Vector2 impulseDir = new Vector2(targetPos.x - myPos.x, targetPos.y - myPos.y);
        spottedTarget.applyForce(impulseDir, spottedTarget.getWorldCenter(), true);
        DynamicMonster monster = (DynamicMonster)spottedTarget.getUserData();
        monster.hitpoints -= 2;

        if (currentLaser == null) {
            currentLaser = new Laser(barrelBody.getPosition().x, barrelBody.getPosition().y);
            currentLaser.setLifeTime(reload / 2000f);
            currentLaser.setBeamColor(new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 255 / 255f));
            currentLaser.setRayColor(new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 255 / 255f));
            currentLaser.setDistance(2);
        }
    }

    public void EnemyEnteredFireRange(Body enemy) {
        Gdx.app.log(TAG, "Enemy entered fire range!");
        targetsInRange.push(enemy);
    }

    public void EnemyExitedFireRange(Body enemy) {
        targetsInRange.remove(enemy);
    }
}
