package com.code2play.game.items;

import com.badlogic.gdx.graphics.g2d.Animation;
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
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void applyEffect(float delta) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public void onEffectFinished(float delta) {
		// TODO Auto-generated method stub
		
	}


}
