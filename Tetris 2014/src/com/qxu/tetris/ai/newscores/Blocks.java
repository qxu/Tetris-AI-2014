package com.qxu.tetris.ai.newscores;

import com.qxu.tetris.TetrisGrid;

public class Blocks {
	public int getNumOfBlocks(TetrisGrid grid) {
		int numOfBlocks = 0;
		for (int c = 0; c < grid.getWidth(); ++c) {
			for (int r = 0; r < grid.getColumnHeight(c); ++r) {
				if (grid.get(r, c)) {
					++numOfBlocks;
				}
			}
		}
		return numOfBlocks;
	}

	public int getWeightedBlocks(TetrisGrid grid) {
		int weightedBlocks = 0;
		for (int c = 0; c < grid.getWidth(); ++c) {
			for (int r = 0; r < grid.getColumnHeight(c); ++r) {
				if (grid.get(r, c)) {
					weightedBlocks += (r + 1);
				}
			}
		}
		return weightedBlocks;
	}
}
