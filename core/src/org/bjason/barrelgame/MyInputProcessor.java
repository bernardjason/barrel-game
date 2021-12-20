package org.bjason.barrelgame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class MyInputProcessor extends InputAdapter {
    Hero hero;
    int skipLevelDebug=0;
    boolean skip = true;
    public MyInputProcessor(Hero hero) {
        this.hero= hero;
    }

    @Override
    public boolean keyDown(int keyCode) {
        handleKey(keyCode, 1,true);
        return true;
    }

    @Override
    public boolean keyUp(int keyCode) {
        handleKey(keyCode, 0,false);
        return true;
    }




    private void handleKey(int keyCode, int by,boolean jumpRelevant) {
        skipLevelDebug++;
        switch (keyCode) {
            case Input.Keys.LEFT:
                this.hero.leftRight(-by);
                break;
            case Input.Keys.RIGHT:
                this.hero.leftRight(by);
                break;
            case Input.Keys.UP:
                this.hero.upDown(by);
                break;
            case Input.Keys.DOWN:
                this.hero.upDown(-by);
                break;
            case Input.Keys.SPACE:
                if ( BarrelGame.screen == BarrelGame.Screen.MAIN) {
                    if ( jumpRelevant ) this.hero.jump();
                } else {
                    BarrelGame.newGame();
                }
                break;
            case Input.Keys.Y:
                if ( skip && skipLevelDebug >= 0 ) {
                    BarrelGameMainPlay.levelOver = true;
                    skipLevelDebug = -5;
                }
                break;
            case Input.Keys.ESCAPE:
                //Gdx.app.exit();
                BarrelGame.changeEndScreen(hero.level);
                break;
        }
    }
}
