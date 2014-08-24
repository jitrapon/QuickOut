package com.code2play.quickout;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.code2play.game.IHud;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;


/**
 * This class abstracts the Scene2D stage and actors for in-game HUD 
 * implementations
 * @author Jitrapon
 *
 */
public class GameHud implements IHud {
	
	private Level level;
	
	private Stage stage;
	
	private ScoreLabel score;										// displays current game level score
	private LabelStyle scoreStyle;
	private BitmapFont scoreFont;
	
	private CounterLabel counter;
	private BitmapFont countFont;
	private LabelStyle countStyle;
	
	private Group moveIconGroup;
	private AnimatedImage moveIcon1;
	private AnimatedImage moveIcon2;
	private AnimatedImage moveIcon3;
	
	private AnimatedImage nextMoveIcon1;
	private AnimatedImage nextMoveIcon2;
	private AnimatedImage nextMoveIcon3;
	
	private Image bottomHud;
	private Group topHud;						
	
	private float moveIconSize = 75.0f;
	private float nextMoveIconSize = 50.0f;
	
	
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
		scoreFont = new BitmapFont();
		scoreFont.scale(1.7f);
		scoreStyle = new LabelStyle(scoreFont, Color.WHITE);
		
		score = new ScoreLabel(level, "undefined", scoreStyle);
		score.setPosition(stage.getWidth()-100, stage.getHeight()-130);
		
		// create ball count label
		countFont = new BitmapFont();
		countFont.scale(2.5f);
		countStyle = new LabelStyle(countFont, Color.WHITE);
		
		counter = new CounterLabel(level, "0", countStyle);
		counter.setPosition(stage.getWidth()-100, stage.getHeight()-70);
		
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
		cloud1.addAction(moveTo(-500, stage.getHeight() - 180, 60.0f, Interpolation.linear));
		cloud2.addAction(moveTo(-500, stage.getHeight() - 180, 90.0f, Interpolation.linear));
		cloud3.addAction(moveTo(-500, stage.getHeight() - 180, 120.0f, Interpolation.linear));
		cloud4.addAction(moveTo(-500, stage.getHeight() - 180, 85.0f, Interpolation.linear));
		cloud5.addAction(moveTo(-500, stage.getHeight() - 180, 150.0f, Interpolation.linear));
		topHud.addActor(cloud1);
		topHud.addActor(cloud2);
		topHud.addActor(cloud3);
		topHud.addActor(cloud4);
		topHud.addActor(cloud5);
//		topHud.setColor(0, 0, 0, 0.2f);						// set alpha
		
		// create level goal indicator
		//TODO
		moveIconGroup = new Group();
		moveIcon1 = new AnimatedImage( Assets.animationList.get(Level.BLUE).first() );
		moveIcon1.setSize(moveIconSize, moveIconSize);
		moveIcon1.setPosition( stage.getWidth()/2 - moveIconSize/2, stage.getHeight()-100 );
		moveIcon2 = new AnimatedImage( Assets.animationList.get(Level.GREEN).first() );
		moveIcon2.setSize(moveIconSize, moveIconSize);
		moveIcon2.setPosition( stage.getWidth()/2 - moveIconSize/2, stage.getHeight()-100 );
		moveIcon2.setVisible(false);
		moveIcon3 = new AnimatedImage( Assets.animationList.get(Level.RED).first() );
		moveIcon3.setSize(moveIconSize, moveIconSize);
		moveIcon3.setPosition( stage.getWidth()/2 - moveIconSize/2, stage.getHeight()-100 );
		moveIcon3.setVisible(false);
		
		nextMoveIcon1 = new AnimatedImage( Assets.animationList.get(Level.BLUE).first() );
		nextMoveIcon1.setSize(nextMoveIconSize, nextMoveIconSize);
		nextMoveIcon1.setPosition( stage.getWidth()/2 + moveIconSize/2, stage.getHeight()-100 );
		nextMoveIcon2 = new AnimatedImage( Assets.animationList.get(Level.GREEN).first() );
		nextMoveIcon2.setSize(nextMoveIconSize, nextMoveIconSize);
		nextMoveIcon2.setPosition( stage.getWidth()/2 + moveIconSize/2 + 10, stage.getHeight()-100 );
		nextMoveIcon2.setVisible(false);
		nextMoveIcon3 = new AnimatedImage( Assets.animationList.get(Level.RED).first() );
		nextMoveIcon3.setSize(nextMoveIconSize, nextMoveIconSize);
		nextMoveIcon3.setPosition( stage.getWidth()/2 + moveIconSize/2 + 20, stage.getHeight()-100 );
		nextMoveIcon3.setVisible(false);
		
		moveIconGroup.addActor(nextMoveIcon1);
		moveIconGroup.addActor(nextMoveIcon2);
		moveIconGroup.addActor(nextMoveIcon3);
		
		moveIconGroup.addActor(moveIcon1);
		moveIconGroup.addActor(moveIcon2);
		moveIconGroup.addActor(moveIcon3);
		
		
		// add all actors to the stage
		// actors inserted later will be drawn on top of actors added earlier. 
		// Touch events that hit more than one actor are distributed to topmost actors first.
		stage.addActor(topHud);
		stage.addActor(counter);
		stage.addActor(score);
		stage.addActor(moveIconGroup);
		stage.addActor(bottomHud);
	}
	
	/**
	 * Displays move icons according to the moveset
	 * TODO display array correctly
	 * @param move
	 */
	private void setMoveIcon(Array<Move> moves, Array<Move> nextMoves) {
		
		switch (moves.size) {
		case 1:
			// set current moveset
			moveIcon1.setAnimation(Assets.animationList.get(moves.first().ballType).first());
			moveIcon1.setPosition(stage.getWidth()/2 + moveIconSize/2, stage.getHeight()-100);
			moveIcon1.setSize(nextMoveIconSize, nextMoveIconSize);
			moveIcon1.addAction(sequence(
					scaleTo(nextMoveIconSize/moveIconSize, nextMoveIconSize/moveIconSize),
					parallel(
					moveTo(stage.getWidth()/2 - moveIconSize/2, stage.getHeight()-100, 0.15f, Interpolation.linear),
					scaleTo(moveIconSize/nextMoveIconSize, moveIconSize/nextMoveIconSize, 0.15f, Interpolation.linear)
					)));//TODO won't scale back
			
			
			// set next moveset
			nextMoveIcon1.setAnimation(Assets.animationList.get(nextMoves.first().ballType).first());
			nextMoveIcon1.setPosition(stage.getWidth()/2 + moveIconSize/2, stage.getHeight()-100);
			
			moveIcon2.setVisible(false);
			nextMoveIcon2.setVisible(false);
			
			moveIcon3.setVisible(false);
			nextMoveIcon3.setVisible(false);
			break;
		case 2:
			moveIcon1.setAnimation(Assets.animationList.get(moves.first().ballType).first());
			moveIcon1.setPosition(stage.getWidth()/2 - moveIconSize, stage.getHeight()-100);
			
			moveIcon2.setAnimation(Assets.animationList.get(moves.get(1).ballType).first());
			moveIcon2.setPosition(stage.getWidth()/2, stage.getHeight()-100);
			moveIcon2.setVisible(true);
			
			moveIcon3.setVisible(false);
			break;
			
		case 3:
			moveIcon1.setAnimation(Assets.animationList.get(moves.first().ballType).first());
			moveIcon1.setPosition(stage.getWidth()/2 - moveIconSize, stage.getHeight()-100);
			
			moveIcon2.setAnimation(Assets.animationList.get(moves.get(1).ballType).first());
			moveIcon2.setPosition(stage.getWidth()/2 - moveIconSize/3, stage.getHeight()-100);
			moveIcon2.setVisible(true);
			
			moveIcon3.setAnimation(Assets.animationList.get(moves.get(2).ballType).first());
			moveIcon3.setPosition(stage.getWidth()/2, stage.getHeight()-100);
			moveIcon3.setVisible(true);
			break;
			
		default:
			
			break;
		}
		
		level.getMoveSet().doneRedrawing();
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
		if (level.getMoveSet().needRedrawing()) 
			setMoveIcon(level.getMoveSet().getMoves(), level.getMoveSet().getNextMoves());
	    stage.act(delta);
	    stage.draw();
	}
	
	public void dispose() {
		scoreFont.dispose();
		countFont.dispose();
		stage.dispose();
	}
	
}
