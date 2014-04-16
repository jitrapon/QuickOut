package com.code2play.quickout;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements Screen {

	private GameMain game;
	private OrthographicCamera camera;
	private static final int VIRTUAL_WIDTH = Gdx.graphics.getWidth();
	private static final int VIRTUAL_HEIGHT = Gdx.graphics.getHeight();

	private int deltaFrame = 0;

	private Array<Rectangle> objList;

	private static final int MAX_NUM_OBJECT_ONSCREEN = 15;

	public GameScreen(GameMain g) {
		// game instance is the same one as the first created
		game = g;

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

		objList = new Array<Rectangle>(MAX_NUM_OBJECT_ONSCREEN);
	}

	@Override
	public void render(float delta) {
		// clear the screen with a dark blue color. The
		// arguments to glClearColor are the red, green
		// blue and alpha component in the range [0,1]
		// of the color to be used to clear the screen.
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		game.batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw 
		game.batch.begin();
		game.font.draw(game.batch, "Game started!", VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT);

		Iterator<Rectangle> iter1 = objList.iterator();
		Random random = new Random();
		while (iter1.hasNext()) {
			Rectangle obj = iter1.next();
			Texture t = Assets.textures.random();

			// move them!
			// make sure the objects stay within the screen bounds and bounce around
			obj.x += random.nextInt(100) * Gdx.graphics.getDeltaTime();
			obj.y += random.nextInt(100) * Gdx.graphics.getDeltaTime();

			// hit left
			if (obj.x < 0) {
				obj.x = 0;
			}

			// hit right
			if (obj.x > VIRTUAL_WIDTH - 150) {
				obj.x = VIRTUAL_WIDTH - 150;
			}

			// hit bottom
			if (obj.y < 0) {
				obj.y = 0;
			}

			// hit top
			if (obj.y > VIRTUAL_HEIGHT - 150) {
				obj.y = VIRTUAL_HEIGHT - 150;
			}

			game.batch.draw(t, obj.x, obj.y);
		}

		game.batch.end();

		// process user input
		if (Gdx.input.justTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			if (objList.size < MAX_NUM_OBJECT_ONSCREEN) {
				objList.add(new Rectangle(touchPos.x, touchPos.y, 
						Assets.textures.first().getWidth(), Assets.textures.first().getHeight()));
			}
		}

		deltaFrame++;
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

	}

}
