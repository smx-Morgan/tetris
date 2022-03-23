// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN221 assignment.
// You may not distribute it in any other way without permission.
package swen221.tetris.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import swen221.tetris.logic.Game;
import swen221.tetris.moves.*;
import swen221.tetris.tetromino.*;

/**
 * Tetris Define a Gui allowing to play. This code is quite advanced and uses
 * features that you will learn in the later part of Swen221; do no worry if
 * there are certain parts that you can not understand.
 *
 * @author David J. Pearce
 * @author Marco Servetto
 */
@SuppressWarnings("serial")
public class Tetris extends JFrame {

	/**
	 * Main entry point for playing the game. This is responsible for creating the
	 * Graphical User Interface, accepting input from the user and running the game.
	 *
	 * @param args Command-line arguments (which are ignored).
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			Tetris frame = new Tetris();
			Game game = new Game(new TetrominoList(), 10, 20);
			//
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.getRootPane().setLayout(new BorderLayout());
			JPanel display = createDisplayCanvas(game);
			JPanel next = createNextTetrominoPanel(game);
			JPanel panel = createInfoPanel(game, next);
			frame.getRootPane().add(display, BorderLayout.CENTER);
			frame.getRootPane().add(panel, BorderLayout.EAST);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowOpened(WindowEvent e) {
					display.requestFocus();
				}
			});
			frame.pack();
			frame.setVisible(true);
			// to make the cells update their color
			display.revalidate();
			next.revalidate();
			// Construct the "clock"
			ClockThread clock = new ClockThread(game, frame, display, next);
			// Start the clock
			clock.start();
		});
	}

	/**
	 * Create canvas on which Tetris game is drawn, and from which keyboard input is
	 * received.
	 *
	 * @param game The game of tetris currently being played.
	 * @return JPanel representing the canvas.
	 */
	public static JPanel createDisplayCanvas(Game game) {
		// Initialise the game
		// Create the display itself
		JPanel display = new JPanel();
		display.setLayout(new GridLayout(20, 10, 1, 1));
		for (int row = 0; row < 20; row += 1) {
			for (int col = 0; col < 10; col += 1) {
				display.add(cell(col, 19 - row, game));
			}
		}
		display.setFocusable(true);
		// Add key listeners
		display.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// NOTE: Multiple threads invoke on the game (Clock Thread and
				// AWT Threads). Hence, need to synchronise here.
				synchronized (game) {
					// Check whether active tetromino exists which we can control
					if (game.getActiveBoard().getActiveTetromino() != null) {
						switch (e.getKeyCode()) {
						case KeyEvent.VK_UP:
							game.apply(new ClockwiseRotation());
							break;
						case KeyEvent.VK_DOWN:
							game.apply(new MoveDown());
							break;
						case KeyEvent.VK_LEFT:
							game.apply(new MoveLeft());
							break;
						case KeyEvent.VK_RIGHT:
							game.apply(new MoveRight());
							break;
						case KeyEvent.VK_SPACE:
							game.apply(new DropMove());
							break;
						}
						display.revalidate();
						display.repaint();
					}
				}
			}
		});
		// Done
		return display;
	}

	/**
	 * Create the information panel which shows the current score, the game level
	 * and also what the next Tetromino is.
	 *
	 * @param game               The game of tetris currently being played.
	 * @param nextTetrominoPanel The panel to use for showing the next tetromino.
	 * @return The info panel.
	 */
	public static JPanel createInfoPanel(Game game, JPanel nextTetrominoPanel) {
		JPanel panel = new JPanel();
		// Set padding to make it look nicer
		panel.setBorder(new EmptyBorder(20, 20, 20, 20));
		// Create score information
		Component[] bits = { nextTetrominoPanel, Box.createVerticalGlue(), new JLabel("Score"), new JLabel() {
			@Override
			public String getText() {
				return String.format("%04d", game.getScore());
			}
		}, Box.createVerticalGlue(), new JLabel("Level"), new JLabel() {
			@Override
			public String getText() {
				return String.format("%04d", game.getLines() / 10);
			}
		}, Box.createVerticalGlue(), new JLabel("Lines"), new JLabel() {
			@Override
			public String getText() {
				return String.format("%04d", game.getLines());
			}
		}, };
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		//
		for (Component c : bits) {
			panel.add(c);
			if (c instanceof JLabel) {
				JLabel l = (JLabel) c;
				// center labels
				l.setAlignmentX(Component.CENTER_ALIGNMENT);
				// increase size of label fonts
				Font f = l.getFont();
				l.setFont(new Font(f.getName(), Font.BOLD, 24));
			}
		}
		//
		return panel;
	}

	/**
	 * Create a panel in which the next Tetromino to appear can be displayed.
	 *
	 * @param game The game of tetris currently being played.
	 * @return The panel.
	 */
	public static JPanel createNextTetrominoPanel(Game game) {
		// Initialise the game
		// Create the display itself
		JPanel display = new JPanel();
		// Add border
		display.setBorder(new LineBorder(Color.BLACK, 5));
		//
		display.setLayout(new GridLayout(4, 5, 1, 1));
		for (int row = 2; row >= -1; row -= 1) {
			for (int col = -2; col < 3; col += 1) {
				display.add(nextCell(col, row, game));
			}
		}
		//
		display.setVisible(true);
		//
		return display;
	}

	/**
	 * Create a cell in the Tetris visualization which uses the current game to
	 * chose its color.
	 *
	 * @param col  Grid column for Cell.
	 * @param row  Grid row for Cell.
	 * @param game The game of tetris currently being played.
	 * @return The cell panel.
	 */
	public static JPanel cell(int col, int row, Game game) {
		return new JPanel() {
			{
				this.setLayout(null);
			}

			@Override
			public void validate() {
				synchronized (game) {
					Tetromino t = game.getActiveBoard().getTetrominoAt(col, row);
					this.setBackground(toAwtColor(t));
					super.validate();
				}
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(30, 30);
			}
		};
	}

	/**
	 * Create a cell in the Tetris visualization which uses the current game to
	 * chose its color. More specifically, this uss the next tetromino to choose its
	 * colour and, hence, is suitable for displaying the next tetromino.
	 *
	 * @param col  Grid column for Cell.
	 * @param row  Grid row for Cell.
	 * @param game The game of tetris currently being played.
	 * @return The cell panel.
	 */
	public static JPanel nextCell(int col, int row, Game game) {
		return new JPanel() {
			{
				this.setLayout(null);
			}

			@Override
			public void validate() {
				synchronized (game) {
					Tetromino t = game.getNextTetromino();
					Color c = t.isWithin(col, row) ? toAwtColor(t) : Color.white;
					this.setBackground(c);
					super.validate();
				}
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(30, 30);
			}
		};
	}

	/**
	 * Convert a Tetromino to an AWT color.
	 *
	 * @param tetromino The Tetromino whose colour we are converting to an AWT
	 *                  colour.
	 * @return Return the appropriate color of the tetromino.
	 */
	private static Color toAwtColor(Tetromino tetromino) {
		if (tetromino == null) {
			return java.awt.Color.WHITE;
		} else {
			switch (tetromino.getColor()) {
			case RED:
				return java.awt.Color.RED;
			case ORANGE:
				return java.awt.Color.ORANGE;
			case YELLOW:
				return java.awt.Color.YELLOW;
			case GREEN:
				return java.awt.Color.GREEN;
			case BLUE:
				return java.awt.Color.BLUE;
			case MAGENTA:
				return java.awt.Color.MAGENTA;
			case DARK_GRAY:
			default:
				return java.awt.Color.DARK_GRAY;
			}
		}
	}

	/**
	 * Represents a random sequence of Tetromino which will be used to select the
	 * upcoming Tetromino from during the game.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class TetrominoList implements Iterator<Tetromino> {
		/**
		 * Provides access to a simple random number generator.
		 */
		private Random random = new Random(System.currentTimeMillis());

		@Override
		public boolean hasNext() {
			// This is an infinite stream
			return true;
		}

		@Override
		public Tetromino next() {
			Tetromino.Color[] colors = Tetromino.Color.values();
			int tetromino = random.nextInt(7);
			// NOTE: -1 here because BLACK is reserved for being stuck.
			int color = random.nextInt(colors.length - 1);
			switch (tetromino) {
			case 0:
				return new Z_Tetromino(Tetromino.Orientation.NORTH, colors[color]);
			case 1:
				return new S_Tetromino(Tetromino.Orientation.NORTH, colors[color]);
			case 2:
				return new T_Tetromino(Tetromino.Orientation.NORTH, colors[color]);
			case 3:
				return new J_Tetromino(Tetromino.Orientation.NORTH, colors[color]);
			case 4:
				return new L_Tetromino(Tetromino.Orientation.NORTH, colors[color]);
			case 5:
				return new O_Tetromino(colors[color]);
			case 6:
				return new I_Tetromino(Tetromino.Orientation.NORTH, colors[color]);
			default:
				throw new IllegalArgumentException("invalid tetromino");
			}
		}
	}

	/**
	 * The Clock Thread is responsible for producing a consistent "pulse" which is
	 * used to fire a downwards move to the game on every cycle.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class ClockThread extends Thread {
		/**
		 * Identifies the game currently being played.
		 */
		private final Game game;
		/**
		 * Identifies the Graphical User Interface for the game currently being played.
		 */
		private final JFrame frame;
		/**
		 * Identifies various panels within the graphical user interface.
		 */
		private final JPanel[] panels;
		/**
		 * Identifies the current game clock.
		 */
		private volatile int delayMillis; // delay between ticks in ms

		/**
		 * Create a new clock thread to manage the game.
		 *
		 * @param game   Current game being played.
		 * @param frame  Graphical User Interface of game being played.
		 * @param panels Various subpanels for GUI.
		 */
		public ClockThread(Game game, JFrame frame, JPanel... panels) {
			this.game = game;
			this.frame = frame;
			this.panels = panels;
		}

		@Override
		public void run() {
			while (1 == 1) {
				// Loop forever
				try {
					Thread.sleep(delayMillis);
					//
					boolean gameOver = false;
					int lines;
					// NOTE: multiple threads invoke on the game (Clock Thread and
					// AWT Threads). Hence, need to synchronise here.
					synchronized (game) {
						// apply gravity, etc.
						game.clock();
						// check whether reached game over state
						gameOver = game.isGameOver();
						//
						lines = game.getLines();
					}
					// NOTE: cannot invoke gameOver() inside synchronised block as this can cause a
					// deadlock with an AWT thread.
					if (gameOver) {
						gameOver();
					} else {
						this.delayMillis = calculateDelayMillis(lines);
					}
					// update the display
					for (JPanel p : panels) {
						p.revalidate();
					}
					frame.repaint();
				} catch (InterruptedException e) {
					// If we get here, then something wierd happened. It doesn't matter, we can just
					// ignore this and continue.
				}
			}
		}

		/**
		 * Handle a game over event.
		 */
		private void gameOver() {
			int choice = JOptionPane.showConfirmDialog(null, "GameOver! Would you play again?", "Select an option...",
					JOptionPane.YES_NO_OPTION);
			if (choice == 0) {
				// NOTE: Multiple threads invoke on the game (Clock Thread and
				// AWT Threads). Hence, need to synchronise here.
				synchronized (game) {
					game.reset();
				}
			} else {
				System.exit(0);
			}
		}

		/**
		 * This calculates the speed at which the game should be clocked, which is
		 * determined by the current level.
		 *
		 * @param lines The number of lines which have been successfully removed by the
		 *              player.
		 * @return The calculated delay (in ms).
		 */
		private int calculateDelayMillis(int lines) {
			// A quick game is a good game :)
			int level = lines / 10;
			int delta = Math.max(level, 4) * 100;
			delta = Math.min(level - 4, 0) * 10;
			// ensure don't go below 10
			return Math.max(10, 500 - delta);
		}
	}
}