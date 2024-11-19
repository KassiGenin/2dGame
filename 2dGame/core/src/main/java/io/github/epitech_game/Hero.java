package io.github.epitech_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Hero extends Character {

    protected int maxHp;
    private float dashCooldownTimer = 0f;
    private final float DASH_COOLDOWN = 1f; // Cooldown time for dash
    private final float DASH_DURATION = 0.5f; // Duration of the dash
    private final float DASH_DISTANCE = 200f; // Distance covered by the dash
    private float dashTimer = 0f; // Timer for ongoing dash
    private boolean isDashing = false; // Whether the hero is dashing
    private float dashDirectionX = 0f, dashDirectionY = 0f; // Direction of the dash

    // Animation-related fields
    private Texture spriteSheet;
    private Texture sideSpriteSheet;
    private Texture rightSpriteSheet;
    private Texture upDashSpriteSheet;
    private Texture downDashSpriteSheet;
    private Texture leftDashSpriteSheet;
    private Texture rightDashSpriteSheet;
    private Texture heartSpriteSheet, heartBg, heartBorder;
    private TextureRegion[] hearts;

    private Animation<TextureRegion> walkDown, walkLeft, walkUp, walkRight;
    private Animation<TextureRegion> dashDown, dashLeft, dashUp, dashRight;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;

    public Hero() {
        super(38, 100, 3.5f, true);
        this.maxHp = 40;

        // Load spritesheets
        spriteSheet = new Texture("link_spritesheet_fixed.png");
        sideSpriteSheet = new Texture("LinkSide.png");
        rightSpriteSheet = new Texture("LinkRight.png");
        upDashSpriteSheet = new Texture("LinkRollUp.png");
        downDashSpriteSheet = new Texture("LinkRollDown.png");
        leftDashSpriteSheet = new Texture("LinkRollLeft.png");
        rightDashSpriteSheet = new Texture("LinkRollRight.png");
        heartSpriteSheet = new Texture("heart.png");
        heartBg = new Texture("heartbg.png");
        heartBorder = new Texture("heartborder.png");

        // Extract frames
        TextureRegion[][] frames = TextureRegion.split(spriteSheet, 32, 33);
        TextureRegion[][] sideFrames = TextureRegion.split(sideSpriteSheet, 32, 32);
        TextureRegion[][] rightFrames = TextureRegion.split(rightSpriteSheet, 32, 32);
        TextureRegion[][] upDashFrames = TextureRegion.split(upDashSpriteSheet, 32, 32);
        TextureRegion[][] downDashFrames = TextureRegion.split(downDashSpriteSheet, 32, 32);
        TextureRegion[][] leftDashFrames = TextureRegion.split(leftDashSpriteSheet, 32, 32);
        TextureRegion[][] rightDashFrames = TextureRegion.split(rightDashSpriteSheet, 32, 32);
        TextureRegion[][] heartFrames = TextureRegion.split(heartSpriteSheet, 17, 17);
        // Create animations
        walkDown = createAnimation(frames, 2, 0, 9);
        walkLeft = createAnimation(sideFrames, 0, 0, 9);
        walkUp = createAnimation(frames, 2, 22, 30);
        walkRight = createAnimation(rightFrames, 0, 0, 9);
        dashDown = createAnimation(downDashFrames, 0, 0, 9, DASH_DURATION);
        dashLeft = createAnimation(leftDashFrames, 0, 0, 9, DASH_DURATION);
        dashUp = createAnimation(upDashFrames, 0, 0, 9, DASH_DURATION);
        dashRight = createAnimation(rightDashFrames, 0, 0, 9, DASH_DURATION);


        hearts = new TextureRegion[5];
        for (int i = 0; i < 5; i++) {
            hearts[i] = heartFrames[0][i];
        }


        // Default animation
        currentAnimation = walkDown;

        stateTime = 0f; // Initialize animation time
    }


    public Animation<TextureRegion> createAnimation(TextureRegion[][] frames, int row, int startCol, int endCol, float duration) {
        TextureRegion[] animationFrames = new TextureRegion[endCol - startCol + 1];
        float frameDuration = duration / (endCol - startCol + 1); // Dynamic frame duration
        for (int i = startCol; i <= endCol; i++) {
            animationFrames[i - startCol] = frames[row][i];
        }
        return new Animation<>(frameDuration, animationFrames);
    }


    public void renderHearts(SpriteBatch spriteBatch) {
        int totalHearts = maxHp / 4; // Total heart containers
        int fullHearts = hp / 4;    // Full hearts
        int remainder = hp % 4;     // Remaining HP for partial hearts

        float baseX = 20; // X offset
        float baseY = Gdx.graphics.getHeight() - (40 * Main.SCALE_FACTOR); // Top-left corner

        for (int i = 0; i < totalHearts; i++) {
            float x = baseX + i * (20 * Main.SCALE_FACTOR);
            float y = baseY;

            // Render heart background and border with scaling
            spriteBatch.draw(heartBg, x, y, 17 * Main.SCALE_FACTOR, 17 * Main.SCALE_FACTOR);
            spriteBatch.draw(heartBorder, x, y, 17 * Main.SCALE_FACTOR, 17 * Main.SCALE_FACTOR);

            // Render heart state with scaling
            if (i < fullHearts) {
                spriteBatch.draw(hearts[0], x, y, 17 * Main.SCALE_FACTOR, 17 * Main.SCALE_FACTOR);
            } else if (i == fullHearts && remainder > 0) {
                spriteBatch.draw(hearts[4 - remainder], x, y, 17 * Main.SCALE_FACTOR, 17 * Main.SCALE_FACTOR);
            } else {
                spriteBatch.draw(hearts[4], x, y, 17 * Main.SCALE_FACTOR, 17 * Main.SCALE_FACTOR);
            }
        }
    }


    @Override
    public void move() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        boolean isMoving = false;

        // Handle dashing
        if (isDashing) {
            dashTimer += deltaTime;
            x += dashDirectionX * DASH_DISTANCE * deltaTime / DASH_DURATION;
            y += dashDirectionY * DASH_DISTANCE * deltaTime / DASH_DURATION;

            // Update animation state for dash
            stateTime += deltaTime;

            // Stop dashing when the duration ends
            if (dashTimer >= DASH_DURATION) {
                isDashing = false;
                dashCooldownTimer = 0f; // Reset cooldown
            }
            return; // Skip normal movement while dashing
        }

        dashCooldownTimer += deltaTime;

        // Handle normal movement
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += speed;
            currentAnimation = walkUp;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= speed;
            currentAnimation = walkDown;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= speed;
            currentAnimation = walkLeft;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += speed;
            currentAnimation = walkRight;
            isMoving = true;
        }

        // Trigger dash
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) && dashCooldownTimer >= DASH_COOLDOWN) {
            startDash();
        }

        // Update animation state only when moving
        if (isMoving) {
            stateTime += deltaTime;
        }

        // Keep hero within bounds
        x = Math.max(0, Math.min(x, Gdx.graphics.getWidth() - 32));
        y = Math.max(0, Math.min(y, Gdx.graphics.getHeight() - 32));
    }

    private void startDash() {
        isDashing = true;
        dashTimer = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dashDirectionX = 0;
            dashDirectionY = 1;
            currentAnimation = dashUp;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dashDirectionX = 0;
            dashDirectionY = -1;
            currentAnimation = dashDown;
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            dashDirectionX = -1;
            dashDirectionY = 0;
            currentAnimation = dashLeft;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            dashDirectionX = 1;
            dashDirectionY = 0;
            currentAnimation = dashRight;
        } else {
            isDashing = false; // Cancel dash if no direction is pressed
        }

        stateTime = 0f; // Reset animation state time for new dash
    }

    public void render(SpriteBatch spriteBatch) {
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        spriteBatch.draw(currentFrame, x, y, currentFrame.getRegionWidth() * Main.SCALE_FACTOR, currentFrame.getRegionHeight() * Main.SCALE_FACTOR);    }

    @Override
    public void attack() {
        // Attack logic remains unchanged
    }

    public void armoredDamage(int damage) {
        if (this.isHittable) {
            this.hp -= damage / 2;
            if (this.hp <= 0) {
                this.isAlive = false;
            }
        }
    }

    public void dispose() {

        if (spriteSheet != null) spriteSheet.dispose();
        if (sideSpriteSheet != null) sideSpriteSheet.dispose();
        if (rightSpriteSheet != null) rightSpriteSheet.dispose();
        if (upDashSpriteSheet != null) upDashSpriteSheet.dispose();
        if (downDashSpriteSheet != null) downDashSpriteSheet.dispose();
        if (leftDashSpriteSheet != null) leftDashSpriteSheet.dispose();
        if (rightDashSpriteSheet != null) rightDashSpriteSheet.dispose();
        heartSpriteSheet.dispose();
        heartBg.dispose();
        heartBorder.dispose();
    }
}
