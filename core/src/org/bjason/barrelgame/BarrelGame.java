package org.bjason.barrelgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BarrelGame extends ApplicationAdapter {


    BitmapFont bitmapFont;
    SpriteBatch batch;

    static BarrelGameMainPlay barrelGameMainPlay;
    static int lastLevel =0;

    public static void changeEndScreen(int level) {
        if ( BarrelGame.screen == Screen.MAIN ) {
            BarrelGame.screen = BarrelGame.Screen.END;
            ticks=0;
            lastLevel = level;
        } else {
            if ( ticks > 100 ) {
                Gdx.app.exit();
            }
        }
    }

    public static void newGame() {
        screen = Screen.MAIN;
        barrelGameMainPlay.nextLevel(true);
    }

    enum Screen {
        START,
        MAIN,
        END
    }

    static Screen screen = Screen.START;

    static int ticks = 0;
    boolean callCreate=true;

    String instructions = "Use left, right and up arrow keys to move.\n" +
            "Space to jump.\n" +
            "\n" +
            "Climb ladders and get the alien at the top.\n" +
            "\n" +
            "Press space to start\n";

    @Override
    public void create() {
        bitmapFont = new BitmapFont();
        batch = new SpriteBatch();
        barrelGameMainPlay = new BarrelGameMainPlay();
    }

    @Override
    public void render() {
        switch (screen) {
            case START:
                startScreen();
                break;
            case MAIN:
                barrelGameMainPlay.render();
                break;
            case END:
                endScreen();
                break;
        }
        if (ticks == 1 && callCreate) {
            barrelGameMainPlay.create();
            callCreate=false;
        }
        ticks++;
    }

    private void startScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        //bitmapFont.draw(batch, "Press space to start!!", 0, Gdx.graphics.getHeight() / 2);
        bitmapFont.draw(batch, instructions, 0, Gdx.graphics.getHeight() / 2);


        batch.end();
        if (ticks > 10) {
            boolean isSpace = Gdx.input.isKeyPressed(Input.Keys.SPACE);
            if (isSpace) {
                screen = Screen.MAIN;
            }
            boolean isEscape = Gdx.input.isKeyPressed(Input.Keys.ESCAPE);
            if (isEscape) {
                Gdx.app.exit();
            }
        }
    }
    private void endScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        barrelGameMainPlay.endScreenRender();
        batch.begin();
        bitmapFont.draw(batch, "You reached level "+lastLevel+"\nPress space to play again\nescape to quit", Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight() / 2);
        batch.end();
        if (ticks > 100) {
            boolean isSpace = Gdx.input.isKeyPressed(Input.Keys.SPACE);
            if (isSpace) {
                screen = Screen.MAIN;
            }
        }
        ticks++;
    }
}
