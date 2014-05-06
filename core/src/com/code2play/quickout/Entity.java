package com.code2play.quickout;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * All game objects are entities. Different behaviors are specified by extending this class 
 * and overriding update method.
 * @author Jitrapon
 *
 */
public class Entity {
	
	/** The default state for any new game object. */
	public static final int INACTIVE = -1;
	
	/** Reference to the level **/
	public Level level;

	/** This game object's x coordinate in world space. */
	public float x;

	/** This game object's y coordinate in world space. */
	public float y;

	/** This game object's width in world units. */
	public float width;

	/** This game object's height in world units. */
	public float height;
	
	/** This game object's current state. */
	public int state;
	
	/** How long this game object has been in its current state (in seconds). */
	public float stateTime;
	
	/** Will be true if this game object is in collision. */
	public boolean inCollision;
	
	/** This game object's texture to be drawn */
	public Texture texture;
	
	// Holds this game object's bounding rectangle in world space.
	private Circle bounds;
	
	/** holds the current velocity magnitude in x,y direction of the object */
	public Vector2 velocity;				
	
	/** This game object's unique tag id **/
	public int tag;
	
	/** Number of collided objects **/
	public int collisionCount = 0;
	
	/** Box2D Physical Body */
	private BodyDef bodyDef;
	
	/** Box2D Body */
	protected Body body;
	
	/** Box2D Fixture **/
	private FixtureDef fixtureDef;
	
	/** Box2D Shape **/
	private CircleShape circle;
	
	public static final float WORLD_TO_BOX = 1/75f;		
	public static final float BOX_TO_WORLD = 75.0f;		
	
	public Entity() {
		stateTime = 0.0f;
		inCollision = false;
		bounds = new Circle();
		bounds.x = x;
		bounds.y = y;
		state = INACTIVE;
	}
	
	public Entity(Texture text, float radius) {
		this();
		bounds.radius = radius;
		width = radius;
		height = radius;
		texture = text;
	}
	
	/**
	 * Attach the Box2D physics body to this entity
	 * @param x Initial x position
	 * @param y Initial y position
	 */
	public void attachPhysicsBody(float radius, float x, float y, float density, float linearDamping, 
			float friction, float restitution) {
		
		// First we create a body definition
		bodyDef = new BodyDef();
		
		// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
		bodyDef.type = BodyType.DynamicBody;
		
		// Set our body's starting position in the world
		bodyDef.position.set(x * WORLD_TO_BOX, y * WORLD_TO_BOX);
		this.x = x;
		this.y = y;
		bounds.x = x;
		bounds.y = y;
		
		// Create our body in the world using our body definition
		body = level.getPhysicsWorld().createBody(bodyDef);
		body.setLinearDamping(linearDamping);
		
		// Create a circle shape and set its radius
		circle = new CircleShape();
		circle.setRadius(radius * WORLD_TO_BOX);

		// Create a fixture definition to apply our shape to
		fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = density; 
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution; 
		
		body.createFixture(fixtureDef);
		
		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		circle.dispose();
	}
	
	public Vector2 getPosition() {
		return new Vector2(body.getPosition().x , body.getPosition().y);
	}
	
	/**
	 * Override this method to dispose any resources attached to this entity
	 */
	public void dispose() {
	}
	
	public void setWorld(Level l) {
		level = l;
	}

	/** Returns this <code>GameObject</code>'s bounding rectangle.
	 * @return the bounding rectangle. */
	public Circle bounds() {
		bounds.x = x;
		bounds.y = y;
		bounds.radius = width;
		return bounds;
	}

	/** Switches this game object into a new state and resets {@link #stateTime}.
	 * @param state the new state. */
	public void setState(int state) {
		// do nothing if current state equals state
		if (state != this.state) {
			this.state = state;
			stateTime = 0.0f;
		}
	}
	
	public Vector2 getVelocity() {
		return body.getLinearVelocity();
	}
	
	public void setVelocity(Vector2 v) {
		body.setLinearVelocity(v.scl(1));
	}
	
	/**
	 * Instantly moves the entity to the specified coordinate world's space
	 * This method uses body.setTransform() which may cause unexpected physical
	 * behaviors. Use this only to move entity to a non-disruptive place.
	 * @param posX New x position on the world's coordinate
	 * @param posY New y position on the world's coordinate
	 */
	public void moveTo(float posX, float posY) {
//		body.setAwake(true);
		body.setTransform(posX * WORLD_TO_BOX, posY * WORLD_TO_BOX, body.getAngle());
		x = posX;
		y = posY;
		bounds.x = x;
		bounds.y = y;
	}
	
	/**
	 * Move the entity by the amount specified in world's space
	 * @param stepX
	 * @param stepY
	 */
	public void moveBy(float stepX, float stepY) {
		x += stepX;
		y += stepY;
		bodyDef.position.set(x * WORLD_TO_BOX, y * WORLD_TO_BOX);
		bounds.x += stepX;
		bounds.y += stepY;
	}
	
	/**
	 * Returns true if this game object is in collision with another game object.
	 */
	public boolean intersects(Entity other) {
		boolean collided = Intersector.overlaps(bounds, other.bounds);
		if (collided) {
			collisionCount++;
			other.collisionCount++;
			inCollision = true;
			other.inCollision = true;
		}
		return collided;
	}
	
	/** Updates this game object. Typically you would override this to create interesting behaviour.
	 * 
	 * @param delta time in seconds since the last update. */
	public void update(float delta) {
	}
	
}
