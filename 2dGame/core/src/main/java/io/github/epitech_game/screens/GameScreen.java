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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.epitech_game.Hero;

public class GameScreen implements Screen {
    private final SpriteBatch spriteBatch;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private final BitmapFont font;

    private Hero hero;
    private ShapeRenderer shapeRenderer;
    private int mapWidth, mapHeight;

    private Viewport viewport;

    private boolean isPaused = false;

    public GameScreen() {
        // Initialisation
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        // Charger la carte Tiled
        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load("overworld.tmx"); // Assurez-vous que le fichier existe dans les assets
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        // Get map dimensions
        mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);

        // Configurer la caméra avec l'échelle calculée
        camera = new OrthographicCamera();
        viewport = new FitViewport(mapWidth, mapHeight, camera);

        // Center the camera on the map
        camera.position.set(mapWidth / 2f, mapHeight / 2f, 0);
        camera.update();

        // Initialiser le héros
        hero = new Hero();
        hero.setPosition(100, 100); // Position initiale du héros
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        // Gestion de la pause
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
        }

        if (!isPaused) {
            // Mise à jour des éléments
            hero.update();
            hero.move();

            // Ajustement de la caméra pour suivre le héros
            camera.position.set(
                Math.min(Math.max(hero.getX(), camera.viewportWidth / 2), mapWidth - camera.viewportWidth / 2),
                Math.min(Math.max(hero.getY(), camera.viewportHeight / 2), mapHeight - camera.viewportHeight / 2),
                0
            );
            camera.update();
        }

        // Effacer l'écran et dessiner la carte
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mapRenderer.setView(camera);
        mapRenderer.render();

        // Dessiner le héros
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        hero.render(spriteBatch);
        spriteBatch.end();

        // Afficher la pause si nécessaire
        if (isPaused) {
            renderPauseMenu();
        }
    }

    private void renderPauseMenu() {
        spriteBatch.begin();
        font.getData().setScale(2);
        font.draw(spriteBatch, "Game Paused", 150, 200); // Ajustez la position si nécessaire
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);
        camera.update();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
        hero.dispose();
        map.dispose();
        mapRenderer.dispose();
        shapeRenderer.dispose();
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

}
