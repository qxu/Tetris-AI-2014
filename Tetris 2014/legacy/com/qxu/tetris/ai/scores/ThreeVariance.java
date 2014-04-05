package com.qxu.tetris.ai.scores;

import com.qxu.tetris.TetrisGrid;

public enum ThreeVariance implements BoardRater {
	INSTANCE;
	
	@Override
	public double rate(TetrisGrid board) {
		int w = board.getWidth();
		double runningVarianceSum = 0.0;
		for (int i = 0; i < w - 2; i++) {
			double h0 = (double) board.getColumnHeight(i);
			double h1 = (double) board.getColumnHeight(i + 1);
			double h2 = (double) board.getColumnHeight(i + 2);
			double m = (h0 + h1 + h2) / 3.0;
			h0 -= m;
			h1 -= m;
			h2 -= m;
			h0 *= h0;
			h1 *= h1;
			h2 *= h2;
			runningVarianceSum += (h0 + h1 + h2) / 3.0;
		}
		return runningVarianceSum / (double) (w - 3);
	}

}
