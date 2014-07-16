package com.code2play.quickout;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
	
	private Image bottomHud;
	private Group topHud;						
	
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
		// create a score label
		font = new BitmapFont();
		font.scale(1.7f);
		style = new LabelStyle(font, Color.WHITE);
		
		score = new ScoreLabel(level, "undefined", style);
		score.setPosition(stage.getWidth()-100, stage.getHeight()-50);
		
		// create ground HUD
		bottomHud = new Image(Assets.getTextureRegion(Assets.LEVEL_HUD_GROUND));
		bottomHud.setPosition(0, 0);
		
		// create top HUD
		// top HUD consists of movable actors 
		topHud = new Group();
		Image cloud1 = new Image(Assets.getTextureRegion(Assets.LEVEL_BACKGROUND_CLOUD1));
		Image cloud2 = new Image(Assets.getTextureRegion(Assets.LEVEL_BACKGROUND_CLOUD2));
		Image cloud3 = new Image(Assets.getTextureRegion(Assets.LEVEL_BACKGROUND_CLOUD3));
		Image cloud4 = new Image(Assets.getTextureRegion(Assets.LEVEL_BACKGROUND_CLOUD4));
		Image cloud5 = new Image(Assets.getTextureRegion(Assets.LEVEL_BACKGROUND_CLOUD5));
		cloud1.setPosition(stage.getWidth()+50, stage.getHeight() - 180);
		cloud2.setPosition(stage.getWidth()+20, stage.getHeight() - 180);
		cloud3.setPosition(stage.getWidth()+10, stage.getHeight() - 180);
		cloud4.setPosition(stage.getWidth()+30, stage.getHeight() - 180);
		cloud5.setPosition(stage.getWidth()+15, stage.getHeight() - 180);
		cloud1.addAction(Actions.moveTo(-500, stage.getHeight() - 180, 60.0f, Interpolation.linear));
		cloud2.addAction(Actions.moveTo(-500, stage.getHeight() - 180, 90.0f, Interpolation.linear));
		cloud3.addAction(Actions.moveTo(-500, stage.getHeight() - 180, 120.0f, Interpolation.linear));
		cloud4.addAction(Actions.moveTo(-500, stage.getHeight() - 180, 85.0f, Interpolation.linear));
		cloud5.addAction(Actions.moveTo(-500, stage.getHeight() - 180, 150.0f, Interpolation.linear));
		topHud.addActor(cloud1);
		topHud.addActor(cloud2);
		topHud.addActor(cloud3);
		topHud.addActor(cloud4);
		topHud.addActor(cloud5);
//		topHud.setColor(0, 0, 0, 0.2f);						// set alpha
		
		// add all actors to the stage
		// actors inserted later will be drawn on top of actors added earlier. 
		// Touch events that hit more than one actor are distributed to topmost actors first.
		stage.addActor(topHud);
		stage.addActor(score);
		stage.addActor(bottomHud);
	}
	
	public float getGroundHUDHeight() {
		if (bottomHud != null) return bottomHud.getHeight();
		else return 0.0f;
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
