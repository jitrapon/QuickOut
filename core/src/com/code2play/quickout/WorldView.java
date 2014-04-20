package com.code2play.quickout;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * WorldView renders all the entities that belong to a World. 
 * 
 * @author Jitrapon
 *
 */
public class WorldView {

	private static final int VIRTUAL_WIDTH = Gdx.graphics.getWidth();
	private static final int VIRTUAL_HEIGHT = Gdx.graphics.getHeight();

	private World world;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private BitmapFont font;

	private int deltaFrame = 0;
	private int currText = -1;
	private int ballCount = 0;

	public WorldView(World world) {
		this.world = world;

		// create the camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		batch = new SpriteBatch();
		font = new BitmapFont();
	}

	/** Called when the view should be rendered.
	 * 
	 * @param delta the time in seconds since the last render. 
	 * */
	public void render(float delta) {
		// clear the screen with a dark blue color. The
		// arguments to glClearColor are the red, green
		// blue and alpha component in the range [0,1]
		// of the color to be used to clear the screen.
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw 
		batch.begin();
		font.draw(batch, "Game started!", VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT);
		drawBalls();
		batch.end();

		// process user input
		if (Gdx.input.justTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			if (world.getBalls().size <= world.getMaxNumObject()) {
				ballCount++;
				Texture t = getNextTexture();
				Ball ball = new Ball(t, t.getHeight()/2, ballCount);
				ball.setWorld(world);
				ball.x = touchPos.x - t.getHeight()/2;
				ball.y = touchPos.y - t.getHeight()/2;
				world.addBall(ball);
			}
		}

		deltaFrame++;

	}
	
	public Texture getNextTexture() {
		currText++;
		if (currText >= Assets.textures.size) 
			currText = 0;
		return Assets.textures.get(currText);
	}
	
	public void drawBalls() {
		Iterator<Ball> iter = world.getBalls().iterator();
		while (iter.hasNext()) {
			Ball ball = iter.next();
			batch.draw(ball.texture, ball.x, ball.y);
		}
	}
	
	public void dispose() {
		batch.dispose();
		font.dispose();
	}
}
