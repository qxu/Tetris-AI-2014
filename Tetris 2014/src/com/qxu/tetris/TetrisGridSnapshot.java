package com.qxu.tetris;

import java.nio.ByteBuffer;
import java.util.List;

public class TetrisGridSnapshot {

	public static TetrisGridSnapshot fromString(String s) {
		byte[] bytes = new byte[s.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = Byte.parseByte(s.substring(i * 2, i * 2 + 1), 16);
		}
		return fromBytes(bytes);
	}

	public static TetrisGridSnapshot fromBytes(byte[] b) {
		ByteBuffer buf = ByteBuffer.wrap(b);
		int height = buf.getInt();
		int width = buf.getInt();
		long[] data = new long[height];
		for (int i = 0; i < height; i++) {
			data[i] = buf.getLong();
		}
		TetrisBlock moveBlock = null;
		int moveColumn = 0;

		if (buf.getLong() != 0) {
			int tOrdinal = buf.getInt();
			int orientation = buf.getInt();
			Tetromino[] tetrominoes = Tetromino.values();
			moveBlock = tetrominoes[tOrdinal].getBlockChain().get(orientation);
			moveColumn = buf.getInt();
		}

		return new TetrisGridSnapshot(height, width, data, moveBlock,
				moveColumn);
	}

	private final int height;
	private final int width;

	private final long[] data;

	private final TetrisBlock moveBlock;
	private final int moveColumn;

	public TetrisGridSnapshot(TetrisGrid grid) {
		this(grid, null, 0);
	}

	public TetrisGridSnapshot(TetrisGrid grid, TetrisBlock moveBlock,
			int moveColumn) {
		this.height = grid.getHeight();
		this.width = grid.getWidth();

		this.data = grid.getData();
		this.moveBlock = moveBlock;
		this.moveColumn = moveColumn;
	}

	private TetrisGridSnapshot(int height, int width, long[] data,
			TetrisBlock moveBlock, int moveColumn) {
		this.height = height;
		this.width = width;
		this.data = data;
		this.moveBlock = moveBlock;
		this.moveColumn = moveColumn;
	}

	public TetrisGrid createGrid() {
		TetrisGrid grid = new TetrisGrid(height, width);

		for (int r = 0; r < height; r++) {
			for (int c = 0; c < width; c++) {
				if ((data[r] & (1L << c)) != 0) {
					grid.set(r, c);
				}
			}
		}

		return grid;
	}

	private int[] getBlockCode(TetrisBlock block) {
		Tetromino[] tetrominoes = Tetromino.values();
		for (int i = 0; i < tetrominoes.length; i++) {
			List<TetrisBlock> blockChain = tetrominoes[i].getBlockChain();
			int orientation = blockChain.indexOf(block);
			if (orientation >= 0) {
				return new int[] { i, orientation };
			}
		}
		throw new AssertionError("Unknown tetris block: \n" + block + "\n");
	}

	public byte[] toByteArray() {
		int capacity = 4 + 4 + data.length * 8 + 8;
		if (moveBlock != null) {
			capacity += 4 + 4 + 4;
		}
		ByteBuffer buf = ByteBuffer.allocate(capacity);
		buf.putInt(height);
		buf.putInt(width);
		for (int i = 0; i < height; i++) {
			buf.putLong(data[i]);
		}
		if (moveBlock != null) {
			buf.putLong(1);
			int[] blockCode = getBlockCode(moveBlock);
			buf.putInt(blockCode[0]);
			buf.putInt(blockCode[1]);
			buf.putInt(moveColumn);
		} else {
			buf.putLong(0);
		}
		return buf.array();
	}

	@Override
	public String toString() {
		byte[] bytes = toByteArray();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			sb.append(Integer.toHexString(b & 0xff));
		}
		return sb.toString();
	}
}
