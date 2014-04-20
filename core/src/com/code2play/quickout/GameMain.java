package com.code2play.quickout;

import com.badlogic.gdx.Game;

/**
 * Main application entry to the game
 * @author Jitrapon Tiachunpun
 * 
 */
public class GameMain extends Game {
	
	MainMenuScreen mainMenuScreen;

	@Override
	public void create () {
		Assets.load();
		mainMenuScreen = new MainMenuScreen(this);
		this.setScreen(mainMenuScreen);
	}
	
	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose() {
		// dispose of all the native resources
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
