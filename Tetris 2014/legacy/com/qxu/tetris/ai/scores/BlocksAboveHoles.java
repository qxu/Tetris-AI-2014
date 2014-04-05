package com.qxu.tetris.ai.scores;

import com.qxu.tetris.TetrisGrid;

public enum BlocksAboveHoles implements BoardRater {
	INSTANCE;
	
	@Override
	public double rate(TetrisGrid board) {
		int w = board.getWidth(), blocksAboveHoles = 0;
		for (int x = 0; x < w; x++) {
			int blocksAboveHoleThisColumn = 0;
			boolean hitHoleYet = false;
			for (int i = board.getColumnHeight(x) - 1; i >= 0; i--) {
				if (!board.get(i, x))
					hitHoleYet = true;
				blocksAboveHoleThisColumn += hitHoleYet ? 0 : 1;
			}

			if (!hitHoleYet)
				blocksAboveHoleThisColumn = 0;
			blocksAboveHoles += blocksAboveHoleThisColumn;
		}
		return blocksAboveHoles;
	}
}
