package com.code2play.quickout;

/**
 * Basic unit of a move in a Level. A move contains a ball type and its state
 * @author Jitrapon
 *
 */
public class Move {

	public int ballType;
	public MoveType moveType;
	public boolean isExcepted;
	
	public Move(int ballType, MoveType moveType, boolean isExcepted) {
		this.ballType = ballType;
		this.moveType = moveType;
		this.isExcepted = isExcepted;
	}
}
