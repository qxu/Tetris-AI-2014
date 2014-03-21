package com.qxu.tetris.ai.scores;

import com.qxu.tetris.TetrisGrid;

public enum SimpleHoles implements BoardRater {
	INSTANCE;
	
	@Override
	public double rate(TetrisGrid board) {
		int holes = 0;
		// Count the holes, and sum up the heights
		for (int x = 0; x < board.getWidth(); x++) {
			final int colHeight = board.getColumnHeight(x);

			int y = colHeight - 2; // addr of first possible hole

			while (y >= 0) {
				if (!board.get(y, x)) {
					holes++;
				}
				y--;
			}
		}
		return holes;
	}

}
