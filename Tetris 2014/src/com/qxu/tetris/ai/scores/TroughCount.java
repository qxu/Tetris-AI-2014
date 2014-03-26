package com.qxu.tetris.ai.scores;

import com.qxu.tetris.TetrisGrid;

public enum TroughCount implements BoardRater {
	INSTANCE;

	@Override
	public double rate(TetrisGrid board) {
		if (board.getWidth() <= 1)
			return 0;

		int troughCount = 0;

		int leftTroughCount = (board.getColumnHeight(1)
				- board.getColumnHeight(0) + 1) / 4;
		if (leftTroughCount > 0)
			troughCount += leftTroughCount;
		
		for (int c = 1; c < board.getWidth() - 1; c++) {
			int leftHeight = board.getColumnHeight(c - 1);
			int centerHeight = board.getColumnHeight(c);
			int rightHeight = board.getColumnHeight(c + 1);
			
			int dLeft = leftHeight - centerHeight;
			int dRight = rightHeight - centerHeight;
			int min = (Math.min(dLeft, dRight) + 1) / 4;
			
			if (min > 0)
				troughCount += min;
		}

		int rightTroughCount = (board.getColumnHeight(board.getWidth() - 2)
				- board.getColumnHeight(board.getWidth() - 1) + 1) / 4;
		if (rightTroughCount > 0)
			troughCount += leftTroughCount;

		return troughCount;
	}
}
