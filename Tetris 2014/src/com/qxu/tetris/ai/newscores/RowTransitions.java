package com.qxu.tetris.ai.newscores;

import com.qxu.tetris.TetrisGrid;

public class RowTransitions {
	public static int getRowTransitionCount(TetrisGrid grid) {
		int count = 0;
		for (int r = grid.getHeight() - 1; r >= 0; r--) {
			if (!grid.get(r, 0)) {
				count++;
			}
			for (int c = 1; c < grid.getWidth(); c++) {
				if (grid.get(r, c - 1) != grid.get(r, c)) {
					count++;
				}
			}
			if (grid.getWidth() >= 2 && !grid.get(r, grid.getWidth() - 1)) {
				count++;
			}
		}
		return count;
	}
}