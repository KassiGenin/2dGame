package io.github.epitech_game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class NPC extends Character {

    private Texture spriteSheet;
    public Animation<TextureRegion> idle;

    private String name;
    private Array<String> dialogue;
    private Integer zone;
    private String png;
    private float time = 3f;
    private float dialogueTimer = 0f; // Timer to track time for dialogue display
    private int dialogueIndex = -1; // Index of the current dialogue being displayed
    private boolean inZone = false; // Track if the hero is in the zone

    private static final float SCALE_FACTOR = 2.5f; // Scale factor for NPC

    public NPC(String name, Array<String> dialogue, Integer zone, String png) {
        super(0, 0, 0, false);

        spriteSheet = new Texture(png);
        TextureRegion[][] frames = TextureRegion.split(spriteSheet, 28, 34);
        idle = createAnimation(frames, 0, 0, 1);

        this.name = name;
        this.dialogue = dialogue;
        this.zone = zone;
        this.png = png;
    }

    @Override
    public Animation<TextureRegion> createAnimation(TextureRegion[][] frames, int row, int startCol, int endCol) {
        TextureRegion[] animationFrames = new TextureRegion[endCol - startCol + 1];
        for (int i = startCol; i <= endCol; i++) {
            animationFrames[i - startCol] = frames[row][i];
        }
        return new Animation<>(0.8f, animationFrames); // Each frame lasts 0.8 seconds
    }

    public void render(SpriteBatch spriteBatch, float stateTime) {
        // Get the current frame of the idle animation
        TextureRegion currentFrame = idle.getKeyFrame(stateTime, true);

        // Apply scaling when rendering
        spriteBatch.draw(
            currentFrame,
            x,                                // X position
            y,                                // Y position
            currentFrame.getRegionWidth() * SCALE_FACTOR,   // Scaled width
            currentFrame.getRegionHeight() * SCALE_FACTOR  // Scaled height
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

    public void setTime(float time) {
        this.time = time;
    }

    public float getTime() {
        return time;
    }

    public void setPng(String png) {
        this.png = png;
    }

    public String getPng() {
        return png;
    }

    @Override
    public void move() {
        // NPC does not move by default
    }

    @Override
    public void attack() {
        // NPC does not attack
    }

    public void checkHeroInZone(Hero hero) {
        // Assuming the NPC's zone is a square with `zone` as its size
        if (hero.x >= x - zone && hero.x <= x + zone && hero.y >= y - zone && hero.y <= y + zone) {
            inZone = true;
        } else {
            inZone = false;
            dialogueIndex = -1; // Reset dialogue if the hero leaves the zone
            dialogueTimer = 0f;
        }
    }

    public String updateDialogue(float deltaTime) {
        if (inZone && dialogue.size > 0) {
            dialogueTimer += deltaTime;

            if (dialogueIndex == -1 || dialogueTimer >= time) {
                dialogueTimer = 0f;
                dialogueIndex++;
                if (dialogueIndex >= dialogue.size) {
                    dialogueIndex = -1; // Reset after all dialogues are displayed
                }
            }

            if (dialogueIndex != -1) {
                return dialogue.get(dialogueIndex); // Return current dialogue line
            }
        }
        return null;
    }

    public void dispose() {
        if (spriteSheet != null) spriteSheet.dispose();
    }
}
