package org.bjason.barrelgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static org.bjason.barrelgame.BarrelGameMainPlay.SCALEX;
import static org.bjason.barrelgame.BarrelGameMainPlay.SCALEY;

public class GameBorder {


    static float HEIGHT = 0;


    public GameBorder(World world) {

        //WIDTH = 37000.0f/(Gdx.graphics.getWidth()) ;//* 0.12f;
        HEIGHT = Gdx.graphics.getHeight() * SCALEY;

        createBody(world,  HEIGHT,30,0 );
        createBody(world,  HEIGHT,-1,0 );

    }

    private Body createBody(World world,  float height, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(new Vector2(x  , y * SCALEY));
        Body body = world.createBody(bodyDef);

        EdgeShape shape = new EdgeShape();
        shape.set(x ,y,x ,height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.5f;

        body.createFixture(fixtureDef);
        // Clean up after ourselves
        shape.dispose();
        body.setUserData("EDGE");
        return body;
    }


    public void hit(Body b) {
    }
}
