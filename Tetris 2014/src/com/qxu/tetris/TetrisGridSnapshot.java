package com.qxu.tetris;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TetrisGridSnapshot {

	private static final Tetromino[] tetrominoes = Tetromino.values();

	private final int height;
	private final int width;

	private final long[] data;

	private final TetrisBlock moveBlock;
	private final int moveColumn;

	private final List<Tetromino> next;

	public TetrisGridSnapshot(TetrisGrid grid, TetrisBlock moveBlock,
			int moveColumn, List<Tetromino> next) {
		this.height = grid.getHeight();
		this.width = grid.getWidth();

		this.data = grid.getData();
		this.moveBlock = moveBlock;
		this.moveColumn = moveColumn;
		this.next = next != null ? new ArrayList<>(next) : null;
	}

	public TetrisGridSnapshot(InputStream in) throws IOException {
		DataInputStream d = new DataInputStream(in);
		this.height = d.readInt();
		this.width = d.readInt();
		this.data = new long[height];
		for (int i = 0; i < height; i++) {
			data[i] = d.readLong();
		}

		if (d.readBoolean()) {
			int tOrdinal = d.readInt();
			int orientation = d.readInt();
			Tetromino[] tetrominoes = Tetromino.values();
			this.moveBlock = tetrominoes[tOrdinal].getBlockChain().get(
					orientation);
			this.moveColumn = d.readInt();
			
			if (d.readBoolean()) {
				int nextSize = d.readInt();
				this.next = new ArrayList<>(nextSize);
				for (int i = 0; i < nextSize; i++) {
					next.add(tetrominoes[d.readInt()]);
				}
			} else {
				this.next = null;
			}
		} else {
			this.moveBlock = null;
			this.moveColumn = 0;
			this.next = null;
		}
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

	public TetrisBlock getMoveBlock() {
		return moveBlock;
	}

	public int getMoveColumn() {
		return moveColumn;
	}

	public void writeTo(OutputStream out) throws IOException {
		DataOutputStream d = new DataOutputStream(out);
		d.writeInt(height);
		d.writeInt(width);
		for (int i = 0; i < height; i++) {
			d.writeLong(data[i]);
		}
		if (moveBlock != null) {
			d.writeBoolean(true);
			int[] blockCode = getBlockCode(moveBlock);
			d.writeInt(blockCode[0]);
			d.writeInt(blockCode[1]);
			d.writeInt(moveColumn);

			if (next != null) {
				d.writeBoolean(true);
				d.writeInt(next.size());
				for (Tetromino t : next) {
					d.writeInt(t.ordinal());
				}
			} else {
				d.writeBoolean(false);
			}
		} else {
			d.writeBoolean(false);
		}
	}

	private int[] getBlockCode(TetrisBlock block) {
		for (int i = 0; i < tetrominoes.length; i++) {
			List<TetrisBlock> blockChain = tetrominoes[i].getBlockChain();
			int orientation = blockChain.indexOf(block);
			if (orientation >= 0) {
				return new int[] { i, orientation };
			}
		}
		throw new AssertionError("Unknown tetris block: \n" + block + "\n");
	}

	@Override
	public String toString() {
		return createGrid().toString();
	}
}
