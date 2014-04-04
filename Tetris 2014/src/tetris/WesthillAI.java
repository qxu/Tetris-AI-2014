package tetris;

import java.util.Arrays;

import com.qxu.tetris.gfx.GridDebugger;

import AIHelper.BoardRater;

public class WesthillAI implements AI {
	private GridDebugger debugger = new GridDebugger();
	
	@Override
	public Move bestMove(Board board, Piece piece, Piece nextPiece,
			int limitHeight) {
		System.out.println("current piece: " + piece);
		System.out.println("next piece: " + nextPiece);
		
//		nextPiece = null;

		double bestScore = Double.NEGATIVE_INFINITY;
		int bestX = -1;
		int bestY = -1;
		Piece bestPiece = null;
		
		Piece cur1 = piece;
		do {
			final int yMax1 = limitHeight - cur1.getHeight() + 1;
			final int xMax1 = board.getWidth() - cur1.getWidth() + 1;

			for (int x1 = 0; x1 < xMax1; x1++) {
				int y1 = board.dropHeight(cur1, x1);
				if ((y1 < yMax1) && board.canPlace(cur1, x1, y1)) {
					Board subBoard1 = new Board(board);
					subBoard1.place(cur1, x1, y1);
					int rowsCleared1 = subBoard1.clearRows();

					if (nextPiece != null) {
						Piece cur2 = nextPiece;
						do {
							final int yMax2 = limitHeight - cur2.getHeight()
									+ 1;
							final int xMax2 = subBoard1.getWidth()
									- cur2.getWidth() + 1;

							for (int x2 = 0; x2 < xMax2; x2++) {
								int y2 = subBoard1.dropHeight(cur2, x2);
								if ((y2 < yMax2)
										&& subBoard1.canPlace(cur2, x2, y2)) {
									Board subBoard2 = new Board(subBoard1);
									subBoard2.place(cur2, x2, y2);
									int rowsCleared2 = subBoard2.clearRows();

									double score = getScore(subBoard2, cur2,
											y2, rowsCleared1 + rowsCleared2);

									if (score > bestScore) {
										bestScore = score;
										bestX = x1;
										bestY = y1;
										bestPiece = cur1;
									}
								}
							}

							cur2 = cur2.nextRotation();
						} while (cur2 != nextPiece || !cur2.equals(nextPiece));
					} else {
						double score = getScore(subBoard1, cur1, y1,
								rowsCleared1);

						if (score > bestScore) {
							bestScore = score;
							bestX = x1;
							bestY = y1;
							bestPiece = cur1;
						}
					}
				}
			}

			cur1 = cur1.nextRotation();
		} while (cur1 != piece || !cur1.equals(piece));

		Move move = new Move();
		move.x = bestX;
		move.y = bestY;
		move.piece = bestPiece;
		
		debugger.getComp().setBoard(board);
		debugger.getComp().setMovePiece(bestPiece, bestX, bestY);
		debugger.getComp().repaint();
		debugger.getNextComp().setNext(Arrays.asList(nextPiece));
		debugger.getNextComp().repaint();

		return move;
	}

	private static final double[] w = new double[] { -4.500158825082766,
			3.4181268101392694, -3.2178882868487753, -9.348695305445199,
			-7.899265427351652, -3.3855972247263626 };

	private static double getScore(Board b, Piece p, int moveHeight,
			int rowsCleared) {
		double r0 = moveHeight + (p.getHeight() - 1) / 2.0;
		double r1 = rowsCleared;
		double r2 = getRowTransitions(b);
		double r3 = getColumnTransitions(b);
		double r4 = getHoles(b);
		double r5 = getWellSumsEl(b);
		return w[0] * r0 + w[1] * r1 + w[2] * r2 + w[3] * r3 + w[4] * r4 + w[5]
				* r5;
	}
	
	private static int getHeight(Board b) {
		return 20;
	}

	private static double getWellSumsEl(Board b) {
		int nr = 0;
		for (int j = 0; j < getHeight(b); j++) {
			if (!b.getGrid(0, j) && b.getGrid(1, j)) {
				nr++;
				for (int k = j - 1; k >= 0; k--) {
					if (!b.getGrid(0, k)) {
						nr++;
					} else {
						break;
					}
				}
			}
			for (int i = 1; i < b.getWidth() - 1; i++) {
				if (!b.getGrid(i, j) && b.getGrid(i - 1, j)
						&& b.getGrid(i + 1, j)) {
					nr++;
					for (int k = j - 1; k >= 0; k--) {
						if (!b.getGrid(i, k)) {
							nr++;
						} else {
							break;
						}
					}
				}
			}
			if (!b.getGrid(b.getWidth() - 1, j)
					&& b.getGrid(b.getWidth() - 2, j)) {
				nr++;
				for (int k = j - 1; k >= 0; k--) {
					if (!b.getGrid(b.getWidth() - 1, j)) {
						nr++;
					} else {
						break;
					}
				}
			}
		}
		return nr;
	}

	private static double getHoles(Board b) {
		int count = 0;
		for (int c = 0; c < b.getWidth(); c++) {
			for (int r = b.getColumnHeight(c) - 2; r >= 0; r--) {
				if (!b.getGrid(c, r)) {
					count++;
				}
			}
		}
		return count;
	}

	private static double getColumnTransitions(Board b) {
		int count = 0;
		for (int c = 0; c < b.getWidth(); c++) {
			int h = b.getColumnHeight(c);
			if (h > 0) {
				count++;
			}
			for (int r = h - 2; r >= 0; r--) {
				if (b.getGrid(c, r) != b.getGrid(c, r + 1)) {
					count++;
				}
			}
			if (!b.getGrid(c, 0)) {
				count++;
			}
		}
		return count;
	}

	private static double getRowTransitions(Board b) {
		int count = 0;
		for (int r = getHeight(b) - 1; r >= 0; r--) {
			if (!b.getGrid(0, r)) {
				count++;
			}
			for (int c = 1; c < b.getWidth(); c++) {
				if (b.getGrid(c - 1, r) != b.getGrid(c, r)) {
					count++;
				}
			}
			if (b.getWidth() >= 2 && !b.getGrid(b.getWidth() - 1, r)) {
				count++;
			}
		}
		return count;
	}

	@Override
	public void setRater(BoardRater r) {
		throw new UnsupportedOperationException();
	}
}