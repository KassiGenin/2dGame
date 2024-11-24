package io.github.epitech_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class ForestBoss extends Boss {
    private Hero hero;
    private ArrayList<Enemy> enemies;

    private Texture downTexture;
    private Texture upTexture;
    private Texture attackTexture;
    private Animation<TextureRegion> moveDownAnimation;
    private Animation<TextureRegion> moveUpAnimation;
    private Animation<TextureRegion> currentAnimation, previousAnimation;
    private Animation<TextureRegion> currentAttackAnimation;
    private float stateTime;
    private boolean flipSprite = false;

    // Attack animations
    private Animation<TextureRegion> attackDownRightAnimation;
    private Animation<TextureRegion> attackDownLeftAnimation;
    private Animation<TextureRegion> attackUpRightAnimation;
    private Animation<TextureRegion> attackUpLeftAnimation;

    // Movement parameters
    private float moveTimer = 0f;
    private float MOVE_DURATION = 3f; // Move for 3 seconds
    private float PAUSE_DURATION = 1f; // Pause for 1 second
    private float pauseTimer = 0f;
    private boolean isMoving = true;

    // Attack parameters
    private final float ATTACK_RANGE = 100f; // Define appropriate attack range
    private boolean isAttacking = false;
    private boolean isUsingCapacity = false;
    private float actionTimer = 0f;
    private final float ACTION_DURATION = 1f; // Duration of attack or capacity
    private boolean capacityExecuted = false;


    public ForestBoss(Hero hero) {
        super(2000, false, 50); // Assuming the ForestBoss is not ranged  // before 2000
        this.hero = hero;

        // Load textures
        downTexture = new Texture("forestBossDown.png");
        upTexture = new Texture("forestBossUp.png");
        attackTexture = new Texture("bossForestAttack.png");

        // Create animations
        moveDownAnimation = createAnimation(downTexture, 86, 96, 7);
        moveUpAnimation = createAnimation(upTexture, 73, 103, 7);
        createAttackAnimations();

        // Set default animation
        currentAnimation = moveDownAnimation;
        previousAnimation = currentAnimation;
        stateTime = 0f;
    }

    private void createAttackAnimations() {
        // Create attack animations for down-right and up-right directions
        attackDownRightAnimation = createAttackAnimation(0, true); // Lines 1-2
        attackUpRightAnimation = createAttackAnimation(2, true);   // Lines 3-4

        // Create reversed animations for down-left and up-left directions
        attackDownLeftAnimation = createAttackAnimation(0, false); // Lines 1-2 reversed
        attackUpLeftAnimation = createAttackAnimation(2, false);   // Lines 3-4 reversed
    }

    private Animation<TextureRegion> createAttackAnimation(int startRow, boolean flip) {
        int frameWidth = 126;
        int frameHeight = 132;
        int totalFrames = 12; // 7 frames from first line + 5 frames from second line (sprites 2-6)

        TextureRegion[] frames = new TextureRegion[totalFrames];
        int index = 0;

        // First line sprites 1-7
        for (int i = 0; i < 7; i++) {
            TextureRegion frame = new TextureRegion(attackTexture,
                i * frameWidth,
                startRow * frameHeight,
                frameWidth - 2, frameHeight - 10); // Adjusted dimensions
            frame.setRegionX(frame.getRegionX() + 2); // Skip 2 pixels from left
            frame.setRegionY(frame.getRegionY() + 18);



            if (!flip) {
                frame.flip(true, false);
            }
            frames[index++] = frame;
        }

        // Second line sprites 2-6
        for (int i = 1; i <= 5; i++) {
            TextureRegion frame = new TextureRegion(attackTexture,
                i * frameWidth,
                (startRow + 1) * frameHeight,
                frameWidth - 2, frameHeight - 8); // Adjusted dimensions
            frame.setRegionX(frame.getRegionX() + 2); // Skip 2 pixels from left
            frame.setRegionY(frame.getRegionY() + 14); // Skip 8 pixels from top

            if (!flip) {
                frame.flip(true, false);
            }
            frames[index++] = frame;
        }

        return new Animation<>(0.1f, frames);
    }

    private Animation<TextureRegion> createAnimation(Texture texture, int frameWidth, int frameHeight, int frameCount) {
        int adjustedFrameWidth = frameWidth - 2;  // Skip last 2 pixels in width
        int adjustedFrameHeight = frameHeight - 2; // Skip last 2 pixels in height
        TextureRegion[] animationFrames = new TextureRegion[frameCount];

        for (int i = 0; i < frameCount; i++) {
            int x = i * frameWidth;
            int y = 0;
            TextureRegion frame = new TextureRegion(texture, x, y, adjustedFrameWidth, adjustedFrameHeight);
            animationFrames[i] = frame;
        }

        return new Animation<>(0.1f, animationFrames);
    }

    @Override
    public void move() {
        if (!isAlive) {
            return;
        }

        float deltaTime = Gdx.graphics.getDeltaTime();

        if (isMoving) {
            moveTimer += deltaTime;

            // Move toward the hero
            float deltaX = hero.getX() - this.x;
            float deltaY = hero.getY() - this.y;

            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            if (distance == 0) {
                distance = 0.0001f; // Avoid division by zero
            }

            float directionX = deltaX / distance;
            float directionY = deltaY / distance;

            // Update position
            this.x += directionX * speed;
            this.y += directionY * speed;

            // Determine if sprite should be flipped
            if (directionY > 0) {
                flipSprite = directionX > 0;
            } else  flipSprite = directionX < 0;


            // Choose animation based on vertical direction
            if (directionY >= 0) {
                currentAnimation = moveUpAnimation;
            } else {
                currentAnimation = moveDownAnimation;
            }

            // Update state time
            stateTime += deltaTime;

            // Check if move duration is over
            if (moveTimer >= MOVE_DURATION) {
                isMoving = false;
                moveTimer = 0f;
                pauseTimer = 0f;
            }
        } else {
            // Pause before next action
            pauseTimer += deltaTime;
            if (pauseTimer >= PAUSE_DURATION) {
                isMoving = true;
                pauseTimer = 0f;
            }
        }
    }

    public void attack(List<Enemy> newEnemies) {
        if (!isAlive) {
            return;
        }

        float deltaTime = Gdx.graphics.getDeltaTime();

        if (isAttacking) {
            // Continue attack animation
            actionTimer += deltaTime;
            stateTime += deltaTime;
            if (currentAttackAnimation.isAnimationFinished(stateTime)) {
                isAttacking = false;
                actionTimer = 0f;
                stateTime = 0f;
                currentAnimation = moveDownAnimation; // Reset to default animation
            }
        } else    if (isUsingCapacity && !isMoving) {
            actionTimer += Gdx.graphics.getDeltaTime();
            if (!capacityExecuted) {
                spawnFlies(newEnemies);
                capacityExecuted = true;
            }
            if (actionTimer >= ACTION_DURATION) {
                isUsingCapacity = false;
                actionTimer = 0f;
                capacityExecuted = false;
            }
        } else {
            // Decide next action
            float distanceToHero = (float) Math.sqrt((hero.getX() - x) * (hero.getX() - x) + (hero.getY() - y) * (hero.getY() - y));
            if (distanceToHero <= ATTACK_RANGE) {
                // Hero is in range, start attacking
                isAttacking = true;
                actionTimer = 0f;
                stateTime = 0f;
                determineAttackAnimation();
            } else {
                // Use capacity
                isUsingCapacity = true;
                actionTimer = 0f;
            }
        }
    }

    private void determineAttackAnimation() {
        float deltaX = hero.getX() - this.x;
        float deltaY = hero.getY() - this.y;

        if (deltaY >= 0) {
            // Attacking upwards
            if (deltaX >= 0) {
                // Up-right
                currentAttackAnimation = attackUpRightAnimation;
                flipSprite = false; // Facing right
            } else {
                // Up-left
                currentAttackAnimation = attackUpLeftAnimation;
                flipSprite = true; // Facing left
            }
        } else {
            // Attacking downwards
            if (deltaX >= 0) {
                // Down-right
                currentAttackAnimation = attackDownRightAnimation;
                flipSprite = false; // Facing right
            } else {
                // Down-left
                currentAttackAnimation = attackDownLeftAnimation;
                flipSprite = true; // Facing left
            }
        }
    }

    public boolean isAlive(){
        return this.isAlive;
    }

    public boolean isInvincible() {
        return isInvincible;
    }

    private void spawnFlies(List<Enemy> newEnemies) {
        Fly fly1 = new Fly(hero);
        fly1.setPosition(0, Gdx.graphics.getHeight() - 50);
        newEnemies.add(fly1);

        Fly fly2 = new Fly(hero);
        fly2.setPosition(Gdx.graphics.getWidth() - 50, Gdx.graphics.getHeight() - 50);
        newEnemies.add(fly2);
    }
    public void render(SpriteBatch spriteBatch) {
        if (!isAlive && !isDying) {
            return;
        }
        if (isDying) {

            if (!deathAnimation.isAnimationFinished(deathStateTime)) {
                TextureRegion currentFrame = deathAnimation.getKeyFrame(deathStateTime, false);
                spriteBatch.draw(
                    currentFrame,
                    x,
                    y,
                    currentFrame.getRegionWidth() * 0.8f,
                    currentFrame.getRegionHeight() * 0.8f
                );
            } return;
        }
        if (isInvincible()) {;
            spriteBatch.setColor(1, 1, 1, 0.5f);


        } else {
            spriteBatch.setColor(1, 1, 1, 1);
        }


        TextureRegion currentFrame;

        if (isAttacking) {
            currentFrame = currentAttackAnimation.getKeyFrame(stateTime, false);
        } else {
            currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        }

        // Flip sprite if necessary
        if ((flipSprite && !currentFrame.isFlipX()) || (!flipSprite && currentFrame.isFlipX())) {
            currentFrame.flip(true, false);
        }

        // Draw the boss
        spriteBatch.draw(
            currentFrame,
            x,
            y,
            currentFrame.getRegionWidth() * 0.8f,
            currentFrame.getRegionHeight() * 0.8f
        );
    }
    @Override
    public void handleDeath() {
        super.handleDeath();
    }



    @Override
    public void update(List<Enemy> newEnemies) {
        super.update();

        if (!isAlive) {
            return;
        }

        move();
        attack(newEnemies);
    }

    public Rectangle getBounds() {
        TextureRegion currentFrame;
        if (isAttacking) {
            currentFrame = currentAttackAnimation.getKeyFrame(stateTime, false);
        } else {
            currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        }
        float width = currentFrame.getRegionWidth() * 0.8f;
        float height = currentFrame.getRegionHeight() * 0.8f;
        return new Rectangle(x, y, width, height);
    }

    // Getters for isAttacking and isUsingCapacity
    public boolean isAttacking() {
        return isAttacking;
    }


    public boolean isInDamageFrame() {
        if (!isAttacking) {
            return false;
        }
        int frameIndex = currentAttackAnimation.getKeyFrameIndex(stateTime);
        return frameIndex >= 4 && frameIndex <= 6;
    }

    public boolean isDying() {
        return isDying;
    }


    @Override
    public void dispose() {
        super.dispose();
        if (downTexture != null) downTexture.dispose();
        if (upTexture != null) upTexture.dispose();
        if (attackTexture != null) attackTexture.dispose();
    }
}


