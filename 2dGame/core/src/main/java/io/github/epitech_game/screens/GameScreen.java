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
import io.github.epitech_game.Fly;
import io.github.epitech_game.ForestBoss;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameScreen implements Screen {

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

    public GameScreen() {
        // Load Tiled map
        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps/overworld.tmx"); // Ensure this file is in the assets folder
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        // Get map dimensions
        mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);

        // Initialize camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(mapWidth, mapHeight, camera);
        viewport.apply();

        camera.position.set(mapWidth / 2f, mapHeight / 2f, 0);
        camera.update();

        // Initialize HUD camera
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        collisionLayer = (TiledMapTileLayer) map.getLayers().get("collisions");
        // Initialize hero
        hero = new Hero(collisionLayer);
        hero.setPosition(100, 100); // Starting position for the hero



        // Initialize enemies
        enemies = new ArrayList<>();
        Fly fly = new Fly(hero);
        fly.setPosition(300, 300);
        enemies.add(fly);

        ForestBoss forestBoss = new ForestBoss(hero);
        forestBoss.setPosition(500, 500);
        enemies.add(forestBoss);

        // Initialize rendering tools
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
    }

    @Override
    public void render(float delta) {
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

        spriteBatch.end();
        // Render HUD elements (hearts, pause menu)
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        spriteBatch.begin();
        hero.renderHearts(spriteBatch);

        if (isPaused) {
            renderPauseMenu();
        }
        spriteBatch.end(); // Add this line
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
                }
            }

            // Check for collisions with hero
            if (enemy.isAlive()) {
                Rectangle enemyBounds = enemy.getBounds();
                if (heroBounds.overlaps(enemyBounds)) {
                    if (enemy instanceof ForestBoss) {
                        ForestBoss forestBoss = (ForestBoss) enemy;
                        if (forestBoss.isAttacking() && forestBoss.isInDamageFrame()) {
                            hero.takeDamage(forestBoss.getAp() * 2);
                        }
                    } else {
                        hero.takeDamage(enemy.getAp());
                    }
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
