package com.juginabi.towerdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Map;

public class TowerDefence extends ApplicationAdapter {
    // Tag of this app
    private static String TAG = "TowerDefence";
	SpriteBatch batch;
	Texture img;
    private Sprite sprite;
    private BitmapFont font;

    // Handles all input events
    EventHandler event = null;
	
	@Override
	public void create () {
        Gdx.app.log(TAG, "create event!");
		batch = new SpriteBatch();
		img = new Texture("smiley.png");
        sprite = new Sprite(img);
        font = new BitmapFont();
        font.setColor(Color.RED);
        font.setScale(2f);
        //sprite.setSize(64f, 64f);
        sprite.setOrigin(img.getWidth() / 2, img.getHeight() / 2);
        sprite.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);

        event = new EventHandler();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.5f, 0.33f, 0.75f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Input.Orientation orientation = Gdx.input.getNativeOrientation();
        int deviceAngle = Gdx.input.getRotation();

        // Move sprite
        if (event.isKeyPressed(Input.Keys.A) || event.isKeyPressed(Input.Keys.LEFT))
            sprite.translateX(-2f);
        if (event.isKeyPressed(Input.Keys.D) || event.isKeyPressed(Input.Keys.RIGHT))
            sprite.translateX(2f);
        if (event.isKeyPressed(Input.Keys.W) || event.isKeyPressed(Input.Keys.UP))
            sprite.translateY(2f);
        if (event.isKeyPressed(Input.Keys.S) || event.isKeyPressed(Input.Keys.DOWN))
            sprite.translateY(-2f);
        Map<Integer, EventHandler.CursorStatus> cursorStatus =  event.getCursorStatus();

        // Render sprite
        batch.begin();
        for (int i = 0; i < cursorStatus.size(); ++i) {
            EventHandler.CursorStatus status = cursorStatus.get(i);
            String message = "";
            if ((System.currentTimeMillis() - status.getTimeSinceUpdate()) < 10000) {
                sprite.setPosition(status.getPosition().x - sprite.getWidth() / 2, (Gdx.graphics.getHeight() - status.getPosition().y) - sprite.getHeight()/2);
                message += "Device angle: " + deviceAngle + "\nOrientation: " + orientation + "\nTouch " + i + " at X: " + status.getPosition().x + ", Y: " + status.getPosition().y + "\nWill disappear in: " + (status.getTimeSinceUpdate() + 5000 - System.currentTimeMillis() + "ms");
                if(Gdx.input.isPeripheralAvailable(Input.Peripheral.Compass)){
                    message += "\nAzmuth:" + Float.toString(Gdx.input.getAzimuth()) + "\n";
                    message += "Pitch:" + Float.toString(Gdx.input.getPitch()) + "\n";
                    message += "Roll:" + Float.toString(Gdx.input.getRoll()) + "\n";
                }
                else{
                    message += "No compass available\n";
                }
                BitmapFont.TextBounds tb = font.getMultiLineBounds(message);
                float x = status.getPosition().x - tb.width/2;
                float y = (Gdx.graphics.getHeight() - status.getPosition().y) + sprite.getHeight() + tb.height/2;
                font.drawMultiLine(batch, message, x, y);
                if (Gdx.input.getPitch() > 20)
                    status.setPosition((int)status.getPosition().x-5, (int)status.getPosition().y);
                else if (Gdx.input.getPitch() < -20)
                    status.setPosition((int)status.getPosition().x+5, (int)status.getPosition().y);
                if (Gdx.input.getRoll() < -20)
                    status.setPosition((int)status.getPosition().x, (int)status.getPosition().y+5);
                else if (Gdx.input.getRoll() > 20)
                    status.setPosition((int)status.getPosition().x, (int)status.getPosition().y-5);
                sprite.draw(batch);
            }
        }
		batch.end();
	}

    @Override
    public void resize(int width, int height) {
        // Dispose all the assets here and recreate
        batch.dispose();
        batch = new SpriteBatch();
        Gdx.app.log(TAG, "resize event!");
    }

    @Override
    public void pause() {
        Gdx.app.log(TAG, "pause event!");
    }
    @Override
    public void resume() {
        Gdx.app.log(TAG, "resume event!");
    }

    public void dispose() {
        Gdx.app.log(TAG, "dispose event!");
        batch.dispose();
        font.dispose();
    }
}
