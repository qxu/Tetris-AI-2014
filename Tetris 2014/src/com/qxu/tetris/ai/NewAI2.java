package com.qxu.tetris.ai;

import java.util.List;

import com.qxu.tetris.TetrisBlock;
import com.qxu.tetris.TetrisGrid;
import com.qxu.tetris.Tetromino;
import com.qxu.tetris.ai.newscores.ColumnTransitions;
import com.qxu.tetris.ai.newscores.Holes;
import com.qxu.tetris.ai.newscores.RowTransitions;
import com.qxu.tetris.ai.newscores.Wells;

public class NewAI2 implements TetrisAI {
	@Override
	public AIMove getMove(TetrisGrid grid, Tetromino t, List<Tetromino> next) {
		double bestScore = Double.NEGATIVE_INFINITY;

		int bestColumn = -1;
		int bestOrientation = -1;

		List<TetrisBlock> blocks = t.getBlockChain();
		for (int or = 0; or < blocks.size(); or++) {
			TetrisBlock block = blocks.get(or);
			int maxCol = grid.getWidth() - block.getData().getWidth();
			for (int c = 0; c <= maxCol; c++) {
				int row = grid.getDropRow(c, block);
				if (row + block.getData().getHeight() <= grid.getHeight()) {
					TetrisGrid subGrid1 = new TetrisGrid(grid);
					subGrid1.addBlock(row, c, block);
					int rowsCleared = subGrid1.clearFullRows();

					double score = getScore(subGrid1, block, row, rowsCleared);

					if (score > bestScore) {
						bestScore = score;
						bestColumn = c;
						bestOrientation = or;
					}
				}
			}
		}

		return (bestColumn >= 0) ? new AIMove(bestColumn, bestOrientation)
				: null;
	}

	private static final double[] w = new double[] { 2382, -135028, -48491,
			-74343, 59739, -72528, -61591, 13344, -19737, -47653, -50015,
			-144688 };

	private static final double[] e = { 0.935, 1.839, 0.905, 1.078, 0.858,
			2.207, 1.565, 0.023, 0.117, 0.894, 1.474, 1.346 };

	private static double getScore(TetrisGrid grid, TetrisBlock block,
			int moveHeight, int rowsCleared) {
		double lh = moveHeight + (block.getData().getHeight() - 1) / 2.0;
		int re = rowsCleared;
		int rt = RowTransitions.getRowTransitionCount(grid);
		int ct = ColumnTransitions.getColumnTransitionCount(grid);
		int ho = Holes.getHoles(grid);
		int ws = Wells.getWellSumsEl(grid);
		return w[0] * lh + w[1] * re + w[2] * rt + w[3] * ct + w[4] * ho + w[5]
				* ws;

	}
}
