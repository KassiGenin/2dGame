package io.github.epitech_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import java.util.List;

public class Wizard extends Enemy {
    private Hero hero;
    private Texture wizardTexture;
    private Texture attackTexture;
    private Animation<TextureRegion> attackAnimation;
    private float attackStateTime;
    private boolean isAttacking;
    private boolean hasDealtDamage;
    private final float FRAME_DURATION = 0.1f;
    private final float WARNING_DURATION = 2f;

    // Fixed position for the attack
    private float attackX;
    private float attackY;

    public Wizard(Hero hero) {
        super(100, 10, 0f, true, true, false, 1000);
        this.hero = hero;
        wizardTexture = new Texture("wizard.png"); // Ensure "wizard.png" exists in assets
        attackTexture = new Texture("mageAttack.png"); // Ensure "mageAttack.png" exists in assets
        attackAnimation = createAttackAnimation(attackTexture);
        attackStateTime = 0f;
        isAttacking = false;
        hasDealtDamage = false;
    }

    private Animation<TextureRegion> createAttackAnimation(Texture texture) {
        int totalFrames = 5;
        TextureRegion[] originalFrames = new TextureRegion[totalFrames];
        int[] frameWidths = {20, 20, 20, 35, 35};
        int frameHeight = 40;
        int xPosition = 0;

        for (int i = 0; i < totalFrames; i++) {
            int frameWidth = frameWidths[i];
            originalFrames[i] = new TextureRegion(texture, xPosition, 0, frameWidth, frameHeight);
            xPosition += frameWidth;
        }

        // Repeat the first frame to last 2 seconds (20 frames * 0.1f = 2s)
        TextureRegion[] extendedFrames = new TextureRegion[20 + (totalFrames - 1)];
        for(int i = 0; i < 20; i++) {
            extendedFrames[i] = originalFrames[0];
        }
        for(int i = 1; i < totalFrames; i++) {
            extendedFrames[20 + i -1] = originalFrames[i];
        }

        Animation<TextureRegion> animation = new Animation<>(FRAME_DURATION, extendedFrames);
        animation.setPlayMode(Animation.PlayMode.NORMAL);
        return animation;
    }

    @Override
    public void move() {
        // Wizard does not move
    }

    @Override
    public void attack() {
        if (!isAttacking && !isDying) {
            isAttacking = true;
            attackStateTime = 0f;
            hasDealtDamage = false;
            // Capture the hero's current position at the start of the attack
            attackX = hero.getX();
            attackY = hero.getY();
        }
    }

    @Override
    public void update(List<Enemy> newEnemies) {
        super.update();

        if (isDying) {
            return;
        }

        float deltaTime = Gdx.graphics.getDeltaTime();

        // Automatically attack if hero is within range and not already attacking
        if (!isAttacking && heroInRange()) {
            attack();
        }

        if (isAttacking) {
            attackStateTime += deltaTime;

            if (attackStateTime >= WARNING_DURATION && !hasDealtDamage) {
                Rectangle attackBounds = getAttackBounds();
                Rectangle heroBounds = hero.getBounds();
                if (attackBounds.overlaps(heroBounds)) {
                    hero.takeDamage(this.ap);
                }
                hasDealtDamage = true;
            }

            if (attackAnimation.isAnimationFinished(attackStateTime)) {
                isAttacking = false;
            }
        }
    }

    private boolean heroInRange() {
        float deltaX = hero.getX() - this.x;
        float deltaY = hero.getY() - this.y;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        return distance <= this.range;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if (!isAlive && !isDying) {
            return;
        }

        if (isDying) {
            deathStateTime += Gdx.graphics.getDeltaTime();
            if (!deathAnimation.isAnimationFinished(deathStateTime)) {
                TextureRegion deathFrame = deathAnimation.getKeyFrame(deathStateTime, false);
                spriteBatch.draw(
                    deathFrame,
                    x,
                    y,
                    deathFrame.getRegionWidth() * 2f,
                    deathFrame.getRegionHeight() * 2f
                );
            }
            return;
        }

        // Render the static wizard texture
        spriteBatch.draw(
            wizardTexture,
            x,
            y,
            wizardTexture.getWidth() * 0.2f,
            wizardTexture.getHeight()*0.2f
        );

        if (isAttacking) {
            TextureRegion attackFrame = attackAnimation.getKeyFrame(attackStateTime, false);
            // Fixed position at the hero's position when attack started, scaled 3x
            spriteBatch.draw(
                attackFrame,
                attackX,
                attackY,
                attackFrame.getRegionWidth() * 3f,
                attackFrame.getRegionHeight() * 3f
            );
        }

        // Reset color to default
        spriteBatch.setColor(1, 1, 1, 1);
    }

    @Override
    public Rectangle getBounds() {
        if (isAttacking && attackStateTime >= WARNING_DURATION) {
            TextureRegion attackFrame = attackAnimation.getKeyFrame(attackStateTime, false);
            return new Rectangle(
                attackX,
                attackY,
                attackFrame.getRegionWidth() * 3f,
                attackFrame.getRegionHeight() * 3f
            );
        } else {
            return new Rectangle(
                x,
                y,
                wizardTexture.getWidth(),
                wizardTexture.getHeight()
            );
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (wizardTexture != null) wizardTexture.dispose();
        if (attackTexture != null) attackTexture.dispose();
    }

    private Rectangle getAttackBounds() {
        TextureRegion attackFrame = attackAnimation.getKeyFrame(attackStateTime, false);
        return new Rectangle(
            attackX,
            attackY,
            attackFrame.getRegionWidth() * 3f,
            attackFrame.getRegionHeight() * 3f
        );
    }
}
