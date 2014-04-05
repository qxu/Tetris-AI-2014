package com.qxu.tetris.ai.scores;

import com.qxu.tetris.TetrisGrid;

public interface BoardRater {
	double rate(TetrisGrid board);
}
