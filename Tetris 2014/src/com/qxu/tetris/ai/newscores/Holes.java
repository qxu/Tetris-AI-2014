package com.qxu.tetris.ai.newscores;

import com.qxu.tetris.TetrisGrid;

public class Holes {
	public static int getHoleCount(TetrisGrid grid) {
		int count = 0;
		for (int c = 0; c < grid.getWidth(); c++) {
			for (int r = grid.getColumnHeight(c) - 2; r >= 0; r--) {
				if (!grid.get(r, c)) {
					count++;
				}
			}
		}
		return count;
	}
}
