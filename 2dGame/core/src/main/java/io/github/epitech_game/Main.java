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
    public void render() {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the character's speed on the screen
        spriteBatch.begin();
        font.draw(spriteBatch, "Character Speed: " , 20, Gdx.graphics.getHeight() - 20);
        spriteBatch.end();
    }

}
