package io.github.epitech_game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public abstract class NPC extends Character {

    protected Texture spriteSheet;
    protected Animation<TextureRegion> idleAnimation;

    protected String name;
    protected Array<String> dialogue;
    protected Integer zone;
    protected String png;
    protected float dialogueDisplayTime = 3f;
    protected float dialogueTimer = 0f;
    protected int dialogueIndex = -1;
    protected boolean inZone = false;

    protected static final float SCALE_FACTOR = 2.5f;

    public NPC(String name, Array<String> dialogue, Integer zone, String png) {
        super(0, 0, 0, false);
        this.name = name;
        this.dialogue = dialogue;
        this.zone = zone;
        this.png = png;
        this.spriteSheet = new Texture(png);
        this.idleAnimation = createIdleAnimation();
    }

    protected abstract Animation<TextureRegion> createIdleAnimation();

    public void render(SpriteBatch spriteBatch, float stateTime) {
        TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        spriteBatch.draw(
            currentFrame,
            x,
            y,
            currentFrame.getRegionWidth() * SCALE_FACTOR,
            currentFrame.getRegionHeight() * SCALE_FACTOR
        );
    }

    public void checkHeroInZone(Hero hero) {
        float deltaX = hero.getX() - this.x;
        float deltaY = hero.getY() - this.y;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (distance <= this.zone) {
            inZone = true;
        } else {
            inZone = false;
            dialogueIndex = -1;
            dialogueTimer = 0f;
        }
    }

    public String updateDialogue(float deltaTime) {
        if (inZone && dialogue.size > 0) {
            dialogueTimer += deltaTime;
            if (dialogueIndex == -1 || dialogueTimer >= dialogueDisplayTime) {
                dialogueTimer = 0f;
                dialogueIndex++;
                if (dialogueIndex >= dialogue.size) {
                    dialogueIndex = -1;
                }
            }
            if (dialogueIndex != -1) {
                return dialogue.get(dialogueIndex);
            }
        }
        return null;
    }

    public void dispose() {
        if (spriteSheet != null) spriteSheet.dispose();
    }


    public Rectangle getBounds() {
        return new Rectangle(
            x,
            y,
            spriteSheet.getWidth() * SCALE_FACTOR,
            spriteSheet.getHeight() * SCALE_FACTOR
        );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Array<String> getDialogue() {
        return dialogue;
    }

    public void setDialogue(Array<String> dialogue) {
        this.dialogue = dialogue;
    }

    public void addDialogue(String dialogue) {
        this.dialogue.add(dialogue);
    }

    public Integer getZone() {
        return zone;
    }

    public void setZone(Integer zone) {
        this.zone = zone;
    }

    public void setPng(String png) {
        this.png = png;
    }

    public String getPng() {
        return png;
    }
}
