package org.bjason.barrelgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static org.bjason.barrelgame.BarrelGameMainPlay.*;

public class Barrel {
    Body body;


    //Texture texture;
    Sprite sprite;
    public static float WIDTH = 16;
    public static float HEIGHT = 22;

    static Texture texture = new Texture(Gdx.files.internal("data/barrel.png"));


    public Barrel(World world, int maxGirderLevel,boolean bottom) {
        WIDTH = texture.getWidth();
        HEIGHT = texture.getHeight();
        float startY=Gdx.graphics.getHeight();

        if ( bottom ) {
            startY=150;
        }

        if ( maxGirderLevel%2 == 0 || bottom ) {
            body = createBody(world, 5, startY);
            body.setLinearVelocity(5,0);
        } else {
            body = createBody(world, Gdx.graphics.getWidth() - 100, startY);
            body.setLinearVelocity(-5,0);
        }

        sprite = new Sprite(texture);
        body.setUserData(this);


    }

    private Body createBody(World world,float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(x * SCALEX , y * SCALEY * 1.1f));
        Body body = world.createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(WIDTH/3 * SCALEX );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 1150.0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.7f; // Make it bounce a little bit

        body.createFixture(fixtureDef);
        circleShape.dispose();
        return body;
    }


    public void render(Batch batch,boolean endScreen) {
        Vector2 position = body.getPosition();
        float velocity = extraPush();
        if ( ! endScreen) sprite.rotate(-velocity);
        //sprite.setPosition(position.x / SCALEX + WIDTH * 0.8f, position.y / DRAW_SCALEY- HEIGHT/3);
        sprite.setPosition(position.x / SCALEX + WIDTH * 0.4f, position.y / DRAW_SCALEY -1);
        sprite.draw(batch);
    }

    float extraPush() {
        float EXTRA_PUSH = 0.5f;
        float velocity = body.getLinearVelocity().x * 0.5f;
        if ( velocity > 0 ) body.applyLinearImpulse(EXTRA_PUSH,0,0,0,true);
        else body.applyLinearImpulse(-EXTRA_PUSH,0,0,0,true);
        return velocity;
    }

    public void dispose() {
        //texture.dispose();
    }


}
