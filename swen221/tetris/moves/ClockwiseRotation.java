// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN221 assignment.
// You may not distribute it in any other way without permission.
package swen221.tetris.moves;

import swen221.tetris.logic.Board;
import swen221.tetris.tetromino.ActiveTetromino;
import swen221.tetris.tetromino.Tetromino;

/**
 * Implements a rotation move which is either clockwise or anti-clockwise.
 *
 * @author David J. Pearce
 * @author Marco Servetto
 *
 */
public class ClockwiseRotation extends AbstractMove implements Move {

	@Override
	public Board apply(Board board) {
		// Create copy of the board to prevent modifying its previous state.
		board = new Board(board);

		ActiveTetromino tetromino = board.getActiveTetromino();
		// Create a copy of this board which will be updated.

		tetromino = board.getActiveTetromino().rotate(1);


		// Apply the move to the new board, rather than to this board.
		board.setActiveTetromino(tetromino);
		// Return updated version of this board.
		return board;
	}

	@Override
	public boolean isValid(Board board) {
		if(!super.isValid(board))
			return false;
		// make a copy of the board, otherwise it will break the original rules
		Board boardCopy = new Board(board);

		ActiveTetromino tetromino = boardCopy.getActiveTetromino().rotate(1);

		// Apply the move to the new board, rather than to this board.
		boardCopy.setActiveTetromino(tetromino);

		// check if it is out side the broad
		if(CheckOutside(tetromino,board))
			return false;
		return true;
	}
	public boolean CheckOutside(ActiveTetromino activeTetromino , Board board) {
		// if left edge is vaidld return true
		if(activeTetromino.getBoundingBox().getMinX() < 0) return true;

		// if right edge is vaidld return true
		if(activeTetromino.getBoundingBox().getMaxX() >= board.getWidth()) return true;

		// if bottom edge is vaidld return true
		if(activeTetromino.getBoundingBox().getMinY() < 0) return true;

		return false;
	}
}
