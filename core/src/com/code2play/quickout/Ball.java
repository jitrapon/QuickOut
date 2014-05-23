package com.code2play.quickout;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.code2play.quickout.Level.EntityType;

public class Ball extends Entity {

	// physical values
	public boolean removed = false;						// marked for cleanup
	private final float mass;
	public float radius;
	public boolean physicsEnabled = true;
	public String type;									// representation of the ball's type
	public int tag;

	/* ALL BALL STATE (default idle state */
	public static final int TAPPED = 0;					// indicates the ball is just being tapped 
	public static final int LONG_TAPPED = 1;			// indicates the ball is just being long-tapped
	public static final int DRAGGED = 2;				// indicates the ball is just being dragged
	public static final int FLINGED = 3;				// indicates the ball is just being let go of drag state

	public Ball(Texture texture, float radius, int tag) {
		super(texture, radius);
		setType(tag);
		Random random = new Random();
		velocity = new Vector2(random.nextInt(50), random.nextInt(50));
		//		velocity = new Vector2(0, 0);
		mass = 1;
		this.radius = radius;
	}
	
	public Ball(Array<Texture> textures, float radius, int tag) {
		super(textures, radius);
		setType(tag);
		Random random = new Random();
		velocity = new Vector2(random.nextInt(50), random.nextInt(50));
		//		velocity = new Vector2(0, 0);
		mass = 1;
		this.radius = radius;
	}

	public void setType(int tag) {
		this.tag = tag;

		switch (tag) {
		case 0: 
			type = "blue";
			break;
		case 1: 
			type = "green";
			break;
		case 2: 
			type = "red";
			break;
		case 3: 
			type = "yellow";
			break;
		default: 
			type = "undefined";
			break;
		}
	}

	public String getType() {
		return type;
	}


	@Override
	public void setState(int state) {
		// do nothing if current state equals state
		if (state != this.state) {
			this.state = state;
			stateTime = 0.0f;
			
			if (state == FLINGED) {
				// get existing fixture and change category bits to filter wall collision out
				Filter filter = this.getFilterData();
				filter.maskBits = EntityType.BALL.getCategoryBits();
				this.setFilterData(filter);
			}
		}
		else {
			// do what ?
		}
	}

	/**
	 * Attach the Box2D physics body to this entity with the shape of circle and initialize
	 * certain physical properties
	 * @param x Initial x position
	 * @param y Initial y position
	 */
	@Override
	public void attachPhysicsBody(short categoryBits, float radius, float x, float y, float density, float linearDamping, 
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

		// Default fixture category bits for collision filtering in-game
		fixtureDef.filter.categoryBits = categoryBits;

		body.createFixture(fixtureDef);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		circle.dispose();

		// add this object to the body's user data for later reference
		body.setUserData(this);
	}


	/**
	 * @deprecated This method is no longer in use, but is kept for reference for collision
	 * @param other
	 */
	public void resolveCollision(Ball other) {
		float newVelX1 = (this.velocity.x * (this.mass - other.mass) + (2 * other.mass * other.velocity.x)) / 
				(this.mass + other.mass);
		float newVelY1 = (this.velocity.y * (this.mass - other.mass) + (2 * other.mass * other.velocity.y)) / 
				(this.mass + other.mass);
		float newVelX2 = (other.velocity.x * (other.mass - this.mass) + (2 * this.mass * this.velocity.x)) / 
				(this.mass + other.mass);
		float newVelY2 = (other.velocity.y * (other.mass - this.mass) + (2 * this.mass * this.velocity.y)) / 
				(this.mass + other.mass);

		this.velocity.x = newVelX1;
		this.velocity.y = newVelY1;
		other.velocity.x = newVelX2;
		other.velocity.y = newVelY2;
	}

	/**
	 * Update the ball by each iteration of the world step
	 */
	@Override
	public void update(float delta) {

		// check state
		switch (state) {

		/* the ball is tapped once */
		case TAPPED:
			//			moveTo(-1200, -1200);
			removed = true;
			dispose();
			return;

			/* the ball is currently being dragged */
		case DRAGGED:

			break;

		case LONG_TAPPED:
			removed = true;
			dispose();
			return;

			/* the ball is just released of the drag with certain velocity threshold*/
		case FLINGED:
			if (x + radius < 0 || x - radius > level.getMaxX() 
					|| y + radius < 0 || y - radius > level.getMaxY()) {
				removed = true;
				dispose();
				return;
			}
			
			/* default state is INACTIVE */
		default:

			break;
		}

		// update state time
		stateTime += delta;

		x = body.getPosition().x * BOX_TO_WORLD;
		y = body.getPosition().y * BOX_TO_WORLD;
	}

	@Override
	public void dispose() {
		level.getPhysicsWorld().destroyBody(body);
		body = null;
	}
}
