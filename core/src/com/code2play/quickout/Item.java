package com.code2play.quickout;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.code2play.game.IGameItem;
import com.code2play.quickout.Level.ItemType;

/**
 * Items are enhancements in a level to boost bonus gold and scores.
 * Items can be stored in slots.
 * @author Jitrapon
 *
 */
public abstract class Item extends Entity implements IGameItem {
	
	// physical values
	public boolean slotted;								// whether this item has been slotted
	public boolean removed;								// marked for cleanup
	public float radius;								// radius of this item
	public ItemType type;								// type of this item

	/* Animations */
	private Animation animation;						// Current animation

	/* item values */
	private final float MAX_DURATION;					// item's effect duration in seconds
	private boolean active;								// whether or not the item's effect are currently active
	private float lifeTime;								// item's lifetime before it disappears

	/* ALL ITEM STATE (default idle state) */
	public static final int ACTIVE = 0;					// indicates the item is just being tapped and active
	public static final int FLINGED = 1;				// indicates the item is just being flinged
	public static final int SLOTTED = 2;				// indicates the item is slotted
	
	/* other properties */

	/**
	 * Initializes a generic item.
	 * @param animList
	 * @param radius
	 * @param level 
	 * @param tag
	 */
	public Item(Animation animation, float radius, ItemType type, 
			float maxDuration, float lifeTime, Level level) {
		super(radius);
		this.level = level;
		this.animation = animation;
		this.type = type;
		this.radius = radius;
		MAX_DURATION = maxDuration;
		this.lifeTime = lifeTime;
		active = false;
		slotted = false;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public float getLifeTime() {
		return lifeTime;
	} 

	public Animation getAnimation() {
		return animation;
	}
	
	/**
	 * Sets whether or not this item's effect is currently active
	 * @param isActive
	 */
	public void setActive(boolean isActive) {
		active = isActive;
		if (isActive) setState(ACTIVE);
		else setState(INACTIVE);
	}
	

	@Override
	public void setState(int state) {
		// do nothing if current state equals state
		if (state != this.state) {
			this.state = state;
			stateTime = 0.0f;

			if (state == FLINGED) {
				// get existing fixture and change category bits to filter every collision out
				Filter filter = this.getFilterData();
				filter.maskBits = 0x0;
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
	public void attachPhysicsBody(short categoryBits, float x, float y, float density, float linearDamping, 
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
		circle.setRadius(this.radius * WORLD_TO_BOX);

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
	 * Update the item bubble by each iteration of the world step
	 */
	boolean beginActive = true;
	@Override
	public void update(float delta) {
		
		// update state time
		stateTime += delta;

		// check state
		switch (state) {

		/* the item is tapped and now its effect is active */
		case ACTIVE:
			if (body != null) 
				dispose();
			
			if (level.resetItemEffectDuration(this)) {
				removed = true;
				return;
			}
			
			// called in the beginning of the effect
			if (beginActive) {
				onEffectStarted(delta);
				beginActive = false;
			}
			
			// while still in duration, call active effect
			if (stateTime > MAX_DURATION) {
				this.removed = true;
				onEffectFinished(delta);
			}
			else 
				applyEffect(delta);
			
			return;

		/* the item is just released of the drag with certain velocity threshold */
		case FLINGED:
			
			// check if out of bound
			if (x + radius < level.getMinX() || x - radius > level.getMaxX() 
					|| y + radius < level.getMinY() || y - radius > level.getMaxY()) {
				
				// get rid of mousejoint
				if (level.getWorldRenderer().mouseJoint != null) {
					level.getWorldRenderer().draggedItem = null;
					level.getPhysicsWorld().destroyJoint(level.getWorldRenderer().mouseJoint);
					level.getWorldRenderer().mouseJoint = null;
				} 
				
				// get rid of item physics stuff
				dispose();
				
				// if item is thrown into the slot below
				if (y + radius < level.getMinY()) {
					setState(SLOTTED);
					return;
				}
				
				// if item is thrown into other directions than below, we remove it
				removed = true;
				return;
			}

			break;
			
		/* the item is slotted */
		case SLOTTED:
			if (!slotted) {
				level.getItemSlot().addItem(this);
				slotted = true;
			}
			return;

		/* default state is INACTIVE */
		default:
			break;
		}

		x = body.getPosition().x * BOX_TO_WORLD;
		y = body.getPosition().y * BOX_TO_WORLD;
	}

	@Override
	public void dispose() {
		level.getPhysicsWorld().destroyBody(body);
		body = null;
	}
}
