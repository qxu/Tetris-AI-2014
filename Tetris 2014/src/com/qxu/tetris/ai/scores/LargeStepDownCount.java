package com.qxu.tetris.ai.scores;

import com.qxu.tetris.TetrisGrid;

public enum LargeStepDownCount implements BoardRater {
	INSTANCE;

	@Override
	public double rate(TetrisGrid board) {
		int count = 0;
		for (int c = 1; c < board.getWidth(); c++) {
			int dh = board.getColumnHeight(c - 1) - board.getColumnHeight(c);
			if (dh >= 2) {
				count++;
			}
		}
		return count;
	}
}
