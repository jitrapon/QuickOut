package com.code2play.quickout;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

/**
 * World is the world that Actors live in.
 * 
 * All Actor are associated with a World and can get access to the world object.
 * @author Jitrapon
 *
 */
public class World {
	
	private static final int MAX_NUM_OBJECT_ONSCREEN = 15;
	private static final int VIRTUAL_WIDTH = Gdx.graphics.getWidth();
	private static final int VIRTUAL_HEIGHT = Gdx.graphics.getHeight();
	
	private Array<Ball> balls;

	
	public World() {
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
	
	public void addBall(Ball b) {
		balls.add(b);
	}
	
	/** Called when the World is to be updated.
	 * 
	 * @param delta the time in seconds since the last render. */
	public void update(float delta) {
		Iterator<Ball> iter = balls.iterator();
		while (iter.hasNext()) {
			Ball ball = iter.next();
			ball.update(delta);
		}
		
	}
}
