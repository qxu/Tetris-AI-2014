package com.qxu.tetris.ai;

import java.util.List;

import com.qxu.tetris.TetrisBlock;
import com.qxu.tetris.TetrisGrid;
import com.qxu.tetris.Tetromino;
import com.qxu.tetris.ai.scores.FinalRater;

public class RaterAI implements TetrisAI {
	private FinalRater rater;
	
	public RaterAI(FinalRater rater) {
		this.rater = rater;
	}
	
	@Override
	public AIMove getMove(TetrisGrid grid, Tetromino t) {
		double bestScore = Double.NEGATIVE_INFINITY;
		
		int bestColumn = -1;
		int bestOrientation = -1;
		
		List<TetrisBlock> blocks = t.getBlockChain();
		for (int or = 0; or < blocks.size(); or++) {
			TetrisBlock block = blocks.get(or);
			int maxCol = grid.getWidth() - block.getData().getWidth();
			for (int c = 0; c <= maxCol; c++) {
				int row = grid.getDropRow(c, block);
				if (row < grid.getHeight()) {
					TetrisGrid subGrid = new TetrisGrid(grid);
					subGrid.addBlock(row, c, block);
					double score = rater.rate(subGrid);
					
					if (score > bestScore) {
						bestScore = score;
						bestOrientation = or;
						bestColumn = c;
					}
				}
			}
		}
		
		return (bestColumn >= 0) ? new AIMove(bestColumn, bestOrientation) : null;
	}
}
