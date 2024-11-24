package io.github.epitech_game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import java.util.List;

public class KnightBoss extends Boss {

    private Hero hero;
    private float MOVE_DURATION = 4f;
    private float moveTimer = 0f;
    private float PAUSE_DURATION = 1f;
    private float pauseTimer = 0f;
    private float stateTime = 0f;

    private boolean isAttacking = false;


    private Texture walkTexture;
    private Texture attackTexture;

    private Animation<TextureRegion> walkDownAnimation;
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> walkUpAnimation;
    private Animation<TextureRegion> currentAnimation;
    private Animation<TextureRegion> previousAnimation;

    private Animation<TextureRegion> attackDownAnimation;
    private Animation<TextureRegion> attackLeftAnimation;
    private Animation<TextureRegion> attackRightAnimation;
    private Animation<TextureRegion> attackUpAnimation;


    public KnightBoss(Hero hero){
        super(200, false, 40);
        this.hero = hero;

        walkTexture = new Texture("knightBossWalk.png");
        attackTexture = new Texture("knightBossAttack.png");

        TextureRegion[][] tmpFrames = TextureRegion.split(walkTexture, 61, 65);
        TextureRegion[][] attackFrames = TextureRegion.split(attackTexture, 65, 75);

        TextureRegion[] walkDownFrames = new TextureRegion[3];
        TextureRegion[] walkLeftFrames = new TextureRegion[3];
        TextureRegion[] walkUpFrames = new TextureRegion[3];

        TextureRegion[] attackDownFrames = new TextureRegion[2];
        TextureRegion[] attackLeftFrames = new TextureRegion[2];
        TextureRegion[] attackUpFrames = new TextureRegion[2];


        for(int i = 0; i < 3; i++) {
            walkDownFrames[i] = tmpFrames[0][i]; // Frames 1-3
            walkLeftFrames[i] = tmpFrames[1][i]; // Frames 4-6
            walkUpFrames[i] = tmpFrames[2][i];   // Frames 7-9


            walkDownAnimation = new Animation<>(0.2f, walkDownFrames);
            walkDownAnimation.setPlayMode(Animation.PlayMode.LOOP);

            walkLeftAnimation = new Animation<>(0.2f, walkLeftFrames);
            walkLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);

            // Create right animation by flipping left frames
            TextureRegion[] walkRightFrames = new TextureRegion[3];
            for (int j = 0; j < 3; j++) {
                walkRightFrames[i] = new TextureRegion(walkLeftFrames[i]);
                walkRightFrames[i].flip(true, false); // Flip horizontally
            }
            walkRightAnimation = new Animation<>(0.2f, walkRightFrames);
            walkRightAnimation.setPlayMode(Animation.PlayMode.LOOP);

            walkUpAnimation = new Animation<>(0.2f, walkUpFrames);
            walkUpAnimation.setPlayMode(Animation.PlayMode.LOOP);

            // Initialize with down animation
            currentAnimation = walkDownAnimation;
            previousAnimation = walkDownAnimation;
        }

        for(int i = 0; i < 2; i++) {
            attackDownFrames[i] = tmpFrames[0][i]; // Frames 1-3
            attackLeftFrames[i] = tmpFrames[1][i]; // Frames 4-6
            attackUpFrames[i] = tmpFrames[2][i];   // Frames 7-9


            attackDownAnimation = new Animation<>(0.4f,attackDownFrames);


            attackLeftAnimation = new Animation<>(0.4f, attackLeftFrames);

            // Create right animation by flipping left frames
            TextureRegion[] attackRightFrames = new TextureRegion[3];
            for (int j = 0; j < 3; j++) {
                attackRightFrames[i] = new TextureRegion(attackLeftFrames[i]);
                attackRightFrames[i].flip(true, false); // Flip horizontally
            }
            attackRightAnimation = new Animation<>(0.4f, attackRightFrames);

            attackUpAnimation = new Animation<>(0.2f, walkUpFrames);

        }



    }

    @Override
    public void move(){
        if(!isAlive){
            return;
        }
        float deltaTime = Gdx.graphics.getDeltaTime();
        stateTime += deltaTime;

        float distanceToHero = (float) Math.sqrt((hero.getX() - x) * (hero.getX() - x) + (hero.getY() - y) * (hero.getY() - y));
        if (distanceToHero <= range) {
            isAttacking = true;
            attack();
        }


        if (moveTimer < MOVE_DURATION) {
            moveTimer += deltaTime;
            float deltaX = hero.getX() - this.x;
            float deltaY = hero.getY() - this.y;

            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            if (distance == 0) {
                distance = 0.0001f;
            }

            float directionX = deltaX / distance;
            float directionY = deltaY / distance;

            this.x += directionX * speed;
            this.y += directionY * speed;

            if(Math.abs(directionX) > Math.abs(directionY)) {
                if(directionX > 0) {
                    currentAnimation = walkRightAnimation;
                } else {
                    currentAnimation = walkLeftAnimation;
                }
            } else {
                if(directionY > 0) {
                    currentAnimation = walkUpAnimation;
                } else {
                    currentAnimation = walkDownAnimation;
                }
            }

        }
        else if (pauseTimer < PAUSE_DURATION) {
            pauseTimer += deltaTime;
            // add distance attack
        }
        else {
            moveTimer = 0f;
            pauseTimer = 0f;

        }

        if(currentAnimation != previousAnimation){
            stateTime = 0f;
            previousAnimation = currentAnimation;
        }

    }

    public void update(List<Enemy> newEnemies) {
        super.update();
        move();
        attack();
    }

    @Override
    public void attack() {
        if (!isAlive) {
            return;
        }
        if (isAttacking) {
            if (currentAnimation == walkDownAnimation) {
                currentAnimation = attackDownAnimation;
            } else if (currentAnimation == walkLeftAnimation) {
                currentAnimation = attackLeftAnimation;
            } else if (currentAnimation == walkRightAnimation) {
                currentAnimation = attackRightAnimation;
            } else if (currentAnimation == walkUpAnimation) {
                currentAnimation = attackUpAnimation;
            }
        }
        if (isHeroInRange()) {
                hero.takeDamage(this.ap);
        }


    }


    public boolean isAlive() {
        return isAlive;
    }

    public boolean isAttacking() {
        return isAttacking;
    }


    private boolean isValidFrames(){
        switch (currentAnimation.getKeyFrameIndex(stateTime)){
            case 1:

                return true;
            default:
                return false;
        }
    }

    private boolean isHeroInRange() {
        float deltaX = hero.getX() - this.x;
        float deltaY = hero.getY() - this.y;
        return Math.abs(deltaX) <= this.range && Math.abs(deltaY) <= range;
    }

    public Rectangle getBounds() { //placeholder
        return new Rectangle(x, y, walkTexture.getWidth() * 2f, walkTexture.getHeight() * 2f);

    }
    @Override
    public void render(SpriteBatch spriteBatch) {
        if(isAlive){
            TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
            spriteBatch.draw(currentFrame, x, y, currentFrame.getRegionWidth() * 2f, currentFrame.getRegionHeight() * 2f);
        }
    }

    @Override
    public void dispose() {
        walkTexture.dispose();
        // Dispose other resources if any
    }



}
