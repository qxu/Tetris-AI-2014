package com.qxu.tetris;
import java.util.Collections;
import java.util.List;

public enum Tetromino {
	I(1, 4, 0xf),
	O(2, 2, 0xf),
	T(2, 3, 0x17),
	J(2, 3, 0xf),
	L(2, 3, 0x27),
	S(2, 3, 0x33),
	Z(2, 3, 0x1e);

	private final List<TetrisBlock> blockChain;

	private Tetromino(int height, int width, long data) {
		List<TetrisBlock> c = TetrisBlock.constructBlockChain(height, width, data);
		this.blockChain = Collections.unmodifiableList(c);
	}

	public List<TetrisBlock> getBlockChain() {
		return blockChain;
	}
	
	public TetrisBlock getFirstRotation() {
		return blockChain.get(0);
	}
}
