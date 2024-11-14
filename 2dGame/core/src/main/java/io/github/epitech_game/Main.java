package io.github.epitech_game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends ApplicationAdapter {

    private Character hero;
    private SpriteBatch spriteBatch;
    private BitmapFont font;

    @Override
    public void create() {
        // Create an instance of a Character subclass
        hero = new Hero(100, 200, 50);

        // Initialize rendering objects
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE); // Set the font color to white
    }

    @Override
    public void render() {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the character's speed on the screen
        spriteBatch.begin();
        font.draw(spriteBatch, "Character Speed: " + hero.speed, 20, Gdx.graphics.getHeight() - 20);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        // Clean up resources
        spriteBatch.dispose();
        font.dispose();
    }

    // Simple Hero subclass of Character
    public static class Hero extends Character {
        public Hero(float x, float y, float speed) {
            super(x, y, speed);
        }

        @Override
        public void move(float deltaX, float deltaY) {
            x += deltaX * speed;
            y += deltaY * speed;
        }
    }
}
