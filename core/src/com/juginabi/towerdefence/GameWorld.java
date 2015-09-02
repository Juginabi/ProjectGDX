package com.juginabi.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.juginabi.towerdefence.GameEntities.DynamicEntity;
import com.juginabi.towerdefence.GameEntities.Monsters.JesseMonster;
import com.juginabi.towerdefence.GameEntities.Monsters.PencilNeckedGeek;
import com.juginabi.towerdefence.GameEntities.Projectiles.Laser;
import com.juginabi.towerdefence.GameEntities.Towers.Cannon;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import static com.badlogic.gdx.math.Vector2.dst2;

/**
 * Created by Jukka on 3.3.2015.
 */
public class GameWorld {
    private final static String TAG = "GameWorld";
    // Map for all the monster and tower entities
    private Stack<DynamicEntity> activeList;
    private Stack<DynamicEntity> activeProjectilesList;
    // Pool of entities
    private Stack<DynamicEntity> CannonTowers;
    private Stack<DynamicEntity> LaserTowers;
    private Stack<DynamicEntity> EnemyGeeks;
    private Stack<DynamicEntity> EnemyJesses;
    private Stack<DynamicEntity> ProjectileLasers;
    // List for entities which are to be removed from map
    private List<DynamicEntity> removedEntities;
    // Graphics for entities
    public TextureAtlas entityAtlas;
    // Class for indexing entities in map. Includes helper tools
    private TileWorld tileWorld;
    // Physics world
    PhysicsWorld physicsWorld;

    public static final int
        TowerCannon = 0,
        TowerLaser = 1,
        EnemyGeek = 2,
        EnemyMorgoth = 3,
        EnemyJesse = 4,
        ProjectileLaser = 5;

    public GameWorld(PhysicsWorld physicsWorld) {
        activeList = new Stack<DynamicEntity>();
        activeProjectilesList = new Stack<DynamicEntity>();
        CannonTowers = new Stack<DynamicEntity>();
        LaserTowers = new Stack<DynamicEntity>();
        EnemyGeeks = new Stack<DynamicEntity>();
        EnemyJesses = new Stack<DynamicEntity>();
        ProjectileLasers = new Stack<DynamicEntity>();
        removedEntities = new Stack<DynamicEntity>();
        tileWorld = new TileWorld(32, 18);
        this.physicsWorld = physicsWorld;
    }

    public void InitializeWorld() {
        entityAtlas = TowerDefence.getAssetManager().get("Graphics/EntityAtlas.txt", TextureAtlas.class);
        int i = 0;
        while (i != 500) {
            CreateEntity(TowerCannon);
            CreateEntity(EnemyJesse);
            CreateEntity(EnemyGeek);
            CreateEntity(ProjectileLaser);
            ++i;
        }
    }

    public DynamicEntity SpawnEntity(int type, int x, int y) {
        // This is either enemy or defender
        DynamicEntity entity = null;
        try {
            switch (type) {
                case TowerCannon:
                    entity = CannonTowers.pop();
                    tileWorld.InsertEntity(x, y, entity);
                    break;
                case TowerLaser:
                    entity = LaserTowers.pop();
                    tileWorld.InsertEntity(x, y, entity);
                    break;
                case EnemyGeek:
                    entity = EnemyGeeks.pop();
                    tileWorld.InsertEntity(x, y, entity);
                    //activeList.push(entity);
                    break;
                case EnemyJesse:
                    entity = EnemyJesses.pop();
                    tileWorld.InsertEntity(x, y, entity);
                    break;
                case ProjectileLaser:
                    entity = ProjectileLasers.pop();
                    activeProjectilesList.push(entity);
                    break;
            }
        } catch (EmptyStackException e) {
            // This is bound to happen!
            Gdx.app.log(TAG,e.getMessage());
            entity = null;
        }
        return entity;
    }

    DynamicEntity CreateEntity(int type) {
        DynamicEntity entity = null;
        switch (type) {
            case TowerCannon:
            case TowerLaser:
                entity = new Cannon(this, entityAtlas.findRegion("tankBlack"), physicsWorld);
                break;
            case EnemyGeek:
                entity = new PencilNeckedGeek(this, entityAtlas.findRegion("smiley"));
                break;
            case EnemyJesse:
                entity = new JesseMonster(this, entityAtlas.findRegion("jesseMonster"));
                break;
            case ProjectileLaser:
                TextureAtlas.AtlasRegion[] laserRegions = {entityAtlas.findRegion("beamstart1"), entityAtlas.findRegion("beamstart2"),
                        entityAtlas.findRegion("beammid1"), entityAtlas.findRegion("beammid2"),
                        entityAtlas.findRegion("beamend1"), entityAtlas.findRegion("beamend2")};
                entity = new Laser(this, laserRegions);
                break;
            default:
                Gdx.app.log(TAG, "Unable to create entity!");
        }
        if (entity != null) {
            switch (type) {
                case TowerCannon:
                    CannonTowers.push(entity);
                    break;
                case TowerLaser:
                    LaserTowers.push(entity);
                    break;
                case EnemyGeek:
                    EnemyGeeks.push(entity);
                    break;
                case EnemyJesse:
                    EnemyJesses.push(entity);
                    break;
                case ProjectileLaser:
                    ProjectileLasers.push(entity);
                    break;
            }
        }
        return entity;
    }

    public void UpdateWorld(float tickMilliseconds) {
        Stack<DynamicEntity> deadStack = new Stack<DynamicEntity>();
        tileWorld.Update(tickMilliseconds, deadStack);
        DynamicEntity deadEntity = null;
        while (!deadStack.isEmpty()) {
            deadEntity = deadStack.pop();
            switch (deadEntity.getType()) {
                case TowerCannon:
                    CannonTowers.push(deadEntity);
                    break;
                case TowerLaser:
                    LaserTowers.push(deadEntity);
                    break;
                case EnemyGeek:
                    EnemyGeeks.push(deadEntity);
                    break;
                case EnemyJesse:
                    EnemyJesses.push(deadEntity);
                    break;
            }
        }
        /*Iterator it = activeList.iterator();
        while (it.hasNext()) {
            DynamicEntity entity = (DynamicEntity) it.next();
            if (!entity.Update(tickMilliseconds)) {

                it.remove();
            }
        }
        it = activeProjectilesList.iterator();
        while (it.hasNext()) {
            DynamicEntity entity = (DynamicEntity) it.next();
            if (!entity.Update(tickMilliseconds)) {
                ProjectileLasers.push(entity);
                it.remove();
            }
        }*/
    }

    public void GetTilesInRange(int x, int y, int range, List<TileWorld.Tile> tiles) {
        int minX = x - range;
        if (minX < 0)
            minX = 0;
        int minY = y - range;
        if (minY < 0)
            minY = 0;
        int maxX = x + range;
        if (maxX > 32)
            maxX = 32;
        int maxY = y + range;
        if (maxY > 18)
            maxY = 18;
        for (int itX = minX; itX < maxX; ++itX) {
            for (int itY = minY; itY < maxY; ++itY) {
                if (itX == x && itY == y)
                    continue;
                else
                    tiles.add(tileWorld.GetTile(itX, itY));
            }
        }
    }

    public void DrawWorld(Batch batch) {
        tileWorld.Draw(batch);
        /*Iterator it = activeList.iterator();
        while (it.hasNext()) {
            DynamicEntity entity = (DynamicEntity) it.next();
            entity.Draw(batch);
        }

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        it = activeProjectilesList.iterator();
        while (it.hasNext()) {
            DynamicEntity entity = (DynamicEntity) it.next();
            entity.Draw(batch);
        }
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);*/
    }

    public DynamicEntity GetClosestEnemy(DynamicEntity tower) {
        DynamicEntity closestEntity = null;
        float range = ((Cannon) tower).GetRangeOfFire();
        float closestRange = range+1;
        float x = tower.getX();
        float y = tower.getY();
        float enemyX, enemyY;
        float distance;
        Iterator it = activeList.iterator();

        while (it.hasNext()) {
            DynamicEntity entity = (DynamicEntity) it.next();
            if (entity.IsAlive() && entity.getType() > 1 && entity.getType() < 5) {
                // This entity is enemy
                enemyX = entity.getX();
                enemyY = entity.getY();
                distance = dst2(x, y, enemyX, enemyY);
                if (distance < range*range) {
                    if (range < closestRange) {
                        closestRange = distance;
                        closestEntity = entity;
                    }
                }
            }
        }

        return closestEntity;
    }
}
