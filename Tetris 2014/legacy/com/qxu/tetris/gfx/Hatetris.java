package com.qxu.tetris.gfx;

import java.util.List;

import com.qxu.tetris.TetrisBlock;
import com.qxu.tetris.TetrisGrid;
import com.qxu.tetris.Tetromino;
import com.qxu.tetris.ai.AIMove;
import com.qxu.tetris.ai.TetrisAI;
import com.qxu.tetris.ai.newscores.ColumnTransitions;
import com.qxu.tetris.ai.newscores.Holes;
import com.qxu.tetris.ai.newscores.RowTransitions;
import com.qxu.tetris.ai.newscores.Wells;

public class Hatetris {
	private static final TetrisRunner runner = new TetrisRunner(20, 10, false) {
		@Override
		public Tetromino getNewTetromino() {
			return worstPiece(grid);
		}
		@Override
		protected boolean turnLoop() {
			boolean cont = super.turnLoop();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return cont;
		};
	};
	
	public static void main(String[] args) {
		runner.seekSize = 0;
		runner.ai = new TetrisAI() {
			@Override
			public AIMove getMove(TetrisGrid grid, Tetromino t, List<Tetromino> next) {
				AIMove move = searchMove(grid, t, 1);
				runner.comp.setGrid(grid);
				return move;
			}
		};
		runner.run();
		System.out.println("terminated.");
	}

	private static final double[] w = new double[] { -4.500158825082766,
			3.4181268101392694, -3.2178882868487753, -9.348695305445199,
			-7.899265427351652, -3.3855972247263626 };

	private static double getScore(TetrisGrid grid, TetrisBlock block,
			int moveHeight, int rowsCleared) {
		double lh = moveHeight + (block.getHeight() - 1) / 2.0;
		int re = rowsCleared;
		int rt = RowTransitions.getRowTransitions(grid);
		int ct = ColumnTransitions.getColumnTransitions(grid);
		int ho = Holes.getHoles(grid);
		int ws = Wells.getWellSumsEl(grid);
		return w[0] * lh + w[1] * re + w[2] * rt + w[3] * ct + w[4] * ho + w[5]
				* ws;
	}
	
	private static AIMove searchMove(TetrisGrid grid, Tetromino t, int depth) {
		double bestScore = Double.NEGATIVE_INFINITY;
		
		int bestColumn = -1;
		int bestOrientation = -1;
		
		List<TetrisBlock> blocks = t.getBlockChain();
		for (int or = 0; or < blocks.size(); or++) {
			TetrisBlock block = blocks.get(or);
			int maxCol = grid.getWidth() - block.getWidth();
			for (int c = 0; c <= maxCol; c++) {
				int row = grid.getDropRow(c, block);
				if (row + block.getHeight() <= grid.getHeight()) {
					TetrisGrid subGrid = new TetrisGrid(grid);
					subGrid.addBlock(row, c, block);
					subGrid.clearFullRows();
					
					runner.comp.setGrid(subGrid);

					Tetromino next = worstPiece(subGrid);
					double subScore = search(subGrid, next, depth - 1);
					if (subScore > bestScore) {
						bestScore = subScore;
						bestColumn = c;
						bestOrientation = or;
					}
				}
			}
		}
		return new AIMove(bestColumn, bestOrientation);
	}

	private static double search(TetrisGrid grid, Tetromino t, int depth) {
		double bestScore = Double.NEGATIVE_INFINITY;

		List<TetrisBlock> blocks = t.getBlockChain();
		for (int or = 0; or < blocks.size(); or++) {
			TetrisBlock block = blocks.get(or);
			int maxCol = grid.getWidth() - block.getWidth();
			for (int c = 0; c <= maxCol; c++) {
				int row = grid.getDropRow(c, block);
				if (row + block.getHeight() <= grid.getHeight()) {
					TetrisGrid subGrid = new TetrisGrid(grid);
					subGrid.addBlock(row, c, block);
					int rowsCleared = subGrid.clearFullRows();
					
					runner.comp.setGrid(subGrid);

					Tetromino next = worstPiece(subGrid);
					double subScore;
					if (depth <= 1) {
						subScore = getScore(subGrid, block, row, rowsCleared);
					} else {
						subScore = search(subGrid, next, depth - 1);
					}
					if (subScore > bestScore) {
						bestScore = subScore;
					}
				}
			}
		}
		return bestScore;
	}

	private static final int searchDepth = 0;
	private static final Tetromino[] tetrominoes = { Tetromino.S, Tetromino.Z,
			Tetromino.O, Tetromino.I, Tetromino.L, Tetromino.J, Tetromino.T };

	public static Tetromino worstPiece(TetrisGrid grid) {
		double worstRating = Double.POSITIVE_INFINITY;
		Tetromino worstPiece = null;

		for (Tetromino t : tetrominoes) {
			double currentRating = bestRating(grid, t, searchDepth);

			if (currentRating < worstRating) {
				worstRating = currentRating;
				worstPiece = t;
			}
		}
		return worstPiece;
	}

	private static double bestRating(TetrisGrid grid, Tetromino t,
			int searchDepth) {
		double bestRating = 0;

		List<TetrisBlock> blocks = t.getBlockChain();
		for (int or = 0; or < blocks.size(); or++) {
			TetrisBlock block = blocks.get(or);
			int maxCol = grid.getWidth() - block.getWidth();
			for (int c = 0; c <= maxCol; c++) {
				int row = grid.getDropRow(c, block);
				if (row + block.getHeight() <= grid.getHeight()) {
					TetrisGrid subGrid = new TetrisGrid(grid);
					subGrid.addBlock(row, c, block);
					subGrid.clearFullRows();

					double currentRating = getRating(subGrid);
					if (searchDepth > 0) {
						currentRating += worstPieceRating(subGrid,
								searchDepth - 1) / 100.0;
					}

					if (currentRating > bestRating) {
						bestRating = currentRating;
					}
				}
			}
		}
		return bestRating;
	}

	private static double worstPieceRating(TetrisGrid grid, int searchDepth) {
		double worstRating = Double.POSITIVE_INFINITY;
		for (Tetromino t : tetrominoes) {
			double currentRating = bestRating(grid, t, searchDepth);
			if (currentRating < worstRating) {
				worstRating = currentRating;
			}

			if (worstRating == 0) {
				return 0;
			}
		}
		return worstRating;
	}

	private static double getRating(TetrisGrid grid) {
		int highestBlue = grid.getHeight();
		for (int c = 0; c < grid.getWidth(); c++) {
			int blue = grid.getHeight() - grid.getColumnHeight(c);
			if (blue < highestBlue) {
				highestBlue = blue;
			}
		}
		return highestBlue;
	}
}
