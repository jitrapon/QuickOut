package com.code2play.quickout;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

/**
 * All game objects are entities. Different behaviors are specified by extending this class 
 * and overriding update method.
 * @author Jitrapon
 *
 */
public class Entity {
	
	/** The default state for any new game object. */
	public static final int INACTIVE = -1;
	
	/** Reference to the world **/
	public World world;

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
	
	public Entity() {
		stateTime = 0.0f;
		inCollision = false;
		bounds = new Circle();
		state = INACTIVE;
	}
	
	public Entity(Texture text, float radius) {
		stateTime = 0.0f;
		inCollision = false;
		bounds = new Circle();
		bounds.radius = radius;
		width = radius;
		height = radius;
		texture = text;
		state = INACTIVE;
	}
	
	public void setWorld(World w) {
		world = w;
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
		this.state = state;
		stateTime = 0.0f;
	}
	
	/**
	 * Returns true if this game object is in collision with another game object.
	 */
	public boolean intersects(Entity other) {
		return Intersector.overlaps(bounds, other.bounds);
	}
	
	/** Updates this game object. Typically you would override this to create interesting behaviour.
	 * 
	 * @param delta time in seconds since the last update. */
	public void update(float delta) {
	}
	
}
