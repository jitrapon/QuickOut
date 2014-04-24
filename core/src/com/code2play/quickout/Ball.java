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

	/* ALL BALL STATE (default idle state */
	public static final int TAPPED = 0;					// indicates the ball is just being tapped 
	public static final int LONG_TAPPED = 1;			// indicates the ball is just being long-tapped
	public static final int DRAGGED = 2;				// indicates the ball is just being dragged
	public static final int FLINGED = 3;				// indicates the ball is just being let go of drag state

	public Ball(Texture texture, float radius, int tag) {
		super(texture, radius);
		this.tag = tag;
		Random random = new Random();
		velocity = new Vector2(random.nextInt(100), random.nextInt(100));
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
					resolveCollision(this, other);
					//					Gdx.app.log("overlap detection", this.tag + " overlaps " + other.tag);
				}
			}
		}
	}

	@Override
	public void setState(int state) {
		super.setState(state);
		
		switch (state) {
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

	private void resolveCollision(Ball A, Ball B) {
		float newVelX1 = (A.velocity.x * (A.mass - B.mass) + (2 * B.mass * B.velocity.x)) / 
				(A.mass + B.mass);
		float newVelY1 = (A.velocity.y * (A.mass - B.mass) + (2 * B.mass * B.velocity.y)) / 
				(A.mass + B.mass);
		float newVelX2 = (B.velocity.x * (B.mass - A.mass) + (2 * A.mass * A.velocity.x)) / 
				(A.mass + B.mass);
		float newVelY2 = (B.velocity.y * (B.mass - A.mass) + (2 * A.mass * A.velocity.y)) / 
				(A.mass + B.mass);

		A.velocity.x = newVelX1;
		A.velocity.y = newVelY1;
		B.velocity.x = newVelX2;
		B.velocity.y = newVelY2;
	}

	public void update(float delta) {
		// update state time
		stateTime += delta;

		// handles collision in the world
		checkOtherBallCollision();

		// update its position
		// make sure the object stay within the screen bounds and bounce around if NOT dragged
		x += velocity.x * Gdx.graphics.getDeltaTime();
		y += velocity.y * Gdx.graphics.getDeltaTime();

		// on collision with left or right wall
		if (x - bounds().radius < 0 || x + bounds().radius > world.getMaxX()) {
			velocity.x *= -1;
		}

		// on collision with bottom or top wall
		if (y - bounds().radius < 0 || y + bounds().radius > world.getMaxY()) {
			velocity.y *= -1;
		}
	}


}
