package io.github.epitech_game;

public abstract class Boss extends Enemy {
    public Boss(int hp, boolean isRanged, int range) {
        super(hp, 8, 0.6f, true, isRanged, true, range);
    }

    @Override
    public void move() {
    }

    @Override
    public void attack() {
    }
}
