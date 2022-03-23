package swen221.tetris.moves;

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
}
