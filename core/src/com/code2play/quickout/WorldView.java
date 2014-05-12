package com.code2play.quickout;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;

/**
 * WorldView renders all the entities that belong to a World. 
 * 
 * @author Jitrapon
 *
 */
public class WorldView implements GestureListener {

	private static final int VIRTUAL_WIDTH = Gdx.graphics.getWidth();
	private static final int VIRTUAL_HEIGHT = Gdx.graphics.getHeight();

	private Level level;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private BitmapFont font;

	private Ball draggedBall;					// specifies which ball is currently being dragged

	private int currText = -1;

	GestureDetector gestureDetector;

	/** our mouse joint **/
	protected MouseJoint mouseJoint = null;

	public WorldView(Level level) {
		this.level = level;

		// create the camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		batch = new SpriteBatch();
		font = new BitmapFont();

		//		spawnSimpleGridLevel();
		spawnSimultaneousCollisionTest();

		gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, this);
		Gdx.input.setInputProcessor(gestureDetector);
	}

	public void spawnSimultaneousCollisionTest() {
		level.createGroundBody();
		level.createWallBoundary();
		
		//		float vel = 1.1f; // min
		float vel = 3.5f;
		Texture texture = getNextTexture();
		Ball b = level.spawnBall(texture, (float)(0.27*1*VIRTUAL_WIDTH), 
				(float)(0.5*VIRTUAL_HEIGHT), -1.0f);
		b.setVelocity(new Vector2(vel, 0));

		texture = getNextTexture();
		b = level.spawnBall(texture, (float)(0.27*2*VIRTUAL_WIDTH), 
				(float)(0.5*VIRTUAL_HEIGHT), -1.0f);
		b.setVelocity(new Vector2(0, 0));

		texture = getNextTexture();
		b = level.spawnBall(texture, (float)(0.27*3*VIRTUAL_WIDTH), 
				(float)(0.5*VIRTUAL_HEIGHT), -1.0f);
		b.setVelocity(new Vector2(vel*-1, 0));

		texture = getNextTexture();
		b = level.spawnBall(texture, (float)(0.27*2*VIRTUAL_WIDTH), 
				(float)(0.75*VIRTUAL_HEIGHT), -1.0f);
		b.setVelocity(new Vector2(0, vel*-1));

		texture = getNextTexture();
		b = level.spawnBall(texture, (float)(0.27*2*VIRTUAL_WIDTH), 
				(float)(0.25*VIRTUAL_HEIGHT), -1.0f);
		b.setVelocity(new Vector2(0, vel));
	}

	public void spawnSimpleGridLevel() {
		// for now: current level, spawn balls in grid to limit
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 3; j++) {
				Texture texture = getNextTexture();
				level.spawnBall(texture, (float)(0.27*j*VIRTUAL_WIDTH), 
						(float)(0.15*i*VIRTUAL_HEIGHT), -1.0f);
			}
		}
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
		// player has tapped the screen
		if (Gdx.input.justTouched()) {
		}
	}

	public Texture getNextTexture() {
		currText++;
		if (currText >= Assets.textures.size) 
			currText = 0;
		return Assets.textures.get(currText);
	}

	public void drawBalls() {
		Iterator<Ball> iter = level.getBalls().iterator();
		while (iter.hasNext()) {
			Ball ball = iter.next();
			batch.draw(ball.texture, ball.x - ball.radius, ball.y - ball.radius, ball.radius*2, ball.radius*2);
		}
	}

	public void dispose() {
		batch.dispose();
		font.dispose();
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		for (Ball b : level.getBalls()) {
			Vector2 ballPos = new Vector2(b.x, b.y);
			Vector3 touchPos = new Vector3(x, y, 0);
			camera.unproject(touchPos);
			if (b.bounds().radius >= Math.abs(ballPos.dst(new Vector2(touchPos.x, touchPos.y))))  {
				Gdx.app.log("tap", "ball tag: " + b.tag);
				b.setState(Ball.TAPPED);
			}
		}
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		Gdx.app.log("longPress", x + ", " + y);
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		if (draggedBall != null) {
			draggedBall.setState(Ball.FLINGED);
		}
		draggedBall = null;
		return false;
	}

	Vector3 touchPos = new Vector3();

	/** another temporary vector **/
	Vector2 target = new Vector2();
	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		//		Gdx.app.log("pan", x + ", " + y + ", delta(" + deltaX + ", " + deltaY + ")");
		touchPos.set(x, y, 0);
		camera.unproject(touchPos);

		// if we haven't started dragging yet
		if (mouseJoint == null) {
			for (Ball b : level.getBalls()) {
				Vector2 ballPos = new Vector2(b.x, b.y);
				if (b.bounds().radius >= Math.abs(ballPos.dst(new Vector2(touchPos.x, touchPos.y)))) {
					b.setState(Ball.DRAGGED);
					//				b.moveTo(touchPos.x, touchPos.y);

					// init mousejoint
					MouseJointDef mJointDef = new MouseJointDef();
					mJointDef.bodyA = level.getGroundBody();
					mJointDef.bodyB = b.getBody();
					mJointDef.dampingRatio = 0.0f;
//					mJointDef.frequencyHz = 0.2f;
					mJointDef.collideConnected = true;
					mJointDef.target.set(touchPos.x * level.getWorldToBoxMultiplier(), touchPos.y * level.getWorldToBoxMultiplier());
					mJointDef.maxForce = 500.0f * b.getBody().getMass();

					mouseJoint = (MouseJoint)level.getPhysicsWorld().createJoint(mJointDef);
					b.getBody().setAwake(true);

					draggedBall = b;
				}
			}
//			Gdx.app.log("Drag", "Drag position is " + touchPos.x + ", " + touchPos.y);
		}

		// if a mouse joint exists we simply update
		// the target of the joint based on the new
		// mouse coordinates
		else {
			mouseJoint.setTarget(target.set(touchPos.x * level.getWorldToBoxMultiplier(), 
					touchPos.y * level.getWorldToBoxMultiplier()));
//			Gdx.app.log("Drag", "Drag position is " + touchPos.x + ", " + touchPos.y);
		}
		
		
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		if (draggedBall != null) {
			draggedBall.setState(Ball.INACTIVE);
		}

		// if a mouse joint exists we simply destroy it
		if (mouseJoint != null) {
			level.getPhysicsWorld().destroyJoint(mouseJoint);
			mouseJoint = null;
		}
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}

}
