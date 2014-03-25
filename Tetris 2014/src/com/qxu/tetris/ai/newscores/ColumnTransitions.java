package com.qxu.tetris.ai.newscores;

import com.qxu.tetris.TetrisGrid;

public class ColumnTransitions {
	public static int getColumnTransitions(TetrisGrid grid) {
		int nr = 0;
		for (int j = grid.getHeight() + 1; j > 1; j--) {
			for (int i = 1; i < grid.getWidth() + 1; i++) {
				if (grid.get(j - 1, i) != grid.get(j, i)) {
					nr++;
				}
			}
		}
		return nr;
	}
}
