package com.juginabi.towerdefence.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.juginabi.towerdefence.TowerDefence;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "TowerDefence demo";
		config.width = 1280;
		config.height = 760;
		new LwjglApplication(new TowerDefence(), config);
	}
}
