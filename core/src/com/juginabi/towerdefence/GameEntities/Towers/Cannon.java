package com.juginabi.towerdefence.GameEntities.Towers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.juginabi.towerdefence.GameEntities.DynamicEntity;
import com.juginabi.towerdefence.GameWorld;
import com.juginabi.towerdefence.GameEntities.Projectiles.Laser;
import com.juginabi.towerdefence.TileWorld;
import com.juginabi.towerdefence.TowerDefence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Juginabi on 03.03.2015.
 */
public class Cannon extends DynamicEntity {
    // Tag for logging purposes
    private static String TAG = "Cannon";

    // Last reload time. This controls how ofter this entity fires the weapon
    private double timeSinceLastFire = 0;

    // Reference to sound played when weapon is fired
    private Sound fireSound;

    // Reload time and damage
    private float reloadTime = 500f;
    private final float reloadVariance = (float) Math.random() * 200;
    private final float damage;
    // Randomize ray color for this cannon
    private final float beamColorValue = (float)Math.random();
    private Color beamColor = Color.RED;
    private final Color rayColor = Color.WHITE;

    // Range of fire
    private final float rangeOfFire_ = 4*64f;

    // Target entity
    private DynamicEntity target;

    List<TileWorld.Tile> tiles;

    public Cannon(GameWorld parent, TextureAtlas.AtlasRegion tex) {
        super(parent, tex, GameWorld.TowerCannon);
        tiles = new ArrayList<TileWorld.Tile>();
        timeSinceLastFire = TimeUtils.millis();
        damage = 5f;

        if (beamColorValue > 0.8f)
            beamColor = Color.BLUE;
        else if (beamColorValue > 0.6f)
            beamColor = Color.GREEN;
        else if (beamColorValue > 0.4f)
            beamColor = Color.PURPLE;
        else if (beamColorValue > 0.2f)
            beamColor = Color.WHITE;
        else if (beamColorValue > 0f)
            beamColor = Color.BLACK;

        SetStatusAlive(false);
    }

    public void Initialize(float x, float y) {
        setBounds(x,y, 1,1);
        isAlive_ = true;
        int tileX = (int)x;
        int tileY = (int)y;
        GetParentWorld().GetTilesInRange(tileX, tileY, 2, tiles);
    }

    public float GetRangeOfFire() {
        return this.rangeOfFire_;
    }

    @Override
    public boolean Update(float deltaTime) {
        if (!isAlive_)
            return false;
        if (TimeUtils.millis() - timeSinceLastFire > reloadTime) {
            GetTarget();
            if (target != null) {
                Fire();
            }
            timeSinceLastFire = TimeUtils.millis();
            // add little variance to reload time.
            reloadTime =  reloadVariance + 1000f;
        }
        return true;
    }

    private float getDistanceToEnemy(float x, float y) {
        return Vector2.dst(getX(), getY(), x, y);
    }

    private void GetTarget() {
        if (!tiles.isEmpty()) {
            List<DynamicEntity> entityList = new ArrayList<DynamicEntity>();
            Iterator it = tiles.iterator();
            while (it.hasNext()) {
                TileWorld.Tile tile = (TileWorld.Tile) it.next();
                tile.GetEntities(entityList);
            }
            if (!entityList.isEmpty()) {
                for (int i = 0; i < entityList.size(); ++i)
                    if (entityList.get(i).getType() > 1)
                        target = entityList.get(i);
            }
        }
    }

    private void Fire() {
        if (fireSound != null) {
            // TODO: Audio playing takes too much time because ever tower wants to play.
            //fireSound.play(0.4f);
        } else {
            // Get reference to fireSound if it has finished loading.
            final AssetManager manager = TowerDefence.getAssetManager();
            if (manager.isLoaded("Audio/defaultlaser.ogg"))
                fireSound = manager.get("Audio/defaultlaser.ogg");
        }
        /*Laser laser = (Laser) GetParentWorld().SpawnEntity(GameWorld.ProjectileLaser);
        if (laser != null) {
            laser.resetTotalTime();
            float lifetime = reloadTime / 2500f;
            laser.setLifeTime(lifetime);
            laser.setBeamColor(this.beamColor);
            laser.setRayColor(this.rayColor);
            laser.setPosition(getX(), getY());
            float distance = getDistanceToEnemy(target.getX(), target.getY());
            laser.setDistance(distance);
            laser.setDegrees(getDegrees(target.getX(), target.getY(), distance));
            laser.SetStatusAlive(true);
        }*/
        if (target.inflictDamage(damage))
            target = null;
    }

    public void Draw(Batch batch) {
        if (!IsAlive()) {
            return;
        }
        super.draw(batch);
    }

    private float getDegrees(float x, float y, float distance) {
        float diffX = x - getX();
        float diffY = y - getY();

        if (diffX >= 0 && diffY >= 0) {
            double rad = Math.asin(diffY / distance);
            double degrees = 90 - rad*180/Math.PI;
            return (float)(360 - degrees);
        } else if (diffX < 0 && diffY >= 0) {
            double rad = Math.asin(Math.abs(diffY)/distance);
            double degrees = 90 - rad*180/Math.PI;
            return (float)degrees;
        } else if (diffX < 0 && diffY < 0) {
            double rad = Math.asin(Math.abs(diffY)/distance);
            double degrees = rad*180/Math.PI;
            return (float)(180 - (90 - degrees));

        } else {
            double rad = Math.asin(Math.abs(diffY)/distance);
            double degrees = rad*180/Math.PI;
            return (float)(270 - degrees);
        }
    }
}
