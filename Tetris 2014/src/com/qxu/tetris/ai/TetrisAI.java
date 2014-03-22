package com.qxu.tetris.ai;

import com.qxu.tetris.TetrisGrid;
import com.qxu.tetris.Tetromino;

public interface TetrisAI {
	AIMove getMove(TetrisGrid grid, Tetromino t);
}
