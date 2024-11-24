package io.github.epitech_game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class NPC2 extends NPC {

    public NPC2() {
        super(
            "Royal guard",
            new Array<String>(new String[] {"bla bla bla too weak", "aura kill you", "go see the elder"}),
            150,
            "NPC2.png"
        );
    }

    @Override
    protected Animation<TextureRegion> createIdleAnimation() {
        TextureRegion[][] frames = TextureRegion.split(spriteSheet, 32, 32);
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
