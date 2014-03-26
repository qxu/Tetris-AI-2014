package com.qxu.tetris.ai.newscores;

import com.qxu.tetris.TetrisGrid;

public class Wells {
	public static int getWellSums(TetrisGrid grid) {
		if (grid.getWidth() <= 1)
			return 0;

		int count = 0;

		int leftCount = grid.getColumnHeight(1) - grid.getColumnHeight(0);
		if (leftCount > 0)
			count += leftCount;

		for (int c = 1; c < grid.getWidth() - 1; c++) {
			int hLeft = grid.getColumnHeight(c - 1);
			int hCenter = grid.getColumnHeight(c);
			int hRight = grid.getColumnHeight(c + 1);

			int dLeft = hLeft - hCenter;
			int dRight = hRight - hCenter;
			int min = Math.min(dLeft, dRight);

			if (min > 0)
				count += min;
		}

		int rightCount = grid.getColumnHeight(grid.getWidth() - 2)
				- grid.getColumnHeight(grid.getWidth() - 1);
		if (rightCount > 0)
			count += leftCount;

		return count;
	}
}
