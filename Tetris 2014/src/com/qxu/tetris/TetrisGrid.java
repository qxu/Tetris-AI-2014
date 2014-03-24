package com.qxu.tetris;

import java.util.Arrays;

public class TetrisGrid {
	private final int height;
	private final int width;

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
		return heights[column];
	}

	public int getDropRow(int column, TetrisBlock block) {
		BlockData data = block.getData();
		int blockWidth = data.getWidth();

		int dropRow = 0;
		for (int x = 0; x < blockWidth; x++) {
			int blockPadding = data.getBottomPadding(x);
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
			int colHeight = getColumnHeight(column + x);
			int blockPadding = data.getBottomPadding(x);

			if (blockPadding + colHeight + row < 0) {
				return false;
			}
			if (blockPadding + colHeight + row == 0) {
				hasBase = true;
			}
		}
		return hasBase;
	}

	public boolean get(int row, int column) {
		checkBounds(row, column);
		return (data[row] & (1L << column)) != 0;
	}

	public void addBlock(int row, int column, TetrisBlock block) {
		BlockData data = block.getData();
		int blockWidth = data.getWidth();
		int blockHeight = data.getHeight();
		checkBounds(row, column);
		checkBounds(row + blockHeight - 1, column + blockWidth - 1);

		for (int x = 0; x < blockWidth; x++) {
			for (int y = 0; y < blockHeight; y++) {
				if (data.get(y, x)) {
					internalSet(row + y, column + x);
				}
			}
			updateHeights(column + x);
		}
	}

	public int clearFullRows() {
		final long fullRow = (1L << width) - 1L;
		int clearedCount = 0;
		int r = 0;
		while (r + clearedCount < height) {
			if (data[r] == fullRow) {
				System.arraycopy(data, r + 1, data, r, data.length - r - 1);
				data[data.length - 1] = 0L;
				clearedCount++;
			} else {
				r++;
			}
		}
		if (clearedCount > 0) {
			for (int c = 0; c < width; c++) {
				updateHeights(c);
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
		updateHeights(column);
	}
	
	public long[] getData() {
		return data.clone();
	}
	
	private void internalSet(int row, int column) {
		data[row] |= 1L << column;
	}

	public void clear(int row, int column) {
		checkBounds(row, column);
		data[row] &= ~(1L << column);
		updateHeights(column);
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	private void checkBounds(int row, int column) {
		checkRowBounds(row);
		checkColumnBounds(column);
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

	private void updateHeights(int column) {
		for (int r = height - 1; r >= 0; r--) {
			if (get(r, column)) {
				heights[column] = r + 1;
				return;
			}
		}
		heights[column] = 0;
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
