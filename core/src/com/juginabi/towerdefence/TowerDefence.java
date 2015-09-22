package com.juginabi.towerdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.juginabi.towerdefence.GameEntities.DynamicMonster;
import com.juginabi.towerdefence.GameEntities.GameEntity;

public class TowerDefence extends ApplicationAdapter {

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
    private final int MAP_WIDTH = 16;
    private final int MAP_HEIGHT = 9;

    private long monsterSpawnTime;

    // Handles all input events
    private EventHandler event;
    public static AssetManager manager;
    private GameWorld gameWorld;
    private PhysicsWorld physicsWorld;

    private boolean worldInitialized = false;

    private Array<Body> bodies;

	@Override
	public void create () {
        // Event handler init
        event = new EventHandler();
        // Asset handling init
        manager = new AssetManager();
        loadAssets();

        // Gameworld which handles all dynamic entities in it
        physicsWorld = new PhysicsWorld(new Vector2(0, 0), true, false);
        gameWorld = new GameWorld(physicsWorld);

        // Lets create camera
        cam = new OrthographicCamera(MAP_WIDTH,MAP_HEIGHT);

        // Load our level1 TD map
        map = new TmxMapLoader().load("Graphics/Maps/Level1.tmx");
        // Give map to tiledmap renderer
        renderer = new OrthogonalTiledMapRenderer(map, 1/64f);

        // Get layers we use at out TD map
        BASE_LAYER = (TiledMapTileLayer) map.getLayers().get("BASE_LAYER");
        BUILD_LAYER = (TiledMapTileLayer) map.getLayers().get("BUILD_LAYER");
        TOP_LAYER = (TiledMapTileLayer) map.getLayers().get("TOP_LAYER");
        OBJECTIVE_LAYER = (TiledMapTileLayer) map.getLayers().get("OBJECTIVE_LAYER");

        monsterSpawnTime = TimeUtils.millis();

        this.bodies = new Array<Body>();
	}

    private void loadAssets() {
        // Texture assets
        manager.load("Graphics/EntityAtlas.txt", TextureAtlas.class);

        // Audio assets
        FileHandle[] files = Gdx.files.internal("Audio").list();
        for (FileHandle fi : files)
            Gdx.app.log(TAG, fi.name());
        manager.load("Graphics/topdown-nazi.png", Texture.class);
        manager.load("Graphics/topdown1.png", Texture.class);
        //manager.load("Audio/defaultlaser.ogg", Sound.class);
    }

    private void checkTileTouched() {
        if(Gdx.input.justTouched()) {
            Ray pickRay = cam.getPickRay(Gdx.input.getX(), Gdx.input.getY());
            Intersector.intersectRayPlane(pickRay, intersectPlane, intersectPos);

            int xx, yy;
            xx = Gdx.input.getX();
            yy = Gdx.input.getY();

            float x = intersectPos.x ;
            float y = intersectPos.y;

            Gdx.app.log(TAG, "Intersection at location (" + x + ", " + y + ")");
            TiledMapTileLayer.Cell cell = BUILD_LAYER.getCell((int)x, (int)y);
            if (cell != null) {
                Gdx.app.log(TAG, "Something here!");
            }
            else {
                gameWorld.SpawnEntity(GameEntity.ID_DEFENDER_TANK, x,y);
            }
        }
    }

	@Override
	public void render () {
        Gdx.gl.glClearColor(190 / 255f, 255 / 255f, 255 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getRawDeltaTime();

        if (manager.update()) {
            // All assets loaded
            cam.update();
            renderer.setView(cam);
            long time = TimeUtils.millis();

            if (!worldInitialized) {
                gameWorld.InitializeWorld();
                worldInitialized = true;
            }

            if (time - monsterSpawnTime > 1000) {
                SpawnEntity(DynamicMonster.ID_ENEMY_NAZI, 4, 9);
                monsterSpawnTime = time;
            }
            // Fill map with cannon towers.
            //FillMapWithCannonTowers(time);
            // Render base layers
            renderer.render(groundLayers);

            // Update the world state
            gameWorld.UpdateWorld(deltaTime);
            // Begin batch and start drawing entities and towers to the map
            Batch batch = renderer.getBatch();
            batch.begin();
            // Draw the world state using tiledMap Batch
            gameWorld.DrawWorld(batch);
            // End batch
            batch.end();

            physicsWorld.render(cam);
            // Render rest of the tilemap layers
            renderer.render(topLayers);
            physicsWorld.doPhysicsStep(deltaTime);

            // Remove dead bodies from world
            bodies.clear();
            physicsWorld.world_.getBodies(bodies);
            for (Body b : bodies) {
                GameEntity entity = (GameEntity) b.getUserData();
                if (entity != null && !entity.isAlive) {;
                    if (entity.body != null) {
                        physicsWorld.world_.destroyBody(entity.body);
                        entity.body.setUserData(null);
                        entity.body = null;
                    }
                }
            }

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

    private void SpawnEntity(int type, float x, float y) {
        gameWorld.SpawnEntity(type, x, y);
    }

    @Override
    public void resize(int width, int height) {
        // Dispose all the assets here and recreate
        float screenAR = width / (float) height;
        float mapAR = MAP_WIDTH / (float)MAP_HEIGHT;
        // Set camera always so it shows map centered vertically and horizontally filling the screen on shortest side
        if (mapAR > screenAR) {
            cam = new OrthographicCamera(MAP_WIDTH, MAP_WIDTH / screenAR);
            float mapHeight = ((width*MAP_HEIGHT)/MAP_WIDTH);
            float emptySpace = height - mapHeight;
            float tileHeight = mapHeight / MAP_HEIGHT;
            float tmp = emptySpace / tileHeight;
            cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2 - tmp/2, 0);
        } else {
            cam = new OrthographicCamera(MAP_HEIGHT*screenAR, MAP_HEIGHT);
            float mapWidth = ((height*MAP_WIDTH)/MAP_HEIGHT);
            float emptySpace = width - mapWidth;
            float tileHeight = mapWidth / MAP_WIDTH;
            float tmp = emptySpace / tileHeight;
            cam.position.set(cam.viewportWidth / 2 - tmp/2, cam.viewportHeight / 2, 0);
        }
        cam.update();
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

    /*public void moveCamera () {
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

    private void FillMapWithCannonTowers(long time) {
        if ((time - timeSinceMonstersStarted) > 1000 && spawnMore){
            for (int x = 0; x < MAP_WIDTH; ++x )
                for (int y = 0; y < MAP_HEIGHT; ++y) {
                    TiledMapTileLayer.Cell cell = BUILD_LAYER.getCell(x, y);
                    if (cell == null) {
                        DynamicEntity cannon = world.SpawnEntity(GameWorld.TowerCannon, x, y);
                        if (cannon != null) {
                            // Entity succesfully created
                            cannon.initialize(x, y);
                            //if (manager.isLoaded("Audio/threeTone2.ogg", Sound.class))
                            //    manager.get("Audio/threeTone2.ogg", Sound.class).play();
                        }
                    }
                }
            spawnMore = false;
        }
    }*/
}
