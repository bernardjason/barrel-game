package org.bjason.barrelgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.HashMap;

import static org.bjason.barrelgame.BarrelGameMainPlay.*;

public class Girders {


    private Texture texture;
    HashMap<XY, Body> levelGirders = new HashMap();
    public static float GIRDER_WIDTH = 32;
    public static float GIRDER_HEIGHT = 8;
    public static final int LEVEL_GAP_START = 130;
    public static int levelGap = LEVEL_GAP_START;
    Ladders ladders;
    private float bodyHeight;
    int maxGirderLevel=0;
    private float fudgePositionY;
    float endJigGround = levelGap;
    boolean groundJigged = false;

    public Girders(World world) {


        ladders = new Ladders();
        texture = new Texture(Gdx.files.internal("data/girder.png"));
        float startFrom = 0;
        float end = Gdx.graphics.getWidth();
        int alternate = 0;

        fudgePositionY = Gdx.graphics.getHeight() / 215;


        for (float y = 0; y < Gdx.graphics.getHeight() - levelGap; y = y + levelGap) {
            for (float x = startFrom; x < end; x = x + GIRDER_WIDTH) {
                XY key = new XY(x, y);
                Sprite sprite = new Sprite(texture);
                Body b = createBody(world, GIRDER_WIDTH, GIRDER_HEIGHT, x, y);
                b.setUserData(sprite);
                levelGirders.put(key, b);
            }
            if (alternate % 2 == 0) {
                end = Gdx.graphics.getWidth() - GIRDER_WIDTH * 3;
                startFrom = 0;
            } else {
                end = Gdx.graphics.getWidth();
                startFrom = GIRDER_WIDTH * 2;
            }
            alternate++;
            maxGirderLevel++;
        }
    }

    float angle = -1;
    float addBy = 0.06f;
    void jigGround(World world) {
        float end = Gdx.graphics.getWidth();
        for (float y = endJigGround - levelGap; y < endJigGround ; y = y + levelGap) {
            float yTally = 0;
            float lastHeight=0;
            float lastx=0;
            for (float x = GIRDER_WIDTH*2; x < end - GIRDER_WIDTH*2f ; x = x + GIRDER_WIDTH) {
                lastx=x;
                XY key = new XY(x, y);
                Body currentGirder = levelGirders.get(key);
                if (currentGirder != null  ) {
                    Vector2 curentPosition = currentGirder.getTransform().getPosition();
                    curentPosition.y += yTally;
                    lastHeight = curentPosition.y;
                    currentGirder.setTransform(curentPosition, currentGirder.getAngle() - (float) Math.toRadians(angle));
                    ((Sprite) currentGirder.getUserData()).rotate(-angle);
                }
                yTally = yTally + addBy;
            }
            for (float x = lastx; x <= end+GIRDER_WIDTH*2 ; x = x + GIRDER_WIDTH) {
                XY key = new XY(x, y);
                Body currentGirder = levelGirders.get(key);
                if (currentGirder != null  ) {
                    Vector2 curentPosition = currentGirder.getTransform().getPosition();
                    curentPosition.y = lastHeight;
                    currentGirder.setTransform(curentPosition, currentGirder.getAngle() - (float) Math.toRadians(angle));
                }
            }

            addBy = addBy * -1;
            angle = angle * -1;
        }
        endJigGround = endJigGround + levelGap;
        if ( endJigGround >= Gdx.graphics.getHeight() ) {
            this.groundJigged = true;
            addAllLadders(world);
        }

    }

    void addAllLadders(World world) {
        int startFrom = 1;
        int end = Gdx.graphics.getWidth();
        int level = 0;
        for (int y = 0; y < Gdx.graphics.getHeight() - levelGap; y = y + levelGap) {
            level++;
            if (level < maxGirderLevel) {
                for (int x = (int) (startFrom * GIRDER_WIDTH); x < end; x = (int) (x + GIRDER_WIDTH)) {
                    XY key = new XY(x, y);
                    //if (cellCount == (3 + stagger)) {
                    XY aboveKey = new XY(x, y + levelGap);
                    Body currentGirder = levelGirders.get(key);
                    Body above = levelGirders.get(aboveKey);
                    if (above == null && currentGirder != null) {
                        Body left = levelGirders.get( new XY(x-GIRDER_WIDTH,y+ levelGap));
                        Body right = levelGirders.get( new XY(x+GIRDER_WIDTH,y+ levelGap));
                        Vector2 currentGirderPosition = currentGirder.getPosition();
                        float height = 0;//(above.getTransform().getPosition().y - currentGirderPosition.y) + 1;
                        if ( right != null ) {
                            height =( right.getTransform().getPosition().y - currentGirderPosition.y) ;
                        } else if ( left != null ) {
                            height =( left.getTransform().getPosition().y - currentGirderPosition.y) ;
                        }
                        height=height + GIRDER_HEIGHT * DRAW_SCALEY;
                        float origX = currentGirderPosition.x;

                        ladders.addLadder(world, origX, currentGirderPosition.y , height);
                        break;
                    }

                }
            }
        }
    }



    private Body createBody(World world, float width, float height, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyHeight = height * SCALEY / 2;
        bodyDef.position.set(new Vector2(x * SCALEX, y * SCALEY + bodyHeight));
        Body body = world.createBody(bodyDef);
        PolygonShape rectangle = new PolygonShape();
        rectangle.setAsBox(width * SCALEX / 2, height * SCALEY / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = rectangle;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.0f; // Make it bounce a little bit

        body.createFixture(fixtureDef);
        rectangle.dispose();
        return body;
    }


    public void render(Batch batch) {
        for (Body b : levelGirders.values()) {
            Sprite sprite = (Sprite) b.getUserData();
            Vector2 position = b.getPosition();
            position.y+= bodyHeight*fudgePositionY;
            sprite.setPosition((position.x) / SCALEX + sprite.getWidth() / 2, position.y / DRAW_SCALEY   );

            if (!DEBUG_DRAW) sprite.draw(batch);
        }
        ladders.render(batch);

    }

    public void dispose() {
        ladders.dispose();
        texture.dispose();
    }
}