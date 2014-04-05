package com.qxu.tetris.ai.scores;

import com.qxu.tetris.TetrisGrid;

public enum HeightStdDev implements BoardRater {
	INSTANCE;
	
	@Override
	public double rate(TetrisGrid board) {
		return Math.sqrt(HeightVar.INSTANCE.rate(board));
	}
}
