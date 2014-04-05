package com.qxu.tetris.ai.scores;

import com.qxu.tetris.TetrisGrid;

public enum HeightVar implements BoardRater {
	INSTANCE;
	
	@Override
	public double rate(TetrisGrid board) {
		int sumHeight = 0;

		// count the holes and sum up the height
		for (int x = 0; x < board.getWidth(); x++) {
			final int colHeight = board.getColumnHeight(x);
			sumHeight += colHeight;
		}

		double avgHeight = ((double) sumHeight) / board.getWidth();

		// first variance
		int varisum = 0;
		for (int x = 0; x < board.getWidth(); x++) {
			final int colHeight = board.getColumnHeight(x);
			varisum += Math.pow(colHeight - avgHeight, 2);
		}

		return varisum / board.getWidth();

	}
}
