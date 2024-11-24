package io.github.epitech_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class Hero extends Character {

    protected int maxHp;
    private boolean isDead = false;
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
    private float stateTime;
    private boolean isInvincible = false;
    private float invincibleTimer = 0f;

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
    private Texture attackUpSheet, attackDownSheet, attackLeftSheet, attackRightSheet;
    private Animation<TextureRegion> walkDown, walkLeft, walkUp, walkRight;
    private Animation<TextureRegion> dashDown, dashLeft, dashUp, dashRight;
    private Animation<TextureRegion> currentAnimation;
    private Animation<TextureRegion> slashDown, slashUp, slashRight, slashLeft;
    private Animation<TextureRegion> attackUpAnimation, attackDownAnimation, attackLeftAnimation, attackRightAnimation;
    private Animation<TextureRegion> currentAttackAnimation;
    private Animation<TextureRegion> currentAttackHeroAnimation;

    //Collisions attributes
    private TiledMapTileLayer collisionLayer;

    public Hero(TiledMapTileLayer collisionLayer) {
        super(120,100, 2.5f, true);  // speed before 3.5 f
        this.maxHp = 120;
        this.collisionLayer = collisionLayer;


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
        slashDown = createAttackAnimation(TextureRegion.split(slashDownSheet, 54, 37), false, false);
        slashUp = createAttackAnimation(TextureRegion.split(slashUpSheet, 54, 37), false, true);
        slashRight = createAttackAnimation(TextureRegion.split(slashRightSheet, 65, 27), true, false);
        slashLeft = createAttackAnimation(TextureRegion.split(slashLeftSheet, 65, 27), true, false);

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
    }

    @Override
    public void takeDamage(int damage) {
        if (!isInvincible) {
            hp -= damage;
            if (hp <= 0) {
                handleDeath();
            } else {
                setInvincible(2f); // Set invincibility duration to 2 seconds
            }
        }
    }

    public void setInvincible(float duration) {
        isInvincible = true;
        invincibleTimer = duration;
    }

    @Override
    public void handleDeath() {
        isDead = true;
        this.speed = 0;
        this.isHittable = false;
    }

    public boolean isDead() {
        return isDead;
    }

    public Rectangle getAttackBounds() {
        if (!isAttacking) {
            return null;
        }

        float attackX = x;
        float attackY = y;
        float attackWidth = 0;
        float attackHeight = 0;

        TextureRegion currentSlashFrame = currentAttackAnimation.getKeyFrame(attackTimer, false);

        float scale = 0.8f;

        if (currentAttackAnimation == slashUp) {
            attackX = x;
            attackY = y + 32 * scale;
            attackWidth = currentSlashFrame.getRegionWidth() * 1.2f;
            attackHeight = currentSlashFrame.getRegionHeight() * 1.2f;
        } else if (currentAttackAnimation == slashDown) {
            attackX = x;
            attackY = y - 37 * scale;
            attackWidth = currentSlashFrame.getRegionWidth() * scale;
            attackHeight = currentSlashFrame.getRegionHeight() * scale;
        } else if (currentAttackAnimation == slashLeft) {
            attackX = x - 66 * scale;
            attackY = y;
            attackWidth = currentSlashFrame.getRegionWidth() * 1.2f;
            attackHeight = currentSlashFrame.getRegionHeight() * 1.2f;
        } else if (currentAttackAnimation == slashRight) {
            attackX = x + 32 * scale;
            attackY = y;
            attackWidth = currentSlashFrame.getRegionWidth() * 1.5f;
            attackHeight = currentSlashFrame.getRegionHeight() * 1.5f;
        }

        return new Rectangle(attackX, attackY, attackWidth, attackHeight);
    }

    public Rectangle getBounds() {
        float width = 32 * GameConstants.SCALE_FACTOR;
        float height = 32 * GameConstants.SCALE_FACTOR;
        return new Rectangle(x, y, width, height);
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    private Animation<TextureRegion> createAttackAnimation(TextureRegion[][] frames, boolean horizontal, boolean reverseVertical) {
        int frameCount = horizontal ? frames[0].length : frames.length;
        TextureRegion[] animationFrames = new TextureRegion[frameCount];

        for (int i = 0; i < frameCount; i++) {
            if (horizontal) {
                animationFrames[i] = frames[0][i];
            } else {
                animationFrames[i] = frames[i][0];
            }
        }

        return new Animation<>(ATTACK_DURATION / frameCount, animationFrames);
    }

    public float getWidth() {
        return 32 * GameConstants.SCALE_FACTOR;
    }

    public float getHeight() {
        return 32 * GameConstants.SCALE_FACTOR;
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
        float baseY = Gdx.graphics.getHeight() - 50;// Top-left corner


        float heartWidth = 40; // Set the width of the hearts
        float heartHeight = 40; // Set the height of the hearts

        for (int i = 0; i < totalHearts; i++) {
            float x = baseX + i * (heartWidth +10) ; // Add a margin 10 between hearts
            float y = baseY;

            // Render heart background and border with scaling
            spriteBatch.draw(heartBg, x, y,heartWidth  , heartHeight);
            spriteBatch.draw(heartBorder, x, y, heartWidth , heartHeight);

            // Render heart state with scaling
            if (i < fullHearts) {
                spriteBatch.draw(hearts[0], x, y, heartWidth , heartHeight);
            } else if (i == fullHearts && remainder > 0) {
                spriteBatch.draw(hearts[4 - remainder], x, y, heartWidth , heartHeight);
            } else {
                spriteBatch.draw(hearts[4], x, y, heartWidth , heartHeight);
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
            float newX = x + dashDirectionX * DASH_DISTANCE * deltaTime / DASH_DURATION;
            float newY = y + dashDirectionY * DASH_DISTANCE * deltaTime / DASH_DURATION;

            // Collision detection during dash
            if (!isCollision(newX, y)) {
                x = newX;
            }
            if (!isCollision(x, newY)) {
                y = newY;
            }
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
            float newX = x;
            float newY = y;

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                newY += speed;
                currentAnimation = walkUp;
                isMoving = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                newY -= speed;
                currentAnimation = walkDown;
                isMoving = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                newX -= speed;
                currentAnimation = walkLeft;
                isMoving = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                newX += speed;
                currentAnimation = walkRight;
                isMoving = true;
            }

            // Collision detection
            if (!isCollision(newX, y)) {
                x = newX;
            }
            if (!isCollision(x, newY)) {
                y = newY;
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
                currentHeroAttackFrame.getRegionWidth() * GameConstants.SCALE_FACTOR,
                currentHeroAttackFrame.getRegionHeight() * GameConstants.SCALE_FACTOR
            );

            // Define scaling factors
            float slashScale = GameConstants.SCALE_FACTOR*0.8f; // a modifier apres
            float offsetX = 0;
            float offsetY = 0;

            // Calculate offsets based on direction
            if (currentAttackAnimation == slashUp) {
                offsetY = 32 * GameConstants.SCALE_FACTOR;
            } else if (currentAttackAnimation == slashDown) {
                offsetY = -37 * GameConstants.SCALE_FACTOR;
            } else if (currentAttackAnimation == slashLeft) {
                offsetX = -66 * GameConstants.SCALE_FACTOR;
            } else if (currentAttackAnimation == slashRight) {
                offsetX = 32 * GameConstants.SCALE_FACTOR;
                offsetY = 10 * GameConstants.SCALE_FACTOR; // Adjust if necessary
            }

            // Draw the slash animation
            spriteBatch.draw(
                currentSlashFrame,
                x + offsetX,
                y + offsetY,
                currentSlashFrame.getRegionWidth() * slashScale,
                currentSlashFrame.getRegionHeight() * slashScale
            );

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
                currentFrame.getRegionWidth() * GameConstants.SCALE_FACTOR,
                currentFrame.getRegionHeight() * GameConstants.SCALE_FACTOR
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
    private boolean isCollision(float x, float y) {
        // Get the collision layer's tile dimensions
        float tileWidth = collisionLayer.getTileWidth();
        float tileHeight = collisionLayer.getTileHeight();

        // Define the hero's bounding box dimensions
        float width = 28 * GameConstants.SCALE_FACTOR;
        float height = 28 * GameConstants.SCALE_FACTOR;

        // Check all four corners of the hero's bounding box
        int[][] corners = new int[][]{
            {(int) (x / tileWidth), (int) (y / tileHeight)}, // Bottom-left
            {(int) ((x + width) / tileWidth), (int) (y / tileHeight)}, // Bottom-right
            {(int) (x / tileWidth), (int) ((y + height) / tileHeight)}, // Top-left
            {(int) ((x + width) / tileWidth), (int) ((y + height) / tileHeight)} // Top-right
        };

        for (int[] corner : corners) {
            int tileX = corner[0];
            int tileY = corner[1];
            TiledMapTileLayer.Cell cell = collisionLayer.getCell(tileX, tileY);
            if (cell != null && cell.getTile() != null) {
                int tileID = cell.getTile().getId();
//                if (tileID != 0) {
//                    return true; // Collision detected
//                }
                // Assuming that any tile in the collision layer represents a collision
                return true; // Collision detected
            }
        }
        return false; // No collision
    }



}
