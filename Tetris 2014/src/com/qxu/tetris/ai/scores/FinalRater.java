package com.qxu.tetris.ai.scores;

import com.qxu.tetris.TetrisGrid;

public class FinalRater implements BoardRater {
	public static BoardRater[] RATERS = {
//			ConsecHorzHoles.INSTANCE,
//			HeightAvg.INSTANCE,
			HeightMax.INSTANCE,
//			HeightMinMax.INSTANCE,
			HeightVar.INSTANCE,
//			HeightStdDev.INSTANCE,
			SimpleHoles.INSTANCE,
			ThreeVariance.INSTANCE,
			TroughCount.INSTANCE,
//			WeightedHoles.INSTANCE,
//			RowsWithHolesInMostHoledColumn.INSTANCE,
//			AverageSquaredTroughHeight.INSTANCE,
			BlocksAboveHoles.INSTANCE
	};

	public double[] coefficients = {
			0, // ConsecHorzHoles
			-10, // HeightAvg
			-1, // HeightMax
			-1, // HeightMinMax
			0, // HeightVar
			-5, // HeightStdDev
			-40, // SimpleHoles
			-10, // ThreeVariance
			-1, // Trough
			-4, // WeightedHoles
			-4, // RowsWithHolesInMostHoledColumn
			-15, // AverageSquaredTroughHeight
			-2 // BlocksAboveHoles
	};

	public FinalRater() {
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
