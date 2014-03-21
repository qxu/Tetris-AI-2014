package com.qxu.tetris.ai.scores;

import com.qxu.tetris.TetrisGrid;

public enum ConsecHorzHoles implements BoardRater {
	INSTANCE;

	@Override
	public double rate(TetrisGrid board) {
		final int width = board.getWidth();

		int holes = 0;

		// Count the holes, and sum up the heights
		for (int x = 0; x < width; x++) {
			final int colHeight = board.getColumnHeight(x);
			int y = colHeight - 2; // addr of first possible hole

			boolean consecutiveHole = false;
			while (y >= 0) {
				if (!board.get(y, x)) {
					if (!consecutiveHole) {
						holes++;
						consecutiveHole = true;
					}
				} else {
					consecutiveHole = false;
				}
				y--;
			}
		}

		return holes;
	}
}
