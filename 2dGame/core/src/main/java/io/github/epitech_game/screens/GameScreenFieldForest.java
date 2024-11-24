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
import io.github.epitech_game.Fly; // Ensure Fly is correctly implemented
import io.github.epitech_game.Main;
import io.github.epitech_game.SpawnDirection;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameScreenFieldForest implements Screen {

    private Main game;
    private SpawnDirection spawnDirection;

    private Hero hero;
    private ArrayList<Enemy> enemies;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private OrthographicCamera hudCamera;
    private Viewport viewport;
    private BitmapFont font;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private int mapWidth, mapHeight;

    private boolean isPaused = false;

    // Pause menu variables
    private String[] pauseMenuOptions = {"Resume", "Settings", "Credits"};
    private int selectedOption = 0;
    private boolean inCredits = false;

    private TiledMapTileLayer collisionLayer;

    public GameScreenFieldForest(Main game, SpawnDirection spawnDirection) {
        this.game = game;
        this.spawnDirection = spawnDirection;
        initialize();
    }

    private void initialize() {
        try {
            // Load Tiled map
            TmxMapLoader mapLoader = new TmxMapLoader();
            map = mapLoader.load("maps/newmap.tmx");
            mapRenderer = new OrthogonalTiledMapRenderer(map);
            Gdx.app.log("GameScreen2", "Successfully loaded newmap.tmx");
        } catch (Exception e) {
            Gdx.app.error("GameScreen2", "Failed to load newmap.tmx", e);
            // Transition to an error screen or exit the game
            Gdx.app.exit();
            return;
        }

        // Get map dimensions
        try {
            mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
            mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);
            Gdx.app.log("GameScreen2", "Map dimensions: " + mapWidth + "x" + mapHeight);
        } catch (Exception e) {
            Gdx.app.error("GameScreen2", "Failed to retrieve map dimensions", e);
            Gdx.app.exit();
            return;
        }

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
                Gdx.app.error("GameScreen2", "'collisions' layer not found in newmap.tmx");
                Gdx.app.exit();
                return;
            }
            Gdx.app.log("GameScreen2", "'collisions' layer successfully retrieved");
        } catch (Exception e) {
            Gdx.app.error("GameScreen2", "Error accessing 'collisions' layer", e);
            Gdx.app.exit();
            return;
        }

        // Initialize hero
        try {
            hero = new Hero(collisionLayer);
            setHeroSpawnPosition();
            Gdx.app.log("GameScreen2", "Hero initialized and positioned");
        } catch (Exception e) {
            Gdx.app.error("GameScreen2", "Failed to initialize hero", e);
            Gdx.app.exit();
            return;
        }

        // Initialize enemies
        try {
            enemies = new ArrayList<>();
            Fly enemy1 = new Fly(hero); // Ensure Fly is correctly implemented
            enemy1.setPosition(400, 400);
            enemies.add(enemy1);
            Gdx.app.log("GameScreen2", "Enemy1 (Fly) initialized at (400,400)");

            Fly enemy2 = new Fly(hero);
            enemy2.setPosition(600, 600);
            enemies.add(enemy2);
            Gdx.app.log("GameScreen2", "Enemy2 (Fly) initialized at (600,600)");
        } catch (Exception e) {
            Gdx.app.error("GameScreen2", "Failed to initialize enemies", e);
            Gdx.app.exit();
            return;
        }

        // Initialize rendering tools
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
    }

    private void setHeroSpawnPosition() {
        float heroWidth = hero.getWidth();
        float heroHeight = hero.getHeight();

        switch (spawnDirection) {
            case FROM_TOP:
                hero.setPosition(mapWidth / 2f, mapHeight - heroHeight - 10);
                break;
            case FROM_BOTTOM:
                hero.setPosition(mapWidth / 2f, 10);
                break;
            case FROM_LEFT:
                hero.setPosition(10, mapHeight / 2f);
                break;
            case FROM_RIGHT:
                hero.setPosition(mapWidth - heroWidth - 10, mapHeight / 2f);
                break;
            case CENTER:
                hero.setPosition(mapWidth / 2f - heroWidth / 2f, mapHeight / 2f - heroHeight / 2f);
                break;
            default:
                hero.setPosition(mapWidth / 2f, mapHeight / 2f);
                break;
        }
        Gdx.app.log("GameScreen2", "Hero spawn direction: " + spawnDirection);
    }

    @Override
    public void render(float delta) {
        // Handle pause input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            Gdx.app.log("GameScreen2", "Pause toggled: " + isPaused);
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

        spriteBatch.end();

        // Render HUD elements (hearts, pause menu)
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        spriteBatch.begin();
        hero.renderHearts(spriteBatch);

        if (isPaused) {
            renderPauseMenu();
        }
        spriteBatch.end();
    }

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
                    Gdx.app.log("GameScreen2", "Enemy took damage from hero attack");
                }
            }

            // Check for collisions with hero
            if (enemy.isAlive()) {
                Rectangle enemyBounds = enemy.getBounds();
                if (heroBounds.overlaps(enemyBounds)) {
                    hero.takeDamage(enemy.getAp());
                    Gdx.app.log("GameScreen2", "Hero took damage from enemy");
                }
            }

            // Remove dead enemies
            if (!enemy.isAlive() && !enemy.isDying()) {
                enemy.dispose();
                enemyIterator.remove();
                Gdx.app.log("GameScreen2", "Enemy disposed and removed");
            }
        }

        // Add new enemies to the list
        enemies.addAll(newEnemies);

        // Check if hero crosses the screen border and determine the spawn direction
        checkScreenBorders();

        // Update the camera to follow the hero
        camera.position.set(
            Math.min(Math.max(hero.getX(), camera.viewportWidth / 2), mapWidth - camera.viewportWidth / 2),
            Math.min(Math.max(hero.getY(), camera.viewportHeight / 2), mapHeight - camera.viewportHeight / 2),
            0
        );
        camera.update();
    }

    private void checkScreenBorders() {
        float heroX = hero.getX();
        float heroY = hero.getY();
        float heroWidth = hero.getWidth();
        float heroHeight = hero.getHeight();

        if (heroX < 0) {
            Gdx.app.log("GameScreen2", "Hero crossed the left border");
            transitionToNewScreen(SpawnDirection.FROM_LEFT);
        } else if (heroX + heroWidth > mapWidth) {
            Gdx.app.log("GameScreen2", "Hero crossed the right border");
            transitionToNewScreen(SpawnDirection.FROM_RIGHT);
        } else if (heroY + heroHeight > mapHeight) {
            Gdx.app.log("GameScreen2", "Hero crossed the top border");
            transitionToNewScreen(SpawnDirection.FROM_TOP);
        } else if (heroY < 0) {
            Gdx.app.log("GameScreen2", "Hero crossed the bottom border");
            transitionToNewScreen(SpawnDirection.FROM_BOTTOM);
        }
    }

    private void transitionToNewScreen(SpawnDirection direction) {
        // Dispose current screen resources if necessary
        dispose();
        Gdx.app.log("GameScreen2", "Transitioning to new screen with spawn direction: " + direction);

        // Determine which screen to transition to based on direction
        switch (direction) {
            case FROM_BOTTOM:
                // Transition back to Overworld (GameScreen)
                game.setScreen(new GameScreen(game, SpawnDirection.FROM_TOP));
                break;
            // Add more cases if transitioning to other screens
            default:
                // For simplicity, stay on the same screen or handle other transitions
                game.setScreen(new GameScreenFieldForest(game, direction));
                break;
        }
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

    private void renderPauseMenu() {
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        spriteBatch.begin();
        if (inCredits) {
            font.draw(spriteBatch, "Credits\nGame developed by [Your Name]",
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
        Gdx.app.log("GameScreen2", "Resources disposed");
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {
        isPaused = true;
        Gdx.app.log("GameScreen2", "Game paused");
    }

    @Override
    public void resume() {
        isPaused = false;
        Gdx.app.log("GameScreen2", "Game resumed");
    }
}
