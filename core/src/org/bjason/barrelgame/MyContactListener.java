package org.bjason.barrelgame;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;

public class MyContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Body bodyB = contact.getFixtureB().getBody();
        Body bodyA = contact.getFixtureA().getBody();

        heroIt(bodyB, bodyA);
        heroIt(bodyA, bodyB);
    }

    private void heroIt(Body bodyB, Body bodyA) {
        if (bodyA.getUserData() instanceof Hero) {
            Hero h = (Hero) bodyA.getUserData();
            h.hit(bodyB);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Body bodyB = contact.getFixtureB().getBody();
        Body bodyA = contact.getFixtureA().getBody();

        if ( bodyA.getUserData() instanceof Ladders.LadderInstance  && bodyB.getUserData() instanceof  Hero ) {
            Hero h = (Hero) bodyB.getUserData();
            h.ladderEnd();
        }
        if ( bodyB.getUserData() instanceof Ladders.LadderInstance  && bodyA.getUserData() instanceof  Hero) {
            Hero h = (Hero) bodyA.getUserData();
            h.ladderEnd();
        }

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        ignoreFireBall(contact, a, b);
        ignoreFireBall(contact, b, a);

    }

    private void ignoreFireBall(Contact contact, Fixture fa, Fixture fb) {
        Body a  = fa.getBody();
        Body b = fb.getBody();
        if ( a.getUserData() instanceof FireBall && b.getUserData() instanceof Barrel )   {
            contact.setEnabled(false);
        }

        if ( a.getUserData() instanceof FireBall && b.getUserData() instanceof Sprite )   {
            FireBall fireBall = (FireBall) a.getUserData();
            if ( Math.abs(fireBall.body.getPosition().y - Hero.currentY) > 10) {
                contact.setEnabled(false);
            } else {
                fireBall.firstTimeEnabled();
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
