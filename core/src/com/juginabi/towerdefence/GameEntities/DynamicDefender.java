package com.juginabi.towerdefence.GameEntities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.juginabi.towerdefence.GameWorld;
import com.juginabi.towerdefence.PhysicsWorld;
import com.juginabi.towerdefence.helpers.CollisionBoxLoader;

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

    private long reload = 60;
    private long lastFire;

    public DynamicDefender(GameWorld gameworld, PhysicsWorld physicsWorld) {
        //super(TowerDefence.getAssetManager().get("Graphics/EntityAtlas.txt", TextureAtlas.class).findRegion("tankBlack"));
        super(new Texture(Gdx.files.internal("Graphics/tankRed_outline.png")));
        this.gameWorld = gameworld;
        this.physicsWorld = physicsWorld;
        this.isAlive = true;
        this.lastFire = 0;
        this.allTargets = new Stack<Body>();

        //Create Body definition
        CollisionBoxLoader loader = new CollisionBoxLoader(Gdx.files.internal("Graphics/TowerDefenceCollisionBoxes"));

        BodyDef bd = new BodyDef();
        bd.position.set(7, 4);
        bd.type = BodyDef.BodyType.StaticBody;

        CircleShape circle = new CircleShape();
        circle.setRadius(3f);

        FixtureDef fd = new FixtureDef();
        fd.isSensor = true;
        fd.shape = circle;
        fd.filter.categoryBits = PhysicsWorld.ENTITY_DEFENDER;
        fd.filter.maskBits = PhysicsWorld.ENTITY_ENEMY;

        body = physicsWorld.world_.createBody(bd);
        Fixture fix = body.createFixture(fd);
        body.setUserData(this);
        Vector2 origin = loader.getOrigin("TankRed", 1).cpy();
        setOrigin(origin.x, origin.y);

        loader.attachFixture(body, "TankRed", fd, 0.8f);
        Gdx.app.log(TAG, "Body created for defender");
        this.setBounds(body.getPosition().x - getWidth()/2, body.getPosition().y - getHeight()/2,0.8f,0.8f);
    }

    @Override
    public void Update(float tickMilliseconds) {
        long time = TimeUtils.millis();
        if (body != null) {
            setX(body.getPosition().x - getWidth()/2);
            setY(body.getPosition().y - getHeight()/2);
            setRotation(MathUtils.radiansToDegrees * body.getAngle());
        }

        if (targetBody == null && !allTargets.isEmpty()) {
            targetBody = allTargets.get(0);
        }
        if ( targetBody != null && time - lastFire > reload) {
            // We can fire
            Fire();
            lastFire = time;
        }
    }

    @Override
    public void Draw(Batch batch) {
        super.draw(batch);
    }

    @Override
    public void initialize(float x, float y) {

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
    }
}
