package io.github.epitech_game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Main extends ApplicationAdapter {
    public static final float SCALE_FACTOR = 2.5f; // Scaling sprites to 2.5x their original size

    private Hero hero;
    private NPC npc;
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    private BitmapFont font;
    private float stateTime;

    @Override
    public void create() {
        // Initialize Hero and NPC
        hero = new Hero();

        npc = new NPC("Guide",
            new com.badlogic.gdx.utils.Array<>(new String[]{
                "Hello, traveler!",
                "The world is full of mysteries...",
                "Be careful out there!"
            }),
            100, "NPC1.png");

        npc.setPosition(700, 600); // Position the NPC on the screen

        // Initialize rendering tools
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        stateTime = 0f;
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        stateTime += deltaTime; // Update state time for animations

        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update hero movement
        hero.move();

        // Check if hero is in the NPC's zone and update NPC dialogue
        npc.checkHeroInZone(hero);
        String currentDialogue = npc.updateDialogue(deltaTime);

        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        // Begin rendering
        spriteBatch.begin();
        npc.render(spriteBatch, stateTime); // Render NPC with scaling applied
        hero.render(spriteBatch);
        hero.renderHearts(spriteBatch);
        spriteBatch.end();

        // Draw dialogue box if NPC text is available
        if (currentDialogue != null) {
            renderDialogueBox(currentDialogue);
        }
    }

    private void renderDialogueBox(String text) {
        float boxWidth = 1200; // Width of the dialogue box
        float boxHeight = 100; // Height of the dialogue box
        float boxX = (1400 - boxWidth) / 2; // Centered horizontally on a 1400x1400 screen
        float boxY = 50; // 50px from the bottom of the screen

        // Draw the dialogue box background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.7f)); // Semi-transparent black
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight);
        shapeRenderer.end();

        // Draw the dialogue text
        spriteBatch.begin();
        font.draw(spriteBatch, text, boxX + 20, boxY + 70); // Text inside the box with padding
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Handle resizing
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }

    @Override
    public void dispose() {
        // Dispose resources
        spriteBatch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        hero.dispose();
        npc.dispose(); // Properly dispose NPC resources
    }
}
