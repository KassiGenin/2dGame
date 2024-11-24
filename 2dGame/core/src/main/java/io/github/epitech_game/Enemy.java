package io.github.epitech_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.List;

public abstract class Enemy extends Character {
    protected boolean isRanged;
    protected boolean isBoss;
    protected int range;
    protected boolean isInvincible = false;
    protected float invincibleTimer = 0f;
    protected final float INVINCIBILITY_DURATION = 0.25f;

    // Death animation fields
    protected Texture deathTexture;
    protected Animation<TextureRegion> deathAnimation;
    protected float deathStateTime = 0f;
    protected boolean isDying = false;

    public Enemy(int hp, int ap, float speed, Boolean isHittable, boolean isRanged, boolean isBoss, int range) {
        super(hp, ap, speed, isHittable);
        this.isRanged = isRanged;
        this.isBoss = isBoss;
        this.range = range;



        deathTexture = new Texture("enemyExplode.png");
        deathAnimation = createDeathAnimation(deathTexture);


    }

    public boolean isAlive() {
        return isAlive;
    }

    public boolean isDying() {
        return isDying;
    }

    public abstract void update (List<Enemy> newEnemies);

    @Override
    public void update() {
        super.update();
        if (isInvincible) {
            invincibleTimer -= Gdx.graphics.getDeltaTime();
            if (invincibleTimer <= 0f) {
                isInvincible = false;
                invincibleTimer = 0f;
            }
        }

        if (isDying) {
            deathStateTime += Gdx.graphics.getDeltaTime();
            if (deathAnimation.isAnimationFinished(deathStateTime)) {
                isAlive = false;
                isDying = false;
            }
        }
    }



    @Override
    public abstract void move();

    @Override
    public abstract void attack();

    // Add abstract methods for rendering and getting bounds
    public abstract void render(SpriteBatch spriteBatch);

    public abstract Rectangle getBounds();

    @Override
    public void handleDeath() {
        super.handleDeath();

         //removing the !isboss  allows me to kill the boss but fucks up the screen
            // Start death animation
            isDying = true;
            deathStateTime = 0f;
            this.speed = 0f;
            this.isHittable = false;
            this.isInvincible = true;


    }

    @Override
    public void takeDamage(int damage) {
        if (this.isHittable && !isInvincible) {
            this.hp -= damage;
            if (this.hp <= 0) {
                handleDeath();
            } else {
                setInvincible(INVINCIBILITY_DURATION);
            }
        }
    }


    public void setInvincible(float duration) {
        isInvincible = true;
        invincibleTimer = duration;
    }

    public boolean isInvincible() {
        return isInvincible;
    }


    protected Animation<TextureRegion> createDeathAnimation(Texture texture) {
        TextureRegion[][] tmpFrames = TextureRegion.split(texture, 48, 48);
        TextureRegion[] animationFrames = new TextureRegion[8];

        int index = 0;
        for (int i = 0; i < tmpFrames.length; i++) {
            for (int j = 0; j < tmpFrames[i].length; j++) {
                if (index < 8) {
                    animationFrames[index++] = tmpFrames[i][j];
                }
            }
        }

        return new Animation<>(0.08f, animationFrames);
    }

    public void dispose() {
        if (deathTexture != null) {
            deathTexture.dispose();
        }
    }
}
