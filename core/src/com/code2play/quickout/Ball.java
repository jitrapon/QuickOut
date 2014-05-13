package com.code2play.quickout;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.code2play.quickout.Level.EntityType;

public class Ball extends Entity {

	// physical values
	private final float mass;
	public float radius;
	public boolean physicsEnabled = true;

	/* ALL BALL STATE (default idle state */
	public static final int TAPPED = 0;					// indicates the ball is just being tapped 
	public static final int LONG_TAPPED = 1;			// indicates the ball is just being long-tapped
	public static final int DRAGGED = 2;				// indicates the ball is just being dragged
	public static final int FLINGED = 3;				// indicates the ball is just being let go of drag state

	public Ball(Texture texture, float radius, int tag) {
		super(texture, radius);
		this.tag = tag;
		Random random = new Random();
		velocity = new Vector2(random.nextInt(50), random.nextInt(50));
		//		velocity = new Vector2(0, 0);
		mass = 1;
		this.radius = radius;
	}

	@Override
	public void setState(int state) {
		super.setState(state);
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
			moveTo(-1200, -1200);
			break;
			
		/* the ball is currently being dragged */
		case DRAGGED:
			
			break;
			
		/* the ball is just released of the drag with certain velocity threshold*/
		case FLINGED:
			// get existing fixture and change category bits to filter wall collision out
			Filter filter = this.getFilterData();
			filter.maskBits = EntityType.BALL.getCategoryBits();
			this.setFilterData(filter);
			break;
			
		/* default state is INACTIVE */
		default:

			break;
		}
		
		// update state time
		stateTime += delta;
		
		x = body.getPosition().x * BOX_TO_WORLD;
		y = body.getPosition().y * BOX_TO_WORLD;
	}
}
