package com.qxu.tetris.ai.newscores;

import com.qxu.tetris.TetrisGrid;

public class Holes {
	public static int getHoles(TetrisGrid grid) {
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
	
	public static int getConnectedHoles(TetrisGrid grid) {
		int count = 0;
		for (int c = 0; c < grid.getWidth(); c++) {
			for (int r = grid.getColumnHeight(c) - 2; r >= 0; r--) {
				if (!grid.get(r, c)) {
					count++;
					do {
						r--;
					} while (r >= 0 && !grid.get(r, c));
				}
			}
		}
		return count;
	}
}
