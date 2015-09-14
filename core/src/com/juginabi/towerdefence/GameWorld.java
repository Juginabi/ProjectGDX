package com.juginabi.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.juginabi.towerdefence.GameEntities.DynamicEntity;
import com.juginabi.towerdefence.GameEntities.EntityInitializer;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Created by Jukka on 3.3.2015.
 */
public class GameWorld {
    private final static String TAG = "GameWorld";
    // Map for all the monster and tower entities
    private Stack<DynamicEntity> activeList;
    // Pool of entities
    private Stack<DynamicEntity> enemies;
    // Graphics for entities
    public TextureAtlas entityAtlas;
    // Physics world
    PhysicsWorld physicsWorld;

    public GameWorld(PhysicsWorld physicsWorld) {
        this.activeList = new Stack<DynamicEntity>();
        this.enemies = new Stack<DynamicEntity>();
        this.physicsWorld = physicsWorld;
    }

    public void InitializeWorld() {
        entityAtlas = TowerDefence.getAssetManager().get("Graphics/EntityAtlas.txt", TextureAtlas.class);
        int i = 0;
        while (i != 20) {
            EntityInitializer initializer = new EntityInitializer(TowerDefence.getAssetManager().get("Graphics/topdown-nazi.png", Texture.class), Gdx.files.internal("MonsterData/monsters.xml"), 1, 1);
            //CreateEntity(TowerCannon);
            CreateEntity(DynamicEntity.ID_ENEMY_NAZI, initializer);
            ++i;
        }
    }

    public DynamicEntity SpawnEntity(int type, float x, float y) {
        // This is either enemy or defender
        DynamicEntity entity = null;
        try {
            switch (type) {
                case DynamicEntity.ID_ENEMY_NAZI:
                    if (!enemies.isEmpty()) {
                        entity = enemies.pop();
                        entity.initialize(new Vector2(x, y));
                        activeList.push(entity);
                    }
                    break;
                case DynamicEntity.ID_ENEMY_SPEARMAN:
                    break;
            }
        } catch (EmptyStackException e) {
            e.printStackTrace();
            return null;
        }
        return entity;
    }

    DynamicEntity CreateEntity(int type, EntityInitializer initializer) {
        DynamicEntity entity = null;
        switch (type) {
            case DynamicEntity.ID_ENEMY_NAZI:
                entity = new DynamicEntity(this, physicsWorld, initializer);
                break;
            default:
                Gdx.app.log(TAG, "Unable to create entity!");
        }
        if (entity != null) {
            switch (type) {
                case DynamicEntity.ID_ENEMY_NAZI:
                    enemies.push(entity);
                    break;
            }
        }
        return entity;
    }

    public void UpdateWorld(float tickMilliseconds) {
        Stack<DynamicEntity> deadStack = new Stack<DynamicEntity>();
        for (DynamicEntity e : activeList) {
            e.Update(tickMilliseconds);
            if (e.removeThisEntity)
                deadStack.push(e);
        }
        DynamicEntity deadEntity = null;
        while (!deadStack.isEmpty()) {
            deadEntity = deadStack.pop();
            enemies.push(deadEntity);
            activeList.remove(deadEntity);
        }
    }

    public void DrawWorld(Batch batch) {
        for (DynamicEntity e : activeList)
            e.Draw(batch);
    }
}
