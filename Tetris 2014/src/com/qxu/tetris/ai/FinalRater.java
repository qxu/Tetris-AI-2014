package com.qxu.tetris.ai;

import com.qxu.tetris.TetrisGrid;
import com.qxu.tetris.ai.scores.BlocksAboveHoles;
import com.qxu.tetris.ai.scores.BoardRater;
import com.qxu.tetris.ai.scores.Bumpiness;
import com.qxu.tetris.ai.scores.HeightMax;
import com.qxu.tetris.ai.scores.SimpleHoles;
import com.qxu.tetris.ai.scores.TroughCount;

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
