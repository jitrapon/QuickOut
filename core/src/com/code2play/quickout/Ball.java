package com.code2play.quickout;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Ball extends Entity {

	// physical values
	private final float restitution = 0.2f;				// coefficient of elasticity (bouncing) 0.0 < e < 1.0
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
			
		/* the ball is just released of the drag */
		case FLINGED:
			physicsEnabled  = false;
			break;
			
		/* default state is INACTIVE, which means that the ball's state is determined by the 
		 physics engine */
		default:
			x = body.getPosition().x * BOX_TO_WORLD;
			y = body.getPosition().y * BOX_TO_WORLD;
			break;
		}
		
		// update state time
		stateTime += delta;

		if (tag==2) Gdx.app.log("Position", "Ball " + tag + " position is " + x + ", " + y);

		// on collision with left or right wall
		if (physicsEnabled) {
			if (x - bounds().radius < 0 || x + bounds().radius > level.getMaxX()) {
				Vector2 vec = getVelocity();
				vec.x *= -1.0f;
				setVelocity(vec);
			}

			// on collision with bottom or top wall
			if (y - bounds().radius < 0 || y + bounds().radius > level.getMaxY()) {
				Vector2 vec = getVelocity();
				vec.y *= -1.0f;
				setVelocity(vec);
			}
		}

		// update its position
		// make sure the object stay within the screen bounds and bounce around if NOT dragged
		//		this.moveBy(velocity.x * Gdx.graphics.getDeltaTime(), velocity.y * Gdx.graphics.getDeltaTime());
	}


}
