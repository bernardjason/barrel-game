package org.bjason.barrelgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static org.bjason.barrelgame.BarrelGameMainPlay.*;

public class FireBall {
    private static final float START_Y = Gdx.graphics.getHeight();
    Body body;
    boolean startRolling = false;


    Texture texture = new Texture(Gdx.files.internal("data/fire.png"));
    Sprite sprite;
    public static float WIDTH = 32;
    public static float HEIGHT = 32;
    int animation = 0;


    public FireBall(World world, int maxGirderLevel) {
        //WIDTH = texture.getWidth();
        //HEIGHT = texture.getHeight();

        body = createBody(world,  Hero.currentX, START_Y * SCALEY);
        if ( maxGirderLevel%2 == 0 ) {
            body.setLinearVelocity(15,0);
        } else {
            body.setLinearVelocity(-15,0);
        }



        sprite = new Sprite(texture);
        sprite.setSize(32,32);
        sprite.setCenter(16,16);
        sprite.setOrigin(16,16);
        body.setUserData(this);
        //body.setLinearVelocity(Hero.currentX,0);

    }

    void firstTimeEnabled() {
        if ( ! startRolling ) {
            if ( body.getPosition().x / SCALEX > Gdx.graphics.getWidth()/2 ) {
                body.applyLinearImpulse(10,0,0,0,true);
            } else {
                body.applyLinearImpulse(-10,0,0,0,true);
            }
            startRolling = true;
        }
    }

    private Body createBody(World world,  float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(x , y ));
        Body body = world.createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(WIDTH/3 * SCALEX );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 100.0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.1f; // Make it bounce a little bit

        body.createFixture(fixtureDef);
        // Clean up after ourselves
        circleShape.dispose();
        body.setGravityScale(0.5f);
        return body;
    }




    public void render(Batch batch,boolean endScreen) {
        Vector2 position = body.getPosition();
        float velocity = body.getLinearVelocity().x * 0.5f;

        sprite.setRegion((animation /2) % 4 * 32, 0, 32, 32);

        if ( ! endScreen) {
            sprite.rotate(-velocity);
            animation++;
        }
        sprite.setPosition(position.x / SCALEX + WIDTH * 0.4f, position.y / DRAW_SCALEY -1);
        sprite.draw(batch);
    }

    public void dispose() {
        //texture.dispose();
    }

}
