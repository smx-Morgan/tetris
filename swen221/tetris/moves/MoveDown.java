package swen221.tetris.moves;

import swen221.tetris.logic.Board;
import swen221.tetris.tetromino.ActiveTetromino;

/**
 * Move the active tetromino one square downwards.
 *
 * @author David J. Pearce
 *
 */

public class MoveDown extends AbstractTranslation {
	/**
	 * Construct a new move down translation.
	 */
	public MoveDown() {
		super(0,-1);
	}
	@Override
	public boolean isValid(Board board) {
		ActiveTetromino tetromino = board.getActiveTetromino();

		if(!super.isValid(board)){
			return false;
		}

		if(board.CheckLanded(tetromino)){
			return false;
		}
		return true;
	}
}
