package com.juginabi.towerdefence.Obsolete;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.juginabi.towerdefence.GameEntities.DynamicMonster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Juginabi on 10.03.2015.
 */
public class TileWorld {
    private Map< Integer, List<Tile> > tileMap;
    private int mapWidth;
    private int mapHeight;

    @Deprecated
    public TileWorld(int width, int height) {
        tileMap = new HashMap< Integer, List<Tile> >();
        this.mapWidth = width;
        this.mapHeight = height;
        for (int i = 0; i < mapWidth; ++i) {
            List<Tile> newColumn = new ArrayList<Tile>();
            for (int j = 0; j < mapHeight; ++j) {
                newColumn.add(new Tile(i, j));
            }
            tileMap.put(i, newColumn);
        }
    }

    @Deprecated
    public void Update(float tickSeconds, Stack<DynamicMonster> deadStack) {
        int i = 0;
        Stack<DynamicMonster> transferList = new Stack<DynamicMonster>();
        // This while loop updates the entity positions in map for each tile
        while (i < tileMap.size()) {
            List<Tile> tileColumn = tileMap.get(i);
            for (int tile = 0; tile < tileColumn.size(); ++tile) {
                Tile currentTile  = tileColumn.get(tile);
                currentTile.Update(tickSeconds);
                // if tile reports it has entities which have moved out of bounds of this tile, get the list.
                currentTile.getTransferList(transferList);
            }
            ++i;
        }
        float x;
        float y;
        // move entities to correct tile area.
        while (!transferList.isEmpty()) {
            DynamicMonster entity = transferList.pop();
            x = entity.getX()/64f;
            y = entity.getY()/64f;
            if (checkMapBounds(x,y)) {
                tileMap.get((int) x).get((int) y).InsertEntity(entity);
            }
            else {
                // Entity is out of the map for some reason
                entity.removeThisEntity = true;
                deadStack.push(entity);
            }
        }
    }

    @Deprecated
    private boolean checkMapBounds(float x, float y) {
        return (x >= 0 && y >= 0 && x <= mapWidth && y <= mapHeight);
    }

    @Deprecated
    public void Draw(Batch batch) {
        // TODO Draw world in correct order and from correct direction
        int i = 0;
        // This while loop updates the entity positions in map for each tile
        while (i < tileMap.size()) {
            List<Tile> tileColumn = tileMap.get(i);
            for (int tile = 0; tile < tileColumn.size(); ++tile) {
                Tile currentTile  = tileColumn.get(tile);
                currentTile.Draw(batch);
            }
            ++i;
        }
    }

    @Deprecated
    public void InsertEntity(float x, float y, DynamicMonster entity) {
        Tile tile = GetTile(x, y);
        tile.InsertEntity(entity);
    }

    @Deprecated
    public Tile GetTile(float x, float y) {
        return tileMap.get((int)x).get((int)y);
    }

    @Deprecated
    public class Tile {
        private final String TAG;
        public final int x;
        public final int y;
        private int lowerX;
        private int lowerY;
        private int upperX;
        private int upperY;
        private List<DynamicMonster> entityList;
        private Stack<DynamicMonster> transferList;
        @Deprecated
        public Tile(int x, int y) {
            // I am a tile and this is my neighbourhood!
            this.x = x;
            this.y = y;
            this.lowerX = this.x * 64;
            this.lowerY = this.y * 64;
            this.upperX = this.lowerX + 63;
            this.upperY = this.lowerY + 63;
            entityList = new ArrayList<DynamicMonster>();
            transferList = new Stack<DynamicMonster>();
            TAG = "TILE(" + x + "," + y + ")";
        }
        @Deprecated
        public void GetEntities(List<DynamicMonster> list) {
            if (!entityList.isEmpty()) {
                for (int i = 0; i < entityList.size(); ++i)
                    list.add(entityList.get(i));
            }
        }
        @Deprecated
        public void Update(float tickSeconds) {
            if (entityList.size() > 0) {
                Iterator it = entityList.iterator();
                while (it.hasNext()) {
                    DynamicMonster entity = (DynamicMonster) it.next();
                    entity.Update(tickSeconds);
                    if (!checkBounds(entity.getX(), entity.getY())) {
                        // Entity is out of bounds from this tile.
                        transferList.push(entity);
                        it.remove();
                    }
                }
            }
        }
        @Deprecated
        public void InsertEntity(DynamicMonster entity) {
            if (!entityList.contains(entity)) {
                entityList.add(entity);
            }
        }
        @Deprecated
        public void RemoveEntity(DynamicMonster entity) {
            if (entityList.contains(entity))
                entityList.remove(entity);
        }
        @Deprecated
        private boolean checkBounds(float x, float y) {
            if (x < this.lowerX || x > upperX || y < lowerY || y > upperY)
                return false;
            else
                return true;
        }
        @Deprecated
        public void getTransferList(Stack<DynamicMonster> masterList) {
            while (!transferList.isEmpty()) masterList.push(transferList.pop());
        }
        @Deprecated
        public void Draw(Batch batch) {
            if (entityList.size() > 0) {
                Iterator it = entityList.iterator();
                while (it.hasNext()) {
                    DynamicMonster entity = (DynamicMonster) it.next();
                    entity.Draw(batch);
                }
            }
        }
    } // Tile end

}// TileMap end

