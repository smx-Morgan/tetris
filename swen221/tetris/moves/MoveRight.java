package swen221.tetris.moves;

import swen221.tetris.logic.Board;
import swen221.tetris.tetromino.ActiveTetromino;

/**
 * Move the active tetromino one square to the right.
 *
 * @author David J. Pearce
 *
 */

public class MoveRight extends AbstractTranslation {
	/**
	 * Construct a new move right translation.
	 */
	public MoveRight() {
		super(1,0);
	}

	@Override
	public boolean isValid(Board board) {
		ActiveTetromino tetromino = board.getActiveTetromino();
		try{
			if(tetromino.getBoundingBox().getMaxX() >= board.getWidth() - 1){
				return false;
			}

			if(!super.isValid(board)){
				System.out.println("right invid");
				return false;
			}

			if(board.CheckTouch2(tetromino)){
				return false;
			}
		}catch(NullPointerException e){
			return false;
		}

		return true;
	}
}
