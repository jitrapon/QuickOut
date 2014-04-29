package com.code2play.quickout;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
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

	public void checkOtherBallCollision() {
		for (int i = 0, size = world.getBalls().size; i < size; i++) {
			if (i == tag-1) {
				// don't check against itself!
				continue; 
			}
			else {
				Ball other = world.getBalls().get(i);
				if (this.intersects(other)) {
					this.resolveCollision(other);
					//					Gdx.app.log("overlap detection", this.tag + " overlaps " + other.tag);
				}
			}
		}
	}

	@Override
	public void setState(int state) {
		super.setState(state);

		switch (state) {
		case FLINGED:
			physicsEnabled  = false;
			break;
		case DRAGGED:

			break;
		case TAPPED: 
			x = -500;
			y = -500;
			velocity.x = 0;
			velocity.y = 0;
			break;
		default :
			break;
		}
	}

	private void resolveCollision2(Ball A, Ball B) {
		// Calculate relative velocity
		Vector2 rv = B.velocity.sub(A.velocity);

		// Calculate relative velocity in terms of the normal direction
		Vector2 normal = new Vector2(B.x - A.x, B.y - A.y);
		float velAlongNormal = rv.dot(normal.nor());

		// Do not resolve if velocities are separating
		if(velAlongNormal > 0)
			return;

		// Calculate restitution
		float e = Math.min(A.restitution, B.restitution);

		// Calculate impulse scalar
		float j = -(1 + e) * velAlongNormal;
		j /= (1 / A.mass) + (1 / B.mass);

		// Apply impulse
		Vector2 impulse = normal.scl(j);
		A.velocity = A.velocity.sub(impulse.scl(1 / A.mass));
		B.velocity = B.velocity.add(impulse.scl(1 / B.mass));

	}

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

	public void update(float delta) {
		// update state time
		stateTime += delta;

		//		// handles collision in the world
		//		checkOtherBallCollision();

		// on collision with left or right wall
		if (physicsEnabled) {
			if (x - bounds().radius < 0 || x + bounds().radius > world.getMaxX()) {
				velocity.x *= -1;
			}

			// on collision with bottom or top wall
			if (y - bounds().radius < 0 || y + bounds().radius > world.getMaxY()) {
				velocity.y *= -1;
			}
		}
		
		// update its position
		// make sure the object stay within the screen bounds and bounce around if NOT dragged
		this.moveBy(velocity.x * Gdx.graphics.getDeltaTime(), velocity.y * Gdx.graphics.getDeltaTime());
	}


}
