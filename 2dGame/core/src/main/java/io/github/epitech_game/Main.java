package io.github.epitech_game;

import com.badlogic.gdx.Game;
import io.github.epitech_game.screens.GameScreen;

/**
 * The main class that initializes the game.
 */
public class Main extends Game {
    @Override
    public void create() {
        // Initialize the first screen with the CENTER spawn direction.
        setScreen(new GameScreen(this, SpawnDirection.CENTER,GameMap.WORLD_3));
    }
}
