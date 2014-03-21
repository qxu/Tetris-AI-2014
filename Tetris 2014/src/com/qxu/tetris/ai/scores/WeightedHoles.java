package com.qxu.tetris.ai.scores;

import com.qxu.tetris.TetrisGrid;

public enum WeightedHoles implements BoardRater {
	INSTANCE;
	
	@Override
	public double rate(TetrisGrid board) {
		int maxHeight = 0;
		int minHeight = board.getHeight();

		for (int x = 0; x < board.getWidth(); x++) {
			int height = board.getColumnHeight(x);
			if (height > maxHeight)
				maxHeight = height;
			if (height < minHeight)
				minHeight = height;
		}

		double weightedHoleCount = 0.0;
		int[] heights = new int[board.getWidth()];

		for (int x = 0; x < board.getWidth(); x++) {
			heights[x] = board.getColumnHeight(x);
			int y = heights[x] - 2;
			while (y >= 0) {
				if (!board.get(y, x))
					weightedHoleCount += (double) (maxHeight - y)
							/ (double) maxHeight;
				y--;
			}
		}

		return weightedHoleCount;

	}
}
