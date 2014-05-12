package com.code2play.quickout;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
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

	private World world;
	private Vector2 gravity = new Vector2();
	private float timeStep = 1/45f;
	private int velocityIterations = 6;
	private int positionIterations = 2;

	private static final int MAX_NUM_OBJECT_ONSCREEN = 15;
	private static final int VIRTUAL_WIDTH = Gdx.graphics.getWidth();
	private static final int VIRTUAL_HEIGHT = Gdx.graphics.getHeight();

	private Array<Ball> balls;

	private Body groundBody;	// used as anchor for mousejoint only

	Vector2 lastVel = new Vector2();
	
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
	
	public static final float WORLD_TO_BOX = 1/75f;		
	public static final float BOX_TO_WORLD = 75.0f;	

	public Level() {
		// construct the world object. this object contains all physics objects/bodies and simulates
		// interactions between them. 
		world = new World(gravity, true);

		balls = new Array<Ball>(MAX_NUM_OBJECT_ONSCREEN);
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

	/**
	 * Spawn a ball on a specified world coordinate
	 * @param texture The ball's texture
	 * @param posX	World's x coordinate
	 * @param posY World's y coordinate
	 * @param lifeTime This ball's lifetime in seconds before it disappears
	 */
	public Ball spawnBall(Texture t, float posX, float posY, float lifeTime) {
		Ball ball = new Ball(t, t.getHeight()/2f, balls.size + 1);
		ball.setWorld(this);
		ball.attachPhysicsBody(EntityType.BALL.categoryBits, ball.radius, posX, posY, 1.0f, 1.0f, 1.0f, 1.0f);
		addBall(ball);
		return ball;
	}

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
		
		// Create fixtures for the four borders (the border shape is re-used)
		borderShape.set(lowerLeftCorner, lowerRightCorner);
		fixtureDef.shape = borderShape;
		fixtureDef.filter.categoryBits = EntityType.WALL.categoryBits;
		fixtureDef.density = 0;
		wallBody.createFixture(fixtureDef);
		
		borderShape.set(lowerRightCorner, upperRightCorner);
		fixtureDef.shape = borderShape;
		wallBody.createFixture(fixtureDef);
		
		borderShape.set(upperRightCorner, upperLeftCorner);
		fixtureDef.shape = borderShape;
		wallBody.createFixture(fixtureDef);
		
		borderShape.set(upperLeftCorner, lowerLeftCorner);
		fixtureDef.shape = borderShape;
		wallBody.createFixture(fixtureDef);
		
		borderShape.dispose();
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

	/** Called when the World is to be updated.
	 * 
	 * @param delta the time in seconds since the last render. */
	public void update(float delta) {
		// check collisions for all balls
		//		checkBallsCollision();

		world.step(timeStep, velocityIterations, positionIterations);

		Iterator<Ball> iter = balls.iterator();
		while (iter.hasNext()) {
			Ball ball = iter.next();
			ball.update(delta);
		}
	}

	/**
	 * @deprecated
	 */
	private void checkBallsCollision() {

		//TODO: figure out the balls that are simultaneously colliding
		for (int i = 0; i < balls.size; i++) {
			for (int j = i+1; j < balls.size; j++) {
				balls.get(i).intersects(balls.get(j));
			}
		}


		for (int i = 0; i < balls.size; i++) {
			for (int j = i+1; j < balls.size; j++) {
				//TODO change to ball.collideWith(otherBall)
				if ( balls.get(i).intersects(balls.get(j)) ) {
					lastVel.x = balls.get(i).velocity.x;
					lastVel.y = balls.get(i).velocity.y;
					Gdx.app.log("velocity", "Ball " + i + " previous velocity x: " + balls.get(i).velocity.x);
					Gdx.app.log("velocity", "Ball " + j + " previous velocity x: " + balls.get(j).velocity.x);
					balls.get(i).resolveCollision(balls.get(j));
					Gdx.app.log("velocity", "Ball " + i + " new velocity x: " + balls.get(i).velocity.x);
					Gdx.app.log("velocity", "Ball " + j + " new velocity x: " + balls.get(j).velocity.x);
				}

				// if this ball is already in collision with other balls, we accumulate the velocity changes
				if ( balls.get(i).collisionCount > 1 ) {
					//					balls.get(i).setVelocity( lastVel.add(balls.get(i).velocity) );
					Gdx.app.log("velocity", "Ball " + i + " accumulated velocity so far: " + balls.get(i).velocity.x);
				}
			}
		}

		//		balls.get(1).setVelocity(new Vector2());
		//		Gdx.app.log("velocity", "ball 0 velocity x: " + balls.get(0).velocity.x);

		for (int i = 0; i < balls.size; i++) {
			Gdx.app.log("velocity", "Ball " + i + " final velocity: " + balls.get(i).velocity.x);
			balls.get(i).collisionCount = 0;
			balls.get(i).inCollision = false;
		}
	}
}
