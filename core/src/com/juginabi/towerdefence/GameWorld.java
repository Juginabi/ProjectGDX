package com.juginabi.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.juginabi.towerdefence.GameEntities.DynamicMonster;
import com.juginabi.towerdefence.GameEntities.EntityInitializer;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Created by Jukka on 3.3.2015.
 */
public class GameWorld {
    private final static String TAG = "GameWorld";
    // Map for all the monster and tower entities
    private Stack<DynamicMonster> activeList;
    // Pool of entities
    private Stack<DynamicMonster> enemies;
    // Entities marked for removal. Cleared after each frame.
    Stack<DynamicMonster> deadStack;
    // Graphics for entities
    public TextureAtlas entityAtlas;
    // Physics world
    PhysicsWorld physicsWorld;

    public GameWorld(PhysicsWorld physicsWorld) {
        this.activeList = new Stack<DynamicMonster>();
        this.enemies = new Stack<DynamicMonster>();
        this.deadStack = new Stack<DynamicMonster>();
        this.physicsWorld = physicsWorld;
    }

    public void InitializeWorld() {
        entityAtlas = TowerDefence.getAssetManager().get("Graphics/EntityAtlas.txt", TextureAtlas.class);
        int i = 0;
        while (i != 20) {
            EntityInitializer initializer = new EntityInitializer(TowerDefence.getAssetManager().get("Graphics/topdown-nazi.png", Texture.class), Gdx.files.internal("MonsterData/monsters.xml"), 1, 1);
            //CreateEntity(TowerCannon);
            CreateEntity(DynamicMonster.ID_ENEMY_NAZI, initializer);
            ++i;
        }
    }

    public DynamicMonster SpawnEntity(int type, float x, float y) {
        // This is either enemy or defender
        DynamicMonster entity = null;
        try {
            switch (type) {
                case DynamicMonster.ID_ENEMY_NAZI:
                    if (!enemies.isEmpty()) {
                        entity = enemies.pop();
                        entity.initialize(x,y);
                        activeList.push(entity);
                    }
                    break;
                case DynamicMonster.ID_ENEMY_SPEARMAN:
                    break;
            }
        } catch (EmptyStackException e) {
            e.printStackTrace();
            return null;
        }
        return entity;
    }

    DynamicMonster CreateEntity(int type, EntityInitializer initializer) {
        DynamicMonster entity = null;
        switch (type) {
            case DynamicMonster.ID_ENEMY_NAZI:
                entity = new DynamicMonster(this, physicsWorld, initializer);
                break;
            default:
                Gdx.app.log(TAG, "Unable to create entity!");
        }
        if (entity != null) {
            switch (type) {
                case DynamicMonster.ID_ENEMY_NAZI:
                    enemies.push(entity);
                    break;
            }
        }
        return entity;
    }

    public void UpdateWorld(float tickMilliseconds) {
        DynamicMonster entity = null;
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
