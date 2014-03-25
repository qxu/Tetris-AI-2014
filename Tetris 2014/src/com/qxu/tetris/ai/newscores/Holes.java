package com.qxu.tetris.ai.newscores;

import com.qxu.tetris.TetrisGrid;

public class Holes {
	public static int getHoleCount(TetrisGrid grid) {
		int nr = 0;
		for (int i = 0; i <= grid.getWidth(); i++) {
			int last = 0;
			for (int j = grid.getHeight() - 1; j >= 0; j--) {
				if (grid.get(j, i)) {
					nr += last;
					last = 0;
				} else {
					last++;
				}
			}
		}
		return nr;
	}
}
