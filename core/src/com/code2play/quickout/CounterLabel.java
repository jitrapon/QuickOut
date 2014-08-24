package com.code2play.quickout;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class CounterLabel extends Label {
	
	private Level level;
	
	public CounterLabel(Level level, CharSequence text, LabelStyle style) {
		super(text, style);
		this.level = level;
	}
	
	@Override
    public void act(final float delta) {
        this.setText( Integer.toString(level.getBallCount()) );
        super.act(delta);
    }
}

