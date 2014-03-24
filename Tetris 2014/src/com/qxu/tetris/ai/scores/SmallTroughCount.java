package com.qxu.tetris.ai.scores;

import com.qxu.tetris.TetrisGrid;

public enum SmallTroughCount implements BoardRater {
	INSTANCE;

	@Override
	public double rate(TetrisGrid board) {
		if (board.getWidth() <= 1)
			return 0;

		int troughCount = 0;

		if (board.getColumnHeight(1) - board.getColumnHeight(0) == 2)
			troughCount++;

		for (int c = 2; c <= board.getWidth() - 2; c++) {
			int leftHeight = board.getColumnHeight(c - 1);
			int centerHeight = board.getColumnHeight(c);
			int rightHeight = board.getColumnHeight(c + 1);

			int dLeft = leftHeight - centerHeight;
			int dRight = rightHeight - centerHeight;
			if (dLeft == 2 && dRight == 2)
				troughCount++;
		}

		if (board.getColumnHeight(board.getWidth() - 2)
				- board.getColumnHeight(board.getWidth() - 1) == 2)
			troughCount++;

		return troughCount;
	}
}
