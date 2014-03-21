package com.qxu.tetris.ai;

public class AIMove {
	private final int column;
	private final int orientation;
	
	public AIMove(int column, int orientation) {
		this.column = column;
		this.orientation = orientation;
	}

	public int getColumn() {
		return column;
	}

	public int getOrientation() {
		return orientation;
	}
}
