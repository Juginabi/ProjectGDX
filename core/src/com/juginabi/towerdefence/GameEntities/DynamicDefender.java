package com.juginabi.towerdefence.GameEntities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.juginabi.towerdefence.GameWorld;
import com.juginabi.towerdefence.PhysicsWorld;
import com.juginabi.towerdefence.TowerDefence;
import com.juginabi.towerdefence.helpers.CollisionBoxLoader;

/**
 * Created by Jukka on 15.9.2015.
 */
public class DynamicDefender extends GameEntity {
    private static final String TAG = "DynamicDefender";
    private final GameWorld gameWorld;
    private final PhysicsWorld physicsWorld;

    private Body targetBody;

    public DynamicDefender(GameWorld gameworld, PhysicsWorld physicsWorld) {
        //super(TowerDefence.getAssetManager().get("Graphics/EntityAtlas.txt", TextureAtlas.class).findRegion("tankBlack"));
        super(new Texture(Gdx.files.internal("Graphics/tankRed_outline.png")));
        this.gameWorld = gameworld;
        this.physicsWorld = physicsWorld;
        this.isAlive = true;

        //Create Body definition
        CollisionBoxLoader loader = new CollisionBoxLoader(Gdx.files.internal("Graphics/TowerDefenceCollisionBoxes"));

        BodyDef bd = new BodyDef();
        bd.position.set(7, 4);
        bd.type = BodyDef.BodyType.DynamicBody;

        FixtureDef fd = new FixtureDef();
        fd.density = 1;
        fd.friction = 0.5f;
        fd.restitution = 0.3f;
        fd.filter.categoryBits = PhysicsWorld.ENTITY_DEFENDER;
        fd.filter.maskBits = PhysicsWorld.ENTITY_ENEMY | PhysicsWorld.ENTITY_DEFENDER;

        body = physicsWorld.world_.createBody(bd);
        body.setUserData(this);
        Vector2 origin = loader.getOrigin("TankRed", 1).cpy();
        setOrigin(origin.x, origin.y);

        loader.attachFixture(body, "TankRed", fd, 0.8f);
        Gdx.app.log(TAG, "Body created for defender");
        this.setBounds(body.getPosition().x, body.getPosition().y,0.8f,0.8f);
    }

    @Override
    public void Update(float tickMilliseconds) {
        if (body != null) {
            setX(body.getPosition().x);
            setY(body.getPosition().y);
            setRotation(MathUtils.radiansToDegrees * body.getAngle());
        }
    }

    @Override
    public void Draw(Batch batch) {
        super.draw(batch);
    }

    @Override
    public void initialize(float x, float y) {

    }
}
