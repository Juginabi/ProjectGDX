package com.juginabi.towerdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.juginabi.towerdefence.GameEntities.Cannon;
import com.juginabi.towerdefence.GameEntities.PencilNeckedGeek;

import java.util.ArrayList;
import java.util.List;

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
    OrthogonalTiledMapRenderer renderer;

    final int TILE_WIDTH = 64;
    final int TILE_HEIGHT = 64;
    final int MAP_WIDTH = 32;
    final int MAP_HEIGHT = 18;

    List<PencilNeckedGeek> geeks;
    List<Cannon> cannons;

    // Handles all input events
    private EventHandler event = null;
    private static AssetManager manager = new AssetManager();
	
	@Override
	public void create () {
        loadAssets();

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
        map = new TmxMapLoader().load("Graphics/Maps/Level1.tmx");
        // Give map to tiledmap renderer
        renderer = new OrthogonalTiledMapRenderer(map);

        // Get layers we use at out TD map
        BASE_LAYER = (TiledMapTileLayer) map.getLayers().get("BASE_LAYER");
        BUILD_LAYER = (TiledMapTileLayer) map.getLayers().get("BUILD_LAYER");
        TOP_LAYER = (TiledMapTileLayer) map.getLayers().get("TOP_LAYER");
        OBJECTIVE_LAYER = (TiledMapTileLayer) map.getLayers().get("OBJECTIVE_LAYER");

        batch = new SpriteBatch();
        tex = new Texture("Graphics/tankBlack.png");
        geeks = new ArrayList<PencilNeckedGeek>();
        manager.finishLoading();
        for (int i = 0; i < 100; ++i) {
            PencilNeckedGeek geek = new PencilNeckedGeek(manager.get("Graphics/smiley.png", Texture.class));
            geek.setPosition(4 * TILE_HEIGHT, 17 * TILE_WIDTH);
            geek.setVelocity((float)Math.random() * 150 + 64);
            geeks.add(geek);
        }
        cannons = new ArrayList<Cannon>();

        event = new EventHandler();
	}

    private void loadAssets() {
        // Texture assets
        manager.load("Graphics/tankBlack.png", Texture.class);
        manager.load("Graphics/smiley.png", Texture.class);

        // Audio assets
        manager.load("Audio/threeTone2.ogg", Sound.class);
        manager.load("Audio/defaultLaser.ogg", Sound.class);

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

            int xx, yy;
            xx = Gdx.input.getX();
            yy = Gdx.input.getY();
            final Vector3 vector3 = cam.unproject(new Vector3(xx, yy, 0));

            int x = (int)intersectPos.x / 64;
            int y = (int)intersectPos.y / 64;

            Gdx.app.log(TAG, "Intersection at location (" + x + ", " + y + ")");
            TiledMapTileLayer.Cell cell = BUILD_LAYER.getCell(x, y);
            if (cell != null) {
                Gdx.app.log(TAG, "Something here!");
            }
            else {
                Cannon can = new Cannon(manager.get("Graphics/tankBlack.png", Texture.class));
                can.setPosition(x * 64, y * 64);
                cannons.add(can);
                if (manager.isLoaded("Audio/threeTone2.ogg", Sound.class))
                    manager.get("Audio/threeTone2.ogg", Sound.class).play();
            }
        }
    }

	@Override
	public void render () {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.update();
        renderer.setView(cam);
        int[] baseLayer = {0};
        renderer.render(baseLayer);
        renderer.getBatch().begin();
        for (PencilNeckedGeek geek : geeks) {
            geek.Update(Gdx.graphics.getDeltaTime());
            geek.draw(renderer.getBatch());
        }
        for (Cannon can : cannons) {
            can.Update(Gdx.graphics.getDeltaTime());
            can.draw(renderer.getBatch());
        }
        renderer.getBatch().end();
        int[] restLayers = {1,2,3};
        renderer.render(restLayers);

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
        batch.dispose();
        batch = new SpriteBatch();
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

    public static AssetManager getAssetManager() {
        return manager;
    }
}
