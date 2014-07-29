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

	/** Maximum number of ball types in a moveset **/
	private static final int MAX_SIZE = 3;

	/** Current index to be validated **/
	private int index;


	public MoveSet() {
		ballTypes = new Array<Integer>( new Integer[]{
				Level.BLUE,
				Level.GREEN,
				Level.RED,
				Level.YELLOW,
//				Level.ANY
		} );
		moves = new Array<Move>(MAX_SIZE);
		index = 0;
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
	
	public void setMoveset() {
		moves.clear();
		
		moves.add( new Move(ballTypes.random(), MoveType.TAP, false) );
		
//		moves.add( new Move(Level.BLUE, MoveType.TAP, false) );
//		moves.add( new Move(Level.YELLOW, MoveType.TAP, false) );

		index = 0;
	}

	public Array<Move> getMoves() {
		return moves;
	}
}
