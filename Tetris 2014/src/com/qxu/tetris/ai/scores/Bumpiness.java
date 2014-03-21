package com.qxu.tetris.ai.scores;

import com.qxu.tetris.TetrisGrid;

public enum Bumpiness implements BoardRater {
	INSTANCE;

	@Override
	public double rate(TetrisGrid board) {
		double bumpWeight = 0;
		for (int c = 0; c < board.getWidth() - 1; ++c) {
			int dh = board.getColumnHeight(c)
					- board.getColumnHeight(c + 1);
			bumpWeight += dh * dh;
		}
		return bumpWeight;
	}
}