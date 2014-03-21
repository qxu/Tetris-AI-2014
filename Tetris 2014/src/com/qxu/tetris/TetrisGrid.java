package com.qxu.tetris;

import java.util.Arrays;

public class TetrisGrid {
	private int height;
	private int width;

	private long[] data;
	private int[] heights;

	public TetrisGrid(TetrisGrid copy) {
		this.height = copy.height;
		this.width = copy.width;

		this.data = copy.data.clone();
		this.heights = copy.heights.clone();
	}

	public TetrisGrid(int height, int width) {
		if (width > 64)
			throw new IllegalArgumentException("width: " + width
					+ " is too large");
		this.height = height;
		this.width = width;

		this.data = new long[height];
		this.heights = new int[width];
	}

	public int getColumnHeight(int column) {
		for (int r = height - 1; r >= 0; r--) {
			if (get(r, column))
				return r + 1;
		}
		return 0;
	}

	public int getDropRow(int column, TetrisBlock block) {
		BlockData data = block.getData();
		int blockWidth = data.getWidth();
		int blockHeight = data.getHeight();

		int dropRow = 0;
		for (int x = 0; x < blockWidth; x++) {
			int blockPadding = 0;
			while (blockPadding < blockHeight && !data.get(blockPadding, x)) {
				blockPadding++;
			}
			for (int r = height - blockHeight + blockPadding; r < height; r++) {
				if (get(r, x + column)) {
					return height;
				}
			}
			int row = getColumnHeight(x + column) - blockPadding;
			if (row > dropRow) {
				dropRow = row;
			}
		}
		return dropRow;
	}

	public boolean isValidMove(int row, int column, TetrisBlock block) {
		BlockData data = block.getData();
		int blockWidth = data.getWidth();
		int blockHeight = data.getHeight();
		if (row < 0 || row + blockHeight > height || column < 0
				|| column + blockWidth > width) {
			return false;
		}
		boolean hasBase = false;
		for (int x = 0; x < blockWidth; x++) {
			for (int r = row + blockHeight - 1; r < height; r++) {
				if (get(r, x + column)) {
					return false;
				}
			}
			for (int y = blockHeight - 1; y >= 0; y--) {
				if (!data.get(y, x)) {
					if (get(y + row, x + column)) {
						return false;
					}
				} else {
					break;
				}
			}
			int blockPadding = 0;
			while (blockPadding < blockHeight && !data.get(blockPadding, x)) {
				blockPadding++;
			}
			int r = blockPadding + row - 1;
			if (r == -1 || get(r, x + column)) {
				hasBase = true;
			}
		}
		return hasBase;
	}

	public void addBlock(int row, int column, TetrisBlock block) {
		BlockData data = block.getData();
		int blockWidth = data.getWidth();
		int blockHeight = data.getHeight();
		checkBounds(row, column);
		checkBounds(row + blockHeight - 1, column + blockWidth - 1);

		for (int y = 0; y < blockHeight; y++) {
			for (int x = 0; x < blockWidth; x++) {
				if (data.get(y, x)) {
					set(row + y, column + x);
				}
			}
		}
	}

	public void clearRowAndShiftDown(int row) {
		checkRowBounds(row);
		System.arraycopy(data, row + 1, data, row, data.length - row - 1);
		data[data.length - 1] = 0L;
	}

	public int clearFullRows() {
		long fullRow = (1L << width) - 1L;
		int clearedCount = 0;
		int r = 0;
		while (r + clearedCount < height) {
			if (data[r] == fullRow) {
				clearRowAndShiftDown(r);
				clearedCount++;
			} else {
				r++;
			}
		}
		return clearedCount;
	}

	public void set(int row, int column, boolean b) {
		if (b) {
			set(row, column);
		} else {
			clear(row, column);
		}
	}

	public void set(int row, int column) {
		checkBounds(row, column);
		data[row] |= 1L << column;
	}

	public void clear(int row, int column) {
		checkBounds(row, column);
		data[row] &= ~(1L << column);
	}

	public boolean get(int row, int column) {
		checkBounds(row, column);
		return (data[row] & (1L << column)) != 0;
	}

	public long get(int row) {
		checkRowBounds(row);
		return data[row];
	}

	private void checkBounds(int row, int column) {
		checkRowBounds(row);
		checkColumnBounds(column);
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	private void checkRowBounds(int row) {
		if (row < 0 || row >= height) {
			throw new IndexOutOfBoundsException("Row: " + row);
		}
	}

	private void checkColumnBounds(int column) {
		if (column < 0 || column >= width) {
			throw new IndexOutOfBoundsException("Column: " + column);
		}
	}

	@Override
	public int hashCode() {
		int hash = this.height;
		hash = hash * 31 + this.width;
		for (long x : data) {
			hash = hash * 31 + (int) (x ^ (x >>> 32));
		}
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TetrisGrid)) {
			return false;
		}

		TetrisGrid other = (TetrisGrid) o;
		return this.height == other.height && this.width == other.width
				&& Arrays.equals(data, other.data);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(height * (width + 1));
		for (int r = height - 1; r >= 0; r--) {
			for (int c = 0; c < width; c++) {
				sb.append(get(r, c) ? "1" : " ");
			}
			if (r == 0)
				break;
			sb.append("\n");
		}
		return sb.toString();
	}
}
