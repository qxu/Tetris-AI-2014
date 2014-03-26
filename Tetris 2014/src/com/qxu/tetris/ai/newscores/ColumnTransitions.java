package com.qxu.tetris.ai.newscores;

import com.qxu.tetris.TetrisGrid;

public class ColumnTransitions {
	public static int getColumnTransitionCount(TetrisGrid grid) {
		int count = 0;
		for (int c = 0; c < grid.getWidth(); c++) {
			int h = grid.getColumnHeight(c);
			if (h > 0) {
				count++;
			}
			for (int r = h - 2; r >= 0; r--) {
				if (grid.get(r, c) != grid.get(r + 1, c)) {
					count++;
				}
			}
			if (!grid.get(0, c)) {
				count++;
			}
		}
		return count;
	}
}
