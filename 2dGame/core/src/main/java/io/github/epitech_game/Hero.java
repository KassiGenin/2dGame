package io.github.epitech_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Hero extends Character {

    protected int maxHp;
    private float dashCooldownTimer = 0f;
    private final float DASH_COOLDOWN = 1f; // Dash cooldown
    private final float DASH_DISTANCE = 75f; // Distance covered in a dash

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
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Update dash cooldown timer
        dashCooldownTimer += deltaTime;

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

        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)) {
            dash();
        }

        x = Math.max(0, Math.min(x, Gdx.graphics.getWidth() - 50));
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

    public void dash() {
        if (dashCooldownTimer >= DASH_COOLDOWN) {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                y += DASH_DISTANCE;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                y -= DASH_DISTANCE;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                x -= DASH_DISTANCE;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                x += DASH_DISTANCE;
            }

            dashCooldownTimer = 0f;
        }
    }
}
