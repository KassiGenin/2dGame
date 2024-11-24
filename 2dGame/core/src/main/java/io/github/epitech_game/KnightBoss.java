package io.github.epitech_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

public class KnightBoss extends Boss {
    private Hero hero;
    private ArrayList<Enemy> enemies;

    private Texture walkUpTexture, walkRightTexture, walkLeftTexture, walkDownTexture;
    private Texture attackTexture;
    private Animation<TextureRegion> moveUpAnimation,  moveDownAnimation, moveLeftAnimation, moveRightAnimation;
    private Animation<TextureRegion> currentAnimation, previousAnimation;
    private Animation<TextureRegion> currentAttackAnimation;
    private float stateTime;
    private boolean flipSprite = false;

    // Attack animations

    private Animation<TextureRegion> attackDownAnimation;
    private Animation<TextureRegion> attackLeftAnimation;
    private Animation<TextureRegion> attackRightAnimation;
    private Animation<TextureRegion> attackUpAnimation;

    // Movement parameters
    private float moveTimer = 0f;
    private float MOVE_DURATION = 15f; // Move for 3 seconds
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


    public KnightBoss(Hero hero) {
        super(400, false, 50); // Assuming the KnightBoss is not ranged
        this.hero = hero;


        walkRightTexture = new Texture("knightBossWalkRight.png");
        walkLeftTexture = new Texture("knightBossWalkLeft.png");
        walkUpTexture = new Texture("knightBossWalkUp.png");
        walkDownTexture = new Texture("knightBossWalkDown.png");
        attackTexture = new Texture("knightBossAttack.png");

        // Create animations
        moveUpAnimation = createAnimation(walkUpTexture, 62,65,3, 0, 2);
        moveDownAnimation = createAnimation(walkDownTexture, 62, 65, 3, 0, 2);
        moveLeftAnimation = createAnimation(walkLeftTexture, 56, 65, 3, 0, 2);
        moveRightAnimation = createAnimation(walkRightTexture, 56, 65, 3, 0, 2);

        createAttackAnimations();

        // Set default animation
        currentAnimation = moveDownAnimation;
        previousAnimation = currentAnimation;
        stateTime = 0f;
    }

    private void createAttackAnimations() {
        attackRightAnimation = createAttackAnimation(true);
        attackLeftAnimation = createAttackAnimation(false);

        attackDownAnimation = createAttackAnimation(false);
        attackUpAnimation = createAttackAnimation( false);
    }

    private Animation<TextureRegion> createAttackAnimation(boolean flip) {
        int frameWidth = 65;
        int frameHeight = 78;
        int totalFrames = 6;

        TextureRegion[] frames = new TextureRegion[totalFrames];
        for (int i = 0; i < totalFrames; i++) {
            int x = i * frameWidth;
            int y = 0;
            TextureRegion frame = new TextureRegion(attackTexture, x, y, frameWidth, frameHeight);
            if (flip) {
                frame.flip(true, false);
            }
            frames[i] = frame;
        }

        return new Animation<>(0.3f, frames);
    }

    private Animation<TextureRegion> createAnimation(Texture texture, int frameWidth, int frameHeight, int frameCount, int start, int end) {
        int adjustedFrameWidth = frameWidth - 0;
        int adjustedFrameHeight = frameHeight - 0;
        TextureRegion[] animationFrames = new TextureRegion[frameCount];

        for (int i = 0; i < frameCount; i++) {
            int x = i * frameWidth;
            int y = 0;
            TextureRegion frame = new TextureRegion(texture, x, y, adjustedFrameWidth, adjustedFrameHeight);
            animationFrames[i] = frame;
        }

        return new Animation<>(0.2f, animationFrames);
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

            if (directionY >= 0 && Math.abs(deltaX) < Math.abs(deltaY)) {
                currentAnimation = moveUpAnimation;
            } else if (directionY < 0 && Math.abs(deltaX) < Math.abs(deltaY)) {
                currentAnimation = moveDownAnimation;
            } else if (directionX < 0) {
                currentAnimation = moveLeftAnimation;
            } else {
                currentAnimation = moveRightAnimation;
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
            if (distanceToHero <= ATTACK_RANGE-50) {
                // Hero is in range, start attacking
                isAttacking = true;
                actionTimer = 0f;
                stateTime = 0f;
                determineAttackAnimation();
            } else {

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
                currentAttackAnimation = attackRightAnimation;
                flipSprite = false; // Facing right
            } else {
                // Up-left
                currentAttackAnimation = attackLeftAnimation;
                flipSprite = true; // Facing left
            }
        } else {
            // Attacking downwards
            if (deltaX >= 0) {
                // Down-right
                currentAttackAnimation = attackDownAnimation;
                flipSprite = false; // Facing right
            } else {
                // Down-left
                currentAttackAnimation = attackUpAnimation;
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
        Wizard wizard1 = new Wizard(hero);
        wizard1.setPosition((float) (Math.random() * 300 + 10), (float) (Math.random() * 300 + 10));
        newEnemies.add(wizard1);

        Wizard wizard2 = new Wizard(hero);
        wizard2.setPosition((float) (Math.random() * 300 + 10), (float) (Math.random() * 300 + 10));
        newEnemies.add(wizard2);
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
                    currentFrame.getRegionWidth() * 1f,
                    currentFrame.getRegionHeight() * 1f
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
            currentFrame.getRegionWidth() * 1,
            currentFrame.getRegionHeight() * 1
        );
    }
    @Override
    public void handleDeath() {
        super.handleDeath();
    }



    @Override
    public void update(List<Enemy> newEnemies) {
        super.update();

        if (hero.maxHp <= 40) {
            hero.takeDamage(40);
        }

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

        if (walkUpTexture != null) {
            walkUpTexture.dispose();
        }
        if (walkDownTexture != null) {
            walkDownTexture.dispose();
        }
        if (walkLeftTexture != null) {
            walkLeftTexture.dispose();
        }
        if (walkRightTexture != null) {
            walkRightTexture.dispose();
        }
        if (attackTexture != null) {
            attackTexture.dispose();
        }
    }
}



