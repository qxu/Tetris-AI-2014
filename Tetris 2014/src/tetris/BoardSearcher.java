package tetris;

import java.util.concurrent.Callable;

public class BoardSearcher implements Callable<BoardScore> {
	final Board board;
	private final Piece piece;
	private final int heightLimit;

	public BoardSearcher(Board b, Piece p, int heightLimit) {
		this.board = b;
		this.piece = p;
		this.heightLimit = heightLimit;
	}

	@Override
	public BoardScore call() {
		return bestBoardScore(board, piece, heightLimit);
	}

	/**
	 * Finds the best move. The best move is found by searching through each
	 * possible move considering the next piece (if present, a depth 2 search)
	 * and calculating the score of the sub-board. The search is done by
	 * iterating through all possible orientations of a piece, then all the
	 * possible x-positions of the piece. The best score is then used to return
	 * the AI's move.
	 */
	private static BoardScore bestBoardScore(Board board, Piece piece, int heightLimit) {
		double bestScore = Double.NEGATIVE_INFINITY;
		int bestX = -1;
		int bestY = -1;
		Piece bestPiece = null;

		Piece cur1 = piece; // the current first piece
		do {
			final int yMax1 = heightLimit - cur1.getHeight() + 1;
			final int xMax1 = board.getWidth() - cur1.getWidth() + 1;

			for (int x1 = 0; x1 < xMax1; x1++) {
				int y1 = board.dropHeight(cur1, x1);
				if ((y1 < yMax1) && board.canPlace(cur1, x1, y1)) {
					Board subBoard1 = new Board(board);
					subBoard1.place(cur1, x1, y1);
					int rowsCleared1 = subBoard1.clearRows();

					double score = getScore(subBoard1, cur1, y1, rowsCleared1,
							heightLimit);

					if (score > bestScore) {
						bestScore = score;
						bestX = x1;
						bestY = y1;
						bestPiece = cur1;
					}
				}
			}

			cur1 = cur1.nextRotation();
		} while (cur1 != piece || !cur1.equals(piece));

		Move bestMove = new Move();
		bestMove.x = bestX;
		bestMove.y = bestY;
		bestMove.piece = bestPiece;

		return new BoardScore(bestMove, bestScore);
	}

	/*
	 * The weight vectors to be used in finding the score of a board.
	 */
	private static final double[] w = { -9.000317650165533, 6.836253620278539,
			-6.4357765736975505, -18.697390610890398, -15.798530854703303,
			-6.771194449452725 };

	/**
	 * Gets the score of a move given a board with the piece added and rows
	 * cleared, and the move height. The score is based on several factors:
	 * <ol>
	 * <li>The height where the center of the piece will land</li>
	 * <li>The number of rows cleared</li>
	 * <li>The number of row transitions</li>
	 * <li>The number of column transitions</li>
	 * <li>The number of holes</li>
	 * <li>The score for all wells</li>
	 * </ol>
	 * 
	 * @param b
	 *            the board
	 * @param p
	 *            the piece added
	 * @param moveHeight
	 *            the height which the bottom of the piece was dropped
	 * @param rowsCleared
	 *            the number of rows cleared
	 * @return the score of the move
	 */
	private static double getScore(Board b, Piece p, int moveHeight,
			int rowsCleared, int heightLimit) {
		double r0 = moveHeight + (p.getHeight() - 1) / 2.0; // the center of the
															// piece is
															// calculated with
															// (height - 1) / 2
		double r1 = rowsCleared;
		double r2 = getRowTransitions(b, heightLimit);
		double r3 = getColumnTransitions(b);
		double r4 = getHoles(b);
		double r5 = getWellScore(b);

		return w[0] * r0 + w[1] * r1 + w[2] * r2 + w[3] * r3 + w[4] * r4 + w[5]
				* r5;
	}

	/**
	 * This method calculates the total well score. A well is an empty space
	 * with occupied cells adjacent to it. This includes holes too since they
	 * are unoccupied and bordered. Each well has a different value relative to
	 * its position on the board. The well's score is calculated by the
	 * following formula: h * (h + 1) / 2 (Triangular Numbers) where h is the
	 * height of the well.
	 * 
	 * @param b
	 *            the Board
	 * @return the well score
	 */
	private static double getWellScore(Board b) {
		int wellScore = 0;
		for (int y = 0; y < b.getColumnHeight(1); y++) {
			if (!b.getGrid(0, y) && b.getGrid(1, y)) {
				wellScore++;
				for (int subY = y - 1; subY >= 0; subY--) {
					if (!b.getGrid(0, subY)) {
						wellScore++;
					} else {
						break;
					}
				}
			}
		}
		for (int x = 1; x < b.getWidth() - 1; x++) {
			int minColHeight = Math.min(b.getColumnHeight(x - 1),
					b.getColumnHeight(x + 1));
			for (int y = 0; y < minColHeight; y++) {
				if (!b.getGrid(x, y) && b.getGrid(x - 1, y)
						&& b.getGrid(x + 1, y)) {
					wellScore++;
					for (int subY = y - 1; subY >= 0; subY--) {
						if (!b.getGrid(x, subY)) {
							wellScore++;
						} else {
							break;
						}
					}
				}
			}
		}
		for (int y = 0; y < b.getColumnHeight(b.getWidth() - 2); y++) {
			if (!b.getGrid(b.getWidth() - 1, y)
					&& b.getGrid(b.getWidth() - 2, y)) {
				wellScore++;
				for (int subY = y - 1; subY >= 0; subY--) {
					if (!b.getGrid(b.getWidth() - 1, subY)) {
						wellScore++;
					} else {
						break;
					}
				}
			}
		}
		return wellScore;
	}

	/**
	 * This method counts the number of holes found within the game-board.
	 * 
	 * @param b
	 *            the Board.
	 * @return the number of empty cells with at least one occupied cell above
	 *         it.
	 */
	private static double getHoles(Board b) {
		int holeCount = 0;
		for (int x = 0; x < b.getWidth(); x++) {
			for (int y = b.getColumnHeight(x) - 2; y >= 0; y--) {
				if (!b.getGrid(x, y)) {
					holeCount++;
				}
			}
		}
		return holeCount;
	}

	/**
	 * This method counts how many horizontal transitions there are on the
	 * board. A transition is when a cell changes from unoccupied to occupied or
	 * vice-versa. The outside below the game-board is considered occupied.
	 * 
	 * @param b
	 *            the Board
	 * @return sum of all vertical transitions
	 */
	private static double getColumnTransitions(Board b) {
		int colTransCount = 0;
		for (int x = 0; x < b.getWidth(); x++) {
			int h = b.getColumnHeight(x);
			if (h > 0) {
				colTransCount++;
			}
			for (int y = h - 2; y >= 0; y--) {
				if (b.getGrid(x, y) != b.getGrid(x, y + 1)) {
					colTransCount++;
				}
			}
			if (!b.getGrid(x, 0)) {
				colTransCount++;
			}
		}
		return colTransCount;
	}

	/**
	 * This method works similarly to getColumnTransitions(Board b). The
	 * difference is that this method returns the total
	 * 
	 * @param b
	 *            the Board
	 * @return sum of all horizontal transitions
	 */
	private static double getRowTransitions(Board b, int heightLimit) {
		int rowTransCount = 0;
		for (int y = heightLimit - 1; y >= 0; y--) {
			if (!b.getGrid(0, y)) {
				rowTransCount++;
			}
			for (int x = 1; x < b.getWidth(); x++) {
				if (b.getGrid(x - 1, y) != b.getGrid(x, y)) {
					rowTransCount++;
				}
			}
			if (!b.getGrid(b.getWidth() - 1, y)) {
				rowTransCount++;
			}
		}
		return rowTransCount;
	}
}
