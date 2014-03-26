package com.qxu.tetris.ai.newscores;

import com.qxu.tetris.TetrisGrid;

public class AltitudeDifference {
	public static int getAltitudeDifference(TetrisGrid grid) {
		int tallestHeight = getPileHeight(grid);
		int lowestHeight = grid.getColumnHeight(0); // gets height of first
													// column as reference
		for (int c = 0; c < grid.getWidth(); ++c) {
			if (grid.getColumnHeight(c) < lowestHeight) {
				lowestHeight = grid.getColumnHeight(c);
			}
		}
		return tallestHeight - lowestHeight;

	}

	public static int getPileHeight(TetrisGrid grid) {
		int pileHeight = 0;// pileHeight is the highest height
		for (int c = 0; c < grid.getWidth(); ++c) {
			if (grid.getColumnHeight(c) > pileHeight) {
				pileHeight = grid.getColumnHeight(c);
			}
		}
		return pileHeight;
	}
}
