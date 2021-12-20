package org.bjason.barrelgame;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

public class MyGestureListener implements GestureDetector.GestureListener {
    private static final float SENSITIVE_X = 10;
    private static final float SENSITIVE_Y = 4;
    Hero hero ;
    public MyGestureListener(Hero hero) {
        this.hero = hero;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        if ( BarrelGame.screen == BarrelGame.Screen.START || BarrelGame.screen == BarrelGame.Screen.END) {
            BarrelGame.newGame();
        }
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        System.out.println("PAN "+x+" "+y+" "+deltaX+" "+deltaY);
        float by = 1;
        if ( deltaX > SENSITIVE_X ) {
            this.hero.leftRight(by);
        }
        if ( deltaX < -SENSITIVE_X ) {
            this.hero.leftRight(-by);
        }
        if ( deltaY > SENSITIVE_Y ) {
            this.hero.upDown((int) -by);
        }
        if ( deltaY < - SENSITIVE_Y ) {
            if ( this.hero.ladderClimbingEnabled == false && this.hero.canJump ) {
                hero.jump();
            } else {
                this.hero.upDown((int) by);
            }
        }
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        this.hero.leftRight(0);
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
