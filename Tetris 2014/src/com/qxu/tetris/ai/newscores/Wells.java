package com.qxu.tetris.ai.newscores;

import com.qxu.tetris.TetrisGrid;

public class Wells {
	public static int getMaxWell(TetrisGrid grid) {
		if (grid.getWidth() <= 1)
			return 0;

		int max = 0;

		int leftWell = grid.getColumnHeight(1) - grid.getColumnHeight(0);
		if (leftWell > max)
			max = leftWell;

		for (int c = 1; c < grid.getWidth() - 1; c++) {
			int hLeft = grid.getColumnHeight(c - 1);
			int hCenter = grid.getColumnHeight(c);
			int hRight = grid.getColumnHeight(c + 1);

			int dLeft = hLeft - hCenter;
			int dRight = hRight - hCenter;
			int well = Math.min(dLeft, dRight);

			if (well > max)
				max = well;
		}

		int rightWell = grid.getColumnHeight(grid.getWidth() - 2)
				- grid.getColumnHeight(grid.getWidth() - 1);
		if (rightWell > max)
			max = rightWell;

		return max;
	}
	
	public static int getWellSums(TetrisGrid grid) {
		if (grid.getWidth() <= 1)
			return 0;

		int sum = 0;

		int leftWell = grid.getColumnHeight(1) - grid.getColumnHeight(0);
		if (leftWell > 0)
			sum += leftWell;

		for (int c = 1; c < grid.getWidth() - 1; c++) {
			int hLeft = grid.getColumnHeight(c - 1);
			int hCenter = grid.getColumnHeight(c);
			int hRight = grid.getColumnHeight(c + 1);

			int dLeft = hLeft - hCenter;
			int dRight = hRight - hCenter;
			int well = Math.min(dLeft, dRight);

			if (well > 0)
				sum += well;
		}

		int rightWell = grid.getColumnHeight(grid.getWidth() - 2)
				- grid.getColumnHeight(grid.getWidth() - 1);
		if (rightWell > 0)
			sum += rightWell;

		return sum;
	}

	public static int getWellSumsEl(TetrisGrid grid) {
		int nr = 0;
		for (int j = 0; j < grid.getHeight(); j++) {
			if (!grid.get(j, 0) && grid.get(j, 1)) {
				nr++;
				for (int k = j - 1; k >= 0; k--) {
					if (!grid.get(k, 0)) {
						nr++;
					} else {
						break;
					}
				}
			}
			for (int i = 1; i < grid.getWidth() - 1; i++) {
				if (!grid.get(j, i) && grid.get(j, i - 1) && grid.get(j, i + 1)) {
					nr++;
					for (int k = j - 1; k >= 0; k--) {
						if (!grid.get(k, i)) {
							nr++;
						} else {
							break;
						}
					}
				}
			}
			if (!grid.get(j, grid.getWidth() - 1)
					&& grid.get(j, grid.getWidth() - 2)) {
				nr++;
				for (int k = j - 1; k >= 0; k--) {
					if (!grid.get(k, grid.getWidth() - 1)) {
						nr++;
					} else {
						break;
					}
				}
			}
		}
		return nr;
	}
}
