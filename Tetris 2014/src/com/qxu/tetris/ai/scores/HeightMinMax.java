package com.qxu.tetris.ai.scores;

import com.qxu.tetris.TetrisGrid;

public enum HeightMinMax implements BoardRater {
	INSTANCE;
	
	@Override
	public double rate(TetrisGrid board) {
		int maxHeight = 0;
		int minHeight = board.getHeight();

		for (int x = 0; x < board.getWidth(); x++) {
			int height = board.getColumnHeight(x);

			if (height > maxHeight)
				// record the height of highest coloumn
				maxHeight = height;
			if (height < minHeight)
				// record height of lowest coloumn
				minHeight = height;

		}

		return maxHeight - minHeight;
	}

}
