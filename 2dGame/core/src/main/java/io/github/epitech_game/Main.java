package io.github.epitech_game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Main extends ApplicationAdapter {

    private Hero hero;
    private ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        hero = new Hero();
        hero.setPosition(100, 100); // Starting position
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render() {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update hero movement
        hero.move();

        // Draw the hero as a square
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLUE); // Set the square color
        shapeRenderer.rect(hero.getX(), hero.getY(), 50, 50); // Draw the square
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
