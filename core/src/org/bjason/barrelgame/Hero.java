package org.bjason.barrelgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static org.bjason.barrelgame.BarrelGameMainPlay.*;

public class Hero {

    public static float JUMP_FORCE = 10000;
    public static final int MULTIPLY_DIRECTION = 100;
    Body body;

    static float currentY;
    static float currentX;

    Vector2 direction = new Vector2();

    Texture runningTexture;
    Texture climbingTexture;
    Sprite sprite;
    public static float WIDTH = 10;
    public static float HEIGHT = 24;
    boolean canJump;
    boolean ladderClimbingEnabled;
    Ladders.LadderInstance ladderClimbing;
    boolean isPlayerDead;
    int animation = 0;
    int lives = 3;
    int level = 1;
    private boolean onResetLeftRightFirst;


    private enum HIT_OBJECTS {GIRDER, NOTHING, LADDER}

    HIT_OBJECTS lastHitObject;
    public static float START_X = 10;
    public static float START_Y = 40;


    public Hero(World world) {

        JUMP_FORCE = 5500;
        float over = (170.0f / ( 200.0f + 640.0f - Gdx.graphics.getWidth())) ;
        JUMP_FORCE = JUMP_FORCE / over;
        //System.out.println("JUMP "+JUMP_FORCE);
        body = createBody(world, WIDTH, HEIGHT, START_X, START_Y);

        runningTexture = new Texture(Gdx.files.internal("data/running_right.png"));
        climbingTexture = new Texture(Gdx.files.internal("data/climbing.png"));

        sprite = new Sprite(runningTexture);
        body.setUserData(this);
        reset();

    }

    public void reset() {
        body.setTransform(new Vector2(START_X * SCALEX, START_Y * SCALEY), 0);
        ladderEnd();
        isPlayerDead = false;
        onResetLeftRightFirst=true;
    }

    private Body createBody(World world, float width, float height, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(x * SCALEX, y * SCALEY));
        Body body = world.createBody(bodyDef);

        CircleShape groundBox = new CircleShape();
        groundBox.setRadius(width * SCALEX);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = groundBox;
        fixtureDef.density = 2.0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.1f; // Make it bounce a little bit

        body.createFixture(fixtureDef);
        // Clean up after ourselves
        groundBox.dispose();
        return body;
    }


    static float DRIFT_AS_SLOPE = 2.0f;

    public void render(Batch batch,boolean animate) {
        boolean flipX = false;
        Vector2 velocity = body.getLinearVelocity();
        if ( Math.abs(velocity.x) < DRIFT_AS_SLOPE && Math.abs(velocity.y) > DRIFT_AS_SLOPE) {
            if ( animate) animation++;
        } else {

            if (velocity.x > DRIFT_AS_SLOPE) {
                if ( animate) animation++;
            } else if (velocity.x < -DRIFT_AS_SLOPE) {
                if ( animate) animation++;
                flipX = true;
            } else animation = 0;

        }
        //TODO frig as sometimes velocity at top of ladder gets reduced.
        if (!direction.isZero()) leftRight(direction.x);


        Vector2 position = body.getPosition();
        currentY = position.y;
        currentX = position.x;

        if (ladderClimbingEnabled && ladderClimbing != null && direction.y < 0) {
            if (position.y + 5 <= ladderClimbing.body.getPosition().y) {
                body.setLinearVelocity(0, 0);
            }
        }
        if (ladderClimbingEnabled == false) {
            this.body.setGravityScale(1);
        }
        sprite.setPosition(position.x / SCALEX + WIDTH * 2, position.y / DRAW_SCALEY);
        if (ladderClimbingEnabled) {
            batch.draw(climbingTexture, sprite.getX(), sprite.getY(), 24, 32, (animation /2) % 4 * 24, 0, 24, 32, false, false);
        } else {
            batch.draw(runningTexture, sprite.getX(), sprite.getY(), 24, 32, animation /2 % 4 * 24, 0, 24, 32, flipX, false);
        }
        //sprite.draw(batch);

    }

    public void dispose() {
        runningTexture.dispose();
        climbingTexture.dispose();
    }

    public void leftRight(float by) {
        onResetLeftRightFirst= false;
        if (canJump && lastHitObject != HIT_OBJECTS.NOTHING) {
            if (by == 0) {
                body.setLinearVelocity(0, 0);
            }
        }
        if ((canJump && lastHitObject == HIT_OBJECTS.GIRDER) || ladderClimbingEnabled) {
            body.setLinearVelocity(by * 15, direction.y);
        }
        this.direction.x = by;
    }

    public void upDown(int by) {

        if (ladderClimbingEnabled && canJump == true) {
            body.setLinearVelocity(0, by * 10);
            this.direction.y = by * 10;
        }
    }

    public void jump() {
        if ( onResetLeftRightFirst ) return;
        if (canJump && lastHitObject != HIT_OBJECTS.LADDER) {
            canJump = false;
            // odd frig, occasionally it would stick, I assume a contact on a girder. So move above.
            body.setTransform(body.getPosition().add(0, 0.5f), 0);
            //body.setLinearVelocity(body.getLinearVelocity().x, 9);
            body.applyForceToCenter(new Vector2(0, JUMP_FORCE), false);
        }
    }

    public void hit(Body b) {

        if (b.getPosition().y < body.getPosition().y && ladderClimbingEnabled == false) {
            canJump = true;
            updateLastObjectHit(b.getUserData());
        }
        if (direction.x == 0 && ladderClimbingEnabled == false) {
            body.setLinearVelocity(0, 0);
            updateLastObjectHit(b.getUserData());
        }
        if (b.getUserData() instanceof Ladders.LadderInstance) {
            ladderClimbingEnabled = true;
            canJump=true;
            this.body.setGravityScale(0);
            this.ladderClimbing = (Ladders.LadderInstance) b.getUserData();
            if (direction.x == 0) body.setLinearVelocity(0, 0);
            updateLastObjectHit(b.getUserData());
        }
        if (b.getUserData() instanceof Barrel || b.getUserData() instanceof  FireBall ) {
            isPlayerDead = true;
        }
        if (b.getUserData() instanceof Baddy ) {
            BarrelGameMainPlay.levelOver = true;
        }

    }

    private void updateLastObjectHit(Object o) {
        if (o instanceof Sprite) {
            lastHitObject = HIT_OBJECTS.GIRDER;
        }
        if (o instanceof Ladders.LadderInstance) {
            lastHitObject = HIT_OBJECTS.LADDER;
        }
    }

    public void ladderEnd() {

        this.ladderClimbing = null;
        direction.y = 0;
        this.body.setGravityScale(1);

        // https://stackoverflow.com/questions/17884587/having-trouble-with-box2d-getcontactlist-used-via-libgdx
        if (direction.x == 0) body.setLinearVelocity(0, 0);
        else body.setLinearVelocity(body.getLinearVelocity().x, 0);


        ladderClimbingEnabled = false;
        lastHitObject = HIT_OBJECTS.NOTHING;
    }

    @Override
    public String toString() {
        return "Hero{" +
                "canJump=" + canJump +
                ", ladderClimbingEnabled=" + ladderClimbingEnabled +
                " position=" + body.getPosition() +
                " velocity=" + body.getLinearVelocity().x +
                '}';
    }
}
