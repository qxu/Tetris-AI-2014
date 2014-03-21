package com.qxu.tetris.ai.scores;

import com.qxu.tetris.TetrisGrid;

public enum HeightMax implements BoardRater {
	INSTANCE;
	
	@Override
	public double rate(TetrisGrid board) {
		int maxHeight = 0;
		int width = board.getWidth();
		for (int c = 0; c < width; c++) {
			int h = board.getColumnHeight(c);
			if (h > maxHeight) {
				maxHeight = h;
			}
		}
		return maxHeight;
	}
}
