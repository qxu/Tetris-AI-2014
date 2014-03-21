package com.qxu.tetris.ai.scores;

import com.qxu.tetris.TetrisGrid;

public class FinalRater implements BoardRater {
	public static BoardRater[] RATERS = {
			HeightMax.INSTANCE,
			SimpleHoles.INSTANCE,
			TroughCount.INSTANCE,
			BlocksAboveHoles.INSTANCE,
			Bumpiness.INSTANCE
	};
	
	private double[] coefficients;

	public FinalRater() {
		this.coefficients = new double[RATERS.length];
	}

	public FinalRater(double[] c) {
		if (c.length != RATERS.length)
			throw new IllegalArgumentException();
		this.coefficients = c;
	}

	@Override
	public double rate(TetrisGrid board) {
		double score = 0;
		for (int i = 0; i < RATERS.length; i++) {
			score += coefficients[i] * RATERS[i].rate(board);
		}
		return score;
	}
}
