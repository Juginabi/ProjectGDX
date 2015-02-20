package com.juginabi.towerdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TowerDefence extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	Texture img;
    private Sprite sprite;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("smiley.png");
        sprite = new Sprite(img);
        sprite.setSize(64f, 64f);
        sprite.setOrigin(img.getWidth() / 2, img.getHeight() / 2);
        sprite.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        //sprite.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            sprite.setPosition(Gdx.input.getX() - sprite.getWidth()/2,
                    Gdx.graphics.getHeight() - Gdx.input.getY() - sprite.getHeight()/2);
        }
        if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT)){
            sprite.setPosition(Gdx.graphics.getWidth()/2 -sprite.getWidth()/2,
                    Gdx.graphics.getHeight()/2 - sprite.getHeight()/2);
        }

        batch.begin();
        sprite.draw(batch);
		batch.end();
	}

    @Override
    public boolean keyDown(int keycode) {

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                sprite.translateX(-1f);
            else
                sprite.translateX(-10.0f);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                sprite.translateX(1f);
            else
                sprite.translateX(10.0f);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                sprite.translateY(-1f);
            else
                sprite.translateY(-10.0f);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                sprite.translateY(1f);
            else
                sprite.translateY(10.0f);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
