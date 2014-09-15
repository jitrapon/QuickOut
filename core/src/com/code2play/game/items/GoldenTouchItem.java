package com.code2play.game.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.code2play.quickout.GameHud;
import com.code2play.quickout.Item;
import com.code2play.quickout.Level;
import com.code2play.quickout.Level.ItemType;
import com.code2play.quickout.WorldView;

public class GoldenTouchItem extends Item {
	
	private LabelStyle defaultScoreStyle;
	private LabelStyle newScoreStyle;
	
	private LabelStyle defaultScoreEffectStyle;
	private LabelStyle newScoreEffectStyle;

	public GoldenTouchItem(Animation animation, float radius, ItemType type,
			float maxDuration, float lifeTime, Level level) {
		super(animation, radius, type, maxDuration, lifeTime, level);
		
		defaultScoreStyle = level.getWorldRenderer().getGameHUD().getScoreStyle();
		newScoreStyle = new LabelStyle(defaultScoreStyle);
		newScoreStyle.fontColor = Color.BLUE;
		
		defaultScoreEffectStyle = level.getWorldRenderer().getGameHUD().getScoreEffectStyle();
		newScoreEffectStyle = new LabelStyle(defaultScoreEffectStyle);
		newScoreEffectStyle.fontColor = Color.BLUE;
		
		System.out.println("Spawning Golden Touch!");
	}

	@Override
	public void onEffectStarted(float delta) {
		level.getWorldRenderer().getGameHUD().setScoreStyle(newScoreStyle);
		level.getWorldRenderer().getGameHUD().setScoreEffectStyle(newScoreEffectStyle);
		level.itemGoldenTouchActive = true;
	}
	

	@Override
	public void applyEffect(float delta) {}

	
	@Override
	public void onEffectFinished(float delta) {
		level.getWorldRenderer().getGameHUD().setScoreStyle(defaultScoreStyle);
		level.getWorldRenderer().getGameHUD().setScoreEffectStyle(defaultScoreEffectStyle);
		level.itemGoldenTouchActive = false;
	}

}
