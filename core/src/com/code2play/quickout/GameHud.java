package com.code2play.quickout;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.code2play.game.IHud;


/**
 * This class abstracts the Scene2D stage and actors for in-game HUD 
 * implementations
 * @author Jitrapon
 *
 */
public class GameHud implements IHud {
	
	private Level level;
	
	private Stage stage;
	private ScoreLabel score;
	private LabelStyle style;
	private BitmapFont font;
	
	/**
	 * Creates the HUD with a specified coordinates
	 * The default Viewport used is ExtendedViewport
	 * @param width
	 * @param height
	 */
	public GameHud(Level level, int width, int height, int maxWidth, 
			int maxHeight) {
		this.level = level;
		stage = new Stage( new ExtendViewport(width, height, maxWidth, maxHeight) );
		fillContent();
	}
	
	private void fillContent() {
		font = new BitmapFont();
		font.scale(1.7f);
		style = new LabelStyle(font, Color.WHITE);
		
		score = new ScoreLabel(level, "undefined", style);
		score.setPosition(stage.getWidth()-100, stage.getHeight()-50);
		
		stage.addActor(score);
	}

	public Stage getStage() {
		return stage;
	}
	
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
	
	public void draw(float delta) {
	    stage.act(delta);
	    stage.draw();
	}
	
	public void dispose() {
		stage.dispose();
	}
	
}
