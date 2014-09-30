package com.code2play.quickout;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class TimerLabel extends Label {

	private Level level;

	public TimerLabel(Level level, CharSequence text, LabelStyle style) {
		super(text, style);
		this.level = level;
	}

	@Override
	public void act(final float delta) {
		int timeLeft = MathUtils.round(level.getTimeLeft());
		this.setText( Integer.toString(timeLeft) );
		super.act(delta);
	}
}
