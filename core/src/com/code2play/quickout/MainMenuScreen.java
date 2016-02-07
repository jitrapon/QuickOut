package com.code2play.quickout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.code2play.quickout.GameMain.GameMode;

public class MainMenuScreen implements Screen {
	
	private GameMain game;
	
	OrthographicCamera camera;
	public SpriteBatch batch;
	public BitmapFont font;
	private static final int VIRTUAL_WIDTH = 900;
	private static final int VIRTUAL_HEIGHT = 1600;
	
	private int deltaFrame = 0;
	
	public MainMenuScreen(final GameMain game) {
		this.game = game;
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.getData().setScale(2);
		
		camera = new OrthographicCamera();
        camera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        font.draw(batch, "Welcome to my first Android game!", 40, VIRTUAL_HEIGHT/2);
        if (deltaFrame > 75) {
        	font.draw(batch, "Tap anywhere to begin", 40, VIRTUAL_HEIGHT/2 - 100);
        	if (deltaFrame > 150) 
        		deltaFrame = 0;
        }
        batch.end();

        // TODO SELECT GAME MODE
        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game, GameMode.NORMAL));
        }
        
        deltaFrame += 1;
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		batch.dispose();
		font.dispose();
	}

}
