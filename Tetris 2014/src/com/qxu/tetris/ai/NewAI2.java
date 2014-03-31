package com.qxu.tetris.ai;

import java.util.List;

import com.qxu.tetris.TetrisBlock;
import com.qxu.tetris.TetrisGrid;
import com.qxu.tetris.Tetromino;
import com.qxu.tetris.ai.newscores.AltitudeDifference;
import com.qxu.tetris.ai.newscores.Blocks;
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
			int maxCol = grid.getWidth() - block.getWidth();
			for (int c = 0; c <= maxCol; c++) {
				int row = grid.getDropRow(c, block);
				if (row + block.getHeight() > grid.getHeight()) {
					continue;
				}
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

		return (bestColumn >= 0) ? new AIMove(bestColumn, bestOrientation)
				: null;
	}

	private static final double[] w = new double[] { 2382, -135028, -48491,
			-74343, 59739, -72528, -61591, 13344, -19737, -47653, -50015,
			-144688 };

	private static final double[] e = { 0.935, 1.839, 0.905, 1.078, 0.858,
			2.207, 1.565, 0.023, 0.117, 0.894, 1.474, 1.346 };

	/*private static double getScore(TetrisGrid grid, TetrisBlock block,
			int moveHeight, int rowsCleared) {
		double lh = moveHeight + (block.getHeight() - 1) / 2.0;
		int re = rowsCleared;
		int rt = RowTransitions.getRowTransitions(grid);
		int ct = ColumnTransitions.getColumnTransitions(grid);
		int ho = Holes.getHoles(grid);
		int ws = Wells.getWellSumsEl(grid);
		return w[0] * lh + w[1] * re + w[2] * rt + w[3] * ct + w[4] * ho + w[5]
				* ws;

	}*/
	private static double getScore(TetrisGrid grid, TetrisBlock block, int moveHeight, int rowsCleared){//exponential
		int ph = AltitudeDifference.getPileHeight(grid);
		int h = Holes.getHoles(grid);
		int ch = Holes.getConnectedHoles(grid);
		int rl = rowsCleared;
		int ad = AltitudeDifference.getAltitudeDifference(grid);
		int mwd = Wells.getMaxWell(grid);
		int ws = Wells.getWellSums(grid);
		double lh = moveHeight + (block.getHeight() - 1) / 2.0;
		int b = Blocks.getNumOfBlocks(grid);
		int wb = Blocks.getWeightedBlocks(grid);
		int rt = RowTransitions.getRowTransitions(grid);
		int ct = ColumnTransitions.getColumnTransitions(grid);
		return w[0]*Math.pow(ph,e[0]) + w[1]*Math.pow(h,e[1]) + w[2]*Math.pow(ch,e[2])
				+ w[3]*Math.pow(rl,e[3]) + w[4]*Math.pow(ad,e[4]) + w[5]*Math.pow(mwd,e[5])
				+ w[6]*Math.pow(ws,e[6]) + w[7]*Math.pow(lh,e[7]) + w[8]*Math.pow(b,e[8])
				+ w[9]*Math.pow(wb,e[9]) + w[10]*Math.pow(rt,e[10]) + w[11]*Math.pow(ct,e[11]);
	} 
	//Math.pow(base,exponent)
}
