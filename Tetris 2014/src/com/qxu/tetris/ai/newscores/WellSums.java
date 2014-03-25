package com.qxu.tetris.ai.newscores;

import com.qxu.tetris.TetrisGrid;

public class WellSums {
	public static int getWellSums(TetrisGrid grid) {
		int nr = 0;
		for (int j = grid.getHeight() - 1; j >= 0; j--) {
			for (int i = 1; i <= grid.getWidth() + 1; i++) {
				if (grid.get(j, i) && grid.get(j, i - 1) && grid.get(j, i + 1)) {
					nr++;
					for (int k = j + 1; k < grid.getHeight(); k++) {
						if (!grid.get(k, i)) {
							nr++;
						} else {
							break;
						}
					}
				}
			}
		}
		return nr;
	}
}
