package org.bjason.barrelgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.bjason.barrelgame.BarrelGame;
import org.bjason.barrelgame.BarrelGameMainPlay;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 640;
		config.width = 640;
		config.height = 540;
		//config.width = 640;
		new LwjglApplication(new BarrelGame(), config);
	}
}
