package com.juginabi.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.juginabi.towerdefence.GameEntities.DynamicDefender;
import com.juginabi.towerdefence.GameEntities.DynamicMonster;
import com.juginabi.towerdefence.GameEntities.GameEntity;
import com.juginabi.towerdefence.helpers.EntityInitializer;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Created by Jukka on 3.3.2015.
 */
public class GameWorld {
    private final static String TAG = "GameWorld";
    // Map for all the monster and tower entities
    private Stack<GameEntity> activeList;
    // Pool of entities
    private Stack<GameEntity> enemies;
    private Stack<GameEntity> friends;
    // Physics world
    PhysicsWorld physicsWorld;

    public GameWorld(PhysicsWorld physicsWorld) {
        this.activeList = new Stack<GameEntity>();
        this.enemies = new Stack<GameEntity>();
        this.friends = new Stack<GameEntity>();
        this.physicsWorld = physicsWorld;
    }

    public void InitializeWorld() {
        int i = 0;
        while (i != 20) {
            EntityInitializer initializer = new EntityInitializer(TowerDefence.getAssetManager().get("Graphics/topdown-nazi.png", Texture.class), Gdx.files.internal("MonsterData/monsters.xml"), 1, 1);
            CreateEntity(GameEntity.ID_ENEMY_NAZI, initializer);
            CreateEntity(GameEntity.ID_DEFENDER_TANK, null);
            ++i;
        }
    }

    public GameEntity SpawnEntity(int type, float x, float y) {
        // This is either enemy or defender
        GameEntity entity = null;
        try {
            switch (type) {
                case GameEntity.ID_ENEMY_NAZI:
                    if (!enemies.isEmpty()) {
                        entity = enemies.pop();
                        entity.initialize(x,y);
                        activeList.push(entity);
                    }
                    break;
                case GameEntity.ID_ENEMY_SPEARMAN:
                    break;
                case GameEntity.ID_DEFENDER_TANK:
                    if (!friends.isEmpty()) {
                        entity = friends.pop();
                        entity.initialize(x, y);
                        activeList.push(entity);
                    }
                    break;
            }
        } catch (EmptyStackException e) {
            e.printStackTrace();
            return null;
        }
        return entity;
    }

    void CreateEntity(int type, EntityInitializer initializer) {
        GameEntity entity = null;
        switch (type) {
            case GameEntity.ID_ENEMY_NAZI:
                entity = new DynamicMonster(this, physicsWorld, initializer);
                break;
            case GameEntity.ID_DEFENDER_TANK:
                entity = new DynamicDefender(this, physicsWorld);
                break;
            default:
                Gdx.app.log(TAG, "Unable to create entity with id " + type);
        }
        if (entity != null) {
            switch (type) {
                case GameEntity.ID_ENEMY_NAZI:
                    enemies.push(entity);
                    break;
                case GameEntity.ID_DEFENDER_TANK:
                    friends.push(entity);
                    break;
            }
        }
    }

    public void UpdateWorld(float tickMilliseconds) {
        GameEntity entity;
        for (int i = activeList.size()-1; i >=0;) {
            entity = activeList.get(i);
            entity.Update(tickMilliseconds);
            if (entity.removeThisEntity)
                enemies.push(activeList.remove(i));
            --i;
        }
    }

    public void DrawWorld(Batch batch) {
        for (int i = 0; i < activeList.size(); ++i)
            activeList.get(i).Draw(batch);
    }
}
