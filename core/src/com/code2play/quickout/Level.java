package com.code2play.quickout;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * Level contains the world that Entities live in.
 * 
 * All entities are associated with a level and can get access to the level object.
 * @author Jitrapon
 *
 */
public class Level {

	/* Reference to WorldView */
	private WorldView worldView;

	/* Box2D World constants */
	private World world;
	private CollisionListener bListener;
	private Vector2 gravity = new Vector2();
	private float timeStep = 1/45f;
	private int velocityIterations = 6;
	private int positionIterations = 2;

	public static final float WORLD_TO_BOX = 1/75f;		
	public static final float BOX_TO_WORLD = 75.0f;	

	/* This level's constants */
	private static final int MAX_NUM_OBJECT_ONSCREEN = 17;						// maximum number of spawnable objects onscreen at this level
	private static final int VIRTUAL_WIDTH = Gdx.graphics.getWidth();			// the screen width in world's coordinate
	private static final int VIRTUAL_HEIGHT = Gdx.graphics.getHeight();			// the screen height in world's coordinate
	private static final float MAX_SPEED = 20.0f;								// the maximum speed of any ball
	private static final float RESPAWN_TIME = 0.15f;							// time in seconds before the next respawn
	public boolean spawnMoreBalls = true;										// indicates whether to continue spawning more balls

	/* Some variables */
	private float time = 0.0f;													// keep tracks of current time in seconds (for next respawn)
	private Array<Ball> balls;													// contains the list of balls onscreen at this level
	private Body groundBody;													// used as anchor for mousejoint only

	/* Ball Type Constants */ 
	private int currText = -1;													// the current texture number so far
	public static final int BLUE = 0;
	public static final int GREEN = 1;
	public static final int RED = 2;
	public static final int YELLOW = 3;

	public enum EntityType {
		WALL((short)1), BALL((short)2), SPECIAL((short)4);

		private short categoryBits;

		EntityType(short categoryBits) {
			this.categoryBits = categoryBits;
		}

		public short getCategoryBits() {
			return categoryBits;
		}
	}

	/**
	 * Default ctor
	 */
	public Level() {
		// construct the world object. this object contains all physics objects/bodies and simulates
		// interactions between them. 
		world = new World(gravity, false);
		bListener = new CollisionListener();
		world.setContactListener(bListener);
		balls = new Array<Ball>(MAX_NUM_OBJECT_ONSCREEN);
	}

	/**
	 * Set the renderer after ctor
	 * @param viewRenderer
	 */
	public void setWorldRenderer(WorldView viewRenderer) {
		worldView = viewRenderer;
	}

	public WorldView getWorldRenderer() {
		return worldView;
	}

	public Array<Ball> getBalls() {
		return balls;
	}

	public int getMaxNumObject() {
		return MAX_NUM_OBJECT_ONSCREEN;
	}

	public int getMaxX() {
		return VIRTUAL_WIDTH;
	}

	public int getMaxY() {
		return VIRTUAL_HEIGHT;
	}

	public World getPhysicsWorld() {
		return world;
	}

	public Texture getNextTexture() {
		currText++;
		if (currText >= Assets.textures.size) 
			currText = BLUE;
		return Assets.textures.get(currText);
	}

	public Texture getTexture(int texture) {
		if (texture >= Assets.textures.size)  return null;
		else  return Assets.textures.get(texture);
	}

	/**
	 * Initialize physical bodies in this level
	 */
	public void init() {
		createGroundBody();
		createWallBoundary();

		//		float vel = 1.1f; // min
		float vel = 3.5f;
		Texture texture = getNextTexture();
		Ball b = spawnBall(texture, (float)(0.27*1*VIRTUAL_WIDTH), 
				(float)(0.5*VIRTUAL_HEIGHT), -1.0f, currText);
		b.setVelocity(new Vector2(vel, 0));

		texture = getNextTexture();
		b = spawnBall(texture, (float)(0.27*2*VIRTUAL_WIDTH), 
				(float)(0.5*VIRTUAL_HEIGHT), -1.0f, currText);
		b.setVelocity(new Vector2(0, 0));

		texture = getNextTexture();
		b = spawnBall(texture, (float)(0.27*3*VIRTUAL_WIDTH), 
				(float)(0.5*VIRTUAL_HEIGHT), -1.0f, currText);
		b.setVelocity(new Vector2(vel*-1, 0));

		texture = getNextTexture();
		b = spawnBall(texture, (float)(0.27*2*VIRTUAL_WIDTH), 
				(float)(0.75*VIRTUAL_HEIGHT), -1.0f, currText);
		b.setVelocity(new Vector2(0, vel*-1));

		texture = getNextTexture();
		b = spawnBall(texture, (float)(0.27*2*VIRTUAL_WIDTH), 
				(float)(0.25*VIRTUAL_HEIGHT), -1.0f, currText);
		b.setVelocity(new Vector2(0, vel));
	}

	/**
	 * Debug function
	 */
	public void debugInit() {
		createGroundBody();
		createWallBoundary();
	}

	/**
	 * Spawn a ball on a specified world coordinate
	 * @param texture The ball's texture
	 * @param posX	World's x coordinate
	 * @param posY World's y coordinate
	 * @param lifeTime This ball's lifetime in seconds before it disappears
	 */
	public Ball spawnBall(Texture t, float posX, float posY, float lifeTime, int tag) {
		Ball ball = new Ball(t, t.getHeight()/2f, tag);
		ball.setWorld(this);
		ball.attachPhysicsBody(EntityType.BALL.categoryBits, ball.radius, posX, posY, 1.0f, 1.0f, 1.0f, 1.0f);
		addBall(ball);
		return ball;
	}

	/**
	 * Spawn a ball on a random world coordinate within the camera
	 * @param t The ball's texture
	 * @param lifeTime This ball's lifetime in seconds before it disappears
	 * @return
	 */
	public Ball spawnBall(Texture t, float lifeTime, int tag) {
		Ball ball = new Ball(t, t.getHeight()/2f, tag);
		ball.setWorld(this);
		float posX = getRandomCoordinate(ball.radius, VIRTUAL_WIDTH-ball.radius);
		float posY = getRandomCoordinate(ball.radius, VIRTUAL_HEIGHT-ball.radius);
		ball.attachPhysicsBody(EntityType.BALL.categoryBits, ball.radius, posX, posY, 1.0f, 1.0f, 1.0f, 1.0f);
		addBall(ball);
		return ball;
	}

	/**
	 * Create a joint-anchor ground body for mousejoint events
	 */
	public void createGroundBody() {
		PolygonShape groundPoly = new PolygonShape();
		groundPoly.setAsBox(50, 1);

		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.type = BodyType.StaticBody;
		groundBodyDef.position.set(-1000 * WORLD_TO_BOX, -1000 * WORLD_TO_BOX);
		groundBody = world.createBody(groundBodyDef);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = groundPoly;
		fixtureDef.filter.groupIndex = 0;
		groundBody.createFixture(fixtureDef);
		groundPoly.dispose();
	}

	/**
	 * Create a boundary as well as boundary listener to remove entities that
	 * travel beyond the viewable area
	 */
	public void createWallBoundary() {
		float width = VIRTUAL_WIDTH * WORLD_TO_BOX;
		float height = VIRTUAL_HEIGHT * WORLD_TO_BOX;
		Vector2 lowerLeftCorner = new Vector2();
		Vector2 lowerRightCorner = new Vector2(width, 0);
		Vector2 upperLeftCorner = new Vector2(0, height);
		Vector2 upperRightCorner  = new Vector2(width, height);

		// static container body, with the collisions at screen borders
		BodyDef wallDef = new BodyDef();
		wallDef.position.set(0, 0);
		Body wallBody = world.createBody(wallDef);
		EdgeShape borderShape = new EdgeShape();
		FixtureDef fixtureDef = new FixtureDef(); 
//		FixtureDef fixtureDefSensor = new FixtureDef();

		// Create fixtures for the four borders (the border shape is re-used)
		borderShape.set(lowerLeftCorner, lowerRightCorner);
		fixtureDef.shape = borderShape;
		fixtureDef.filter.categoryBits = EntityType.WALL.categoryBits;
		fixtureDef.density = 0;
//		fixtureDefSensor.shape = borderShape;
//		fixtureDefSensor.filter.categoryBits = EntityType.BALL.categoryBits;			// this is a bit of a hack, we use BALL category so that
		// the ball still registers callback 
//		fixtureDefSensor.density = 0;
//		fixtureDefSensor.isSensor = true;
		wallBody.createFixture(fixtureDef);
//		wallBody.createFixture(fixtureDefSensor);

		borderShape.set(lowerRightCorner, upperRightCorner);
		fixtureDef.shape = borderShape;
//		fixtureDefSensor.shape = borderShape;
		wallBody.createFixture(fixtureDef);
//		wallBody.createFixture(fixtureDefSensor);

		borderShape.set(upperRightCorner, upperLeftCorner);
		fixtureDef.shape = borderShape;
//		fixtureDefSensor.shape = borderShape;
		wallBody.createFixture(fixtureDef);
//		wallBody.createFixture(fixtureDefSensor);

		borderShape.set(upperLeftCorner, lowerLeftCorner);
		fixtureDef.shape = borderShape;
//		fixtureDefSensor.shape = borderShape;
		wallBody.createFixture(fixtureDef);
//		wallBody.createFixture(fixtureDefSensor);

		borderShape.dispose();

		wallBody.setUserData("wall");				// TODO assign the boundary an instance of Entity
	}


	public Body getGroundBody() {
		return groundBody;
	}

	private void addBall(Ball b) {
		balls.add(b);
	}

	public float getWorldToBoxMultiplier() {
		return WORLD_TO_BOX;
	}

	public float getBoxToWorldMultiplier() {
		return BOX_TO_WORLD;
	}

	Random random = new Random();
	Texture texture = null;
	Ball b = null;
	/** Called when the World is to be updated.
	 * 
	 * @param delta the time in seconds since the last render. */
	public void update(float delta) {
		// step through the physics framework to calculate the next frame
		world.step(timeStep, velocityIterations, positionIterations);

		// update all the entities accordingly
		// remove balls that are taken away
		Iterator<Ball> iter = balls.iterator();
		while (iter.hasNext()) {
			Ball ball = iter.next();
			ball.update(delta);

			//TODO set mousejoint in WorldView to null if this ball 
			// is a dragged ball
			if (ball.state == Ball.DRAGGED && ball.hasCollidedCorrectly) {
				Gdx.app.log("DRAGGED COLLISION", "Dragged ball " + ball.getType() + " has collided correctly!");
			}

			// remove objects that are flagged as removed
			if (ball.removed) {
				iter.remove();
				time = 0.0f;					// reset spawn timer
				validateAction(ball);
			}
		}

		// spawn entities if current num is less than max value
		if (balls.size < MAX_NUM_OBJECT_ONSCREEN && spawnMoreBalls) {
			if (time > RESPAWN_TIME) {
				texture = getNextTexture();
				b = spawnBall(texture, -1.0f, currText);
				b.setVelocity( new Vector2( (random.nextFloat()*MAX_SPEED) - 5.0f, (random.nextFloat()*MAX_SPEED) - 5.0f) );
				time = 0.0f;					// reset spawn timer
			}
		}
		time += delta;
	}

	/**
	 * TODO
	 * Check to validate if the current move done to the ball fits the condition given
	 * Update the score after
	 * @param ball 
	 * @return true if the action is correct, false otherwise
	 */
	private boolean validateAction(Ball ball) {
		Gdx.app.log("ACTION", "Move type: " + ball.state + " on " + ball.getType());
		return true;
	}

	/**
	 * Call this method to return a random location within the screen
	 * Usually for spawning purposes
	 * @return
	 */
	private float getRandomCoordinate(float min, float max) {
		float rand = random.nextFloat() * (max - min) + min;
		return rand;
	}

	/**
	 * Listener callbacks for ball collisions
	 * @author Jitrapon
	 */
	class CollisionListener implements ContactListener {

		@Override
		public void beginContact(Contact contact) {
			// make sure that this collision is that of two balls
			if (contact.getFixtureA().getBody().getUserData() instanceof Ball 
					&& contact.getFixtureB().getBody().getUserData() instanceof Ball) {
				Ball ballA = (Ball) contact.getFixtureA().getBody().getUserData();
				Ball ballB = (Ball) contact.getFixtureB().getBody().getUserData();
				if (ballA.tag == RED && ballB.tag == RED) {
					ballA.hasCollidedCorrectly = true;
					ballB.hasCollidedCorrectly = true;
					Gdx.app.log("COLLISION", "Ball " + ballA.getType() + " is colliding with " + ballB.getType());
					//TODO set removed true
				}
				ballA.startContact();
				ballB.startContact();
			}
		}

		@Override
		public void endContact(Contact contact) {
			// make sure that this collision is that of two balls
			if (contact.getFixtureA() != null && contact.getFixtureB() != null) {
				if (contact.getFixtureA().getBody().getUserData().equals("wall") ||
						contact.getFixtureB().getBody().getUserData().equals("wall")) 
					return;
			}
			
			Ball ball = null;
			if (contact.getFixtureA() != null) {
				if (contact.getFixtureA().getBody().getUserData() instanceof Ball) {
					ball = (Ball) contact.getFixtureA().getBody().getUserData();
					ball.endContact();
				} 
			}
			
			if (contact.getFixtureB() != null) {
				if (contact.getFixtureB().getBody().getUserData() instanceof Ball) {
					ball = (Ball) contact.getFixtureB().getBody().getUserData();
					ball.endContact();
				} 
			}
		}

		@Override
		public void preSolve(Contact contact, Manifold oldManifold) {
		}

		@Override
		public void postSolve(Contact contact, ContactImpulse impulse) {
		}

	};
}
