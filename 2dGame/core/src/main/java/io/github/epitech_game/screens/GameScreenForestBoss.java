package io.github.epitech_game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.epitech_game.Hero;
import io.github.epitech_game.Enemy;
import io.github.epitech_game.NPC2;
import io.github.epitech_game.NPC1;
import io.github.epitech_game.NPC;
import io.github.epitech_game.Main;
import io.github.epitech_game.SpawnDirection;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameScreenForestBoss implements Screen {

    private Main game;
    private SpawnDirection spawnDirection;

    private Hero hero;
    private ArrayList<Enemy> enemies;
    private ArrayList<NPC> npcs;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private OrthographicCamera hudCamera;
    private Viewport viewport;
    private BitmapFont font;
    private float stateTime ;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private int mapWidth, mapHeight;

    private boolean isPaused = false;

    // Pause menu variables
    private String[] pauseMenuOptions = {"Resume", "Settings", "Credits"};
    private int selectedOption = 0;
    private boolean inCredits = false;

    private TiledMapTileLayer collisionLayer;

    public GameScreenForestBoss(Main game, SpawnDirection spawnDirection) {
        this.game = game;
        this.spawnDirection = spawnDirection;
        initialize();
    }

    private void initialize() {
        try {
            // Load Tiled map
            TmxMapLoader mapLoader = new TmxMapLoader();
            map = mapLoader.load("maps/overworld.tmx");
            mapRenderer = new OrthogonalTiledMapRenderer(map);
            Gdx.app.log("GameScreenForestBoss", "Successfully loaded overworld.tmx");
        } catch (Exception e) {
            Gdx.app.error("GameScreenForestBoss", "Failed to load overworld.tmx", e);
            Gdx.app.exit();
            return;
        }

        // Get map dimensions
        try {
            mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
            mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);
            Gdx.app.log("GameScreenForestBoss", "Map dimensions: " + mapWidth + "x" + mapHeight);
        } catch (Exception e) {
            Gdx.app.error("GameScreenForestBoss", "Failed to retrieve map dimensions", e);
            Gdx.app.exit();
            return;
        }

        stateTime = 0f;

        // Initialize camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(mapWidth, mapHeight, camera);
        viewport.apply();

        camera.position.set(mapWidth / 2f, mapHeight / 2f, 0);
        camera.update();

        // Initialize HUD camera
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Access collision layer
        try {
            collisionLayer = (TiledMapTileLayer) map.getLayers().get("collisions");
            if (collisionLayer == null) {
                Gdx.app.error("GameScreenForestBoss", "'collisions' layer not found in overworld.tmx");
                Gdx.app.exit();
                return;
            }
            Gdx.app.log("GameScreenForestBoss", "'collisions' layer successfully retrieved");
        } catch (Exception e) {
            Gdx.app.error("GameScreenForestBoss", "Error accessing 'collisions' layer", e);
            Gdx.app.exit();
            return;
        }

        // Initialize hero
        try {
            hero = new Hero(collisionLayer);
            setHeroSpawnPosition();
            Gdx.app.log("GameScreenForestBoss", "Hero initialized and positioned");
        } catch (Exception e) {
            Gdx.app.error("GameScreenForestBoss", "Failed to initialize hero", e);
            Gdx.app.exit();
            return;
        }

        // Initialize enemies
        try {
            enemies = new ArrayList<>();

            Gdx.app.log("GameScreenForestBoss", "Enemy1 (Fly) initialized at (300,300)");

        } catch (Exception e) {
            Gdx.app.error("GameScreenForestBoss", "Failed to initialize enemies", e);
            Gdx.app.exit();
            return;
        }

        try {
            npcs = new ArrayList<>();
            NPC2 npc2 = new NPC2();
            NPC1 npc1 = new NPC1();
            npc2.setPosition(170, 285);
            npc1.setPosition(340, 235);
            npcs.add(npc2);
            npcs.add(npc1);
            Gdx.app.log("GameScreenForestBoss", "Enemy1 (Fly) initialized at (300,300)");

        } catch (Exception e) {
            Gdx.app.error("GameScreenForestBoss", "Failed to initialize enemies", e);
            Gdx.app.exit();
            return;
        }



        // Initialize rendering tools
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
    }
    /**
     * Sets the hero's spawn position based on the spawn direction.
     */
    private void setHeroSpawnPosition() {
        float heroWidth = hero.getWidth();   // Ensure Hero class has getWidth()
        float heroHeight = hero.getHeight(); // Ensure Hero class has getHeight()

        switch (spawnDirection) {
            case FROM_TOP:
                hero.setPosition(mapWidth / 2f, mapHeight - heroHeight - 10); // Top center with padding
                break;
            case FROM_BOTTOM:
                hero.setPosition(mapWidth / 2f, 10); // Bottom center with padding
                break;
            case FROM_LEFT:
                hero.setPosition(10, mapHeight / 2f); // Left center with padding
                break;
            case FROM_RIGHT:
                hero.setPosition(mapWidth - heroWidth - 10, mapHeight / 2f); // Right center with padding
                break;
            case CENTER:
                hero.setPosition(mapWidth / 2f - heroWidth / 2f, mapHeight / 2f - heroHeight / 2f); // Center of the screen
                break;
        }
    }

    @Override
    public void render(float delta) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        stateTime += Gdx.graphics.getDeltaTime();
        // Handle pause input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
        }

        if (!isPaused) {
            if (!hero.isDead()) {
                updateGameLogic(delta);
            } else {
                renderGameOver();
                return;
            }
        }

        // Clear the screen and render the map
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mapRenderer.setView(camera);
        mapRenderer.render();

        // Render the game elements
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        hero.render(spriteBatch);

        for (Enemy enemy : enemies) {
            enemy.render(spriteBatch);
        }

        for (NPC npc : npcs) {
            npc.checkHeroInZone(hero);
        }
        for (NPC npc : npcs) {
            npc.render(spriteBatch, stateTime);
            String dialogueLine = npc.updateDialogue(deltaTime);
            if (dialogueLine != null) {
                font.getData().setScale(0.8f); // Adjust scale as needed
                font.draw(spriteBatch, npc.getName() + ": " + dialogueLine,
                    50, 50); // Adjust Y position as needed
            }
        }

        spriteBatch.end();
        // Render HUD elements (hearts, pause menu)
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        spriteBatch.begin();
        hero.renderHearts(spriteBatch);

        if (isPaused) {
            renderPauseMenu();
        }
        spriteBatch.end(); // Ensure SpriteBatch is closed properly
    }

    /**
     * Updates game logic, including hero movement, enemy behavior, and collision detection.
     *
     * @param delta Time elapsed since the last frame.
     */
    private void updateGameLogic(float delta) {
        hero.update();
        hero.move();

        Rectangle attackBounds = hero.getAttackBounds();
        Rectangle heroBounds = hero.getBounds();

        // Create a list to collect new enemies
        List<Enemy> newEnemies = new ArrayList<>();

        // Update enemies and check collisions
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.update(newEnemies);

            // Check for collisions with hero attack
            if (attackBounds != null && enemy.isAlive()) {
                Rectangle enemyBounds = enemy.getBounds();
                if (attackBounds.overlaps(enemyBounds)) {
                    enemy.takeDamage(hero.getAp());
                }
            }

            // Check for collisions with hero
            if (enemy.isAlive()) {
                Rectangle enemyBounds = enemy.getBounds();
                if (heroBounds.overlaps(enemyBounds)) {
                    // Implement enemy-specific damage logic
                    hero.takeDamage(enemy.getAp());
                }
            }

            // Remove dead enemies
            if (!enemy.isAlive() && !enemy.isDying()) {
                enemy.dispose();
                enemyIterator.remove();
            }
        }

        // Add new enemies to the list
        enemies.addAll(newEnemies);

        // Update the camera to follow the hero
        camera.position.set(
            Math.min(Math.max(hero.getX(), camera.viewportWidth / 2), mapWidth - camera.viewportWidth / 2),
            Math.min(Math.max(hero.getY(), camera.viewportHeight / 2), mapHeight - camera.viewportHeight / 2),
            0
        );
        camera.update();
    }


    private void renderGameOver() {
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        font.setColor(Color.RED);
        font.getData().setScale(3);
        font.draw(spriteBatch, "Game Over",
            Gdx.graphics.getWidth() / 2f - 100,
            Gdx.graphics.getHeight() / 2f);
        spriteBatch.end();
    }

    /**
     * Renders the pause menu.
     */
    private void renderPauseMenu() {
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        spriteBatch.begin();
        if (inCredits) {
            font.draw(spriteBatch, "Credits\nGame developed by Alain & Kassi",
                100, Gdx.graphics.getHeight() - 100);
            font.draw(spriteBatch, "Press ESCAPE to return",
                100, Gdx.graphics.getHeight() - 200);
        } else {
            float menuX = Gdx.graphics.getWidth() / 2f - 50;
            float menuY = Gdx.graphics.getHeight() / 2f + 50;
            for (int i = 0; i < pauseMenuOptions.length; i++) {
                font.setColor(i == selectedOption ? Color.YELLOW : Color.WHITE);
                font.draw(spriteBatch, pauseMenuOptions[i],
                    menuX, menuY - i * 30);
            }
        }
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hudCamera.setToOrtho(false, width, height);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
        map.dispose();
        mapRenderer.dispose();
        hero.dispose();
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {
        // Handle any logic for pausing the game if necessary
        isPaused = true;
    }

    @Override
    public void resume() {
        // Handle any logic for resuming the game if necessary
        isPaused = false;
    }
}
