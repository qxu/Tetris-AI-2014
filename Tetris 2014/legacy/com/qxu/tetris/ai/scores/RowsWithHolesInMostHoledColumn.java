package com.qxu.tetris.ai.scores;

import com.qxu.tetris.TetrisGrid;

public enum RowsWithHolesInMostHoledColumn implements BoardRater {
	INSTANCE;
	
	@Override
	public double rate(TetrisGrid board) {
		// count the holes and sum up the heights
		int mostHolesInAnyColumn = 0;
		for (int x = 0; x < board.getWidth(); x++) {
			final int colHeight = board.getColumnHeight(x);
			int y = colHeight - 2;
			int holes = 0;

			while (y >= 0) {
				if (!board.get(y, x)) {
					holes++;
				}
				y--;
			}

			if (mostHolesInAnyColumn < holes)
				mostHolesInAnyColumn = holes;
		}

		return mostHolesInAnyColumn;
	}

}
