package com.code2play.quickout;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Main application entry to the game
 * @author Jitrapon Tiachunpun
 * 
 */
public class GameMain extends Game {

	public SpriteBatch batch;
	public BitmapFont font;

	@Override
	public void create () {
		Assets.load();
		batch = new SpriteBatch();
		font = new BitmapFont();
		this.setScreen(new MainMenuScreen(this));
	}
	
	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose() {
		// dispose of all the native resources
		batch.dispose();
		font.dispose();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
