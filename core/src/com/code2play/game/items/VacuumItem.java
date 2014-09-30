package com.code2play.game.items;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.code2play.quickout.Ball;
import com.code2play.quickout.Item;
import com.code2play.quickout.Level;
import com.code2play.quickout.Level.ItemType;

public class VacuumItem extends Item {

	public VacuumItem(Animation animation, float radius, ItemType type,
			float maxDuration, float lifeTime, Level level) {
		super(animation, radius, type, maxDuration, lifeTime, level);
		System.out.println("Spawning Vacuum!");
	}

	@Override
	public void onEffectStarted(float delta) {
		level.itemVacuumActive = true;
	}
	

	Vector2 vec = new Vector2();
	@Override
	public void applyEffect(float delta) {
		if (level.itemVacuumActive && level.itemVacuumApplied && 
				level.getWorldRenderer().getMouseJoint() == null) {
			for (Ball b : level.getBalls()) {
				if (!b.removed && b.tag == level.getMoveSet().getMoves().first().ballType) {
					
					if (b.getBody().getPosition().epsilonEquals(level.vacuumPos.x*Level.WORLD_TO_BOX, 
							level.vacuumPos.y*Level.WORLD_TO_BOX, 0.5f)) {
						b.setState(Ball.TAPPED);
					}
					else {
						vec.set((level.vacuumPos.x*Level.WORLD_TO_BOX) - b.getBody().getPosition().x,
								(level.vacuumPos.y*Level.WORLD_TO_BOX) - b.getBody().getPosition().y);
						
//						b.getBody().applyForceToCenter(vec.scl(3f), true);
						b.getBody().setLinearVelocity(vec.nor().scl(15f));
//						b.getBody().setLinearVelocity(vec.scl(3f));
					}
				}
			}
		}
	}

	
	@Override
	public void onEffectFinished(float delta) {
		level.itemVacuumActive = false;
		level.itemVacuumApplied = false;
	}


}
