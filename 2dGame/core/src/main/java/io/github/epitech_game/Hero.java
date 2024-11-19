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
    private float attackCooldownTimer = 0f;
    private final float ATTACK_COOLDOWN = 0.3f; // Attack cooldown time
    private final float ATTACK_DURATION = 0.2f; // Slash animation duration
    private boolean isAttacking = false;
    private float attackTimer = 0f;

    // Animation-related fields
    private final Texture spriteSheet;
    private final Texture sideSpriteSheet;
    private Texture rightSpriteSheet;
    private Texture upDashSpriteSheet;
    private Texture downDashSpriteSheet;
    private Texture leftDashSpriteSheet;
    private Texture rightDashSpriteSheet;
    private Texture heartSpriteSheet, heartBg, heartBorder;
    private TextureRegion[] hearts;
    private Texture slashDownSheet, slashUpSheet, slashRightSheet, slashLeftSheet;

    // New attack spritesheets
    private Texture attackUpSheet, attackDownSheet, attackLeftSheet, attackRightSheet;

    private Animation<TextureRegion> walkDown, walkLeft, walkUp, walkRight;
    private Animation<TextureRegion> dashDown, dashLeft, dashUp, dashRight;
    private Animation<TextureRegion> currentAnimation;
    private Animation<TextureRegion> slashDown, slashUp, slashRight, slashLeft;
    private Animation<TextureRegion> attackUpAnimation, attackDownAnimation, attackLeftAnimation, attackRightAnimation;
    private Animation<TextureRegion> currentAttackAnimation;
    private Animation<TextureRegion> currentAttackHeroAnimation;
    private float stateTime;

    public Hero() {
        super(37, 100, 3.5f, true);
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
        slashDownSheet = new Texture("slashDown.png");
        slashUpSheet = new Texture("slashUp.png");
        slashRightSheet = new Texture("slashRight.png");
        slashLeftSheet = new Texture("slashLeft.png");

        // Load new attack spritesheets
        attackUpSheet = new Texture("attackUp.png");
        attackDownSheet = new Texture("attackDown.png");
        attackLeftSheet = new Texture("attackLeft.png");
        attackRightSheet = new Texture("attackRight.png");

        // Extract frames
        TextureRegion[][] frames = TextureRegion.split(spriteSheet, 32, 33);
        TextureRegion[][] sideFrames = TextureRegion.split(sideSpriteSheet, 32, 32);
        TextureRegion[][] rightFrames = TextureRegion.split(rightSpriteSheet, 32, 32);
        TextureRegion[][] upDashFrames = TextureRegion.split(upDashSpriteSheet, 31, 32);
        TextureRegion[][] downDashFrames = TextureRegion.split(downDashSpriteSheet, 31, 32);
        TextureRegion[][] leftDashFrames = TextureRegion.split(leftDashSpriteSheet, 32, 32);
        TextureRegion[][] rightDashFrames = TextureRegion.split(rightDashSpriteSheet, 32, 32);
        TextureRegion[][] heartFrames = TextureRegion.split(heartSpriteSheet, 17, 17);

        // Extract attack frames
        TextureRegion[][] attackUpFrames = TextureRegion.split(attackUpSheet, 32, 32);
        TextureRegion[][] attackDownFrames = TextureRegion.split(attackDownSheet, 32, 32);
        TextureRegion[][] attackLeftFrames = TextureRegion.split(attackLeftSheet, 32, 32);
        TextureRegion[][] attackRightFrames = TextureRegion.split(attackRightSheet, 32, 32);

        // Create animations
        walkDown = createAnimation(frames, 2, 0, 9);
        walkLeft = createAnimation(sideFrames, 0, 0, 9);
        walkUp = createAnimation(frames, 2, 22, 30);
        walkRight = createAnimation(rightFrames, 0, 0, 9);
        dashDown = createAnimation(downDashFrames, 0, 0, 7, DASH_DURATION);
        dashLeft = createAnimation(leftDashFrames, 0, 0, 6, DASH_DURATION);
        dashUp = createAnimation(upDashFrames, 0, 0, 7, DASH_DURATION);
        dashRight = createAnimation(rightDashFrames, 0, 0, 6, DASH_DURATION);
        slashDown = createAnimation(TextureRegion.split(slashDownSheet, 54, 37), false, false);
        slashUp = createAnimation(TextureRegion.split(slashUpSheet, 27, 65), false, true);
        slashRight = createAnimation(TextureRegion.split(slashRightSheet, 66, 27), true, false);
        slashLeft = createAnimation(TextureRegion.split(slashLeftSheet, 65, 27), true, false);

        // Create attack animations
        attackUpAnimation = new Animation<>(0.1f, attackUpFrames[0]);
        attackDownAnimation = new Animation<>(0.1f, attackDownFrames[0]);
        attackLeftAnimation = new Animation<>(0.1f, attackLeftFrames[0]);
        attackRightAnimation = new Animation<>(0.1f, attackRightFrames[0]);

        hearts = new TextureRegion[5];
        for (int i = 0; i < 5; i++) {
            hearts[i] = heartFrames[0][i];
        }

        // Default animation
        currentAnimation = walkDown;

        stateTime = 0f; // Initialize animation time
    }

    private Animation<TextureRegion> createAnimation(TextureRegion[][] frames, boolean horizontal, boolean reverseVertical) {
        int frameCount = horizontal ? frames[0].length : frames.length;
        TextureRegion[] animationFrames = new TextureRegion[frameCount];

        for (int i = 0; i < frameCount; i++) {
            if (horizontal) {
                animationFrames[i] = frames[0][horizontal ? i : frameCount - 1 - i]; // Left-to-right or right-to-left
            } else {
                animationFrames[i] = frames[reverseVertical ? frameCount - 1 - i : i][0]; // Top-to-bottom or bottom-to-top
            }
        }

        return new Animation<>(ATTACK_DURATION / frameCount, animationFrames);
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

        attackCooldownTimer += deltaTime;

        // Handle dashing
        if (isDashing) {
            dashTimer += deltaTime;
            x += dashDirectionX * DASH_DISTANCE * deltaTime / DASH_DURATION;
            y += dashDirectionY * DASH_DISTANCE * deltaTime / DASH_DURATION;

            stateTime += deltaTime;

            if (dashTimer >= DASH_DURATION) {
                isDashing = false;
                dashCooldownTimer = 0f;
            }
            return;
        }

        dashCooldownTimer += deltaTime;

        // Handle movement
        if (!isAttacking) { // Disable movement while attacking
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
        }

        // Handle attack
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && attackCooldownTimer >= ATTACK_COOLDOWN) {
            attack();
        }

        // Handle dashing
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) && dashCooldownTimer >= DASH_COOLDOWN) {
            startDash();
        }

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
        TextureRegion currentFrame;

        if (isAttacking) {
            // Render attack animation
            attackTimer += Gdx.graphics.getDeltaTime();

            TextureRegion currentHeroAttackFrame = currentAttackHeroAnimation.getKeyFrame(attackTimer, false);
            TextureRegion currentSlashFrame = currentAttackAnimation.getKeyFrame(attackTimer, false);

            // Draw the hero's attack frame at hero's position
            spriteBatch.draw(
                currentHeroAttackFrame,
                x,
                y,
                currentHeroAttackFrame.getRegionWidth() * Main.SCALE_FACTOR,
                currentHeroAttackFrame.getRegionHeight() * Main.SCALE_FACTOR
            );

            // Position the slash animation based on direction
            if (currentAttackAnimation == slashUp) {
                spriteBatch.draw(
                    currentSlashFrame,
                    x,
                    y + 32 * Main.SCALE_FACTOR,
                    currentSlashFrame.getRegionWidth() * Main.SCALE_FACTOR,
                    currentSlashFrame.getRegionHeight() * Main.SCALE_FACTOR
                );
            } else if (currentAttackAnimation == slashDown) {
                spriteBatch.draw(
                    currentSlashFrame,
                    x,
                    y - 37 * Main.SCALE_FACTOR,
                    currentSlashFrame.getRegionWidth() * Main.SCALE_FACTOR,
                    currentSlashFrame.getRegionHeight() * Main.SCALE_FACTOR
                );
            } else if (currentAttackAnimation == slashLeft) {
                spriteBatch.draw(
                    currentSlashFrame,
                    x - 66 * Main.SCALE_FACTOR,
                    y,
                    currentSlashFrame.getRegionWidth() * Main.SCALE_FACTOR,
                    currentSlashFrame.getRegionHeight() * Main.SCALE_FACTOR
                );
            } else if (currentAttackAnimation == slashRight) {
                spriteBatch.draw(
                    currentSlashFrame,
                    x + 32 * Main.SCALE_FACTOR,
                    y,
                    currentSlashFrame.getRegionWidth() * Main.SCALE_FACTOR,
                    currentSlashFrame.getRegionHeight() * Main.SCALE_FACTOR
                );
            }

            // End the attack animation when it finishes
            if (attackTimer >= ATTACK_DURATION) {
                isAttacking = false;
            }
        } else {
            // Render normal hero animation
            currentFrame = currentAnimation.getKeyFrame(stateTime, true);
            spriteBatch.draw(
                currentFrame,
                x,
                y,
                currentFrame.getRegionWidth() * Main.SCALE_FACTOR,
                currentFrame.getRegionHeight() * Main.SCALE_FACTOR
            );
        }
    }

    public void attack() {
        isAttacking = true;
        attackTimer = 0f;
        attackCooldownTimer = 0f;

        // Choose the correct animation based on direction
        if (currentAnimation == walkUp) {
            currentAttackAnimation = slashUp;
            currentAttackHeroAnimation = attackUpAnimation;
        } else if (currentAnimation == walkDown) {
            currentAttackAnimation = slashDown;
            currentAttackHeroAnimation = attackDownAnimation;
        } else if (currentAnimation == walkLeft) {
            currentAttackAnimation = slashLeft;
            currentAttackHeroAnimation = attackLeftAnimation;
        } else {
            currentAttackAnimation = slashRight;
            currentAttackHeroAnimation = attackRightAnimation;
        }
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
        if (slashDownSheet != null) slashDownSheet.dispose();
        if (slashUpSheet != null) slashUpSheet.dispose();
        if (slashLeftSheet != null) slashLeftSheet.dispose();
        if (slashRightSheet != null) slashRightSheet.dispose();

        // Dispose new attack spritesheets
        if (attackUpSheet != null) attackUpSheet.dispose();
        if (attackDownSheet != null) attackDownSheet.dispose();
        if (attackLeftSheet != null) attackLeftSheet.dispose();
        if (attackRightSheet != null) attackRightSheet.dispose();
    }
}
