package io.github.epitech_game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Character {
    protected int hp;
    protected int ap;
    protected float speed;
    protected Boolean isHittable;
    protected Boolean isAlive;
    protected float x;
    protected float y;

    public Character(int hp, int ap, float speed, Boolean isHittable) {
        this.hp = hp;
        this.ap = ap;
        this.speed = speed;
        this.isHittable = isHittable;
        this.isAlive = true;
        this.x = 0;
        this.y = 0;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Animation<TextureRegion> createAnimation(TextureRegion[][] frames, int row, int startCol, int endCol) {
        TextureRegion[] animationFrames = new TextureRegion[endCol - startCol + 1];
        for (int i = startCol; i <= endCol; i++) {
            animationFrames[i - startCol] = frames[row][i];
        }
        return new Animation<>(0.1f, animationFrames); // Each frame lasts 0.1 seconds
    }

    public abstract void move();

    public abstract void attack();


    public void takeDamage(int damage) {
        if (this.isHittable) {
            this.hp -= damage;
            if (this.hp <= 0) {
                this.isAlive = false;
            }
        }
    }

    public int getHp() {
        return this.hp;
    }

    public int getAp() {
        return this.ap;
    }

    public float getSpeed() {
        return this.speed;
    }

    public Boolean getIsHittable() {
        return this.isHittable;
    }

    public Boolean getIsAlive() {
        return this.isAlive;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setAp(int ap) {
        this.ap = ap;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setIsHittable(Boolean isHittable) {
        this.isHittable = isHittable;
    }

    public void setIsAlive(Boolean isAlive) {
        this.isAlive = isAlive;
    }


}
