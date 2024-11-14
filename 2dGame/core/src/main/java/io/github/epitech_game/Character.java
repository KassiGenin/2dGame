package io.github.epitech_game;

public abstract class Character {
    protected float x; // X-coordinate of the character
    protected float y; // Y-coordinate of the character
    protected float speed; // Movement speed of the character

    public Character(float x, float y, float speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public abstract void move(float deltaX, float deltaY);

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
