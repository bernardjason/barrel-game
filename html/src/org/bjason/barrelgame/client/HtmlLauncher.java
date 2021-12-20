package org.bjason.barrelgame.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.preloader.Preloader;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import org.bjason.barrelgame.BarrelGame;

public class HtmlLauncher extends GwtApplication {

    public Preloader.PreloaderCallback getPreloaderCallback() {
        return new Preloader.PreloaderCallback() {
            @Override
            public void error(String file) {
                System.out.println("error: " + file);
            }

            @Override
            public void update(Preloader.PreloaderState state) {
                //meterStyle.setWidth(100f * state.getProgress(), Style.Unit.PCT);
            }
        };
    }


    // UNCOMMENT THIS CODE FOR A RESIZABLE APPLICATION
    // PADDING is to avoid scrolling in iframes, set to 20 if you have problems
    private static final int PADDING = 20;
    private GwtApplicationConfiguration cfg;

    @Override
    public GwtApplicationConfiguration getConfig() {
        int w = Window.getClientWidth() - PADDING;
        int h = Window.getClientHeight() - PADDING;
        if ( h > 440 ) {
            h=440;
        }
        if ( w > 640 ) {
            w=640;
        }
        cfg = new GwtApplicationConfiguration(w, h);
        Window.enableScrolling(false);
        Window.setMargin("0");
        Window.addResizeHandler(new ResizeListener());
        cfg.preferFlash = false;
        return cfg;
    }

    class ResizeListener implements ResizeHandler {
        @Override
        public void onResize(ResizeEvent event) {
            int width = event.getWidth() - PADDING;
            int height = event.getHeight() - PADDING;
            getRootPanel().setWidth("" + width + "px");
            getRootPanel().setHeight("" + height + "px");
            getApplicationListener().resize(width, height);
            Gdx.graphics.setWindowedMode(width, height);
        }
    }


/*
        // USE THIS CODE FOR A FIXED SIZE APPLICATION
        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(640, 440);
        }

 */


    @Override
    public ApplicationListener createApplicationListener() {
        return new BarrelGame();
    }
}