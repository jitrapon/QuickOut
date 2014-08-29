package com.code2play.quickout;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.code2play.quickout.Level.ScoreIndicator;

public class ScoreEffectLabel extends Label {
	
	public ScoreEffectLabel(Stage stage, ScoreIndicator score, LabelStyle style) {
		super(Integer.toString(score.score), style);
		this.setPosition(score.posX / (Level.MAX_VIRTUAL_WIDTH/stage.getWidth()), 
				score.posY / (Level.MAX_VIRTUAL_HEIGHT/stage.getHeight()));
		this.addAction(
				parallel(
				moveTo(this.getX(), this.getY()+100, 2.50f, Interpolation.linear),
//				scaleTo(0.5f, 0.5f, 2.50f, Interpolation.linear),
				fadeOut(2.50f, Interpolation.linear)
				));
	}
	
	@Override
    public void act(final float delta) {
        super.act(delta);
    }
}


