package com.code2play.quickout;

import com.badlogic.gdx.utils.Array;

/**
 * This class abstracts the generation and validation of moves in a Level.
 * @author Jitrapon
 *
 */
public class MoveSet {

	/** All available ball types in this level **/
	private Array<Integer> ballTypes;

	/** All available action types in this level **/
	private Array<MoveType> moveTypes;
	
	/** Currently correct move(s) **/
	private Array<Move> moves;
	
	/** Next correct move(s) **/
	private Array<Move> nextMoves;

	/** Maximum number of ball types in a moveset **/
	private static final int MAX_SIZE = 1;

	/** Current index to be validated **/
	private int index;
	
	/** Indicates whether the currently displayed move is validated to be correct **/
	private boolean correct;
	
	/** Indicates whether move icons need to be redrawn **/
	private boolean redraw;


	public MoveSet() {
		ballTypes = new Array<Integer>( new Integer[]{
				Level.BLUE,
				Level.GREEN,
				Level.RED,
				Level.YELLOW,
//				Level.ANY
		} );
		moves = new Array<Move>(MAX_SIZE);
		nextMoves = new Array<Move>(MAX_SIZE);
		index = 0;
		correct = true;
		redraw = true;
	}
	
	public boolean validate(int ballTag, int ballState) {
		boolean correctMove = false;
		Move move =  moves.get(index);
		if (moveEqualsState( move.moveType, ballState) ) {
			
			// wildcard ball
			if (move.ballType == Level.ANY)
				correctMove = true;
				
			// except this ball
			else if (move.isExcepted && move.ballType != ballTag) 
				correctMove = true;
			
			// normal matching
			else if (!move.isExcepted && move.ballType == ballTag)
				correctMove = true;
			else 
				correctMove = false;
		}
		else 
			correctMove = false;

		// increment or reset index
		if (correctMove)
			index = index+1 >= moves.size ? 0 : index+1;

		correct = correctMove;
		return correctMove;
	}

	/**
	 * Checks if current move type equals to the state of the ball
	 * @param moveType
	 * @param ballState
	 * @return
	 */
	private boolean moveEqualsState(MoveType moveType, int ballState) {
		switch(moveType) {
		case ANY:
			return true;
		case FLING:
			return ballState==Ball.FLINGED;
		case TAP:
			return ballState==Ball.TAPPED;
		default:
			return false;
		}
	}
	
	public void setMoveset(boolean forceChange) {
		if (correct || forceChange) {
			moves.clear();

			for (int i = 0; i < MAX_SIZE; i++) {
				if (nextMoves.size == 0) 
					moves.add( new Move(ballTypes.random(), MoveType.TAP, false) );
				else
					moves.add(nextMoves.get(i));
			} 
			index = 0;
			setNextMoveSet(forceChange);
			correct = false;
			redraw = true;							// need this because cannot call GameHud functions here, 
													// since it is not initialized yet on the first call
		}
	}
	
	public boolean isCorrect() {
		return correct;
	}
	
	private void setNextMoveSet(boolean forceChange) {
		if (correct || forceChange) {
			nextMoves.clear();
			
			for (int i = 0; i < MAX_SIZE; i++) {
				nextMoves.add( new Move(ballTypes.random(), MoveType.TAP, false) );
			}
		}
	}

	public Array<Move> getMoves() {
		return moves;
	}
	
	public Array<Move> getNextMoves() {
		return nextMoves;
	}
	
	public boolean needRedrawing() {
		return redraw;
	}
	
	public void doneRedrawing() {
		redraw = false;
	}
}
