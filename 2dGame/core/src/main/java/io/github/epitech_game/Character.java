package io.github.epitech_game;

public abstract class Character {
    protected int hp;
    protected int ap;
    protected float speed;
    protected Boolean isHittable;
    protected Boolean isAlive;

    public Character(int hp, int ap, float speed, Boolean isHittable, Boolean isAlive) {
        this.hp = hp;
        this.ap = ap;
        this.speed = speed;
        this.isHittable = isHittable;
        this.isAlive = isAlive;
    }

    public void move() {
    }

    public void attack() {
    }

    public void takeDamage(int damage) {
        if (this.isHittable){
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
