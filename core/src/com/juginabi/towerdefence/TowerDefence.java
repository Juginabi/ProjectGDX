package com.juginabi.towerdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.juginabi.towerdefence.GameEntities.DynamicEntity;
import com.juginabi.towerdefence.GameEntities.Monsters.JesseMonster;
import com.juginabi.towerdefence.GameEntities.Monsters.PencilNeckedGeek;
import com.juginabi.towerdefence.GameEntities.Towers.Cannon;

public class TowerDefence extends ApplicationAdapter {
    private BitmapFont font;

    // Tag of this app
    private static String TAG = "TowerDefence";

    private OrthographicCamera cam;
    private Viewport viewport;

    private final Plane intersectPlane = new Plane(new Vector3(0, 0, 1), 0);
    private final Vector3 intersectPos = new Vector3();

    // Variables for camera moving
    private final Vector3 curr = new Vector3();
    private final Vector3 last = new Vector3(-1, -1, -1);
    private final Vector3 delta = new Vector3();

    // Map and map layers
    private TiledMap map;
    private TiledMapTileLayer BASE_LAYER;
    private TiledMapTileLayer BUILD_LAYER;
    private TiledMapTileLayer TOP_LAYER;
    private TiledMapTileLayer OBJECTIVE_LAYER;
    private int[] groundLayers = {0,1,2};
    private int[] topLayers = {3,4};
    // Tiled map renderer
    private OrthogonalTiledMapRenderer renderer;

    // Map width/height properties
    private final int TILE_WIDTH = 64;
    private final int TILE_HEIGHT = 64;
    private final int MAP_WIDTH = 32;
    private final int MAP_HEIGHT = 18;

    private long timeSinceSpawn;
    private long spawnInterval;
    private long jesseSpawnInterval;
    private long timeSinceJesseSpawn;
    private long timeSinceMonstersStarted;
    private boolean spawnMore = true;

    // Handles all input events
    private EventHandler event = null;
    private static AssetManager manager;
    private GameWorld world;

    private boolean worldInitialized = false;

    public interface traceInterface {
        public void startTrace(String name);
    }

	@Override
	public void create () {
        Gdx.app.log(TAG, "Create!");
        // Event handler init
        event = new EventHandler();
        // Asset handling init
        manager = new AssetManager();
        loadAssets();

        // Gameworld which handles all dynamic entities in it
        world = new GameWorld();

        // Lets create camera
        cam = new OrthographicCamera();
        // Viewport for the camera
        viewport = new ExtendViewport(1920, 1080, cam);
        viewport.apply();
        // Set camera position to middle of viewport dimensions
        cam.position.set(MAP_WIDTH*TILE_WIDTH/2.f-TILE_WIDTH/2.f, MAP_HEIGHT*TILE_HEIGHT/2.f, 15);
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

        timeSinceSpawn = TimeUtils.millis();
        timeSinceJesseSpawn = TimeUtils.millis();
        timeSinceMonstersStarted = TimeUtils.millis();

        spawnInterval = 200;
        jesseSpawnInterval = 500;

        font = new BitmapFont();
        font.setColor(Color.RED);
	}

    private void loadAssets() {
        // Texture assets
        manager.load("Graphics/EntityAtlas.txt", TextureAtlas.class);

        // Audio assets
        FileHandle[] files = Gdx.files.internal("Audio").list();
        for (FileHandle fi : files)
            Gdx.app.log(TAG, fi.name());
        manager.load("Audio/threeTone2.ogg", Sound.class);
        manager.load("Audio/defaultlaser.ogg", Sound.class);
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

            int x = (int)intersectPos.x / 64;
            int y = (int)intersectPos.y / 64;

            Gdx.app.log(TAG, "Intersection at location (" + x + ", " + y + ")");
            TiledMapTileLayer.Cell cell = BUILD_LAYER.getCell(x, y);
            if (cell != null) {
                Gdx.app.log(TAG, "Something here!");
            }
            else {
                DynamicEntity cannon = world.SpawnEntity(GameWorld.TowerCannon, x, y);
                if (cannon != null) {
                    // Entity succesfully created
                    ((Cannon)cannon).Initialize(x * TILE_WIDTH, y * TILE_HEIGHT);
                    //cannon.setPosition(x * TILE_WIDTH, y * TILE_HEIGHT);
                    //cannon.SetStatusAlive(true);
                    if (manager.isLoaded("Audio/threeTone2.ogg", Sound.class))
                        manager.get("Audio/threeTone2.ogg", Sound.class).play();
                }
            }
        }
    }

	@Override
	public void render () {
        Gdx.gl.glClearColor(175/255f,238/255f,238/255f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getRawDeltaTime();

        if (manager.update()) {
            // All assets loaded
            cam.update();
            renderer.setView(cam);
            long time = TimeUtils.millis();

            if (!worldInitialized) {
                world.InitializeWorld();
                worldInitialized = true;
            }

            SpawnGeek(time);
            SpawnJesse(time);
            // Fill map with cannon towers.
            FillMapWithCannonTowers(time);
            // Render base layers
            renderer.render(groundLayers);

            // Update the world state
            world.UpdateWorld(deltaTime);

            // Begin batch and start drawing entities and towers to the map
            renderer.getBatch().begin();
            // Draw the world state using tiledMap Batch
            world.DrawWorld(renderer.getBatch());
            // End batch
            renderer.getBatch().end();

            // Render rest of the tilemap layers
            renderer.render(topLayers);

            // Check for cursor status and reset mouse position if button released
            EventHandler.CursorStatus status = event.getCursorStatus().get(0);
            if (!status.getMouseLeft())
                last.set(-1, -1, -1);
            // Check if tile touched and add new tower to touched location if possible
            checkTileTouched();
            //moveCamera();

        } else {
            // Continue loading assets
            float progress = manager.getProgress();
            Gdx.app.log(TAG, "Asset manager load progress: " + String.format("%.2f", progress * 100));
        }
	}

    private void SpawnGeek(long time) {
        if ((time - timeSinceSpawn) > spawnInterval) {
            // Spawn another geek
            DynamicEntity geek = world.SpawnEntity(GameWorld.EnemyGeek, 4, 17);
            if (geek != null) {
                ((PencilNeckedGeek)geek).Initialize();
            }
            timeSinceSpawn = TimeUtils.millis();
            //spawnInterval -= 2;
            //if (spawnInterval < 100)
            //    spawnInterval = 100;
        }
    }

    private void SpawnJesse(long time) {
        if ((time - timeSinceJesseSpawn) > jesseSpawnInterval) {
            // Spawn another jesse
            DynamicEntity jesse = world.SpawnEntity(GameWorld.EnemyJesse, 4, 17);
            if (jesse != null) {
                ((JesseMonster)jesse).Initialize();
            }
            timeSinceJesseSpawn = TimeUtils.millis();
            //jesseSpawnInterval -= 1;
            //if (spawnInterval < 300)
            //    spawnInterval = 300;
        }
    }

    private void FillMapWithCannonTowers(long time) {
        if ((time - timeSinceMonstersStarted) > 1000 && spawnMore){
            for (int x = TILE_WIDTH; x < 64 * MAP_WIDTH; x += 64)
                for (int y = TILE_HEIGHT; y < 64 * MAP_HEIGHT; y += 64) {
                    TiledMapTileLayer.Cell cell = BUILD_LAYER.getCell(x / 64, y / 64);
                    if (cell == null) {
                        DynamicEntity cannon = world.SpawnEntity(GameWorld.TowerCannon, x/64, y/64);
                        if (cannon != null) {
                            // Entity succesfully created
                            ((Cannon)cannon).Initialize(x,y);
                            Gdx.app.log(TAG, "Setting tower: " + x + ", " + y);
                            //if (manager.isLoaded("Audio/threeTone2.ogg", Sound.class))
                            //    manager.get("Audio/threeTone2.ogg", Sound.class).play();
                        }
                    }
                }
            spawnMore = false;
        }
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
        manager.dispose();
        renderer.dispose();
        Gdx.app.log(TAG, "dispose event!");
    }

    public static AssetManager getAssetManager() {
        return manager;
    }
}
