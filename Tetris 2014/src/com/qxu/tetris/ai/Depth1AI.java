package com.qxu.tetris.ai;

import java.util.List;

import com.qxu.tetris.TetrisBlock;
import com.qxu.tetris.TetrisGrid;
import com.qxu.tetris.Tetromino;
import com.qxu.tetris.ai.newscores.ColumnTransitions;
import com.qxu.tetris.ai.newscores.Holes;
import com.qxu.tetris.ai.newscores.RowTransitions;
import com.qxu.tetris.ai.newscores.Wells;

public class Depth1AI implements TetrisAI {
	@Override
	public AIMove getMove(TetrisGrid grid, Tetromino t, List<Tetromino> next) {
		double bestScore = Double.NEGATIVE_INFINITY;

		int bestColumn = -1;
		int bestOrientation = -1;

		List<TetrisBlock> blocks = t.getBlockChain();
		for (int or = 0; or < blocks.size(); or++) {
			TetrisBlock block = blocks.get(or);
			int maxCol = grid.getWidth() - block.getWidth();
			for (int c = 0; c <= maxCol; c++) {
				int row = grid.getDropRow(c, block);
				if (row + block.getHeight() > grid.getHeight()) {
					continue;
				}
				TetrisGrid subGrid = new TetrisGrid(grid);
				int rowsCleared = subGrid.addAndClearRows(row, c, block);

				double score = getScore(subGrid, block, row, rowsCleared);

				if (score > bestScore) {
					bestScore = score;
					bestColumn = c;
					bestOrientation = or;
				}
			}
		}

		return (bestColumn >= 0) ? new AIMove(bestColumn, bestOrientation)
				: null;
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
}
