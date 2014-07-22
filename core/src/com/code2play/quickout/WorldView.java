package com.code2play.quickout;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * WorldView renders all the entities that belong to a World. 
 * This renders all the scene objects as well as HUDs and various dialogs
 * 
 * @author Jitrapon Tiachunpun
 *
 */
public class WorldView implements GestureListener {

	private Level level;
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private FPSLogger fpsLogger;

	public Ball draggedBall;					// specifies which ball is currently being dragged

	/** Gesture detector **/
	GestureDetector gestureDetector;
	private float longPressDuration = 1.0f;

	/** our mouse joint **/
	protected MouseJoint mouseJoint = null;
	
	/** Game HUD, the game hud is an abstract representation of the Scene2D stage **/
	private GameHud gameHud;	
	private static final int HUD_WIDTH = 576;
	private static final int HUD_HEIGHT = 1024;
	private static final int MAX_HUD_WIDTH = 768;
	private static final int MAX_HUD_HEIGHT = 1024;
	
	public static final float LEVEL_TO_HUD_RATIO = (float)Level.VIRTUAL_HEIGHT / HUD_HEIGHT;
	
	/** Level Background Sprites **/
	private Sprite gameBackground;
	private Sprite hillBackground;

	public WorldView(Level level) {
		// debug: log fps in console
		fpsLogger = new FPSLogger();
		this.level = level;

		// create the camera with the coordinate specified
		// initialize the drawing batch as well
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Level.VIRTUAL_WIDTH, Level.VIRTUAL_HEIGHT);
		viewport = new ExtendViewport(Level.VIRTUAL_WIDTH, Level.VIRTUAL_HEIGHT, 
				Level.MAX_VIRTUAL_WIDTH, Level.MAX_VIRTUAL_HEIGHT, camera);
		batch = new SpriteBatch();

		// set up background rendering
		gameBackground = new Sprite(Assets.getLevelBackground());
		gameBackground.setSize(Level.VIRTUAL_WIDTH, Level.VIRTUAL_HEIGHT);
		hillBackground = new Sprite(Assets.getTextureRegion(Assets.LEVEL_BACKGROUND_HILL));
		TextureRegion hudGround = Assets.getTextureRegion(Assets.LEVEL_HUD_GROUND);
		hillBackground.setScale(LEVEL_TO_HUD_RATIO);
		hillBackground.setPosition(162, LEVEL_TO_HUD_RATIO * hudGround.getRegionHeight());
		
		// initialize HUD
		gameHud = new GameHud(level, HUD_WIDTH, HUD_HEIGHT, 
				MAX_HUD_WIDTH, MAX_HUD_HEIGHT);

		// set up input listener
		// multiplexer is used for handling HUD overlain the game
		InputMultiplexer inMultiplexer = new InputMultiplexer();
		gestureDetector = new GestureDetector(20, 0.5f, longPressDuration, 0.15f, this);
		inMultiplexer.addProcessor(gestureDetector);
		inMultiplexer.addProcessor(gameHud.getStage());
		Gdx.input.setInputProcessor(inMultiplexer);
		
		// initialize level contents
		//		level.debugInit();
		level.init();
	}
	
	public float getLevelToHUDRatio() {
		return (float)Level.VIRTUAL_HEIGHT / HUD_HEIGHT;
	}
	
	public GameHud getGameHUD() {
		return gameHud;
	}

	/**
	 * Resize the viewport to a specified resolution
	 * Used by the GameScreen to resize current viewport
	 * @param width
	 * @param height
	 */
	public void resize(int width, int height) {
		viewport.update(width, height);
		gameHud.resize(width, height);
	}

	/** Called when the view should be rendered.
	 * @param delta the time in seconds since the last render. 
	 * */
	public void render(float delta) {
		// debug fps log
				fpsLogger.log();

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
		/********************************
		 * BEGIN GAME ENTITIES DRAWING HERE
		 *******************************/
		// draw background
		batch.disableBlending();
		gameBackground.draw(batch);
		batch.enableBlending();
		
		hillBackground.draw(batch);
		
		// draw balls
		drawBalls();
		
		/********************************
		 * END GAME ENTITIES DRAWING
		 *******************************/
		batch.end();
		
		// draw game HUD
		gameHud.draw(delta);

		// process user input
		// player has tapped the screen
		if (Gdx.input.justTouched()) {
		}

		// for gravity-enabled levels
		if (level.gravityEnabled) {
			if (Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer) == true) { 
				processAccelerometer();
			}
		}
	}

	float prevAccelX = 0.0f;
	float prevAccelY = 0.0f;
	/**
	 * Process accelerometer values and apply gravity accordingly
	 */
	private void processAccelerometer() {
		float y = Gdx.input.getAccelerometerY();
		float x = Gdx.input.getAccelerometerX();
		if ((prevAccelX != x) || prevAccelY != y) {
			level.getPhysicsWorld().setGravity( new Vector2(-x, -y) );//Negative on the x, but not on the Y. Somewhat geocentric view.
			prevAccelX = x;
			prevAccelY = y;
		}
	}

	/**
	 * Draw all the balls onscreen
	 */
	public void drawBalls() {
		Iterator<Ball> iter = level.getBalls().iterator();
		while (iter.hasNext()) {
			Ball ball = iter.next();
			batch.draw(ball.getCurrentAnimation().getKeyFrame(ball.stateTime), 
					ball.x - ball.radius, ball.y - ball.radius, ball.radius*2, ball.radius*2);
		}
	}

	public void dispose() {
		batch.dispose();
		gameHud.dispose();
		Gdx.app.log("DISPOSING", "Released worldview resources");
	}

	Vector3 touchPos = new Vector3();
	Vector2 ballPos = new Vector2();

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		//		Gdx.app.log("Tap", x + ", " + y);
		for (Ball b : level.getBalls()) {
			ballPos.set(b.x, b.y);
			touchPos.set(x, y, 0);
			camera.unproject(touchPos);
			if (b.radius >= Math.abs(ballPos.dst(new Vector2(touchPos.x, touchPos.y))))  {
				b.setState(Ball.TAPPED);
			}
		}
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		//		Gdx.app.log("Long Press", x + ", " + y);
		for (Ball b : level.getBalls()) {
			ballPos.set(b.x, b.y);
			touchPos.set(x, y, 0);
			camera.unproject(touchPos);
			if (b.radius >= Math.abs(ballPos.dst(new Vector2(touchPos.x, touchPos.y))))  {
				b.setState(Ball.LONG_TAPPED);
			}
		}
		return true;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		//		Gdx.app.log("FLING", "Flinging with velocity " + velocityX + " " + velocityY);
		return false;
	}

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
		//		Gdx.app.log("PAN STOP", "Panning stop at location " + x + " " + y);
		//TODO check velocity
		if (draggedBall != null) {
			draggedBall.setState(Ball.FLINGED);
			// if too slow -> set state to Ball.INACTIVE
			draggedBall = null;
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
