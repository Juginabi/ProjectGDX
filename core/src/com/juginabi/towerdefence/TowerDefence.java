package com.juginabi.towerdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TowerDefence extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
    private Sprite sprite;

    // Handles all input events
    EventHandler event = null;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("smiley.png");
        sprite = new Sprite(img);
        sprite.setSize(64f, 64f);
        sprite.setOrigin(img.getWidth() / 2, img.getHeight() / 2);
        sprite.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);

        event = new EventHandler();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Move sprite
        if (event.isKeyPressed(Input.Keys.A) || event.isKeyPressed(Input.Keys.LEFT))
            sprite.translateX(-2f);
        if (event.isKeyPressed(Input.Keys.D) || event.isKeyPressed(Input.Keys.RIGHT))
            sprite.translateX(2f);
        if (event.isKeyPressed(Input.Keys.W) || event.isKeyPressed(Input.Keys.UP))
            sprite.translateY(2f);
        if (event.isKeyPressed(Input.Keys.S) || event.isKeyPressed(Input.Keys.DOWN))
            sprite.translateY(-2f);

        // Render sprite
        batch.begin();
        sprite.draw(batch);
		batch.end();
	}

}
