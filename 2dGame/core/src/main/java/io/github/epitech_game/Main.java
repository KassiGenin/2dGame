package io.github.epitech_game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main extends ApplicationAdapter {
    public static final float SCALE_FACTOR = 2.5f; // Scaling sprites to 2.5x their original size

    private Hero hero;
    private ArrayList<Enemy> enemies;
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    private BitmapFont font;
    private float stateTime;

    private boolean isPaused = false;

    // Pause menu variables
    private String[] pauseMenuOptions = {"Resume", "Settings", "Credits"};
    private int selectedOption = 0;
    private boolean inCredits = false;

    @Override
    public void create() {
        // Initialize characters
        hero = new Hero();
        enemies = new ArrayList<>();

        // Add initial fly
        Fly fly = new Fly(hero);
        fly.setPosition(300, 300);
        enemies.add(fly);

        // Add forest boss
        ForestBoss forestBoss = new ForestBoss(hero);
        forestBoss.setPosition(500, 500);
        enemies.add(forestBoss);

        // Initialize rendering tools
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        stateTime = 0f;
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Handle pause input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
        }

        if (!isPaused) {
            if (!hero.isDead()) {
                hero.update();
                hero.move();

                Rectangle attackBounds = hero.getAttackBounds();
                Rectangle heroBounds = hero.getBounds();

                // Create a list to collect new enemies
                List<Enemy> newEnemies = new ArrayList<>();

                // Update and render enemies
                Iterator<Enemy> enemyIterator = enemies.iterator();
                while (enemyIterator.hasNext()) {
                    Enemy enemy = enemyIterator.next();
                    enemy.update(newEnemies); // Pass the newEnemies list

                    // Check for collision between hero's attack and enemy
                    if (attackBounds != null && enemy.isAlive()) {
                        Rectangle enemyBounds = enemy.getBounds();
                        if (attackBounds.overlaps(enemyBounds)) {
                            enemy.takeDamage(hero.getAp());
                        }
                    }

                    // Check for collision between hero and enemy
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

                    // Remove enemy if it's dead and not dying
                    if (!enemy.isAlive() && !enemy.isDying()) {
                        enemy.dispose();
                        enemyIterator.remove();
                    }
                }

                // Add any new enemies collected during update
                enemies.addAll(newEnemies);

                camera.update();
                spriteBatch.setProjectionMatrix(camera.combined);

                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                spriteBatch.begin();
                hero.render(spriteBatch);
                hero.renderHearts(spriteBatch);

                // Render all enemies
                for (Enemy enemy : enemies) {
                    enemy.render(spriteBatch);
                }

                spriteBatch.end();
            } else {
                // Hero is dead, display Game Over
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                spriteBatch.begin();
                font.setColor(Color.RED);
                font.getData().setScale(3); // Adjust font size as needed
                font.draw(spriteBatch, "Game Over",
                    Gdx.graphics.getWidth() / 2 - 100,
                    Gdx.graphics.getHeight() / 2);
                spriteBatch.end();
            }
        } else {
            handlePauseMenuInput();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            spriteBatch.begin();
            renderPauseMenu();
            spriteBatch.end();
        }
    }

    private void handlePauseMenuInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedOption--;
            if (selectedOption < 0) {
                selectedOption = pauseMenuOptions.length - 1;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOption++;
            if (selectedOption >= pauseMenuOptions.length) {
                selectedOption = 0;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            String selected = pauseMenuOptions[selectedOption];
            if (selected.equals("Resume")) {
                isPaused = false;
            } else if (selected.equals("Settings")) {
                // Settings will be implemented later
            } else if (selected.equals("Credits")) {
                inCredits = true;
            }
        }
        // Handle exiting credits screen
        if (inCredits && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            inCredits = false;
        }
    }

    private void renderPauseMenu() {
        if (inCredits) {
            // Render credits placeholder
            font.setColor(Color.WHITE);
            font.draw(spriteBatch, "Credits\nGame developed by [Your Name]",
                100, Gdx.graphics.getHeight() - 100);
            font.draw(spriteBatch, "Press ESCAPE to return",
                100, Gdx.graphics.getHeight() - 200);
        } else {
            // Render pause menu options
            float menuX = Gdx.graphics.getWidth() / 2 - 50;
            float menuY = Gdx.graphics.getHeight() / 2 + 50;
            for (int i = 0; i < pauseMenuOptions.length; i++) {
                if (i == selectedOption) {
                    font.setColor(Color.YELLOW);
                } else {
                    font.setColor(Color.WHITE);
                }
                font.draw(spriteBatch, pauseMenuOptions[i],
                    menuX, menuY - i * 30);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // Handle resizing
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2,
            camera.viewportHeight / 2, 0);
    }

    @Override
    public void dispose() {
        // Dispose resources
        spriteBatch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        hero.dispose();
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
    }
}
