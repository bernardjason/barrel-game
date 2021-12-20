package org.bjason.barrelgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static org.bjason.barrelgame.BarrelGameMainPlay.*;

public class Baddy {
    Body body;
    private static final float START_Y = Gdx.graphics.getHeight();

    Texture texture = new Texture(Gdx.files.internal("data/baddy.png"));
    Sprite sprite;
    public static final float THE_WIDTH = 64;
    public static final float THE_HEIGHT = 64;
    int animation = 0;

    int ticks=0;

    public Baddy(World world,int maxGirderLevel) {

        if ( maxGirderLevel%2 == 0 ) {
            //body = createBody(world, (Gdx.graphics.getWidth()-100) * SCALEX, START_Y * SCALEY);
            body = createBody(world, THE_WIDTH/5 * SCALEX, START_Y * SCALEY);
        } else {
            //body = createBody(world, (Gdx.graphics.getWidth() - THE_WIDTH*8) * SCALEX, START_Y-100);
            body = createBody(world, (Gdx.graphics.getWidth() - THE_WIDTH ) * SCALEX, START_Y * SCALEY);
        }


        sprite = new Sprite(texture);
        sprite.setSize(THE_WIDTH,THE_HEIGHT);
        sprite.setCenter(THE_WIDTH/2,THE_HEIGHT/2);
        sprite.setOrigin(THE_WIDTH/2,THE_HEIGHT/2);
        body.setUserData(this);
        //body.setLinearVelocity(Hero.currentX,0);

    }

    private Body createBody(World world,  float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(x , y ));
        Body body = world.createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(THE_HEIGHT/4 * SCALEX );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 50.0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.75f; // Make it bounce a little bit

        body.createFixture(fixtureDef);
        // Clean up after ourselves
        circleShape.dispose();
        body.setGravityScale(0.5f);
        return body;
    }



    float previousVelocityY = 1;

    public void render(Batch batch,boolean bounce) {
        Vector2 position = body.getPosition();

        if ( bounce ) {
            animation++;
            if ( previousVelocityY < 0 || body.getLinearVelocity().y < 0 ) {
                previousVelocityY = body.getLinearVelocity().y;
                body.setLinearVelocity(0,-10);
            }
        } else {
            ticks++;
            animation=0;
            if ( body.getLinearVelocity().y > 0 ) {
                body.getFixtureList().get(0).getBody().getFixtureList().get(0).setRestitution(0);
                body.getFixtureList().get(0).getBody().getFixtureList().get(0).setDensity(10000);
            }
            if ( ticks == 200 ) {

                body.getFixtureList().get(0).getBody().setType(BodyDef.BodyType.StaticBody);
            }
        }

        sprite.setRegion(((animation /12) % 4) * 64, 0, 64,64);

        if ( bounce ) {
            sprite.setPosition(position.x / SCALEX , position.y / DRAW_SCALEY  - THE_HEIGHT * 0.2f );
        } else {
            sprite.setPosition(position.x / SCALEX , position.y / DRAW_SCALEY  - THE_HEIGHT * 0.15f);
        }
        sprite.draw(batch);
    }

    public void dispose() {
        texture.dispose();
    }

}
