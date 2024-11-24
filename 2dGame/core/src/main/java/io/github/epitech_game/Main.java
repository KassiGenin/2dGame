package io.github.epitech_game;

import com.badlogic.gdx.Game;
import io.github.epitech_game.screens.GameScreen;

public class Main extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen()); // Pass this game instance to GameScreen
    }
}

