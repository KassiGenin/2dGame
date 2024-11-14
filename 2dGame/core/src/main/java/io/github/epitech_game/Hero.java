package io.github.epitech_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Hero extends Character {

    protected int maxHp;

    public Hero() {
        super(40, 100, 1f, true);
        this.maxHp = 40;
    }

    public void heal(int healAmount) {
        if (this.hp + healAmount > this.maxHp) {
            this.hp = this.maxHp;
        } else {
            this.hp += healAmount;
        }
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getMaxHp() {
        return this.maxHp;
    }

    @Override
    public void move() {
        // Handle movement based on WASD input
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += speed;
        }

        x = Math.max(0, Math.min(x, Gdx.graphics.getWidth() - 50)); // Assume square size 50
        y = Math.max(0, Math.min(y, Gdx.graphics.getHeight() - 50));
    }

    @Override
    public void attack() {
        // Attack logic to be implemented
    }

    public void armoredDamage(int damage) {
        if (this.isHittable) {
            this.hp -= damage / 2;
            if (this.hp <= 0) {
                this.isAlive = false;
            }
        }
    }
}
