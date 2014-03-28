package com.qxu.tetris.ai.newscores;

import com.qxu.tetris.TetrisGrid;

public class AltitudeDifference {
	public static int getAltitudeDifference(TetrisGrid grid) {
		int tallestHeight = getPileHeight(grid);
		int lowestHeight = grid.getColumnHeight(0); // gets height of first
													// column as reference
		for (int c = 1; c < grid.getWidth(); ++c) {
			int height = grid.getColumnHeight(c);
			if (height < lowestHeight) {
				lowestHeight = height;
			}
		}
		return tallestHeight - lowestHeight;

	}

	public static int getPileHeight(TetrisGrid grid) {
		int pileHeight = grid.getColumnHeight(0);// pileHeight is the highest height
		for (int c = 1; c < grid.getWidth(); ++c) {
			int height = grid.getColumnHeight(c);
			if (height > pileHeight) {
				pileHeight = height;
			}
		}
		return pileHeight;
	}
}
