package com.qxu.tetris.ai.scores;

import com.qxu.tetris.TetrisGrid;

public enum HeightAvg implements BoardRater {
	INSTANCE;
	
	@Override
	public double rate(TetrisGrid board) {
		int sumHeight = 0;
		// count the holes and sum up the heights
		for (int x = 0; x < board.getWidth(); x++) {
			final int colHeight = board.getColumnHeight(x);
			sumHeight += colHeight;
		}

		return ((double) sumHeight / board.getWidth());
	}

}
