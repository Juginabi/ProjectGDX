package com.juginabi.towerdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TowerDefence extends ApplicationAdapter {
    // Tag of this app
    private static String TAG = "TowerDefence";

    Texture tex;
    OrthographicCamera cam;
    Viewport viewport;
    SpriteBatch batch;

    final Plane intersectPlane = new Plane(new Vector3(0, 0, 1), 0);
    final Vector3 intersectPos = new Vector3();

    final Vector3 curr = new Vector3();
    final Vector3 last = new Vector3(-1, -1, -1);
    final Vector3 delta = new Vector3();

    TiledMap map;
    TiledMapTileLayer BASE_LAYER;
    TiledMapTileLayer BUILD_LAYER;
    TiledMapTileLayer TOP_LAYER;
    TiledMapTileLayer OBJECTIVE_LAYER;
    TiledMapRenderer renderer;

    final int TILE_WIDTH = 64;
    final int TILE_HEIGHT = 64;
    final int MAP_WIDTH = 32;
    final int MAP_HEIGHT = 18;

    // Handles all input events
    EventHandler event = null;
	
	@Override
	public void create () {
        // Lets create camera
        cam = new OrthographicCamera();
        // Viewport for the camera
        viewport = new ExtendViewport(1920, 1080, cam);
        viewport.apply();
        // Set camera position to middle of viewport dimensions
        cam.position.set(MAP_WIDTH*TILE_WIDTH/2.f, MAP_HEIGHT*TILE_HEIGHT/2.f, 15);
        // Near clipping plane
        cam.near = 1;
        // Far clipping plane
        cam.far = 100;

        // Load our level1 TD map
        map = new TmxMapLoader().load("Maps/Level1.tmx");
        // Give map to tiledmap renderer
        renderer = new OrthogonalTiledMapRenderer(map);

        // Get layers we use at out TD map
        BASE_LAYER = (TiledMapTileLayer) map.getLayers().get("BASE_LAYER");
        BUILD_LAYER = (TiledMapTileLayer) map.getLayers().get("BUILD_LAYER");
        TOP_LAYER = (TiledMapTileLayer) map.getLayers().get("TOP_LAYER");
        OBJECTIVE_LAYER = (TiledMapTileLayer) map.getLayers().get("OBJECTIVE_LAYER");

        tex = new Texture("tankBlack.png");

        event = new EventHandler();
	}

    public void moveCamera () {
        EventHandler.CursorStatus status = event.getCursorStatus().get(0);
        if (status.getMouseLeft()) {
            float x = status.getPosition().x;
            float y = status.getPosition().y;
            Ray pickRay = cam.getPickRay(x, y);
            Intersector.intersectRayPlane(pickRay, intersectPlane, curr);

            if(!(last.x == -1 && last.y == -1 && last.z == -1)) {
                pickRay = cam.getPickRay(last.x, last.y);
                Intersector.intersectRayPlane(pickRay, intersectPlane, delta);
                delta.sub(curr);
                cam.position.add(delta.x, delta.y, delta.z);
                int totalWidth = MAP_WIDTH*TILE_WIDTH;
                int totalHEIGHT = MAP_HEIGHT*TILE_WIDTH;
                float viewPortWidth = cam.viewportWidth;
                float viewPortHeight = cam.viewportHeight;
                cam.position.x = MathUtils.clamp(cam.position.x,viewPortWidth/2f, totalWidth-viewPortWidth/2f);
                cam.position.y = MathUtils.clamp(cam.position.y,viewPortHeight/2f, totalHEIGHT/2f +(totalHEIGHT/2f - viewPortHeight/2f));

            }
            last.set(x, y, 0);
        }

    }

    private void checkTileTouched() {
        if(Gdx.input.justTouched()) {
            Ray pickRay = cam.getPickRay(Gdx.input.getX(), Gdx.input.getY());
            Intersector.intersectRayPlane(pickRay, intersectPlane, intersectPos);

            int x = (int)intersectPos.x / 64;
            int y = (int)intersectPos.y / 64;

            Gdx.app.log(TAG, "Intersection at location (" + x + ", " + y + ")");
            TiledMapTileLayer.Cell cell = BUILD_LAYER.getCell(x, y);
            if (cell != null)
                Gdx.app.log(TAG, "Something here!");
            else {
                Gdx.app.log(TAG, "Is this cell empty!?");
                cell = new TiledMapTileLayer.Cell();
                TextureRegion textureRegion = new TextureRegion(tex,64,64);
                StaticTiledMapTile tile = new StaticTiledMapTile(textureRegion);
                cell.setTile(tile);
                BUILD_LAYER.setCell(x,y, cell);
            }

        }
    }

	@Override
	public void render () {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.update();
        renderer.setView(cam);
        renderer.render();

        EventHandler.CursorStatus status = event.getCursorStatus().get(0);
        if (status.getMouseLeft() == false)
            last.set(-1, -1, -1);
        checkTileTouched();
        //moveCamera();
	}

    @Override
    public void resize(int width, int height) {
        // Dispose all the assets here and recreate
        Gdx.app.log(TAG, "resize event!");
        viewport.update(width,height);
    }

    @Override
    public void pause() {
        Gdx.app.log(TAG, "pause event!");
    }
    @Override
    public void resume() {
        Gdx.app.log(TAG, "resume event!");
    }

    public void dispose() {
        Gdx.app.log(TAG, "dispose event!");
    }
}
