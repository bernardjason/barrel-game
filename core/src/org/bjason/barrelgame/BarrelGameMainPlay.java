package org.bjason.barrelgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

public class BarrelGameMainPlay extends ApplicationAdapter {
    public static final int GRAVITY_DOWN_Y = -25;
    private static final int ADD_BARREL_EVERY_TICK = 400;
    private static final int ADD_FIRE_TICK = 500;
    public static final double END_SCREEN_X = 0.5;
    public static final int END_SCREEN_Y = 3;

    SpriteBatch batch;
    Girders girders;
    Hero hero;
    World world;
    ArrayList<Barrel> barrels = new ArrayList<Barrel>();
    ArrayList<Barrel> removeBarrel = new ArrayList();
    ArrayList<FireBall> fireBalls = new ArrayList();
    ArrayList<FireBall> remFireBalls = new ArrayList();
    Baddy baddy;
    GameBorder gameBorder;
    public static float SCALEX = 0.1f;
    public static float SCALEY = 0.0f;//5f;
    public static float DRAW_SCALEY = 0.0f;
    private static final float BOX_STEP = 1 / 60f;
    private static final int BOX_VELOCITY_ITERATIONS = 10;
    private static final int BOX_POSITION_ITERATIONS = 1;

    static boolean DEBUG_DRAW = false;

    private OrthographicCamera cam;
    private Box2DDebugRenderer debugRenderer;
    BitmapFont bitmapFont;
    int ticks;
    static boolean levelOver = false;

    @Override
    public void create() {
        DRAW_SCALEY = (float) (48.0 / Gdx.graphics.getHeight());
        SCALEY = (float) (48.0 / Gdx.graphics.getHeight());
        SCALEX = (float) (64.0 / Gdx.graphics.getWidth());
        bitmapFont = new BitmapFont();
        cam = new OrthographicCamera(64, 48);
        Girders.levelGap = Girders.LEVEL_GAP_START;
        cam.position.x = 29;
        cam.position.y = 23;
        cam.update();
        debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);
        world = new World(new Vector2(0, GRAVITY_DOWN_Y), true);
        batch = new SpriteBatch();
        girders = new Girders(world);
        hero = new Hero(world);
        addBarellIfNeeded();
        gameBorder = new GameBorder(world);
        baddy = new Baddy(world, girders.maxGirderLevel);

        MyInputProcessor inputProcessor = new MyInputProcessor(hero);
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(inputProcessor);
        inputMultiplexer.addProcessor(new GestureDetector(new MyGestureListener(hero)));
        Gdx.input.setInputProcessor(inputMultiplexer);

        world.setContactListener(new MyContactListener());

        barrels.add(new Barrel(world, girders.maxGirderLevel,true));
    }

    void nextLevel(boolean startScreen) {

        resetGame(true);
        if ( startScreen ) {
            hero.lives=3;
            hero.level=1;
            Girders.levelGap = Girders.LEVEL_GAP_START;

        } else {
            hero.lives++;
            hero.level++;
            if ( hero.level %4 == 0 ) {
                Girders.levelGap = Girders.LEVEL_GAP_START;
            } else {
                Girders.levelGap = Girders.levelGap - 23;
            }
        }

        for(Body girder : girders.levelGirders.values()) {
            world.destroyBody(girder);
        }
        for(Ladders.LadderInstance ladderInstance : girders.ladders.ladders.values()) {
            world.destroyBody(ladderInstance.body );
        }
        girders.dispose();

        world.destroyBody(baddy.body);
        baddy.dispose();

        girders = new Girders(world);
        baddy = new Baddy(world, girders.maxGirderLevel);
        Barrel barrel = new Barrel(world, girders.maxGirderLevel,true);
        barrel.extraPush();
        barrels.add(barrel);

        ticks=0;
    }

    private void resetGame(boolean nextLevel) {
        for (Barrel remove : barrels) {
            world.destroyBody(remove.body);
            remove.dispose();
        }
        barrels.clear();
        for (FireBall remove : fireBalls) {
            world.destroyBody(remove.body);
            remove.dispose();
        }
        fireBalls.clear();

        hero.reset();
        if ( ! nextLevel ) hero.lives--;

        if ( hero.lives == 0 ) {
            BarrelGame.changeEndScreen(hero.level);
        }

        levelOver = false;

        ticks = 0;
    }

    private void addBarellIfNeeded() {

        removeBarrel.clear();
        for (Barrel b : barrels) {
            Vector2 pos = b.body.getPosition();
            //TODO could be better
            if (pos.x < END_SCREEN_X && pos.y < END_SCREEN_Y) {
                removeBarrel.add(b);
            }
        }
        for (Barrel remove : removeBarrel) {
            world.destroyBody(remove.body);
            barrels.remove(remove);
        }
        remFireBalls.clear();
        for (FireBall b : fireBalls) {
            Vector2 pos = b.body.getPosition();
            if (pos.y < 0 || pos.x < END_SCREEN_X && pos.y < END_SCREEN_Y) {
                remFireBalls.add(b);
            }
        }
        for (FireBall remove : remFireBalls) {
            world.destroyBody(remove.body);
            fireBalls.remove(remove);
        }

        if (ticks % ADD_BARREL_EVERY_TICK == 0 || barrels.size() == 0) {
            barrels.add(new Barrel(world, girders.maxGirderLevel,false));
        }
        if (fireBalls.size() == 0 && ticks % ADD_FIRE_TICK == 100) {
            fireBalls.add(new FireBall(world, girders.maxGirderLevel));
        }
    }

    public void endScreenRender() {
        batch.begin();
        girders.render(batch);
        hero.render(batch,false);
        for (Barrel barrel : barrels) {
            barrel.render(batch,true);
        }
        for (FireBall fireBall : fireBalls) {
            fireBall.render(batch,true);
        }
        baddy.render(batch, false);
        batch.end();
    }

    @Override
    public void render() {
        ticks++;
        Box2D.init();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        girders.render(batch);
        if (girders.groundJigged) {

            hero.render(batch,true);
            for (Barrel barrel : barrels) {
                barrel.render(batch,false);
            }
            for (FireBall fireBall : fireBalls) {
                fireBall.render(batch,false);
            }
            addBarellIfNeeded();
        } else {
            if (ticks % 60 == 50) {
                girders.jigGround(world);

            }
        }
        baddy.render(batch, !girders.groundJigged);

        bitmapFont.draw(batch, "Level "+hero.level+" lives "+hero.lives, 0, Gdx.graphics.getHeight() - 10);
        //bitmapFont.draw(batch, "XXX "+hero.body.getPosition().x+" lives "+hero.lives, 0, Gdx.graphics.getHeight() - 100);

        batch.end();

        if (DEBUG_DRAW) debugRenderer.render(world, cam.combined);
        //debugRenderer.render(world, cam.combined);

        world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);

        if (hero.isPlayerDead) {
            resetGame(false);
        }
        if (levelOver == true) {
            nextLevel(false);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        girders.dispose();
    }
}
