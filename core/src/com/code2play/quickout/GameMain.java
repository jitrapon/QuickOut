package com.code2play.quickout;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

/**
 * Main application class
 * @author Jitrapon Tiachunpun
 * 
 */
public class GameMain extends ApplicationAdapter {

	SpriteBatch batch;

	public Array<Texture> textures;
	private static final int DEFAULT_TEXTURE_LOAD_SIZE = 10;
	
	Texture test;
	int frameTime;

	@Override
	public void create () {
		batch = new SpriteBatch();
		loadTextures();
		test = textures.random();
	}
	
	public void loadTextures() {
		textures = new Array<Texture>(DEFAULT_TEXTURE_LOAD_SIZE);
		textures.add( new Texture("badlogic.jpg") ); 
		textures.add( new Texture("balls/blue.png") );
		textures.add( new Texture("balls/green.png") );
		textures.add( new Texture("balls/red.png") );
		textures.add( new Texture("balls/yellow.png") );
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		if (frameTime > 50) {
			test = textures.random();
			frameTime = 0;
		}
		
		batch.draw(test, 100, 120);
		batch.end();
		
		frameTime++;
	}

	@Override
	public void dispose() {
		// dispose of all the native resources
		batch.dispose();
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
