package com.code2play.quickout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;

public class GameScreen implements Screen {

	private GameMain game;
	private World world;
	private WorldView worldView;

	public GameScreen(GameMain g) {
		// game instance is the same one as the first created
		game = g;
		world = new World();
		worldView = new WorldView(world);
	}

	@Override
	/**
	 * Calls upon World instance to update its entities, then 
	 * renders them by WorldView
	 */
	public void render(float delta) {
		// If we're not paused then update the world and the subsystems.
		world.update(delta);
		
		// Clear the screen and draw the views.
		worldView.render(delta);
		
		// return to main menu 
		if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
			dispose();							// release resources before switching screen
			game.setScreen(game.mainMenuScreen);
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void hide() {
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// never called automatically
		worldView.dispose();
	}

}
