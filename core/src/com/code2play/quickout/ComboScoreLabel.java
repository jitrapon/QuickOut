package com.code2play.quickout;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class ComboScoreLabel extends Label {
	
	private Level level;
	
	public ComboScoreLabel(Level level, CharSequence text, LabelStyle style) {
		super(text, style);
		this.level = level;
	}
	
	@Override
    public void act(final float delta) {
        this.setText( Integer.toString(level.getComboScore()) );
        super.act(delta);
    }
}
