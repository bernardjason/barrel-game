package org.bjason.barrelgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.HashMap;
import java.util.Map;

import static org.bjason.barrelgame.BarrelGameMainPlay.*;
import static org.bjason.barrelgame.Girders.GIRDER_HEIGHT;

public class Ladders {

    private float bodyHeight;
    private float textureHeight;

    class LadderInstance {
        Sprite sprite;
        Body body;

        public LadderInstance(Sprite newLadder, Body body) {
            this.sprite=newLadder;
            this.body = body;
        }
    }

    //private Texture texture;
    Map<XY, LadderInstance> ladders = new HashMap();

    public static int LADDER_WIDTH = 32;
    public static int LADDER_RUNG_HEIGHT = 8;


    public Ladders() {
        //texture = new Texture(Gdx.files.internal("data/ladder.png"));
    }
    public void addLadder(World world,float x, float y , float height) {

       Texture ladder = makeTexture(height   );

        Sprite newLadder = new Sprite(ladder);
        Body body = createBody(world,LADDER_WIDTH  *0.55f,height  ,x,y);
        LadderInstance ladderInstance = new LadderInstance(newLadder,body);
        body.setUserData(ladderInstance);

        this.ladders.put( new XY(x,y),ladderInstance);

    }

    private Texture makeTexture(float height) {
        textureHeight = height/DRAW_SCALEY ;
        Pixmap pixmap = new Pixmap(LADDER_WIDTH, (int) textureHeight, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GOLD);
        pixmap.fillRectangle(0,0,2, (int) textureHeight);
        pixmap.fillRectangle(LADDER_WIDTH-2,0,2, (int) textureHeight);
        for(int rungs = 0; rungs < textureHeight ; rungs+=LADDER_RUNG_HEIGHT) {
            pixmap.fillRectangle(0,rungs,LADDER_WIDTH,2);
        }
        Texture ladder = new Texture(pixmap);
        pixmap.dispose();
        return ladder;
    }

    private Body createBody(World world, float width, float height, float x, float y) {
        bodyHeight =  height ;
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(new Vector2(x  , y  + bodyHeight/2 ));
        Body body = world.createBody(bodyDef);

        PolygonShape rectangle = new PolygonShape();
        rectangle.setAsBox(width * SCALEX/2 , bodyHeight/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = rectangle;
        fixtureDef.density = 0.0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.isSensor = true;
        fixtureDef.restitution = 0.0f; // Make it bounce a little bit

        body.createFixture(fixtureDef);
        rectangle.dispose();
        return body;
    }

    public void render(Batch batch) {

        for (LadderInstance ladderInstance : ladders.values()) {
            Vector2 position = ladderInstance.body.getPosition();
            position.x =(position.x+1.5f)/SCALEX ;
            position.y =( position.y /SCALEY );

            position.y = position.y - ladderInstance.sprite.getHeight()/2 + GIRDER_HEIGHT*2;
            ladderInstance.sprite.setPosition(position.x,position.y);
            if ( ! DEBUG_DRAW ) ladderInstance.sprite.draw(batch);
        }

    }

    public void dispose() {
        //texture.dispose();
        for (LadderInstance ladderInstance : ladders.values()) {
            ladderInstance.sprite.getTexture().dispose();
        }
    }
}
