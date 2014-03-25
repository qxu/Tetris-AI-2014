package com.qxu.tetris.ai.newscores;

import com.qxu.tetris.TetrisGrid;

public class RowTransitions {
	public static int getRowTransitions(TetrisGrid grid) {
		int nr = 0;
		for (int j = grid.getHeight() - 1; j >= 0; j--) {
			for (int i = 0; i < grid.getWidth() + 1; i++) {
				if (grid.get(j, i) != grid.get(j, i + 1)) {
					nr++;
				}
			}
		}
		return nr;
	}
}