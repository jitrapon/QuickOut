package com.code2play.quickout;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
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
import com.code2play.game.IGameManager;

/**
 * Level contains the world that Entities live in.
 * 
 * All entities are associated with a level and can get access to the level object.
 * @author Jitrapon
 *
 */
public class Level implements IGameManager {

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
	public boolean gravityEnabled = false;
	private static final float BALL_RADIUS = 75.0f;
	public static final float ITEM_RADIUS = 50.0f;
	public static final int MAX_NUM_OBJECT_ONSCREEN = 17;						// maximum number of spawnable objects onscreen at this level
	public static final int VIRTUAL_WIDTH = 900;								// the screen width in world's coordinate
	public static final int VIRTUAL_HEIGHT = 1600;								// the screen height in world's coordinate
	public static final int MAX_VIRTUAL_WIDTH = 1200;
	public static final int MAX_VIRTUAL_HEIGHT = 1600;
	private static final float MAX_SPEED = 20.0f;								// the maximum speed of any ball
	public static final float MAX_LEVEL_TIME = 15.0f;							// duration of this level in seconds
	private static final float RESPAWN_TIME = 0.25f;							// time in seconds before the next respawn
	private static final float MOVE_CHANGE_TIME = 6.5f;							// if used, indicates the time in seconds before the next moveset is changed
	public boolean spawnMoreBalls = true;										// indicates whether to continue spawning more balls
	private static final int MAX_NUM_ITEMS = 3;									// maximum number of item slots

	/* Some variables */
	private float time = 0;														// current time elapsed since the start of the level
	private int score = 0;														// current level's score
	private int comboScore = 0;													// current level's combo score
	private int ballCount = 0;													// current level's ball collected!!!
	private float spawnTime = 0.0f;												// keep tracks of current time in seconds (for next respawn)
	private Array<Ball> balls;													// contains the list of balls onscreen at this level
	private Array<ScoreIndicator> ballPoints;									// contains the list of points worth of all balls 
																				// to be used for rendering
	private ItemSlot itemSlot;													// storing items
	private Array<Item> items;
	private Body groundBody;													// used as anchor for mousejoint only

	/* Ball Type Constants */ 
	private int currBallType = -1;												// the current type of the ball that will spawned
	private MoveSet moveSet;
	public static final int BLUE = 0;
	public static final int GREEN = 1;
	public static final int RED = 2;
	public static final int YELLOW = 3;
	public static final int ANY = 4;
	
	/* Ground height */
	public static final float GROUND_HEIGHT = 150.0f;

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
	
	/** Score indicator on every points taken used in the game HUD **/
	class ScoreIndicator {
		float posX;
		float posY;
		int score;
		boolean isPenalty;
		
		public ScoreIndicator(float posX, float posY, int score) {
			this.posX = posX;
			this.posY = posY;
			this.score = score;
			this.isPenalty = false;
		}
		
		public ScoreIndicator(float posX, float posY, int score, boolean isPenalty) {
			this(posX, posY, score);
			this.isPenalty = true;
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
		items = new Array<Item>();
		ballPoints = new Array<ScoreIndicator>(MAX_NUM_OBJECT_ONSCREEN);
		itemSlot = new ItemSlot(MAX_NUM_ITEMS);
	}
	
	public int getScore() {
		return score;
	}
	
	public int getBallCount() {
		return ballCount;
	}
	
	public ItemSlot getItemSlot() {
		return itemSlot;
	}

	/**
	 * Set the renderer after ctor
	 * @param viewRenderer
	 */
	@Override
	public void setWorldRenderer(WorldView viewRenderer) {
		worldView = viewRenderer;
	}

	@Override
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
	
	public int getMinX() {
		return 0;
	}
	
	public int getMinY() {
		return (int) GROUND_HEIGHT;
	}

	@Override
	public World getPhysicsWorld() {
		return world;
	}

	/**
	 * Returns the next animation set after the previous set.
	 * The ordering is specified by the Assets class. 
	 * @return
	 */
	public Array<Animation> getNextAnimationSet() {
		currBallType++;
		if (currBallType >= Assets.animationList.size) 
			currBallType = 0;
		return Assets.animationList.get(currBallType);
	}

	/**
	 * Returns the specified animation set.
	 * The ordering is specified by the Assets class.
	 * @param index
	 * @return
	 */
	public Array<Animation> getAnimationSet(int index) {
		if (index >= Assets.animationList.size)  return null;
		else  {
			currBallType = index;
			return Assets.animationList.get(index);
		}
	}

	
	/**
	 * Initialize all physical bodies in this level
	 */
	@Override
	public void init() {
		createGroundBody();
		createWallBoundary();

		//		float vel = 1.1f; // min
//		float vel = 3.5f;
//		Array<Animation> anim = getNextAnimationSet();
//		Ball b = spawnBall(anim, (float)(0.27*1*VIRTUAL_WIDTH), 
//				(float)(0.5*VIRTUAL_HEIGHT), -1.0f, currBallType);
//		b.setVelocity(new Vector2(vel, 0));
//
//		anim = getNextAnimationSet();
//		b = spawnBall(anim, (float)(0.27*2*VIRTUAL_WIDTH), 
//				(float)(0.5*VIRTUAL_HEIGHT), -1.0f, currBallType);
//		b.setVelocity(new Vector2(0, 0));
//
//		anim = getNextAnimationSet();
//		b = spawnBall(anim, (float)(0.27*3*VIRTUAL_WIDTH), 
//				(float)(0.5*VIRTUAL_HEIGHT), -1.0f, currBallType);
//		b.setVelocity(new Vector2(vel*-1, 0));
//
//		anim = getNextAnimationSet();
//		b = spawnBall(anim, (float)(0.27*2*VIRTUAL_WIDTH), 
//				(float)(0.75*VIRTUAL_HEIGHT), -1.0f, currBallType);
//		b.setVelocity(new Vector2(0, vel*-1));
//
//		anim = getNextAnimationSet();
//		b = spawnBall(anim, (float)(0.27*2*VIRTUAL_WIDTH), 
//				(float)(0.25*VIRTUAL_HEIGHT), -1.0f, currBallType);
//		b.setVelocity(new Vector2(0, vel));
		
		moveSet = new MoveSet();
		moveSet.setMoveset(true);
	}
	
	public MoveSet getMoveSet() {
		return moveSet;
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
	public Ball spawnBall(Array<Animation> animList, float posX, float posY, float lifeTime, int tag) {
		Ball ball = new Ball(animList, BALL_RADIUS, tag);
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
	public Ball spawnBall(Array<Animation> animList, float lifeTime, int tag) {
		Ball ball = new Ball(animList, BALL_RADIUS, tag);
		ball.setWorld(this);
		float posX = getRandomCoordinate(ball.radius, VIRTUAL_WIDTH-ball.radius);
		float posY = getRandomCoordinate(ball.radius + GROUND_HEIGHT, VIRTUAL_HEIGHT-ball.radius);
		ball.attachPhysicsBody(EntityType.BALL.categoryBits, ball.radius, posX, posY, 1.0f, 1.0f, 1.0f, 1.0f);
		addBall(ball);
		return ball;
	}
	
	public Item spawnItem(Animation animation, int tag, float maxDuration, float lifeTime) {
		//TODO item factory class
		Item item = new Item(animation, ITEM_RADIUS, tag, maxDuration, lifeTime);
		item.setWorld(this);
		float posX = getRandomCoordinate(item.radius, VIRTUAL_WIDTH-item.radius);
//		float posY = getRandomCoordinate(item.radius + GROUND_HEIGHT, VIRTUAL_HEIGHT-item.radius);
		float posY = VIRTUAL_HEIGHT;
		item.attachPhysicsBody(EntityType.SPECIAL.categoryBits, posX, posY, 1.0f, 1.0f, 1.0f, 1.0f);
		addItem(item);
		return item;
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
		float posY = getRandomCoordinate(ball.radius + GROUND_HEIGHT, VIRTUAL_HEIGHT-ball.radius);
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
		Vector2 lowerLeftCorner = new Vector2(0, GROUND_HEIGHT * WORLD_TO_BOX);
		Vector2 lowerRightCorner = new Vector2(width, GROUND_HEIGHT * WORLD_TO_BOX);
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
	
	private void addItem(Item item) {
		items.add(item);
	}

	public float getWorldToBoxMultiplier() {
		return WORLD_TO_BOX;
	}

	public float getBoxToWorldMultiplier() {
		return BOX_TO_WORLD;
	}
	
	public Array<ScoreIndicator> getScoreIndicators() {
		return ballPoints;
	}
	
	public Array<Item> getUnslottedItems() {
		return items;
	}

	Random random = new Random();
	Texture texture = null;
	Ball b = null;
	Array<Animation> anim = null;
	float moveChangeTimer = 0.0f;
	int numBall = 0;
	int scoreAdder = 0;
	float comboTimer = 0f;
	boolean hasNotSpawnedItem = false;
	Array<Ball> collidedBalls = new Array<Ball>();
	
	int itemSize = -1;
	
	/** Called when the World is to be updated.
	 * @param delta the time in seconds since the last render. */
	@Override
	public void update(float delta) {
		// step through the physics framework to calculate the next frame
		world.step(timeStep, velocityIterations, positionIterations);

		// update all the entities accordingly
		// remove balls that are taken away
		Iterator<Ball> iter = balls.iterator();
		numBall = balls.size;
		while (iter.hasNext()) {
			Ball ball = iter.next();
			ball.update(delta);
			if (ball.collidedWithBall != null) 
				collidedBalls.add(ball.collidedWithBall);

			// remove objects that are flagged as removed
			if (ball.removed) {
				numBall--;
//				iter.remove();													//TODO this may have to be moved to WorldView.render()
				if (spawnTime > RESPAWN_TIME) spawnTime = 0.0f;					// reset spawn timer
				
				scoreAdder = getScoreAdderFromTimeLapsed(moveChangeTimer);

				// no need to validate action on collision hits
				if (!ball.hasCollidedCorrectly) {
					
					// correct move!
					if (validateAction(ball)) {
						score += scoreAdder * 1.5;
						ballCount+=1;											// use 1 because it is a variable!
						ball.correctMove = true;
						ballPoints.add(new ScoreIndicator(ball.x, ball.y, (int) (scoreAdder*1.5)));
						comboScore+=1;
						hasNotSpawnedItem = true;
					}
					
					// wrong move!
					// penalize the player
					else {
						ball.correctMove = false;
						ballCount = ballCount-3 < 0? 0 : ballCount-3;
						ballPoints.add(new ScoreIndicator(ball.x, ball.y, -3, true));
						comboScore = 0;
					}
				}
				
				// on correct collision
				else {
					score += scoreAdder * 2;
					ballCount+=1;												// use 1 because it is a variable!
					ballPoints.add(new ScoreIndicator(ball.x, ball.y, scoreAdder*2));
					comboScore+=1;
					hasNotSpawnedItem = true;
					
					if (collidedBalls.contains(ball, true)) {
//						//TODO spawn new ball of the indicated color
//						anim = getAnimationSet(moveSet.getMoves().first().ballType);
//						b = spawnBall(anim, ball.x, ball.y, -1.0f, currBallType);
//						spawnTime = 0.0f;
//						numBall++;
					}
				}
			}
		}
		collidedBalls.clear();

		// spawn entities if current num is less than max value
		if (numBall < MAX_NUM_OBJECT_ONSCREEN && spawnMoreBalls) {
			if (spawnTime > RESPAWN_TIME) {
				anim = getNextAnimationSet();
				b = spawnBall(anim, -1.0f, currBallType);
				b.setVelocity( new Vector2( (random.nextFloat()*MAX_SPEED) - 5.0f, (random.nextFloat()*MAX_SPEED) - 5.0f) );
				spawnTime = 0.0f;					// reset spawn timer
			}
		}

		//TODO set current level's objective if the timer is up
		// SET LEVEL's current ball indicator here
		if (
				moveChangeTimer > MOVE_CHANGE_TIME 
				|| moveSet.isCorrect()
				) {
			if (moveChangeTimer > MOVE_CHANGE_TIME) {
				ballCount = ballCount-3 < 0? 0 : ballCount-3;
				comboScore = 0;
			}
			moveSet.setMoveset(true);
			moveChangeTimer = 0.0f;
		}
		
		// Update items and their effects
		Iterator<Item> itemIter = items.iterator();
		while (itemIter.hasNext()) {
			Item item = itemIter.next();
			item.update(delta);
			
			// remove objects that are flagged as removed
			if (item.removed) {
			}
		}
		
		if (items.size != itemSize) {
			System.out.println(items.size);
			itemSize = items.size;
		}
		
		//TODO Spawn new items based on combo
		if (comboTimer > 3.5f) {
			comboScore = 0;
			comboTimer = 0.0f;
		}
		else {
			if (comboScore > 0 && comboScore % 5 == 0 && hasNotSpawnedItem) {
				spawnItem(Assets.itemAnimationList.random(), 0, 3f, 5f);
				hasNotSpawnedItem = false;
			}
		}

		// update respawn timer
		spawnTime += delta;
		moveChangeTimer += delta;
		time += delta;
		comboTimer += delta;
	}
	
	/**
	 * Get the elapsed time in seconds since the start of the level
	 * @return the elapsed time in sec
	 */
	public float getElapsedTime() {
		return time;
	}
	
	/**
	 * Function to calculate score after time lapsed
	 * 100 (0 - 0.2 sec) 
	 * 10 (5.0 sec)
	 * @return  max value is 100, min is 10
	 */
	private int getScoreAdderFromTimeLapsed(float delta) {
		int minScore = 10;
		int maxScore = 100;
		float minDelta = 0.2f;
		float maxDelta = 5.0f;
		
		if (delta < minDelta) {
			return maxScore;
		}
		
		float deltaIncr = (maxDelta-minDelta) / (maxScore-minScore);
		float score = maxScore - ((delta-minDelta) / deltaIncr);
		return score < minScore ? minScore : (int)score;
	}

	/**
	 * TODO
	 * Check to validate if the current move done to the ball fits the condition given
	 * Update the score a
	 * @param ball 
	 * @return true if the action is correct, false otherwise
	 */
	private boolean validateAction(Ball ball) {
//		Gdx.app.log("ACTION", "Move type: " + ball.state + " on " + ball.getType());
		return moveSet.validate(ball.tag, ball.state);
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
				if (ballA.tag == moveSet.getMoves().first().ballType &&
						ballB.tag == moveSet.getMoves().first().ballType) {
//				if (ballA.tag == ballB.tag) {
					ballA.hasCollidedCorrectly = true;
					ballA.collidedWithBall = ballB;
					ballB.hasCollidedCorrectly = true;
					ballB.collidedWithBall = ballA;
//					Gdx.app.log("COLLISION", "Ball " + ballA.getType() + " is colliding with " + ballB.getType());
					//TODO increase the score
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

	}
	
	/**
	 * Release all resources
	 */
	private void dispose() {
		world.dispose();
	}

	@Override
	public void saveGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitGame() {
		dispose();
		Gdx.app.log("DISPOSING", "Released level resources");
	}
}
