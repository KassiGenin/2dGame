package io.github.epitech_game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class NPC1 extends NPC {

    public NPC1() {
        super(
            "Elder Willow",
            new Array<String>(new String[] {"Greetings.", "The forest holds many secrets.", "An ancient warrior lives here.","The legend says the one to defeat him ","would be granted great strength."}),
            50,
            "NPC1.png"
        );
    }

    @Override
    protected Animation<TextureRegion> createIdleAnimation() {
        TextureRegion[][] frames = TextureRegion.split(spriteSheet, 28, 34);
        TextureRegion[] idleFrames = new TextureRegion[2];
        idleFrames[0] = frames[0][0];
        idleFrames[1] = frames[0][1];
        return new Animation<>(0.8f, idleFrames);
    }

    @Override
    public void attack(){}

    @Override
    public void move(){}
}
