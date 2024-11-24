package io.github.epitech_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import java.util.List   ;

public class Fly extends Enemy {
    private Texture flyUpSheet, flyDownSheet;
    private Animation<TextureRegion> flyUpAnimation, flyDownAnimation;
    private Animation<TextureRegion> currentAnimation, previousAnimation;
    private float stateTime;
    private Hero hero;

    private int range;
    private boolean flipSprite = false;

    public Fly(Hero hero) {
        super(200, 2, 1f, true, false, false, 25);
        this.hero = hero;
        this.range = 25;
        this.stateTime = 0f;

        // Load spritesheets
        flyUpSheet = new Texture("flyUp.png");
        flyDownSheet = new Texture("flyDown.png");

        // Create animations with specified frames
        flyUpAnimation = createFlyUpAnimation(flyUpSheet, 13, 5, 12); // Use frames 0 to 12
        flyDownAnimation = createFlyDownAnimation(flyDownSheet, 13, 5, 12); // Use frames 0 to 12

        // Set default animation
        currentAnimation = flyDownAnimation;
        previousAnimation = currentAnimation;
    }

    private Animation<TextureRegion> createFlyUpAnimation(Texture sheet, int totalFrames, int firstFrameIndex, int lastFrameIndex) {
        int frameCount = lastFrameIndex - firstFrameIndex + 1;
        TextureRegion[] frames = new TextureRegion[frameCount];
        int originalFrameWidth = 106;
        int originalFrameHeight = 74;
        int frameWidth = originalFrameWidth - 50 - 2; // Cut 50 pixels from left, 2 pixels from right
        int frameHeight = originalFrameHeight - 20; // Cut 20 pixels from top

        for (int i = firstFrameIndex; i <= lastFrameIndex; i++) {
            int x = i * originalFrameWidth + 50;
            int y = 20;
            frames[i - firstFrameIndex] = new TextureRegion(sheet, x, y, frameWidth, frameHeight);
        }
        Animation<TextureRegion> animation = new Animation<>(0.1f, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        return animation;
    }

    private Animation<TextureRegion> createFlyDownAnimation(Texture sheet, int totalFrames, int firstFrameIndex, int lastFrameIndex) {
        int frameCount = lastFrameIndex - firstFrameIndex + 1;
        TextureRegion[] frames = new TextureRegion[frameCount];
        int originalFrameWidth = 104;
        int originalFrameHeight = 83;
        int frameWidth = originalFrameWidth - 2 - 52; // Cut 2 pixels from left, 52 pixels from right
        int frameHeight = originalFrameHeight - 20; // Cut 20 pixels from bottom

        for (int i = firstFrameIndex; i <= lastFrameIndex; i++) {
            int x = i * originalFrameWidth + 2;
            int y = 0;
            frames[i - firstFrameIndex] = new TextureRegion(sheet, x, y, frameWidth, frameHeight);
        }
        Animation<TextureRegion> animation = new Animation<>(0.1f, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        return animation;
    }

    @Override
    public void move() {
        if (!isAlive) {
            return;
        }

        float deltaX = hero.getX() - this.x;
        float deltaY = hero.getY() - this.y;

        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (distance == 0) {
            currentAnimation = flyUpAnimation;
            return;
        }

        float directionX = deltaX / distance;
        float directionY = deltaY / distance;

        this.x += directionX * speed;
        this.y += directionY * speed;

        // Determine if sprite should be flipped
        flipSprite = directionX > 0;

        if (directionY >= 0) {
            currentAnimation = flyUpAnimation;
        } else {
            currentAnimation = flyDownAnimation;
        }

        // Update state time
        if (currentAnimation != previousAnimation) {
            stateTime = 0f;
            previousAnimation = currentAnimation;
        } else {
            stateTime += Gdx.graphics.getDeltaTime();
        }
    }

    @Override
    public void attack() {
        // If the fly is dead, do not attack
        if (!isAlive) {
            return;
        }

        // Check if the hero is within attack range
        if (isHeroInRange()) {
            hero.takeDamage(this.ap);
        }
    }

    private boolean isHeroInRange() {
        float deltaX = hero.getX() - this.x;
        float deltaY = hero.getY() - this.y;
        return Math.abs(deltaX) <= range && Math.abs(deltaY) <= range;
    }

    public void render(SpriteBatch spriteBatch) {
        if (!isAlive && !isDying) {
            return; // Do not render if the fly is dead and death animation has finished
        }

        if (isDying) {
            if (!deathAnimation.isAnimationFinished(deathStateTime)) {
                // Render death animation
                TextureRegion currentFrame = deathAnimation.getKeyFrame(deathStateTime, false);
                spriteBatch.draw(
                    currentFrame,
                    x,
                    y,
                    currentFrame.getRegionWidth() * 0.5f,
                    currentFrame.getRegionHeight() * 0.5f
                );
            }
        } else {

            TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

            if (currentAnimation == flyUpAnimation) {
                if ((currentFrame.isFlipX() && !flipSprite) || (!currentFrame.isFlipX() && flipSprite)) {
                    currentFrame.flip(true, false);
                }
            } else {
                if ((currentFrame.isFlipX() && flipSprite) || (!currentFrame.isFlipX() && !flipSprite)) {
                    currentFrame.flip(true, false);
                }
            }

            if (isInvincible()) {
                spriteBatch.setColor(1, 1, 1, 0.5f); // 50% transparency
            } else {
                spriteBatch.setColor(1, 1, 1, 1);
            }

            spriteBatch.draw(
                currentFrame,
                x,
                y,
                currentFrame.getRegionWidth() *0.5f,
                currentFrame.getRegionHeight() * 0.5f
            );

            // Reset color to default
            spriteBatch.setColor(1, 1, 1, 1);
        }
    }

    // Method to get the fly's bounding rectangle for collision detection
    public Rectangle getBounds() {
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        float width = currentFrame.getRegionWidth() * 0.24f;
        float height = currentFrame.getRegionHeight() * 0.24f;
        return new Rectangle(x, y, width, height);
    }

    public void update(List<Enemy> newEnemies) {
        super.update();
        move();
        attack();
    }

    @Override
    public void handleDeath() {
        super.handleDeath();
        // Additional death handling can be added here
    }

    public void dispose() {
        if (flyUpSheet != null) flyUpSheet.dispose();
        if (flyDownSheet != null) flyDownSheet.dispose();
    }


    // Ensure you have the isInvincible() method if not already present
    public boolean isInvincible() {
        return isInvincible;
    }

}
