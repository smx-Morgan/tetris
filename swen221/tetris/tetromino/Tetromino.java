// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN221 assignment.
// You may not distribute it in any other way without permission.
package swen221.tetris.tetromino;

import swen221.tetris.logic.Rectangle;

/**
 * An abstract representation of a tetromino which provides various operations
 * for manipulating them, such as rotation and translation.
 *
 * @author David J. Pearce
 * @author Marco Servetto
 */
public interface Tetromino {
	/**
	 * The orientation of a tetromino determines the direction in which it is
	 * facing.
	 *
	 * @author David J. Pearce
	 *
	 */
	public enum Orientation {
		/**
		 * Facing upwards.
		 */
		NORTH,
		/**
		 * Facing right.
		 */
		EAST,
		/**
		 * Facing down.
		 */
		SOUTH,
		/**
		 * Facing left.
		 */
		WEST;

		/**
		 * Rotate a given orientation by a given number of steps in either a clockwise
		 * or anti-clockwise direction.
		 *
		 * @param steps
		 *            Number of steps to rotate in clockwise direction, where negative
		 *            values go in the anti-clockwise direction.
		 * @return Rotated orientation.
		 */
		public Orientation rotate(int steps) {
			Orientation[] values = values();
			int index = ordinal() + steps;
			while (index < 0) {
				index += values.length;
			}
			while (index >= values.length) {
				index -= values.length;
			}
			return values[index];
		}
	}

	/**
	 * Represents the colour of a cell on the tetris board. Each colour is
	 * associated with a "character code" which is used when drawing a textual
	 * representation of the board.
	 */
	public enum Color {
		/**
		 * For red tetrominos.
		 */
		RED,
		/**
		 * For orange tetrominos.
		 */
		ORANGE,
		/**
		 * For yellow tetrominos.
		 */
		YELLOW,
		/**
		 * For green tetrominos.
		 */
		GREEN,
		/**
		 * For blue tetrominos.
		 */
		BLUE,
		/**
		 * For pink tetrominos.
		 */
		MAGENTA,
		/**
		 * For gray tetrominos.
		 */
		DARK_GRAY;
	}

	/**
	 * Get the color of this tetromino.
	 *
	 * @return The colour.
	 */
	public Color getColor();

	/**
	 * Get the orientation of this tetromino.
	 *
	 * @return The orientation.
	 */
	public Orientation getOrientation();

	/**
	 * Check whether a given coordinate on the board is within this tetromino. That
	 * is, whether or not it is one of the locations making up this tetromino. This
	 * method is used, for example, when drawing a tetrmoino. That is, we first
	 * acquire the bounding box for the tetromino and then employ this method to
	 * determine which cells are actually within the tetromino. This method can also
	 * be used to check whether a given tetromino overlaps with another, etc.
	 *
	 * @param x
	 *            The x-coordinate of the position being checked for membership
	 *            within this tetromino.
	 * @param y
	 *            The y-coordinate of the position being checked for membership
	 *            within this tetromino.
	 * @return True if coordinate is within this tetromino, false otherwise.
	 */
	public boolean isWithin(int x, int y);

	/**
	 * Get the bounding box of this tetromino. The bounding box is the smallest
	 * rectangle which fully encloses the tetromino.
	 *
	 * @return A rectangle representing the bounding box.
	 */
	public Rectangle getBoundingBox();

	/**
	 * Rotate this tetromino a given number of steps in a clockwise or
	 * anti-clockwise direction.
	 *
	 * @param steps Number of steps to rotate in clockwise direction, where negative
	 *              values go in the anti-clockwise direction.
	 * @return A new tetromino representing this tetromino after 0 or more clockwise
	 *         rotations.
	 */
	public Tetromino rotate(int steps);

	/**
	 * Get the short name used to describe this tetromino in the notation.
	 *
	 * @return The short name.
	 */
	public String getName();
}