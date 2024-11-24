package io.github.epitech_game;

public enum GameMap {
    WORLD_3("maps/World3.tmx"),
    WORLD_2("maps/World2.tmx"),
    OVERWORLD("maps/overworld.tmx");


    private String mapPath;

    GameMap(String mapPath) {
        this.mapPath = mapPath;
    }

    public String getMapPath() {
        return mapPath;
    }
}
