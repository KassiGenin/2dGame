package io.github.epitech_game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class NPC2 extends NPC {

    public NPC2() {
        super(
            "Royal guard",
            new Array<String>(new String[] {"Don't go there, the dark knight would kill you.", "He took the place of the king months ago.", "Nobody is strong enough to face him.", "A simple gaze would have reason of you.", "If you want to disturb someone, go see the elder"}),
            70,
            "NPC2.png"
        );
    }
    // faire le changement de dialogue dans le main if hero.getmaxhp> vjebvje truc

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
